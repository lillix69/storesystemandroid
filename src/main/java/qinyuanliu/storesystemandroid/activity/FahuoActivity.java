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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import qinyuanliu.storesystemandroid.R;
import qinyuanliu.storesystemandroid.adapter.FahuoAdapter;
import qinyuanliu.storesystemandroid.adapter.SaleSnsAdapter;
import qinyuanliu.storesystemandroid.common.Session;
import qinyuanliu.storesystemandroid.http.Codes;
import qinyuanliu.storesystemandroid.http.SCExceptionCodeList;
import qinyuanliu.storesystemandroid.http.SCResponseListener;
import qinyuanliu.storesystemandroid.http.SCResult;
import qinyuanliu.storesystemandroid.http.SCSDK;
import qinyuanliu.storesystemandroid.http.util.Json;
import qinyuanliu.storesystemandroid.listener.AddModelScanListener;
import qinyuanliu.storesystemandroid.listener.DeleteSnsListener;
import qinyuanliu.storesystemandroid.listener.EditSendCountListener;
import qinyuanliu.storesystemandroid.listener.RefreshTokenListener;
import qinyuanliu.storesystemandroid.listener.ShowSaleSnsListener;
import qinyuanliu.storesystemandroid.model.AddSaleModel;
import qinyuanliu.storesystemandroid.model.FahuoModel;
import qinyuanliu.storesystemandroid.util.DateUtil;
import qinyuanliu.storesystemandroid.util.HeadImageUtil;

/**
 * Created by qinyuanliu on 2019/8/4.
 */

public class FahuoActivity extends BaseActivity {
    private Button btn_back;
    private ListView listview_models;
    private TextView tv_confirm;
    private TextView tv_saleordercode;
    private TextView tv_clientinfo;
    private TextView tv_orderdate;
    private EditText edt_logisticscode;
    private EditText edt_remark;
    private ImageView img_pic;

    private String clientinfo = "";
    private String remark = "";
    private String logisticscode = "";
    private String orderdate = "";
    private String picstr = "";
    private String saleordercode;

    private Calendar calendarEnd = Calendar.getInstance();
    private int endYear = 0;
    private int endMonth = 0;
    private int endDay = 0;

    // 裁剪后图片的宽(X)和高(Y),480 X 480的正方形。
    private static int output_X = 160;
    private static int output_Y = 160;
    private String mExtStorDir;
    private Uri mUriPath;
    private final int PERMISSION_READ = 1;//读取权限
    private static final int CODE_GALLERY_REQUEST = 0xa0;
    private static final int CODE_CAMERA_REQUEST = 0xa1;
    private static final int CODE_RESULT_REQUEST = 0xa2;
    private static final String CROP_IMAGE_FILE_NAME = "wuliu_crop.jpg";
    //调用照相机返回图片文件
    private File tempFile;

private FahuoAdapter fahuoAdapter;
    private ArrayList<FahuoModel> fahuomodels = new ArrayList<>();
private String  currenteditcode;
    final int SCANMODEL = 1005;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fahuo);

        mExtStorDir = Environment.getExternalStorageDirectory().toString();
        saleordercode = getIntent().getStringExtra("saleordercode");
        clientinfo = getIntent().getStringExtra("clientinfo");
        ArrayList<String> tempstrlist = getIntent().getStringArrayListExtra("modellist");

        for (String tempstr:tempstrlist) {
            SCResult.SaleOrderModel tempdata = Json.decode(tempstr,SCResult.SaleOrderModel.class);
            //是否是元器件 0：不是 1：是,
            boolean tempismin = tempdata.ismin == 0 ? false : true;
            fahuomodels.add(new FahuoModel(tempdata.lineno,tempdata.modelcode,tempdata.modelname,tempismin,tempdata.spec,String.valueOf(tempdata.salescount),String.valueOf(tempdata.storedcount),tempdata.mealproduct, tempdata.mealproductname));
        }
        initView();
        initData();
        RefreshListview();
    }


    private void initView() {
        btn_back = (Button) findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
                }
                finish();
            }
        });
        listview_models = (ListView)findViewById(R.id.listview_models);
        tv_confirm = (TextView)findViewById(R.id.tv_confirm);
        tv_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClickFahuo();
            }
        });
        edt_remark = (EditText) findViewById(R.id.tv_remark);
        edt_remark.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                remark = s.toString();
            }
        });
        edt_logisticscode = (EditText)findViewById(R.id.edt_logisticscode);
        edt_logisticscode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                logisticscode = s.toString();
            }
        });
        tv_saleordercode = (TextView)findViewById(R.id.tv_saleordercode);
        tv_saleordercode.setText(saleordercode);
        tv_clientinfo = (TextView)findViewById(R.id.tv_clientinfo);
        tv_clientinfo.setText(clientinfo);
        tv_orderdate = (TextView)findViewById(R.id.tv_orderdate);
        tv_orderdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClickDate();
            }
        });
        img_pic = (ImageView)findViewById(R.id.img_pic);
        img_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClickImg();
            }
        });
    }

