package dev.hodory.musinsa.product.repository;

import dev.hodory.musinsa.product.domain.projection.LowestPriceProductDTO;
import dev.hodory.musinsa.product.domain.projection.ProductInfo;
import java.util.List;

public interface ProductRepositoryCustom {

    /**
     * 카테고리별 최소 가격을 조회합니다.
     * @return 카테고리별 최소 가격 리스트
     */
    List<LowestPriceProductDTO> findLowestPricePerCategory();

    /**
     * 브랜드와 카테고리별 최소 가격을 조회합니다.
     * @return 브랜드와 카테고리별 최소 가격 리스트
     */
    List<ProductInfo> findLowestPriceGroupByBrandIdAndCategoryId();

    /**
     * 특정 카테고리의 최소 가격을 조회합니다.
     * @param categoryId 카테고리 ID
     * @return 카테고리별 최소 가격 리스트
     */
    List<ProductInfo> findLowestPriceByCategoryId(Long categoryId);

    /**
     * 특정 카테고리의 최고 가격을 조회합니다.
     * @param categoryId 카테고리 ID
     * @return 카테고리별 최고 가격 리스트
     */
    List<ProductInfo> findHighestPriceByCategoryId(Long categoryId);
}
