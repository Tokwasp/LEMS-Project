package lems.cowshed.dto.comment.response;

import lems.cowshed.domain.post.comment.Comment;
import lems.cowshed.domain.user.User;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

@Getter
public class CommentsInfo {

    private List<CommentInfo> comments;

    public CommentsInfo(List<CommentInfo> comments) {
        this.comments = comments;
    }

    public static CommentsInfo Of(List<Comment> comments, Map<Long, User> groupIdMap,
                                  Long userId, LocalDateTime currentDateTime) {
        List<CommentInfo> commentInfoList = comments.stream()
                .map(comment ->
                        CommentInfo.of(
                                findUserName(groupIdMap, comment),
                                createDaysAgo(currentDateTime, comment),
                                comment.getContext(),
                                comment.isMyUserId(userId)
                        )
                ).toList();

        return new CommentsInfo(commentInfoList);
    }

    private static String findUserName(Map<Long, User> groupIdMap, Comment comment) {
        return groupIdMap.get(comment.getUserId()).getUsername();
    }

    private static String createDaysAgo(LocalDateTime currentDateTime, Comment comment) {
        long between = ChronoUnit.DAYS.between(currentDateTime, comment.getCreatedDateTime());
        if(between == 0){
            return "오늘";
        }
        return between + "일 전";
    }

}
