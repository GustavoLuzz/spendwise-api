package com.gustavoluz.spendwise_api.mapper;

import com.gustavoluz.spendwise_api.dto.savinggoal.SavingGoalRequestDto;
import com.gustavoluz.spendwise_api.dto.savinggoal.SavingGoalResponseDto;
import com.gustavoluz.spendwise_api.entity.SavingGoal;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SavingGoalMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    SavingGoal toEntity(SavingGoalRequestDto dto);

    SavingGoalResponseDto toDto(SavingGoal entity);
}
