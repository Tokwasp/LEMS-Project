package lems.cowshed.service.comment;

import lems.cowshed.dto.comment.request.CommentModifyRequest;
import lems.cowshed.dto.comment.request.CommentSaveRequest;
import lems.cowshed.dto.comment.response.CommentsInfo;
import lems.cowshed.domain.post.comment.Comment;
import lems.cowshed.domain.post.Post;
import lems.cowshed.repository.post.comment.CommentRepository;
import lems.cowshed.repository.post.PostRepository;
import lems.cowshed.domain.user.User;
import lems.cowshed.repository.user.UserRepository;
import lems.cowshed.exception.Message;
import lems.cowshed.exception.NotFoundException;
import lems.cowshed.exception.Reason;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class CommentService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    public CommentsInfo getPostComments(Long postId, Long userId, LocalDateTime dateTime) {
        List<Comment> comments = commentRepository.findByPostId(postId);
        List<Long> userIds = getUserIdsFrom(comments);

        List<User> users = userRepository.findByIdIn(userIds);
        Map<Long, User> groupIdMap = groupIdMap(users);
        return CommentsInfo.Of(comments, groupIdMap, userId, dateTime);
    }

    @Transactional
    public Long save(CommentSaveRequest request, Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException(Reason.POST_ID, Message.POST_ID));

        Comment comment = request.toEntity(userId);
        comment.connectPost(post);

        commentRepository.save(comment);
        return comment.getId();
    }

    @Transactional
    public Long modify(CommentModifyRequest request, Long commentId, Long userId) {
        Comment comment = commentRepository.findByIdAndUserId(commentId, userId)
                .orElseThrow(() -> new NotFoundException(Reason.COMMENT_ID, Message.COMMENT_ID));

        comment.modify(request.getContext());
        return comment.getId();
    }

    @Transactional
    public Long delete(Long commentId, Long userId) {
        Comment comment = commentRepository.findByIdAndUserId(commentId, userId)
                .orElseThrow(() -> new NotFoundException(Reason.COMMENT_ID, Message.COMMENT_ID));

        commentRepository.delete(comment);
        return comment.getId();
    }

    private List<Long> getUserIdsFrom(List<Comment> comments) {
        return comments.stream()
                .map(Comment::getUserId)
                .toList();
    }

    private Map<Long, User> groupIdMap(List<User> users) {
        return users.stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));
    }
}
