package lems.cowshed.api.controller.dto.user.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserSignUpValidationDto {

    private boolean isExists;

    @Builder
    public UserSignUpValidationDto(boolean isExists) {
        this.isExists = isExists;
    }

    public static UserSignUpValidationDto from(boolean isExists){
        return UserSignUpValidationDto.builder()
                .isExists(isExists)
                .build();
    }
}
