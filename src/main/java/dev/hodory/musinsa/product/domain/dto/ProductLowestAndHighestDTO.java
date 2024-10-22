package dev.hodory.musinsa.product.domain.dto;

import dev.hodory.musinsa.product.domain.projection.ProductInfo;
import dev.hodory.musinsa.utils.PriceFormatter;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProductLowestAndHighestDTO {
    private String category;
    private List<PriceInfo> lowest;
    private List<PriceInfo> highest;

    @Builder
    private ProductLowestAndHighestDTO(String category, List<PriceInfo> lowest, List<PriceInfo> highest) {
        this.category = category;
        this.lowest = lowest;
        this.highest = highest;
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class PriceInfo {
        private String brand;
        private String price;

        @Builder
        protected PriceInfo(String brand, Long price) {
            this.brand = brand;
            this.price = PriceFormatter.format(price);
        }

        public static PriceInfo of(ProductInfo product) {
            return PriceInfo.builder()
                .brand(product.getBrandName())
                .price(product.getPrice())
                .build();
        }
    }
}
