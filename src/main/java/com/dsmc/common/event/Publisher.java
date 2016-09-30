package com.dsmc.common.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import reactor.bus.Event;
import reactor.bus.EventBus;

@Service
public class Publisher {
  @Autowired
  private EventBus eventBus;

  public <T> void publish(String topic, T event) {
    eventBus.notify(topic, Event.wrap(event));
  }
}
