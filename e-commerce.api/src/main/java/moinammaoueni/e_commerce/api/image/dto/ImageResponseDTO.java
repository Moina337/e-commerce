package moinammaoueni.e_commerce.api.image.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ImageResponseDTO {

    private Long id;
    private String originalFilename;
    private String contentType;
    private Long size;
    private String url;
    private LocalDateTime createdAt;
}