private void initData(){
    calendarEnd.setTime(new Date());
    endYear = calendarEnd.get(Calendar.YEAR);
    endMonth = calendarEnd.get(Calendar.MONTH);
    endDay = calendarEnd.get(Calendar.DATE);
    orderdate = DateUtil.GetDateString(endYear, endMonth + 1, endDay);
    tv_orderdate.setText(orderdate);
}

    private void ClickDate() {
        View view = View.inflate(FahuoActivity.this, R.layout.date_picker, null);
        final DatePicker datePicker = (DatePicker) view.findViewById(R.id.new_act_date_picker);
        datePicker.init(endYear, endMonth, endDay, null);
        // Build DateTimeDialog
        AlertDialog.Builder timeBuilder = new AlertDialog.Builder(FahuoActivity.this);
        timeBuilder.setView(view);
       timeBuilder.setTitle("选择发货日期");
        timeBuilder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                endYear = datePicker.getYear();
                endMonth = datePicker.getMonth();
                endDay = datePicker.getDayOfMonth();
                String str = DateUtil.GetDateString(endYear, endMonth + 1, endDay);
                orderdate = str;
                tv_orderdate.setText(str);
            }
        });
        timeBuilder.show();
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

    private void ClickImg(){
        new AlertDialog.Builder(this).setTitle("添加物流图片")
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
    private void checkCameraPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(FahuoActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(FahuoActivity.this, "请先开启手机摄像头使用权限", Toast.LENGTH_SHORT).show();
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
               picstr = HeadImageUtil.BitmapToString(compresspic);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }else if(requestCode == SCANMODEL){

                Bundle b = data.getExtras();
                ArrayList<String> templist = b.getStringArrayList("modellist");
            for (final FahuoModel model:
                    fahuomodels) {
                if(model.getModelCode().equals(currenteditcode)){
                    for (final String tempid : templist) {
                        SCSDK.getInstance().GetProductdetailByScan(Session.getInstance().getShopCode(),tempid, Session.getInstance().getToken(), new SCResponseListener() {
                            @Override
                            public void onResult(Object result) {
                                final SCResult.ProductDetailResult product = (SCResult.ProductDetailResult) result;
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if(currenteditcode.equals(product.modelcode)) {
                                                    model.getSn().add(tempid);
                                                    int count = model.getSn().size();
                                                    model.setSendCount(String.valueOf(count));
                                            RefreshListview();
                                        }
                                        else{
                                            Toast.makeText(FahuoActivity.this,"条码【" + tempid + "】不是当前型号！", Toast.LENGTH_SHORT).show();

                                        }
                                    }
                                });
                            }

                            @Override
                            public void onError(final int code, final String errormsg) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(FahuoActivity.this, errormsg, Toast.LENGTH_SHORT).show();

                                    }
                                });
                            }
                        });
                    }

                    break;
                }

            }
