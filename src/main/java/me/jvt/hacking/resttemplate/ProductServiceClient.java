package me.jvt.hacking.resttemplate;

import java.util.List;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

@Component
public class ProductServiceClient {
  private final RestTemplate template;

  public ProductServiceClient(RestTemplateBuilder builder) {
    template = builder.build();
  }

  public List<Product> retrieveProducts() throws ProductServiceException {
    ResponseEntity<ProductContainer> response;
    try {
      response = template.getForEntity("/products", ProductContainer.class);
    } catch (RestClientResponseException e) {
      throw new ProductServiceException("Huh, something went wrong", e);
    }

    if (response.getBody() == null) {
      throw new ProductServiceException(
          "No response body was returned from the service, even though it returned HTTP "
              + response.getStatusCodeValue());
    }

    return response.getBody().getProducts();
  }
}
