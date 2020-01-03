package qinyuanliu.storesystemandroid.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
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
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import qinyuanliu.storesystemandroid.R;
import qinyuanliu.storesystemandroid.common.Session;
import qinyuanliu.storesystemandroid.http.Codes;
import qinyuanliu.storesystemandroid.http.SCExceptionCodeList;
import qinyuanliu.storesystemandroid.http.SCResponseListener;
import qinyuanliu.storesystemandroid.http.SCResult;
import qinyuanliu.storesystemandroid.http.SCSDK;
import qinyuanliu.storesystemandroid.http.util.StringUtils;
import qinyuanliu.storesystemandroid.listener.RefreshTokenListener;
import qinyuanliu.storesystemandroid.util.HeadImageUtil;

/**
 * Created by qinyuanliu on 2019/6/9.
 */

public class AddModelActivity extends BaseActivity {
    private Button btn_back;
    private ImageView img_pic;
    private TextView tv_name;//型号名称
    private TextView tv_storename;
    private TextView tv_rack;
    private TextView tv_location;
    private TextView tv_category;//分类
    private TextView tv_danwei;//单位
    private TextView tv_guige;//规格
    private TextView tv_check;//校验
    private TextView tv_add;
    private TextView tv_safecount;
    private TextView tv_remark;
    private TextView tv_identify;

    private String modelname = "";
    private String storecode = "";
    private String storename = "";
    private String rackcode = "";
    private String rackname = "";
    private String locationcode = "";
    private String locationname = "";
    private String typecode = "";
    private String typename = "";
    private String danwei = "";
    private String guige = "";
    private int needcheck =0;
    private String compressPic = null;
    private int safecount = 0;
    private String remark;

    private AlertDialog storeAlert;
    private String[] storeData;
    private HashMap<String, String> storenameandcode = new HashMap<>();
    private AlertDialog rackAlert;
    private String[] rackData;
    private HashMap<String, String> racknameandcode = new HashMap<>();
    private AlertDialog locationAlert;
    private String[] locationData;
    private HashMap<String, String> locationnameandcode = new HashMap<>();
    private AlertDialog typeAlert;
    private String[] typeData;
    private HashMap<String, String> typenameandcode = new HashMap<>();
    private AlertDialog checkAlert;
    private String[] checkData = {"不需要", "需要"};
    private AlertDialog identifyAlert;
    private String[] identifyData;
    private HashMap<String, String> identifynameandcode = new HashMap<>();

