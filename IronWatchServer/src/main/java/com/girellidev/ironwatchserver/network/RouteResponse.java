package com.girellidev.ironwatchserver.network;

public class RouteResponse {

    private boolean success;
    private String message;
    private String token;
    private Object data;

    public RouteResponse() {
    }

    public RouteResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public RouteResponse(boolean success, String message, Object data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public RouteResponse(boolean success, String message, String token, Object data) {
        this.success = success;
        this.message = message;
        this.token = token;
        this.data = data;
    }

    public static RouteResponse ok(String message) {
        return new RouteResponse(true, message);
    }

    public static RouteResponse ok(String message, Object data) {
        return new RouteResponse(true, message, data);
    }

    public static RouteResponse okWithToken(String message, String token, Object data) {
        return new RouteResponse(true, message, token, data);
    }

    public static RouteResponse error(String message) {
        return new RouteResponse(false, message);
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public String getToken() {
        return token;
    }

    public Object getData() {
        return data;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setData(Object data) {
        this.data = data;
    }
}