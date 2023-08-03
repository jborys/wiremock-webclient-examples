package me.jvt.hacking.webclient;

public class ProductServiceException extends Exception {
  public ProductServiceException(String message) {
    super(message);
  }

  public ProductServiceException(String message, Throwable throwable) {
    super(message, throwable);
  }
}
