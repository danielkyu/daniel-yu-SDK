package com.danielkyu.oneapi;

import java.io.IOException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class OneApiServiceTest {
  private MockWebServer server;

  @BeforeEach
  void beforeEach() throws IOException {
    this.server = new MockWebServer();
    this.server.start();
  }

  @AfterEach
  void afterEach() throws IOException {
    this.server.shutdown();
    this.server = null;
  }

  @Test
  void doesAddAuthorizationHeader() throws InterruptedException, IOException {
    String apiKey = "api-key";

    this.server.enqueue(new MockResponse().setBody("body"));

    OkHttpClient okHttpClient =
        new OkHttpClient.Builder()
            .addInterceptor(new OneApiService.RequestAuthorizationHeaderInterceptor(apiKey))
            .build();

    okHttpClient.newCall(new Request.Builder().url(this.server.url("/")).get().build()).execute();

    Assertions.assertEquals("Bearer api-key", this.server.takeRequest().getHeader("Authorization"));
  }
}
