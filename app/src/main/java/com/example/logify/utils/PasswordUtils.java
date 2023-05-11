package com.example.logify.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordUtils {
    public static String hashPassword(String password) throws NoSuchAlgorithmException {
        // Get an instance of the SHA-256 algorithm
        MessageDigest md = MessageDigest.getInstance("SHA-256");

        // Hash the password
        md.update(password.getBytes());
        byte[] hashBytes = md.digest();

        // Convert the hashed bytes to a hexadecimal string
        StringBuilder sb = new StringBuilder();
        for (byte b : hashBytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    public static boolean verifyPassword(String password, String hashedPassword) throws NoSuchAlgorithmException {
        // Hash the entered password
        String hashedEnteredPassword = hashPassword(password);

        // Compare the hashed entered password with the stored hashed password
        return hashedEnteredPassword.equals(hashedPassword);
    }
}