package com.oriol.customermagnet.repository.implementation;

import com.oriol.customermagnet.domain.Role;
import com.oriol.customermagnet.exception.ApiException;
import com.oriol.customermagnet.repository.RoleRepository;
import com.oriol.customermagnet.rowmapper.RoleRowMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;

import static com.oriol.customermagnet.enumeration.RoleType.ROLE_USER;
import static com.oriol.customermagnet.query.RoleQuery.*;

@Repository
@RequiredArgsConstructor
@Slf4j
public class RoleRepositoryImpl implements RoleRepository<Role> {
    private final NamedParameterJdbcTemplate jdbc;

    @Override
    public Role create(Role data) {
        return null;
    }

    @Override
    public Collection<Role> list(int page, int pageSize) {
        return null;
    }

    @Override
    public Role get(Long id) {
        return null;
    }

    @Override
    public Role update(Role data) {
        return null;
    }

    @Override
    public Boolean delete(Long id) {
        return null;
    }

    @Override
    public void addRoleToUser(Long userId, String roleName) {
        log.info("RoleRepository: Adding role {} to user {}.",roleName, userId);
        try {
            Role role = jdbc.queryForObject(SELECT_ROLE_BY_NAME_QUERY, Map.of("name", roleName), new RoleRowMapper());
            jdbc.update(INSERT_ROLE_TO_USER_QUERY, Map.of("userId", userId, "roleId", Objects.requireNonNull(role).getId()));
        } catch (EmptyResultDataAccessException exception) {
            throw new ApiException("No role found by name: " + ROLE_USER.name());
        } catch (Exception exception) {
            throw new ApiException("RoleRepository. An error has occurred. Please, try again. " );
        }
    }

    @Override
    public Role getRoleByUserId(Long userId) {
        try {
            Role role = jdbc.queryForObject(SELECT_ROLE_BY_USER_ID_QUERY, Map.of("id", userId), new RoleRowMapper());
            return role;
        } catch (EmptyResultDataAccessException exception) {
            throw new ApiException("No role found by name: " + ROLE_USER.name());
        } catch (Exception exception) {
            throw new ApiException("RoleRepository. An error has occurred. Please, try again. " );
        }
    }

    @Override
    public Role getRoleByUserEmail(String email) {
        return null;
    }

    @Override
    public void updateUserRole(Long userId, String roleName) {
        try {
            Role role = jdbc.queryForObject(SELECT_ROLE_BY_NAME_QUERY, Map.of("name", roleName), new RoleRowMapper());
            jdbc.update(UPDATE_ROLE_TO_USER_BY_ID, Map.of("userId", userId, "roleId", Objects.requireNonNull(role).getId()));
        } catch (EmptyResultDataAccessException exception) {
            throw new ApiException("No role found by name: " + roleName);
        } catch (Exception exception) {
            throw new ApiException("RoleRepository. An error has occurred. Please, try again. " );
        }
    }

    @Override
    public Collection<Role> list() {
        try {
            return jdbc.query(SELECT_ROLES_QUERY, new RoleRowMapper());
        } catch (Exception exception) {
            throw new ApiException("RoleRepository. An error has occurred. Please, try again. " );
        }
    }


}
