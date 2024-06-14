package com.oriol.customermagnet.query;

public class UserQuery {
    public static final String COUNT_USER_EMAIL_QUERY = "SELECT COUNT(*) FROM Users WHERE email = :email";
    public static final String INSERT_USER_QUERY = "INSERT INTO Users (first_name, last_name, email, password) " +
            "VALUES(:firstName, :lastName, :email, :password)";
    public static final String INSERT_ACCOUNT_VERIFICATION_QUERY = "INSERT INTO AccountVerifications (user_id, url) " +
            "VALUES(:user_id, :url)";

    public static final String SELECT_USER_BY_EMAIL_QUERY = "SELECT * FROM Users WHERE email = :email";
    public static final String  DELETE_VERIFICATION_CODE_BY_USER_ID_QUERY = "DELETE FROM TwoFactorVerifications WHERE user_id = :id";
    public static final String INSERT_VERIFICATION_CODE_QUERY = "INSERT INTO TwoFactorVerifications (user_id, code, expiration_date) " +
            "VALUES(:userId, :code, :expirationDate)";
    public static final String SELECT_USER_BY_USER_CODE_QUERY = "SELECT * FROM Users WHERE id = (SELECT user_id FROM TwoFactorVerifications WHERE code = :code)";
    public static final String DELETE_CODE_QUERY = "DELETE FROM TwoFactorVerifications WHERE code = :code";
    public static final String CHECK_VERIFICATION_EXPIRED_QUERY = "SELECT expiration_date < NOW() FROM TwoFactorVerifications WHERE code = :code ";
    public static final String DELETE_RESET_PASSWORD_URL_BY_USER_ID_QUERY = "DELETE FROM ResetPasswordVerifications WHERE user_id = :id";
    public static final String INSERT_RESET_PASSWORD_URL_QUERY = "INSERT INTO ResetPasswordVerifications (user_id, url, expiration_date) VALUES(:userId, :url, :expirationDate)";
    public static final String SELECT_USER_BY_PASSWORD_URL_QUERY = "SELECT * FROM Users WHERE id = (SELECT user_id from ResetPasswordVerifications WHERE url = :url )";
    public static final String SELECT_EXPIRATION_BY_URL_QUERY = "SELECT expiration_date < NOW() AS is_expired FROM ResetPasswordVerifications WHERE url = :url";
    public static final String INSERT_RENEW_PASSWORD_QUERY = "UPDATE Users SET password = :password WHERE id = (SELECT user_id from ResetPasswordVerifications WHERE url = :url)";
    public static final String DELETE_RESET_PASSWORD_URL_QUERY = "DELETE FROM ResetPasswordVerifications WHERE url = :url";
    public static final String SELECT_USER_BY_ACCOUNT_VERIFY_URL_QUERY = "SELECT * FROM Users WHERE id = (SELECT user_id from AccountVerifications WHERE url = :url)";
    public static final String UPDATE_ENABLE_ACCOUNT_BY_ID_QUERY = "UPDATE Users SET enabled = :enable WHERE id = :id";
    public static final String UPDATE_USER_DETAILS_QUERY = "UPDATE Users SET first_name = :firstName, last_name = :lastName, email = :email, address = :address, phone = :phone, title = :title, bio = :bio WHERE id = :id ";
    public static final String SELECT_USER_BY_ID_QUERY = "SELECT * FROM Users WHERE id = :id";
    public static final String UPDATE_USER_PASSWORD_BY_ID_QUERY = "UPDATE Users SET password = :password WHERE id = :id";
    public static final String UPDATE_SETTINGS_TO_USER_BY_ID_QUERY = "UPDATE Users SET enabled = :enabled, non_locked = :non_locked WHERE id = :userId";
    public static final String UPDATE_MFA_TO_USER_BY_EMAIL_QUERY = "UPDATE Users SET using_nfa = :isMfa WHERE email = :email";
    public static final String UPDATE_IMAGE_TO_USER_BY_EMAIL_QUERY = "UPDATE Users SET image_url = :imageUrl WHERE email = :email";
}
