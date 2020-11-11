package com.ottpay.paysdk.exceptions;

public class ExceptionInfo {
  ExceptionInfoType type = ExceptionInfoType.TEXT;
  String message;

  public ExceptionInfo() {
  }

  public ExceptionInfo(ExceptionInfoType type, String message) {
    this.type = type;
    this.message = message;
  }

  public ExceptionInfoType getType() {
    return type;
  }

  public void setType(ExceptionInfoType type) {
    this.type = type;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }
}
