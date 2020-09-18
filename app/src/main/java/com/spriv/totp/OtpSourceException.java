package com.spriv.totp;

public class OtpSourceException extends Exception {
  public OtpSourceException(String message) {
    super(message);
  }

  public OtpSourceException(String message, Throwable cause) {
    super(message, cause);
  }
}

