package moinammaoueni.e_commerce.api.image;

import jakarta.servlet.http.HttpServletResponse;
import moinammaoueni.e_commerce.api.image.dto.ImageResponseDTO;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
public class ImageController {

    private final ImageService imageService;

    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @PostMapping("/admin/products/{productId}/images")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ImageResponseDTO> uploadImage(@PathVariable Long productId,
                                                      @RequestParam("file") MultipartFile file) {
        return ResponseEntity.status(HttpStatus.CREATED).body(imageService.uploadImage(productId, file));
    }

    @GetMapping("/images/{id}/content")
    public ResponseEntity<Resource> getImageContent(@PathVariable Long id, HttpServletResponse response) {
        Resource resource = imageService.readImageContent(id);
        ImageResponseDTO metadata = imageService.getImageMetadata(id);

        response.setHeader(HttpHeaders.CACHE_CONTROL, "public, max-age=86400, immutable");
        response.setHeader("X-Content-Type-Options", "nosniff");
        response.setHeader(HttpHeaders.CONTENT_TYPE, metadata.getContentType());

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(metadata.getContentType()))
                .body(resource);
    }

    @DeleteMapping("/admin/images/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteImage(@PathVariable Long id) {
        imageService.deleteImage(id);
    }
}