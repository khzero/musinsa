package dev.hodory.musinsa.integrations.product.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import dev.hodory.musinsa.category.domain.enums.CategoryInfo;
import dev.hodory.musinsa.integrations.IntegrationControllerTestBase;
import dev.hodory.musinsa.product.domain.dto.ProductDTO;
import dev.hodory.musinsa.product.domain.dto.ProductDTO.CreateRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@DisplayName("ProductController의 Integration 테스트를 실행 합니다.")
class ProductControllerTest extends IntegrationControllerTestBase {

    @Nested
    @DisplayName("상품 등록에 대한 테스트를 실행 합니다.")
    class ProductCreateTest {

        @Test
        @DisplayName("정상적인 요청을 하면 응답코드 200을 응답합니다.")
        void addProduct_success() throws Exception {
            CreateRequest createRequest = CreateRequest.builder()
                    .category(CategoryInfo.ACCESSORY.name())
                    .brandId(1L)
                    .price(1000L)
                    .build();

            mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/products")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createRequest)))
                    .andDo(print())
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("빈 값으로 상품 등록 요청시 400 에러를 반환 합니다.")
        void addProduct_failure_whenNoDataIsSent() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/products")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("category를 null로 상품 등록 요청시 400 에러를 반환 합니다.")
        void addProduct_failure_whenCategoryIsNull() throws Exception {
            CreateRequest createRequest = CreateRequest.builder()
                    .category(null)
                    .brandId(1L)
                    .price(1000L)
                    .build();

            mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/products")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createRequest)))
                    .andDo(print())
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("brandId를 null로 상품 등록 요청시 400 에러를 반환 합니다.")
        void addProduct_failure_whenBrandIdIsNull() throws Exception {
            CreateRequest createRequest = CreateRequest.builder()
                    .category(CategoryInfo.ACCESSORY.name())
                    .brandId(null)
                    .price(1000L)
                    .build();

            mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/products")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createRequest)))
                    .andDo(print())
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("price를 null로 상품 등록 요청시 400 에러를 반환 합니다.")
        void addProduct_failure_whenPriceIsNull() throws Exception {
            CreateRequest createRequest = CreateRequest.builder()
                    .category(CategoryInfo.ACCESSORY.name())
                    .brandId(1L)
                    .price(null)
                    .build();

            mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/products")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createRequest)))
                    .andDo(print())
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("category가 존재하지 않는 값으로 상품 등록 요청시 400 에러를 반환하는지 테스트")
        void addProduct_failure_whenCategoryIsInvalid() throws Exception {
            CreateRequest createRequest = CreateRequest.builder()
                    .category("자켓")
                    .brandId(1L)
                    .price(1000L)
                    .build();

            mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/products")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createRequest)))
                    .andDo(print())
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("brandId가 존재하지 않는 값으로 상품 등록 요청시 400 에러를 반환 합니다.")
        void addProduct_failure_whenBrandIdIsInvalid() throws Exception {
            CreateRequest createRequest = CreateRequest.builder()
                    .category(CategoryInfo.ACCESSORY.name())
                    .brandId(9897L)
                    .price(1000L)
                    .build();

            mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/products")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createRequest)))
                    .andDo(print())
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("price가 음수인 값으로 상품 등록 요청시 400 에러를 반환 합니다.")
        void addProduct_failure_whenPriceIsNegative() throws Exception {
            CreateRequest createRequest = CreateRequest.builder()
                    .category(CategoryInfo.ACCESSORY.name())
                    .brandId(1L)
                    .price(-1000L)
                    .build();

            mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/products")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createRequest)))
                    .andDo(print())
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("상품 수정에 대한 테스트를 실행 합니다.")
    class ProductUpdateTest {

        @Test
        @DisplayName("상품이 성공적으로 업데이트 되는 경우")
        void updateProduct_success() throws Exception {
            Long id = 1L;
            ProductDTO.UpdateRequest request = ProductDTO.UpdateRequest.builder()
                    .category(CategoryInfo.HAT.name())
                    .brandId(1L)
                    .price(1000L)
                    .build();

            mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/products/" + id)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("상품이 존재하지 않아 업데이트에 실패하는 경우")
        void updateProduct_failure_whenProductNotFound() throws Exception {
            Long id = 999L;
            ProductDTO.UpdateRequest request = ProductDTO.UpdateRequest.builder()
                    .category(CategoryInfo.ACCESSORY.name())
                    .brandId(1L)
                    .price(1000L)
                    .build();

            mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/products/" + id)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("category가 존재하지 않는 값으로 상품 업데이트 요청시 400 에러를 반환하는지 테스트")
        void updateProduct_failure_whenCategoryIsInvalid() throws Exception {
            Long id = 1L;
            ProductDTO.UpdateRequest request = ProductDTO.UpdateRequest.builder()
                    .category("자켓")
                    .brandId(1L)
                    .price(1000L)
                    .build();

            mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/products/" + id)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("brandId가 존재하지 않는 값으로 상품 업데이트 요청시 400 에러를 반환하는지 테스트")
        void updateProduct_failure_whenBrandIdIsInvalid() throws Exception {
            Long id = 1L;
            ProductDTO.UpdateRequest request = ProductDTO.UpdateRequest.builder()
                    .category(CategoryInfo.ACCESSORY.name())
                    .brandId(9897L)
                    .price(1000L)
                    .build();

            mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/products/" + id)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("price가 음수인 값으로 상품 업데이트 요청시 400 에러를 반환하는지 테스트")
        void updateProduct_failure_whenPriceIsNegative() throws Exception {
            Long id = 1L;
            ProductDTO.UpdateRequest request = ProductDTO.UpdateRequest.builder()
                    .category(CategoryInfo.ACCESSORY.name())
                    .brandId(1L)
                    .price(-1000L)
                    .build();

            mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/products/" + id)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("빈 값으로 상품 업데이트 요청시 400 에러를 반환하는지 테스트")
        void updateProduct_failure_whenNoDataIsSent() throws Exception {
            Long id = 1L;

            mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/products/" + id)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("category를 null로 상품 업데이트 요청시 카테고리를 제외하고 업데이트 되는지 확인")
        void updateProduct_failure_whenCategoryIsNull() throws Exception {
            Long id = 2L;
            ProductDTO.UpdateRequest request = ProductDTO.UpdateRequest.builder()
                    .category(null)
                    .brandId(1L)
                    .price(1000L)
                    .build();

            mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/products/" + id)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpectAll(
                            status().isOk(),
                            jsonPath("$.data.id").value(id),
                            jsonPath("$.data.category_name").value(CategoryInfo.OUTER.getTitle()),
                            jsonPath("$.data.brand.id").value(1),
                            jsonPath("$.data.price").value(request.getPrice()));
        }

        @Test
        @DisplayName("brand_id를 null로 상품 업데이트 요청시 brand_id를 제외하고 업데이트 되는지 확인")
        void updateProduct_failure_whenBrandIdIsNull() throws Exception {
            Long id = 3L;
            ProductDTO.UpdateRequest request = ProductDTO.UpdateRequest.builder()
                    .category(CategoryInfo.ACCESSORY.name())
                    .brandId(null)
                    .price(1000L)
                    .build();

            mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/products/" + id)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpectAll(
                            status().isOk(),
                            jsonPath("$.data.id").value(id),
                            jsonPath("$.data.category_name").value(CategoryInfo.ACCESSORY.getTitle()),
                            jsonPath("$.data.brand.id").value(1),
                            jsonPath("$.data.price").value(request.getPrice()));
        }

        @Test
        @DisplayName("price를 null로 상품 업데이트 요청시 상품 가격이 변경되지 않는지 테스트")
        void updateProduct_failure_whenPriceIsNull() throws Exception {
            Long id = 4L;
            ProductDTO.UpdateRequest request = ProductDTO.UpdateRequest.builder()
                    .category(CategoryInfo.ACCESSORY.name())
                    .brandId(1L)
                    .price(null)
                    .build();

            mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/products/" + id)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpectAll(
                            jsonPath("$.data.id").value(id),
                            jsonPath("$.data.category_name").value(CategoryInfo.ACCESSORY.getTitle()),
                            jsonPath("$.data.price").value(9000L));
        }

        @Test
        @DisplayName("상품 업데이트 요청시 카테고리가 변경되었을 경우 성공")
        void updateProduct_success_whenCategoryIsChanged() throws Exception {
            Long id = 5L;
            ProductDTO.UpdateRequest request = ProductDTO.UpdateRequest.builder()
                    .category(CategoryInfo.HAT.name())
                    .brandId(1L)
                    .price(1000L)
                    .build();

            mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/products/" + id)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpectAll(
                            status().isOk(),
                            jsonPath("$.data.id").value(id),
                            jsonPath("$.data.category_name").value(CategoryInfo.HAT.getTitle()),
                            jsonPath("$.data.price").value(request.getPrice()));
        }

    }

    @Nested
    @DisplayName("상품 삭제에 대한 테스트를 실행 합니다.")
    class ProductDeleteTest {

        @Test
        @DisplayName("상품 삭제 요청시 성공")
        void deleteProduct_success() throws Exception {
            Long id = 1L;

            mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/products/" + id))
                    .andDo(print())
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("상품 삭제 요청시 상품이 존재하지 않을 경우 실패")
        void deleteProduct_failure_whenProductNotFound() throws Exception {
            Long id = 999L;

            mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/products/" + id))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("단일 브랜드로 모든 카테고리 상품을 구매할 때 최저가격에 판매하는 브랜드와 카테고리의 상품가격, 총액을 조회하는 API를 테스트 합니다.")
    class GetLowestPriceBrandTest {

        @Test
        @DisplayName("최저가 브랜드를 조회하는 테스트")
        void getLowestPriceBrand_success() throws Exception {
            // given
            String expectedBrandName = "D";
            int expectedCategoryProductsSize = 8;
            String expectedTotalPrice = "36,100";
            String[][] expectedCategoryPrices = {
                    { "상의", "10,100" }, { "아우터", "5,100" }, { "바지", "3,000" }, { "스니커즈", "9,500" },
                    { "가방", "2,500" }, { "모자", "1,500" }, { "양말", "2,400" }, { "액세서리", "2,000" }
            };

            // when
            var result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/products/lowest-brand"))
                    .andDo(print());

            // then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.brand_name").value(expectedBrandName))
                    .andExpect(
                            jsonPath("$.data.category_products", hasSize(expectedCategoryProductsSize)))
                    .andExpect(jsonPath("$.data.total_price").value(expectedTotalPrice));

            for (int index = 0; index < expectedCategoryPrices.length; index++) {
                String category = expectedCategoryPrices[index][0];
                String price = expectedCategoryPrices[index][1];
                result.andExpect(
                        jsonPath("$.data.category_products[" + index + "].category").value(category))
                        .andExpect(
                                jsonPath("$.data.category_products[" + index + "].price").value(price));
            }
        }
    }
}