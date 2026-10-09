package moinammaoueni.e_commerce.api.image;

import moinammaoueni.e_commerce.api.image.dto.ImageResponseDTO;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface ImageService {

    ImageResponseDTO uploadImage(Long productId, MultipartFile file);

    ImageResponseDTO getImageMetadata(Long imageId);

    Resource readImageContent(Long imageId);

    void deleteImage(Long imageId);

    boolean existsById(Long imageId);

    boolean productHasImage(Long productId);
}
