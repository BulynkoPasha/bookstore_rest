package com.bookstore.service.impl;

import com.bookstore.dto.request.CategoryRequestDto;
import com.bookstore.dto.response.BookResponseDto;
import com.bookstore.dto.response.CategoryResponseDto;
import com.bookstore.exception.EntityNotFoundException;
import com.bookstore.mapper.BookMapper;
import com.bookstore.mapper.CategoryMapper;
import com.bookstore.entity.Category;
import com.bookstore.repository.BookRepository;
import com.bookstore.repository.CategoryRepository;
import com.bookstore.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final BookRepository bookRepository;
    private final CategoryMapper categoryMapper;
    private final BookMapper bookMapper;

    @Override
    public List<CategoryResponseDto> findAll() {
        return categoryRepository.findAll().stream()
                .map(categoryMapper::toDto)
                .toList();
    }

    @Override
    public CategoryResponseDto findById(Long id) {
        return categoryMapper.toDto(getCategoryOrThrow(id));
    }

    @Override
    public Page<BookResponseDto> findBooksByCategoryId(Long categoryId, Pageable pageable) {
        getCategoryOrThrow(categoryId);
        return bookRepository.findByCategoryId(categoryId, pageable).map(bookMapper::toDto);
    }

    @Override
    @Transactional
    public CategoryResponseDto save(CategoryRequestDto dto) {
        Category category = categoryMapper.toEntity(dto);
        return categoryMapper.toDto(categoryRepository.save(category));
    }

    @Override
    @Transactional
    public CategoryResponseDto update(Long id, CategoryRequestDto dto) {
        Category category = getCategoryOrThrow(id);
        categoryMapper.updateEntityFromDto(dto, category);
        return categoryMapper.toDto(categoryRepository.save(category));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        getCategoryOrThrow(id);
        categoryRepository.deleteById(id);
    }

    private Category getCategoryOrThrow(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Category not found with id: " + id));
    }
}