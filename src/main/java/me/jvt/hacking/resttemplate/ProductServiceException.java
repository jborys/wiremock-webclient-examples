package me.jvt.hacking.resttemplate;

public class ProductServiceException extends Exception {
  public ProductServiceException(String message) {
    super(message);
  }

  public ProductServiceException(String message, Throwable throwable) {
    super(message, throwable);
  }
}
