package server.spider.impl;
import	java.text.SimpleDateFormat;

import org.assertj.core.util.Lists;
import org.jsoup.select.Elements;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import server.config.Config;
import server.response.Response;
import server.response.Result;
import server.spider.Spider;
import server.util.SpringUtil;
import us.codecraft.xsoup.XElements;
import org.jsoup.nodes.Element;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class SmzdmSpiderImpl extends Spider {

  private String searchKey;

  public SmzdmSpiderImpl(String name) {
    super("Smzdm:" + name);
    this.searchKey = name;
  }

  @Override
  public void onStart(Config config) {
    this.config = config;
    this.config.getHeaders().put("Accept-Language","zh-CN,zh;q=0.9,en;q=0.8");
    this.config.getCookies().put("_zdmA.uid","ZDMA.JmwezZzBS.1591603704.2419200");
    this.config.getCookies().put("__ckguid","SqY4gB6ea9YWNlycvQnnt84");
    this.startUrls(
        "https://search.m.smzdm.com/?s=" + searchKey + "&source=hot_keyword&v=b&p=1&order=time",
        "https://search.m.smzdm.com/?s=" + searchKey + "&source=hot_keyword&v=b&p=2&order=time",
        "https://search.m.smzdm.com/?s=" + searchKey + "&source=hot_keyword&v=b&p=3&order=time",
        "https://search.m.smzdm.com/?s=" + searchKey + "&source=hot_keyword&v=b&p=4&order=time",
        "https://search.m.smzdm.com/?s=" + searchKey + "&source=hot_keyword&v=b&p=5&order=time"
    );
    this.addPipeline((item, request) -> {
      RabbitTemplate rabbitTemplate = SpringUtil.getBean(RabbitTemplate.class);
      rabbitTemplate.convertAndSend("topicExchange","smzdm",item);
    });
  }

  @Override
  protected <T> Result<T> parse(Response response) {
    String s = response.body().toString();
    System.out.println(s);
    XElements xpath = response.body().xpath("/html/body/div[2]/ul/li");
    Elements elements = xpath.getElements();
    String start = "http:";
    List<Model> modelList = Lists.newArrayList();

    for (Element element : elements) {
      Model model = new Model();
      System.out.println(element);
      Elements select = element.select("a[href]");
      String url = start + select.attr("href");
      String title = element.select("a[href]").select(".zm-card-title").text();
      String price = element.select("a[href]").select(".card-price").text();
      String mall = element.select("a[href]").select(".card-mall").text();
      String time = element.select("a[href]").select(".zm-card-actions-left span span").get(1).text();
      String img = start + select.select(".zm-card-media img").attr("src");
      String substring = url.substring(0, url.length() - 1);
      int i = substring.lastIndexOf('/');
      String id = substring.substring(i + 1, substring.length());
      model.setSourceId(id);
      model.setCreateTime(new Date());
      model.setImage(img);
      model.setModel(searchKey);
      model.setName(title);
      model.setTitle(searchKey);
      String t ;
      if (time.contains(":")){
        t = new SimpleDateFormat("yyyy-MM-dd").format(new Date()) ;
        t += " ";
        t += time;
      }else{
        String yyyy = new SimpleDateFormat("yyyy").format(new Date());
        t = yyyy +"-"+time.trim()+ " 00:00";
      }
      try {
        Date parse = new SimpleDateFormat("yyyy-MM-dd hh:mm").parse(t);
        model.setTime(parse);
      } catch (ParseException e) {
        e.printStackTrace();
      }
      model.setPrice(price);
      model.setUrl(url);
      model.setSource(mall);
      model.setTitle(title);
      String replace = title.replace(" ", "");
      if (replace.toLowerCase().contains(searchKey.toLowerCase())) {
        modelList.add(model);
      }
    }
//    modelList = modelList.stream().filter(item->item.getPrice().length()<12).collect(Collectors.toList());
    return new Result(modelList);
  }


}
