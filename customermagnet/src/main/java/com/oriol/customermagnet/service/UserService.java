package com.oriol.customermagnet.service;

import com.oriol.customermagnet.domain.User;
import com.oriol.customermagnet.dto.UserDTO;
import com.oriol.customermagnet.request.LoginForm;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {

    UserDTO createUser(User user);

    UserDTO getUserByEmail(String email);

    void sendVerificationCode(UserDTO userDTO);


    UserDTO verifyCode(String email, String code);

    void resetPassword(String email);

    UserDTO veriyfPasswordKey(String key);

    void renewPassword(String key, String password, String confirmPassword);

    UserDTO verifyAccount(String key);

    UserDTO updateUserDetails(LoginForm user);
    UserDTO getUserById(Long id);

    void updatePassword(Long id, String currentPassword, String newPassword, String confirmNewPassword);

    void updateUserRole(Long id, String roleName);

    void updateAccountSettings(Long id, Boolean enabled, Boolean nonLocked);

    UserDTO toggleMfa(String email);

    void updateProfileImage(UserDTO userDTO, MultipartFile image);
}
