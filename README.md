# Integration Testing your Spring (Boot) HTTP Clients

An example repo to go with the following articles:

- [Integration Testing Your Spring `RestTemplate`s with `RestClientTest`](https://www.jvt.me/posts/2022/02/01/resttemplate-integration-test/)
- [Integration Testing Your Spring `WebClient`s with okhttp's `MockWebServer`](https://www.jvt.me/posts/2022/02/07/webclient-integration-test/)
- [Integration Testing Your Spring `WebClient`s with Wiremock](https://www.jvt.me/posts/2022/03/22/webclient-integration-test-wiremock/)

## `RestTemplate` testing

This can be found under the package `me.jvt.hacking.resttemplate`.

Contains:

- `springtest`, which uses spring-test's HTTP server for validation

## `WebClient` testing

This can be found under the package `me.jvt.hacking.webclient`.

Contains:

- `okhttp`, which uses OkHttp as the HTTP server for validation
- `wiremock`, which uses Wiremock as the HTTP server for validation
