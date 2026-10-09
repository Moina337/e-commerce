package moinammaoueni.e_commerce.api.product.mapper;

import moinammaoueni.e_commerce.api.category.Category;
import moinammaoueni.e_commerce.api.image.Image;
import moinammaoueni.e_commerce.api.image.dto.ImageResponseDTO;
import moinammaoueni.e_commerce.api.product.Product;
import moinammaoueni.e_commerce.api.product.dto.CategorySummaryDTO;
import moinammaoueni.e_commerce.api.product.dto.ProductManagementResponseDTO;
import moinammaoueni.e_commerce.api.product.dto.ProductRequestDTO;
import moinammaoueni.e_commerce.api.product.dto.ProductResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "category", expression = "java(toCategorySummary(product.getCategory()))")
    @Mapping(target = "images", expression = "java(toImageDtos(product.getImages()))")
    ProductResponseDTO toProductResponseDTO(Product product);

    @Mapping(target = "category", expression = "java(toCategorySummary(product.getCategory()))")
    @Mapping(target = "images", expression = "java(toImageDtos(product.getImages()))")
    ProductManagementResponseDTO toProductManagementResponseDTO(Product product);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "slug", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "images", ignore = true)
    Product toEntity(ProductRequestDTO request);

    default CategorySummaryDTO toCategorySummary(Category category) {
        if (category == null) {
            return null;
        }

        return new CategorySummaryDTO(category.getId(), category.getName(), category.getSlug());
    }

    default List<ImageResponseDTO> toImageDtos(List<Image> images) {
        if (images == null || images.isEmpty()) {
            return List.of();
        }

        return images.stream()
                .map(image -> {
                    ImageResponseDTO dto = new ImageResponseDTO();
                    dto.setId(image.getId());
                    dto.setOriginalFilename(image.getOriginalFilename());
                    dto.setContentType(image.getContentType());
                    dto.setSize(image.getSize());
                    dto.setCreatedAt(image.getCreatedAt());
                    dto.setUrl("/api/images/" + image.getId() + "/content");
                    return dto;
                })
                .toList();
    }
}