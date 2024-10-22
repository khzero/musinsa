package dev.hodory.musinsa.brand.controller;

import dev.hodory.musinsa.brand.domain.dto.BrandDTO;
import dev.hodory.musinsa.brand.domain.dto.BrandProductDTO;
import dev.hodory.musinsa.brand.service.BrandService;
import dev.hodory.musinsa.common.dto.ResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/brands")
@RequiredArgsConstructor
public class BrandController {

    private final BrandService brandService;

    @PostMapping
    public ResponseEntity<ResponseDTO> addBrand(
        @RequestBody @Valid BrandDTO.Request request) {
        return ResponseEntity.ok(ResponseDTO.of(brandService.addBrand(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseDTO> updateBrand(@PathVariable Long id,
        @RequestBody @Valid BrandDTO.Request request) {
        return ResponseEntity.ok(ResponseDTO.of(brandService.updateBrand(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDTO> deleteBrand(@PathVariable Long id) {
        return ResponseEntity.ok(ResponseDTO.of(brandService.deleteById(id)));
    }
}
