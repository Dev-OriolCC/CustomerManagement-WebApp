package com.oriol.customermagnet.enumeration;

public enum EventType {
    LOGIN_ATTEMPT("You tried to log in"),
    LOGIN_ATTEMPT_FAILED("You tried to log in and failed"),
    LOGIN_ATTEMPT_SUCCESS("You tried to login and suceed"),
    PROFILE_UPDATE("You updated your profile information"),
    PROFILE_PICTURE_UPDATE("You updated your profile picture"),
    ROLE_UPDATE("You updated your role"),
    ACCOUNT_SETTINGS_UPDATE("You updated your account settings"),
    PASSWORD_UPDATE("You updated your password"),
    MFA_UPDATE("You updated you MFA settings");

    private String description;

    public String getDescription() {
        return description;
    }

    EventType(String description) {
        this.description = description;
    }
}
