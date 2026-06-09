package com.example.android.utils;


import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

//密码工具类
public class PasswordUtil {
    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();


    public static String encodePassword(String rawPassword) {
        return encoder.encode(rawPassword);
    }

    public static boolean matches(String rawPassword,String encodedPassword){
        return encoder.matches(rawPassword,encodedPassword);
    }
}
