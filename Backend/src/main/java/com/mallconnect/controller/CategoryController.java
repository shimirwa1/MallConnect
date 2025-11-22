package com.mallconnect.controller;

import com.mallconnect.dto.CategoryDto;
import com.mallconnect.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<CategoryDto>> getAllCategories(
            @RequestParam(defaultValue = "false") boolean activeOnly) {
        List<CategoryDto> categories = activeOnly
                ? categoryService.getActiveCategories()
                : categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryDto> getCategory(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.getCategoryById(id));
    }
}
