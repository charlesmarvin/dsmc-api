package com.dsmc.common.adapters.email;

import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.velocity.app.VelocityEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.ui.velocity.VelocityEngineUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service("mailgun")
public class MailgunEmailAdapter implements EmailAdapter {
  private static final Logger LOG = LoggerFactory.getLogger(MailgunEmailAdapter.class);
  private final VelocityEngine velocityEngine;
  private final String endpoint;
  private final CloseableHttpClient client;

  @Autowired
  public MailgunEmailAdapter(VelocityEngine velocityEngine,
                             @Value("${app.mailgun.endpoint}") String endpoint,
                             @Value("${app.mailgun.clientId}") String clientId,
                             @Value("${app.mailgun.apikey}") String apiKey) {
    this.velocityEngine = velocityEngine;
    this.endpoint = endpoint;
    CredentialsProvider provider = new BasicCredentialsProvider();
    UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(clientId, apiKey);
    provider.setCredentials(AuthScope.ANY, credentials);
    client = HttpClientBuilder.create().setDefaultCredentialsProvider(provider).build();

  }

  @Override
  public void send(String sender, Set<String> recipients, String subject, String body) throws Exception {
    HttpPost post = new HttpPost(endpoint);
    List<NameValuePair> postParams = new ArrayList<>();
    postParams.add(new BasicNameValuePair("to", String.join(",", recipients)));
    postParams.add(new BasicNameValuePair("from", sender));
    postParams.add(new BasicNameValuePair("subject", subject));
    postParams.add(new BasicNameValuePair("text", body));
    post.setEntity(new UrlEncodedFormEntity(postParams));

    try (CloseableHttpResponse response = client.execute(post)) {
      LOG.info("Request: {}, Response: {}", post.getEntity(), response);
    }

  }

  @Override
  public void send(String sender,
                   String recipient,
                   String subject,
                   String templateId,
                   Map<String, Object> templateData) throws Exception {
    String body = VelocityEngineUtils.mergeTemplateIntoString(
        velocityEngine, "templates/" + templateId + ".vm", "UTF-8", templateData);

    HttpPost post = new HttpPost(endpoint);
    List<NameValuePair> postParams = new ArrayList<>();
    postParams.add(new BasicNameValuePair("to", recipient));
    postParams.add(new BasicNameValuePair("from", sender));
    postParams.add(new BasicNameValuePair("subject", subject));
    postParams.add(new BasicNameValuePair("html", body));
    post.setEntity(new UrlEncodedFormEntity(postParams));

    try (CloseableHttpResponse response = client.execute(post)) {
      LOG.info("Request: {}, Response: {}", post.getEntity(), response);
    }

  }
}