    // 裁剪后图片的宽(X)和高(Y),480 X 480的正方形。
    private static int output_X = 160;
    private static int output_Y = 160;
    private String mExtStorDir;
    private Uri mUriPath;
    private final int PERMISSION_READ = 1;//读取权限
    private static final int CODE_GALLERY_REQUEST = 0xa0;
    private static final int CODE_CAMERA_REQUEST = 0xa1;
    private static final int CODE_RESULT_REQUEST = 0xa2;
    private static final String CROP_IMAGE_FILE_NAME = "bala_crop.jpg";
    //调用照相机返回图片文件
    private File tempFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addmodel);

        initView();
        mExtStorDir = Environment.getExternalStorageDirectory().toString();
    }

    private void initView() {
        btn_back = (Button) findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tv_add = (TextView) findViewById(R.id.tv_add);
        tv_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Add();
            }
        });
        img_pic = (ImageView) findViewById(R.id.img_pic);
        img_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClickImage();
            }
        });
        tv_check = (TextView) findViewById(R.id.tv_check);
        tv_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClickCheck();
            }
        });
        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClickName();
            }
        });
        tv_storename = (TextView) findViewById(R.id.tv_storename);
        tv_storename.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClickStore();
            }
        });
        tv_rack = (TextView) findViewById(R.id.tv_rack);
        tv_rack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClickRack();
            }
        });
        tv_location = (TextView) findViewById(R.id.tv_location);
        tv_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClickLocation();
            }
        });
        tv_category = (TextView) findViewById(R.id.tv_category);
        tv_category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClickCategory();
            }
        });
        tv_danwei = (TextView) findViewById(R.id.tv_danwei);
        tv_danwei.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClickUnit();
            }
        });
        tv_guige = (TextView) findViewById(R.id.tv_guige);
        tv_guige.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClickGuige();
            }
        });
        tv_safecount = (TextView) findViewById(R.id.tv_safecount);
        tv_safecount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClickSafecount();
            }
        });
        tv_remark = (TextView) findViewById(R.id.tv_remark);
        tv_remark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClickRemark();
            }
        });
        tv_identify = (TextView)findViewById(R.id.tv_identify);
        tv_identify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClickIdentify();
            }
        });
    }

       private void ClickSafecount() {
        final EditText et = new EditText(this);
        et.setInputType(InputType.TYPE_CLASS_NUMBER);
        et.setFocusable(true);
        et.setFocusableInTouchMode(true);
        et.requestFocus();
        if(safecount>0) {
            et.setText(Integer.toString(safecount));
        }
        AlertDialog.Builder alert = new AlertDialog.Builder(this).setTitle("编辑安全库存阀值")
                .setView(et)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String input = et.getText().toString();

                        tv_safecount.setText(input);
                        if (input.equals("")) {
                            safecount = 0;
                        } else {
                            if (StringUtils.isvalidNumber(input)) {
                                safecount = Integer.valueOf(input);
                            } else {
                                Toast.makeText(AddModelActivity.this, "请输入整数值", Toast.LENGTH_SHORT).show();
                            }
                        }
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(et.getWindowToken(), 0) ;
                    }
                })
                .setNegativeButton("取消",  new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(et.getWindowToken(), 0) ;
                    }
                });
        //.show();
        AlertDialog tempDialog = alert.create();
        tempDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(et, InputMethodManager.SHOW_IMPLICIT);
            }
        });
        tempDialog.show();
    }

    private void ClickRemark() {
        final EditText et = new EditText(this);
        et.setFocusable(true);
        et.setFocusableInTouchMode(true);
        et.requestFocus();
        if(remark != null) {
            et.setText(remark);
        }
        AlertDialog.Builder alert = new AlertDialog.Builder(this).setTitle("编辑型号备注")
                .setView(et)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        remark = et.getText().toString();
                        tv_remark.setText(remark);

                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(et.getWindowToken(), 0) ;
                    }
                })
                .setNegativeButton("取消",  new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(et.getWindowToken(), 0) ;
                    }
                });
        //.show();
        AlertDialog tempDialog = alert.create();
        tempDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(et, InputMethodManager.SHOW_IMPLICIT);
            }
        });
        tempDialog.show();
    }


    private void ClickImage() {
//        WindowManager wm = this.getWindowManager();
//        int width = wm.getDefaultDisplay().getWidth() / 2;
//        final ImageView pic = new ImageView(AddModelActivity.this);
//        pic.setAdjustViewBounds(true);
//        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
//                width, LinearLayout.LayoutParams.WRAP_CONTENT);
//        pic.setLayoutParams(layoutParams);
//        pic.setMaxWidth(width);
//        pic.setMaxHeight(width);
//        pic.setImageResource(R.drawable.icon_defaultimg);
//        Glide.with(AddModelActivity.this).load(originalPic)
//                .placeholder(R.drawable.icon_defaultimg)
//                .error(R.drawable.icon_defaultimg)
//                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
//                .into(pic);

        new AlertDialog.Builder(this).setTitle("编辑型号图片")
               // .setView(pic)
                .setPositiveButton("从相册选择", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        checkReadPermission();

                    }
                })
                .setNegativeButton("拍照", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        checkCameraPermission();
                    }
                })

                .show();
    }

    //权限申请回调
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case PERMISSION_READ:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    choseHeadImageFromGallery();
                }
                break;
        }
    }

    private void checkCameraPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(AddModelActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(AddModelActivity.this, "请先开启手机摄像头使用权限", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Settings.ACTION_SETTINGS);
                startActivity(intent);
            } else {
                choseImageFromCamera();
            }
        } else {
            choseImageFromCamera();
        }
    }

    private void checkReadPermission() {
        int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (permission == PackageManager.PERMISSION_DENIED) {
            String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
            ActivityCompat.requestPermissions(this, permissions, PERMISSION_READ);
        } else {
            choseHeadImageFromGallery();
        }
    }

    private void choseImageFromCamera() {
        //跳转到调用系统相机
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            tempFile = createOriImageFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (tempFile != null) {
            Uri imgUriOri;
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                imgUriOri = Uri.fromFile(tempFile);
            } else {
                imgUriOri = FileProvider.getUriForFile(this, getPackageName() + ".fileProvider", tempFile);
            }
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imgUriOri);
            startActivityForResult(intent, CODE_CAMERA_REQUEST);
        }
    }

    /**
     * 创建原图像保存的文件
     *
     * @return
     * @throws IOException
     */
    private File createOriImageFile() throws IOException {
        String imgNameOri = "HomePic_" + new SimpleDateFormat(
                "yyyyMMdd_HHmmss").format(new Date());
        File pictureDirOri = new File(getExternalFilesDir(
                Environment.DIRECTORY_PICTURES).getAbsolutePath() + "/OriPicture");
        if (!pictureDirOri.exists()) {
            pictureDirOri.mkdirs();
        }
        File image = File.createTempFile(
                imgNameOri,         /* prefix */
                ".jpg",             /* suffix */
                pictureDirOri       /* directory */
        );
        // imgPathOri = image.getAbsolutePath();
        return image;
    }


    // 从本地相册选取图片作为头像
    private void choseHeadImageFromGallery() {
        Intent intentFromGallery = new Intent(Intent.ACTION_PICK, null);
        intentFromGallery.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intentFromGallery, CODE_GALLERY_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 用户没有进行有效的设置操作，返回
        if (resultCode == RESULT_CANCELED) {
            Toast.makeText(getApplication(), "取消", Toast.LENGTH_LONG).show();
            return;
        }

        if (requestCode == CODE_GALLERY_REQUEST) {
            cropRawPhoto(data.getData());
        } else if (requestCode == CODE_CAMERA_REQUEST) {
            //用相机返回的照片去调用剪裁也需要对Uri进行处理
            cropRawPhoto(getImageContentUri(tempFile));
        } else if (requestCode == CODE_RESULT_REQUEST) {
            try {
                Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(mUriPath));
                Bitmap compresspic = HeadImageUtil.compressImage(bitmap);
                setImageToHeadView(data, bitmap);
                compressPic = HeadImageUtil.BitmapToString(compresspic);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            Log.i("======", Integer.toString(requestCode));
        }
    }

    /**
     * 提取保存裁剪之后的图片数据，并设置头像部分的View
     */
    private void setImageToHeadView(Intent intent, Bitmap b) {
        try {
            if (intent != null) {
                //   Bitmap bitmap = imageZoom(b);//看个人需求，可以不压缩
//                Glide.with(ModelDetailActivity.this).onStop();
//             img_pic.setImageBitmap(b);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                b.compress(Bitmap.CompressFormat.PNG, 100, baos);
                byte[] bytes = baos.toByteArray();
                Glide.with(this).load(bytes).centerCrop().into(img_pic);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 7.0以上获取裁剪 Uri
     *
     * @param imageFile
     * @return
     */
    private Uri getImageContentUri(File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Media._ID},
                MediaStore.Images.Media.DATA + "=? ",
                new String[]{filePath}, null);

        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor
                    .getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/images/media");
            return Uri.withAppendedPath(baseUri, "" + id);
        } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
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
        String mLinshi = System.currentTimeMillis() + CROP_IMAGE_FILE_NAME;
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



    private void ClickName() {
        final EditText et = new EditText(this);
        et.setText(modelname);
        et.setFocusable(true);
        et.setFocusableInTouchMode(true);
        et.requestFocus();
        AlertDialog.Builder alert = new AlertDialog.Builder(this).setTitle("编辑型号名称")
                .setView(et)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String input = et.getText().toString();
                        if (input.equals("")) {
                            Toast.makeText(AddModelActivity.this, "名称不能为空", Toast.LENGTH_SHORT).show();
                        } else {
                            tv_name.setText(input);
                            modelname = input;
                        }
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(et.getWindowToken(), 0) ;
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(et.getWindowToken(), 0) ;
                    }
                });
        // .show();
        AlertDialog tempDialog = alert.create();
        tempDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(et, InputMethodManager.SHOW_IMPLICIT);
            }
        });
        tempDialog.show();
    }

    private void ClickUnit() {
        final EditText et = new EditText(this);
        et.setText(danwei);
        et.setFocusable(true);
        et.setFocusableInTouchMode(true);
        et.requestFocus();
        AlertDialog.Builder alert =  new AlertDialog.Builder(this).setTitle("编辑单位")
                .setView(et)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String input = et.getText().toString();
                        if (input.equals("")) {
                            Toast.makeText(AddModelActivity.this, "单位不能为空", Toast.LENGTH_SHORT).show();
                        } else {
                            tv_danwei.setText(input);
                            danwei = input;
                        }
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(et.getWindowToken(), 0) ;
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(et.getWindowToken(), 0) ;
                    }
                });
        //  .show();
        AlertDialog tempDialog = alert.create();
        tempDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(et, InputMethodManager.SHOW_IMPLICIT);
            }
        });
        tempDialog.show();
    }

    private void ClickGuige() {
        final EditText et = new EditText(this);
        et.setText(guige);
        et.setFocusable(true);
        et.setFocusableInTouchMode(true);
        et.requestFocus();
        AlertDialog.Builder alert =  new AlertDialog.Builder(this).setTitle("编辑规格")
                .setView(et)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String input = et.getText().toString();
                        if (input.equals("")) {
                            Toast.makeText(AddModelActivity.this, "规格不能为空", Toast.LENGTH_SHORT).show();
                        } else {
                            tv_guige.setText(input);
                            guige = input;
                        }
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(et.getWindowToken(), 0) ;
                    }
                })
                .setNegativeButton("取消",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(et.getWindowToken(), 0) ;
                    }
                });
        //.show();
        AlertDialog tempDialog = alert.create();
        tempDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(et, InputMethodManager.SHOW_IMPLICIT);
            }
        });
        tempDialog.show();
    }


    private void ClickCheck() {
        if (checkAlert == null) {
            checkAlert = new AlertDialog.Builder(AddModelActivity.this)
                    .setTitle("选择是否需要校验")
                    .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (needcheck == 0) {
                                tv_check.setText("不需要");
                            } else {
                                tv_check.setText("需要");
                            }
                            checkAlert.dismiss();

                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            checkAlert.dismiss();
                        }
                    })
                    .setSingleChoiceItems(checkData, -1, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            needcheck = which;
                        }
                    }).create();
        }
        checkAlert.show();
    }

    private String identifycode = "";
    private String identifyname = "";
    private String tempidentifycode = "";
    private String tempidentifyname = "";
    private void ClickIdentify(){
        if(identifyData == null){
            Session.CheckRefreshToken(new RefreshTokenListener() {
                @Override
                public void RefreshTokenResult(final int resultcode) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (resultcode == Codes.Code_Success) {
                                SCSDK.getInstance().GetModelrule(Session.getInstance().getShopCode(),Session.getInstance().getToken(), new SCResponseListener() {
                                    @Override
                                    public void onResult(final Object result) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                SCResult.ModelRuleResult type = (SCResult.ModelRuleResult) result;
                                                if (type.modelrules != null && type.modelrules.size() > 0) {
                                                    identifyData = new String[type.modelrules.size()];
                                                    for (int i = 0; i < type.modelrules.size(); i++) {
                                                        SCResult.ModelRule temp = type.modelrules.get(i);
                                                        identifyData[i] = temp.modelrulename;
                                                        identifynameandcode.put(temp.modelrulename, temp.modelrulecode);
                                                    }
                                                    ShowIdentifyAlert();
                                                }
                                            }
                                        });
                                    }

                                    @Override
                                    public void onError(int code, final String errormsg) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(AddModelActivity.this, errormsg, Toast.LENGTH_SHORT).show();

                                            }
                                        });
                                    }
                                });
                            } else {
                                Toast.makeText(AddModelActivity.this, SCExceptionCodeList.getExceptionMsg(resultcode), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            });
        }
        else{
            ShowIdentifyAlert();
        }
    }

    private void ShowIdentifyAlert(){
        if (identifyAlert == null) {
            identifyAlert = new AlertDialog.Builder(AddModelActivity.this)
                    .setTitle("选择数字标识")
                    .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            identifyname = tempidentifyname;
                            identifycode = tempidentifycode;
                            tv_identify.setText(identifyname);
                            identifyAlert.dismiss();

                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            identifyAlert.dismiss();
                        }
                    })
                    .setSingleChoiceItems(identifyData, -1, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            tempidentifycode = identifynameandcode.get(identifyData[which]);
                            tempidentifyname = identifyData[which];
                        }
                    }).create();
        }
        identifyAlert.show();
    }


    private void ClickCategory() {
        if (typeData == null) {
            Session.CheckRefreshToken(new RefreshTokenListener() {
                @Override
                public void RefreshTokenResult(final int resultcode) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (resultcode == Codes.Code_Success) {
                                SCSDK.getInstance().GetTypeList(Session.getInstance().getShopCode(),Session.getInstance().getToken(), new SCResponseListener() {
                                    @Override
                                    public void onResult(final Object result) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                SCResult.TypeResult type = (SCResult.TypeResult) result;
                                                if (type.modeltypes != null && type.modeltypes.size() > 0) {
                                                    typeData = new String[type.modeltypes.size()];
                                                    for (int i = 0; i < type.modeltypes.size(); i++) {
                                                        SCResult.Type temptype = type.modeltypes.get(i);
                                                        typeData[i] = temptype.typename;
                                                        typenameandcode.put(temptype.typename, temptype.typecode);
                                                    }
                                                    ShowTypeAlert();
                                                }
                                            }
                                        });
                                    }

                                    @Override
                                    public void onError(int code, final String errormsg) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(AddModelActivity.this, errormsg, Toast.LENGTH_SHORT).show();

                                            }
                                        });
                                    }
                                });
                            } else {
                                Toast.makeText(AddModelActivity.this, SCExceptionCodeList.getExceptionMsg(resultcode), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            });
        } else {
            ShowTypeAlert();
        }
    }

    private String temptypecode = "";
    private String temptypename = "";
    private void ShowTypeAlert() {
        if (typeAlert == null) {
            typeAlert = new AlertDialog.Builder(AddModelActivity.this)
                    .setTitle("选择型号分类")
                    .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            typename = temptypename;
                            typecode = temptypecode;
                            tv_category.setText(typename);
                            typeAlert.dismiss();

                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            typeAlert.dismiss();
                        }
                    })
                    .setSingleChoiceItems(typeData, -1, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            temptypecode = typenameandcode.get(typeData[which]);
                            temptypename = typeData[which];
                        }
                    }).create();
        }
        typeAlert.show();
    }

    private void ClickStore() {
        if (storeData == null) {
            Session.CheckRefreshToken(new RefreshTokenListener() {
                @Override
                public void RefreshTokenResult(final int resultcode) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (resultcode == Codes.Code_Success) {
                                SCSDK.getInstance().GetStoreList(Session.getInstance().getShopCode(),Session.getInstance().getToken(), new SCResponseListener() {
                                    @Override
                                    public void onResult(final Object result) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                SCResult.StoreResult store = (SCResult.StoreResult) result;
                                                if (store.stores != null && store.stores.size() > 0) {
                                                    storeData = new String[store.stores.size()];
                                                    for (int i = 0; i < store.stores.size(); i++) {
                                                        SCResult.Store tempstore = store.stores.get(i);
                                                        storeData[i] = tempstore.name;
                                                        storenameandcode.put(tempstore.name, tempstore.code);
                                                    }
                                                    ShowStoreAlert();
                                                }
                                            }
                                        });
                                    }

                                    @Override
                                    public void onError(int code, final String errormsg) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(AddModelActivity.this, errormsg, Toast.LENGTH_SHORT).show();

                                            }
                                        });
                                    }
                                });
                            } else {
                                Toast.makeText(AddModelActivity.this, SCExceptionCodeList.getExceptionMsg(resultcode), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            });
        } else {
            ShowStoreAlert();
        }

    }

    private String newchoosestore="";
    private String newchoosestorecode="";
    private void ShowStoreAlert() {
        if (storeAlert == null) {
            storeAlert = new AlertDialog.Builder(AddModelActivity.this)
                    .setTitle("选择所属仓库")
                    .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if(!storename.equals(newchoosestore)){
                                storename = newchoosestore;
                                storecode = newchoosestorecode;
                                rackData = null;
                                locationData = null;
                                tv_rack.setText("");
                                rackname = "";
                                rackcode = "";
                                tv_location.setText("");
                                locationname = "";
                                locationcode = "";
                                tv_storename.setText(storename);
                                Toast.makeText(AddModelActivity.this, "修改成功,请继续选择所属货架", Toast.LENGTH_SHORT).show();
                                ClickRack();
                            }
                            storeAlert.dismiss();
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            storeAlert.dismiss();
                        }
                    })
                    .setSingleChoiceItems(storeData, -1, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            newchoosestorecode = storenameandcode.get(storeData[which]);
                            newchoosestore = storeData[which];
                        }
                    }).create();
        }
        storeAlert.show();
    }


    private void ClickRack() {
        if (storecode != null && !storecode.equals("")) {
            if(rackData == null) {
                Session.CheckRefreshToken(new RefreshTokenListener() {
                    @Override
                    public void RefreshTokenResult(final int resultcode) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (resultcode == Codes.Code_Success) {
                                    SCSDK.getInstance().GetRackList(Session.getInstance().getShopCode(),storecode, Session.getInstance().getToken(), new SCResponseListener() {
                                        @Override
                                        public void onResult(final Object result) {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    SCResult.RackResult rack = (SCResult.RackResult) result;
                                                    if (rack.racks != null && rack.racks.size() > 0) {
                                                        rackData = new String[rack.racks.size()];
                                                        for (int i = 0; i < rack.racks.size(); i++) {
                                                            SCResult.Rack temprack = rack.racks.get(i);
                                                            rackData[i] = temprack.name;
                                                            racknameandcode.put(temprack.name, temprack.code);
                                                        }
                                                        ShowRackAlert(true);
                                                    } else {
                                                        rackData = null;
                                                        racknameandcode.clear();
                                                    }
                                                }
                                            });
                                        }

                                        @Override
                                        public void onError(int code, final String errormsg) {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Toast.makeText(AddModelActivity.this, errormsg, Toast.LENGTH_SHORT).show();
                                                    rackData = null;
                                                    racknameandcode.clear();
                                                }
                                            });
                                        }
                                    });
                                } else {
                                    rackData = null;
                                    racknameandcode.clear();
                                    Toast.makeText(AddModelActivity.this, SCExceptionCodeList.getExceptionMsg(resultcode), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
            }
            else{
                ShowRackAlert(false);
            }
        } else {
            Toast.makeText(AddModelActivity.this, "请先选择所属仓库", Toast.LENGTH_SHORT).show();
        }

    }

    private String newchooserack="";
    private String newchooserackcode="";
    private void ShowRackAlert(boolean isfresh) {
        if (rackData != null && rackData.length > 0) {
            if(isfresh) {
                rackAlert = new AlertDialog.Builder(AddModelActivity.this)
                        .setTitle("选择所属货架")
                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (!rackname.equals(newchooserack)) {
                                    rackname = newchooserack;
                                    rackcode = newchooserackcode;
                                    locationData = null;
                                    tv_location.setText("");
                                    locationcode = "";
                                    locationname = "";
                                    tv_rack.setText(rackname);
                                    Toast.makeText(AddModelActivity.this, "修改成功,请继续选择所在位置", Toast.LENGTH_SHORT).show();
                                    ClickLocation();
                                }

                                rackAlert.dismiss();

                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                rackAlert.dismiss();
                            }
                        })
                        .setSingleChoiceItems(rackData, -1, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                newchooserackcode = racknameandcode.get(rackData[which]);
                                newchooserack = rackData[which];
                            }
                        }).create();
            }
            rackAlert.show();
        }
    }

    private void ClickLocation() {
        if (rackcode != null && !rackcode.equals("")) {
            if(locationData == null) {
                Session.CheckRefreshToken(new RefreshTokenListener() {
                    @Override
                    public void RefreshTokenResult(final int resultcode) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (resultcode == Codes.Code_Success) {
                                    SCSDK.getInstance().GetPositionList(Session.getInstance().getShopCode(),storecode, rackcode, Session.getInstance().getToken(), new SCResponseListener() {
                                        @Override
                                        public void onResult(final Object result) {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    SCResult.PositionResult position = (SCResult.PositionResult) result;
                                                    if (position.positions != null && position.positions.size() > 0) {
                                                        locationData = new String[position.positions.size()];
                                                        for (int i = 0; i < position.positions.size(); i++) {
                                                            SCResult.Position tempposition = position.positions.get(i);
                                                            locationData[i] = tempposition.name;
                                                            locationnameandcode.put(tempposition.name, tempposition.code);
                                                        }
                                                        ShowLocationAlert(true);
                                                    } else {
                                                        locationData = null;
                                                        locationnameandcode.clear();
                                                    }
                                                }
                                            });
                                        }

                                        @Override
                                        public void onError(int code, final String errormsg) {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Toast.makeText(AddModelActivity.this, errormsg, Toast.LENGTH_SHORT).show();
                                                    locationData = null;
                                                    locationnameandcode.clear();
                                                }
                                            });
                                        }
                                    });
                                } else {
                                    locationData = null;
                                    locationnameandcode.clear();
                                    Toast.makeText(AddModelActivity.this, SCExceptionCodeList.getExceptionMsg(resultcode), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
            }
            else{
                ShowLocationAlert(false);
            }
        } else {
            Toast.makeText(AddModelActivity.this, "请先选择所属货架", Toast.LENGTH_SHORT).show();
        }

    }

    private void ShowLocationAlert(boolean isfresh) {
        if (locationData != null && locationData.length > 0) {
            if(isfresh) {
                locationAlert = new AlertDialog.Builder(AddModelActivity.this)
                        .setTitle("选择所在位置")
                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                tv_location.setText(locationname);
                                locationAlert.dismiss();
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                locationAlert.dismiss();
                            }
                        })
                        .setSingleChoiceItems(locationData, -1, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                locationcode = locationnameandcode.get(locationData[which]);
                                locationname = locationData[which];
                            }
                        }).create();
            }
            locationAlert.show();
        }
    }

    private void Add() {
        Session.CheckRefreshToken(new RefreshTokenListener() {
            @Override
            public void RefreshTokenResult(final int resultcode) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (resultcode == Codes.Code_Success) {
                            if (!CheckAddData()) {
                                return;
                            }
                            SCSDK.getInstance().AddModel(Session.getInstance().getShopCode(),modelname,Session.getInstance().getToken(), typecode, identifycode, guige,remark,compressPic,storecode, rackcode, locationcode,  danwei,safecount, needcheck, new SCResponseListener() {
                                        @Override
                                        public void onResult(Object result) {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Toast.makeText(AddModelActivity.this, "新增型号成功", Toast.LENGTH_SHORT).show();
                                                    finish();
                                                }
                                            });
                                        }

                                        @Override
                                        public void onError(int code, final String errormsg) {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Toast.makeText(AddModelActivity.this, errormsg, Toast.LENGTH_SHORT).show();

                                                }
                                            });
                                        }
                                    });
                        } else {
                            Toast.makeText(AddModelActivity.this, SCExceptionCodeList.getExceptionMsg(resultcode), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    private boolean CheckAddData() {
        if(identifycode.equals("")){
            Toast.makeText(AddModelActivity.this, "数字标识不能为空", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (guige.equals("")) {
            Toast.makeText(AddModelActivity.this, "型号规格不能为空", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (modelname.equals("")) {
            Toast.makeText(AddModelActivity.this, "型号名称不能为空", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (typename.equals("")) {
            Toast.makeText(AddModelActivity.this, "型号分类不能为空", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
