package moinammaoueni.e_commerce.api.image;

import moinammaoueni.e_commerce.api.common.exception.ResourceNotFoundException;
import moinammaoueni.e_commerce.api.image.dto.ImageResponseDTO;
import moinammaoueni.e_commerce.api.image.mapper.ImageMapper;
import moinammaoueni.e_commerce.api.product.Product;
import moinammaoueni.e_commerce.api.product.ProductRepository;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class ImageServiceImpl implements ImageService {

    private final ImageRepository imageRepository;
    private final ProductRepository productRepository;
    private final ImageStorageService imageStorageService;
    private final ImageMapper imageMapper;
    private final ImageValidator imageValidator;

    @Override
    @Transactional
    public ImageResponseDTO uploadImage(Long productId, MultipartFile file) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Produit introuvable avec l'id : " + productId));

        ImageValidator.ValidatedImage validatedImage = imageValidator.validate(file);
        String storageKey = createStorageKey(validatedImage.extension());
        String originalFilename = file.getOriginalFilename() == null ? "upload" : file.getOriginalFilename();

        try {
            imageStorageService.store(file, storageKey);
        } catch (IOException e) {
            throw new IllegalStateException("Erreur de stockage du fichier image", e);
        }

        Image image = new Image();
        image.setOriginalFilename(originalFilename);
        image.setStorageKey(storageKey);
        image.setContentType(validatedImage.contentType());
        image.setSize(file.getSize());
        image.setProduct(product);

        try {
            Image saved = imageRepository.save(image);
            return imageMapper.toDtoWithUrl(saved, "/api/images");
        } catch (Exception e) {
            rollbackStoredFile(storageKey);
            throw e;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ImageResponseDTO getImageMetadata(Long imageId) {
        Image image = imageRepository.findById(imageId)
                .orElseThrow(() -> new ResourceNotFoundException("Image introuvable avec l'id : " + imageId));
        return imageMapper.toDtoWithUrl(image, "/api/images");
    }

    @Override
    @Transactional(readOnly = true)
    public Resource readImageContent(Long imageId) {
        Image image = imageRepository.findById(imageId)
                .orElseThrow(() -> new ResourceNotFoundException("Image introuvable avec l'id : " + imageId));
        try {
            return imageStorageService.load(image.getStorageKey());
        } catch (IOException e) {
            throw new IllegalStateException("Erreur de lecture du fichier image", e);
        }
    }

    @Override
    @Transactional
    public void deleteImage(Long imageId) {
        Image image = imageRepository.findById(imageId)
                .orElseThrow(() -> new ResourceNotFoundException("Image introuvable avec l'id : " + imageId));

        // Soft-delete: mark metadata as deleted so productHasImage() and other checks
        // only consider non-deleted images. We still attempt to remove the physical
        // file, but do not fail the operation if file deletion fails to avoid
        // leaving the domain in an inconsistent state.
        image.setDeleted(true);
        imageRepository.save(image);

        try {
            imageStorageService.delete(image.getStorageKey());
        } catch (IOException e) {
            // best-effort: physical file deletion failed, metadata is already marked deleted
            // swallowing the exception intentionally; callers should rely on metadata state
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(Long imageId) {
        return imageRepository.existsById(imageId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean productHasImage(Long productId) {
        // Only consider non-deleted images when checking if a product has an image.
        return imageRepository.existsByProductIdAndDeletedFalse(productId);
    }

    private void rollbackStoredFile(String storageKey) {
        try {
            imageStorageService.delete(storageKey);
        } catch (IOException deleteException) {
            throw new IllegalStateException("Erreur de stockage du fichier image et impossibilité de supprimer le fichier créé", deleteException);
        }
    }

    private String createStorageKey(String extension) {
        return UUID.randomUUID() + "." + extension;
    }
}