package dev.hodory.musinsa.common.exception.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class ErrorResponse {

    private Error error;

    @AllArgsConstructor
    @Getter
    @Setter
    public static class Error {

        private String message;
        private String code;
    }

    public static ErrorResponse of(String message, String code) {
        return new ErrorResponse(new Error(message, code));
    }
}
