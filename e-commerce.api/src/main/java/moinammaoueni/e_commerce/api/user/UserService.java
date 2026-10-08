package moinammaoueni.e_commerce.api.user;

import moinammaoueni.e_commerce.api.user.dto.UserResponseDTO;
import moinammaoueni.e_commerce.api.user.dto.UserSummaryDTO;

public interface UserService {

    UserResponseDTO getCurrentUserProfile(Long userId);

    UserSummaryDTO getCurrentUserSummary(Long userId);
}
