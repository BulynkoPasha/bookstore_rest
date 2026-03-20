package com.bookstore.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLDelete(sql = "UPDATE categories SET is_deleted = true WHERE id = ?")
@Where(clause = "is_deleted = false")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "name_ru")
    private String nameRu;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;

    @ManyToMany(mappedBy = "categories", fetch = FetchType.LAZY)
    private Set<Book> books = new HashSet<>();
}