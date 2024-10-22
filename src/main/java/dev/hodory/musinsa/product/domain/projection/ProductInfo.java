package dev.hodory.musinsa.product.domain.projection;

import com.querydsl.core.annotations.QueryProjection;

import dev.hodory.musinsa.product.domain.entity.Product;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProductInfo {

    private Long brandId;
    private String brandName;
    private Long categoryId;
    private String categoryName;
    private Long price;

    @Builder
    @QueryProjection
    public ProductInfo(Long brandId, String brandName, Long categoryId, String categoryName, Long price) {
        this.brandId = brandId;
        this.brandName = brandName;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.price = price;
    }

    public static ProductInfo of(Product product) {
        return ProductInfo.builder()
            .brandId(product.getBrand().getId())
            .brandName(product.getBrand().getName())
            .categoryId(product.getCategory().getId())
            .categoryName(product.getCategory().getTitle())
            .price(product.getPrice())
            .build();
    }
}
