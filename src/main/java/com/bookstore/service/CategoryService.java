package com.bookstore.service;

import com.bookstore.dto.request.CategoryRequestDto;
import com.bookstore.dto.response.BookResponseDto;
import com.bookstore.dto.response.CategoryResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CategoryService {

    List<CategoryResponseDto> findAll();

    CategoryResponseDto findById(Long id);

    Page<BookResponseDto> findBooksByCategoryId(Long categoryId, Pageable pageable);

    CategoryResponseDto save(CategoryRequestDto dto);

    CategoryResponseDto update(Long id, CategoryRequestDto dto);

    void delete(Long id);
}