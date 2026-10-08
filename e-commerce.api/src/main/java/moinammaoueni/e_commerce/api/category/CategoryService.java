package moinammaoueni.e_commerce.api.category;

import moinammaoueni.e_commerce.api.category.dto.CategoryManagementResponseDTO;
import moinammaoueni.e_commerce.api.category.dto.CategoryRequestDTO;
import moinammaoueni.e_commerce.api.category.dto.CategoryResponseDTO;

import java.util.List;

/**
 * Interface du service métier pour la gestion des catégories.
 */
public interface CategoryService {

    CategoryManagementResponseDTO createCategory(CategoryRequestDTO request);

    List<CategoryResponseDTO> getPublicCategories();

    CategoryResponseDTO getPublicCategoryBySlug(String slug);

    List<CategoryManagementResponseDTO> getAdminCategories();

    CategoryManagementResponseDTO getAdminCategoryById(Long id);

    CategoryManagementResponseDTO updateCategory(Long id, CategoryRequestDTO request);

    void deleteCategory(Long id);
}
