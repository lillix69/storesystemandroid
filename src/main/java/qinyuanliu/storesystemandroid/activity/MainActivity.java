package qinyuanliu.storesystemandroid.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Hashtable;

import qinyuanliu.storesystemandroid.MyApplication;
import qinyuanliu.storesystemandroid.R;
import qinyuanliu.storesystemandroid.adapter.FastmodeProductListAdapter;
import qinyuanliu.storesystemandroid.adapter.ProductListAdapter;
import qinyuanliu.storesystemandroid.common.Session;
import qinyuanliu.storesystemandroid.http.Codes;
import qinyuanliu.storesystemandroid.http.SCExceptionCodeList;
import qinyuanliu.storesystemandroid.http.SCResponseListener;
import qinyuanliu.storesystemandroid.http.SCResult;
import qinyuanliu.storesystemandroid.http.SCSDK;
import qinyuanliu.storesystemandroid.listener.ClickInStoreListener;
import qinyuanliu.storesystemandroid.listener.ClickLookStoreListener;
import qinyuanliu.storesystemandroid.listener.ClickOutStoreListener;
import qinyuanliu.storesystemandroid.listener.ClickPositionListener;
import qinyuanliu.storesystemandroid.listener.ClickProductListener;
import qinyuanliu.storesystemandroid.listener.RefreshTokenListener;
import qinyuanliu.storesystemandroid.model.ProductModel;
import qinyuanliu.storesystemandroid.util.BitmapUtil;
import qinyuanliu.storesystemandroid.util.DensityUtil;
import qinyuanliu.storesystemandroid.util.UpdateManager;
import qinyuanliu.storesystemandroid.util.zxing.camera.PlanarYUVLuminanceSource;
import qinyuanliu.storesystemandroid.util.zxing.decoding.RGBLuminanceSource;

/**
 * Created by lillix on 5/28/18.
 */
public class MainActivity extends BaseActivity {
    private ListView listview_product;
    private Button btn_notice;
    private ImageView img_refresh;
    private TextView tv_name;
    private Button btn_config;
    // private Button btn_add;
    private Button btn_scan;
    private TextView tv_keyword;
    private ImageView img_dot;

