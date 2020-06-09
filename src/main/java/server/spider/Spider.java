package server.spider;

import lombok.Data;
import server.config.Config;
import server.even.Event;
import server.even.EventManager;
import server.pipeline.Pipeline;
import server.request.Parser;
import server.request.Request;
import server.response.Response;
import server.response.Result;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
@Data
public abstract class Spider {

  /** 爬虫名称 **/
  protected String name;
  /** 爬虫配置 **/
  protected Config config;
  /** 爬虫url **/
  protected List<String> startUrls = new ArrayList<>();
  /** 处理流 **/
  protected List<Pipeline> pipelines = new ArrayList<>();
  /** 请求 **/
  protected List<Request>  requests  = new ArrayList<>();

  public Spider(String name){
    this.name = name;
    // 注册事件
    EventManager.registerEvent(Event.SPIDER_STARTED, this::onStart);
  }

  /**
   * 爬虫启动前执行
   */
  public  abstract void  onStart(Config config);

  /**
   * 设置url
   * @param urls 请求列表
   * @return
   */
  public Spider startUrls(String... urls) {
    this.startUrls.addAll(Arrays.asList(urls));
    return this;
  }


  /**
   * 添加 Pipeline 处理 处理流
   */
  protected <T> Spider addPipeline(Pipeline<T> pipeline) {
    this.pipelines.add(pipeline);
    return this;
  }

  /**
   * 构建一个Request
   */
  public <T> Request<T> makeRequest(String url) {
    Parser<T> parse = this::parse;
    return makeRequest(url, parse);
  }


  public <T> Request<T> makeRequest(String url, Parser<T> parser) {
    return new Request(this, url, parser);
  }



  /**
   * 解析文本
   */
  protected abstract <T> Result<T> parse(Response response);



}
