package lems.cowshed.service;

import lems.cowshed.api.controller.dto.bookmark.request.BookmarkEditRequestDto;
import lems.cowshed.api.controller.dto.bookmark.request.BookmarkSaveRequestDto;
import lems.cowshed.api.controller.dto.bookmark.response.BookmarkResponseDto;
import lems.cowshed.domain.bookmark.Bookmark;
import lems.cowshed.domain.bookmark.BookmarkRepository;
import lems.cowshed.domain.event.EventJpaRepository;
import lems.cowshed.domain.user.User;
import lems.cowshed.domain.user.UserRepository;
import lems.cowshed.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static lems.cowshed.exception.Message.*;
import static lems.cowshed.exception.Reason.*;

@RequiredArgsConstructor
@Transactional
@Service
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final EventJpaRepository eventRepository;
    private final UserRepository userRepository;

    public void createBookmark(Long userId, BookmarkSaveRequestDto request) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException(USER_ID, USER_NOT_FOUND)
        );
        bookmarkRepository.save(request.toEntity(user, request));
    }

    public BookmarkResponseDto getAllBookmarks(Long userId) {
        List<Bookmark> bookmarks = bookmarkRepository.findByUserId(userId);
//        return BookmarkResponseDto.of(toBookmarkNameList(bookmarks));
        return null;
    }

    public void editBookmarkName(BookmarkEditRequestDto request, Long bookmarkId) {
        Bookmark bookmark = bookmarkRepository.findById(bookmarkId).orElseThrow(
                () -> new NotFoundException(BOOKMARK_ID, BOOKMARK_NOT_FOUND)
        );
        bookmark.editName(request.getNewBookmarkFolderName());
    }

    //TODO
    public void deleteBookmark(Long bookmarkId) {
        // boomarkEventRepository.deleteByBoomarkId();
        bookmarkRepository.deleteById(bookmarkId);
    }

    public void saveBookmarkEvent(long eventId) {
//        Bookmark bookmark = bookmarkRepository.findById(bookmarkId).orElseThrow(
//                () -> new NotFoundException(BOOKMARK_ID, BOOKMARK_NOT_FOUND)
//        );
//        Event event = eventRepository.findById(eventId).orElseThrow(
//                () -> new NotFoundException(EVENT_ID, EVENT_NOT_FOUND)
//        );
//        BookmarkEvent.create(event, bookmark);
//        bookmarkRepository.save(bookmark);
    }

    private List<String> toBookmarkNameList(List<Bookmark> bookmarks) {
        return bookmarks.stream()
                .map(Bookmark::getName)
                .collect(Collectors.toList());
    }
}
