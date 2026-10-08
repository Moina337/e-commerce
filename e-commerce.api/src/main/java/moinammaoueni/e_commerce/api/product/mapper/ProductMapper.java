package moinammaoueni.e_commerce.api.product.mapper;

import moinammaoueni.e_commerce.api.category.Category;
import moinammaoueni.e_commerce.api.product.Product;
import moinammaoueni.e_commerce.api.product.dto.CategorySummaryDTO;
import moinammaoueni.e_commerce.api.product.dto.ProductManagementResponseDTO;
import moinammaoueni.e_commerce.api.product.dto.ProductRequestDTO;
import moinammaoueni.e_commerce.api.product.dto.ProductResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "category", expression = "java(toCategorySummary(product.getCategory()))")
    ProductResponseDTO toProductResponseDTO(Product product);

    @Mapping(target = "category", expression = "java(toCategorySummary(product.getCategory()))")
    ProductManagementResponseDTO toProductManagementResponseDTO(Product product);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "slug", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Product toEntity(ProductRequestDTO request);

    default CategorySummaryDTO toCategorySummary(Category category) {
        if (category == null) {
            return null;
        }

        return new CategorySummaryDTO(category.getId(), category.getName(), category.getSlug());
    }
}
