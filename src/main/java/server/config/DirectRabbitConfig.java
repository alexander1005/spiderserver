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
  public Queue smzdmQueue() {
    return new Queue("smzdm");
  }

  @Bean
  public Queue jdQueue() {
    return new Queue("jd");
  }

  @Bean
  Binding bindingExchangeMessage() {
    return BindingBuilder.bind(smzdmQueue()).to(exchange()).with("smzdm");
  }

  @Bean
  Binding bindingExchangeMessage1() {
    return BindingBuilder.bind(jdQueue()).to(exchange()).with("jd");
  }


}
