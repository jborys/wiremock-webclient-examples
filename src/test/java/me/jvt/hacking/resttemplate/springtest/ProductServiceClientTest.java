package me.jvt.hacking.resttemplate.springtest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withBadRequest;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import me.jvt.hacking.resttemplate.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClientResponseException;

@ContextConfiguration(classes = Application.class)
@RestClientTest(ProductServiceClient.class)
class ProductServiceClientTest {

  @Autowired private ObjectMapper mapper;
  @Autowired private MockRestServiceServer server;

  @Autowired private ProductServiceClient client;

  @Test
  void returnsProductsWhenSuccessful() throws ProductServiceException {
    server
        .expect(requestTo("/products"))
        .andRespond(withSuccess(successBody(), MediaType.APPLICATION_JSON));

    List<Product> products = client.retrieveProducts();

    assertThat(products)
        .containsExactly(
            new Product("123", "Credit Card"), new Product("456", "Debit Card (Express)"));
  }

  @Test
  void throwsProductServiceExceptionWhenErrorStatus() {
    server.expect(requestTo("/products")).andRespond(withBadRequest());

    assertThatThrownBy(() -> client.retrieveProducts())
        .isInstanceOf(ProductServiceException.class)
        .hasCauseInstanceOf(RestClientResponseException.class);
  }

  @Test
  void setsAcceptHeader() throws ProductServiceException {
    server
        .expect(header("accept", "application/json, application/*+json"))
        .andRespond(withSuccess(successBody(), MediaType.APPLICATION_JSON));

    client.retrieveProducts();

    server.verify();
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
