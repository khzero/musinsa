package dev.hodory.musinsa.brand.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import dev.hodory.musinsa.product.domain.projection.ProductInfo;
import dev.hodory.musinsa.utils.PriceFormatter;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BrandProductDTO {
    @JsonProperty("brand_name")
    private String brandName;
    @JsonProperty("category_products")
    private List<CategoryPrice> categoryProduct;
    @JsonIgnore
    private Long totalPrice;
    @JsonProperty("total_price")
    private String formattedTotalPrice;

    @Builder
    private BrandProductDTO(String brandName, List<CategoryPrice> categoryProduct, Long totalPrice) {
        this.brandName = brandName;
        this.categoryProduct = categoryProduct;
        this.totalPrice = totalPrice;
        this.formattedTotalPrice = PriceFormatter.format(totalPrice);
    }

    public static BrandProductDTO of(List<ProductInfo> products) {
        String brandName = products.get(0).getBrandName();
        List<CategoryPrice> categoryPrices = products.stream()
            .map(CategoryPrice::of)
            .toList();

        long totalPrice = products.stream()
            .mapToLong(ProductInfo::getPrice)
            .sum();

        return BrandProductDTO.builder()
            .brandName(brandName)
            .categoryProduct(categoryPrices)
            .totalPrice(totalPrice)
            .build();
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class CategoryPrice {
        private String category;
        private String price;

        @Builder
        private CategoryPrice(String category, Long price) {
            this.category = category;
            this.price = PriceFormatter.format(price);
        }

        public static CategoryPrice of(ProductInfo product) {
            return CategoryPrice.builder()
                .category(product.getCategoryName())
                .price(product.getPrice())
                .build();
        }
    }

    @Getter
    public static class Response {
        private final BrandProductDTO data;

        private Response(BrandProductDTO data) {
            this.data = data;
        }

        public static Response of(BrandProductDTO data) {
            return new Response(data);
        }
    }
}
