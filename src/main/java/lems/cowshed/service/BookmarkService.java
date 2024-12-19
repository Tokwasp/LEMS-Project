package lems.cowshed.service;

import lems.cowshed.api.controller.dto.bookmark.request.BookmarkEditRequestDto;
import lems.cowshed.api.controller.dto.bookmark.request.BookmarkSaveRequestDto;
import lems.cowshed.api.controller.dto.bookmark.response.BookmarkResponseDto;
import lems.cowshed.domain.bookmark.Bookmark;
import lems.cowshed.domain.bookmark.BookmarkRepository;
import lems.cowshed.domain.user.User;
import lems.cowshed.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Transactional
@Service
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final UserRepository userRepository;

    public void createBookmark(Long userId, BookmarkSaveRequestDto request) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new IllegalArgumentException("유저를 찾지 못했습니다.")
        );
        bookmarkRepository.save(request.toEntity(user, request));
    }

    public BookmarkResponseDto getAllBookmarks(Long userId) {
        List<Bookmark> bookmarks = bookmarkRepository.findByUserId(userId);
        return BookmarkResponseDto.of(toBookmarkNameList(bookmarks));
    }

    public void editBookmarkName(BookmarkEditRequestDto request, Long bookmarkId) {
        Bookmark bookmark = bookmarkRepository.findById(bookmarkId).orElseThrow(
                () -> new IllegalArgumentException("북마크를 찾지 못했습니다.")
        );
        bookmark.editName(request.getNewBookmarkFolderName());
    }

    //TODO
    public void deleteBookmark(Long bookmarkId) {
        // boomarkEventRepository.deleteByBoomarkId();
        bookmarkRepository.deleteById(bookmarkId);
    }

    private List<String> toBookmarkNameList(List<Bookmark> bookmarks) {
        return bookmarks.stream()
                .map(Bookmark::getName)
                .collect(Collectors.toList());
    }
}
