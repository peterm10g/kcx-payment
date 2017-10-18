package com.lsh.payment.core.exception;

public class BusinessException extends RuntimeException {

    private static final long serialVersionUID = -2323383251706123601L;

    private String businessCode;

    private String data;

    public BusinessException() {
        super();
    }

    public BusinessException(String code) {
        super();
        this.businessCode = code;
    }

    public BusinessException(String code, String message) {
        super(message);
        this.businessCode = code;
    }

    public BusinessException(String code,String data ,String message) {
        super(message);
        this.businessCode = code;
        this.data = data;
    }

    public BusinessException(String code, String message, Throwable cause) {
        super(message, cause);
        this.businessCode = code;
    }

    public BusinessException(String code, Throwable cause) {
        super(cause);
        this.businessCode = code;
    }

    public String getCode() {
        return businessCode;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(this.getClass().getName());
        String code = this.getCode();
        if (code != null) {
            builder.append(" [").append(code).append("]");
        }
        String message = getMessage();
        if (message != null) {
            builder.append(": ").append(message);
        }
        return builder.toString();
    }
}
