package me.jvt.hacking.webclient.okhttp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.concurrent.TimeUnit;
import me.jvt.hacking.webclient.*;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.reactive.function.client.WebClient;

@Import({ProductServiceClientTest.Config.class, JacksonAutoConfiguration.class})
@ExtendWith(SpringExtension.class)
class ProductServiceClientTest {

  @TestConfiguration
  static class Config {
    @Bean
    public MockWebServer webServer() {
      return new MockWebServer();
    }

    @Bean
    public WebClient webClient(MockWebServer webServer) {
      return WebClient.builder().baseUrl(webServer.url("").toString()).build();
    }

    @Bean
    public ProductServiceClient client(WebClient webClient) {
      return new ProductServiceClient(webClient);
    }
  }

  @Autowired private ObjectMapper mapper;
  @Autowired private MockWebServer server;

  @Autowired private ProductServiceClient client;

  @Test
  void returnsProductsWhenSuccessful() throws ProductServiceException {
    server.enqueue(
        new MockResponse()
            .setResponseCode(200)
            .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .setBody(successBody()));

    List<Product> products = client.retrieveProducts();

    assertThat(products)
        .containsExactly(
            new Product("123", "Credit Card"), new Product("456", "Debit Card (Express)"));
  }

  @Test
  void throwsProductServiceExceptionWhenErrorStatus() {
    server.enqueue(new MockResponse().setResponseCode(400));

    assertThatThrownBy(() -> client.retrieveProducts())
        .hasCauseInstanceOf(ProductServiceException.class);
  }

  @Test
  void setsAcceptHeader() throws ProductServiceException, InterruptedException {
    server.enqueue(
        new MockResponse()
            .setResponseCode(200)
            .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .setBody(successBody()));

    client.retrieveProducts();

    var request = server.takeRequest(100, TimeUnit.MILLISECONDS);
    assertThat(request).isNotNull();

    assertThat(request.getHeader("accept")).isEqualTo("application/json, application/*+json");
  }

  private String successBody() {
    ProductContainer container = new ProductContainer();
    container.setProducts(
        List.of(new Product("123", "Credit Card"), new Product("456", "Debit Card (Express)")));
    try {
      return mapper.writeValueAsString(container);
    } catch (JsonProcessingException e) {
      throw new IllegalStateException(e);
    }
  }
}
