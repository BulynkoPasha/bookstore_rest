package com.bookstore.mapper;

import com.bookstore.dto.response.OrderItemResponseDto;
import com.bookstore.dto.response.OrderResponseDto;
import com.bookstore.entity.Order;
import com.bookstore.entity.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OrderMapper {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.firstName", target = "userFirstName")
    @Mapping(source = "user.lastName", target = "userLastName")
    @Mapping(source = "user.email", target = "userEmail")
    @Mapping(source = "user.phone", target = "userPhone")
    OrderResponseDto toDto(Order order);

    @Mapping(source = "book.id", target = "bookId")
    @Mapping(source = "book.title", target = "bookTitle")
    OrderItemResponseDto toItemDto(OrderItem orderItem);
}