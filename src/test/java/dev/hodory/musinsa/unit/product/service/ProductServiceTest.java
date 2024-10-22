package dev.hodory.musinsa.unit.product.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;

import dev.hodory.musinsa.brand.domain.dto.BrandProductDTO;
import dev.hodory.musinsa.brand.domain.entity.Brand;
import dev.hodory.musinsa.brand.repository.BrandRepository;
import dev.hodory.musinsa.product.domain.projection.LowestPriceProductDTO;
import dev.hodory.musinsa.category.domain.entity.Category;
import dev.hodory.musinsa.category.domain.enums.CategoryInfo;
import dev.hodory.musinsa.category.repository.CategoryRepository;
import dev.hodory.musinsa.product.domain.dto.ProductDTO;
import dev.hodory.musinsa.product.domain.dto.ProductListDTO;
import dev.hodory.musinsa.product.domain.dto.ProductLowestAndHighestDTO;
import dev.hodory.musinsa.product.domain.entity.Product;
import dev.hodory.musinsa.product.domain.projection.ProductInfo;
import dev.hodory.musinsa.product.repository.ProductRepository;
import dev.hodory.musinsa.product.service.ProductService;
import dev.hodory.musinsa.unit.UnitServiceTestBase;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

@DisplayName("ProductService의 Unit 테스트를 실행합니다.")
class ProductServiceTest extends UnitServiceTestBase {

    public static final long PRODUCT_ID = 2024L;

    @InjectMocks
    private ProductService productService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private BrandRepository brandRepository;

    @Mock
    private CategoryRepository categoryRepository;

    private Product createProduct(Long id, Brand brand, Category category, Long price) {
        return Product.builder()
                .id(id)
                .brand(brand)
                .category(category)
                .price(price)
                .build();
    }

    private static ProductDTO getProductDTO(Brand brand, Category category) {
        return ProductDTO.builder()
                .id(PRODUCT_ID)
                .brandId(brand.getId())
                .category(category.getCode().name())
                .price(3000L)
                .build();
    }

    private static Category getCategory() {
        return Category.builder()
                .id(6L)
                .category(CategoryInfo.HAT)
                .build();
    }

    private static Category getCategory(Long id, CategoryInfo category) {
        return Category.builder()
                .id(id)
                .category(category)
                .build();
    }

    private static Brand getBrand() {
        return Brand.builder()
                .id(517L)
                .name("Test_BrandName")
                .build();
    }

    private static Brand createBrand(Long id, String name) {
        return Brand.builder()
                .id(id)
                .name(name)
                .build();
    }

    @Nested
    @DisplayName("상품 등록/수정/삭제에 대한 유닛 테스트를 실행합니다.")
    class ProductManageTest {

        @Test
        @DisplayName("정상적인 상품 등록 요청시 상품에 대한 정보를 반환 합니다.")
        void shouldReturnProductWhenAddingProduct() {
            // given
            final Brand brand = getBrand();
            final Category category = getCategory();
            final ProductDTO dto = getProductDTO(brand, category);
            final Product product = createProduct(dto.getId(), brand, category, dto.getPrice());
            final ProductDTO.Response expected = ProductDTO.Response.of(product);

            when(productRepository.save(any(Product.class))).thenReturn(product);
            when(brandRepository.findById(brand.getId())).thenReturn(Optional.of(brand));
            when(categoryRepository.findByCode(category.getCode())).thenReturn(
                    Optional.of(category));

            // when
            ProductDTO.Response result = productService.addProduct(dto);

            // then
            assertThat(result.getId()).isEqualTo(expected.getId());
            assertThat(result.getCategoryName()).isEqualTo(expected.getCategoryName());
            assertThat(result.getPrice()).isEqualTo(expected.getPrice());
        }

