package server.repository.user;

import com.google.gson.annotations.Expose;
import exception.InvalidUserCredentialsException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static validation.ObjectValidator.validateNotNull;

public record UserCredentials(@Expose String email, @Expose String password) {

    private static final String EMAIL_REGEX = "^[a-zA-Z0-9]+@[a-zA-Z]+\\.[a-zA-Z]{2,}$";
    private static final String PASSWORD_REGEX = "^(?=.*[A-Za-z])(?=.*\\d).{6,}$";

    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(PASSWORD_REGEX);

    public String getCredentialsString() {
        return this.email() + " " + this.password();
    }

    public void verifyAreValid() throws InvalidUserCredentialsException {
        if (!isEmailValid(email)) {
            String message = "The email must follow the format - \"simpleEmail@example.com\"!";

            throw new InvalidUserCredentialsException(message);
        }
        if (!isPasswordValid(password)) {
            String message = "The password must contain a letter and a digit! It must be at least 6 symbols long!";

            throw new InvalidUserCredentialsException(message);
        }
    }

    private static boolean isEmailValid(String email) {
        validateNotNull(email, "email");

        Matcher matcher = EMAIL_PATTERN.matcher(email);
        return matcher.matches();
    }

    private static boolean isPasswordValid(String password) {
        validateNotNull(password, "password");

        Matcher matcher = PASSWORD_PATTERN.matcher(password);
        return matcher.matches();
    }

}