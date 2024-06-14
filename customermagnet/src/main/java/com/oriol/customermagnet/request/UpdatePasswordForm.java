package com.oriol.customermagnet.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdatePasswordForm {
    @NotEmpty(message = "current password can not be empty!")
    private String currentPassword;
    @NotEmpty(message = "new password can not be empty!")
    private String newPassword;
    @NotEmpty(message = "confirm new password can not be empty!")
    private String confirmNewPassword;
}
