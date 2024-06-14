package com.oriol.customermagnet.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
public class UserDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String address;
    private String phone;
    private String title;
    private String bio;
    private String imageUrl;
    private boolean enabled;
    private boolean non_locked;
    private boolean using_nfa;
    private LocalDateTime created_date;
    private String roleName;
    private String permissions;
}
