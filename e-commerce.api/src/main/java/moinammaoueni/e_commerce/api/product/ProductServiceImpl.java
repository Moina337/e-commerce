package moinammaoueni.e_commerce.api.product;

import moinammaoueni.e_commerce.api.category.Category;
import moinammaoueni.e_commerce.api.category.CategoryRepository;
import moinammaoueni.e_commerce.api.common.exception.DuplicateResourceException;
import moinammaoueni.e_commerce.api.common.exception.ResourceNotFoundException;
import moinammaoueni.e_commerce.api.product.dto.ProductManagementResponseDTO;
import moinammaoueni.e_commerce.api.product.dto.ProductRequestDTO;
import moinammaoueni.e_commerce.api.product.dto.ProductResponseDTO;
import moinammaoueni.e_commerce.api.product.mapper.ProductMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.util.Locale;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;

    public ProductServiceImpl(ProductRepository productRepository, CategoryRepository categoryRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.productMapper = productMapper;
    }

    @Override
    @Transactional
    public ProductManagementResponseDTO createProduct(ProductRequestDTO request) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Catégorie introuvable avec l'id : " + request.getCategoryId()));

        String name = request.getName().trim();
        String slug = generateSlug(name);

        if (productRepository.existsBySlug(slug)) {
            throw new DuplicateResourceException("Un produit avec ce slug existe déjà");
        }

        Product product = productMapper.toEntity(request);
        product.setName(name);
        product.setSlug(slug);
        product.setCategory(category);

        Product savedProduct = productRepository.save(product);
        return productMapper.toProductManagementResponseDTO(savedProduct);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponseDTO> getPublicProducts(Pageable pageable) {
        return productRepository.findAll(pageable)
                .map(productMapper::toProductResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponseDTO getPublicProductBySlug(String slug) {
        Product product = productRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Produit introuvable pour le slug : " + slug));
        return productMapper.toProductResponseDTO(product);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponseDTO> getPublicProductsByCategory(String categorySlug, Pageable pageable) {
        Category category = categoryRepository.findBySlug(categorySlug)
                .orElseThrow(() -> new ResourceNotFoundException("Catégorie introuvable pour le slug : " + categorySlug));

        return productRepository.findByCategory(category, pageable)
                .map(productMapper::toProductResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponseDTO> searchPublicProductsByName(String name, Pageable pageable) {
        String searchTerm = name == null ? "" : name.trim();
        if (searchTerm.isEmpty()) {
            return getPublicProducts(pageable);
        }
        return productRepository.findByNameContainingIgnoreCase(searchTerm, pageable)
                .map(productMapper::toProductResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductManagementResponseDTO> getAdminProducts(Pageable pageable) {
        return productRepository.findAll(pageable)
                .map(productMapper::toProductManagementResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductManagementResponseDTO getAdminProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produit introuvable avec l'id : " + id));
        return productMapper.toProductManagementResponseDTO(product);
    }

    @Override
    @Transactional
    public ProductManagementResponseDTO updateProduct(Long id, ProductRequestDTO request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produit introuvable avec l'id : " + id));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Catégorie introuvable avec l'id : " + request.getCategoryId()));

        String newName = request.getName().trim();
        String newSlug = generateSlug(newName);

        if (!product.getName().equals(newName) && productRepository.existsBySlug(newSlug)) {
            throw new DuplicateResourceException("Un produit avec ce slug existe déjà");
        }

        product.setName(newName);
        product.setSlug(newSlug);
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStockQuantity(request.getStockQuantity());
        product.setCategory(category);

        return productMapper.toProductManagementResponseDTO(productRepository.save(product));
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produit introuvable avec l'id : " + id));
        productRepository.delete(product);
    }

    private String generateSlug(String value) {
        String normalized = Normalizer.normalize(value, Normalizer.Form.NFD);
        String slug = normalized.replaceAll("[^\\p{ASCII}]", "")
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-+|-$", "")
                .trim();

        return slug.isEmpty() ? "product" : slug;
    }
}