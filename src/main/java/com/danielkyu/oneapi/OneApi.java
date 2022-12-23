package com.danielkyu.oneapi;

import com.danielkyu.oneapi.params.MovieParams;
import com.danielkyu.oneapi.responses.MovieResponse;
import java.io.IOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import retrofit2.Call;
import retrofit2.Response;

/**
 * The entry point of the OneApi Java SDK.
 *
 * <p>This class provides a Java-native way for callers to interact with the OneApi web service.
 * This class is inherrently thread-safe. It is recommended that a single instance be created early
 * during the application's initialization and referenced as a singleton where needed.
 *
 * <p>The class requires that a configuration file be bundled as a resource at: one-api/config.yaml.
 * The API key, among other configuration values, will be loaded from this location.
 *
 * <p>Both synchronous and asynchronous mechanisms of invocation are supported. Note that the
 * aysnchronous model for handling network calls utilizes a thread pool in the background in order
 * to more efficiently manage resources. As a result of relying on the default behavior of the
 * thread pool, the application may hang for about a minute after termination as the VM tries to
 * wait for any in-flight network calls to complete. As a result, the asynchronous API is only
 * recommended for applications that hardly ever terminate (e.g. an Android application).
 */
public final class OneApi {
  private static final Logger logger = LogManager.getLogger();
  private static final String RESOURCE_PATH_CONFIG_FILE = "one-api/config.yaml";

  private final OneApiService oneApi;

  /**
   * Creates an instance.
   *
   * @throws OneApiException An error occurred while initializing the instance.
   */
  public OneApi() throws OneApiException {
    this(ConfigOptions.builder().build());
  }

  /**
   * Creates an instance.
   *
   * @param configOptions Additional configution options set at runtime that may override
   *     configuration set in the static configuration resource file.
   * @throws OneApiException An error occured while initializing the instance.
   */
  public OneApi(ConfigOptions configOptions) throws OneApiException {
    if (configOptions == null) {
      throw new OneApiException("ConfigOptions must not be null.");
    }

    logger.info("Loading OneApi configuration from resources: " + RESOURCE_PATH_CONFIG_FILE);

    this.oneApi =
        OneApiService.create(
            Config.loadConfigFromResource(RESOURCE_PATH_CONFIG_FILE), configOptions);

    logger.info(
        "Successfully loaded OneApi configuration from resources: " + RESOURCE_PATH_CONFIG_FILE);
    logger.info("Successfully created OneApi instance.");
  }

  /**
   * Returns movies based on the filters in the parameters provided.
   *
   * <p>This operation is synchronous and will block the calling thread until a response is
   * received.
   *
   * @param movieParams Parameters that specify the types of movies to return.
   * @return An instance of MovieResponse which contains the movies that meet the filter criteria.
   * @throws OneApiException An error occurred during the operation. Refer to the exception message
   *     for more details as to the cause of the error.
   */
  public MovieResponse getMovies(MovieParams movieParams) throws OneApiException {
    try {
      logger.info("Fetching movies from OneApi service.", movieParams);

      Response<MovieResponse> response = this.oneApi.getMovies(movieParams.toQueryList()).execute();

      if (!response.isSuccessful()) {
        throw new OneApiException("Failed to get movie data: Server returned " + response.code());
      }

      return response.body();
    } catch (IOException e) {
      throw new OneApiException("Failed to get movie data.", e);
    }
  }

  /**
   * Returns movies based on the filters in the parameters provided.
   *
   * <p>This operation is synchronous and will block the calling thread until a response is
   * received.
   *
   * @param movieParams Parameters that specify the types of movies to return.
   * @return An instance of MovieResponse which contains the movies that meet the filter criteria.
   * @throws OneApiException An error occurred during the operation. Refer to the exception message
   *     for more details as to the cause of the error.
   */
  public void getMovies(MovieParams movieParams, Callback<MovieResponse> callback) {
    logger.info("Fetching movies from OneApi service.");

    this.oneApi
        .getMovies(movieParams.toQueryList())
        .enqueue(
            new retrofit2.Callback<MovieResponse>() {
              @Override
              public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                int status = response.code();

                if (response.isSuccessful()) {
                  callback.onSuccess(status, response.body());
                } else {
                  callback.onFailure(status);
                }
              }

              @Override
              public void onFailure(Call<MovieResponse> call, Throwable t) {
                callback.onError(t);
              }
            });
  }
}
