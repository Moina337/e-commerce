package moinammaoueni.e_commerce.api.product.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import moinammaoueni.e_commerce.api.image.dto.ImageResponseDTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductManagementResponseDTO {

    private Long id;
    private String name;
    private String slug;
    private String description;
    private BigDecimal price;
    private Integer stockQuantity;
    private List<ImageResponseDTO> images;
    private CategorySummaryDTO category;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}