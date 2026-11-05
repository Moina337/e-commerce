
package moinammaoueni.e_commerce.api.image.mapper;

import moinammaoueni.e_commerce.api.image.Image;
import moinammaoueni.e_commerce.api.image.dto.ImageResponseDTO;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ImageMapper {

    @Mapping(target = "url", source = "cloudinaryUrl")
    ImageResponseDTO toDto(Image image);

    default ImageResponseDTO toDtoWithUrl(Image image, String baseUrl) {
        return toDto(image);
    }
}
