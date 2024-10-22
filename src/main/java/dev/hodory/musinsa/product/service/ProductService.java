package dev.hodory.musinsa.product.service;

import dev.hodory.musinsa.product.domain.dto.LowestProductPerCategoryDTO;
import dev.hodory.musinsa.product.domain.dto.ProductLowestAndHighestDTO;
import dev.hodory.musinsa.product.domain.dto.ProductLowestAndHighestDTO.PriceInfo;
import dev.hodory.musinsa.product.domain.projection.LowestPriceProductDTO;
import dev.hodory.musinsa.brand.domain.dto.BrandProductDTO;
import dev.hodory.musinsa.brand.domain.entity.Brand;
import dev.hodory.musinsa.brand.repository.BrandRepository;
import dev.hodory.musinsa.product.domain.dto.ProductListDTO;
import dev.hodory.musinsa.category.domain.entity.Category;
import dev.hodory.musinsa.category.domain.enums.CategoryInfo;
import dev.hodory.musinsa.category.repository.CategoryRepository;
import dev.hodory.musinsa.product.domain.dto.ProductDTO;
import dev.hodory.musinsa.product.domain.entity.Product;
import dev.hodory.musinsa.product.domain.projection.ProductInfo;
import dev.hodory.musinsa.product.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;

    /**
     * 카테고리 조회
     *
     * @param dto 상품 정보
     * @return 카테고리
     */
    private Category getCategory(ProductDTO dto) {
        Category category = null;
        if (dto.getCategory() != null) {
            final CategoryInfo categoryInfo = CategoryInfo.findByCode(dto.getCategory());
            category = categoryRepository.findByCode(categoryInfo)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리입니다."));
        }
        return category;
    }

    /**
     * 브랜드 조회
     *
     * @param dto 상품 정보
     * @return 브랜드
     */
    private Brand getBrand(ProductDTO dto) {
        Brand brand = null;
        if (dto.getBrandId() != null) {
            brand = brandRepository.findById(dto.getBrandId())
                .orElseThrow(() -> new IllegalArgumentException("해당 브랜드가 존재하지 않습니다."));
        }
        return brand;
    }

    private Product getProduct(Long id) {
        return productRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 상품입니다."));
    }

    /**
     * 상품 등록
     *
     * @param dto 상품 정보
     * @return 등록된 상품
     */
    @Transactional(rollbackFor = RuntimeException.class)
    public ProductDTO.Response addProduct(ProductDTO dto) {
        Category category = getCategory(dto);
        final Brand brand = getBrand(dto);
        final Product entity = dto.toEntity(category, brand);
        productRepository.save(entity);
        return ProductDTO.Response.of(entity);
    }

    /**
     * 상품 수정
     *
     * @param id  상품 ID
     * @param dto 상품 정보
     * @return 수정된 상품
     */
    @Transactional(rollbackFor = RuntimeException.class)
    public ProductDTO.Response updateProduct(Long id, ProductDTO dto) {
        Category category = getCategory(dto);
        final Brand brand = getBrand(dto);

        final Product entity = getProduct(id);

        if (category != null && !entity.getCategory().equals(category)) {
            entity.setCategory(category);
        }

        if (dto.getPrice() != null && !entity.getPrice().equals(dto.getPrice())) {
            entity.setPrice(dto.getPrice());
        }

        if (brand != null && !entity.getBrand().getId().equals(brand.getId())) {
            entity.setBrand(brand);
        }

        final Product result = productRepository.save(entity);
        return ProductDTO.Response.of(result);
    }

    /**
     * 상품 삭제
     *
     * @param id 상품 ID
     * @return 삭제된 상품
     */
    @Transactional(rollbackFor = RuntimeException.class)
    public ProductDTO.Response deleteProduct(Long id) {
        final Product entity = getProduct(id);

        productRepository.deleteById(id);
        return ProductDTO.Response.of(entity);
    }

    /**
     * 카테고리별 최저가 상품 조회
     *
     * @return 카테고리별 최저가 상품
     */
    @Transactional(readOnly = true)
    public ProductListDTO getLowestPricePerCategory() {
        List<LowestPriceProductDTO> lowestPriceProducts = productRepository.findLowestPricePerCategory();

        if (lowestPriceProducts.isEmpty()) {
            throw new EntityNotFoundException("등록된 상품이 없습니다.");
        }

        if (lowestPriceProducts.size() != CategoryInfo.values().length) {
            throw new EntityNotFoundException("모든 카테고리의 상품이 등록되지 않았습니다.");
        }

        List<LowestProductPerCategoryDTO> products = lowestPriceProducts.stream()
            .map(LowestProductPerCategoryDTO::of)
            .toList();

        return ProductListDTO.of(products);
    }

    /**
     * 최저가 브랜드의 상품 정보 조회
     *
     * @return 최저가 브랜드
     */
    @Transactional(readOnly = true)
    public BrandProductDTO getLowestPriceBrand() {
        final List<ProductInfo> productInfoList = productRepository
            .findLowestPriceGroupByBrandIdAndCategoryId();

        if (productInfoList.isEmpty()) {
            throw new EntityNotFoundException("등록된 상품이 없습니다.");
        }

        final int totalCategories = CategoryInfo.values().length;

        final Map<Long, List<ProductInfo>> brandGroups = productInfoList.stream()
            .collect(Collectors.groupingBy(ProductInfo::getBrandId));

        return brandGroups.values().parallelStream()
            .filter(brandProductList -> brandProductList.size() == totalCategories)
            .map(BrandProductDTO::of)
            .min(Comparator.comparingLong(BrandProductDTO::getTotalPrice))
            .orElseThrow(() -> new EntityNotFoundException("모든 카테고리의 상품을 가진 브랜드가 없습니다."));
    }

    /**
     * 카테고리 이름으로 최저, 최고 가격 브랜드와 상품 가격을 조회
     *
     * @param categoryName 카테고리 이름
     * @return 최저, 최고 가격 브랜드와 상품 가격
     */
    @Transactional(readOnly = true)
    public ProductLowestAndHighestDTO getLowestAndHighestPriceBrandByCategoryName(
        String categoryName) {
        Category category = categoryRepository.findByTitle(categoryName)
            .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 카테고리입니다."));

        List<ProductInfo> lowestPrices = productRepository.findLowestPriceByCategoryId(
            category.getId());
        if (lowestPrices.isEmpty()) {
            throw new EntityNotFoundException("해당 카테고리에 등록된 상품이 없습니다.");
        }

        List<ProductInfo> highestPrices = productRepository.findHighestPriceByCategoryId(
            category.getId());
        if (highestPrices.isEmpty()) {
            throw new EntityNotFoundException("해당 카테고리에 등록된 상품이 없습니다.");
        }

        return ProductLowestAndHighestDTO.builder()
            .category(categoryName)
            .lowest(convertToPriceInfoList(lowestPrices))
            .highest(convertToPriceInfoList(highestPrices))
            .build();
    }

    /**
     * ProductInfo 리스트를 PriceInfo 리스트로 변환
     *
     * @param productInfos ProductInfo 리스트
     * @return PriceInfo 리스트
     */
    private List<PriceInfo> convertToPriceInfoList(List<ProductInfo> productInfos) {
        return productInfos.stream()
            .map(PriceInfo::of)
            .toList();
    }
}
