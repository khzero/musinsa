package dev.hodory.musinsa.integrations.product.service;

import static dev.hodory.musinsa.product.domain.entity.QProduct.product;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.assertj.core.groups.Tuple;

import dev.hodory.musinsa.brand.domain.dto.BrandProductDTO;
import dev.hodory.musinsa.brand.domain.entity.Brand;
import dev.hodory.musinsa.brand.repository.BrandRepository;
import dev.hodory.musinsa.product.domain.dto.ProductListDTO;
import dev.hodory.musinsa.product.domain.dto.ProductLowestAndHighestDTO;
import dev.hodory.musinsa.category.domain.entity.Category;
import dev.hodory.musinsa.category.domain.enums.CategoryInfo;
import dev.hodory.musinsa.category.repository.CategoryRepository;
import dev.hodory.musinsa.integrations.IntegrationServiceTestBase;
import dev.hodory.musinsa.product.domain.dto.ProductDTO;
import dev.hodory.musinsa.product.domain.entity.Product;
import dev.hodory.musinsa.product.repository.ProductRepository;
import dev.hodory.musinsa.product.service.ProductService;
import jakarta.persistence.EntityNotFoundException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@DisplayName("ProductService의 Integration 테스트를 실행 합니다.")
class ProductServiceTest extends IntegrationServiceTestBase {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductService productService;

    @Autowired
    private JPAQueryFactory queryFactory;

    private Product createProduct(Long id, Brand brand, Category category, Long price) {
        return Product.builder()
            .id(id)
            .brand(brand)
            .category(category)
            .price(price)
            .build();
    }

    private static ProductDTO getProductDTO(Brand brand, Category category, Long price) {
        return ProductDTO.builder()
            .id(2024L)
            .brandId(brand.getId())
            .category(category.getCode().name())
            .price(price)
            .build();
    }

    @Nested
    @DisplayName("카테고리 별 최저가격 브랜드와 상품 가격, 총액을 조회하는 테스트")
    class LowestPricePerCategory {

        @Test
        @DisplayName("테스트 데이터를 이용하여 카테고리별 최저가 상품 조회시 상품 목록과 총액이 조회된다.")
        void getLowestPricePerCategory() {
            // 테스트 실행
            final ProductListDTO list = productService.getLowestPricePerCategory();

            // 결과 검증
            assertThat(list.getProducts()).hasSize(8);
            assertThat(list.getTotalPrice()).isEqualTo("34,100");

            // 가격 검증
            assertThat(list.getProducts()).extracting("priceFormatted")
                .containsExactly("10,000", "5,000", "3,000", "9,000", "2,000", "1,500", "1,700",
                    "1,900");

            // 브랜드 검증
            assertThat(list.getProducts()).extracting("brand")
                .containsExactly("C", "E", "D", "G", "A", "D", "I", "F");

            // 카테고리 검증
            assertThat(list.getProducts()).extracting("category")
                .containsExactly("상의", "아우터", "바지", "스니커즈", "가방", "모자", "양말", "액세서리");
        }

        @Test
        @DisplayName("특정 카테고리에 같은 브랜드의 더 저렴한 상품 추가 시, 해당 카테고리가 최저가의 브랜드 상품으로 조회된다")
        @Transactional
        void getLowestPricePerCategoryWithNewCheaperProduct() {
            // given
            final Brand brand = brandRepository.findByName("E").get();
            final Category category = categoryRepository.findByCode(CategoryInfo.OUTER).get();
            final ProductDTO dto = getProductDTO(brand, category, 4000L);
            final Product product = createProduct(dto.getId(), brand, category, dto.getPrice());
            productRepository.save(product);
            final ProductDTO dto2 = getProductDTO(brand, category, 3000L);
            final Product product2 = createProduct(dto2.getId(), brand, category, dto2.getPrice());
            productRepository.save(product2);

            // when
            final ProductListDTO list = productService.getLowestPricePerCategory();

            // then
            assertThat(list.getProducts()).hasSize(8);
            assertThat(list.getTotalPrice()).isEqualTo("32,100");
            assertThat(list.getProducts()).extracting("priceFormatted")
                .containsExactly("10,000", "3,000", "3,000", "9,000", "2,000", "1,500", "1,700",
                    "1,900");
            assertThat(list.getProducts()).extracting("brand")
                .containsExactly("C", "E", "D", "G", "A", "D", "I", "F");
            assertThat(list.getProducts()).extracting("category")
                .containsExactly("상의", "아우터", "바지", "스니커즈", "가방", "모자", "양말", "액세서리");
        }

