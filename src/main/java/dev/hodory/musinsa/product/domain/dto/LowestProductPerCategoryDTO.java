package dev.hodory.musinsa.product.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import dev.hodory.musinsa.product.domain.projection.LowestPriceProductDTO;
import dev.hodory.musinsa.utils.PriceFormatter;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LowestProductPerCategoryDTO {

    private String category;
    private String brand;
    @JsonIgnore
    private Long price;
    @JsonProperty("price")
    private String priceFormatted;

    @Builder
    private LowestProductPerCategoryDTO(String category, String brand, Long price) {
        this.category = category;
        this.brand = brand;
        this.price = price;
        this.priceFormatted = PriceFormatter.format(price);
    }

    public static LowestProductPerCategoryDTO of(LowestPriceProductDTO product) {
        return LowestProductPerCategoryDTO.builder()
                .category(product.getCategory())
                .brand(product.getBrand())
                .price(product.getPrice())
                .build();
    }
}
