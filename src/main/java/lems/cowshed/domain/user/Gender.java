package lems.cowshed.domain.user;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Gender {
    FEMALE, MALE;

    @JsonCreator
    public static Gender getEnumFromValue(String value){
        for (Gender gender : Gender.values()) {
            if(gender.name().equals(value)){
                return gender;
            }
        }
        return null;
    }
}
