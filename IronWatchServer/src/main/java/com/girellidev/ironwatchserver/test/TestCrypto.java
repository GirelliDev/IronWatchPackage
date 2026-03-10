package com.girellidev.ironwatchserver.test;

import com.girellidev.ironwatchserver.security.ApiKeyCrypto;

public class TestCrypto {
    public static void main(String[] args) {
        String encrypted = ApiKeyCrypto.encrypt("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");
        System.out.println("key:" + encrypted);
    }
}