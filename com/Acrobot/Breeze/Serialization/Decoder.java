package com.Acrobot.Breeze.Serialization;

import org.apache.commons.codec.binary.Base64;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

/**
 * @author Acrobot
 */
public class Decoder {
    /**
     * Decodes an object from string
     *
     * @param string String to decode
     * @return Object returned
     */
    public static Object fromString(String string) {
        byte[] data = Base64.decodeBase64(string);

        try {
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
            Object o = ois.readObject();
            ois.close();

            return o;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }
}
