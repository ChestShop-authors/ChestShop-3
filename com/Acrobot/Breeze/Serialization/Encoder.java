package com.Acrobot.Breeze.Serialization;

import org.apache.commons.codec.binary.Base64;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * @author Acrobot
 */
public class Encoder {
    /**
     * Encodes a serializable object
     *
     * @param serializable Object to serialize
     * @return Serialized object
     */
    public static String toString(Serializable serializable) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            ObjectOutputStream stream = new ObjectOutputStream(baos);

            stream.writeObject(serializable);
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return Base64.encodeBase64String(baos.toByteArray());
    }
}
