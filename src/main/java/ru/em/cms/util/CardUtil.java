package ru.em.cms.util;

import java.util.Random;

public class CardUtil {
    private static final Random RANDOM = new Random();

    public static String generateCardNumber(String bin) {
        if (bin.length() != 6) {
            throw new IllegalArgumentException("BIN must be 6 digits");
        }

        StringBuilder cardNumber = new StringBuilder(bin);

        for (int i = 0; i < 9; i++) {
            cardNumber.append(RANDOM.nextInt(10));
        }

        int checkDigit = calculateLuhnDigit(cardNumber.toString());
        cardNumber.append(checkDigit);

        return cardNumber.toString();
    }

    private static int calculateLuhnDigit(String number) {
        int sum = 0;
        boolean alternate = true;

        for (int i = number.length() - 1; i >= 0; i--) {
            int n = Character.getNumericValue(number.charAt(i));
            if (alternate) {
                n *= 2;
                if (n > 9) {
                    n -= 9;
                }
            }
            sum += n;
            alternate = !alternate;
        }

        return (10 - (sum % 10)) % 10;
    }
}
