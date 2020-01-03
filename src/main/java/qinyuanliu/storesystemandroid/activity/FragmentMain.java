package qinyuanliu.storesystemandroid.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
import qinyuanliu.storesystemandroid.util.UpdateManager;
import qinyuanliu.storesystemandroid.util.zxing.camera.PlanarYUVLuminanceSource;

/**
 * Created by qinyuanliu on 2019/4/13.
 */

public class FragmentMain extends Fragment {
    private ListView listview_product;
    private Button btn_notice;
    private ImageView img_refresh;
    private ImageView img_addmodel;
    private TextView tv_name;
    private Button btn_config;
    private Button btn_scan;
    private TextView tv_keyword;
    private ImageView img_dot;
    private ArrayList<ProductModel> datalist = new ArrayList<>();
    private FastmodeProductListAdapter listAdapter_fastmode;
    private ProductListAdapter listAdapter;
    private static final int CODE_GALLERY_REQUEST = 0xa0;
    private static final int CODE_RESULT_REQUEST = 0xa2;
    private final int PERMISSION_READ = 1;//读取权限
    // 裁剪后图片的宽(X)和高(Y),150 X 150的正方形。
    private static int output_X = 150;
    private static int output_Y = 150;
    private String mExtStorDir;
    private Uri mUriPath;
    private AlertDialog scanAlert;

    public static FragmentMain newInstance() {

        FragmentMain fragmentMain = new FragmentMain();

        return fragmentMain;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyApplication.setMainActivity(this);
        mExtStorDir = Environment.getExternalStorageDirectory().toString();


    }

