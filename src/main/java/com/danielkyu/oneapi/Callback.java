package com.danielkyu.oneapi;

/** Callback instance used for asynchronous invocations to OneApi. */
public abstract class Callback<T> {
  /**
   * Callback function that is invoked if the call to the service resulted in an error.
   *
   * @param error A throwable that contains details on the error.
   */
  public void onError(Throwable error) {}

  /**
   * Callback function that is invoked if the service returned a non-successful error response.
   *
   * @param status The HTTP status code returned by the server.
   */
  public void onFailure(int status) {}

  /**
   * Callback function that is invoked if the server returned a successful response.
   *
   * @param status The HTTP status code returned by the server.
   * @param data The response body returned by the server.
   */
  public void onSuccess(int status, T data) {}
}
