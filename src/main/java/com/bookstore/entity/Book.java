package com.bookstore.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "books")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@SQLDelete(sql = "UPDATE books SET is_deleted = true WHERE id = ?")
@Where(clause = "is_deleted = false")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(name = "title_ru")
    private String titleRu;

    @Column(nullable = false)
    private String author;

    @Column(name = "author_ru")
    private String authorRu;

    @Column(nullable = false, unique = true)
    private String isbn;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "description_ru", columnDefinition = "TEXT")
    private String descriptionRu;

    @Column(name = "cover_image")
    private String coverImage;

    @Column(name = "published_year")
    private Integer publishedYear;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "books_categories",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private Set<Category> categories = new HashSet<>();
}