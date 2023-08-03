package me.jvt.hacking.resttemplate;

import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {
  @Bean
  public RestTemplate foo(@Value("1.2.3") String apiKey) {
    return new RestTemplateBuilder()
        .additionalInterceptors(
            (request, body, execution) -> {
              request.getHeaders().set("api-key", apiKey);
              return execution.execute(request, body);
            })
        .build();
  }

  @Bean
  public RestTemplate bar() {
    return new RestTemplateBuilder()
        .additionalInterceptors(
            (request, body, execution) -> {
              request.getHeaders().setAccept(List.of(MediaType.valueOf("text/plain")));
              return execution.execute(request, body);
            })
        .build();
  }
}
