package com.girellidev.ironwatchserver.security;

public class LoginQrPayload {

    private final String code;

    public LoginQrPayload(String code, String login, String password) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }


    public String toJson() {
        return "{"
                + "\"code\":\"" + escape(code) + "\","
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