package com.gustavoluz.spendwise_api.repository;

import com.gustavoluz.spendwise_api.entity.Category;
import com.gustavoluz.spendwise_api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID> {
    List<Category> findAllByUser(User user);
}
