package lems.cowshed.domain.event;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Category {
    SPORTS, TRAVEL, READING, MUSIC, PICTURE, FOOD, FASHION, BEAUTY;

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
