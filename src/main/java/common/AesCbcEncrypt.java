package common;

import org.apache.shiro.codec.Base64;
import sun.misc.BASE64Decoder;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.security.SecureRandom;

public class AesCbcEncrypt {

    public static String payloadEncrypt(String key) throws Exception {
        byte[] k = new BASE64Decoder().decodeBuffer(key);
        InputStream inputStream = HttpUtils.class.getClassLoader().getResourceAsStream("detect.txt");
        // 读取字节流还是用 ByteArrayOutputStream
        // 将数据读到 byteArrayOutputStream 中
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        int n;
        while ((n=inputStream.read()) != -1){
            byteArrayOutputStream.write(n);
        }
        byte[] bytes = byteArrayOutputStream.toByteArray();

        byte[] ivBytes = new byte[16];
        SecureRandom random = new SecureRandom();
        random.nextBytes(ivBytes);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(ivBytes);

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        SecretKeySpec keySpec = new SecretKeySpec(k,"AES");
        cipher.init(1,keySpec,ivParameterSpec);
        byte[] payloadBytes = cipher.doFinal(bytes);

        byte[] output = new byte[ivBytes.length + payloadBytes.length];
        System.arraycopy(ivBytes, 0, output, 0, ivBytes.length);
        System.arraycopy(payloadBytes, 0, output, ivBytes.length, payloadBytes.length);

        String b64Payload = Base64.encodeToString(output);
        return b64Payload;
    }

    public static void main(String[] args) throws Exception {
        System.out.println(new AesCbcEncrypt().payloadEncrypt("kPH+bIxk5D2deZiIxcaaaA=="));
    }
}
