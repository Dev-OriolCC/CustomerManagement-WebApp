package com.oriol.customermagnet.repository.implementation;

import com.oriol.customermagnet.domain.Role;
import com.oriol.customermagnet.domain.User;
import com.oriol.customermagnet.domain.UserPrincipal;
import com.oriol.customermagnet.dto.UserDTO;
import com.oriol.customermagnet.enumeration.VerificationType;
import com.oriol.customermagnet.exception.ApiException;
import com.oriol.customermagnet.repository.RoleRepository;
import com.oriol.customermagnet.repository.UserRepository;
import com.oriol.customermagnet.request.LoginForm;
import com.oriol.customermagnet.rowmapper.UserRowMapper;
import com.oriol.customermagnet.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.CompletableFuture;

import static com.oriol.customermagnet.constant.Constants.DATE_FORMAT;
import static com.oriol.customermagnet.enumeration.RoleType.ROLE_USER;
import static com.oriol.customermagnet.enumeration.VerificationType.ACCOUNT;
import static com.oriol.customermagnet.enumeration.VerificationType.PASSWORD;
import static com.oriol.customermagnet.query.UserQuery.*;
import static com.oriol.customermagnet.utils.SmsUtils.sendSMS;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static java.util.Objects.requireNonNull;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.apache.commons.lang3.time.DateUtils.addDays;

@Repository
@RequiredArgsConstructor
@Slf4j
public class UserRepositoryImpl implements UserRepository<User>, UserDetailsService {
    private final NamedParameterJdbcTemplate jdbc;
    private final RoleRepository<Role> roleRepository;
    private final BCryptPasswordEncoder encoder;
    private final EmailService emailService;

    @Override
    public User create(User user) {
        // 1. Check email
        if (checkEmailCount(user.getEmail().trim().toLowerCase()) > 0 ) throw new ApiException("Email already in use");
        //2. Save new user
        try {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            SqlParameterSource parameters = getSqlParameterSource(user);
            jdbc.update(INSERT_USER_QUERY, parameters, keyHolder);
            user.setId(requireNonNull(keyHolder.getKey()).longValue());
            //3. Add role to user
            roleRepository.addRoleToUser(user.getId(), ROLE_USER.name());
            //4. Send verf. url
            String verificationUrl = getVerificationUrl(UUID.randomUUID().toString(),ACCOUNT.getType());
            //5. Save url
            jdbc.update(INSERT_ACCOUNT_VERIFICATION_QUERY,Map.of("user_id",user.getId(),"url",verificationUrl));
            //6. Send email to user with url
            //emailService.sendVerificationEmail(user.getFirst_name(), user.getEmail(), verificationUrl, ACCOUNT);
            sendEmail(user.getFirstName(), user.getEmail(), verificationUrl, ACCOUNT);
            user.setEnabled(false);
            user.setNon_locked(true);
            // 7. Return user
            return user;
        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new ApiException("An error has occurred. Please, try again. ");
        }
    }

