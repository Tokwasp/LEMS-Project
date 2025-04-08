package lems.cowshed.domain.mail;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class RandomIntCodeGenerator {
    private static final SecureRandom RANDOM = new SecureRandom();

    public String generateRandomString() {
        StringBuilder sb = new StringBuilder(10);
        for (int i = 0; i < 10; i++) {
            int randomNum = RANDOM.nextInt(10);
            sb.append(randomNum);
        }
        return sb.toString();
    }
}
