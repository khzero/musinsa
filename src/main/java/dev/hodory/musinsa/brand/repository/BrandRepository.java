package dev.hodory.musinsa.brand.repository;

import dev.hodory.musinsa.brand.domain.entity.Brand;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BrandRepository extends JpaRepository<Brand, Long> {

    Optional<Brand> findByName(String name);
}
