package lems.cowshed.api.controller.dto.user.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DuplicateCheckResult {

    private boolean isDuplicated;

    @Builder
    public DuplicateCheckResult(boolean isDuplicated) {
        this.isDuplicated = isDuplicated;
    }

    public static DuplicateCheckResult of(boolean isDuplicated){
        return DuplicateCheckResult.builder()
                .isDuplicated(isDuplicated)
                .build();
    }
}
