package com.ottpay.paysdk.exceptions;

public class ApiException extends Exception {
  int status = 400;
  ExceptionInfo info;

  public ApiException() {
  }

  public ApiException(String message) {
    super(message);
  }

  public ApiException(int status, String message) {
    super(message);
    this.status = status;
  }

  public ApiException(String message, ExceptionInfo info) {
    super(message);
    this.info = info;
  }

  public ApiException(int status, String message, ExceptionInfo info) {
    super(message);
    this.status = status;
    this.info = info;
  }

  public int getStatus() {
    return status;
  }

  public ExceptionInfo getInfo() {
    return info;
  }

  public void setInfo(ExceptionInfo info) {
    this.info = info;
  }
}
