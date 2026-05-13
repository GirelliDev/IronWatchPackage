package com.girellidev.ironwatchserver.security;

import com.girellidev.ironwatchserver.logger.LoggerService;

public class SecurityException extends RuntimeException {
    private static final LoggerService logger = new LoggerService();
    public SecurityException(String message) {
        super(message);
    logger.erro("Security","message");
    }
}