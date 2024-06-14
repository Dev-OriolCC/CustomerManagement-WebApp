package com.oriol.customermagnet.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class User {
    private Long id;
    @NotEmpty(message = "First name can not be empty!")
    private String firstName;
    @NotEmpty(message = "Last name can not be empty!")
    private String lastName;
    @NotEmpty(message = "Email can not be empty!")
    @Email
    private String email;
    @NotEmpty(message = "Password can not be empty!")
    private String password;
    private String address;
    private String phone;
    private String title;
    private String bio;
    private String imageUrl;
    private boolean enabled;
    private boolean non_locked;
    private Boolean using_nfa;
    private LocalDateTime created_date;

}
