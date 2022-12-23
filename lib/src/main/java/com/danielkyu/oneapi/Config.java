package com.danielkyu.oneapi;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import java.io.BufferedInputStream;
import java.io.IOException;
import lombok.Getter;

/**
 * Configuration options parsed from the static resource configuration file: one-api/config.yaml.
 */
@Getter
public class Config {
  @Getter
  public static class Networking {
    @JsonProperty("log-traffic")
    public boolean logTraffic;
  }

  @JsonProperty("api-key")
  public String apiKey;

  public Networking networking;

  /**
   * Loads the configuration file from a resource path.
   *
   * @param path The resource path where the configuration file is located. This path must not start
   *     with a leading slash (/).
   * @return A new Config instance that contains the values parsed from the configuration file.
   */
  static Config loadConfigFromResource(String path) {
    ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());

    try (BufferedInputStream bufferedIn =
        new BufferedInputStream(classLoader.getResourceAsStream(path))) {
      return objectMapper.readValue(bufferedIn, Config.class);
    } catch (IOException e) {
      throw new OneApiException("Failed to load configuration from resource: " + path, e);
    }
  }
}
