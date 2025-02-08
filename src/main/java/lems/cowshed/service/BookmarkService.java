package lems.cowshed.service;

import lems.cowshed.api.controller.dto.bookmark.response.BookmarkResponseDto;
import lems.cowshed.domain.bookmark.Bookmark;
import lems.cowshed.domain.bookmark.BookmarkRepository;
import lems.cowshed.domain.event.Event;
import lems.cowshed.domain.event.EventJpaRepository;
import lems.cowshed.domain.user.User;
import lems.cowshed.domain.user.UserRepository;
import lems.cowshed.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static lems.cowshed.exception.Message.*;
import static lems.cowshed.exception.Reason.*;

@RequiredArgsConstructor
@Transactional
@Service
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final EventJpaRepository eventRepository;
    private final UserRepository userRepository;

    public Long saveBookmark(long eventId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(USER_ID, USER_NOT_FOUND));

        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException(EVENT_ID, EVENT_NOT_FOUND)
        );

        return bookmarkRepository.save(Bookmark.create(event, user)).getId();
    }

    public void deleteBookmark(Long bookmarkId) {
        bookmarkRepository.deleteById(bookmarkId);
    }

    public BookmarkResponseDto getAllBookmarks(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(USER_ID, USER_NOT_FOUND));

        List<Bookmark> bookmarks = bookmarkRepository.findByUserId(userId);
        return BookmarkResponseDto.from(bookmarks);
    }

}