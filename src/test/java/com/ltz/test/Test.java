package com.ltz.test;

import server.config.Config;
import server.engine.SpiderEngine;
import server.spider.impl.SmzdmSpiderImpl;

public class Test {

  public static void main(String[] args) {

    SpiderEngine spiderEngine = new SpiderEngine(new Config());

    spiderEngine.addSpider(new SmzdmSpiderImpl("sony wh-1000xm3"));

    spiderEngine.start();


  }
}
