package server.engine;

import java.util.Random;


import org.assertj.core.util.Lists;
import org.springframework.util.CollectionUtils;
import server.config.Config;
import server.download.Downloader;
import server.even.Event;
import server.even.EventManager;
import server.request.Parser;
import server.request.Request;
import server.response.Response;
import server.response.Result;
import server.scheduler.Scheduler;
import server.spider.Spider;

import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class SpiderEngine {


  // 配置
  private Config config;
  // 爬虫列表
  private List<Spider> spiders = Lists.newArrayList();
  // 是否运行
  private boolean isRunning;
  // 调度器 观察者模式
  private Scheduler scheduler;
  // 线程池
  private ExecutorService executorService;


  public SpiderEngine(Config config) {
    this.config = config;
    this.isRunning = false;
    this.scheduler = new Scheduler();
    this.executorService = new ThreadPoolExecutor(config.parallelThreads(), config.parallelThreads(), 0, TimeUnit.MILLISECONDS,
        config.queueSize() == 0 ? new SynchronousQueue<>()
            : (config.queueSize() < 0 ? new LinkedBlockingQueue<>()
            : new LinkedBlockingQueue<>(config.queueSize())), ((ThreadFactory) Thread::new));
  }

  public void addSpider(Spider spider) {
    if (null == this.spiders) {
      this.spiders = Lists.newArrayList();
    }
    spiders.add(spider);
  }

  public void start() {
    // 启动
    if (isRunning) {
      throw new IllegalStateException("该服务已启动");
    }
    this.isRunning = true;
    // 将配置放入事件
    EventManager.fireEvent(Event.GLOBAL_STARTED, config);
    // 启动下载器
    Thread downloader = new Thread(() -> {
      while (isRunning) {
        if (!scheduler.hasRequest()) {
          this.sleepThread(100);
          continue;
        }
        //提交
        Request request = scheduler.nextRequest();
        executorService.submit(new Downloader(scheduler, request));
        this.sleepThread(new Random().nextInt(10000));
      }
    }, "download");

    downloader.setDaemon(true);
    downloader.setName("download-thread");
    downloader.start();
    // 消费
    this.complete();
  }

  private void complete() {

    while (isRunning) {

      // 启动
      spiders.forEach(spider -> {

        // 获取配置
        Config conf = config.clone();
        // 注册 Onstart
        EventManager.fireEvent(Event.SPIDER_STARTED, conf);
        // 设置配置
        spider.setConfig(config);
        // 构造请求数据流
        List<Request> requests = spider.getStartUrls().stream().map(spider::makeRequest).collect(Collectors.toList());
        // 设置所有请求
        spider.setRequests(requests);
        scheduler.addRequests(requests);
      });
      if (!CollectionUtils.isEmpty(spiders)) {
        spiders.clear();
      }
      if (!scheduler.hasResponse()) {
        sleepThread(100);
        continue;
      }
      Response response = scheduler.nextResponse();
      Parser parser = response.getRequest().getParser();
      if (null != parser) {

        Result<?> result = parser.parse(response);
        List<Request> requests = result.getRequests();
        if (!CollectionUtils.isEmpty(requests)) {
          requests.forEach(scheduler::addRequest);
        }

        if (null != result.getItem()) {
          response.getRequest().getSpider().getPipelines().forEach(p -> p.process(result.getItem(), response.getRequest()));
        }
      }
    }

  }


  public void sleepThread(Integer time) {
    try {
      Thread.sleep(time);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  public void stop() {
    isRunning = false;
    scheduler.clear();
  }

}
