package com.gustavoluz.spendwise_api.mapper;

import com.gustavoluz.spendwise_api.dto.category.CategoryRequestDto;
import com.gustavoluz.spendwise_api.dto.category.CategoryResponseDto;
import com.gustavoluz.spendwise_api.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "transactions", ignore = true)
    @Mapping(target = "user", ignore = true)
    Category toEntity(CategoryRequestDto dto);

    CategoryResponseDto toDto (Category entity);

}
