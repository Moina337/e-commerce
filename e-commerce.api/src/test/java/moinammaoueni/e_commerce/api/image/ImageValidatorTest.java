package moinammaoueni.e_commerce.api.image;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.unit.DataSize;

import static org.junit.jupiter.api.Assertions.*;

class ImageValidatorTest {

    private ImageValidator imageValidator;

    @BeforeEach
    void setUp() {
        ImageStorageProperties properties = new ImageStorageProperties();
        properties.setMaxFileSize(DataSize.ofMegabytes(5));
        imageValidator = new ImageValidator(properties);
    }

    @Test
    void validate_shouldAcceptValidJpegFile() {
        byte[] bytes = new byte[] {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0, 0x00, 0x00};
        MockMultipartFile file = new MockMultipartFile("file", "photo.jpg", "image/jpeg", bytes);

        ImageValidator.ValidatedImage result = imageValidator.validate(file);

        assertNotNull(result);
        assertEquals("jpg", result.extension());
        assertEquals("image/jpeg", result.contentType());
    }

    @Test
    void validate_shouldRejectEmptyFile() {
        MockMultipartFile file = new MockMultipartFile("file", "photo.jpg", "image/jpeg", new byte[0]);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> imageValidator.validate(file));
        assertEquals("Le fichier image est vide", ex.getMessage());
    }

    @Test
    void validate_shouldRejectFileWhenTooLarge() {
        byte[] bytes = new byte[1024];
        MockMultipartFile file = new MockMultipartFile("file", "photo.jpg", "image/jpeg", bytes);
        ImageStorageProperties properties = new ImageStorageProperties();
        properties.setMaxFileSize(DataSize.ofBytes(10));
        imageValidator = new ImageValidator(properties);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> imageValidator.validate(file));
        assertEquals("Le fichier image dépasse la taille maximale autorisée", ex.getMessage());
    }

    @Test
    void validate_shouldRejectInvalidExtension() {
        byte[] bytes = new byte[] {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0};
        MockMultipartFile file = new MockMultipartFile("file", "photo.txt", "image/jpeg", bytes);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> imageValidator.validate(file));
        assertEquals("Format d'image non pris en charge", ex.getMessage());
    }

    @Test
    void validate_shouldRejectInvalidSignature() {
        MockMultipartFile file = new MockMultipartFile("file", "photo.jpg", "image/jpeg", "not an image".getBytes());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> imageValidator.validate(file));
        assertEquals("Le fichier ne correspond pas à une image valide", ex.getMessage());
    }
}
