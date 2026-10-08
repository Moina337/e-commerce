package moinammaoueni.e_commerce.api.product;

import moinammaoueni.e_commerce.api.product.dto.ProductManagementResponseDTO;
import moinammaoueni.e_commerce.api.product.dto.ProductRequestDTO;
import moinammaoueni.e_commerce.api.product.dto.ProductResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {

    ProductManagementResponseDTO createProduct(ProductRequestDTO request);

    Page<ProductResponseDTO> getPublicProducts(Pageable pageable);

    ProductResponseDTO getPublicProductBySlug(String slug);

    Page<ProductResponseDTO> getPublicProductsByCategory(String categorySlug, Pageable pageable);

    Page<ProductResponseDTO> searchPublicProductsByName(String name, Pageable pageable);

    Page<ProductManagementResponseDTO> getAdminProducts(Pageable pageable);

    ProductManagementResponseDTO getAdminProductById(Long id);

    ProductManagementResponseDTO updateProduct(Long id, ProductRequestDTO request);

    void deleteProduct(Long id);
}