        @Test
        @DisplayName("상품 수정 요청 성공시 수정된 상품 정보를 리턴합니다.")
        void shouldReturnUpdatedProductWhenUpdatingProduct() {
            // given
            final Brand brand = getBrand();
            final Category category = getCategory();
            final Product product = createProduct(PRODUCT_ID, brand, category, 2000L);

            final Category updateCategory = getCategory(3L, CategoryInfo.SOCKS);
            final Brand updateBrand = createBrand(3L, "New Brand");
            final long updatePrice = 4500L;
            final Product updatedProduct = createProduct(product.getId(), updateBrand,
                    updateCategory,
                    updatePrice);
            final ProductDTO dto = ProductDTO.builder().id(PRODUCT_ID)
                    .brandId(updateBrand.getId())
                    .category(updateCategory.getCode().name())
                    .price(updatePrice)
                    .build();
            final ProductDTO.Response expected = ProductDTO.Response.of(updatedProduct);

            when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
            when(brandRepository.findById(updateBrand.getId())).thenReturn(
                    Optional.of(updateBrand));
            when(productRepository.save(product)).thenReturn(updatedProduct);
            when(categoryRepository.findByCode(updateCategory.getCode())).thenReturn(
                    Optional.of(updateCategory));

            // when
            ProductDTO.Response result = productService.updateProduct(product.getId(), dto);

            // then
            assertThat(result.getId()).isEqualTo(expected.getId());
            assertThat(result.getCategoryName()).isEqualTo(expected.getCategoryName());
            assertThat(result.getPrice()).isEqualTo(expected.getPrice());
        }

        @Test
        @DisplayName("상품 업데이트 요청시 상품이 존재하지 않을 경우 EntityNotFoundException 에러가 발생 합니다.")
        void shouldThrowExceptionWhenUpdatingProductNotFound() {
            // given
            final Brand brand = getBrand();
            final Category category = getCategory();
            final ProductDTO dto = getProductDTO(brand, category);

            when(brandRepository.findById(brand.getId())).thenReturn(Optional.of(brand));
            when(categoryRepository.findByCode(category.getCode())).thenReturn(
                    Optional.of(category));
            when(productRepository.findById(dto.getId())).thenReturn(Optional.empty());

            // when & then
            assertThrows(EntityNotFoundException.class,
                    () -> productService.updateProduct(dto.getId(), dto));
        }

        @Test
        @DisplayName("상품 업데이트 요청시 카테고리만 변경 요청 했을 경우 카테고리만 변경됩니다.")
        void shouldOnlyUpdateCategoryWhenCategoryIsChanged() {
            // given
            final Brand brand = getBrand();
            final Category category = getCategory();
            final long price = 4000L;
            final Product product = createProduct(PRODUCT_ID, brand, category, price);
            final Category updateCategory = getCategory(3L, CategoryInfo.SOCKS);
            final ProductDTO dto = ProductDTO.builder().id(product.getId())
                    .category(updateCategory.getCode().name())
                    .build();
            final Product updatedProduct = createProduct(product.getId(), brand, updateCategory,
                    price);
            final ProductDTO.Response expected = ProductDTO.Response.of(updatedProduct);

            when(brandRepository.findById(brand.getId())).thenReturn(Optional.of(brand));
            when(productRepository.findById(dto.getId())).thenReturn(Optional.of(product));
            when(productRepository.save(product)).thenReturn(updatedProduct);
            when(categoryRepository.findByCode(updateCategory.getCode())).thenReturn(
                    Optional.of(updateCategory));

            // when
            final ProductDTO.Response result = productService.updateProduct(product.getId(), dto);

            // then
            assertThat(result.getId()).isEqualTo(expected.getId());
            assertThat(result.getCategoryName()).isEqualTo(expected.getCategoryName());
            assertThat(result.getBrand().getName()).isEqualTo(brand.getName());
            assertThat(result.getPrice()).isEqualTo(price);
        }

