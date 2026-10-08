package moinammaoueni.e_commerce.api.category.mapper;

import moinammaoueni.e_commerce.api.category.Category;
import moinammaoueni.e_commerce.api.category.dto.CategoryManagementResponseDTO;
import moinammaoueni.e_commerce.api.category.dto.CategoryRequestDTO;
import moinammaoueni.e_commerce.api.category.dto.CategoryResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper MapStruct pour convertir entre l'entité Category et ses DTOs.
 */
@Mapper(componentModel = "spring")
public interface CategoryMapper {

    /**
     * Mappe une entité Category en CategoryResponseDTO (public).
     *
     * @param category l'entité Category
     * @return CategoryResponseDTO sans timestamps
     */
    CategoryResponseDTO toCategoryResponseDTO(Category category);

    /**
     * Mappe une entité Category en CategoryManagementResponseDTO (back-office).
     *
     * @param category l'entité Category
     * @return CategoryManagementResponseDTO avec timestamps
     */
    CategoryManagementResponseDTO toCategoryManagementResponseDTO(Category category);

    /**
     * Mappe un CategoryRequestDTO en entité Category.
     * Le slug ne doit pas être mappé ici, il sera généré par le service.
     *
     * @param request le DTO de requête
     * @return une nouvelle entité Category
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "slug", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Category toEntity(CategoryRequestDTO request);
}