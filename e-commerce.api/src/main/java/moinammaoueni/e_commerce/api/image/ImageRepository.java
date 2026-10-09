package moinammaoueni.e_commerce.api.image;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ImageRepository extends JpaRepository<Image, Long> {

    Optional<Image> findByStorageKey(String storageKey);

    boolean existsByProductId(Long productId);

    boolean existsById(Long id);
}