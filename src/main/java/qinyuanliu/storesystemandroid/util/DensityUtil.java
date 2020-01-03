package qinyuanliu.storesystemandroid.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;

/**
 * Created by lillix on 5/11/18.
 */

    public class DensityUtil {
        public static float RATIO = 0.95F;//缩放比例值

        /**
         * px 转 dp【按照一定的比例】*/
        public static int px2dipRatio(Context context, float pxValue) {
            float scale = getScreenDendity(context) * RATIO;
            return (int)((pxValue / scale) + 0.5f);
        }

        /**
         * dp转px【按照一定的比例】*/
        public static int dip2pxRatio(Context context, float dpValue) {
            float scale = getScreenDendity(context) * RATIO;
            return (int)((dpValue * scale) + 0.5f);
        }

        /**
         * px 转 dp
       */
        public static int px2dip(Context context, float pxValue) {
            float scale = getScreenDendity(context);
            return (int)((pxValue / scale) + 0.5f);
        }

        /**
         * dp转px
        */
        public static int dip2px(Context context, int dpValue) {
            float scale = getScreenDendity(context);
            return (int)(dpValue * scale);
        }

        /**获取屏幕的宽度（像素）*/
        public static int getScreenWidth(Context context) {
            return context.getResources().getDisplayMetrics().widthPixels;//1080
        }

        /**获取屏幕的宽度（dp）*/
        public static int getScreenWidthDp(Context context) {
            float scale = getScreenDendity(context);
            return (int)(context.getResources().getDisplayMetrics().widthPixels / scale);//360
        }

        /**获取屏幕的高度（像素）*/
        public static int getScreenHeight(Context context) {
            return context.getResources().getDisplayMetrics().heightPixels;//1776
        }

        /**获取屏幕的高度（像素）*/
        public static int getScreenHeightDp(Context context) {
            float scale = getScreenDendity(context);
            return (int)(context.getResources().getDisplayMetrics().heightPixels / scale);//592
        }
        /**屏幕密度比例*/
        public static float getScreenDendity(Context context){
            return context.getResources().getDisplayMetrics().density;//3
        }
        /**获取水平方向的dpi的密度比例值
         * 2.7653186*/
        public static float getPixelScaleFactor(Context context) {
            DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
            return (displayMetrics.xdpi / 160.0f);
        }

    public static byte[] rgb2YUV(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int[] pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);

        int len = width * height;
        byte[] yuv = new byte[len * 3 / 2];
        int y, u, v;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int rgb = pixels[i * width + j] & 0x00FFFFFF;

                int r = rgb & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = (rgb >> 16) & 0xFF;

                y = ((66 * r + 129 * g + 25 * b + 128) >> 8) + 16;
                u = ((-38 * r - 74 * g + 112 * b + 128) >> 8) + 128;
                v = ((112 * r - 94 * g - 18 * b + 128) >> 8) + 128;

                y = y < 16 ? 16 : (y > 255 ? 255 : y);
                u = u < 0 ? 0 : (u > 255 ? 255 : u);
                v = v < 0 ? 0 : (v > 255 ? 255 : v);

                yuv[i * width + j] = (byte) y;
//                yuv[len + (i >> 1) * width + (j & ~1) + 0] = (byte) u;
//                yuv[len + (i >> 1) * width + (j & ~1) + 1] = (byte) v;
            }
        }
        return yuv;
    }

}
