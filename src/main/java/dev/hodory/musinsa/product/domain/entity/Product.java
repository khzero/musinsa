package dev.hodory.musinsa.product.domain.entity;

import dev.hodory.musinsa.brand.domain.entity.Brand;
import dev.hodory.musinsa.category.domain.entity.Category;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Entity(name = "product")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @Setter
    private Brand brand;

    @Setter
    @ManyToOne
    private Category category;

    @Setter
    private Long price;

    @Builder
    protected Product(Long id, Brand brand, Category category, Long price) {
        this.id = id;
        this.brand = brand;
        this.category = category;
        this.price = price;
    }
}
