package com.mallconnect.service;

import com.mallconnect.dto.CategoryDto;
import com.mallconnect.model.Category;
import com.mallconnect.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryDto createCategory(CategoryDto dto) {
        if (categoryRepository.existsByName(dto.getName())) {
            throw new RuntimeException("Category name already exists: " + dto.getName());
        }
        Category category = Category.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .active(dto.isActive())
                .build();
        category = categoryRepository.save(category);
        return toDto(category);
    }

    @Transactional(readOnly = true)
    public List<CategoryDto> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CategoryDto> getActiveCategories() {
        return categoryRepository.findByActiveTrue().stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public CategoryDto getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found: " + id));
        return toDto(category);
    }

    public CategoryDto updateCategory(Long id, CategoryDto dto) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found: " + id));

        if (!category.getName().equals(dto.getName()) && categoryRepository.existsByName(dto.getName())) {
            throw new RuntimeException("Category name already exists: " + dto.getName());
        }

        category.setName(dto.getName());
        category.setDescription(dto.getDescription());
        category.setActive(dto.isActive());
        category = categoryRepository.save(category);
        return toDto(category);
    }

    public void deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new RuntimeException("Category not found: " + id);
        }
        categoryRepository.deleteById(id);
    }

    public CategoryDto toDto(Category category) {
        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .active(category.isActive())
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .build();
    }
}
