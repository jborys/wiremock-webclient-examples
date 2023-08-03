package me.jvt.hacking.webclient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
  @Bean
  public WebClient defaultWebClient() {
    return WebClient.builder().build();
  }

  @Bean
  public WebClient foo(@Value("1.2.3") String apiKey) {
    return WebClient.builder()
        .defaultRequest(requestHeadersSpec -> requestHeadersSpec.header("api-key", apiKey))
        .build();
  }

  @Bean
  public WebClient bar() {
    return WebClient.builder()
        .defaultRequest(
            requestHeadersSpec -> requestHeadersSpec.accept(MediaType.valueOf("text/plain")))
        .build();
  }
}
