package qinyuanliu.storesystemandroid.util;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Environment;
import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import qinyuanliu.storesystemandroid.http.util.StringUtils;

public class HeadImageUtil {
  // public static final String CROP_CACHE_FILE_NAME = "/storesystemdemo/";
    public static final int REQUEST_GALLERY = 0xa0;
    public static final int RE_GALLERY = 127;


    private HeadImageUtil() {
    }

    private static HeadImageUtil instance = new HeadImageUtil();

    public static Bitmap makeRoundCorner(Bitmap bitmap)
    {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int left = 0, top = 0, right = width, bottom = height;
        float roundPx = height/2;
        if (width > height) {
            left = (width - height)/2;
            top = 0;
            right = left + height;
            bottom = height;
        } else if (height > width) {
            left = 0;
            top = (height - width)/2;
            right = width;
            bottom = top + width;
            roundPx = width/2;
        }
        // ZLog.i(TAG, "ps:"+ left +", "+ top +", "+ right +", "+ bottom);

        Bitmap output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        int color = 0xff424242;
        Paint paint = new Paint();
        Rect rect = new Rect(left, top, right, bottom);
        RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }
    public static Bitmap compressImage(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > 80) {    //循环判断如果压缩后图片是否大于80kb,大于继续压缩
            baos.reset();//重置baos即清空baos
            options -= 10;//每次都减少10
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
//        Log.i("compresssize=====",Integer.toString(baos.toByteArray().length/ 1024));
        return bitmap;
    }
    static public String BitmapToString(Bitmap bitmap) {
        String des = null;
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
            byte[] buffer = out.toByteArray();
            byte[] encode = Base64.encode(buffer, Base64.DEFAULT);
            des = new String(encode);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return des;
    }

    public static Bitmap GetBitMBitmap(String urlpath) {
        Bitmap map = null;
        try {
            URL url = new URL(urlpath);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(4000);
            conn.setRequestMethod("GET");
            if (conn.getResponseCode() == 200) {
                InputStream inputStream = conn.getInputStream();
                map = BitmapFactory.decodeStream(inputStream);
            }
            // TODO Auto-generated catch block
        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }

    public static byte[] Bitmap2Bytes(Bitmap bm){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }
    public static HeadImageUtil getCropHelperInstance() {
        return instance;
    }

    private Uri buildUri() {

        return Uri.fromFile(Environment.getExternalStorageDirectory())
                .buildUpon().appendPath("/storesystemdemo/"+ StringUtils.getCurrentTime()+".jpg").build();
    }
    public void sethandleResultListerner(CropHandler handler, int requestCode,
                                         int resultCode, Intent data) {
        if (handler == null)
            return;
        if (resultCode == Activity.RESULT_CANCELED) {
            handler.onCropCancel();
        }
        else if (resultCode == Activity.RESULT_OK) {
            Bitmap photo;
            switch (requestCode) {

                case RE_GALLERY:
                    if (data == null || data.getExtras() == null) {
                        handler.onCropFailed("CropHandler's context MUST NOT be null!");
                        return;
                    }
                    photo = data.getExtras().getParcelable("data");

                    handler.onPhotoCropped(photo, requestCode);
                    break;

                case REQUEST_GALLERY:
                    if (data == null) {
                        handler.onCropFailed("Data MUST NOT be null!");
                        return;
                    }
                    Intent intent2 = buildCropIntent(data.getData());

                    if (handler.getContext() != null) {
                        handler.getContext().startActivityForResult(intent2,
                                RE_GALLERY);
                    } else {
                        handler.onCropFailed("CropHandler's context MUST NOT be null!");
                    }
                    break;
            }
        }
    }
    public Intent buildGalleryIntent() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        return galleryIntent;
    }



    private Intent buildCropIntent(Uri uri) {
        Intent cropIntent = new Intent("com.android.camera.action.CROP");
        cropIntent.setDataAndType(uri, "image/*");
        cropIntent.putExtra("crop", "true");
        cropIntent.putExtra("aspectX", 1);
        cropIntent.putExtra("aspectY", 1);
        cropIntent.putExtra("outputX", 150);
        cropIntent.putExtra("outputY", 150);
      //  cropIntent.putExtra("return-data", true); 小米7.0+有错
        cropIntent.putExtra("return-data",false);
        return cropIntent;
    }

    public interface CropHandler {
        void onPhotoCropped(Bitmap photo, int requesCode);

        void onCropCancel();

        void onCropFailed(String message);

        Activity getContext();
    }

}
