package com.gustavoluz.spendwise_api.config.data;

import com.gustavoluz.spendwise_api.entity.Category;
import com.gustavoluz.spendwise_api.entity.enums.CategoryType;
import com.gustavoluz.spendwise_api.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final CategoryRepository categoryRepository;

    @Override
    public void run(String... args) {
        if (categoryRepository.findAllByIsGlobalTrue().isEmpty()) {
            createGlobalCategories();
        }
    }

    private void createGlobalCategories() {
        List<Category> globals = Arrays.asList(
                createCategory("Food", CategoryType.EXPENSE),
                createCategory("Transport", CategoryType.EXPENSE),
                createCategory("Housing", CategoryType.EXPENSE),
                createCategory("Leisure", CategoryType.EXPENSE),
                createCategory("Health", CategoryType.EXPENSE),
                createCategory("Education", CategoryType.EXPENSE),
                createCategory("Salary", CategoryType.INCOME),
                createCategory("Freelance", CategoryType.INCOME),
                createCategory("Investments", CategoryType.INCOME)
        );

        categoryRepository.saveAll(globals);
    }

    private Category createCategory(String name, CategoryType type) {
        Category category = new Category();
        category.setName(name);
        category.setType(type);
        category.setIsGlobal(true);
        return category;
    }
}