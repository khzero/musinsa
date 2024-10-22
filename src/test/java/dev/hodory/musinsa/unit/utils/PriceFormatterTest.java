package dev.hodory.musinsa.unit.utils;


import static org.assertj.core.api.Assertions.assertThat;

import dev.hodory.musinsa.utils.PriceFormatter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PriceFormatterTest {

    @Test
    @DisplayName("Long 타입의 가격을 받아 Number Formatting 시키는지 확인합니다.")
    void format() {
        assertThat(PriceFormatter.format(2000L)).isEqualTo("2,000");
        assertThat(PriceFormatter.format(1234567890L)).isEqualTo("1,234,567,890");
    }
}