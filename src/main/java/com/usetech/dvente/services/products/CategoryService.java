package com.usetech.dvente.services.products;

import com.usetech.dvente.entities.products.Category;
import com.usetech.dvente.repositories.products.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<Category> getAllCategories(Boolean main) {
        if(main != null && main) {
            return categoryRepository.findByMain(true);
        }
        return categoryRepository.findAll();
    }
}
