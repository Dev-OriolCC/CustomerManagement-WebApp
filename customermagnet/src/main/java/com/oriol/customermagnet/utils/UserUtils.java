package com.oriol.customermagnet.utils;

import com.oriol.customermagnet.domain.UserPrincipal;
import com.oriol.customermagnet.dto.UserDTO;
import org.springframework.security.core.Authentication;

public class UserUtils {

    public static UserDTO getAuthenticatedUser(Authentication authentication) {
        return ((UserDTO) authentication.getPrincipal());
    }

    public static UserDTO getLoggedInUser(Authentication authentication) {
        System.out.println(authentication.getPrincipal());
        return ((UserPrincipal) authentication.getPrincipal()).getUser();
    }

}
