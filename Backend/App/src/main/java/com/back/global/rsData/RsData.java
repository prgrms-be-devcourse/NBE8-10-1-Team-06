package com.back.global.rsData;

public record RsData<T>(
    String message,
    T data
) {
    public RsData(String message, T data) {
        this.message = message;
        this.data = data;
    }

}
