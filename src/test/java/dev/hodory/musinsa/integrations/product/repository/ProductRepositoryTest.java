package dev.hodory.musinsa.integrations.product.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

import dev.hodory.musinsa.brand.domain.entity.Brand;
import dev.hodory.musinsa.category.domain.entity.Category;
import dev.hodory.musinsa.category.domain.enums.CategoryInfo;
import dev.hodory.musinsa.integrations.IntegrationRepositoryTestBase;
import dev.hodory.musinsa.product.domain.entity.Product;
import dev.hodory.musinsa.product.domain.projection.ProductInfo;
import dev.hodory.musinsa.product.repository.ProductRepository;
import java.util.ArrayList;
import java.util.List;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("ProductRepository의 Integration 테스트를 실행 합니다.")
class ProductRepositoryTest extends IntegrationRepositoryTestBase {

    @Autowired
    private ProductRepository productRepository;

    private static final long BRAND_ID = 1L;
    private static final String BRAND_NAME = "A";
    private static final long PRODUCT_PRICE = 10000L;

    @Test
    @DisplayName("1번 브랜드의 카테고리별 최소 가격을 조회한다.")
    void getLowestPriceGroupByBrandIdAndCategoryId() {
        assertResults(getExpectedResults());
    }

    @Test
    @DisplayName("1번 브랜드의 상의 카테고리에 5000원 상품이 추가되면 상의의 최소 가격은 5000원이 된다.")
    void getLowestPriceGroupByBrandIdAndCategoryIdWhenNewProductAdded() {
        addProduct(5000L);

        List<Tuple> expectedResults = new ArrayList<>(getExpectedResults());
        expectedResults.set(0, tuple(BRAND_ID, 1L, 5000L));

        assertResults(expectedResults);
    }

    @Test
    @DisplayName("1번 브랜드의 상의 카테고리에 동일한 가격의 상품이 추가되면 최소 가격은 동일하게 유지된다.")
    void getLowestPriceGroupByBrandIdAndCategoryIdWhenSamePriceProductAdded() {
        addProduct(11200L);

        assertResults(getExpectedResults());
    }

    private void addProduct(long price) {
        addProduct(BRAND_ID, 1L, price);
    }

    private void addProduct(long brandId, long categoryId, long price) {
        final Product newProduct = Product.builder()
            .brand(Brand.builder().id(brandId).build())
            .category(Category.builder().id(categoryId).category(CategoryInfo.HAT).build())
            .price(price)
            .build();

        productRepository.save(newProduct);
    }

    private void assertResults(List<Tuple> expectedResults) {
        final List<ProductInfo> result = productRepository.findLowestPriceGroupByBrandIdAndCategoryId();

        List<ProductInfo> brand1Results = result.stream()
            .filter(dto -> dto.getBrandId() == BRAND_ID)
            .toList();

        assertThat(brand1Results).hasSize(8);
        assertThat(brand1Results).extracting("brandId", "categoryId", "price")
            .containsExactlyInAnyOrderElementsOf(expectedResults);
    }

    private List<Tuple> getExpectedResults() {
        return List.of(
            tuple(BRAND_ID, 1L, 11200L),
            tuple(BRAND_ID, 2L, 5500L),
            tuple(BRAND_ID, 3L, 4200L),
            tuple(BRAND_ID, 4L, 9000L),
            tuple(BRAND_ID, 5L, 2000L),
            tuple(BRAND_ID, 6L, 1700L),
            tuple(BRAND_ID, 7L, 1800L),
            tuple(BRAND_ID, 8L, 2300L));
    }

    @Test
    @DisplayName("특정 카테고리의 최소 가격을 조회 하였을때, 브랜드명과 금액을 리턴한다.")
    void findLowestPriceByCategoryId() {
        List<ProductInfo> result = productRepository.findLowestPriceByCategoryId(1L);

        assertThat(result).hasSize(1);
        assertThat(result).extracting("brandName", "price")
            .containsExactly(tuple("C", PRODUCT_PRICE));
    }

    @Test
    @DisplayName("특정 카테고리의 최고 가격을 조회 하였을때, 브랜드명과 금액을 리턴한다.")
    void findHighestPriceByCategoryId() {
        List<ProductInfo> result = productRepository.findHighestPriceByCategoryId(1L);

        assertThat(result).hasSize(1);
        assertThat(result).extracting("brandName", "price")
            .containsExactly(tuple("I", 11400L));
    }

    @Test
    @DisplayName("특정 카테고리의 상품이 1개만 존재하면 최소 가격과 최고 가격이 동일하게 리턴된다.")
    void findLowestPriceByCategoryIdWhenOnlyOneProduct() {
        productRepository.deleteAllInBatch();
        addProduct(PRODUCT_PRICE);

        List<ProductInfo> lowest = productRepository.findLowestPriceByCategoryId(1L);
        List<ProductInfo> highest = productRepository.findHighestPriceByCategoryId(1L);

        assertThat(lowest).hasSize(1);
        assertThat(lowest).extracting("brandName", "price")
            .containsExactly(tuple(BRAND_NAME, PRODUCT_PRICE));

        assertThat(highest).hasSize(1);
        assertThat(highest).extracting("brandName", "price")
            .containsExactly(tuple(BRAND_NAME, PRODUCT_PRICE));
    }

    @Test
    @DisplayName("특정 카테고리의 상품이 없으면 빈 리스트를 리턴한다.")
    void findLowestPriceByCategoryIdWhenNoProduct() {
        productRepository.deleteAllInBatch();

        List<ProductInfo> result = productRepository.findLowestPriceByCategoryId(1L);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("특정 카테고리에 최저가 상품이 여러개 있으면 모든 상품의 브랜드명과 가격이 리턴된다.")
    void findLowestPriceByCategoryIdWhenMultipleLowestPriceProduct() {
        final long categoryId = 1L;
        final long price = 3000L;
        addProduct(1L, categoryId, price);
        addProduct(2L, categoryId, price);

        List<ProductInfo> result = productRepository.findLowestPriceByCategoryId(categoryId);

        assertThat(result).hasSize(2);
        assertThat(result).extracting("brandId", "price")
            .containsExactly(
                tuple(1L, price),
                tuple(2L, price)
            );
    }

    @Test
    @DisplayName("특정 카테고리에 최고가 상품이 여러개 있으면 모든 상품의 브랜드명과 가격이 리턴된다.")
    void findHighestPriceByCategoryIdWhenMultipleHighestPriceProduct() {
        final long categoryId = 1L;
        final long price = 100000L;
        addProduct(1L, categoryId, price);
        addProduct(2L, categoryId, price);

        List<ProductInfo> result = productRepository.findHighestPriceByCategoryId(categoryId);

        assertThat(result).hasSize(2);
        assertThat(result).extracting("brandId", "price")
            .containsExactly(
                tuple(1L, price),
                tuple(2L, price)
            );
    }

    @Test
    @DisplayName("특정 카테고리에 최저가 상품과 동일한 브랜드의 상품이 존재하면 최저가 상품의 브랜드명과 가격이 리턴된다.")
    void findLowestPriceByCategoryIdWhenSameBrandProductExists() {
        final long categoryId = 1L;
        final long price = 3000L;
        addProduct(1L, categoryId, price);
        addProduct(1L, categoryId, price);

        List<ProductInfo> result = productRepository.findLowestPriceByCategoryId(categoryId);

        assertThat(result).hasSize(1);
        assertThat(result).extracting("brandId", "price")
            .containsExactly(tuple(1L, price));
    }
}
