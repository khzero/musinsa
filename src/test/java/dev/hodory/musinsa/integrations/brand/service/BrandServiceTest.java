package dev.hodory.musinsa.integrations.brand.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import dev.hodory.musinsa.brand.domain.dto.BrandDTO.Request;
import dev.hodory.musinsa.brand.repository.BrandRepository;
import dev.hodory.musinsa.product.domain.entity.Product;
import dev.hodory.musinsa.product.repository.ProductRepository;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import dev.hodory.musinsa.brand.domain.dto.BrandDTO;
import dev.hodory.musinsa.brand.service.BrandService;
import dev.hodory.musinsa.integrations.IntegrationServiceTestBase;

@DisplayName("BrandService의 Integration 테스트를 실행합니다.")
class BrandServiceTest extends IntegrationServiceTestBase {

    @Autowired
    private BrandService brandService;

    @Autowired
    private ProductRepository productRepository;

    @Test
    @DisplayName("브랜드명이 빈 문자열일 경우 IllegalArgumentException을 던진다")
    void shouldThrowIllegalArgumentExceptionWhenBrandNameIsEmpty() {
        // Given
        final String brandName = "";
        final Request request = Request.builder()
            .name(brandName)
            .build();

        // When & Then
        assertThatThrownBy(() -> brandService.addBrand(request))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("브랜드명이 중복될 경우 IllegalArgumentException을 던진다")
    void shouldThrowIllegalArgumentExceptionWhenBrandNameIsDuplicate() {
        // Given
        final String brandName = "A";
        final Request request = Request.builder()
            .name(brandName)
            .build();

        // When & Then
        assertThatThrownBy(() -> brandService.addBrand(request))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("브랜드를 등록하면 등록된 브랜드명이 리턴된다.")
    void shouldRegisterBrand() {
        // Given
        final String brandName = "ABCD";
        final Request request = Request.builder()
            .name(brandName)
            .build();

        // When
        BrandDTO response = brandService.addBrand(request);

        // Then
        assertThat(response.getName()).isEqualTo(brandName);
    }

    @Test
    @DisplayName("브랜드명을 수정하면 수정된 브랜드명이 리턴된다.")
    void shouldUpdateBrand() {
        // Given
        final Long brandId = 1L;
        final String brandName = "ABCD";
        final BrandDTO.Request request = BrandDTO.Request.builder()
            .name(brandName)
            .build();

        // When
        final BrandDTO brandDTO = brandService.updateBrand(brandId, request);

        // Then
        assertThat(brandDTO).extracting("id", "name")
            .containsExactly(brandId, brandName);
    }


    @Test
    @DisplayName("브랜드를 삭제하면, 해당 브랜드에 등록된 상품을 모두 삭제한다.")
    void shouldDeleteProductsInBrandOnBrandDeleteSuccessfully() {
        // given
        final long brandId = 1L;
        brandService.deleteById(brandId);
        final List<Product> products = productRepository.findAllByBrandId(brandId);

        // when& then
        assertThat(products).isEmpty();
    }
}
