import com.google.common.primitives.Bytes;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class Crypto {
    public byte[] encryptMessage(byte[] message, byte[] keyBytes, IvParameterSpec iv) throws InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
        Cipher cipher = Cipher.getInstance("AES/OFB/NoPadding");
        SecretKey secretKey = new SecretKeySpec(keyBytes, "AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
        byte[] encryptedMessage = cipher.doFinal(message);
        return encryptedMessage;
    }

    public byte[] decryptMessage(byte[] encryptedMessage, byte[] keyBytes) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
        SecretKey secretKey = new SecretKeySpec(keyBytes, "AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] clearMessage = cipher.doFinal(encryptedMessage);
        return clearMessage;
    }


    public byte[] addPadding(byte[] message) {
        int count = 0;
        while ((message.length + count) % 16 != 0) {
            count = count + 1;
        }
        byte[] paddedMessage = new byte[message.length + count];
        for (int i = 0; i < message.length; i++) {
            paddedMessage[i] = message[i];
        }
        for (int i = message.length; i < message.length + count; i++) {
            paddedMessage[i] = (byte) 32;
        }
        return paddedMessage;
    }

    public byte[] encryptECB(byte[] message, byte[] keyBytes) throws NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException, IllegalBlockSizeException, InvalidKeyException {
        Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
        SecretKey secretKey = new SecretKeySpec(keyBytes, "AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);

        List<Byte> myList = new ArrayList<Byte>();
        byte[] cipherBlock = new byte[16];
        int contor = 0;
        int len = 16;
        int start = 0;
        byte[] paddedMessage = addPadding(message);
        while (contor != paddedMessage.length / 16) {
            int j = 0;
            for (int i = start; i < len; i++) {
                cipherBlock[j] = paddedMessage[i];
                j += 1;
            }
            byte[] encryptedMessage = cipher.doFinal(cipherBlock);
            for (int i = 0; i < encryptedMessage.length; i++) {
                Byte bObj = encryptedMessage[i];
                myList.add(bObj);
            }
            start += 16;
            len += 16;
            contor += 1;
        }

        byte[] finalMessage = Bytes.toArray(myList);

        return finalMessage;
    }

    public byte[] decryptECB(byte[] message, byte[] keyBytes) throws NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException, IllegalBlockSizeException, InvalidKeyException {
        Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
        SecretKey secretKey = new SecretKeySpec(keyBytes, "AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        System.out.println("cheia" + " " + secretKey.toString());
        List<Byte> myList = new ArrayList<Byte>();
        byte[] cipherBlock = new byte[16];
        int contor = 0;
        int len = 16;
        int start = 0;
        byte[] paddedMessage = addPadding(message);
        while (contor != paddedMessage.length / 16) {
            int j = 0;
            for (int i = start; i < len; i++) {
                cipherBlock[j] = paddedMessage[i];
                j += 1;
            }
            byte[] encryptedMessage = cipher.doFinal(cipherBlock);
            for (int i = 0; i < encryptedMessage.length; i++) {
                Byte bObj = encryptedMessage[i];
                myList.add(bObj);
            }
            start += 16;
            len += 16;
            contor += 1;
        }

        byte[] finalMessage = Bytes.toArray(myList);
        return finalMessage;
    }

    public byte[] encryptOFB(byte[] message, byte[] keyBytes, byte[] iv) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
        SecretKey secretKey = new SecretKeySpec(keyBytes, "AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedIV = cipher.doFinal(iv);
        byte[] paddedMessage = addPadding(message);
        List<Byte> myList = new ArrayList<Byte>();
        byte[] cipherBlock = new byte[16];
        int contor = 0;
        int len = 16;
        int start = 0;
        while (contor != paddedMessage.length / 16) {

            int j = 0;
            for (int i = start; i < len; i++) {
                cipherBlock[j] = paddedMessage[i];
                j += 1;
            }
            byte[] encryptedMessage = new byte[16];
            for (int i = 0; i < 16; i++) {
                encryptedMessage[i] = (byte) (encryptedIV[i] ^ cipherBlock[i]);
            }
            encryptedIV = cipher.doFinal(encryptedIV);
            for (int i = 0; i < encryptedMessage.length; i++) {
                Byte bObj = encryptedMessage[i];
                myList.add(bObj);
            }
            start += 16;
            len += 16;
            contor += 1;
        }

        return Bytes.toArray(myList);

    }


}
