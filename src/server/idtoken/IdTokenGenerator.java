package server.idtoken;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class IdTokenGenerator {

    private static final String ALGORITHM = "MD5";
    private static final String HEXADECIMAL_STRING_FORMAT = "%02x";
    private final MessageDigest md;

    public IdTokenGenerator() {
        try {
            md = MessageDigest.getInstance(ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            String message = ALGORITHM + " algorithm not available";

            throw new RuntimeException(message, e);
        }
    }

    public String generateCheckSum(String input) {
        byte[] hashBytes = md.digest(input.getBytes());

        StringBuilder hexString = new StringBuilder();
        for (byte b : hashBytes) {
            hexString.append(String.format(HEXADECIMAL_STRING_FORMAT, b));
        }

        return hexString.toString();
    }

}