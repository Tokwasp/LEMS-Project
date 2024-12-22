package lems.cowshed.api.controller.dto.bookmark.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lems.cowshed.domain.bookmark.Bookmark;
import lems.cowshed.domain.user.User;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "북마크 폴더 등록 요청 데이터")
public class BookmarkSaveRequestDto {

    @NotBlank
    @Schema(description = "북마크 폴더 이름", example = "기본 폴더")
    String bookmarkFolderName;

    @Builder
    private BookmarkSaveRequestDto(String bookmarkFolderName) {
        this.bookmarkFolderName = bookmarkFolderName;
    }

    public static BookmarkSaveRequestDto of(String bookmarkFolderName){
        return BookmarkSaveRequestDto.builder()
                .bookmarkFolderName(bookmarkFolderName)
                .build();
    }

    public Bookmark toEntity(User user, BookmarkSaveRequestDto request) {
        return Bookmark.builder()
                .name(request.getBookmarkFolderName())
                .user(user)
                .build();
    }
}