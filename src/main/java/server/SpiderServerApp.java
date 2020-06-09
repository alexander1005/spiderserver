package server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

@SpringBootConfiguration
@EnableAutoConfiguration
@EnableDiscoveryClient
public class SpiderServerApp {

  public static void main(String[] args) {
    SpringApplication.run(SpiderServerApp.class, args);
  }
}
