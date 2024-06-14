package com.oriol.customermagnet.query;

public class RoleQuery {
    public static final String SELECT_ROLE_BY_NAME_QUERY = "SELECT * FROM Roles WHERE name = :name";
    public static final String INSERT_ROLE_TO_USER_QUERY = "INSERT INTO UserRoles (user_id, role_id " +
            ") VALUES (:userId, :roleId)";
    public static final String SELECT_ROLE_BY_USER_ID_QUERY = "SELECT r.id, r.name, r.permission FROM Roles r JOIN UserRoles ur ON ur.role_id = r.id JOIN Users u ON u.id = ur.user_id WHERE u.id = :id";
    public static final String SELECT_ROLES_QUERY = "SELECT * FROM Roles ORDER BY id";
    public static final String UPDATE_ROLE_TO_USER_BY_ID = "UPDATE UserRoles SET role_id = :roleId WHERE user_id = :userId";
}
