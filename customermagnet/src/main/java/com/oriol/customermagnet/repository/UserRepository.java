package com.oriol.customermagnet.repository;

import com.oriol.customermagnet.domain.User;
import com.oriol.customermagnet.dto.UserDTO;
import com.oriol.customermagnet.request.LoginForm;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collection;

public interface UserRepository <T extends User> {
    /* Crud operations */
    T create(T data);
    Collection<T> list(int page, int pageSize);
    T get(Long id);
    T update(T data);
    Boolean delete (Long id);

    User getUserByEmail(String email);

    void sendVerificationCode(UserDTO userDTO);

    T verifyCode(String email, String code);

    void resetPassword(String email);

    T veriyfPasswordKey(String key);

    void renewPassword(String key, String password, String confirmPassword);

    T verifyAccount(String key);

    T updateUserDetails(LoginForm user);

    void updatePassword(Long id, String currentPassword, String newPassword, String confirmNewPassword);

    void updateAccountSettings(Long id, Boolean enabled, Boolean nonLocked);

    User toggleMfa(String email);

    void updateProfileImage(UserDTO userDTO, MultipartFile image);
}
