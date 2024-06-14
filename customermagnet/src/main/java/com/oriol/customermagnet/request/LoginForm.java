package com.oriol.customermagnet.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginForm {

    private Long id;
    @NotEmpty(message = "First name can not be empty!")
    private String firstName;
    @NotEmpty(message = "Last name can not be empty!")
    private String lastName;
    @NotEmpty(message = "Email can not be empty!")
    @Email
    private String email;
    private String address;
    @Pattern(regexp = "^\\d{11}$", message = "Phone can not be empty")
    private String phone;
    private String title;
    private String bio;

}
