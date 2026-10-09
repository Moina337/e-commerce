package moinammaoueni.e_commerce.api.image;

import moinammaoueni.e_commerce.api.common.exception.ResourceNotFoundException;
import moinammaoueni.e_commerce.api.image.dto.ImageResponseDTO;
import moinammaoueni.e_commerce.api.image.mapper.ImageMapper;
import moinammaoueni.e_commerce.api.product.Product;
import moinammaoueni.e_commerce.api.product.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ImageServiceTest {

    @Mock
    private ImageRepository imageRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ImageStorageService imageStorageService;

    @Mock
    private ImageMapper imageMapper;

    @Mock
    private ImageValidator imageValidator;

    @InjectMocks
    private ImageServiceImpl imageService;

    @Test
    void uploadImage_shouldAcceptValidFileAndReturnDto() throws IOException {
        Product product = new Product();
        byte[] bytes = new byte[] {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0};
        MockMultipartFile file = new MockMultipartFile("file", "photo.jpg", "image/jpeg", bytes);
        Image saved = new Image();
        saved.setId(12L);
        saved.setOriginalFilename("photo.jpg");
        saved.setStorageKey("abc.jpg");
        saved.setContentType("image/jpeg");
        saved.setSize((long) bytes.length);
        saved.setProduct(product);

        ImageResponseDTO dto = new ImageResponseDTO();
        dto.setId(12L);
        dto.setOriginalFilename("photo.jpg");
        dto.setContentType("image/jpeg");
        dto.setUrl("/api/images/12/content");

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(imageValidator.validate(file)).thenReturn(new ImageValidator.ValidatedImage("jpg", "image/jpeg", "jpeg", bytes));
        when(imageStorageService.store(any(MultipartFile.class), anyString())).thenReturn("abc.jpg");
        when(imageRepository.save(any(Image.class))).thenReturn(saved);
        when(imageMapper.toDtoWithUrl(saved, "/api/images")).thenReturn(dto);

        ImageResponseDTO result = imageService.uploadImage(1L, file);

        assertNotNull(result);
        assertEquals(dto.getId(), result.getId());
        verify(imageStorageService).store(any(MultipartFile.class), anyString());
        verify(imageRepository).save(any(Image.class));
    }

    @Test
    void uploadImage_shouldThrowWhenProductDoesNotExist() {
        MockMultipartFile file = new MockMultipartFile("file", "photo.jpg", "image/jpeg", new byte[] {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0});

        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> imageService.uploadImage(99L, file));
        verifyNoInteractions(imageStorageService);
    }

    @Test
    void uploadImage_shouldDeleteStoredFileWhenDatabaseSaveFails() throws IOException {
        Product product = new Product();
        byte[] bytes = new byte[] {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0};
        MockMultipartFile file = new MockMultipartFile("file", "photo.jpg", "image/jpeg", bytes);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(imageValidator.validate(file)).thenReturn(new ImageValidator.ValidatedImage("jpg", "image/jpeg", "jpeg", bytes));
        when(imageStorageService.store(any(MultipartFile.class), anyString())).thenReturn("failed.jpg");
        when(imageRepository.save(any(Image.class))).thenThrow(new RuntimeException("db error"));
        doNothing().when(imageStorageService).delete(anyString());

        assertThrows(RuntimeException.class, () -> imageService.uploadImage(1L, file));
        verify(imageStorageService).delete(anyString());
    }

    @Test
    void productHasImage_shouldDelegateToRepository() {
        when(imageRepository.existsByProductId(4L)).thenReturn(true);

        assertTrue(imageService.productHasImage(4L));
        verify(imageRepository).existsByProductId(4L);
    }
}