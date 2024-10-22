package dev.hodory.musinsa.brand.service;

import dev.hodory.musinsa.brand.domain.dto.BrandDTO;
import dev.hodory.musinsa.brand.domain.dto.BrandDTO.Request;
import dev.hodory.musinsa.brand.domain.entity.Brand;
import dev.hodory.musinsa.brand.repository.BrandRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class BrandService {

    private final BrandRepository brandRepository;

    @Transactional(rollbackFor = RuntimeException.class)
    public BrandDTO addBrand(Request request) {
        if (request.getName() == null || request.getName().isBlank()) {
            throw new IllegalArgumentException("브랜드명은 비어있지 않아야 합니다.");
        }

        if (brandRepository.findByName(request.getName()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 브랜드명입니다.");
        }

        return BrandDTO.of(brandRepository.save(request.toEntity()));
    }

    @Transactional(rollbackFor = RuntimeException.class)
    public BrandDTO updateBrand(Long brandId, BrandDTO.Request data) {
        if (data.getName() == null || data.getName().isBlank()) {
            throw new IllegalArgumentException("브랜드명은 비어있지 않아야 합니다.");
        }

        final Brand brand = getBrand(brandId);

        if (brandRepository.findByName(data.getName()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 브랜드명입니다.");
        }

        brand.updateName(data.getName());
        return BrandDTO.of(brandRepository.save(brand));
    }

    @Transactional(rollbackFor = RuntimeException.class)
    public BrandDTO deleteById(Long brandId) {
        final Brand brand = getBrand(brandId);

        brandRepository.deleteById(brandId);
        return BrandDTO.of(brand);
    }

    private Brand getBrand(Long brandId) {
        return brandRepository.findById(brandId).orElseThrow(
                () -> new EntityNotFoundException("해당 브랜드를 찾을 수 없습니다."));
    }
}
