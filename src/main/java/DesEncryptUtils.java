import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Key;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class DesEncryptUtils {

    private static final String KEY_ALGORITHM = "DESede";
    private static final String CIPHER_ALGORITHM = "DESede";
    private static final int BUFFER_SIZE = 4096;

    private static SecretKey toKey(byte[] key) throws Exception {
        DESedeKeySpec dks = new DESedeKeySpec(key);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(KEY_ALGORITHM);
        return keyFactory.generateSecret(dks);
    }

    private static byte[] desEncrypt(byte[] key, byte[] plainBytes) throws Exception {
        SecretKey deskey = new SecretKeySpec(key, KEY_ALGORITHM);
        Cipher c1 = Cipher.getInstance(CIPHER_ALGORITHM);
        c1.init(Cipher.ENCRYPT_MODE, deskey);
        byte[] b1 = c1.doFinal(plainBytes);
        return b1;
    }

    private static byte[] desDecrypt(byte[] key, byte[] plainBytes) throws Exception {
        SecretKey deskey = new javax.crypto.spec.SecretKeySpec(key, KEY_ALGORITHM);
        Cipher c1 = Cipher.getInstance(CIPHER_ALGORITHM);
        c1.init(Cipher.DECRYPT_MODE, deskey);
        byte[] b1 = c1.doFinal(plainBytes);
        return b1;
    }

    public static void encrypt(byte[] key, InputStream in, OutputStream out) throws Exception {
        Key k = toKey(key);
        Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, k);

        CipherOutputStream cos = new CipherOutputStream(out, cipher);

        byte[] buffer = new byte[BUFFER_SIZE];
        int bytesRead;
        while ((bytesRead = in.read(buffer)) != -1) {
            cos.write(buffer, 0, bytesRead);
        }
        cos.close();

        //For extra security, don't leave any plaintext hanging around memory.
        Arrays.fill(buffer, (byte)0);
    }

    public static void decrypt(byte[] key, InputStream in, OutputStream out) throws Exception {
        Key k = toKey(key);
        Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, k);

        CipherInputStream cis = new CipherInputStream(in, cipher);
        int bytesRead;
        byte[] buffer = new byte[BUFFER_SIZE];
        while ((bytesRead = cis.read(buffer)) != -1) {
            out.write(buffer, 0, bytesRead);
        }
        cis.close();
        out.flush();
    }

    /**
     * 分段Des加密
     */
    public static byte[] desEncryptForSection(byte[] key, byte[] text) throws Exception {
        ByteArrayInputStream bais = new ByteArrayInputStream(text);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buff = new byte[4096];
        int length = -1;
        while ((length = bais.read(buff)) != -1) {
            byte[] temp = new byte[length];
            System.arraycopy(buff, 0, temp, 0, length);
            byte[] endata = desEncrypt(key, temp);
            baos.write(endata);
        }
        baos.flush();
        return baos.toByteArray();
    }

    /**
     * 分段Des加密
     */
    public static void desEncryptForSection(byte[] key, InputStream in, String filePath) throws Exception {
        //			ByteArrayInputStream bais = new ByteArrayInputStream(text);
        //			ByteArrayOutputStream baos = new ByteArrayOutputStream();
        if (in == null)
            return;
        BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(filePath));
        byte[] buff = new byte[4096];
        int length = -1;
        while ((length = in.read(buff)) != -1) {
            byte[] temp = new byte[length];
            System.arraycopy(buff, 0, temp, 0, length);
            byte[] endata = desEncrypt(key, temp);
            out.write(endata);
        }
        out.flush();
        out.close();
        in.close();
    }

    /**
     * 分段Des解密
     */
    public static byte[] desDecryptForSection(byte[] key, byte[] text) throws Exception {
        ByteArrayInputStream bais = new ByteArrayInputStream(text);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buff = new byte[4096 + 8];
        int length = -1;
        while ((length = bais.read(buff)) != -1) {
            byte[] temp = new byte[length];
            System.arraycopy(buff, 0, temp, 0, length);
            byte[] endata = desDecrypt(key, temp);
            baos.write(endata);
        }
        baos.flush();
        return baos.toByteArray();
    }

    /**
     * 分段Des解密

     */
    public static void desDecryptForSection(byte[] key, InputStream in, String filePath) throws Exception {
        //			ByteArrayInputStream bais = new ByteArrayInputStream(text);
        //			ByteArrayOutputStream baos = new ByteArrayOutputStream();
        if (in == null)
            return;
        BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(filePath));
        byte[] buff = new byte[4096 + 8];
        int length = -1;
        while ((length = in.read(buff)) != -1) {
            byte[] temp = new byte[length];
            System.arraycopy(buff, 0, temp, 0, length);
            byte[] endata = desDecrypt(key, temp);
            out.write(endata);
        }
        out.flush();
        out.close();
        in.close();
    }
}
