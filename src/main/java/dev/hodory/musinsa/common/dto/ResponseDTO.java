package dev.hodory.musinsa.common.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ResponseDTO {

    private Object data;

    @Builder
    private ResponseDTO(Object data) {
        this.data = data;
    }

    public static ResponseDTO of(Object data) {
        return ResponseDTO.builder()
            .data(data)
            .build();
    }
}
