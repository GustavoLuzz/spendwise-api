package com.gustavoluz.spendwise_api.mapper;

import com.gustavoluz.spendwise_api.dto.transaction.TransactionRequestDto;
import com.gustavoluz.spendwise_api.dto.transaction.TransactionResponseDto;
import com.gustavoluz.spendwise_api.dto.transaction.TransactionUpdateDto;
import com.gustavoluz.spendwise_api.entity.Transaction;
import com.gustavoluz.spendwise_api.entity.enums.CategoryType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "category", ignore = true)
    Transaction toEntity(TransactionRequestDto dto);

    @Mapping(target = "categoryId", source = "category.id")
    @Mapping(target = "categoryName", source = "category.name")
    @Mapping(target = "categoryType", source = "category.type")
    TransactionResponseDto toDto(Transaction entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "category", ignore = true)
    Transaction toEntity(TransactionUpdateDto dto);
}