        @Test
        @DisplayName("브랜드가 변경되면 상품의 브랜드도 업데이트된다")
        void shouldUpdateBrandWhenBrandIsChanged() {
            // Given
            Long productId = 1L;
            Long newBrandId = 2L;
            Category category = getCategory(); // 카테고리 생성
            Product existingProduct = Product.builder()
                    .id(productId)
                    .brand(createBrand(1L, "Old Brand"))
                    .category(category) // 카테고리 설정
                    .price(1000L) // 가격 설정
                    .build();

            Brand newBrand = createBrand(newBrandId, "New Brand");

            ProductDTO updateDto = ProductDTO.builder()
                    .brandId(newBrandId)
                    .build();

            when(productRepository.findById(productId)).thenReturn(Optional.of(existingProduct));
            when(brandRepository.findById(newBrandId)).thenReturn(Optional.of(newBrand));
            when(productRepository.save(existingProduct)).thenReturn(existingProduct);

            // When
            ProductDTO.Response result = productService.updateProduct(productId, updateDto);

            // Then
            assertThat(result.getBrand().getId()).isEqualTo(newBrandId);
            verify(productRepository).save(existingProduct);
        }

        @Test
        @DisplayName("상품 삭제시 삭제된 상품에 대한 정보를 리턴 합니다.")
        void shouldReturnDeletedProductWhenDeletingProduct() {
            // given
            final Brand brand = getBrand();
            final Category category = getCategory();
            final Product product = createProduct(PRODUCT_ID, brand, category, 4000L);
            ProductDTO.Response expected = ProductDTO.Response.of(product);

            when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));

            // when
            final ProductDTO.Response result = productService.deleteProduct(PRODUCT_ID);

