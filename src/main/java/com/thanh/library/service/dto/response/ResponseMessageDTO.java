package com.thanh.library.service.dto.response;

import java.time.Instant;
import java.util.Objects;

public class ResponseMessageDTO {

    private int statusCode;
    private String message;
    private Instant timestamp;

    public ResponseMessageDTO() {}

    public ResponseMessageDTO(int statusCode, String message, Instant timestamp) {
        this.statusCode = statusCode;
        this.message = message;
        this.timestamp = timestamp;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ResponseMessageDTO that = (ResponseMessageDTO) o;
        return statusCode == that.statusCode && Objects.equals(message, that.message) && Objects.equals(timestamp, that.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(statusCode, message, timestamp);
    }

    @Override
    public String toString() {
        return "ResponseMessageDTO{" + "statusCode=" + statusCode + ", message='" + message + '\'' + ", timestamp=" + timestamp + '}';
    }
}
