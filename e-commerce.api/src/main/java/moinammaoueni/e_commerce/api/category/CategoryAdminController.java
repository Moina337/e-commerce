package moinammaoueni.e_commerce.api.category;

import moinammaoueni.e_commerce.api.category.dto.CategoryManagementResponseDTO;
import moinammaoueni.e_commerce.api.category.dto.CategoryRequestDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller back-office pour la gestion des catégories.
 */
@RestController
@RequestMapping("/api/admin/categories")
public class CategoryAdminController {

    private final CategoryService categoryService;

    public CategoryAdminController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public ResponseEntity<List<CategoryManagementResponseDTO>> getAllCategoriesForAdmin() {
        return ResponseEntity.ok(categoryService.getAdminCategories());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryManagementResponseDTO> getCategoryByIdForAdmin(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.getAdminCategoryById(id));
    }

    @PostMapping
    public ResponseEntity<CategoryManagementResponseDTO> createCategory(@Valid @RequestBody CategoryRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(categoryService.createCategory(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryManagementResponseDTO> updateCategory(@PathVariable Long id,
                                                                      @Valid @RequestBody CategoryRequestDTO request) {
        return ResponseEntity.ok(categoryService.updateCategory(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}
