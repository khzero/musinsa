package dev.hodory.musinsa.category.domain.entity;

import dev.hodory.musinsa.category.domain.enums.CategoryInfo;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "category")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Category {

    @Id
    private Long id;

    @Enumerated(EnumType.STRING)
    private CategoryInfo code;

    private String title;

    @Builder
    protected Category(Long id, CategoryInfo category) {
        this.id = id;
        this.code = category;
        this.title = category.getTitle();
    }
}
