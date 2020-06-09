package server.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import server.engine.SpiderEngine;

@Configuration
public class SpiderConfig {

  @Bean
  SpiderEngine spiderEngine(){
    SpiderEngine spiderEngine = new SpiderEngine(new Config());
    new Thread(()->spiderEngine.start()).start();
    return spiderEngine;
  }
}
