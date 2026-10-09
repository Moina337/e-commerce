package moinammaoueni.e_commerce.api.image;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ImageStorageService {

    String store(MultipartFile file, String storageKey) throws IOException;

    Resource load(String storageKey) throws IOException;

    void delete(String storageKey) throws IOException;
}
