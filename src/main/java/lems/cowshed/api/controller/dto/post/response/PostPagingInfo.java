package lems.cowshed.api.controller.dto.post.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class PostPagingInfo {

    private List<PostInfo> postInfos;
    private boolean hasNext;

    @Builder
    private PostPagingInfo(List<PostInfo> postInfos, boolean hasNext) {
        this.postInfos = postInfos;
        this.hasNext = hasNext;
    }

    public static PostPagingInfo of(List<PostInfo> postInfos, boolean hasNext){
        return PostPagingInfo.builder()
                .postInfos(postInfos)
                .hasNext(hasNext)
                .build();
    }
}
