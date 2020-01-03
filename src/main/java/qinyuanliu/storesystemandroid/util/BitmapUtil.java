package qinyuanliu.storesystemandroid.util;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by qinyuanliu on 2019/2/25.
 */

public class BitmapUtil {
    public static Bitmap decodeUri(Context context, Uri uri, int maxWidth, int maxHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true; //只读取图片尺寸
        readBitmapScale(context, uri, options);

        //计算实际缩放比例
        int scale = 1;
        for (int i = 0; i < Integer.MAX_VALUE; i++) {
            if ((options.outWidth / scale > maxWidth &&
                    options.outWidth / scale > maxWidth * 1.4) ||
                    (options.outHeight / scale > maxHeight &&
                            options.outHeight / scale > maxHeight * 1.4)) {
                scale++;
            } else {
                break;
            }
        }

        options.inSampleSize = scale;
        options.inJustDecodeBounds = false;//读取图片内容
        options.inPreferredConfig = Bitmap.Config.RGB_565; //根据情况进行修改
        Bitmap bitmap = null;
        try {
            bitmap = readBitmapData(context, uri, options);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    private static void readBitmapScale(Context context, Uri uri, BitmapFactory.Options options) {
        if (uri == null) {
            return;
        }
        String scheme = uri.getScheme();
        if (ContentResolver.SCHEME_CONTENT.equals(scheme) ||
                ContentResolver.SCHEME_FILE.equals(scheme)) {
            InputStream stream = null;
            try {
                stream = context.getContentResolver().openInputStream(uri);
                BitmapFactory.decodeStream(stream, null, options);
            } catch (Exception e) {
                Log.w("readBitmapScale", "Unable to open content: " + uri, e);
            } finally {
                if (stream != null) {
                    try {
                        stream.close();
                    } catch (IOException e) {
                        Log.e("readBitmapScale", "Unable to close content: " + uri, e);
                    }
                }
            }
        } else if (ContentResolver.SCHEME_ANDROID_RESOURCE.equals(scheme)) {
            Log.e("readBitmapScale", "Unable to close content: " + uri);
        } else {
            Log.e("readBitmapScale", "Unable to close content: " + uri);
        }
    }

    private static Bitmap readBitmapData(Context context, Uri uri, BitmapFactory.Options options) {
        if (uri == null) {
            return null;
        }
        Bitmap bitmap = null;
        String scheme = uri.getScheme();
        if (ContentResolver.SCHEME_CONTENT.equals(scheme) ||
                ContentResolver.SCHEME_FILE.equals(scheme)) {
            InputStream stream = null;
            try {
                stream = context.getContentResolver().openInputStream(uri);
                bitmap = BitmapFactory.decodeStream(stream, null, options);
            } catch (Exception e) {
                Log.e("readBitmapData", "Unable to open content: " + uri, e);
            } finally {
                if (stream != null) {
                    try {
                        stream.close();
                    } catch (IOException e) {
                        Log.e("readBitmapData", "Unable to close content: " + uri, e);
                    }
                }
            }
        } else if (ContentResolver.SCHEME_ANDROID_RESOURCE.equals(scheme)) {
            Log.e("readBitmapData", "Unable to close content: " + uri);
        } else {
            Log.e("readBitmapData", "Unable to close content: " + uri);
        }
        return bitmap;
    }

    /**
     * 得到bitmap的YUV数据
     *
     * @param sourceBmp 原始bitmap
     * @return yuv数据
     */
    public static byte[] getBitmapYUVBytes(Bitmap sourceBmp) {
        if (null != sourceBmp) {
            int inputWidth = sourceBmp.getWidth();
            int inputHeight = sourceBmp.getHeight();
            int[] argb = new int[inputWidth * inputHeight];
            sourceBmp.getPixels(argb, 0, inputWidth, 0, 0, inputWidth, inputHeight);
            byte[] yuv = new byte[inputWidth
                    * inputHeight
                    + ((inputWidth % 2 == 0 ? inputWidth : (inputWidth + 1)) * (inputHeight % 2 == 0 ? inputHeight
                    : (inputHeight + 1))) / 2];
            encodeYUV420SP(yuv, argb, inputWidth, inputHeight);
            sourceBmp.recycle();
            return yuv;
        }
        return null;
    }

    /**
     * 将bitmap里得到的argb数据转成yuv420sp格式
     * 这个yuv420sp数据就可以直接传给MediaCodec, 通过AvcEncoder间接进行编码
     *
     * @param yuv420sp 用来存放yuv429sp数据
     * @param argb     传入argb数据
     * @param width    bmpWidth
     * @param height   bmpHeight
     */
    private static void encodeYUV420SP(byte[] yuv420sp, int[] argb, int width, int height) {
        // 帧图片的像素大小
        final int frameSize = width * height;
        // Y的index从0开始
        int yIndex = 0;
        // UV的index从frameSize开始
        int uvIndex = frameSize;
        // YUV数据, ARGB数据
        int Y, U, V, a, R, G, B;
        ;
        int argbIndex = 0;
        // ---循环所有像素点，RGB转YUV---
        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {

                // a is not used obviously
                a = (argb[argbIndex] & 0xff000000) >> 24;
                R = (argb[argbIndex] & 0xff0000) >> 16;
                G = (argb[argbIndex] & 0xff00) >> 8;
                B = (argb[argbIndex] & 0xff);
                argbIndex++;

                // well known RGB to YUV algorithm
                Y = ((66 * R + 129 * G + 25 * B + 128) >> 8) + 16;
                U = ((-38 * R - 74 * G + 112 * B + 128) >> 8) + 128;
                V = ((112 * R - 94 * G - 18 * B + 128) >> 8) + 128;

                Y = Math.max(0, Math.min(Y, 255));
                U = Math.max(0, Math.min(U, 255));
                V = Math.max(0, Math.min(V, 255));

                // NV21 has a plane of Y and interleaved planes of VU each
                // sampled by a factor of 2
                // meaning for every 4 Y pixels there are 1 V and 1 U. Note the
                // sampling is every other
                // pixel AND every other scanline.
                // ---Y---
                yuv420sp[yIndex++] = (byte) Y;
                // ---UV---
                if ((j % 2 == 0) && (i % 2 == 0)) {
                    yuv420sp[uvIndex++] = (byte) V;
                    yuv420sp[uvIndex++] = (byte) U;
                }
            }
        }
    }

}


