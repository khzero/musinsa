package dev.hodory.musinsa.product.repository;

import static dev.hodory.musinsa.brand.domain.entity.QBrand.brand;
import static dev.hodory.musinsa.category.domain.entity.QCategory.category;
import static dev.hodory.musinsa.product.domain.entity.QProduct.product;

import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import dev.hodory.musinsa.product.domain.projection.LowestPriceProductDTO;
import dev.hodory.musinsa.product.domain.entity.QProduct;
import dev.hodory.musinsa.product.domain.projection.ProductInfo;
import dev.hodory.musinsa.product.domain.projection.QLowestPriceProductDTO;
import dev.hodory.musinsa.product.domain.projection.QProductInfo;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryCustomImpl implements ProductRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<LowestPriceProductDTO> findLowestPricePerCategory() {

        QProduct subProduct = new QProduct("subProduct");

        return queryFactory
            .select(new QLowestPriceProductDTO(
                category.title,
                brand.name,
                product.price
            ))
            .from(product)
            .join(product.brand, brand)
            .join(product.category, category)
            .where(product.id.in(
                JPAExpressions
                    .select(subProduct.id.max())
                    .from(subProduct)
                    .join(subProduct.category, category)
                    .groupBy(category.id, subProduct.price)
                    .having(subProduct.price.eq(
                        JPAExpressions
                            .select(subProduct.price.min())
                            .from(subProduct)
                            .where(subProduct.category.eq(category))
                    ))
            ))
            .orderBy(category.id.asc())
            .fetch();
    }

    @Override
    public List<ProductInfo> findLowestPriceGroupByBrandIdAndCategoryId() {
        return queryFactory
            .select(new QProductInfo(
                product.brand.id,
                product.brand.name,
                product.category.id,
                product.category.title,
                product.price.min()))
            .from(product)
            .groupBy(product.brand.id, product.category.id)
            .orderBy(
                product.brand.id.asc(),
                category.id.asc()
            )
            .fetch();
    }

    @Override
    public List<ProductInfo> findLowestPriceByCategoryId(Long categoryId) {
        return queryFactory
            .select(new QProductInfo(
                product.brand.id,
                product.brand.name,
                product.category.id,
                product.category.title,
                product.price))
            .from(product)
            .where(product.category.id.eq(categoryId))
            .groupBy(product.brand.id, product.price)
            .having(product.price.eq(
                JPAExpressions.select(product.price.min())
                    .from(product)
                    .where(product.category.id.eq(categoryId))
            ))
            .fetch();
    }

    @Override
    public List<ProductInfo> findHighestPriceByCategoryId(Long categoryId) {
        return queryFactory
            .select(new QProductInfo(
                product.brand.id,
                product.brand.name,
                product.category.id,
                product.category.title,
                product.price))
            .from(product)
            .where(product.category.id.eq(categoryId))
            .groupBy(product.brand.id, product.price)
            .having(product.price.eq(
                JPAExpressions.select(product.price.max())
                    .from(product)
                    .where(product.category.id.eq(categoryId))
            ))
            .fetch();
    }
}
