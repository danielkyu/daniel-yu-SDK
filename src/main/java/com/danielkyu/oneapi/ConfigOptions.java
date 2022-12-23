package com.danielkyu.oneapi;

import lombok.Builder;
import lombok.Getter;
import okhttp3.OkHttpClient;

/**
 * The runtime configuration options that supplement the static configuration file.
 *
 * <p>Values in the ConfigOptions will override any values specified in the Config instance.
 */
@Builder
@Getter
public class ConfigOptions {
  /** The API key as provided by https://the-one-api.dev. */
  public String apiKey;

  /** The base URL of the OneApi service. */
  public String baseUrl;

  /** The OkHttpClient that should be used for network calls. */
  public OkHttpClient okHttpClient;
}
