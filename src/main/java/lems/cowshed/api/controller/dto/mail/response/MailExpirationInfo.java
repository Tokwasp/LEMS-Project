package lems.cowshed.api.controller.dto.mail.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class MailExpirationInfo {

    private boolean isExpired;

    @Builder
    private MailExpirationInfo(boolean isExpired) {
        this.isExpired = isExpired;
    }

    public static MailExpirationInfo of(boolean isExpired){
        return MailExpirationInfo.builder()
                .isExpired(isExpired)
                .build();
    }
}
