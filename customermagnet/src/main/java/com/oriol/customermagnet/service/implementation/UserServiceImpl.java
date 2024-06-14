package com.oriol.customermagnet.service.implementation;

import com.oriol.customermagnet.domain.Role;
import com.oriol.customermagnet.domain.User;
import com.oriol.customermagnet.dto.UserDTO;
import com.oriol.customermagnet.mapper.UserDTOMapper;
import com.oriol.customermagnet.repository.RoleRepository;
import com.oriol.customermagnet.repository.UserRepository;
import com.oriol.customermagnet.request.LoginForm;
import com.oriol.customermagnet.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository<User> userRepository;
    private final RoleRepository<Role> roleRepository;

    @Override
    public UserDTO createUser(User user) {
        return mapToUserDTO(userRepository.create(user));
    }

    @Override
    public UserDTO getUserByEmail(String email) {
        return mapToUserDTO(userRepository.getUserByEmail(email));
    }

    @Override
    public void sendVerificationCode(UserDTO userDTO) {
        userRepository.sendVerificationCode(userDTO);
    }

    @Override
    public UserDTO verifyCode(String email, String code) {
        return mapToUserDTO(userRepository.verifyCode(email, code));
    }

    @Override
    public void resetPassword(String email) {
        userRepository.resetPassword(email);
    }

    @Override
    public UserDTO veriyfPasswordKey(String key) {
        return mapToUserDTO(userRepository.veriyfPasswordKey(key));
    }

    @Override
    public void renewPassword(String key, String password, String confirmPassword) {
        userRepository.renewPassword(key, password, confirmPassword);
    }

    @Override
    public UserDTO verifyAccount(String key) {
        return mapToUserDTO(userRepository.verifyAccount(key));
    }

    @Override
    public UserDTO updateUserDetails(LoginForm user) {
        return mapToUserDTO(userRepository.updateUserDetails(user));
    }

    @Override
    public UserDTO getUserById(Long id) {
        return mapToUserDTO(userRepository.get(id));
    }

    @Override
    public void updatePassword(Long id, String currentPassword, String newPassword, String confirmNewPassword) {
        userRepository.updatePassword(id, currentPassword, newPassword, confirmNewPassword);
    }

    @Override
    public void updateUserRole(Long id, String roleName) {
        roleRepository.updateUserRole(id, roleName);
    }

    @Override
    public void updateAccountSettings(Long id, Boolean enabled, Boolean nonLocked) {
        userRepository.updateAccountSettings(id, enabled, nonLocked);
    }

    @Override
    public UserDTO toggleMfa(String email) {
        return mapToUserDTO(userRepository.toggleMfa(email));
    }

    @Override
    public void updateProfileImage(UserDTO userDTO, MultipartFile image) {
        userRepository.updateProfileImage(userDTO, image);
    }

    private UserDTO mapToUserDTO(User user) {
        return UserDTOMapper.fromUser(user, roleRepository.getRoleByUserId(user.getId()));
    }

}
