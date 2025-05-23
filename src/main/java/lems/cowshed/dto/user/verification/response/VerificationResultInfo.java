package lems.cowshed.dto.user.verification.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class VerificationResultInfo {

    private boolean isVerificationPassed;

    @Builder
    private VerificationResultInfo(boolean isVerificationPassed) {
        this.isVerificationPassed = isVerificationPassed;
    }

    public static VerificationResultInfo of(boolean isVerificationPassed){
        return VerificationResultInfo.builder()
                .isVerificationPassed(isVerificationPassed)
                .build();
    }
}
