package com.internlink.internlink.security;

import java.util.Map;

public class OAuth2UserInfo {

    private final Map<String, Object> attributes;

    public OAuth2UserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public String getEmail() {
        // Google uses "email", Microsoft uses "mail" or "userPrincipalName"
        if (attributes.containsKey("email")) {
            return (String) attributes.get("email");
        }
        if (attributes.containsKey("mail")) {
            return (String) attributes.get("mail");
        }
        if (attributes.containsKey("userPrincipalName")) {
            return (String) attributes.get("userPrincipalName");
        }
        return null;
    }

    public String getName() {
        if (attributes.containsKey("name")) {
            return (String) attributes.get("name");
        }
        if (attributes.containsKey("displayName")) {
            return (String) attributes.get("displayName");
        }
        return "User";
    }

    public String getPicture() {
        return (String) attributes.getOrDefault("picture", null);
    }
}