    @Override
    public void onResume() {
        super.onResume();
        RefreshData();
        new Thread(new UpdateManager()).start();
        CheckPushCount();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_main, container, false);
        img_dot = (ImageView) v.findViewById(R.id.img_dot);
        listview_product = (ListView) v.findViewById(R.id.listview_product);
        btn_notice = (Button) v.findViewById(R.id.btn_notice);
        btn_notice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), NoticeActivity.class));
            }
        });
        img_refresh = (ImageView) v.findViewById(R.id.img_refresh);
        img_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RefreshData();
            }
        });
        img_addmodel = (ImageView) v.findViewById(R.id.img_addmodel);
        img_addmodel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddModel();
            }
        });
        btn_config = (Button) v.findViewById(R.id.btn_config);
        btn_config.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), SettingActivity.class));
            }
        });

        btn_scan = (Button) v.findViewById(R.id.btn_scan);
        btn_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClickScan();
            }
        });
        tv_name = (TextView) v.findViewById(R.id.tv_name);
        tv_keyword = (TextView) v.findViewById(R.id.tv_keyword);
        tv_keyword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), SearchProductActivity.class));
            }
        });
        return v;
    }

    private void ClickScan(){
        if(scanAlert == null) {
            scanAlert = new AlertDialog.Builder(getContext()).setTitle("选择扫码方式")
                    .setCancelable(true)
                    .setPositiveButton("扫码", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            ScanCamera();
                        }
                    })
                    .setNegativeButton("手动输入", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            EditSns();
                        }
                    }).create();
        }
        scanAlert.show();
    }

    private void EditSns(){
        final EditText et = new EditText(getContext());
        et.setFocusable(true);
        et.setFocusableInTouchMode(true);
        et.requestFocus();
        AlertDialog.Builder alert = new AlertDialog.Builder(getContext()).setTitle("请输入条码")
                .setView(et)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        final String input = et.getText().toString();
                        if(!input.equals("")) {
                            //判断是型号还是产品
                            Session.CheckRefreshToken(new RefreshTokenListener() {
                                @Override
                                public void RefreshTokenResult(final int resultcode) {
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (resultcode == Codes.Code_Success) {
                                                SCSDK.getInstance().GetTypeByScan(Session.getInstance().getShopCode(), input, Session.getInstance().getToken(), new SCResponseListener() {
                                                    @Override
                                                    public void onResult(Object result) {
                                                        final SCResult.ScanResult product = (SCResult.ScanResult) result;
                                                        getActivity().runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                // 0:产品  1：型号  3：批量条码扫码二维码
                                                                if (product.result == 0) {
                                                                    ShowProduct(product.valuecode);
                                                                } else if (product.result == 1) {
                                                                    ShowModel(product.valuecode);
                                                                } else if (product.result == 2) {
                                                                    ShowInout(product.valuecode);
                                                                }
                                                                else if (product.result == 3) {
                                                                    ShowBatch(product.valuecode);
                                                                }
                                                                else {
                                                                    Toast.makeText(getContext(), "无法识别输入的条码!", Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        });
                                                    }

                                                    @Override
                                                    public void onError(final int code, final String errormsg) {
                                                        getActivity().runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                    Toast.makeText(getContext(), errormsg, Toast.LENGTH_SHORT).show();
                                                            }
                                                        });

                                                    }
                                                });
                                            } else {
                                                Toast.makeText(getContext(), SCExceptionCodeList.getExceptionMsg(resultcode), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            });
                        }

                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(et.getWindowToken(), 0) ;
                    }
                })
                .setNegativeButton("取消",  new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(et.getWindowToken(), 0) ;
                    }
                });
        //.show();
        AlertDialog tempDialog = alert.create();
        tempDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(et, InputMethodManager.SHOW_IMPLICIT);
            }
        });
        tempDialog.show();
    }

    private void ShowInout(String qrcode) {
        Intent scanqr = new Intent(getContext(), ScanInoutStoreActivity.class);
        scanqr.putExtra("scanQRcode", qrcode);
        startActivity(scanqr);
    }

    private void ShowProduct(String productid) {
        Intent productintent = new Intent(getContext(), ProductDetailActivity.class);
        productintent.putExtra("productid", productid);
        startActivity(productintent);
    }

    private void ShowModel(String modelid) {
        Intent productintent = new Intent(getContext(), ModelDetailActivity.class);
        productintent.putExtra("productid", modelid);
        startActivity(productintent);
    }
    private void ShowBatch(String modelid) {
        Intent productintent = new Intent(getContext(),BatchStoreActivity.class);
        productintent.putExtra("productid", modelid);
        startActivity(productintent);
    }

    private void RefreshData() {
        tv_name.setText("欢迎您, " + Session.getInstance().getShopname() + "(" + Session.getInstance().getNickname() + ")");
        Session.CheckRefreshToken(new RefreshTokenListener() {
            @Override
            public void RefreshTokenResult(final int resultcode) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (resultcode == Codes.Code_Success) {
                            SCSDK.getInstance().SearchModel(Session.getInstance().getShopCode(), null, Session.getInstance().getToken(), new SCResponseListener() {
                                @Override
                                public void onResult(Object result) {
                                    final SCResult.ModelResult res = (SCResult.ModelResult) result;
                                    if(getActivity() != null) {
                                        getActivity().runOnUiThread(new Runnable() {
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
                                }

                                @Override
                                public void onError(int code, final String errormsg) {
                                    if(getActivity() != null) {
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(getContext(), errormsg, Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(getContext(), SCExceptionCodeList.getExceptionMsg(resultcode), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
    private void RefreshListview() {
        if (Session.getInstance().getFastmodeFlag()) {
            if (listAdapter == null) {
                listAdapter = new ProductListAdapter(getContext(), datalist, new ClickProductListener() {
                    @Override
                    public void onClickProduct(String productid, String modelname,String sn) {
                        Intent productdetail = new Intent(getContext(), ModelDetailActivity.class);
                        productdetail.putExtra("productid", productid);
                        startActivity(productdetail);
                    }
                }, new ClickInStoreListener() {
                    @Override
                    public void onClickInStore(String productid) {
                        Intent inintent = new Intent(getContext(), InStoreActivity.class);
                        inintent.putExtra("productid", productid);
                        startActivity(inintent);
                    }
                }, new ClickOutStoreListener() {
                    @Override
                    public void onClickOutStore(String productid) {
                        Intent outintent = new Intent(getContext(), OutStoreActivity.class);
                        outintent.putExtra("productid", productid);
                        startActivity(outintent);
                    }
                }, new ClickLookStoreListener() {
                    @Override
                    public void onClickLookStore(String productid) {
                        Intent inintent = new Intent(getContext(), LookStoreActivity.class);
                        inintent.putExtra("productid", productid);
                        startActivity(inintent);
                    }
                }, new ClickPositionListener() {
                    @Override
                    public void ClickPosition(String productid) {
                        Intent inintent = new Intent(getContext(), SamePositionActivity.class);
                        inintent.putExtra("modelcode", productid);
                        startActivity(inintent);
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
                listAdapter_fastmode = new FastmodeProductListAdapter(getContext(), datalist, new ClickProductListener() {
                    @Override
                    public void onClickProduct(String productid,String modelname,String sn) {
                        Intent productdetail = new Intent(getContext(), ModelDetailActivity.class);
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

    private void AddModel(){
        Session.CheckRefreshToken(new RefreshTokenListener() {
            @Override
            public void RefreshTokenResult(final int resultcode) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (resultcode == Codes.Code_Success) {
                            SCSDK.getInstance().GetAccountModelPower(Session.getInstance().getShopCode(), Session.getInstance().getToken(), new SCResponseListener() {
                                @Override
                                public void onResult(Object result) {
                                    final SCResult.ModelPowerResult res = (SCResult.ModelPowerResult) result;
                                    if(getActivity() != null) {
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                //新增型号 0：无权限 1：可以新增型号 2：可以修改型号 3：可以新增和修改型号
                                                if (res.modelpower == 1 || res.modelpower == 3) {
                                                    Intent addintent = new Intent(getContext(), AddModelActivity.class);
                                                    startActivity(addintent);
                                                } else {
                                                    Toast.makeText(getActivity(), "您的账户没有新增型号的权限！", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    }
                                }

                                @Override
                                public void onError(int code, final String errormsg) {
                                    if(getActivity() != null) {
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(getActivity(), errormsg, Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(getActivity(), SCExceptionCodeList.getExceptionMsg(resultcode), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

    }

    private void CheckPushCount() {
        Session.CheckRefreshToken(new RefreshTokenListener() {
            @Override
            public void RefreshTokenResult(final int resultcode) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (resultcode == Codes.Code_Success) {
                            SCSDK.getInstance().GetPushCount(Session.getInstance().getShopCode(), Session.getInstance().getToken(), new SCResponseListener() {
                                @Override
                                public void onResult(Object result) {
                                    final SCResult.PushCountResult res = (SCResult.PushCountResult) result;
                                    if(getActivity() != null) {
                                        getActivity().runOnUiThread(new Runnable() {
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
                                }

                                @Override
                                public void onError(int code, final String errormsg) {
                                    if(getActivity() != null) {
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(getContext(), errormsg, Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(getContext(), SCExceptionCodeList.getExceptionMsg(resultcode), Toast.LENGTH_SHORT).show();
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
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getContext(), "请先开启手机摄像头使用权限", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Settings.ACTION_SETTINGS);
                startActivity(intent);
            } else {
                Intent scanintent = new Intent(getContext(), MipcaActivityCapture.class);
                scanintent.putExtra("isSingle", true);
                startActivity(scanintent);
            }
        } else {
            Intent scanintent = new Intent(getContext(), MipcaActivityCapture.class);
            scanintent.putExtra("isSingle", true);
            startActivity(scanintent);
        }
    }

    // 从本地相册选取图片
    private void choseImageFromGallery() {
        int permission = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
            ActivityCompat.requestPermissions(getActivity(), permissions, PERMISSION_READ);
        } else {
            Intent intentFromGallery = new Intent(Intent.ACTION_PICK, null);
            intentFromGallery.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
            startActivityForResult(intentFromGallery, CODE_GALLERY_REQUEST);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 用户没有进行有效的设置操作，返回
//        if (resultCode == RESULT_CANCELED) {
//            Toast.makeText(getContext(), "取消", Toast.LENGTH_SHORT).show();
//            return;
//        }
        if (requestCode == CODE_GALLERY_REQUEST) {
            //cropRawPhoto(data.getData());
            String resultString = handleAlbumPic(data.getData());
            if(resultString == null || resultString.equals("")){
                Toast.makeText(getContext(), "无法识别图片二维码", Toast.LENGTH_SHORT).show();
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
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
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
