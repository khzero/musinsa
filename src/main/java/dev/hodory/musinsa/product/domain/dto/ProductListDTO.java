package dev.hodory.musinsa.product.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.hodory.musinsa.utils.PriceFormatter;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProductListDTO {

    private List<LowestProductPerCategoryDTO> products = new ArrayList<>();

    @JsonProperty("total_price")
    private String totalPrice;

    @Builder
    private ProductListDTO(List<LowestProductPerCategoryDTO> products, Long totalPrice) {
        this.products = products;
        this.totalPrice = PriceFormatter.format(totalPrice);
    }

    public static ProductListDTO of(List<LowestProductPerCategoryDTO> products) {
        final Long totalPrice = products.parallelStream().mapToLong(LowestProductPerCategoryDTO::getPrice).sum();

        return ProductListDTO.builder()
            .products(products)
            .totalPrice(totalPrice)
            .build();
    }
}
