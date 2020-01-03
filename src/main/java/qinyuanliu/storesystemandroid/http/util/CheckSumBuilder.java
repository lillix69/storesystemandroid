package qinyuanliu.storesystemandroid.http.util;

import java.security.MessageDigest;

/**
 * Created by lillix on 6/26/17.
 */
public class CheckSumBuilder {
    private static final char[] HEX_DIGITS = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    public CheckSumBuilder() {
    }

    public static String getCheckSum(String appSecret, String nonce, String curTime) {
        return encode("sha1", appSecret + nonce + curTime);
    }

    public static String getMD5(String requestBody) {
        return encode("md5", requestBody);
    }

    private static String encode(String algorithm, String value) {
        if(value == null) {
            return null;
        } else {
            try {
                MessageDigest e = MessageDigest.getInstance(algorithm);
                e.update(value.getBytes());
                return getFormattedText(e.digest());
            } catch (Exception var3) {
                throw new RuntimeException(var3);
            }
        }
    }

    private static String getFormattedText(byte[] bytes) {
        int len = bytes.length;
        StringBuilder buf = new StringBuilder(len * 2);

        for(int j = 0; j < len; ++j) {
            buf.append(HEX_DIGITS[bytes[j] >> 4 & 15]);
            buf.append(HEX_DIGITS[bytes[j] & 15]);
        }

        return buf.toString();
    }
}
