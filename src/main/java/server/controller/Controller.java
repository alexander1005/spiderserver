package server.controller;
import	java.util.HashMap;
import	java.awt.geom.GeneralPath;
import java.util.Map;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import server.engine.SpiderEngine;
import server.spider.impl.JdSpiderImpl;
import server.spider.impl.SmzdmSpiderImpl;

@RestController()
@RequestMapping("spider")
public class Controller {

  @Autowired
  SpiderEngine engine;
  @Autowired
  RabbitTemplate rabbitTemplate;

  @GetMapping("search")
  public Map<String,Object> search(@RequestParam String key,String type) {
    if (type.equals("smzdm")){
      engine.addSpider(new SmzdmSpiderImpl(key));
    }
    if (type.equals("jd")){
      engine.addSpider(new JdSpiderImpl(key));
    }
    Map<String,Object> map = new HashMap<> ();
    map.put("code",200);
    return map;
  }

  @GetMapping("push")
  public Map<String,Object> search(@RequestParam String message) {
    rabbitTemplate.convertAndSend("topicExchange","smzdm",message);
    Map<String,Object> map = new HashMap<> ();
    map.put("code",200);
    return map;
  }

}
