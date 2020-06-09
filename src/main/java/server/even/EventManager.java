package server.even;

import server.config.Config;

import java.util.*;
import java.util.function.Consumer;

public class EventManager {

  private static final Map<Event, List<Consumer<Config>>> eventConsumerMap = new HashMap<>();

  /**
   * 给消费者展示注册事件
   * @param event 事件
   * @param consumer 消费者
   */
  public static void registerEvent(Event event,Consumer<Config> consumer){
    List<Consumer<Config>> consumers = eventConsumerMap.get(event);
    if (null == consumers) {
      consumers = new ArrayList<>();
    }
    consumers.add(consumer);
    eventConsumerMap.put(event, consumers);
  }

  /**
   * 使用观察者模式给消费者做配置
   * @param event  事件
   * @param config 配置
   */
  public static void fireEvent(Event event, Config config) {
    Optional.ofNullable(eventConsumerMap.get(event)).ifPresent(consumers -> consumers.forEach(consumer -> consumer.accept(config)));
  }

}
