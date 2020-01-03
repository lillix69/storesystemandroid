package qinyuanliu.storesystemandroid.http.util;

import android.os.Build;

import java.util.Random;

import qinyuanliu.storesystemandroid.http.Constants;


/**
 * Created by lillix on 6/26/17.
 */
public final class StringUtils {
    private StringUtils() {
    }

    public static String join(Object[] array, String sep) {
        return join(array, sep, (String)null);
    }

    public static String join(Object[] array, String sep, String prefix) {
        if(array == null) {
            return "";
        } else {
            int arraySize = array.length;
            if(arraySize == 0) {
                return "";
            } else {
                if(sep == null) {
                    sep = "";
                }

                if(prefix == null) {
                    prefix = "";
                }

                StringBuilder buf = new StringBuilder(prefix);

                for(int i = 0; i < arraySize; ++i) {
                    if(i > 0) {
                        buf.append(sep);
                    }

                    buf.append(array[i] == null?"":array[i]);
                }

                return buf.toString();
            }
        }
    }

    public static String jsonJoin(String[] array) {
        int arraySize = array.length;
        int bufSize = arraySize * (array[0].length() + 3);
        StringBuilder buf = new StringBuilder(bufSize);

        for(int i = 0; i < arraySize; ++i) {
            if(i > 0) {
                buf.append(',');
            }

            buf.append('\"');
            buf.append(array[i]);
            buf.append('\"');
        }

        return buf.toString();
    }

    public static boolean isNullOrEmpty(String s) {
        return s == null || "".equals(s);
    }

    public static boolean inStringArray(String s, String[] array) {
        String[] var5 = array;
        int var4 = array.length;

        for(int var3 = 0; var3 < var4; ++var3) {
            String x = var5[var3];
            if(x.equals(s)) {
                return true;
            }
        }

        return false;
    }

    public static byte[] utf8Bytes(String data) {
        return data.getBytes(Constants.UTF_8);
    }

    public static String utf8String(byte[] data) {
        return new String(data, Constants.UTF_8);
    }

    public static String getRandomNonce() {
        Random random = new Random();
        StringBuffer sb = new StringBuffer();

        for(int i = 0; i < 15; ++i) {
            int number = random.nextInt(3);
            long result = 0L;
            switch(number) {
                case 0:
                    result = Math.round(Math.random() * 25.0D + 65.0D);
                    sb.append(String.valueOf((char)((int)result)));
                    break;
                case 1:
                    result = Math.round(Math.random() * 25.0D + 97.0D);
                    sb.append(String.valueOf((char)((int)result)));
                    break;
                case 2:
                    sb.append(String.valueOf((new Random()).nextInt(10)));
            }
        }

        return sb.toString();
    }

    public static String getCurrentTime() {
        long curtime = System.currentTimeMillis();
        return String.valueOf(curtime / 1000L);
    }

    static public String GetDeviceIDForAndroidphone() {
        String tempdeviceid = "";
        int randomhead = (int) (Math.random() * 900) + 100;
        tempdeviceid = String.valueOf(randomhead) +
                Build.BOARD.length() % 10 +
                Build.BRAND.length() % 10 +
                Build.DEVICE.length() % 10 +
                Build.DISPLAY.length() % 10 +
                Build.HOST.length() % 10 +
                Build.ID.length() % 10 +
                Build.MANUFACTURER.length() % 10 +
                Build.MODEL.length() % 10 +
                Build.PRODUCT.length() % 10 +
                Build.TAGS.length() % 10 +
                Build.TYPE.length() % 10 +
                Build.USER.length() % 10; //3+12 digits
        return tempdeviceid;
    }

    public static boolean isvalidNumber(String input){
        return input.matches("^(([0-9]+)([.]([0-9]+))?|([.]([0-9]+))?)$");
    }

}
