package com.bookstore.service;

import com.bookstore.dto.request.BookRequestDto;
import com.bookstore.dto.response.BookResponseDto;
import com.bookstore.dto.response.CategoryResponseDto;
import com.bookstore.exception.EntityNotFoundException;
import com.bookstore.mapper.BookMapper;
import com.bookstore.entity.Book;
import com.bookstore.entity.Category;
import com.bookstore.repository.BookRepository;
import com.bookstore.repository.CategoryRepository;
import com.bookstore.service.impl.BookServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("BookService Unit Tests")
class BookServiceTest {

    @Mock private BookRepository bookRepository;
    @Mock private CategoryRepository categoryRepository;
    @Mock private BookMapper bookMapper;

    @InjectMocks
    private BookServiceImpl bookService;

    private Book testBook;
    private BookResponseDto testBookDto;

    @BeforeEach
    void setUp() {
        Category category = new Category();
        category.setId(1L);
        category.setName("Programming");
        category.setNameRu("Программирование");

        testBook = Book.builder()
                .id(1L)
                .title("Clean Code")
                .titleRu("Чистый код")
                .author("Robert C. Martin")
                .authorRu("Роберт Мартин")
                .isbn("9780132350884")
                .price(new BigDecimal("39.99"))
                .publishedYear(2008)
                .categories(Set.of(category))
                .build();

        testBookDto = new BookResponseDto(
                1L, "Clean Code", "Чистый код",
                "Robert C. Martin", "Роберт Мартин",
                "9780132350884", new BigDecimal("39.99"),
                "A handbook", "Руководство",
                null, 2008,
                Set.of(new CategoryResponseDto(1L, "Programming", "Программирование", null))
        );
    }

    @Test
    @DisplayName("findById — возвращает книгу по существующему ID")
    void findById_whenExists_returnsBook() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));
        when(bookMapper.toDto(testBook)).thenReturn(testBookDto);

        BookResponseDto result = bookService.findById(1L);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.title()).isEqualTo("Clean Code");
        assertThat(result.price()).isEqualByComparingTo("39.99");
        assertThat(result.publishedYear()).isEqualTo(2008);
        verify(bookRepository).findById(1L);
    }

    @Test
    @DisplayName("findById — выбрасывает EntityNotFoundException если книга не найдена")
    void findById_whenNotExists_throwsException() {
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.findById(99L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("99");

        verify(bookRepository).findById(99L);
    }

    @Test
    @DisplayName("findAll — возвращает страницу книг с пагинацией")
    void findAll_returnsPageOfBooks() {
        PageRequest pageable = PageRequest.of(0, 10);
        Page<Book> page = new PageImpl<>(List.of(testBook), pageable, 1);
        when(bookRepository.findAll(pageable)).thenReturn(page);
        when(bookMapper.toDto(testBook)).thenReturn(testBookDto);

        Page<BookResponseDto> result = bookService.findAll(pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).title()).isEqualTo("Clean Code");
    }

    @Test
    @DisplayName("save — сохраняет новую книгу и возвращает DTO")
    void save_createsAndReturnsBook() {
        BookRequestDto dto = new BookRequestDto(
                "Clean Code", "Чистый код",
                "Robert C. Martin", "Роберт Мартин",
                "9780132350884", new BigDecimal("39.99"),
                "Desc EN", "Desc RU", null, 2008, Set.of(1L)
        );
        Category category = new Category();
        category.setId(1L);

        when(bookMapper.toEntity(dto)).thenReturn(testBook);
        when(categoryRepository.findAllById(Set.of(1L))).thenReturn(List.of(category));
        when(bookRepository.save(any(Book.class))).thenReturn(testBook);
        when(bookMapper.toDto(testBook)).thenReturn(testBookDto);

        BookResponseDto result = bookService.save(dto);

        assertThat(result).isNotNull();
        assertThat(result.title()).isEqualTo("Clean Code");
        verify(bookRepository).save(any(Book.class));
    }

    @Test
    @DisplayName("save — выбрасывает исключение если категория не найдена")
    void save_whenCategoryNotFound_throwsException() {
        BookRequestDto dto = new BookRequestDto(
                "Title", null, "Author", null,
                "9780000000000", new BigDecimal("9.99"),
                null, null, null, null, Set.of(999L)
        );
        when(bookMapper.toEntity(dto)).thenReturn(testBook);
        when(categoryRepository.findAllById(Set.of(999L))).thenReturn(List.of());

        assertThatThrownBy(() -> bookService.save(dto))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("categories");
    }

    @Test
    @DisplayName("delete — вызывает удаление существующей книги")
    void delete_whenExists_deletesBook() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));

        bookService.delete(1L);

        verify(bookRepository).deleteById(1L);
    }

    @Test
    @DisplayName("delete — не удаляет если книга не найдена")
    void delete_whenNotExists_throwsAndDoesNotDelete() {
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.delete(99L))
                .isInstanceOf(EntityNotFoundException.class);

        verify(bookRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("search — возвращает книги по поисковому запросу")
    void search_returnsMatchingBooks() {
        PageRequest pageable = PageRequest.of(0, 8);
        Page<Book> page = new PageImpl<>(List.of(testBook));
        when(bookRepository.search(eq("Clean"), eq(pageable))).thenReturn(page);
        when(bookMapper.toDto(testBook)).thenReturn(testBookDto);

        Page<BookResponseDto> result = bookService.search("Clean", pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).title()).isEqualTo("Clean Code");
    }

    @Test
    @DisplayName("search — возвращает пустую страницу если ничего не найдено")
    void search_whenNoResults_returnsEmptyPage() {
        PageRequest pageable = PageRequest.of(0, 8);
        when(bookRepository.search(eq("xyz123"), eq(pageable)))
                .thenReturn(new PageImpl<>(List.of()));

        Page<BookResponseDto> result = bookService.search("xyz123", pageable);

        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isZero();
    }
}