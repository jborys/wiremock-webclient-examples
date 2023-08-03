package me.jvt.hacking.webclient.wiremock;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

import com.github.tomakehurst.wiremock.WireMockServer;
import me.jvt.hacking.webclient.Application;
import me.jvt.hacking.webclient.WebClientConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.reactive.function.client.WebClient;

@ExtendWith(SpringExtension.class)
@Import(WebClientConfig.class)
@ContextConfiguration(classes = Application.class)
class WebClientIntegrationTest {

  @Autowired
  @Qualifier("foo")
  private WebClient foo;

  @Autowired
  @Qualifier("bar")
  private WebClient bar;

  private final WireMockServer server = new WireMockServer(options().dynamicPort());

  @BeforeEach
  void setup() {
    server.start();
    server.stubFor(get(anyUrl()).willReturn(aResponse().withStatus(200)));
  }

  @Test
  void fooSetsApiKey() {
    foo.get().uri(server.url("/products")).retrieve().toBodilessEntity().block();

    server.verify(getRequestedFor(urlEqualTo("/products")).withHeader("Api-Key", equalTo("1.2.3")));
  }

  @Test
  void barSetsTextPlainAcceptHeader() {
    bar.get().uri(server.url("/products")).retrieve().bodyToMono(String.class).block();

    server.verify(
        getRequestedFor(urlEqualTo("/products"))
            .withHeader("accept", equalTo(MediaType.TEXT_PLAIN_VALUE)));
  }
}
