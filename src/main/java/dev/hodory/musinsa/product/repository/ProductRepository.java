package dev.hodory.musinsa.product.repository;

import dev.hodory.musinsa.product.domain.entity.Product;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long>, ProductRepositoryCustom {
    List<Product> findAllByBrandId(Long brandId);
}
