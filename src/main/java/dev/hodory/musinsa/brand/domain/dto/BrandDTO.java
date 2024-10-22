package dev.hodory.musinsa.brand.domain.dto;

import dev.hodory.musinsa.brand.domain.entity.Brand;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BrandDTO {

    private Long id;
    private String name;

    @Builder
    private BrandDTO(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public static BrandDTO of(Brand brand) {
        return BrandDTO.builder()
            .id(brand.getId())
            .name(brand.getName())
            .build();
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Request {

        @NotBlank(message = "브랜드 이름은 필수입니다.")
        private String name;

        @Builder
        private Request(String name) {
            this.name = name;
        }

        public Brand toEntity() {
            return Brand.builder()
                .name(name)
                .build();
        }
    }
}
