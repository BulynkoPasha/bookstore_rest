package com.bookstore.mapper;

import com.bookstore.dto.request.CategoryRequestDto;
import com.bookstore.dto.response.CategoryResponseDto;
import com.bookstore.entity.Category;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CategoryMapper {

    CategoryResponseDto toDto(Category category);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "books", ignore = true)
    Category toEntity(CategoryRequestDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "books", ignore = true)
    void updateEntityFromDto(CategoryRequestDto dto, @MappingTarget Category category);
}