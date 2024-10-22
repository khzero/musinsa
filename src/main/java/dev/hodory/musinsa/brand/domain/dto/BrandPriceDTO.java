package dev.hodory.musinsa.brand.domain.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BrandPriceDTO {

    private String brand;
    private String price;

    @Builder
    private BrandPriceDTO(String brand, String price) {
        this.brand = brand;
        this.price = price;
    }
}
