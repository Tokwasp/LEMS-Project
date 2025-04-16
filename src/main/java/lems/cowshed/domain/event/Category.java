package lems.cowshed.domain.event;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Category {
    SPORTS("운동"), TRAVEL("여행"), READING("독서"), RESTAURANT("맛집"),
    PERFORMANCE("공연"), PICTURE("사진"), SELF_DEVELOPMENT("자기계발"), HOBBY("취미"),
    PET("반려동물"),GAME("게임"),SERVICE("봉사"),REMAIN("기타");

    private final String description;

    Category(String description) {
        this.description = description;
    }

    @JsonCreator
    public static Category getEnumFromValue(String value){
        for (Category category : Category.values()) {
            if(category.name().equals(value)){
                return category;
            }
        }
        return null;
    }
}
