
package moinammaoueni.e_commerce.api.image;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@Primary
public class CloudinaryImageStorageService implements ImageStorageService {

    private final Cloudinary cloudinary;
    private final String folder;

    public CloudinaryImageStorageService(
            Cloudinary cloudinary,
            @Value("${CLOUDINARY_FOLDER}") String folder) {

        this.cloudinary = cloudinary;
        this.folder = folder;
    }

    /**
     * Envoie une image vers Cloudinary.
     */
    @Override
    public StoredImage store(
            MultipartFile file,
            String storageKey) throws IOException {

        if (file == null || file.isEmpty()) {
            throw new IOException("Le fichier est vide");
        }

        Map<?, ?> result = cloudinary.uploader().upload(
                file.getBytes(),
                ObjectUtils.asMap(
                        "folder", folder,
                        "public_id", storageKey,
                        "resource_type", "image"
                )
        );

        String publicId = (String) result.get("public_id");
        String secureUrl = (String) result.get("secure_url");

        if (publicId == null || secureUrl == null) {
            throw new IOException("Cloudinary n'a pas retourné les informations de l'image");
        }

        return new StoredImage(publicId, secureUrl);
    }

    /**
     * Récupère une image depuis Cloudinary.
     */
    @Override
    public Resource load(String storageKey) throws IOException {

        if (storageKey == null || storageKey.isBlank()) {
            throw new IOException("Identifiant de stockage invalide");
        }

        String imageUrl = cloudinary.url()
                .secure(true)
                .resourceType("image")
                .generate(storageKey);

        Resource resource = new UrlResource(imageUrl);

        if (!resource.exists() || !resource.isReadable()) {
            throw new IOException("Image introuvable ou illisible sur Cloudinary");
        }

        return resource;
    }

    /**
     * Supprime une image de Cloudinary.
     */
    @Override
    public void delete(String storageKey) throws IOException {

        if (storageKey == null || storageKey.isBlank()) {
            throw new IOException("Identifiant de stockage invalide");
        }

        Map<?, ?> result = cloudinary.uploader().destroy(
                storageKey,
                ObjectUtils.asMap(
                        "resource_type", "image",
                        "invalidate", true
                )
        );

        String status = (String) result.get("result");

        if (!"ok".equals(status) && !"not found".equals(status)) {
            throw new IOException("Impossible de supprimer l'image sur Cloudinary");
        }
    }
}
