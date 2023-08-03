package me.jvt.hacking.webclient;

import java.util.List;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class ProductServiceClient {
  private final WebClient webClient;

  public ProductServiceClient(@Qualifier("defaultWebClient") WebClient webClient) {
    this.webClient = webClient;
  }

  public List<Product> retrieveProducts() throws ProductServiceException {
    ProductContainer response;
    response =
        webClient
            .get()
            .uri("/products")
            .accept(MediaType.APPLICATION_JSON, MediaType.valueOf("application/*+json"))
            .retrieve()
            .onStatus(
                HttpStatus::is4xxClientError,
                error -> Mono.error(new ProductServiceException("Huh, something went wrong")))
            .bodyToMono(ProductContainer.class)
            .block();

    if (response == null) {
      throw new ProductServiceException("No response body was returned from the service");
    }

    return response.getProducts();
  }
}
