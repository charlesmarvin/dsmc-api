package com.dsmc.common.adapters.email;

import java.util.Map;
import java.util.Set;

public interface EmailAdapter {

    void send(String sender, Set<String> recipients, String subject, String body) throws Exception;

    void send(String sender,
              String recipient,
              String subject,
              String templateId,
              Map<String, Object> templateData) throws Exception;
}
