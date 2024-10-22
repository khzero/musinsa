package dev.hodory.musinsa.category.domain.enums;

import lombok.Getter;

public enum CategoryInfo {
    TOP("상의"), OUTER("아우터"), PANTS("바지"), SNEAKERS("스니커즈"), BAG("가방"), HAT("모자"), SOCKS(
        "양말"), ACCESSORY("액세서리");

    @Getter
    private final String title;

    CategoryInfo(String title) {
        this.title = title;
    }

    public static CategoryInfo findByCode(String code) {
        for (CategoryInfo categories : CategoryInfo.values()) {
            if (categories.name().equals(code)) {
                return categories;
            }
        }
        throw new IllegalArgumentException("존재하지 않는 카테고리입니다.");
    }

    public static CategoryInfo findByTitle(String title) {
        for (CategoryInfo categories : CategoryInfo.values()) {
            if (categories.getTitle().equals(title)) {
                return categories;
            }
        }
        throw new IllegalArgumentException("존재하지 않는 카테고리입니다.");
    }
}
