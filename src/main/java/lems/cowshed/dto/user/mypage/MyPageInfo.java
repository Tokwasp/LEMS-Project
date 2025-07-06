package lems.cowshed.dto.user.mypage;

import lombok.Builder;
import lombok.Getter;

@Getter
public class MyPageInfo {

    private String username;
    private long participatedEventCount;
    private long bookmarkEventCount;

    @Builder
    private MyPageInfo(String username, long participatedEventCount, long bookmarkEventCount) {
        this.username = username;
        this.participatedEventCount = participatedEventCount;
        this.bookmarkEventCount = bookmarkEventCount;
    }

    public static MyPageInfo of(String username, long participatedEventCount, long bookmarkEventCount) {
        return MyPageInfo.builder()
                .username(username)
                .participatedEventCount(participatedEventCount)
                .bookmarkEventCount(bookmarkEventCount)
                .build();
    }
}
