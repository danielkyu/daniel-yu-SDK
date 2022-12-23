# one-api-java-sdk

A very minimal Java SDK for the-one-api.dev web service.

### Pre-requisites

This project assumes that you have [your own API key](https://the-one-api.dev/sign-up).

### Integrating the SDK

1. Add the following to your `build.gradle` file.

```
repositories {
  maven {
    url "https://jitpack.io"
  }
}

dependencies {
  implementation "com.github.danielkyu:Daniel-Yu-SDK:1.0.0"
}
```

2. Create a new configuration file in your resources folder at: `one-api/config.yaml` with the following content.

```
api-key: <INSERT_YOUR_API_KEY_HERE>
```

3. In your Java application, create a new instance of `OneApi`. Instances of `OneApi` are thread-safe and are best treated as a singleton.

### Invoking the Movie API

```java
OneApi oneApi = new OneApi();

/**
 * Synchronous invocation.
 *
 * Get movie with ID: 5cd95395de30eff6ebccde56.
 */
MovieResponse response = oneApi.getMovies(new MovieParams()
    .withAttributeEquals(MovieAttribute.ID, "5cd95395de30eff6ebccde56"));

System.out.println(response.getMovies().get(0).getName());

/**
 * Asynchronous invocation.
 *
 * Get a maximum of 10 movies that match the following criteria:
 *   - name contains the word 'Series' (case-insensitive)
 *   - budgetInMillions < 600
 *   - runtimeInMinutes >= 90
 */
oneApi.getMovies(
    new MovieParams()
        .withAttributeEquals(MovieAttribute.NAME, "/Series/i")
        .withAttributeLessThan(MovieAttribute.BUDGET_IN_MILLIONS, 600)
        .withAttributeGreaterThanOrEqualTo(MovieAttribute.RUNTIME_IN_MINUTES, 90)
        .withLimit(10),
    new Callback<MovieResponse> {
      @Override
      public void onSuccess(int status, MovieResponse response) {
        System.out.println(response.getMovies().get(0).getName());
      }

      @Override
      public void onFailure(int status) {
        System.out.println("Server responded with status: " + status);
      }

      @Override
      public void onError(Throwable error) {
        System.out.println("Failed to get movie data: " + error.getMessage());
      }
    });
```

### Running the Unit Tests

```
./gradlew test -i
```

### Building the SDK

```
./gradlew clean build

# Install to local Maven repository
./gradlew publishToMavenLocal

```

Artifacts are built and hosted on jitpack.io. Simply pushing the latest code and tagging it with a new release makes it available to serve artifacts. The artifact should be built the next time its requested.

### Design Principles

This SDK was designed with the following in mind.

1. The Java SDK should be general-purpose enough to run in any Java context (e.g. Android application, back-end web server, etc.).
2. The SDK should be as simple to use and integrate with regardless of the skill or experience of the developer.
3. We assume the web API is not in our control and the workings of the server beyond the API documentation is a complete black box to us.

### Architecture Points of Interest

The SDK is more or less a wrapper for the Retrofit HTTP client that is configured to talk to the-one-api.dev. During initialization, the SDK reads in a static configuration file that is hard-coded to be in `one-api/config.yaml` (see `Config.java`). This is typically where the user defines their API key and serves as a base for future SDK configuration. The SDK can also optionally take in some additional runtime configuration options that supplement and/or override the values from the configuration file (see `ConfigOptions.java`). While having a static configuration file is easy for implementation purposes, being able to dynamically configure properties at runtime can provide additional capabilities that cannot be represented at compile time. For example, we can configure the SDK to use an existing OkHttp client (assuming the application is already using it internally) in an effort to reduce the creation of duplicate resources. In the event that there is no existing OkHttpClient that can be used, the SDK will create a new instance that manages its own internal connection and thread pool with sensible defaults.

With the configuration in-place, the SDK creates a Retrofit HTTP client that can hit the endpoints of the web service and a Java interface (`OneApiService.java`) that provides a Java native mechanism for interacting with the service. The `getMovies` API can be used to fetch movie data about the Lord of the Rings. The movie parameters (`MovieParams.java`) can be specified to get back a list of movies that meet the criteria (`MovieResponse.java`).

In order to support a wide variety of Java contexts, the SDK includes both support for synchronous and asynchronous invocations of the Movie API. Synchronous calls are simpler and easier to deal with and are recommended when possible. They are a good fit for things like building back-end web applications since the server framework is responsible for providing a request thread context. In contrast the asynchronous API requires defining callback functions and usually run in a managed thread pool. Since the thread pool handles the lifecycle of the worker threads, it's possible that the application may not terminate until all worker threads have exhausted their work and the TTL has elapsed. As a result, this API is best suited for application contexts where the application rarerly, if ever, terminates naturally (e.g. Android applications).
