package lems.cowshed.api.controller;

import lems.cowshed.service.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityContextUtil {
    public static Long getUserId(){
        CustomUserDetails principal = getCurrentUserDetails();
        return (principal != null) ? principal.getUserId() : null;
    }

    public static String getUserName() {
        CustomUserDetails principal = getCurrentUserDetails();
        return (principal != null) ? principal.getUsername() : null;
    }

    private static CustomUserDetails getCurrentUserDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            return (CustomUserDetails) authentication.getPrincipal();
        }
        return null;
    }
}

