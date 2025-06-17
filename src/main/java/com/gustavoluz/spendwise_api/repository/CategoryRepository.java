package com.gustavoluz.spendwise_api.repository;

import com.gustavoluz.spendwise_api.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID> {
}
