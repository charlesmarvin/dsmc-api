package com.dsmc.auth;

import com.dsmc.common.event.EventCatalogue;
import com.dsmc.user.domain.User;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

import reactor.bus.Event;
import reactor.bus.EventBus;
import reactor.fn.Consumer;

import static reactor.bus.selector.Selectors.$;

@Service
public class IdentityEventConsumer {
  private static final Logger LOGGER = LoggerFactory.getLogger(IdentityEventConsumer.class);

  @Autowired
  private IdentityService identityService;
  @Autowired
  private EventBus eventBus;

  @PostConstruct
  private void init() {
    eventBus.on($(EventCatalogue.USER_CREATED), (Consumer<Event<User>>) userEvent -> processUserCreated(userEvent.getData()));
    eventBus.on($(EventCatalogue.USER_UPDATED), (Consumer<Event<User>>) userEvent -> processUserUpdated(userEvent.getData()));
  }

  private void processUserUpdated(User user) {
    LOGGER.info("Processing user update event for [{}]", user.getId());
    identityService.findByIdentifier(user.getId())
        .ifPresent(identity -> {
          identity.setUsername(user.getUsername());
          identity.setPassword(user.getPassword());
          identity.setStatus(user.getStatus());
          identity.setCompanyId(user.getCompany().getId());
          identityService.update(identity);
        });
  }

  private void processUserCreated(User user) {
    LOGGER.info("Processing user create event for [{}]", user.getId());
    Identity identity = new Identity();
    identity.setIdentifier(user.getId());
    identity.setUsername(user.getUsername());
    identity.setPassword(user.getPassword());
    identity.setStatus(user.getStatus());
    identity.setCompanyId(user.getCompany().getId());
    identityService.create(identity);
  }
}
