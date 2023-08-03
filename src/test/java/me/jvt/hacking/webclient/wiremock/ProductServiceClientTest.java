package me.jvt.hacking.webclient.wiremock;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import java.util.List;
import me.jvt.hacking.webclient.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.reactive.function.client.WebClient;

@Import({ProductServiceClientTest.Config.class, JacksonAutoConfiguration.class})
@ExtendWith(SpringExtension.class)
class ProductServiceClientTest {

  @TestConfiguration
  static class Config {
    @Bean
    public WireMockServer webServer() {
      WireMockServer wireMockServer = new WireMockServer(options().dynamicPort());
      // required so we can use `baseUrl()` in the construction of `webClient` below
      wireMockServer.start();
      return wireMockServer;
    }

    @Bean
    public WebClient webClient(WireMockServer server) {
      return WebClient.builder().baseUrl(server.baseUrl()).build();
    }

    @Bean
    public ProductServiceClient client(WebClient webClient) {
      return new ProductServiceClient(webClient);
    }
  }

  @Autowired private ObjectMapper mapper;
  @Autowired private WireMockServer server;

  @Autowired private ProductServiceClient client;

  @Test
  void returnsProductsWhenSuccessful() throws ProductServiceException {
    server.stubFor(
        get(urlEqualTo("/products"))
            .willReturn(
                aResponse()
                    .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                    .withBody(successBody())));

    List<Product> products = client.retrieveProducts();

    assertThat(products)
        .containsExactly(
            new Product("123", "Credit Card"), new Product("456", "Debit Card (Express)"));
  }

  @Test
  void throwsProductServiceExceptionWhenErrorStatus() {
    server.stubFor(get(anyUrl()).willReturn(aResponse().withStatus(400)));

    assertThatThrownBy(() -> client.retrieveProducts())
        .hasCauseInstanceOf(ProductServiceException.class);
  }

  @Test
  void setsAcceptHeader() throws ProductServiceException {
    server.stubFor(
        get(urlEqualTo("/products"))
            .willReturn(
                aResponse()
                    .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                    .withBody(successBody())));

    client.retrieveProducts();

    server.verify(
        getRequestedFor(urlEqualTo("/products"))
            .withHeader("accept", equalTo("application/json, application/*+json")));
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
