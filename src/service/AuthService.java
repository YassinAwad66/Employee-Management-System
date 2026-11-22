package service;

import java.util.Arrays;

public class AuthService {

    private static final String USERNAME = "admin";
    private static final String PASSWORD = "admin123";

    public boolean authenticate(String username, char[] password) {

        if (username == null || password == null) {
            return false;
        }

        boolean validUsername = USERNAME.equals(username.trim());

        boolean validPassword =
                Arrays.equals(PASSWORD.toCharArray(), password);

        return validUsername && validPassword;
    }
}

