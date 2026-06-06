package com.gustavoluz.spendwise_api.repository;

import com.gustavoluz.spendwise_api.entity.Category;
import com.gustavoluz.spendwise_api.entity.User;
import com.gustavoluz.spendwise_api.entity.enums.CategoryType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID> {

    List<Category> findAllByIsGlobalTrue();

    List<Category> findAllByIsGlobalTrueAndType(CategoryType type);

    List<Category> findAllByUser(User user);

    List<Category> findAllByUserAndType(User user, CategoryType type);

    boolean existsByNameIgnoreCaseAndUser(String name, User user);

    boolean existsByNameIgnoreCaseAndUserAndIdNot(String name, User user, UUID id);

    boolean existsByNameIgnoreCaseAndIsGlobalTrue(String name);

    boolean existsByNameIgnoreCaseAndIsGlobalTrueAndIdNot(String name, UUID id);
}