//                for (FahuoModel model:
//                        fahuomodels) {
//                    if(model.getModelCode().equals(currenteditcode)){
//                        for (String tempid : templist) {
//                            model.getSn().add(tempid);
//                        }
//                        int count = model.getSn().size();
//                        model.setSendCount(String.valueOf(count));
//                        break;
//                    }
//
//                }
//                RefreshListview();

        }

        else {
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


    private void RefreshListview() {
        if (fahuoAdapter == null) {
            fahuoAdapter = new FahuoAdapter(FahuoActivity.this, fahuomodels, new ShowSaleSnsListener() {
                        @Override
                        public void ShowSaleSns(String modelcode) {
                            currenteditcode = modelcode;
                            for (FahuoModel fahuo :fahuomodels) {
                                if (fahuo.getModelCode().equals(modelcode)) {
                                    if (fahuo.getSn() != null && fahuo.getSn().size() > 0) {
                                        ArrayList<String> snslist = new ArrayList<>();
                                        for (String sn : fahuo.getSn()) {
                                            snslist.add(sn);
                                        }
                                        ShowSnsDetail(snslist);
                                    } else {
                                        Toast.makeText(FahuoActivity.this, "已扫序列号为空!", Toast.LENGTH_SHORT).show();
                                    }
                                    break;
                                }
                            }
                        }
                    },

                    new AddModelScanListener() {
                        @Override
                        public void AddModelScan(int index,String code) {
                            currenteditcode = code;
                            ClickScan();
                        }
                    },
                    new EditSendCountListener() {
                        @Override
                        public void EditSendCount(String modelcode, String count) {
                            for (int i = 0; i < fahuomodels.size(); i++) {
                                if (fahuomodels.get(i).getModelCode().equals(modelcode)) {
                                    fahuomodels.get(i).setSendCount(count);
                                    break;
                                }
                            }
                        }
                    });

            listview_models.setAdapter(fahuoAdapter);
        } else {
            fahuoAdapter.setModellist(fahuomodels);
            fahuoAdapter.notifyDataSetChanged();
        }
    }
    private  AlertDialog scanAlert;
    private void ClickScan(){
        if(scanAlert == null) {
            scanAlert = new AlertDialog.Builder(this).setTitle("选择扫码方式")
                    .setCancelable(true)
                    .setPositiveButton("扫码", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent scanintent = new Intent(FahuoActivity.this, MipcaActivityCapture.class);
                            scanintent.putExtra("isSingle", false);
                            startActivityForResult(scanintent, SCANMODEL);
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
        final EditText et = new EditText(FahuoActivity.this);
        et.setFocusable(true);
        et.setFocusableInTouchMode(true);
        et.requestFocus();
        AlertDialog.Builder alert = new AlertDialog.Builder(FahuoActivity.this).setTitle("请输入单个条码")
                .setView(et)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String input = et.getText().toString();
                        if(input != null && !input.equals("")) {
                            //检查输入条码是否属于该型号
                            CheckIsModelSn(input);

//                            //输入编码整理
//                            List<String> templist = Arrays.asList(input.split(","));
//                            for (FahuoModel model:
//                                    fahuomodels) {
//                                if(model.getModelCode().equals(currenteditcode)){
//                                    for (String tempid : templist) {
//                                        model.getSn().add(tempid);
//                                    }
//                                    int count = model.getSn().size();
//                                    model.setSendCount(String.valueOf(count));
//                                    break;
//                                }
//                            }
                           // RefreshListview();
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

    private void CheckIsModelSn(final String checksn){
        SCSDK.getInstance().GetProductdetailByScan(Session.getInstance().getShopCode(), checksn, Session.getInstance().getToken(), new SCResponseListener() {
            @Override
            public void onResult(Object result) {
                final SCResult.ProductDetailResult product = (SCResult.ProductDetailResult) result;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(currenteditcode.equals(product.modelcode)) {
                            for (FahuoModel model: fahuomodels) {
                                if(model.getModelCode().equals(currenteditcode)){
                                        model.getSn().add(checksn);
                                    int count = model.getSn().size();
                                    model.setSendCount(String.valueOf(count));
                                    break;
                                }
                            }
                             RefreshListview();
                        }
                        else{
                            Toast.makeText(FahuoActivity.this,"条码【" + checksn + "】不是当前型号！", Toast.LENGTH_SHORT).show();

                        }
                    }
                });
            }

            @Override
            public void onError(final int code, final String errormsg) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                            Toast.makeText(FahuoActivity.this, errormsg, Toast.LENGTH_SHORT).show();

                    }
                });
            }
        });

    }

    SaleSnsAdapter adapter;
    private void ShowSnsDetail(final ArrayList<String> snslist) {

        LayoutInflater inflater = LayoutInflater.from(this);// 渲染器
        View customdialog2view = inflater.inflate(R.layout.popup_salesnsdetail,
                null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("序列号明细");
        builder.setView(customdialog2view);
        builder.setNegativeButton("关闭",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //同步sns数量
                        for (int i=0; i< fahuomodels.size(); i++) {
                            if (fahuomodels.get(i).getModelCode().equals(currenteditcode)) {
                                int editcount = fahuomodels.get(i).getSn().size();
                                fahuomodels.get(i).setSendCount(String.valueOf(editcount));
                                break;
                            }
                        }
                        RefreshListview();

                    }
                });

        ListView listView = (ListView) customdialog2view
                .findViewById(R.id.lv);

        adapter = new SaleSnsAdapter(this, snslist, new DeleteSnsListener() {
            @Override
            public void DeleteSns(String sns) {
                for (int i = 0; i < fahuomodels.size(); i++) {
                    if (fahuomodels.get(i).getModelCode().equals(currenteditcode)) {
                        fahuomodels.get(i).getSn().remove(sns);
                        break;
                    }
                }
                for(int j=0;j<snslist.size();j++){
                    if(snslist.get(j).equals(sns)){
                        snslist.remove(j);
                        break;
                    }
                }
                adapter.notifyDataSetChanged();
            }
        }
        );
        listView.setAdapter(adapter);
        listView.setItemsCanFocus(false);
        listView.setChoiceMode(ListView.CHOICE_MODE_NONE);

        AlertDialog dialog = builder.create();
        dialog.show();

    }


    private void ClickFahuo(){
        InputMethodManager imm =  (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        if(imm != null) {
            imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
        }

        //models:型号编码-发货数量-【序列号1：序列号2：序列号3】，型号编码-发货数量
        Session.CheckRefreshToken(new RefreshTokenListener() {
            @Override
            public void RefreshTokenResult(final int resultcode) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (resultcode == Codes.Code_Success) {
                            //1st
                            String models = fahuomodels.get(0).getLino()+ "-" + fahuomodels.get(0).getModelCode() + "-";
                            if(fahuomodels.get(0).getSendCount().equals("")){
                                models = models + "0";
                            }
                            else{
                                models = models + fahuomodels.get(0).getSendCount();
                            }
                            if(!fahuomodels.get(0).getIsMin()){
                                models = models + "-[" ;
                                List<String> snlist = new ArrayList(fahuomodels.get(0).getSn());
                                StringBuffer str2 = new StringBuffer();
                                for (Iterator<String> iterator = snlist.iterator(); iterator.hasNext();) {
                                    String string = (String) iterator.next();
                                    str2.append(string);
                                    if(iterator.hasNext()){
                                        str2.append(":");
                                    }
                                }
                                models = models + str2 +  "]";
                            }

                            //2....
                            for(int i=1; i<fahuomodels.size(); i++){
                                models = models + "," + fahuomodels.get(i).getLino()+ "-" + fahuomodels.get(i).getModelCode() + "-";
                                if(fahuomodels.get(i).getSendCount().equals("")){
                                    models = models + "0";
                                }
                                else{
                                    models = models + fahuomodels.get(i).getSendCount();
                                }
                                if(!fahuomodels.get(i).getIsMin()){
                                    models = models + "-[" ;
                                    List<String> snlist = new ArrayList(fahuomodels.get(i).getSn());
                                    StringBuffer str2 = new StringBuffer();
                                    for (Iterator<String> iterator = snlist.iterator(); iterator.hasNext();) {
                                        String string = (String) iterator.next();
                                        str2.append(string);
                                        if(iterator.hasNext()){
                                            str2.append(":");
                                        }
                                    }
                                    models = models + str2 +  "]";
                                }
                            }
                            System.out.println(models);

                            SCSDK.getInstance().StoreSaleOrder(saleordercode,orderdate,logisticscode,picstr,remark,models,Session.getInstance().getShopCode(), Session.getInstance().getToken(), new SCResponseListener() {
                                @Override
                                public void onResult(Object result) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(FahuoActivity.this, "发货成功！", Toast.LENGTH_SHORT).show();
                                            finish();
                                        }
                                    });
                                }

                                @Override
                                public void onError(int code, final String errormsg) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(FahuoActivity.this, errormsg, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            });
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(FahuoActivity.this, SCExceptionCodeList.getExceptionMsg(resultcode), Toast.LENGTH_SHORT).show();

                                }
                            });
                        }
                    }
                });
            }
        });
    }

}
