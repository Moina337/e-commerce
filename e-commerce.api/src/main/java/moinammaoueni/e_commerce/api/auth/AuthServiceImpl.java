package moinammaoueni.e_commerce.api.auth;

import moinammaoueni.e_commerce.api.auth.dto.AuthResponseDTO;
import moinammaoueni.e_commerce.api.auth.dto.LoginRequestDTO;
import moinammaoueni.e_commerce.api.auth.dto.RegisterRequestDTO;
import moinammaoueni.e_commerce.api.common.exception.DuplicateResourceException;
import moinammaoueni.e_commerce.api.common.exception.ResourceNotFoundException;
import moinammaoueni.e_commerce.api.security.JwtService;
import moinammaoueni.e_commerce.api.user.Role;
import moinammaoueni.e_commerce.api.user.User;
import moinammaoueni.e_commerce.api.user.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthServiceImpl(UserRepository userRepository,
                          PasswordEncoder passwordEncoder,
                          AuthenticationManager authenticationManager,
                          JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @Override
    @Transactional
    public AuthResponseDTO register(RegisterRequestDTO request) {
        if (userRepository.existsByEmail(request.getEmail().trim())) {
            throw new DuplicateResourceException("Un compte existe déjà pour cet email");
        }

        User user = new User();
        user.setName(request.getName().trim());
        user.setEmail(request.getEmail().trim());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.CLIENT);
        user.setEnabled(true);

        User savedUser = userRepository.save(user);
        String token = jwtService.generateToken(savedUser.getEmail(), savedUser.getRole().name());

        AuthResponseDTO response = new AuthResponseDTO();
        response.setAccessToken(token);
        response.setTokenType("Bearer");
        response.setExpiresIn(jwtService.getExpiration());
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResponseDTO login(LoginRequestDTO request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable"));

            if (!authentication.isAuthenticated()) {
                throw new BadCredentialsException("Identifiants invalides");
            }

            String token = jwtService.generateToken(user.getEmail(), user.getRole().name());

            AuthResponseDTO response = new AuthResponseDTO();
            response.setAccessToken(token);
            response.setTokenType("Bearer");
            response.setExpiresIn(jwtService.getExpiration());
            return response;
        } catch (BadCredentialsException ex) {
            throw new BadCredentialsException("Identifiants invalides");
        }
    }
}
