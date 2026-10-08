package moinammaoueni.e_commerce.api.user;

import moinammaoueni.e_commerce.api.security.CurrentUser;
import moinammaoueni.e_commerce.api.user.dto.UserResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final CurrentUser currentUser;

    public UserController(UserService userService, CurrentUser currentUser) {
        this.userService = userService;
        this.currentUser = currentUser;
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> getCurrentUser() {
        User user = currentUser.get();
        return ResponseEntity.ok(userService.getCurrentUserProfile(user.getId()));
    }
}