            // then
            assertThat(result.getId()).isEqualTo(expected.getId());
            assertThat(result.getCategoryName()).isEqualTo(expected.getCategoryName());
            assertThat(result.getPrice()).isEqualTo(expected.getPrice());
        }

        @Test
        @DisplayName("상품 삭제시 상품이 존재하지 않을 경우 EntityNotFoundException 오류가 발생 합니다.")
        void shouldThrowExceptionWhenDeletingProductNotFound() {
            // given
            final Long id = 999L;

            // when
            when(productRepository.findById(id)).thenReturn(Optional.empty());

            // then
            assertThrows(EntityNotFoundException.class, () -> productService.deleteProduct(id));
        }
    }

    @Nested
    @DisplayName("카테고리 별 최저가격 브랜드와 상품 가격, 총액을 조회하는 기능에 대한 유닛 테스트를 실행합니다")
    class LowestPricePerCategoryTest {

        @Test
        @DisplayName("카테고리 별 최저 가격 브랜드와 상품 가격, 총액이 정상적으로 조회 되는지 확인합니다.")
        void shouldReturnLowestPricePerCategory() {
            // given
            when(productRepository.findLowestPricePerCategory()).thenReturn(
                    List.of(
                            LowestPriceProductDTO.builder()
                                    .category(CategoryInfo.TOP.getTitle())
                                    .brand("C")
                                    .price(10000L)
                                    .build(),
                            LowestPriceProductDTO.builder()
                                    .category(CategoryInfo.OUTER.getTitle())
                                    .brand("E")
                                    .price(5000L)
                                    .build(),
                            LowestPriceProductDTO.builder()
                                    .category(CategoryInfo.PANTS.getTitle())
                                    .brand("D")
                                    .price(3000L)
                                    .build(),
                            LowestPriceProductDTO.builder()
                                    .category(CategoryInfo.SNEAKERS.getTitle())
                                    .brand("G")
                                    .price(9000L)
                                    .build(),
                            LowestPriceProductDTO.builder()
                                    .category(CategoryInfo.BAG.getTitle())
                                    .brand("A")
                                    .price(2000L)
                                    .build(),
                            LowestPriceProductDTO.builder()
                                    .category(CategoryInfo.HAT.getTitle())
                                    .brand("D")
                                    .price(1500L)
                                    .build(),
                            LowestPriceProductDTO.builder()
                                    .category(CategoryInfo.SOCKS.getTitle())
                                    .brand("I")
                                    .price(1700L)
                                    .build(),
                            LowestPriceProductDTO.builder()
                                    .category(CategoryInfo.ACCESSORY.getTitle())
                                    .brand("F")
                                    .price(1900L)
                                    .build()));

            // when
            ProductListDTO result = productService.getLowestPricePerCategory();

            // then
            assertThat(result.getProducts()).hasSize(8);
            assertThat(result.getTotalPrice()).isEqualTo("34,100");
            assertThat(result.getProducts()).extracting("category").containsExactly(
                    CategoryInfo.TOP.getTitle(),
                    CategoryInfo.OUTER.getTitle(),
                    CategoryInfo.PANTS.getTitle(),
                    CategoryInfo.SNEAKERS.getTitle(),
                    CategoryInfo.BAG.getTitle(),
                    CategoryInfo.HAT.getTitle(),
                    CategoryInfo.SOCKS.getTitle(),
                    CategoryInfo.ACCESSORY.getTitle());
            assertThat(result.getProducts()).extracting("brand").containsExactly(
                    "C",
                    "E",
                    "D",
                    "G",
                    "A",
                    "D",
                    "I",
                    "F");
            assertThat(result.getProducts()).extracting("price").containsExactly(
                    10000L,
                    5000L,
                    3000L,
                    9000L,
                    2000L,
                    1500L,
                    1700L,
                    1900L);
        }

        @Test
        @DisplayName("상품이 없는 카테고리가 있을 때 카테고리별 최저가 조회시 EntityNotFoundException 예외가 발생한다")
        void shouldThrowExceptionWhenCategoryHasNoProducts() {
            // given
            final Category category = getCategory();

            when(productRepository.findLowestPricePerCategory()).thenReturn(
                    List.of(
                            LowestPriceProductDTO.builder()
                                    .category(category.getTitle())
                                    .brand("C")
                                    .price(1000L)
                                    .build()));

            // when & then
            assertThrows(EntityNotFoundException.class, productService::getLowestPricePerCategory);
        }

        @Test
        @DisplayName("등록된 상품이 없는 경우 카테고리별 최저가 조회시 EntityNotFoundException 예외가 발생한다")
        void shouldThrowExceptionWhenAllCategoryHasNoProducts() {
            // when & then
            assertThrows(EntityNotFoundException.class, productService::getLowestPricePerCategory);
        }
    }

    @Nested
    @DisplayName("단일 브랜드로 모든 카테고리 상품을 구매할 때 최저가격에 판매하는 브랜드와 카테고리의 상품가격, 총액을 조회하는 테스트")
    class LowestPriceBrand {

        @DisplayName("등록된 상품이 없는 경우 최저가 브랜드 조회시 EntityNotFoundException 예외가 발생한다")
        @Test
        void shouldThrowExceptionWhenAllCategoryHasNoProducts() {
            assertThrows(EntityNotFoundException.class, productService::getLowestPriceBrand);
        }
    }

    @Nested
    @DisplayName("최저가 브랜드의 상품 정보 조회")
    class LowestPriceBrandProductInfoTest {

        @DisplayName("최저가 브랜드의 상품 정보 조회시 상품의 브랜드 및 카테고리 정보가 정상적으로 조회되는지 확인합니다.")
        @Test
        void shouldReturnLowestPriceBrandProductInfo() {
            // given
            final Brand brand = Brand.builder().id(4L).name("D").build();
            final Product product1 = createProduct(1L, brand,
                    Category.builder().id(1L).category(CategoryInfo.TOP).build(), 10100L);
            final Product product2 = createProduct(2L, brand,
                    Category.builder().id(2L).category(CategoryInfo.OUTER).build(), 5100L);
            final Product product3 = createProduct(3L, brand,
                    Category.builder().id(3L).category(CategoryInfo.PANTS).build(), 3000L);
            final Product product4 = createProduct(4L, brand,
                    Category.builder().id(4L).category(CategoryInfo.SNEAKERS).build(), 9500L);
            final Product product5 = createProduct(5L, brand,
                    Category.builder().id(5L).category(CategoryInfo.BAG).build(), 2500L);
            final Product product6 = createProduct(6L, brand,
                    Category.builder().id(6L).category(CategoryInfo.HAT).build(), 1500L);
            final Product product7 = createProduct(7L, brand,
                    Category.builder().id(7L).category(CategoryInfo.SOCKS).build(), 2400L);
            final Product product8 = createProduct(8L, brand,
                    Category.builder().id(8L).category(CategoryInfo.ACCESSORY).build(), 2000L);

            when(productRepository.findLowestPriceGroupByBrandIdAndCategoryId()).thenReturn(
                    List.of(ProductInfo.of(product1), ProductInfo.of(product2),
                            ProductInfo.of(product3), ProductInfo.of(product4), ProductInfo.of(product5),
                            ProductInfo.of(product6), ProductInfo.of(product7), ProductInfo.of(product8)));

            final BrandProductDTO result = productService.getLowestPriceBrand();

            assertThat(result.getBrandName()).isEqualTo(brand.getName());
            assertThat(result.getCategoryProduct()).hasSize(8);
            assertThat(result.getCategoryProduct()).extracting("category", "price").containsExactly(
                    Tuple.tuple("상의", "10,100"),
                    Tuple.tuple("아우터", "5,100"),
                    Tuple.tuple("바지", "3,000"),
                    Tuple.tuple("스니커즈", "9,500"),
                    Tuple.tuple("가방", "2,500"),
                    Tuple.tuple("모자", "1,500"),
                    Tuple.tuple("양말", "2,400"),
                    Tuple.tuple("액세서리", "2,000"));
            assertThat(result.getFormattedTotalPrice()).isEqualTo("36,100");
        }

        @DisplayName("최저가 브랜드의 상품 정보 조회시 등록된 상품이 없는 경우 EntityNotFoundException 예외가 발생한다")
        @Test
        void shouldThrowExceptionWhenAllCategoryHasNoProducts() {
            assertThrows(EntityNotFoundException.class, productService::getLowestPriceBrand);
        }

        @DisplayName("최저가 브랜드의 상품 정보 조회시 브랜드에 카테고리 하나라도 상품이 없는 경우 EntityNotFoundException 예외가 발생한다")
        @Test
        void shouldThrowExceptionWhenCategoryHasNoProducts() {
            // given
            final Brand brand = Brand.builder().id(4L).name("D").build();
            final Product product1 = createProduct(1L, brand,
                    Category.builder().id(1L).category(CategoryInfo.TOP).build(), 10100L);
            final Product product2 = createProduct(2L, brand,
                    Category.builder().id(2L).category(CategoryInfo.OUTER).build(), 5100L);
            final Product product3 = createProduct(3L, brand,
                    Category.builder().id(3L).category(CategoryInfo.PANTS).build(), 3000L);
            final Product product4 = createProduct(4L, brand,
                    Category.builder().id(4L).category(CategoryInfo.SNEAKERS).build(), 9500L);
            final Product product5 = createProduct(5L, brand,
                    Category.builder().id(5L).category(CategoryInfo.BAG).build(), 2500L);
            final Product product6 = createProduct(6L, brand,
                    Category.builder().id(6L).category(CategoryInfo.HAT).build(), 1500L);
            final Product product7 = createProduct(7L, brand,
                    Category.builder().id(7L).category(CategoryInfo.SOCKS).build(), 2400L);

            when(productRepository.findLowestPriceGroupByBrandIdAndCategoryId()).thenReturn(
                    List.of(ProductInfo.of(product1), ProductInfo.of(product2),
                            ProductInfo.of(product3), ProductInfo.of(product4), ProductInfo.of(product5),
                            ProductInfo.of(product6), ProductInfo.of(product7)));

            // when & then
            assertThrows(EntityNotFoundException.class, productService::getLowestPriceBrand);
        }
    }

    @Nested
    @DisplayName("카테고리 이름으로 최저, 최고 가격 브랜드와 상품 가격을 조회하는 테스트를 실행합니다")
    class GetLowestPriceGroupByBrandIdAndCategoryId {
        @Test
        @DisplayName("등록되지 않은 카테고리로 조회시 EntityNotFoundException 예외가 발생한다")
        void shouldThrowExceptionWhenCategoryIsNotRegistered() {
            assertThrows(EntityNotFoundException.class,
                    () -> productService.getLowestAndHighestPriceBrandByCategoryName(
                            CategoryInfo.TOP.getTitle()));
        }

        @Test
        @DisplayName("카테고리에 등록된 상품이 없는 경우 EntityNotFoundException 예외가 발생한다")
        void shouldThrowExceptionWhenCategoryHasNoProducts() {
            // given
            final Category category = getCategory();
            when(categoryRepository.findByTitle(category.getTitle())).thenReturn(
                    Optional.of(category));

            // when & then
            assertThrows(EntityNotFoundException.class,
                    () -> productService.getLowestAndHighestPriceBrandByCategoryName(
                            CategoryInfo.TOP.getTitle()));
        }

        @Test
        @DisplayName("카테고리 이름으로 최저, 최고 가격 브랜드와 상품 가격을 조회시 정상적으로 조회되는지 확인합니다.")
        void shouldReturnLowestAndHighestPriceBrandByCategoryName() {
            // given
            final Category category = getCategory();
            final Brand brand = getBrand();
            final Product product = createProduct(1L, brand, category, 1000L);
            final Product product2 = createProduct(2L, brand, category, 500L);
            when(categoryRepository.findByTitle(category.getTitle())).thenReturn(
                    Optional.of(category));
            when(productRepository.findLowestPriceByCategoryId(category.getId())).thenReturn(
                    List.of(ProductInfo.of(product2)));
            when(productRepository.findHighestPriceByCategoryId(category.getId())).thenReturn(
                    List.of(ProductInfo.of(product)));

            // when
            final ProductLowestAndHighestDTO result = productService.getLowestAndHighestPriceBrandByCategoryName(
                    category.getTitle());

            // then
            assertThat(result.getCategory()).isEqualTo(category.getTitle());
            assertThat(result.getLowest()).hasSize(1);
            assertThat(result.getHighest()).hasSize(1);
            assertThat(result.getLowest().get(0).getBrand()).isEqualTo(brand.getName());
            assertThat(result.getLowest().get(0).getPrice()).isEqualTo("500");
            assertThat(result.getHighest().get(0).getBrand()).isEqualTo(brand.getName());
            assertThat(result.getHighest().get(0).getPrice()).isEqualTo("1,000");
        }

        @Test
        @DisplayName("다른 브랜드의 동일한 가격의 상품이 여러개인 경우, 여러개를 리턴합니다.")
        void shouldReturnMultipleProductsWhenSamePrice() {
            // given
            final Category category = getCategory();
            final Brand brand = getBrand();
            final Brand newBrand = createBrand(987L, "Test_NewBrand");
            final Product product = createProduct(1L, brand, category, 1000L);
            final Product product2 = createProduct(2L, newBrand, category, 1000L);
            when(categoryRepository.findByTitle(category.getTitle())).thenReturn(
                    Optional.of(category));
            when(productRepository.findLowestPriceByCategoryId(category.getId())).thenReturn(
                    List.of(ProductInfo.of(product), ProductInfo.of(product2)));
            when(productRepository.findHighestPriceByCategoryId(category.getId())).thenReturn(
                    List.of(ProductInfo.of(product), ProductInfo.of(product2)));

            // when
            final ProductLowestAndHighestDTO result = productService.getLowestAndHighestPriceBrandByCategoryName(
                    category.getTitle());

            // then
            assertThat(result.getLowest()).hasSize(2);
            assertThat(result.getHighest()).hasSize(2);

            assertThat(result.getLowest()).extracting("brand", "price").containsExactly(
                    Tuple.tuple(brand.getName(), "1,000"),
                    Tuple.tuple(newBrand.getName(), "1,000"));
            assertThat(result.getHighest()).extracting("brand", "price").containsExactly(
                    Tuple.tuple(brand.getName(), "1,000"),
                    Tuple.tuple(newBrand.getName(), "1,000"));
        }
    }
}
