package moinammaoueni.e_commerce.api.user.mapper;

import moinammaoueni.e_commerce.api.user.User;
import moinammaoueni.e_commerce.api.user.dto.UserResponseDTO;
import moinammaoueni.e_commerce.api.user.dto.UserSummaryDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserResponseDTO toResponseDTO(User user);

    UserSummaryDTO toSummaryDTO(User user);
}
