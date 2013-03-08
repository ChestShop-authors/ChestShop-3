package com.Acrobot.Breeze.Utils.Encoding;

/**
 * Base62 encoding class
 *
 * @author Acrobot
 */
public class Base62 {
    private static String ALPHABET = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static int BASE = ALPHABET.length();

    /**
     * Encodes a number to Base62 string
     *
     * @param number Number to encode
     * @return Encoded number
     */
    public static String encode(int number) {
        if (number == 0) {
            return ALPHABET.substring(0, 1);
        }

        StringBuilder code = new StringBuilder(16);

        while (number > 0) {
            int remainder = number % BASE;
            number /= BASE;

            code.append(ALPHABET.charAt(remainder));
        }

        return code.reverse().toString();
    }


    /**
     * Decodes a Base62 string
     *
     * @param code Code to decode
     * @return Decoded code
     */
    public static int decode(String code) {
        int number = 0;

        for (int i = 0; i < code.length(); i++) {
            int power = code.length() - (i + 1);
            number += ALPHABET.indexOf(code.charAt(i)) * Math.pow(BASE, power);
        }

        return number;
    }


}
