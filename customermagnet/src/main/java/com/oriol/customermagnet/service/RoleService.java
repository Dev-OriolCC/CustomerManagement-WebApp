package com.oriol.customermagnet.service;

import com.oriol.customermagnet.domain.Role;

import java.util.Collection;

public interface RoleService {
    Role getRoleByUserId(Long userId);

    Collection<Role> getRoles();
}
