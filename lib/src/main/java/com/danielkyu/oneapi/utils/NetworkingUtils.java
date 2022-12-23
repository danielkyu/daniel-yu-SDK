package com.danielkyu.oneapi.utils;

import com.danielkyu.oneapi.Config;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

/** Utility methods for common networking related operations. */
public final class NetworkingUtils {
  static final int DEFAULT_MAX_IDLE_CONNECTIONS = 5;
  static final long DEFAULT_KEEP_ALIVE_DURATION_MS = 60 * 1000;

  private NetworkingUtils() {
    // Prevent instantiation as this is a utility class.
  }

  /**
   * Creates a new OkHttpClient.Builder instance that can be used to generate an OkHttpClient
   * instance using properties from the configuration instance.
   *
   * @param config The Config instance produced from parsing the static configuration resource file.
   * @return OkHttpClient.Builder instance for generating OkHttpClient instances.
   */
  public static OkHttpClient.Builder createOkHttpClientBuilder(Config config) {
    OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder();
    boolean isLoggingEnabled =
        Optional.ofNullable(config.getNetworking())
            .map(Config.Networking::isLogTraffic)
            .orElse(false);

    if (isLoggingEnabled) {
      // Log networking traffic to the console--useful for development/debug purposes.

      HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();

      loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);
      okHttpClientBuilder.addInterceptor(loggingInterceptor);
    }

    return okHttpClientBuilder.connectionPool(
        new ConnectionPool(
            DEFAULT_MAX_IDLE_CONNECTIONS, DEFAULT_KEEP_ALIVE_DURATION_MS, TimeUnit.MILLISECONDS));
  }
}
