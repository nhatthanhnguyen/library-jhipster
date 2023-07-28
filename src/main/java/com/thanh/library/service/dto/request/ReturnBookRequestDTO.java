package com.thanh.library.service.dto.request;

import java.util.Objects;

public class ReturnBookRequestDTO {

    private Long checkoutId;
    private boolean returnSuccess;

    public ReturnBookRequestDTO(Long checkoutId, boolean returnSuccess) {
        this.checkoutId = checkoutId;
        this.returnSuccess = returnSuccess;
    }

    public Long getCheckoutId() {
        return checkoutId;
    }

    public void setCheckoutId(Long checkoutId) {
        this.checkoutId = checkoutId;
    }

    public boolean isReturnSuccess() {
        return returnSuccess;
    }

    public void setReturnSuccess(boolean returnSuccess) {
        this.returnSuccess = returnSuccess;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReturnBookRequestDTO that = (ReturnBookRequestDTO) o;
        return returnSuccess == that.returnSuccess && Objects.equals(checkoutId, that.checkoutId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(checkoutId, returnSuccess);
    }

    @Override
    public String toString() {
        return "ReturnBookRequestDTO{" + "checkoutId=" + checkoutId + ", returnSuccess=" + returnSuccess + '}';
    }
}
