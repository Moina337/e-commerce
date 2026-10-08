package moinammaoueni.e_commerce.api.category;

import moinammaoueni.e_commerce.api.category.dto.CategoryManagementResponseDTO;
import moinammaoueni.e_commerce.api.category.dto.CategoryRequestDTO;
import moinammaoueni.e_commerce.api.category.dto.CategoryResponseDTO;
import moinammaoueni.e_commerce.api.category.mapper.CategoryMapper;
import moinammaoueni.e_commerce.api.common.exception.DuplicateResourceException;
import moinammaoueni.e_commerce.api.common.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.util.List;
import java.util.Locale;

/**
 * Implémentation du service métier pour les catégories.
 */
@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public CategoryServiceImpl(CategoryRepository categoryRepository, CategoryMapper categoryMapper) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
    }

    @Override
    @Transactional
    public CategoryManagementResponseDTO createCategory(CategoryRequestDTO request) {
        if (request == null || request.getName() == null || request.getName().isBlank()) {
            throw new IllegalArgumentException("Le nom de la catégorie est obligatoire");
        }

        String normalizedName = request.getName().trim();
        String slug = generateSlug(normalizedName);

        if (categoryRepository.existsBySlug(slug)) {
            throw new DuplicateResourceException("Une catégorie avec ce slug existe déjà");
        }

        if (categoryRepository.existsByName(normalizedName)) {
            throw new DuplicateResourceException("Une catégorie avec ce nom existe déjà");
        }

        Category category = categoryMapper.toEntity(request);
        category.setName(normalizedName);
        category.setSlug(slug);

        Category savedCategory = categoryRepository.save(category);
        return categoryMapper.toCategoryManagementResponseDTO(savedCategory);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponseDTO> getPublicCategories() {
        return categoryRepository.findAll()
                .stream()
                .map(categoryMapper::toCategoryResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponseDTO getPublicCategoryBySlug(String slug) {
        Category category = categoryRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Catégorie introuvable pour le slug : " + slug));
        return categoryMapper.toCategoryResponseDTO(category);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryManagementResponseDTO> getAdminCategories() {
        return categoryRepository.findAll()
                .stream()
                .map(categoryMapper::toCategoryManagementResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryManagementResponseDTO getAdminCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Catégorie introuvable avec l'id : " + id));
        return categoryMapper.toCategoryManagementResponseDTO(category);
    }

    @Override
    @Transactional
    public CategoryManagementResponseDTO updateCategory(Long id, CategoryRequestDTO request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Catégorie introuvable avec l'id : " + id));

        String newName = request.getName().trim();
        String newSlug = generateSlug(newName);

        if (!category.getName().equals(newName) && categoryRepository.existsByName(newName)) {
            throw new DuplicateResourceException("Une catégorie avec ce nom existe déjà");
        }

        if (!category.getSlug().equals(newSlug) && categoryRepository.existsBySlug(newSlug)) {
            throw new DuplicateResourceException("Une catégorie avec ce slug existe déjà");
        }

        category.setName(newName);
        category.setSlug(newSlug);

        return categoryMapper.toCategoryManagementResponseDTO(categoryRepository.save(category));
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Catégorie introuvable avec l'id : " + id));
        categoryRepository.delete(category);
    }

    private String generateSlug(String value) {
        String normalized = Normalizer.normalize(value, Normalizer.Form.NFD);
        String slug = normalized.replaceAll("[^\\p{ASCII}]", "")
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-+|-$", "")
                .trim();

        return slug.isEmpty() ? "category" : slug;
    }
}
