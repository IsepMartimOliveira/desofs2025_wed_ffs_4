package com.example.psoft_22_23_project.utils;

import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;

public class Utils {

    // Gets the id of current authenticated user
    public static String getAuthId() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return username.split(",")[0];
    }

    public static String transformSpaces(String input) {
        String trimmed = input.trim();
        String replaced = trimmed.replaceAll("\\s+", " ");
        return replaced.replaceAll(" ", "_");
    }

    public static String sanitize(String input) {
            if (input == null) return null;
            return input.replaceAll("[\n\r\t]", "_");
        }


}
