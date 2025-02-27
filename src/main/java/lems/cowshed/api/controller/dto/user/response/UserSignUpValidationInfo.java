package lems.cowshed.api.controller.dto.user.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserSignUpValidationInfo {

    private boolean isExists;

    @Builder
    public UserSignUpValidationInfo(boolean isExists) {
        this.isExists = isExists;
    }

    public static UserSignUpValidationInfo from(boolean isExists){
        return UserSignUpValidationInfo.builder()
                .isExists(isExists)
                .build();
    }
}
