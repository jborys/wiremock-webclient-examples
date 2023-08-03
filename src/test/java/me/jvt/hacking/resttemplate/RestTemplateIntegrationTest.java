package me.jvt.hacking.resttemplate;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.web.client.MockServerRestTemplateCustomizer;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

@ExtendWith(SpringExtension.class)
@Import(RestTemplateConfig.class)
@ContextConfiguration(classes = Application.class)
class RestTemplateIntegrationTest {

  @Autowired
  @Qualifier("foo")
  private RestTemplate foo;

  @Autowired
  @Qualifier("bar")
  private RestTemplate bar;

  private MockRestServiceServer serverFoo;
  private MockRestServiceServer serverBar;

  @BeforeEach
  void setup() {
    serverFoo = buildServer(foo);
    serverBar = buildServer(bar);
  }

  @Test
  void fooSetsApiKey() {
    serverFoo
        .expect(requestTo("/products"))
        .andExpect(header("Api-Key", "1.2.3"))
        .andRespond(withSuccess("Foo", MediaType.APPLICATION_JSON));

    foo.getForObject("/products", String.class);

    serverFoo.verify();
  }

  @Test
  void barSetsTextPlainAcceptHeader() {
    serverBar
        .expect(requestTo("/products"))
        .andExpect(header("accept", "text/plain"))
        .andRespond(withSuccess("Bar", MediaType.APPLICATION_JSON));

    bar.getForObject("/products", String.class);

    serverBar.verify();
  }

  private static MockRestServiceServer buildServer(RestTemplate restTemplate) {
    MockServerRestTemplateCustomizer serverRestTemplateCustomizer =
        new MockServerRestTemplateCustomizer();
    serverRestTemplateCustomizer.customize(restTemplate);
    return serverRestTemplateCustomizer.getServer();
  }
}
