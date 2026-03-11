package com.girellidev.ironwatchserver.test;

import com.girellidev.ironwatchserver.security.PasswordHasher;

public class TestHash {
    public static void main(String[] args) {
        System.out.println(PasswordHasher.hash("Kv13013+"));
    }
}