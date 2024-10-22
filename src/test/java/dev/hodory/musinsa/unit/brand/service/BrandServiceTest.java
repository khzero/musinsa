package dev.hodory.musinsa.unit.brand.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import dev.hodory.musinsa.brand.domain.dto.BrandDTO;
import dev.hodory.musinsa.brand.domain.dto.BrandDTO.Request;
import dev.hodory.musinsa.brand.domain.entity.Brand;
import dev.hodory.musinsa.brand.repository.BrandRepository;
import dev.hodory.musinsa.brand.service.BrandService;
import jakarta.persistence.EntityNotFoundException;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@DisplayName("BrandService Unit 테스트")
class BrandServiceTest {

    @InjectMocks
    private BrandService brandService;

    @Mock
    private BrandRepository brandRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private Brand createBrand(Long id, String name) {
        return Brand.builder().id(id).name(name).build();
    }

    private Request createRegisterRequest(String name) {
        return Request.builder().name(name).build();
    }

    @Nested
    @DisplayName("브랜드 등록 테스트")
    class AddBrandTest {

        @Test
        @DisplayName("정상적으로 브랜드를 등록한다")
        void shouldAddBrandSuccessfully() {
            // Given
            String brandName = "BrandName";
            Request request = createRegisterRequest(brandName);
            Brand brand = createBrand(1L, brandName);
            when(brandRepository.save(any(Brand.class))).thenReturn(brand);

            // When
            BrandDTO result = brandService.addBrand(request);

            // Then
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getName()).isEqualTo(brandName);
        }

        @Test
        @DisplayName("브랜드명이 null일 경우 IllegalArgumentException을 던진다")
        void shouldThrowIllegalArgumentExceptionWhenBrandNameIsNull() {
            // Given
            Request request = createRegisterRequest(null);

            // When & Then
            assertThatThrownBy(() -> brandService.addBrand(request))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("브랜드명이 빈 문자열일 경우 IllegalArgumentException을 던진다")
        void shouldThrowIllegalArgumentExceptionWhenBrandNameIsEmpty() {
            // Given
            String brandName = "";
            Request request = createRegisterRequest(brandName);

            // When & Then
            assertThatThrownBy(() -> brandService.addBrand(request))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("브랜드명이 중복될 경우 IllegalArgumentException을 던진다")
        void shouldThrowIllegalArgumentExceptionWhenBrandNameIsDuplicate() {
            // Given
            String brandName = "BrandName";
            Request request = createRegisterRequest(brandName);
            Brand brand = createBrand(1L, brandName);
            when(brandRepository.findByName(brandName)).thenReturn(Optional.of(brand));

            // When & Then
            assertThatThrownBy(() -> brandService.addBrand(request))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("브랜드 수정 테스트")
    class UpdateBrandTest {

        @Test
        @DisplayName("정상적으로 브랜드명을 업데이트한다")
        void shouldUpdateBrandSuccessfully() {
            // Given
            Long brandId = 1L;
            String updatedName = "UpdatedBrandName";
            BrandDTO.Request updateData = BrandDTO.Request.builder().name(updatedName).build();
            Brand brand = createBrand(brandId, updatedName);

            when(brandRepository.findById(brandId)).thenReturn(Optional.of(brand));
            when(brandRepository.save(any(Brand.class))).thenReturn(brand);

            // When
            BrandDTO result = brandService.updateBrand(brandId, updateData);

            // Then
            assertThat(result.getId()).isEqualTo(brandId);
            assertThat(result.getName()).isEqualTo(updatedName);
        }

        @Test
        @DisplayName("존재하지 않는 브랜드 ID로 수정 시 EntityNotFoundException을 던진다")
        void shouldThrowEntityNotFoundExceptionWhenBrandNotFound() {
            // Given
            Long nonExistentId = 999L;
            BrandDTO.Request updateData = BrandDTO.Request.builder().name("UpdatedBrandName").build();
            when(brandRepository.findById(nonExistentId)).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> brandService.updateBrand(nonExistentId, updateData))
                    .isInstanceOf(EntityNotFoundException.class);
        }

        @Test
        @DisplayName("브랜드명이 null일 경우 IllegalArgumentException을 던진다")
        void shouldThrowIllegalArgumentExceptionWhenBrandNameIsNull() {
            // Given
            Long brandId = 1L;
            BrandDTO.Request updateData = BrandDTO.Request.builder().name(null).build();
            Brand brand = createBrand(brandId, "BrandName");
            when(brandRepository.findById(brandId)).thenReturn(Optional.of(brand));

            // When & Then
            assertThatThrownBy(() -> brandService.updateBrand(brandId, updateData))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("브랜드명이 빈 문자열일 경우 IllegalArgumentException을 던진다")
        void shouldThrowIllegalArgumentExceptionWhenBrandNameIsEmpty() {
            // Given
            Long brandId = 1L;
            BrandDTO.Request updateData = BrandDTO.Request.builder().name("").build();
            Brand brand = createBrand(brandId, "BrandName");
            when(brandRepository.findById(brandId)).thenReturn(Optional.of(brand));

            // When & Then
            assertThatThrownBy(() -> brandService.updateBrand(brandId, updateData))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("브랜드명이 중복될 경우 IllegalArgumentException을 던진다")
        void shouldThrowIllegalArgumentExceptionWhenBrandNameIsDuplicate() {
            // Given
            Long brandId = 1L;
            String updatedName = "UpdatedBrandName";
            BrandDTO.Request updateData = BrandDTO.Request.builder().name(updatedName).build();
            Brand brand = createBrand(brandId, updatedName);
            when(brandRepository.findById(brandId)).thenReturn(Optional.of(brand));
            when(brandRepository.findByName(updatedName)).thenReturn(Optional.of(brand));

            // When & Then
            assertThatThrownBy(() -> brandService.updateBrand(brandId, updateData))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("브랜드 삭제 테스트")
    class DeleteBrandTest {

        @Test
        @DisplayName("정상적으로 브랜드를 삭제한다")
        void shouldDeleteBrandSuccessfully() {
            // Given
            Long brandId = 1L;
            Brand brand = createBrand(brandId, "BrandName");
            when(brandRepository.findById(brandId)).thenReturn(Optional.of(brand));

            // When
            BrandDTO result = brandService.deleteById(brandId);

            // Then
            assertThat(result.getId()).isEqualTo(brandId);
            assertThat(result.getName()).isEqualTo(brand.getName());
        }

        @Test
        @DisplayName("존재하지 않는 브랜드 ID로 삭제 시 EntityNotFoundException을 던진다")
        void shouldThrowEntityNotFoundExceptionWhenDeletingNonExistentBrand() {
            // Given
            Long nonExistentId = 999L;
            when(brandRepository.findById(nonExistentId)).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> brandService.deleteById(nonExistentId))
                    .isInstanceOf(EntityNotFoundException.class);
        }
    }
}
