package com.girellidev.ironwatchserver.security;

public class LoginQrPayload {

    private final String code;
    private final String login;
    private final String password;

    public LoginQrPayload(String code, String login, String password) {
        this.code = code;
        this.login = login;
        this.password = password;
    }

    public String getCode() {
        return code;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public String toJson() {
        return "{"
                + "\"code\":\"" + escape(code) + "\","
                + "\"login\":\"" + escape(login) + "\","
                + "\"password\":\"" + escape(password) + "\""
                + "}";
    }

    private String escape(String value) {
        if (value == null) {
            return "";
        }

        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"");
    }
}