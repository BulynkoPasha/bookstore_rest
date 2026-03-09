package com.bookstore.service;

import com.bookstore.dto.request.BookRequestDto;
import com.bookstore.dto.response.BookResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookService {

    Page<BookResponseDto> findAll(Pageable pageable);

    BookResponseDto findById(Long id);

    Page<BookResponseDto> search(String query, Pageable pageable);

    BookResponseDto save(BookRequestDto dto);

    BookResponseDto update(Long id, BookRequestDto dto);

    void delete(Long id);
}