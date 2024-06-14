package com.oriol.customermagnet.service.implementation;

import com.oriol.customermagnet.domain.Role;
import com.oriol.customermagnet.repository.RoleRepository;
import com.oriol.customermagnet.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
    private final RoleRepository<Role> roleRepository;
    @Override
    public Role getRoleByUserId(Long userId) {
        return roleRepository.getRoleByUserId(userId);
    }

    @Override
    public Collection<Role> getRoles() {
        return roleRepository.list();
    }
}
