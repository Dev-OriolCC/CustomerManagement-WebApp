package com.oriol.customermagnet.resource;

import com.oriol.customermagnet.domain.HttpResponse;
import com.oriol.customermagnet.domain.User;
import com.oriol.customermagnet.domain.UserPrincipal;
import com.oriol.customermagnet.dto.UserDTO;
import com.oriol.customermagnet.enumeration.EventType;
import com.oriol.customermagnet.event.NewUserEvent;
import com.oriol.customermagnet.exception.ApiException;
import com.oriol.customermagnet.provider.TokenProvider;
import com.oriol.customermagnet.request.AccountSettingsForm;
import com.oriol.customermagnet.request.LoginForm;
import com.oriol.customermagnet.request.LoginRequest;
import com.oriol.customermagnet.request.UpdatePasswordForm;
import com.oriol.customermagnet.service.EventService;
import com.oriol.customermagnet.service.RoleService;
import com.oriol.customermagnet.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Map;
import static com.oriol.customermagnet.mapper.UserDTOMapper.toUser;
import static com.oriol.customermagnet.utils.ExceptionUtils.processError;
import static com.oriol.customermagnet.utils.UserUtils.getAuthenticatedUser;
import static com.oriol.customermagnet.utils.UserUtils.getLoggedInUser;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.MediaType.IMAGE_PNG_VALUE;
import static org.springframework.security.authentication.UsernamePasswordAuthenticationToken.unauthenticated;

@RestController
@RequestMapping(path = "/api/v1/user")
@RequiredArgsConstructor
public class UserResource {

    private final UserService userService;
    private final RoleService roleService;
    private final EventService eventService;
    private final AuthenticationManager authenticationManager;
    private final TokenProvider tokenProvider;
    private final HttpServletRequest request;
    private final HttpServletResponse response;
    private final ApplicationEventPublisher publisher; // 1.

    private static final String TOKEN_PREFIX = "Bearer ";
    @PostMapping("/login")
    public ResponseEntity<HttpResponse> login(@RequestBody @Valid LoginRequest loginRequest) {
        UserDTO userDTO = authenticate(loginRequest.getEmail(), loginRequest.getPassword());
        return userDTO.isUsing_nfa() ? sendVerificationCode(userDTO) : sendResponse(userDTO);
    }


    private UserDTO authenticate(String email, String password) {
        try {
            if(userService.getUserByEmail(email) != null) {
                publisher.publishEvent(new NewUserEvent(EventType.LOGIN_ATTEMPT, email));
            }
            Authentication authentication = authenticationManager.authenticate(unauthenticated(email, password));
            UserDTO loggedInUser = getLoggedInUser(authentication);
            if(!loggedInUser.isUsing_nfa()) {
                publisher.publishEvent(new NewUserEvent(EventType.LOGIN_ATTEMPT_SUCCESS, email));
            }
            return loggedInUser;
        } catch (Exception e) {
            publisher.publishEvent(new NewUserEvent(EventType.LOGIN_ATTEMPT_FAILED, email));
            //processError(request, response, e);
            throw new ApiException(e.getMessage());
        }
    }


