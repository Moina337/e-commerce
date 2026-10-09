package moinammaoueni.e_commerce.api.image;

import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Locale;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class ImageValidator {

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of("image/jpeg", "image/png", "image/webp");
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "jpeg", "png", "webp");

    private final ImageStorageProperties imageStorageProperties;

    public ValidatedImage validate(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Le fichier image est vide");
        }

        if (file.getSize() > imageStorageProperties.getMaxFileSize().toBytes()) {
            throw new IllegalArgumentException("Le fichier image dépasse la taille maximale autorisée");
        }

        byte[] bytes = readBytes(file);
        if (bytes.length == 0) {
            throw new IllegalArgumentException("Le fichier image est vide");
        }

        String extension = normalizeExtension(file.getOriginalFilename());
        if (extension == null || !ALLOWED_EXTENSIONS.contains(extension)) {
            throw new IllegalArgumentException("Format d'image non pris en charge");
        }

        String contentType = normalizeContentType(file.getContentType());
        if (contentType != null && !ALLOWED_CONTENT_TYPES.contains(contentType)) {
            throw new IllegalArgumentException("Type de fichier non autorisé. Formats acceptés : JPEG, PNG, WebP");
        }

        String detectedFormat = detectFormat(bytes);
        if (detectedFormat == null) {
            throw new IllegalArgumentException("Le fichier ne correspond pas à une image valide");
        }

        String expectedFormatFromExtension = detectExpectedFormat(extension);
        if (!detectedFormat.equals(expectedFormatFromExtension)) {
            throw new IllegalArgumentException("Le format réel du fichier ne correspond pas à son extension");
        }

        String canonicalContentType = expectedContentType(detectedFormat);
        if (contentType != null && !canonicalContentType.equals(contentType)) {
            throw new IllegalArgumentException("Le type de contenu ne correspond pas au format réel de l'image");
        }

        return new ValidatedImage(extension, canonicalContentType, detectedFormat, bytes);
    }

    private byte[] readBytes(MultipartFile file) {
        try {
            return file.getBytes();
        } catch (IOException e) {
            throw new IllegalStateException("Impossible de lire le fichier image", e);
        }
    }

    private String normalizeExtension(String originalFilename) {
        String extension = FilenameUtils.getExtension(originalFilename);
        if (extension == null || extension.isBlank()) {
            return null;
        }
        return extension.toLowerCase(Locale.ROOT);
    }

    private String detectExpectedFormat(String extension) {
        if ("jpg".equals(extension) || "jpeg".equals(extension)) {
            return "jpeg";
        }
        if ("png".equals(extension)) {
            return "png";
        }
        if ("webp".equals(extension)) {
            return "webp";
        }
        return null;
    }

    private String normalizeContentType(String contentType) {
        if (contentType == null || contentType.isBlank()) {
            return null;
        }

        String normalized = contentType.trim().toLowerCase(Locale.ROOT);
        int separatorIndex = normalized.indexOf(';');
        return separatorIndex >= 0 ? normalized.substring(0, separatorIndex).trim() : normalized;
    }

    private String detectFormat(byte[] bytes) {
        if (hasJpegSignature(bytes)) {
            return "jpeg";
        }
        if (hasPngSignature(bytes)) {
            return "png";
        }
        if (hasWebpSignature(bytes)) {
            return "webp";
        }
        return null;
    }

    private boolean hasJpegSignature(byte[] bytes) {
        return bytes.length >= 3 && (bytes[0] == (byte) 0xFF && bytes[1] == (byte) 0xD8 && bytes[2] == (byte) 0xFF);
    }

    private boolean hasPngSignature(byte[] bytes) {
        return bytes.length >= 8
                && bytes[0] == (byte) 0x89
                && bytes[1] == 0x50
                && bytes[2] == 0x4E
                && bytes[3] == 0x47
                && bytes[4] == (byte) 0x0D
                && bytes[5] == 0x0A
                && bytes[6] == (byte) 0x1A
                && bytes[7] == 0x0A;
    }

    private boolean hasWebpSignature(byte[] bytes) {
        return bytes.length >= 12
                && bytes[0] == 'R'
                && bytes[1] == 'I'
                && bytes[2] == 'F'
                && bytes[3] == 'F'
                && bytes[8] == 'W'
                && bytes[9] == 'E'
                && bytes[10] == 'B'
                && bytes[11] == 'P';
    }

    private String expectedContentType(String detectedFormat) {
        return switch (detectedFormat) {
            case "jpeg" -> "image/jpeg";
            case "png" -> "image/png";
            case "webp" -> "image/webp";
            default -> throw new IllegalArgumentException("Format d'image non pris en charge");
        };
    }

    public record ValidatedImage(String extension, String contentType, String detectedFormat, byte[] bytes) {
    }
}
