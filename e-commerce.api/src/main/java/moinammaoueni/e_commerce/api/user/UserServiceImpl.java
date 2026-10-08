package moinammaoueni.e_commerce.api.user;

import moinammaoueni.e_commerce.api.common.exception.ResourceNotFoundException;
import moinammaoueni.e_commerce.api.user.dto.UserResponseDTO;
import moinammaoueni.e_commerce.api.user.dto.UserSummaryDTO;
import moinammaoueni.e_commerce.api.user.mapper.UserMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDTO getCurrentUserProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable avec l'id : " + userId));
        return userMapper.toResponseDTO(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserSummaryDTO getCurrentUserSummary(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable avec l'id : " + userId));
        return userMapper.toSummaryDTO(user);
    }
}
