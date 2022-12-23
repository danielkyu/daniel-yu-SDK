package com.danielkyu.oneapi;

import com.danielkyu.oneapi.responses.MovieResponse;
import com.danielkyu.oneapi.utils.NetworkingUtils;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.QueryName;

/**
 * An abstract representation of the OneApi web service with callable endpoints as interface
 * methods.
 */
interface OneApiService {
  static final String BASE_URL_ONE_API_DEV = "https://the-one-api.dev/v2/";

  /**
   * Custom OkHttp interceptor that adds the request authorization header (API key) in every
   * outgoing request.
   */
  static class RequestAuthorizationHeaderInterceptor implements Interceptor {
    static final String HTTP_HEADER_AUTHORIZATION = "Authorization";

    private final String authorization;

    /**
     * Constructs the instance
     *
     * @param apiKey The API key as provided by https://the-one-api.dev.
     */
    RequestAuthorizationHeaderInterceptor(String apiKey) {
      this.authorization = "Bearer " + Validate.notBlank(apiKey);
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
      return chain.proceed(
          chain
              .request()
              .newBuilder()
              .addHeader(HTTP_HEADER_AUTHORIZATION, this.authorization)
              .build());
    }
  }

  /**
   * Fetches movie data.
   *
   * @return A Retrofit Call instance that contains the results of the API invocation.
   */
  @GET("movie")
  Call<MovieResponse> getMovies(@QueryName List<String> queryParams);

  /**
   * Creates an instance of the OneApiService.
   *
   * <p>The created implementation is thread-safe and should be reused across multiple contexts.
   *
   * @param config The Config instance produced from parsing the static configuration resource file.
   * @param configOptions The ConfigOptions instance that contains additional configuration options
   *     set at runtime.
   * @return An instance of OneApiService that can be used to interact with the web service.
   */
  static OneApiService create(Config config, ConfigOptions configOptions) {
    // If provided an OkHttpClient instance, use it to spawn a new instance for our use.
    // This allows us to configure the instance for our API calls but shares the same underlying
    // internal networking resources (e.g. connection pool, thread pools, etc.) as the rest of the
    // application. If the application is already using OkHttp, this should usually result in
    // better performance and resource usage.
    // -
    // https://medium.com/@leandromazzuquini/if-you-are-using-okhttp-you-should-know-this-61d68e065a2b

    OkHttpClient.Builder okHttpClientBuilder =
        Optional.ofNullable(configOptions.getOkHttpClient())
            .map(OkHttpClient::newBuilder)
            .orElse(null);

    if (okHttpClientBuilder == null) {
      // If a preferred OkHttp client does not exist, use sensible defaults to create our own.
      // This is implemented as a fallback in case the application does not use OkHttp as well as to
      // simplify integration/on-boarding for new projects.

      okHttpClientBuilder = NetworkingUtils.createOkHttpClientBuilder(config);
    }

    // Obtain the API key from the configOptions (runtime) or, if it doesn't exist, the config
    // object (compile-time resource file) as a fallback. If the API key is not defined in either
    // location, throw an error.
    String apiKey = Optional.ofNullable(configOptions.getApiKey()).orElse(config.getApiKey());

    if (StringUtils.isBlank(apiKey)) {
      throw new OneApiException("No API key was provided.");
    }

    okHttpClientBuilder.addInterceptor(new RequestAuthorizationHeaderInterceptor(apiKey));

    return new Retrofit.Builder()
        .baseUrl(Optional.ofNullable(configOptions.getBaseUrl()).orElse(BASE_URL_ONE_API_DEV))
        .client(okHttpClientBuilder.build())
        .addConverterFactory(JacksonConverterFactory.create())
        .build()
        .create(OneApiService.class);
  }
}