        @Test
        @DisplayName("카테고리 상의의 최저가 상품 17번을 제거하면 상의의 최저가가 10,100원으로 변경된다")
        void getLowestPricePerCategoryWithDeletedProduct() {
            // given
            final Product product = productRepository.findById(17L)
                .orElseThrow(EntityNotFoundException::new);
            productRepository.delete(product);

            // when
            final ProductListDTO list = productService.getLowestPricePerCategory();

            // then
            assertThat(list.getProducts()).hasSize(8);
            assertThat(list.getTotalPrice()).isEqualTo("34,200");
            assertThat(list.getProducts()).extracting("priceFormatted")
                .containsExactly("10,100", "5,000", "3,000", "9,000", "2,000", "1,500", "1,700",
                    "1,900");
            assertThat(list.getProducts()).extracting("brand")
                .containsExactly("D", "E", "D", "G", "A", "D", "I", "F");
        }

        @Test
        @DisplayName("카테고리중 하나라도 등록된 상품이 없으면 예외가 발생한다.")
        void getLowestPricePerCategoryWithNoProducts() {
            // given
            queryFactory.delete(product).where(product.category.id.eq(1L)).execute();

            assertThrows(EntityNotFoundException.class, () -> productService.getLowestPricePerCategory());
        }
    }

    @Nested
    @DisplayName("단일 브랜드로 모든 카테고리 상품을 구매할 때 최저가격에 판매하는 브랜드와 카테고리의 상품가격, 총액을 조회하는 테스트 ")
    class LowestPricePerBrand {

        @Test
        @DisplayName("테스트 데이터를 이용하여 단일 브랜드로 모든 카테고리 상품을 구매할 때 최저가격에 판매하는 브랜드와 카테고리의 상품가격, 총액을 조회하면 상품 목록과 총액이 조회된다.")
        void getLowestPricePerBrand() {
            // given & when
            final BrandProductDTO result = productService.getLowestPriceBrand();

            // then
            assertThat(result.getBrandName()).isEqualTo("D");
            assertThat(result.getCategoryProduct()).hasSize(8);
            assertThat(result.getFormattedTotalPrice()).isEqualTo("36,100");
        }

        @Test
        @DisplayName("최저가 브랜드의 상품이 삭제되면 카테고리의 최저가 브랜드가 변경된다.")
        void getLowestPricePerBrandWithDeletedProduct() {
            // given
            final Product product = productRepository.findById(25L)
                .orElseThrow(EntityNotFoundException::new);
            productRepository.delete(product);

            // when
            final BrandProductDTO result = productService.getLowestPriceBrand();

            // then
            assertThat(result.getBrandName()).isEqualTo("C");
            assertThat(result.getCategoryProduct()).hasSize(8);
            assertThat(result.getFormattedTotalPrice()).isEqualTo("37,100");
        }

        @Test
        @DisplayName("상품이 삭제되어 해당 브랜드의 가격의 총합이 줄어들더라도 최저가 브랜드는 변경되지 않는다.")
        void getLowestPriceWhenBrandPriceSumIsReduced() {
            // given
            productRepository.deleteById(17L);

            // when
            final BrandProductDTO result = productService.getLowestPriceBrand();

            // then
            assertThat(result.getBrandName()).isEqualTo("D");
            assertThat(result.getCategoryProduct()).hasSize(8);
            assertThat(result.getFormattedTotalPrice()).isEqualTo("36,100");
        }
    }

    @Nested
    @DisplayName("카테고리 이름으로 최저, 최고 가격 브랜드와 상품 가격을 조회하는 API 테스트")
    class LowestAndHighestPricePerCategory {

        @Test
        @DisplayName("테스트 데이터를 이용하여 카테고리 이름으로 최저, 최고 가격 브랜드와 상품 가격을 조회하면 조회된다.")
        void getLowestAndHighestPricePerCategory() {
            // given & when
            final ProductLowestAndHighestDTO result = productService.getLowestAndHighestPriceBrandByCategoryName(
                CategoryInfo.TOP.getTitle());

            // then
            assertThat(result.getCategory()).isEqualTo("상의");
            assertThat(result.getLowest()).hasSize(1);
            assertThat(result.getHighest()).hasSize(1);
            assertThat(result.getLowest()).extracting("brand", "price")
                .containsExactly(Tuple.tuple("C", "10,000"));
            assertThat(result.getHighest()).extracting("brand", "price")
                .containsExactly(Tuple.tuple("I", "11,400"));
        }

        @Test
        @DisplayName("존재하지 않는 카테고리 이름으로 조회하면 예외가 발생한다.")
        void getLowestAndHighestPricePerCategoryWithNotExistCategory() {
            assertThrows(EntityNotFoundException.class, () -> productService.getLowestAndHighestPriceBrandByCategoryName("Non-Exists"));
        }

        @Test
        @DisplayName("카테고리에 등록된 상품이 없으면 예외가 발생한다.")
        void getLowestAndHighestPricePerCategoryWithNoProducts() {
            // given
            Category category = categoryRepository.findByTitle(CategoryInfo.TOP.getTitle())
                .orElseThrow(EntityNotFoundException::new);
            queryFactory.delete(product).where(product.category.id.eq(category.getId())).execute();

            // when & then
            assertThrows(EntityNotFoundException.class, () -> productService.getLowestAndHighestPriceBrandByCategoryName(category.getTitle()));
        }
    }
}