    @PostMapping("/register")
    public ResponseEntity<HttpResponse> create(@RequestBody @Valid User user) {
        UserDTO userDTO = userService.createUser(user);
        return ResponseEntity.created(getUri()).body(
                HttpResponse.builder()
                        .timestamp(LocalDateTime.now().toString())
                        .data(Map.of("user", userDTO))
                        .message("User created")
                        .status(HttpStatus.CREATED)
                        .statusCode(HttpStatus.CREATED.value())
                        .build());
    }
    @GetMapping("/profile")
    public ResponseEntity<HttpResponse> profile(Authentication authentication) {
        //UserDTO userDTO = userService.getUserByEmail(getAuthenticatedUser(authentication).getEmail());
        UserDTO userDTO = userService.getUserByEmail("walterwhite@hotmail.com");

        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .timestamp(LocalDateTime.now().toString())
                        .data(Map.of(
                                "user", userDTO,
                                "roles", roleService.getRoles(),
                                "events", eventService.getUserEventsByUserId(userDTO.getId())
                        ))
                        .message("User Retrieved")
                        .status(HttpStatus.OK)
                        .statusCode(HttpStatus.OK.value())
                        .build());
    }

    @PatchMapping("/update")
    public ResponseEntity<HttpResponse> update(@RequestBody @Valid LoginForm user) {
        UserDTO userDTO = userService.updateUserDetails(user);
        publisher.publishEvent(new NewUserEvent(EventType.PROFILE_UPDATE, userDTO.getEmail()));
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .timestamp(LocalDateTime.now().toString())
                        .data(Map.of(
                                "user", userDTO
                        ))
                        .message("User updated")
                        .status(HttpStatus.OK)
                        .statusCode(HttpStatus.OK.value())
                        .build());
    }
    @PatchMapping("/update/password")
    public ResponseEntity<HttpResponse> updatePassword(Authentication authentication, @RequestBody @Valid UpdatePasswordForm form) {
        UserDTO userDTO = getAuthenticatedUser(authentication);
        userService.updatePassword(userDTO.getId(), form.getCurrentPassword(), form.getNewPassword(), form.getConfirmNewPassword());
        publisher.publishEvent(new NewUserEvent(EventType.PASSWORD_UPDATE, userDTO.getEmail()));
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .timestamp(LocalDateTime.now().toString())
                        .data(Map.of(
                                "user", userService.getUserById(userDTO.getId()),
                                "roles", roleService.getRoles(),
                                "events", eventService.getUserEventsByUserId(userDTO.getId())
                        ))
                        .message("Password updated successfully")
                        .status(HttpStatus.OK)
                        .statusCode(HttpStatus.OK.value())
                        .build());
    }

    @PatchMapping("/update/role/{roleName}")
    public ResponseEntity<HttpResponse> updateUserRole(Authentication authentication, @PathVariable("roleName") String roleName ) {
        UserDTO userDTO = getAuthenticatedUser(authentication);
        userService.updateUserRole(userDTO.getId(), roleName);
        publisher.publishEvent(new NewUserEvent(EventType.ROLE_UPDATE, userDTO.getEmail()));
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .timestamp(LocalDateTime.now().toString())
                        .data(Map.of(
                                "user", userService.getUserById(userDTO.getId()),
                                "roles", roleService.getRoles(),
                                "events", eventService.getUserEventsByUserId(userDTO.getId())
                        ))
                        .message("Role updated successfully")
                        .status(HttpStatus.OK)
                        .statusCode(HttpStatus.OK.value())
                        .build());
    }
    @PatchMapping("/update/account/settings")
    public ResponseEntity<HttpResponse> updateAccountSettings(Authentication authentication, @RequestBody @Valid AccountSettingsForm accountSettingsForm) {
        UserDTO userDTO = getAuthenticatedUser(authentication);
        userService.updateAccountSettings(userDTO.getId(), accountSettingsForm.getEnabled(), accountSettingsForm.getNon_locked());
        publisher.publishEvent(new NewUserEvent(EventType.ACCOUNT_SETTINGS_UPDATE, userDTO.getEmail()));
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .timestamp(LocalDateTime.now().toString())
                        .data(Map.of(
                                "user", userService.getUserById(userDTO.getId()),
                                "roles", roleService.getRoles(),
                                "events", eventService.getUserEventsByUserId(userDTO.getId())
                        ))
                        .message("Account Settings updated successfully")
                        .status(HttpStatus.OK)
                        .statusCode(HttpStatus.OK.value())
                        .build());
    }

    @PatchMapping("/toggleMfa")
    public ResponseEntity<HttpResponse> toggleMfa(Authentication authentication) {
        UserDTO userDTO = userService.toggleMfa(getAuthenticatedUser(authentication).getEmail());
        publisher.publishEvent(new NewUserEvent(EventType.MFA_UPDATE, userDTO.getEmail()));
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .timestamp(LocalDateTime.now().toString())
                        .data(Map.of(
                                "user", userDTO,
                                "roles", roleService.getRoles(),
                                "events", eventService.getUserEventsByUserId(userDTO.getId())
                        ))
                        .message("MFA updated successfully")
                        .status(HttpStatus.OK)
                        .statusCode(HttpStatus.OK.value())
                        .build());
    }

    @PatchMapping("/update/profile/image")
    public ResponseEntity<HttpResponse> updateProfileImage(Authentication authentication, @RequestParam("image") MultipartFile image) {
        UserDTO userDTO = getAuthenticatedUser(authentication);
        userService.updateProfileImage(userDTO, image);
        publisher.publishEvent(new NewUserEvent(EventType.PROFILE_PICTURE_UPDATE, userDTO.getEmail()));
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .timestamp(LocalDateTime.now().toString())
                        .data(Map.of(
                                "user", userService.getUserById(userDTO.getId()),
                                "roles", roleService.getRoles(),
                                "events", eventService.getUserEventsByUserId(userDTO.getId())
                        ))
                        .message("Profile image updated successfully")
                        .status(HttpStatus.OK)
                        .statusCode(HttpStatus.OK.value())
                        .build());
    }
    @GetMapping(value = "/image/{imageName}", produces = IMAGE_PNG_VALUE)
    public byte[] getUserProfileImage(@PathVariable("imageName") String imageName) throws IOException {
        return Files.readAllBytes(Paths.get(System.getProperty("user.home") + "/Downloads/images/"+ imageName));
    }
    @GetMapping("/resetpassword/{email}")
    public ResponseEntity<HttpResponse> resetPassword(@PathVariable("email") String email) {
        userService.resetPassword(email);
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .timestamp(LocalDateTime.now().toString())
                        .message("Email sent at: "+email+". \n Please check your email to continue.")
                        .status(HttpStatus.OK)
                        .statusCode(HttpStatus.OK.value())
                        .build());
    }

    @GetMapping("/verify/password/{key}")
    public ResponseEntity<HttpResponse> veriyfPasswordKey(@PathVariable("key") String key) {
        UserDTO userDTO = userService.veriyfPasswordKey(key);
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .timestamp(LocalDateTime.now().toString())
                        .data(Map.of("user", userDTO))
                        .message("Password verified.")
                        .status(HttpStatus.OK)
                        .statusCode(HttpStatus.OK.value())
                        .build());
    }
    @PostMapping("/reset/password/{key}/{password}/{confirmPassword}")
    public ResponseEntity<HttpResponse> resetPassword(@PathVariable("key") String key, @PathVariable("password") String password,
                                                     @PathVariable("confirmPassword") String confirmPassword) {
        userService.renewPassword(key, password, confirmPassword);
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .timestamp(LocalDateTime.now().toString())
                        .message("Password renewed successfully.")
                        .status(HttpStatus.OK)
                        .statusCode(HttpStatus.OK.value())
                        .build());
    }
    @GetMapping("/verify/code/{email}/{code}")
    public ResponseEntity<HttpResponse> verifyCode(@PathVariable("email") String email, @PathVariable("code") String code) {
        UserDTO userDTO = userService.verifyCode(email, code);
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .timestamp(LocalDateTime.now().toString())
                        .data(Map.of(
                                "user", userDTO,
                                "access_token", tokenProvider.createAccessToken(getUserPrincipal(userDTO)),
                                "refresh_token", tokenProvider.createRefreshToken(getUserPrincipal(userDTO))
                        ))
                        .message("Login successful")
                        .status(HttpStatus.OK)
                        .statusCode(HttpStatus.OK.value())
                        .build());
    }

    @GetMapping("/verify/account/{key}")
    public ResponseEntity<HttpResponse> verifyAccount(@PathVariable("key") String key) {
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .timestamp(LocalDateTime.now().toString())
                        .message(userService.verifyAccount(key).isEnabled() ? "Account already verified" : "Account verified")
                        .status(HttpStatus.OK)
                        .statusCode(HttpStatus.OK.value())
                        .build());
    }

    @GetMapping("/refresh/token")
    public ResponseEntity<HttpResponse> refreshToken(HttpServletRequest request) {
        if(isHeaderAndTokenValid(request)) {
            String token = request.getHeader(AUTHORIZATION).substring(TOKEN_PREFIX.length());
            System.out.println(token);
            UserDTO userDTO = userService.getUserById(tokenProvider.getSubject(token, request));
            return ResponseEntity.ok().body(
                    HttpResponse.builder()
                            .timestamp(LocalDateTime.now().toString())
                            .data(Map.of(
                                    "user", userDTO,
                                    "access_token", tokenProvider.createAccessToken(getUserPrincipal(userDTO)),
                                    "refresh_token", token)
                            )
                            .message("Refresh token successful.")
                            .status(HttpStatus.OK)
                            .statusCode(HttpStatus.OK.value())
                            .build());
        }
        return ResponseEntity.badRequest().body(
                HttpResponse.builder()
                        .timestamp(LocalDateTime.now().toString())
                        .message("Token not valid or expired.")
                        .status(HttpStatus.BAD_REQUEST)
                        .statusCode(HttpStatus.BAD_REQUEST.value())
                        .build());
    }

    private boolean isHeaderAndTokenValid(HttpServletRequest request) {
        return request.getHeader(AUTHORIZATION) != null
                && request.getHeader(AUTHORIZATION).startsWith(TOKEN_PREFIX)
                && tokenProvider.isTokenValid(tokenProvider.getSubject(request.getHeader(AUTHORIZATION).substring(TOKEN_PREFIX.length()), request),
                request.getHeader(AUTHORIZATION).substring(TOKEN_PREFIX.length())
        );
    }


    @RequestMapping("/error")
    public ResponseEntity<HttpResponse> handleError(HttpServletRequest request) {
        System.out.println("Llegue! ");
        return new ResponseEntity<>(HttpResponse.builder()
                .timestamp(LocalDateTime.now().toString())
                .reason("Not Found: "+request+"\n Please check the url.")
                .status(NOT_FOUND)
                .statusCode(NOT_FOUND.value())
                .build(), NOT_FOUND);
    }
    private URI getUri() {
        return URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/user/get/<userId>").toUriString());
    }
    private ResponseEntity<HttpResponse> sendResponse(UserDTO userDTO) {
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .timestamp(LocalDateTime.now().toString())
                        .data(Map.of(
                                "user", userDTO,
                                "access_token", tokenProvider.createAccessToken(getUserPrincipal(userDTO)),
                                "refresh_token", tokenProvider.createRefreshToken(getUserPrincipal(userDTO))
                        ))
                        .message("Login successful")
                        .status(HttpStatus.OK)
                        .statusCode(HttpStatus.OK.value())
                        .build());
    }

    private UserPrincipal getUserPrincipal(UserDTO userDTO) {
        return new UserPrincipal(toUser(userService.getUserByEmail(userDTO.getEmail())), roleService.getRoleByUserId(userDTO.getId()));
    }

    private ResponseEntity<HttpResponse> sendVerificationCode(UserDTO userDTO) {
        userService.sendVerificationCode(userDTO);
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .timestamp(LocalDateTime.now().toString())
                        .data(Map.of("user", userDTO))
                        .message("Verification Code Sent!")
                        .status(HttpStatus.OK)
                        .statusCode(HttpStatus.OK.value())
                        .build());
    }

}
