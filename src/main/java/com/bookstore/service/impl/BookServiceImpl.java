package com.bookstore.service.impl;

import com.bookstore.dto.request.BookRequestDto;
import com.bookstore.dto.response.BookResponseDto;
import com.bookstore.exception.EntityNotFoundException;
import com.bookstore.mapper.BookMapper;
import com.bookstore.entity.Book;
import com.bookstore.entity.Category;
import com.bookstore.repository.BookRepository;
import com.bookstore.repository.CategoryRepository;
import com.bookstore.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;
    private final BookMapper bookMapper;

    @Override
    public Page<BookResponseDto> findAll(Pageable pageable) {
        return bookRepository.findAll(pageable).map(bookMapper::toDto);
    }

    @Override
    public BookResponseDto findById(Long id) {
        return bookMapper.toDto(getBookOrThrow(id));
    }

    @Override
    public Page<BookResponseDto> search(String query, Pageable pageable) {
        return bookRepository.search(query, pageable).map(bookMapper::toDto);
    }

    @Override
    @Transactional
    public BookResponseDto save(BookRequestDto dto) {
        Book book = bookMapper.toEntity(dto);
        book.setCategories(resolveCategories(dto.categoryIds()));
        return bookMapper.toDto(bookRepository.save(book));
    }

    @Override
    @Transactional
    public BookResponseDto update(Long id, BookRequestDto dto) {
        Book book = getBookOrThrow(id);
        bookMapper.updateEntityFromDto(dto, book);
        if (dto.categoryIds() != null) {
            book.setCategories(resolveCategories(dto.categoryIds()));
        }
        return bookMapper.toDto(bookRepository.save(book));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        getBookOrThrow(id);
        bookRepository.deleteById(id);  // Soft delete через @SQLDelete
    }

    // Вспомогательный метод — получить книгу или выбросить 404
    private Book getBookOrThrow(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Book not found with id: " + id));
    }

    // Загрузить категории по списку ID из запроса
    private Set<Category> resolveCategories(Set<Long> ids) {
        if (ids == null || ids.isEmpty()) return new HashSet<>();
        Set<Category> categories = new HashSet<>(categoryRepository.findAllById(ids));
        if (categories.size() != ids.size()) {
            throw new EntityNotFoundException("One or more categories not found");
        }
        return categories;
    }
}