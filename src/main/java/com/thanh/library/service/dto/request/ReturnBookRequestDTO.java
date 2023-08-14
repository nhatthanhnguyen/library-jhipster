package com.thanh.library.service.dto.request;

import java.util.Objects;

public class ReturnBookRequestDTO {

    private Long id;
    private boolean success;

    public ReturnBookRequestDTO() {}

    public ReturnBookRequestDTO(Long id, boolean success) {
        this.id = id;
        this.success = success;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReturnBookRequestDTO that = (ReturnBookRequestDTO) o;
        return success == that.success && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, success);
    }

    @Override
    public String toString() {
        return "ReturnBookRequestDTO{" + "checkoutId=" + id + ", returnSuccess=" + success + '}';
    }
}
