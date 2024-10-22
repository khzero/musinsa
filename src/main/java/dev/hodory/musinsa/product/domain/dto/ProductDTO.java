package dev.hodory.musinsa.product.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.hodory.musinsa.brand.domain.dto.BrandDTO;
import dev.hodory.musinsa.brand.domain.entity.Brand;
import dev.hodory.musinsa.category.domain.entity.Category;
import dev.hodory.musinsa.product.domain.entity.Product;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProductDTO {

    private Long id;
    private String category;
    private Long brandId;
    private Long price;

    @Builder
    protected ProductDTO(Long id, String category, Long brandId, Long price) {
        this.id = id;
        this.category = category;
        this.brandId = brandId;
        this.price = price;
    }

    public static ProductDTO of(CreateRequest request) {
        return ProductDTO.builder()
            .category(request.getCategory())
            .brandId(request.getBrandId())
            .price(request.getPrice())
            .build();
    }

    public static ProductDTO of(UpdateRequest request) {
        return ProductDTO.builder()
            .category(request.getCategory())
            .brandId(request.getBrandId())
            .price(request.getPrice())
            .build();
    }

    public Product toEntity(Category category, Brand brand) {
        return Product.builder()
            .id(id)
            .category(category)
            .brand(brand)
            .price(price)
            .build();
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class CreateRequest {

        @JsonProperty("category")
        @NotNull(message = "카테고리는 필수 입력값입니다.")
        private String category;
        @JsonProperty("brand_id")
        @NotNull(message = "브랜드 ID는 필수 입력값입니다.")
        private Long brandId;
        @JsonProperty("price")
        @NotNull(message = "가격은 필수 입력값입니다.")
        @Min(value = 0, message = "가격은 0 이상이어야 합니다.")
        private Long price;

        @Builder
        protected CreateRequest(String category, Long brandId, Long price) {
            this.category = category;
            this.brandId = brandId;
            this.price = price;
        }
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class UpdateRequest {

        @JsonProperty("category")
        private String category;
        @JsonProperty("brand_id")
        private Long brandId;
        @JsonProperty("price")
        @Min(value = 0, message = "가격은 0 이상이어야 합니다.")
        private Long price;

        @Builder
        protected UpdateRequest(String category, Long brandId, Long price) {
            this.category = category;
            this.brandId = brandId;
            this.price = price;
        }
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Response {

        @JsonProperty("id")
        private Long id;
        @JsonProperty("category_name")
        private String categoryName;
        @JsonProperty("brand")
        private BrandDTO brand;
        @JsonProperty("price")
        private Long price;

        @Builder
        private Response(Long id, String categoryName, BrandDTO brand, Long price) {
            this.id = id;
            this.categoryName = categoryName;
            this.brand = brand;
            this.price = price;
        }

        public static Response of(Product product) {
            return Response.builder()
                .id(product.getId())
                .categoryName(product.getCategory().getTitle())
                .brand(BrandDTO.of(product.getBrand()))
                .price(product.getPrice())
                .build();
        }
    }
}