    private void sendEmail(String firstName, String email, String verificationUrl, VerificationType verificationType) {
        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            try {
                log.error("1. Send email....");
                emailService.sendVerificationEmail(firstName, email, verificationUrl, verificationType);
                log.error("Email sent to: "+email);
            } catch (Exception exception) {
                throw new ApiException("Error. Could not send email.");
            }
        });
    }


    @Override
    public Collection<User> list(int page, int pageSize) {
        return null;
    }

    @Override
    public User get(Long id) {
        try {
            return jdbc.queryForObject(SELECT_USER_BY_ID_QUERY, Map.of("id", id), new UserRowMapper());
        } catch (EmptyResultDataAccessException exception) {
            throw new ApiException("No user found by id: "+ id);
        } catch (Exception exception) {
            throw new ApiException("An error has occurred. Please, try again. " );
        }
    }

    @Override
    public User update(User data) {
        return null;
    }

    @Override
    public Boolean delete(Long id) {
        return null;
    }

    /*
    * Methods defined for it's use in this repository
    * */
    private Integer checkEmailCount(String email) {
        return jdbc.queryForObject(COUNT_USER_EMAIL_QUERY, Map.of("email", email), Integer.class);
    }

    private String getVerificationUrl(String key, String type) {
        return "http://localhost:4200/api/v1/user/verify/"+type.toLowerCase()+"/"+key;
        //return ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/v1/user/verify/"+type+"/"+key).toUriString();
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = getUserByEmail(email);
        if (user == null) {
            log.error("User not found with email: {}",email);
            throw new UsernameNotFoundException("User not found.");
        } else {
            log.info("User found with email: {}", email);
            Role role = roleRepository.getRoleByUserId(user.getId());
            return new UserPrincipal(user, role);
        }
    }

    @Override
    public User getUserByEmail(String email) {
        try {
            User user = jdbc.queryForObject(SELECT_USER_BY_EMAIL_QUERY,Map.of("email",email), new UserRowMapper());
            return user;
        } catch (EmptyResultDataAccessException exception) {
            throw new ApiException("No user found by email: "+ email);
        } catch (Exception exception) {
            throw new ApiException("An error has occurred. Please, try again. " );
        }
    }

    @Override
    public void sendVerificationCode(UserDTO userDTO) {
        String expirationDate = DateFormatUtils.format(addDays(new Date(), 1), DATE_FORMAT);
        String verificationCode = randomAlphabetic(8).toUpperCase();
        try {
            jdbc.update(DELETE_VERIFICATION_CODE_BY_USER_ID_QUERY,Map.of("id",userDTO.getId()));
            jdbc.update(INSERT_VERIFICATION_CODE_QUERY,Map.of("userId",userDTO.getId(), "code", verificationCode, "expirationDate", expirationDate));
            //sendSMS(userDTO.getPhone(), "From: CustomerMagnet - Verification Code: "+verificationCode);
            log.info("VerifyCode: {}", verificationCode);
        } catch (EmptyResultDataAccessException exception) {
            throw new ApiException("No user found by email: "+ userDTO.getEmail());
        } catch (Exception exception) {
            throw new ApiException("An error has occurred. Please, try again. " );
        }

    }

    @Override
    public User verifyCode(String email, String code) {
        if (isVerificationCodeExpired(code)) throw new ApiException("Verification code expired. Please login again.");
        try {
            User userEmail = jdbc.queryForObject(SELECT_USER_BY_EMAIL_QUERY, Map.of("email", email), new UserRowMapper());
            User userCode = jdbc.queryForObject(SELECT_USER_BY_USER_CODE_QUERY, Map.of("code", code), new UserRowMapper());
            if (userEmail.getEmail().equalsIgnoreCase(userCode.getEmail())) {
                jdbc.update(DELETE_CODE_QUERY, Map.of("code", code));
                return userCode;
            } else {
                throw new ApiException("Code is invalid. Please try again.");
            }
        } catch (EmptyResultDataAccessException exception) {
            throw new ApiException("Could not find record.");
        } catch (Exception exception) {
            throw new ApiException("An error has occurred. Please, try again.");
        }
    }

    @Override
    public void resetPassword(String email) {
        if (checkEmailCount(email.trim().toLowerCase()) == 0) throw new ApiException("No user found by email: "+ email);
        try {
            String expirationDate = DateFormatUtils.format(addDays(new Date(), 1), DATE_FORMAT);
            String verificationUrl = getVerificationUrl(UUID.randomUUID().toString(), PASSWORD.getType());
            User user = getUserByEmail(email);
            //TODO TEST
            jdbc.update(DELETE_RESET_PASSWORD_URL_BY_USER_ID_QUERY,Map.of("id",user.getId()));
            jdbc.update(INSERT_RESET_PASSWORD_URL_QUERY,Map.of("userId",user.getId(), "url", verificationUrl, "expirationDate", expirationDate));
            //TODO Send Email
            sendEmail(user.getFirstName(), email, verificationUrl, PASSWORD);
            log.info("verificationUrl: {}", verificationUrl);
        } catch (Exception exception) {
            throw new ApiException("An error has occurred. Please, try again. " );
        }
    }

    @Override
    public User veriyfPasswordKey(String key) {
        if(isLinkExpired(key, PASSWORD)) throw new ApiException("This link has expired. Please try again.");
        try {
            System.out.println("Step 2.");
            User user = jdbc.queryForObject(SELECT_USER_BY_PASSWORD_URL_QUERY, Map.of("url", getVerificationUrl(key, PASSWORD.getType())), new UserRowMapper());
            //TODO: DELETE
            return user;
        } catch (EmptyResultDataAccessException exception) {
            throw new ApiException("This link is not valid. Please try again.");
        } catch (Exception exception) {
            throw new ApiException("An error has occurred. Please, try again.");
        }
    }

    @Override
    public void renewPassword(String key, String password, String confirmPassword) {
        if (!password.equals(confirmPassword)) throw new ApiException("Passwords don't match!");
        try {
            //jdbc.update(DELETE_RESET_PASSWORD_URL_BY_USER_ID_QUERY,Map.of("id",user.getId()));
            jdbc.update(INSERT_RENEW_PASSWORD_QUERY,Map.of("password", encoder.encode(password), "url", getVerificationUrl(key, PASSWORD.getType())));
            jdbc.update(DELETE_RESET_PASSWORD_URL_QUERY, Map.of("url", getVerificationUrl(key, PASSWORD.getType())));
        } catch (Exception exception) {
            throw new ApiException("An error has occurred. Please, try again.");
        }
    }

    @Override
    public void renewPassword(Long userId, String password, String confirmPassword) {
        if (!password.equals(confirmPassword)) throw new ApiException("Passwords don't match!");
        try {
            //jdbc.update(DELETE_RESET_PASSWORD_URL_BY_USER_ID_QUERY,Map.of("id",user.getId()));
            //TODO: CHECK
            System.out.println("userId"+ userId);
            System.out.println("password"+ encoder.encode(password));
            jdbc.update(UPDATE_USER_PASSWORD_BY_ID_QUERY,Map.of("id", userId,"password", encoder.encode(password)));
            //jdbc.update(DELETE_RESET_PASSWORD_URL_BY_USER_ID_QUERY, Map.of("id", userId));
        } catch (Exception exception) {
            throw new ApiException("An error has occurred. Please, try again.");
        }
    }

    @Override
    public User verifyAccount(String key) {
        try {
            User user = jdbc.queryForObject(SELECT_USER_BY_ACCOUNT_VERIFY_URL_QUERY, Map.of("url", getVerificationUrl(key, ACCOUNT.getType())), new UserRowMapper());
            jdbc.update(UPDATE_ENABLE_ACCOUNT_BY_ID_QUERY, Map.of("enable", true, "id", user.getId()));
            return user;
        } catch (EmptyResultDataAccessException exception) {
            throw new ApiException("This code is not valid. Please try again.");
        } catch (Exception exception) {
            throw new ApiException("An error has occurred. Please, try again.");
        }
    }

    @Override
    public User updateUserDetails(LoginForm user) {
        try {
            jdbc.update(UPDATE_USER_DETAILS_QUERY, updateSqlParameterSource(user));
            return get(user.getId());
        } catch (Exception exception) {
            throw new ApiException("An error has occurred. Please, try again.");
        }
    }

    @Override
    public void updatePassword(Long id, String currentPassword, String newPassword, String confirmNewPassword) {
        if(!newPassword.equals(confirmNewPassword)) throw new ApiException("Passwords don't match. Please try again.");
        User user = get(id);
        if(encoder.matches(currentPassword, user.getPassword())) {
            try {
                jdbc.update(UPDATE_USER_PASSWORD_BY_ID_QUERY, Map.of("id", id, "password", encoder.encode(newPassword)));
            } catch (Exception exception) {
                throw new ApiException("An error has occurred. Please, try again.");
            }
        } else {
            throw new ApiException("Current password doesn't match. Please try again.");
        }

    }

    @Override
    public void updateAccountSettings(Long id, Boolean enabled, Boolean nonLocked) {
        try {
            jdbc.update(UPDATE_SETTINGS_TO_USER_BY_ID_QUERY, Map.of("userId", id, "enabled", enabled, "non_locked", nonLocked));
        } catch (EmptyResultDataAccessException exception) {
            throw new ApiException("No user found. ");
        } catch (Exception exception) {
            throw new ApiException("UserRepository. An error has occurred. Please, try again. " );
        }
    }

    @Override
    public User toggleMfa(String email) {
        User user = getUserByEmail(email);
        if(user.getPhone().isBlank()) throw new ApiException("You need a phone to update MFA.");
        try {
            user.setUsing_nfa(!user.isUsing_nfa());
            jdbc.update(UPDATE_MFA_TO_USER_BY_EMAIL_QUERY, Map.of("email", email, "isMfa", user.isUsing_nfa()));
            return user;
        } catch (EmptyResultDataAccessException exception) {
            throw new ApiException("No user found. ");
        } catch (Exception exception) {
            throw new ApiException("UserRepository. An error has occurred. Please, try again. " );
        }
    }

    @Override
    public void updateProfileImage(UserDTO user, MultipartFile image) {
        String userImageUrl = setUserImageUrl(user.getEmail());
        user.setImageUrl(userImageUrl);
        saveImage(user.getEmail(), image);
        jdbc.update(UPDATE_IMAGE_TO_USER_BY_EMAIL_QUERY, Map.of("email", user.getEmail(), "imageUrl", userImageUrl ));
    }
    private String setUserImageUrl(String email) {
        return ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/v1/user/image/"+email+".png").toUriString();
    }
    private void saveImage(String email, MultipartFile image) {
        Path fileStorageLocation = Paths.get(System.getProperty("user.home") + "/Downloads/images/").toAbsolutePath().normalize();
        if(!Files.exists(fileStorageLocation)) {
            try {
                Files.createDirectories(fileStorageLocation);
            } catch (IOException e) {
                log.info("Error creating directory: {}", e.getMessage());
                throw new ApiException("Unable to create directory for image storage.");
            }
            log.info("Created Directory: {}", fileStorageLocation);
        }
        try {
            Files.copy(image.getInputStream(), fileStorageLocation.resolve(email+ ".png"), REPLACE_EXISTING);
        } catch (IOException e) {
            log.info("Error saving file: {}", e.getMessage());
            throw new ApiException("Unable to save file.");
        }
        log.info("File saved in {}", fileStorageLocation);
    }



    private SqlParameterSource getSqlParameterSource(User user) {
        return new MapSqlParameterSource()
                .addValue("firstName", user.getFirstName())
                .addValue("lastName", user.getLastName())
                .addValue("email", user.getEmail())
                .addValue("password", encoder.encode(user.getPassword()));
    }

    private SqlParameterSource updateSqlParameterSource(LoginForm user) {
        return new MapSqlParameterSource()
                .addValue("id", user.getId())
                .addValue("firstName", user.getFirstName())
                .addValue("lastName", user.getLastName())
                .addValue("email", user.getEmail())
                .addValue("address", user.getAddress())
                .addValue("phone", user.getPhone())
                .addValue("title", user.getTitle())
                .addValue("bio", user.getBio());
    }

    private Boolean isLinkExpired(String key, VerificationType verificationType) {
        try {
            System.out.println("URL"+getVerificationUrl(key, verificationType.getType()));
            String url = "http://localhost:4200/api/v1/user/verify/"+verificationType.getType().toLowerCase()+"/"+key;
            System.out.println("Temp Url: "+ url);
            //return jdbc.queryForObject(SELECT_EXPIRATION_BY_URL_QUERY, Map.of("url", getVerificationUrl(key, verificationType.getType())), Boolean.class);
            return jdbc.queryForObject(SELECT_EXPIRATION_BY_URL_QUERY, Map.of("url", url), Boolean.class);
        } catch (EmptyResultDataAccessException exception) {
            throw new ApiException("This link is not valid. Please try again.");
        } catch (Exception exception) {
            throw new ApiException("An error has occurred. Please, try again.");
        }
    }

    private Boolean isVerificationCodeExpired(String code) {
        try {
            return jdbc.queryForObject(CHECK_VERIFICATION_EXPIRED_QUERY, Map.of("code", code), Boolean.class);
        } catch (EmptyResultDataAccessException exception) {
            throw new ApiException("This code is not valid. Please login again.");
        } catch (Exception exception) {
            throw new ApiException("An error has occurred. Please, try again.");
        }

    }
}
