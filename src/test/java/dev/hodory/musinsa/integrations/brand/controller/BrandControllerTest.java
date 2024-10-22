package dev.hodory.musinsa.integrations.brand.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import dev.hodory.musinsa.brand.domain.dto.BrandDTO;
import dev.hodory.musinsa.brand.domain.dto.BrandDTO.Request;
import dev.hodory.musinsa.integrations.IntegrationControllerTestBase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@DisplayName("BrandController의 Integration 테스트를 실행 합니다.")
class BrandControllerTest extends IntegrationControllerTestBase {

    @Nested
    @DisplayName("브랜드 등록 테스트")
    class AddBrand {

        @Test
        @DisplayName("브랜드를 등록하면 브랜드 ID와 이름이 반환된다.")
        void addBrand_success() throws Exception {
            Request request = Request.builder()
                .name("BrandName")
                .build();
            BrandDTO expected = BrandDTO.builder()
                .id(10L)
                .name("BrandName")
                .build();

            mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/brands")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(expected.getId()))
                .andExpect(jsonPath("$.data.name").value(expected.getName()));
        }

        @Test
        @DisplayName("브랜드 등록 요청시 데이터가 없으면 400 에러를 반환한다.")
        void addBrand_failure_whenNoDataIsSent() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/brands")
                    .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("브랜드 등록 요청시 name이 null이면 400 에러를 반환한다.")
        void addBrand_failure_whenBrandNameIsNull() throws Exception {
            Request request = Request.builder()
                .name(null)
                .build();

            mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/brands")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("브랜드 업데이트 테스트")
    class UpdateBrand {

        @Test
        @DisplayName("브랜드를 업데이트하면 브랜드 ID와 이름이 반환된다.")
        void updateBrand_success() throws Exception {
            Request request = Request.builder()
                .name("UpdatedBrandName")
                .build();
            BrandDTO expected = BrandDTO.builder()
                .id(1L)
                .name("UpdatedBrandName")
                .build();

            mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/brands/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(expected.getId()))
                .andExpect(jsonPath("$.data.name").value(expected.getName()));
        }

        @Test
        @DisplayName("존재하지 않는 브랜드 id로 브랜드 업데이트 요청시 404 에러를 반환한다.")
        void updateBrand_failure_whenBrandNotFound() throws Exception {
            Request request = Request.builder()
                .name("UpdatedBrandName")
                .build();

            mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/brands/999")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(
                    jsonPath("$.error.message").value("해당 브랜드를 찾을 수 없습니다."))
                .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("브랜드 삭제 테스트")
    class DeleteBrand {

        @Test
        @DisplayName("브랜드를 삭제하면 브랜드 ID와 이름이 반환된다.")
        void deleteBrand_success() throws Exception {
            Long id = 2L;
            BrandDTO expected = BrandDTO.builder()
                .id(id)
                .name("B")
                .build();

            mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/brands/" + id))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(expected.getId()))
                .andExpect(jsonPath("$.data.name").value(expected.getName()));
        }

        @Test
        @DisplayName("존재하지 않는 브랜드 id로 브랜드 삭제 요청시 404 에러를 반환한다.")
        void deleteBrand_failure_whenBrandNotFound() throws Exception {
            long id = 9999L;

            mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/brands/" + id))
                .andDo(print())
                .andExpect(
                    jsonPath("$.error.message").value("해당 브랜드를 찾을 수 없습니다."))
                .andExpect(status().isNotFound());
        }
    }
}