    private ArrayList<ProductModel> datalist = new ArrayList<>();
    private FastmodeProductListAdapter listAdapter_fastmode;
    private ProductListAdapter listAdapter;
    //退出时的时间
    private long mExitTime = 0;
    private static final int CODE_GALLERY_REQUEST = 0xa0;
    private static final int CODE_RESULT_REQUEST = 0xa2;
    private final int PERMISSION_READ = 1;//读取权限
    // 裁剪后图片的宽(X)和高(Y),150 X 150的正方形。
    private static int output_X = 150;
    private static int output_Y = 150;
    private String mExtStorDir;
    private Uri mUriPath;
    private AlertDialog scanAlert;

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            exit();
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    private void exit() {
        if ((System.currentTimeMillis() - mExitTime) > 2000) {
            Toast.makeText(MainActivity.this, "再按一次应用将进入后台", Toast.LENGTH_SHORT).show();
            mExitTime = System.currentTimeMillis();
        } else {
            moveTaskToBack(false);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
       // MyApplication.setMainActivity(this);
        mExtStorDir = Environment.getExternalStorageDirectory().toString();

    }

    @Override
    protected void onResume() {
        super.onResume();
        RefreshData();

        Session.getInstance().setContext(MainActivity.this);
        new Thread(new UpdateManager()).start();
        CheckPushCount();
    }


    private void initView() {
        img_dot = (ImageView) findViewById(R.id.img_dot);
        listview_product = (ListView) findViewById(R.id.listview_product);
        btn_notice = (Button) findViewById(R.id.btn_notice);
        btn_notice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, NoticeActivity.class));
            }
        });
        img_refresh = (ImageView) findViewById(R.id.img_refresh);
        img_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RefreshData();
            }
        });
        btn_config = (Button) findViewById(R.id.btn_config);
        btn_config.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SettingActivity.class));
            }
        });

        btn_scan = (Button) findViewById(R.id.btn_scan);
        btn_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (scanAlert == null) {
//                    scanAlert = new AlertDialog.Builder(MainActivity.this)
//                            .setTitle("扫描二维码")
//                            .setPositiveButton("摄像头扫描", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    ScanCamera();
//                                    scanAlert.dismiss();
//                                }
//                            })
//                            .setNegativeButton("从相册选取", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    choseImageFromGallery();
//                                    scanAlert.dismiss();
//                                }
//                            }).create();
//                }
//                scanAlert.show();
                ScanCamera();
            }
        });
        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_keyword = (TextView) findViewById(R.id.tv_keyword);
        tv_keyword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SearchProductActivity.class));
            }
        });
    }

    private void RefreshData() {
        tv_name.setText("欢迎您, " + Session.getInstance().getShopname() + "(" + Session.getInstance().getNickname() + ")");
        Session.CheckRefreshToken(new RefreshTokenListener() {
            @Override
            public void RefreshTokenResult(final int resultcode) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (resultcode == Codes.Code_Success) {
                            SCSDK.getInstance().SearchModel(Session.getInstance().getShopCode(), null, Session.getInstance().getToken(), new SCResponseListener() {
                                @Override
                                public void onResult(Object result) {
                                    final SCResult.ModelResult res = (SCResult.ModelResult) result;
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (res.models != null) {
                                                datalist.clear();
                                                for (int i = 0; i < res.models.size(); i++) {
                                                    SCResult.Model model = res.models.get(i);
                                                    ProductModel product = new ProductModel(model.modelcode, model.modelname, model.postion, model.spec, model.store, model.typename, "");
                                                    datalist.add(product);
                                                }

                                                RefreshListview();
                                            }
                                        }
                                    });
                                }

                                @Override
                                public void onError(int code, final String errormsg) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(MainActivity.this, errormsg, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            });
                        } else {
                            Toast.makeText(MainActivity.this, SCExceptionCodeList.getExceptionMsg(resultcode), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    private void RefreshListview() {
        if (Session.getInstance().getFastmodeFlag()) {
            if (listAdapter == null) {
                listAdapter = new ProductListAdapter(MainActivity.this, datalist, new ClickProductListener() {
                    @Override
                    public void onClickProduct(String productid, String modelname, String sn) {
                        Intent productdetail = new Intent(MainActivity.this, ModelDetailActivity.class);
                        productdetail.putExtra("productid", productid);
                        startActivity(productdetail);
                    }
                }, new ClickInStoreListener() {
                    @Override
                    public void onClickInStore(String productid) {
                        Intent inintent = new Intent(MainActivity.this, InStoreActivity.class);
                        inintent.putExtra("productid", productid);
                        startActivity(inintent);
                    }
                }, new ClickOutStoreListener() {
                    @Override
                    public void onClickOutStore(String productid) {
                        Intent outintent = new Intent(MainActivity.this, OutStoreActivity.class);
                        outintent.putExtra("productid", productid);
                        startActivity(outintent);
                    }
                }, new ClickLookStoreListener() {
                    @Override
                    public void onClickLookStore(String productid) {
                        Intent inintent = new Intent(MainActivity.this, LookStoreActivity.class);
                        inintent.putExtra("productid", productid);
                        startActivity(inintent);
                    }
                }, new ClickPositionListener() {
                    @Override
                    public void ClickPosition(String productid) {

                    }
                });
                listview_product.setAdapter(listAdapter);
            } else {
                listview_product.setAdapter(listAdapter);
                listAdapter.setDatalist(datalist);
                listAdapter.notifyDataSetChanged();
            }
        } else {
            if (listAdapter_fastmode == null) {
                listAdapter_fastmode = new FastmodeProductListAdapter(MainActivity.this, datalist, new ClickProductListener() {
                    @Override
                    public void onClickProduct(String productid,String modelname,String sn) {
                        Intent productdetail = new Intent(MainActivity.this, ModelDetailActivity.class);
                        productdetail.putExtra("productid", productid);
                        startActivity(productdetail);
                    }
                });
                listview_product.setAdapter(listAdapter_fastmode);
            } else {
                listview_product.setAdapter(listAdapter_fastmode);
                listAdapter_fastmode.setDatalist(datalist);
                listAdapter_fastmode.notifyDataSetChanged();
            }
        }
    }

    private void CheckPushCount() {
        Session.CheckRefreshToken(new RefreshTokenListener() {
            @Override
            public void RefreshTokenResult(final int resultcode) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (resultcode == Codes.Code_Success) {
                            SCSDK.getInstance().GetPushCount(Session.getInstance().getShopCode(), Session.getInstance().getToken(), new SCResponseListener() {
                                @Override
                                public void onResult(Object result) {
                                    final SCResult.PushCountResult res = (SCResult.PushCountResult) result;
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (res.count > 0) {
                                                img_dot.setVisibility(View.VISIBLE);
                                            } else {
                                                img_dot.setVisibility(View.INVISIBLE);
                                            }
                                        }
                                    });
                                }

                                @Override
                                public void onError(int code, final String errormsg) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(MainActivity.this, errormsg, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            });
                        } else {
                            Toast.makeText(MainActivity.this, SCExceptionCodeList.getExceptionMsg(resultcode), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    public void showdot() {
        img_dot.setVisibility(View.VISIBLE);
    }


    //照相机扫一扫
    private void ScanCamera() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this, "请先开启手机摄像头使用权限", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Settings.ACTION_SETTINGS);
                startActivity(intent);
            } else {
                Intent scanintent = new Intent(MainActivity.this, MipcaActivityCapture.class);
                scanintent.putExtra("isSingle", true);
                startActivity(scanintent);
            }
        } else {
            Intent scanintent = new Intent(MainActivity.this, MipcaActivityCapture.class);
            scanintent.putExtra("isSingle", true);
            startActivity(scanintent);
        }
    }

    // 从本地相册选取图片
    private void choseImageFromGallery() {
        int permission = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
            ActivityCompat.requestPermissions(MainActivity.this, permissions, PERMISSION_READ);
        } else {
            Intent intentFromGallery = new Intent(Intent.ACTION_PICK, null);
            intentFromGallery.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
            startActivityForResult(intentFromGallery, CODE_GALLERY_REQUEST);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 用户没有进行有效的设置操作，返回
        if (resultCode == RESULT_CANCELED) {
            Toast.makeText(getApplication(), "取消", Toast.LENGTH_SHORT).show();
            return;
        }
        if (requestCode == CODE_GALLERY_REQUEST) {
            //cropRawPhoto(data.getData());
            String resultString = handleAlbumPic(data.getData());
            if(resultString == null || resultString.equals("")){
                Toast.makeText(getApplication(), "无法识别图片二维码", Toast.LENGTH_SHORT).show();
            }
            else {
                System.out.println("这是图片二维码" + resultString);

            }
        }

    }

    /**
     * 处理选择的图片
     */
    private String handleAlbumPic(Uri uri) {
        // Bitmap scanBitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
        String qrCode = "";
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
           // bitmap = getSmallerBitmap(bitmap);
            if (bitmap != null) {
                int bitmapWidth = bitmap.getWidth();
                int bitmapHeight = bitmap.getHeight();
                // 1.将bitmap的RGB数据转化成YUV420sp数据
                byte[] bmpYUVBytes = BitmapUtil.getBitmapYUVBytes(bitmap);
                // 2.塞给zxing进行decode
                qrCode = decodeYUVByZxing(bmpYUVBytes, bitmapWidth, bitmapHeight);
                bitmap.recycle();
                bitmap = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return qrCode;
    }

private static String decodeYUVByZxing(byte[] bmpYUVBytes, int bmpWidth, int bmpHeight) {
    String zxingResult = "";
    // Both dimensions must be greater than 0
    if (null != bmpYUVBytes && bmpWidth > 0 && bmpHeight > 0) {
        try {
            PlanarYUVLuminanceSource source = new PlanarYUVLuminanceSource(bmpYUVBytes, bmpWidth,
                    bmpHeight, 0, 0, bmpWidth, bmpHeight);
            BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(source));
            Reader reader = new QRCodeReader();
            Result result = reader.decode(binaryBitmap);
            if (null != result) {
                zxingResult = result.getText();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    return zxingResult;
}


    /**
     * 裁剪原始的图片
     */
    public void cropRawPhoto(Uri uri) {

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");

        // 设置裁剪
        intent.putExtra("crop", "true");
        // aspectX , aspectY :宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);

        // outputX , outputY : 裁剪图片宽高
        intent.putExtra("outputX", output_X);
        intent.putExtra("outputY", output_Y);
        intent.putExtra("return-data", true);

        //startActivityForResult(intent, CODE_RESULT_REQUEST); //直接调用此代码在小米手机有异常，换以下代码
        String mLinshi = System.currentTimeMillis() + "二维码截图";
        File mFile = new File(mExtStorDir, mLinshi);
//        mHeadCachePath = mHeadCacheFile.getAbsolutePath();
        mUriPath = Uri.parse("file://" + mFile.getAbsolutePath());
        //将裁剪好的图输出到所建文件中
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mUriPath);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        //注意：此处应设置return-data为false，如果设置为true，是直接返回bitmap格式的数据，耗费内存。设置为false，然后，设置裁剪完之后保存的路径，即：intent.putExtra(MediaStore.EXTRA_OUTPUT, uriPath);
//        intent.putExtra("return-data", true);
        intent.putExtra("return-data", false);
//        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(intent, CODE_RESULT_REQUEST);
    }

}
