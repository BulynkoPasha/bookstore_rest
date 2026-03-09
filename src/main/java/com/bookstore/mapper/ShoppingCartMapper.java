package com.bookstore.mapper;

import com.bookstore.dto.response.CartItemResponseDto;
import com.bookstore.dto.response.ShoppingCartResponseDto;
import com.bookstore.entity.CartItem;
import com.bookstore.entity.ShoppingCart;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ShoppingCartMapper {

    @Mapping(source = "user.id", target = "userId")
    ShoppingCartResponseDto toDto(ShoppingCart shoppingCart);

    @Mapping(source = "book.id", target = "bookId")
    @Mapping(source = "book.title", target = "bookTitle")
    @Mapping(source = "book.author", target = "bookAuthor")
    @Mapping(source = "book.price", target = "bookPrice")
    CartItemResponseDto toItemDto(CartItem cartItem);
}