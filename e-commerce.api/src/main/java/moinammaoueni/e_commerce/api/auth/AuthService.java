package moinammaoueni.e_commerce.api.auth;

import moinammaoueni.e_commerce.api.auth.dto.AuthResponseDTO;
import moinammaoueni.e_commerce.api.auth.dto.LoginRequestDTO;
import moinammaoueni.e_commerce.api.auth.dto.RegisterRequestDTO;

public interface AuthService {

    AuthResponseDTO register(RegisterRequestDTO request);

    AuthResponseDTO login(LoginRequestDTO request);
}
