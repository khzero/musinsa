package dev.hodory.musinsa.utils;

import java.text.DecimalFormat;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PriceFormatter {

    private static final DecimalFormat formatter = new DecimalFormat("#,###");

    public static String format(Long price) {
        return formatter.format(price);
    }
}
