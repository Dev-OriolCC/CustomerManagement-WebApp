package com.oriol.customermagnet.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResetPasswordRequest {
    private Long userId;
    @NotEmpty(message = "Password can not be empty!")
    private String password;
    @NotEmpty(message = "Confirm password can not be empty!")
    private String confirmPassword;

}
