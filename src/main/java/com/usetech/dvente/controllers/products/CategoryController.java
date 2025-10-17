package com.usetech.dvente.controllers.products;

import com.usetech.dvente.entities.products.Category;
import com.usetech.dvente.responses.ApiResponse;
import com.usetech.dvente.services.products.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;


    @GetMapping
    public ResponseEntity<ApiResponse<List<Category>>> getAllCategories(
            @RequestParam(required = false) Boolean main
    ) {
        List<Category> categories = categoryService.getAllCategories(main);
        ApiResponse<List<Category>> response = new ApiResponse<>("success", categories);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
