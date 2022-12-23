package com.danielkyu.oneapi;

import com.danielkyu.oneapi.params.MovieParams;
import com.danielkyu.oneapi.responses.MovieResponse;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class OneApiTest {
  private static final String TEST_MOVIE_RESPONSE =
      "{\"docs\":[{\"_id\": \"100\", \"name\": \"The Lord of the Ring Series\"}, {\"_id\": \"200\","
          + " \"name\": \"The Hobbit Series\"}, {\"_id\": \"300\", \"name\": \"The Unexpected"
          + " Journey\"}], \"total\": 3, \"limit\": 10, \"offset\": 0, \"page\": 1}";

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
  void getMoviesSynchronousSuccess() {
    this.server.enqueue(new MockResponse().setResponseCode(200).setBody(TEST_MOVIE_RESPONSE));

    OneApi oneApi =
        new OneApi(ConfigOptions.builder().baseUrl(this.server.url("/").url().toString()).build());
    MovieResponse response = oneApi.getMovies(new MovieParams());

    Assertions.assertEquals(3, response.getTotal());
    Assertions.assertEquals(10, response.getLimit());
    Assertions.assertEquals(0, response.getOffset());
    Assertions.assertEquals(1, response.getPage());

    Assertions.assertEquals("100", response.getMovies().get(0).getId());
    Assertions.assertEquals("The Lord of the Ring Series", response.getMovies().get(0).getName());
    Assertions.assertEquals("200", response.getMovies().get(1).getId());
    Assertions.assertEquals("The Hobbit Series", response.getMovies().get(1).getName());
    Assertions.assertEquals("300", response.getMovies().get(2).getId());
    Assertions.assertEquals("The Unexpected Journey", response.getMovies().get(2).getName());
  }

  @Test
  void getMoviesSynchronousFailure() {
    this.server.enqueue(new MockResponse().setResponseCode(404).setBody("{}"));

    OneApi oneApi =
        new OneApi(ConfigOptions.builder().baseUrl(this.server.url("/").url().toString()).build());
    OneApiException exception =
        Assertions.assertThrows(OneApiException.class, () -> oneApi.getMovies(new MovieParams()));

    Assertions.assertEquals(
        "Failed to get movie data: Server returned 404", exception.getMessage());
  }

  @Test
  void getMoviesSynchronousError() {
    OneApi oneApi = new OneApi(ConfigOptions.builder().baseUrl("https://localhost:1").build());
    OneApiException exception =
        Assertions.assertThrows(OneApiException.class, () -> oneApi.getMovies(new MovieParams()));

    Assertions.assertEquals("Failed to get movie data.", exception.getMessage());
  }

  @Test
  void getMoviesAsynchronousSuccess() throws Exception {
    this.server.enqueue(new MockResponse().setResponseCode(200).setBody(TEST_MOVIE_RESPONSE));

    OneApi oneApi =
        new OneApi(ConfigOptions.builder().baseUrl(this.server.url("/").url().toString()).build());
    CompletableFuture<MovieResponse> result = new CompletableFuture<>();

    oneApi.getMovies(
        new MovieParams(),
        new Callback<MovieResponse>() {
          @Override
          public void onSuccess(int status, MovieResponse response) {
            try {
              Assertions.assertEquals(200, status);
              result.complete(response);
            } catch (Exception e) {
              result.completeExceptionally(e);
            }
          }

          @Override
          public void onFailure(int status) {
            result.completeExceptionally(new AssertionError("Control should never end up here."));
          }

          @Override
          public void onError(Throwable error) {
            result.completeExceptionally(new AssertionError("Control should never end up here."));
          }
        });

    MovieResponse response = result.get();

    Assertions.assertEquals(3, response.getTotal());
    Assertions.assertEquals(10, response.getLimit());
    Assertions.assertEquals(0, response.getOffset());
    Assertions.assertEquals(1, response.getPage());

    Assertions.assertEquals("100", response.getMovies().get(0).getId());
    Assertions.assertEquals("The Lord of the Ring Series", response.getMovies().get(0).getName());
    Assertions.assertEquals("200", response.getMovies().get(1).getId());
    Assertions.assertEquals("The Hobbit Series", response.getMovies().get(1).getName());
    Assertions.assertEquals("300", response.getMovies().get(2).getId());
    Assertions.assertEquals("The Unexpected Journey", response.getMovies().get(2).getName());
  }

  @Test
  void getMoviesAsynchronousFailure() throws Exception {
    this.server.enqueue(new MockResponse().setResponseCode(404).setBody("{}"));

    OneApi oneApi =
        new OneApi(ConfigOptions.builder().baseUrl(this.server.url("/").url().toString()).build());
    CompletableFuture<Integer> result = new CompletableFuture<>();

    oneApi.getMovies(
        new MovieParams(),
        new Callback<MovieResponse>() {
          @Override
          public void onSuccess(int status, MovieResponse response) {
            result.completeExceptionally(new AssertionError("Control should never end up here."));
          }

          @Override
          public void onFailure(int status) {
            try {
              result.complete(status);
            } catch (Exception e) {
              result.completeExceptionally(e);
            }
          }

          @Override
          public void onError(Throwable error) {
            result.completeExceptionally(new AssertionError("Control should never end up here."));
          }
        });

    Assertions.assertEquals(404, result.get());
  }

  @Test
  void getMoviesAsynchronousError() throws Exception {
    OneApi oneApi =
        new OneApi(
            ConfigOptions.builder()
                .baseUrl(this.server.url("https://localhost:1").url().toString())
                .build());
    CompletableFuture<Void> result = new CompletableFuture<>();

    oneApi.getMovies(
        new MovieParams(),
        new Callback<MovieResponse>() {
          @Override
          public void onSuccess(int status, MovieResponse response) {
            result.completeExceptionally(new AssertionError("Control should never end up here."));
          }

          @Override
          public void onFailure(int status) {
            result.completeExceptionally(new AssertionError("Control should never end up here."));
          }

          @Override
          public void onError(Throwable error) {

            try {
              Assertions.assertEquals(
                  "Failed to connect to localhost/[0:0:0:0:0:0:0:1]:1", error.getMessage());
              result.complete(null);
            } catch (Exception e) {
              result.completeExceptionally(e);
            }
          }
        });

    result.get();
  }
}
