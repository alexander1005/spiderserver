package server.config;


import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DirectRabbitConfig {


  //队列 起名：TestDirectQueue
  @Bean
  TopicExchange exchange() {
    return new TopicExchange("topicExchange");
  }

  @Bean
  public Queue spiderQueue() {
    return new Queue("spider");
  }

  @Bean
  Binding bindingExchangeMessage() {
    return BindingBuilder.bind(spiderQueue()).to(exchange()).with("spider");
  }

}
