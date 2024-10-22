package dev.hodory.musinsa.product.domain.projection;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LowestPriceProductDTO {

    private String category;

    private String brand;

    private Long price;

    @Builder
    @QueryProjection
    public LowestPriceProductDTO(String category, String brand, Long price) {
        this.category = category;
        this.brand = brand;
        this.price = price;
    }
}
