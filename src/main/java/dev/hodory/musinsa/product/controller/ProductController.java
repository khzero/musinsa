package dev.hodory.musinsa.product.controller;

import dev.hodory.musinsa.brand.domain.dto.BrandProductDTO;
import dev.hodory.musinsa.product.domain.dto.ProductLowestAndHighestDTO;
import dev.hodory.musinsa.product.domain.dto.ProductListDTO;
import dev.hodory.musinsa.common.dto.ResponseDTO;
import dev.hodory.musinsa.product.domain.dto.ProductDTO;
import dev.hodory.musinsa.product.service.ProductService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService service;

    @PostMapping
    public ResponseEntity<ResponseDTO> addProduct(@RequestBody @Valid ProductDTO.CreateRequest createRequest) {
        final ProductDTO dto = ProductDTO.of(createRequest);
        final ProductDTO.Response response = service.addProduct(dto);

        return ResponseEntity.ok(ResponseDTO.of(response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseDTO> updateProduct(@PathVariable("id") Long id,
        @RequestBody @Valid ProductDTO.UpdateRequest request) {
        final ProductDTO dto = ProductDTO.of(request);
        final ProductDTO.Response response = service.updateProduct(id, dto);

        return ResponseEntity.ok(ResponseDTO.of(response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDTO> deleteProduct(@PathVariable("id") Long id) {
        final ProductDTO.Response response = service.deleteProduct(id);

        return ResponseEntity.ok(ResponseDTO.of(response));
    }

    @GetMapping(value = "/lowest-price")
    public ResponseEntity<ProductListDTO> getLowestPricePerCategory() {
        final ProductListDTO list = service.getLowestPricePerCategory();
        return ResponseEntity.ok(list);
    }

    @GetMapping(value = "/lowest-brand")
    public ResponseEntity<BrandProductDTO.Response> getLowestProductBrand() {
        final BrandProductDTO list = service.getLowestPriceBrand();
        final BrandProductDTO.Response result = BrandProductDTO.Response.of(list);
        return ResponseEntity.ok(result);
    }

    @GetMapping(value = "/category")
    public ResponseEntity<ProductLowestAndHighestDTO> getLowestAndHighestProduct(@RequestParam("title") String title) {
        final ProductLowestAndHighestDTO list = service.getLowestAndHighestPriceBrandByCategoryName(title);

        return ResponseEntity.ok(list);
    }
}
