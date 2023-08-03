package me.jvt.hacking.webclient.okhttp;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.TimeUnit;
import me.jvt.hacking.webclient.Application;
import me.jvt.hacking.webclient.WebClientConfig;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
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

  private final MockWebServer server = new MockWebServer();

  @BeforeEach
  void setup() {
    // required to be set, otherwise `takeRequest` will never return anything
    server.enqueue(new MockResponse());
  }

  @Test
  void fooSetsApiKey() throws InterruptedException {
    foo.get().uri(server.url("/products").toString()).retrieve().toBodilessEntity().block();

    RecordedRequest request = server.takeRequest(1, TimeUnit.SECONDS);
    assertThat(request).isNotNull(); // could also be wrapped in an `Optional`
    assertThat(request.getPath()).isEqualTo("/products");
    assertThat(request.getHeader("Api-Key")).isEqualTo("1.2.3");
  }

  @Test
  void barSetsTextPlainAcceptHeader() throws InterruptedException {
    bar.get().uri(server.url("/products").toString()).retrieve().bodyToMono(String.class).block();

    RecordedRequest request = server.takeRequest(1, TimeUnit.SECONDS);
    assertThat(request).isNotNull(); // could also be wrapped in an `Optional`
    assertThat(request.getPath()).isEqualTo("/products");
    assertThat(request.getHeader("accept")).isEqualTo(MediaType.TEXT_PLAIN_VALUE);
  }
}
