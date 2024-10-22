package dev.hodory.musinsa.category.repository;

import dev.hodory.musinsa.category.domain.entity.Category;
import dev.hodory.musinsa.category.domain.enums.CategoryInfo;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findByCode(CategoryInfo categoryInfo);

    Optional<Category> findByTitle(String title);
}
