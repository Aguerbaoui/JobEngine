package io.licensemanager.utilities;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * This class is dependant form SIOTH license API to check
 * Every attribute hard coded needs to be double checked as well
 **/
public class LicenseUtilities {


    public static String encrypt(String plainText, String passPhrase) {
        try {
            byte[] salt = generate128BitsOfRandomEntropy();
            byte[] iv = generate128BitsOfRandomEntropy();

            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            PBEKeySpec pbeKeySpec = new PBEKeySpec(passPhrase.toCharArray(), salt, 1000, 128);
            SecretKey secretKey = factory.generateSecret(pbeKeySpec);
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getEncoded(), "AES");

            AlgorithmParameterSpec ivSpec = new IvParameterSpec(iv);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivSpec);
            byte[] ciphertext = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

            byte[] saltIvCiphertext = ByteBuffer.allocate(salt.length + iv.length + ciphertext.length)
                    .put(salt)
                    .put(iv)
                    .put(ciphertext)
                    .array();
            return Base64.getEncoder()
                    .encodeToString(saltIvCiphertext);
        } catch (Exception e) {
            return null;
        }
    }

    public static String decrypt(String plainText, String passPhrase) {


        try {
            byte[] ciphertext = Base64.getDecoder()
                    .decode(plainText);
            if (ciphertext.length < 48) {
                return null;
            }
            byte[] salt = Arrays.copyOfRange(ciphertext, 0, 16);
            byte[] iv = Arrays.copyOfRange(ciphertext, 16, 32);
            byte[] ct = Arrays.copyOfRange(ciphertext, 32, ciphertext.length);

            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            PBEKeySpec spec = new PBEKeySpec(passPhrase.toCharArray(), salt, 1000, 128);
            SecretKey tmp = factory.generateSecret(spec);
            SecretKey secret = new SecretKeySpec(tmp.getEncoded(), "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

            cipher.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(iv));
            byte[] plaintext = cipher.doFinal(ct);

            return new String(plaintext, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
            return null;

        }
    }

    private static byte[] generate128BitsOfRandomEntropy() {
        var randomBytes = new byte[16]; // 16 Bytes will give us 128 bits.

        SecureRandom rngCsp = new SecureRandom();
        // Fill the array with cryptographically secure random bytes.
        rngCsp.nextBytes(randomBytes);

        return randomBytes;
    }

}
