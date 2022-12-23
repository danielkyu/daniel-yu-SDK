package com.danielkyu.oneapi;

/**
 * An custom RuntimeException that indicates an error condition occurred from the OneApi library
 * context.
 */
public class OneApiException extends RuntimeException {
  /**
   * Constructs the instance.
   *
   * @param msg The error message that describes the error.
   */
  OneApiException(String msg) {
    super(msg);
  }

  /**
   * Constructs the instance.
   *
   * @param msg The error message that describes the error.
   * @param cause The underlying Throwable that was responsible for causing this exception.
   */
  OneApiException(String msg, Throwable cause) {
    super(msg, cause);
  }
}
