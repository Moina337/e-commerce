package moinammaoueni.e_commerce.api.image;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class LocalImageStorageService implements ImageStorageService {

    private final Path storageRoot;

    public LocalImageStorageService(ImageStorageProperties properties) {
        this.storageRoot = Paths.get(properties.getLocation()).toAbsolutePath().normalize();
        try {
            Files.createDirectories(storageRoot);
        } catch (IOException e) {
            throw new IllegalStateException("Impossible de créer le répertoire de stockage des images : " + storageRoot, e);
        }
    }

    @Override
    public String store(MultipartFile file, String storageKey) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IOException("Le fichier est vide");
        }

        String sanitizedKey = sanitizeStorageKey(storageKey);
        Path target = resolveStoragePath(sanitizedKey);
        Files.copy(file.getInputStream(), target);
        return sanitizedKey;
    }

    @Override
    public Resource load(String storageKey) throws IOException {
        String sanitizedKey = sanitizeStorageKey(storageKey);
        Path target = resolveStoragePath(sanitizedKey);
        Resource resource = new UrlResource(target.toUri());
        if (!resource.exists() || !resource.isReadable()) {
            throw new IOException("Le fichier image est introuvable ou illisible");
        }
        return resource;
    }

    @Override
    public void delete(String storageKey) throws IOException {
        String sanitizedKey = sanitizeStorageKey(storageKey);
        Path target = resolveStoragePath(sanitizedKey);
        Files.deleteIfExists(target);
    }

    private Path resolveStoragePath(String storageKey) throws IOException {
        Path candidate = storageRoot.resolve(storageKey).normalize();
        if (!candidate.startsWith(storageRoot)) {
            throw new IOException("Chemin de stockage invalide");
        }
        return candidate;
    }

    private String sanitizeStorageKey(String storageKey) {
        if (storageKey == null || storageKey.isBlank() || storageKey.contains("..")) {
            throw new IllegalArgumentException("Clé de stockage invalide");
        }
        return storageKey;
    }
}
