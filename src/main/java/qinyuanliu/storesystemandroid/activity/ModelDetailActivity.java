package qinyuanliu.storesystemandroid.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import qinyuanliu.storesystemandroid.R;
import qinyuanliu.storesystemandroid.adapter.SnsAdapter;
import qinyuanliu.storesystemandroid.common.Session;
import qinyuanliu.storesystemandroid.http.Codes;
import qinyuanliu.storesystemandroid.http.SCExceptionCodeList;
import qinyuanliu.storesystemandroid.http.SCResponseListener;
import qinyuanliu.storesystemandroid.http.SCResult;
import qinyuanliu.storesystemandroid.http.SCSDK;
import qinyuanliu.storesystemandroid.http.util.StringUtils;
import qinyuanliu.storesystemandroid.listener.ClickPrintListener;
import qinyuanliu.storesystemandroid.listener.RefreshTokenListener;
import qinyuanliu.storesystemandroid.model.SubmitPrepareModel;
import qinyuanliu.storesystemandroid.util.DensityUtil;
import qinyuanliu.storesystemandroid.util.FlowRadioGroup;
import qinyuanliu.storesystemandroid.util.HeadImageUtil;

/**
 * Created by lillix on 5/28/18.
 */
public class ModelDetailActivity extends BaseActivity {
    //implements HeadImageUtil.CropHandler {
    private Button btn_back;
    private ImageView img_pic;
    private TextView tv_id;
    private TextView tv_name;
    private TextView tv_storename;
    private TextView tv_rack;
    private TextView tv_location;
    private TextView tv_storecount;
    private TextView tv_category;
    private TextView tv_danwei;
    private TextView tv_guige;
    private TextView tv_price;
    private TextView tv_wuliu;
    private TextView tv_clientid;
    private TextView tv_safecount;
    private Button btn_print;
    private Button btn_out;
    private Button btn_in;
    private Button btn_look;
    private Button btn_scan;
    private Button btn_prepare;
    private TextView tv_check;
    private RelativeLayout printview;
    private TextView tv_suppliername;
    private TextView tv_remark;
    private Button btn_detail;
    private LinearLayout showdetailview;
    private Button btn_showstore;
    private RelativeLayout imgview;

    private String productid = "";
    private String productname = "";
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
    private String clientid = "";
    private double lastbuyin = 0.0;
    private double wuliu = 0.0;
    private int safecount = 0;
    private int needcheck = 0;
    private String suppliername = "";
    private String compressPic = null;
    private String originalPic;
    private int printnum = 1;
    private String remark = "";
    private int storecount = 0;

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
    private boolean isShown = false;
    // private static final String IMAGE_FILE_NAME = "temp_head_image.jpg";
    //调用照相机返回图片文件
    private File tempFile;
    private int powerlevel = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modeldetail);

        productid = getIntent().getStringExtra("productid");
        mExtStorDir = Environment.getExternalStorageDirectory().toString();
        initView();
        initData();
    }

    private void initView() {
        btn_detail = (Button) findViewById(R.id.btn_detail);
        btn_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isShown = !isShown;
                if (isShown) {
                    btn_detail.setText("收起明细>");
                    showdetailview.setVisibility(View.VISIBLE);
                } else {
                    btn_detail.setText("显示明细>");
                    showdetailview.setVisibility(View.GONE);
                }
            }
        });
        showdetailview = (LinearLayout) findViewById(R.id.showdetailview);
        btn_showstore = (Button) findViewById(R.id.btn_showstore);
        btn_showstore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (storecount > 0) {
                    Session.CheckRefreshToken(new RefreshTokenListener() {
                        @Override
                        public void RefreshTokenResult(final int resultcode) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (resultcode == Codes.Code_Success) {
                                        SCSDK.getInstance().SearchModelSns(Session.getInstance().getShopCode(), productid, Session.getInstance().getToken(), new SCResponseListener() {
                                            @Override
                                            public void onResult(Object result) {
                                                final SCResult.SnsResult res = (SCResult.SnsResult) result;
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        if (res.sninfos != null && res.sninfos.size() > 0) {
                                                            ShowSnsDetail(res.sninfos);
                                                        } else {
                                                            Toast.makeText(ModelDetailActivity.this, "当前型号没有序列号", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                            }

                                            @Override
                                            public void onError(int code, final String errormsg) {
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Toast.makeText(ModelDetailActivity.this, errormsg, Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }
                                        });

                                    }
                                    else {
                                        Toast.makeText(ModelDetailActivity.this, SCExceptionCodeList.getExceptionMsg(resultcode), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    });
                                } else {
                    Toast.makeText(ModelDetailActivity.this, "当前型号没有序列号", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btn_back = (Button) findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        img_pic = (ImageView) findViewById(R.id.img_pic);
        imgview = (RelativeLayout) findViewById(R.id.img_view);
        imgview.setOnClickListener(new View.OnClickListener() {
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
        tv_id = (TextView) findViewById(R.id.tv_id);
        tv_id.setText(productid);
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
        tv_storecount = (TextView) findViewById(R.id.tv_storecount);
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
//        tv_guige.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ClickGuige();
//            }
//        });
        tv_price = (TextView) findViewById(R.id.tv_price);
        tv_price.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClickLastBuyin();
            }
        });
        tv_wuliu = (TextView) findViewById(R.id.tv_wuliu);
        tv_wuliu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClickWuliu();
            }
        });
        tv_clientid = (TextView) findViewById(R.id.tv_clientid);
        tv_clientid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClickClientid();
            }
        });
        tv_safecount = (TextView) findViewById(R.id.tv_safecount);
        tv_safecount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClickSafecount();
            }
        });
        tv_suppliername = (TextView) findViewById(R.id.tv_suppliername);
        tv_suppliername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClickSupply();
            }
        });
        tv_remark = (TextView) findViewById(R.id.tv_remark);
        tv_remark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClickRemark();
            }
        });

        btn_print = (Button) findViewById(R.id.btn_print);
        btn_print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClickPrint();
            }
        });
        btn_out = (Button) findViewById(R.id.btn_out);
        btn_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (powerlevel == 1 || powerlevel == 4 || powerlevel == 5) {
                    Toast.makeText(ModelDetailActivity.this, "该账户没有出库权限！", Toast.LENGTH_SHORT).show();
                } else {
                    if (isValid()) {
                        Intent outintent = new Intent(ModelDetailActivity.this, OutStoreActivity.class);
                        outintent.putExtra("productid", productid);
                        outintent.putExtra("isparentdetail", true);
                        startActivityForResult(outintent, 2001);
                    } else {
                        Toast.makeText(ModelDetailActivity.this, "型号的仓库/货架/位置不能为空,请选择完成后再出库", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });

        btn_in = (Button) findViewById(R.id.btn_in);
        btn_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (powerlevel == 2 || powerlevel == 4 || powerlevel == 6) {
                    Toast.makeText(ModelDetailActivity.this, "该账户没有入库权限！", Toast.LENGTH_SHORT).show();
                } else {
                    if (isValid()) {
                        Intent inintent = new Intent(ModelDetailActivity.this, InStoreActivity.class);
                        inintent.putExtra("productid", productid);
                        inintent.putExtra("isparentdetail", true);
                        startActivityForResult(inintent, 2002);
                    } else {
                        Toast.makeText(ModelDetailActivity.this, "型号的仓库/货架/位置不能为空,请选择完成后再入库", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        btn_look = (Button) findViewById(R.id.btn_look);
        btn_look.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isValid()) {
                    Intent lookintent = new Intent(ModelDetailActivity.this, LookStoreActivity.class);
                    lookintent.putExtra("productid", productid);
                    lookintent.putExtra("isparentdetail", true);
                    startActivityForResult(lookintent, 2003);
                } else {
                    Toast.makeText(ModelDetailActivity.this, "型号的仓库/货架/位置不能为空,请选择完成后再盘库", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btn_scan = (Button) findViewById(R.id.btn_scan);
        btn_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(ModelDetailActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(ModelDetailActivity.this, "请先开启手机摄像头使用权限", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Settings.ACTION_SETTINGS);
                    startActivity(intent);
                } else {
                    ClickScan();
                }

            }
        });
        btn_prepare = (Button) findViewById(R.id.btn_prepare);
        btn_prepare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowPrepare();
            }
        });
        printview = (RelativeLayout) findViewById(R.id.printview);
    }

    private  AlertDialog scanAlert;
    private void ClickScan(){
        if(scanAlert == null) {
            scanAlert = new AlertDialog.Builder(this).setTitle("选择扫码方式")
                    .setCancelable(true)
                    .setPositiveButton("扫码", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent scanintent = new Intent(ModelDetailActivity.this, MipcaActivityCapture.class);
                            scanintent.putExtra("isSingle", true);
                            startActivity(scanintent);
                            finish();
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
        final EditText et = new EditText(ModelDetailActivity.this);
        et.setFocusable(true);
        et.setFocusableInTouchMode(true);
        et.requestFocus();
        AlertDialog.Builder alert = new AlertDialog.Builder(ModelDetailActivity.this).setTitle("请输入条码")
                .setView(et)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        final String input = et.getText().toString();
                        if(!input.equals("")) {
                            //判断是型号还是产品
                            Session.CheckRefreshToken(new RefreshTokenListener() {
                                @Override
                                public void RefreshTokenResult(final int resultcode) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (resultcode == Codes.Code_Success) {
                                                SCSDK.getInstance().GetTypeByScan(Session.getInstance().getShopCode(), input, Session.getInstance().getToken(), new SCResponseListener() {
                                                    @Override
                                                    public void onResult(Object result) {
                                                        final SCResult.ScanResult product = (SCResult.ScanResult) result;
                                                        runOnUiThread(new Runnable() {
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
                                                                    Toast.makeText(ModelDetailActivity.this, "无法识别输入的条码!", Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        });
                                                    }

                                                    @Override
                                                    public void onError(final int code, final String errormsg) {
                                                       runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                Toast.makeText(ModelDetailActivity.this, errormsg, Toast.LENGTH_SHORT).show();
                                                            }
                                                        });

                                                    }
                                                });
                                            } else {
                                                Toast.makeText(ModelDetailActivity.this, SCExceptionCodeList.getExceptionMsg(resultcode), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            });
                        }

                        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(et.getWindowToken(), 0) ;
                    }
                })
                .setNegativeButton("取消",  new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
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

    private void ShowInout(String qrcode) {
        Intent scanqr = new Intent(ModelDetailActivity.this, ScanInoutStoreActivity.class);
        scanqr.putExtra("scanQRcode", qrcode);
        startActivity(scanqr);
        finish();
    }

    private void ShowProduct(String productid) {
        Intent productintent = new Intent(ModelDetailActivity.this, ProductDetailActivity.class);
        productintent.putExtra("productid", productid);
        startActivity(productintent);
        finish();
    }

    private void ShowModel(String modelid) {
//        Intent productintent = new Intent(ModelDetailActivity.this, ModelDetailActivity.class);
//        productintent.putExtra("productid", modelid);
//        startActivity(productintent);
//        finish();
        productid = modelid;
        initData();
    }
    private void ShowBatch(String modelid) {
        Intent productintent = new Intent(ModelDetailActivity.this,BatchStoreActivity.class);
        productintent.putExtra("productid", modelid);
        startActivity(productintent);
        finish();
    }

    private int preparecount = 0;
    private FlowRadioGroup flowRadioGroup;
    private HashMap<String, String> stepDict = new HashMap<>();


    private void ShowPrepare() {
        Session.CheckRefreshToken(new RefreshTokenListener() {
            @Override
            public void RefreshTokenResult(final int resultcode) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (resultcode == Codes.Code_Success) {
                            SCSDK.getInstance().GetPrepareStep(Session.getInstance().getShopCode(), Session.getInstance().getToken(), productid, new SCResponseListener() {
                                @Override
                                public void onResult(Object result) {
                                    final SCResult.StepResult res = (SCResult.StepResult) result;
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            stepDict = new HashMap<>();
                                            preparecount = 0;
                                            LayoutInflater inflater = LayoutInflater.from(ModelDetailActivity.this);// 渲染器
                                            View customdialog2view = inflater.inflate(R.layout.popup_prepare,
                                                    null);
                                            EditText prepareEdt = (EditText) customdialog2view.findViewById(R.id.edt_preparecount);
                                            prepareEdt.addTextChangedListener(new TextWatcher() {
                                                @Override
                                                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                                }

                                                @Override
                                                public void onTextChanged(CharSequence s, int start, int before, int count) {

                                                }

                                                @Override
                                                public void afterTextChanged(Editable s) {
                                                    String temp = s.toString();
                                                    if (temp.length() > 0) {
                                                        preparecount = Integer.valueOf(temp);
                                                    } else {
                                                        preparecount = 0;
                                                    }
                                                }
                                            });
                                            flowRadioGroup = (FlowRadioGroup) customdialog2view.findViewById(R.id.flowradiogroup);
                                            flowRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                                                @Override
                                                public void onCheckedChanged(RadioGroup group, int checkedId) {
                                                }
                                            });
                                            int i = 0;
                                            for (SCResult.StepInfo step : res.steps) {
                                                RadioButton radioButton = new RadioButton(ModelDetailActivity.this);
                                                RadioGroup.LayoutParams layoutParams = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT, DensityUtil.dip2px(ModelDetailActivity.this, 40));
                                                radioButton.setLayoutParams(layoutParams);
                                                radioButton.setText(step.stepname);
                                                flowRadioGroup.addView(radioButton);
                                                stepDict.put(step.stepname, step.stepcode);
                                                if (i == 0) {
                                                    radioButton.setChecked(true);
                                                }
                                                i++;
                                            }

                                            AlertDialog.Builder builder = new AlertDialog.Builder(ModelDetailActivity.this);
                                            builder.setTitle("出库配料");
                                            builder.setView(customdialog2view);
                                            builder.setNegativeButton("取消",
                                                    new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {

                                                        }
                                                    });
                                            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    if (preparecount == 0) {
                                                        Toast.makeText(ModelDetailActivity.this, "请输入计划生产数量！", Toast.LENGTH_SHORT).show();
                                                        return;
                                                    }

                                                    String choosestep = flowRadioGroup.getCheckedRadioButtonText();
                                                    String stepcode = stepDict.get(choosestep);
                                                    Intent showprepare = new Intent(ModelDetailActivity.this, ShowPrepareActivity.class);
                                                    showprepare.putExtra("prepare_stepcode", stepcode);
                                                    showprepare.putExtra("prepare_modelcode", productid);
                                                    showprepare.putExtra("prepare_count", preparecount);
                                                    startActivity(showprepare);
                                                    // System.out.println("index:" + flowRadioGroup.getCheckedRadioButtonIndex());
                                                }
                                            });
                                            AlertDialog dialog = builder.create();
                                            dialog.show();
                                        }
                                    });
                                }

                                @Override
                                public void onError(int code, final String errormsg) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {

                                            Toast.makeText(ModelDetailActivity.this, errormsg, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            });
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(ModelDetailActivity.this, SCExceptionCodeList.getExceptionMsg(resultcode), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                });
            }
        });

    }

    private void ShowSnsDetail(final ArrayList<SCResult.SnsModel> snslist) {
        LayoutInflater inflater = LayoutInflater.from(this);// 渲染器
        View customdialog2view = inflater.inflate(R.layout.popup_snsdetail,
                null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("查看序列号");
        builder.setView(customdialog2view);
        builder.setNegativeButton("关闭",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        ListView listView = (ListView) customdialog2view
                .findViewById(R.id.lv);
        SnsAdapter adapter = new SnsAdapter(this, snslist, new ClickPrintListener() {
            @Override
            public void onClickPrint(final String sns) {
                Session.CheckRefreshToken(new RefreshTokenListener() {
                    @Override
                    public void RefreshTokenResult(final int resultcode) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (resultcode == Codes.Code_Success) {
                SCSDK.getInstance().PrintSpecificSn(Session.getInstance().getShopCode(), sns, Session.getInstance().getToken(), new SCResponseListener() {
                    @Override
                    public void onResult(Object result) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(ModelDetailActivity.this, "打印成功", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onError(int code, final String errormsg) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(ModelDetailActivity.this, errormsg, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
                                } else {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(ModelDetailActivity.this, SCExceptionCodeList.getExceptionMsg(resultcode), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }
                        });
                    }
                });
            }
        });
        listView.setAdapter(adapter);
        listView.setItemsCanFocus(false);
        listView.setChoiceMode(ListView.CHOICE_MODE_NONE);

        AlertDialog dialog = builder.create();
        dialog.show();

    }

    private boolean isValid() {
        if (storecode.equals("") || rackcode.equals("") || locationcode.equals("")) {
            return false;
        }
        return true;
    }

    private void initData() {
        Session.CheckRefreshToken(new RefreshTokenListener() {
            @Override
            public void RefreshTokenResult(final int resultcode) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (resultcode == Codes.Code_Success) {
                            //盘库权限设置按钮隐藏，出入库权限记录本地powerlevel
                            SCSDK.getInstance().GetProve(Session.getInstance().getShopCode(), Session.getInstance().getToken(), productid, new SCResponseListener() {
                                @Override
                                public void onResult(Object result) {
                                    if (result != null) {
                                        final SCResult.ProveResult res = (SCResult.ProveResult) result;
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                // 0:全部权限 1：只有入库权限 2：只有出库权限 3只有出入库权限
                                                //4只有盘库权限   5只有入库盘库权限   6只有出库盘库权限
                                                powerlevel = res.power;
                                                if(powerlevel == 0 || powerlevel == 4 || powerlevel == 5 || powerlevel == 6){
                                                    btn_look.setVisibility(View.VISIBLE);
                                                }
                                                else{
                                                    btn_look.setVisibility(View.GONE);
                                                }
                                            }
                                        });
                                    }
                                }
                                @Override
                                public void onError(int code, final String errormsg) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(ModelDetailActivity.this, errormsg, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            });

                            SCSDK.getInstance().GetProductDetail(Session.getInstance().getShopCode(), productid, Session.getInstance().getToken(), new SCResponseListener() {
                                @Override
                                public void onResult(Object result) {
                                    final SCResult.ModelDetailResult res = (SCResult.ModelDetailResult) result;
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (res.modelpicurl != null && !res.modelpicurl.equals("")) {
                                                originalPic = res.modelpicurl;
                                                Glide.with(ModelDetailActivity.this).load(originalPic)
                                                        .listener(new RequestListener<String, GlideDrawable>() {
                                                            @Override
                                                            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                                                Log.i("GlideException", "====" + e);
                                                                return false;
                                                            }

                                                            @Override
                                                            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                                                Log.i("GlideReady", "======载入成功");
                                                                return false;
                                                            }
                                                        })
                                                        .placeholder(R.drawable.icon_defaultimg)
                                                        .error(R.drawable.icon_defaultimg)
                                                        //    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                                                        .into(img_pic);
                                            } else {
                                                img_pic.setImageResource(R.drawable.icon_defaultimg);
                                            }
                                            tv_name.setText(res.modelname);
                                            tv_storename.setText(res.storename);
                                            tv_rack.setText(res.rackname);
                                            tv_location.setText(res.positionname);
                                            tv_storecount.setText(Integer.toString(res.store));
                                            tv_category.setText(res.typename);
                                            tv_danwei.setText(res.unit);
                                            tv_guige.setText(res.spec);
                                            tv_price.setText("¥" + Double.toString(res.lastedbuyin));
                                            tv_wuliu.setText("¥" + Double.toString(res.lastedtrace));
                                            tv_clientid.setText(res.customercode);
                                            tv_safecount.setText(Integer.toString(res.min));
                                            if (res.needcheck == 0) {
                                                tv_check.setText("不需要");
                                            } else {
                                                tv_check.setText("需要");
                                            }
                                            tv_suppliername.setText(res.suppliername);
                                            if (res.ismin == 1) {
                                                printview.setVisibility(View.GONE);
                                                btn_showstore.setVisibility(View.INVISIBLE);
                                            } else {
                                                printview.setVisibility(View.VISIBLE);
                                                btn_showstore.setVisibility(View.VISIBLE);
                                            }
                                            tv_remark.setText(res.remark);
                                            if (res.allowbatch == 1) {
                                                btn_prepare.setVisibility(View.VISIBLE);
                                            } else {
                                                btn_prepare.setVisibility(View.GONE);
                                            }
                                            productid = res.modelcode;
                                            storecount = res.store;
                                            originalPic = res.modelpicurl;
                                            storecode = res.storecode == null ? "" : res.storecode;
                                            storename = res.storename == null ? "" : res.storename;
                                            rackcode = res.rackcode == null ? "" : res.rackcode;
                                            rackname = res.rackname == null ? "" : res.rackname;
                                            locationcode = res.positioncode == null ? "" : res.positioncode;
                                            locationname = res.positionname == null ? "" : res.positionname;
                                            typecode = res.typecode == null ? "" : res.typecode;
                                            typename = res.typename == null ? "" : res.typename;
                                            productname = res.modelname == null ? "" : res.modelname;
                                            danwei = res.unit == null ? "" : res.unit;
                                            guige = res.spec == null ? "" : res.spec;
                                            lastbuyin = res.lastedbuyin;
                                            wuliu = res.lastedtrace;
                                            clientid = res.customercode == null ? "" : res.customercode;
                                            safecount = res.min;
                                            needcheck = res.needcheck;
                                            suppliername = res.suppliername == null ? "" : res.suppliername;
                                            remark = res.remark == null ? "" : res.remark;
                                        }
                                    });
                                }

                                @Override
                                public void onError(int code, final String errormsg) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(ModelDetailActivity.this, errormsg, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            });
                        } else {
                            Toast.makeText(ModelDetailActivity.this, SCExceptionCodeList.getExceptionMsg(resultcode), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    private void ClickPrint() {
        final EditText et = new EditText(this);
        et.setInputType(InputType.TYPE_CLASS_NUMBER);
        et.setText(Integer.toString(printnum));
        et.setFocusable(true);
        et.setFocusableInTouchMode(true);
        et.requestFocus();
        AlertDialog.Builder alert = new AlertDialog.Builder(this).setTitle("编辑要打印的数量")
                .setView(et)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String input = et.getText().toString();

                        if (!input.equals("")) {
                            if (StringUtils.isvalidNumber(input)) {
                                printnum = Integer.valueOf(input);
                                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(et.getWindowToken(), 0);
                                Print();
                            } else {
                                Toast.makeText(ModelDetailActivity.this, "请输入整数值", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(ModelDetailActivity.this, "请输入整数值", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(et.getWindowToken(), 0);
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

    private void Print() {
        Session.CheckRefreshToken(new RefreshTokenListener() {
            @Override
            public void RefreshTokenResult(final int resultcode) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (resultcode == Codes.Code_Success) {
                            SCSDK.getInstance().PrintBarcode(Session.getInstance().getShopCode(), productid, printnum, Session.getInstance().getToken(), new SCResponseListener() {
                                @Override
                                public void onResult(Object result) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(ModelDetailActivity.this, "打印成功", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }

                                @Override
                                public void onError(int code, final String errormsg) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(ModelDetailActivity.this, errormsg, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            });
                        } else {
                            Toast.makeText(ModelDetailActivity.this, SCExceptionCodeList.getExceptionMsg(resultcode), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    private void ClickImage() {
        WindowManager wm = this.getWindowManager();
        int width = wm.getDefaultDisplay().getWidth() / 2;
        final ImageView pic = new ImageView(ModelDetailActivity.this);
        pic.setAdjustViewBounds(true);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                width, LinearLayout.LayoutParams.WRAP_CONTENT);
        pic.setLayoutParams(layoutParams);
        pic.setMaxWidth(width);
        pic.setMaxHeight(width);
        Glide.with(ModelDetailActivity.this).load(originalPic)
                .placeholder(R.drawable.icon_defaultimg)
                .error(R.drawable.icon_defaultimg)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(pic);

        new AlertDialog.Builder(this).setTitle("编辑型号图片")
                .setView(pic)
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
            if (ContextCompat.checkSelfPermission(ModelDetailActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(ModelDetailActivity.this, "请先开启手机摄像头使用权限", Toast.LENGTH_SHORT).show();
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
                Modify();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else if (requestCode == 2001
                || requestCode == 2002 || requestCode == 2003) {
            if (resultCode == RESULT_OK) {
                initData();
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
        et.setText(productname);
        et.setFocusable(true);
        et.setFocusableInTouchMode(true);
        et.requestFocus();
        AlertDialog.Builder alert = new AlertDialog.Builder(this).setTitle("编辑型号名称")
                .setView(et)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String input = et.getText().toString();
                        if (input.equals("")) {
                            Toast.makeText(ModelDetailActivity.this, "名称不能为空", Toast.LENGTH_SHORT).show();
                        } else {
                            tv_name.setText(input);
                            productname = input;
                            Modify();
                        }
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(et.getWindowToken(), 0);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(et.getWindowToken(), 0);
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

    private void ClickRemark() {
        final EditText et = new EditText(this);
        et.setText(remark);
        et.setFocusable(true);
        et.setFocusableInTouchMode(true);
        et.requestFocus();
        AlertDialog.Builder alert = new AlertDialog.Builder(this).setTitle("编辑型号备注")
                .setView(et)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String input = et.getText().toString();
                        if (input.equals("")) {
                            Toast.makeText(ModelDetailActivity.this, "备注不能为空", Toast.LENGTH_SHORT).show();
                        } else {
                            tv_remark.setText(input);
                            remark = input;
                            Modify();
                        }
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(et.getWindowToken(), 0);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(et.getWindowToken(), 0);
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

    private void ClickUnit() {
        final EditText et = new EditText(this);
        et.setText(danwei);
        et.setFocusable(true);
        et.setFocusableInTouchMode(true);
        et.requestFocus();
        AlertDialog.Builder alert = new AlertDialog.Builder(this).setTitle("编辑单位")
                .setView(et)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String input = et.getText().toString();
                        if (input.equals("")) {
                            Toast.makeText(ModelDetailActivity.this, "单位不能为空", Toast.LENGTH_SHORT).show();
                        } else {
                            tv_danwei.setText(input);
                            danwei = input;
                            Modify();
                        }
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(et.getWindowToken(), 0);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(et.getWindowToken(), 0);
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
        AlertDialog.Builder alert = new AlertDialog.Builder(this).setTitle("编辑规格")
                .setView(et)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String input = et.getText().toString();
                        if (input.equals("")) {
                            Toast.makeText(ModelDetailActivity.this, "规格不能为空", Toast.LENGTH_SHORT).show();
                        } else {
                            tv_guige.setText(input);
                            guige = input;
                            Modify();
                        }
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(et.getWindowToken(), 0);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(et.getWindowToken(), 0);
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

    private void ClickLastBuyin() {
        final EditText et = new EditText(this);
        et.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
        if (lastbuyin > 0) {
            et.setText(Double.toString(lastbuyin));
        }
        et.setFocusable(true);
        et.setFocusableInTouchMode(true);
        et.requestFocus();
        AlertDialog.Builder alert = new AlertDialog.Builder(this).setTitle("编辑最近一次进货单价")
                .setView(et)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String input = et.getText().toString();
                        if (input.equals("")) {
                            Toast.makeText(ModelDetailActivity.this, "进货单价不能为空", Toast.LENGTH_SHORT).show();
                        } else {
                            if (StringUtils.isvalidNumber(input)) {
                                tv_price.setText("¥" + input);
                                lastbuyin = Double.valueOf(input);
                                Modify();
                            } else {
                                Toast.makeText(ModelDetailActivity.this, "请输入整数或小数值", Toast.LENGTH_SHORT).show();
                            }
                        }
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(et.getWindowToken(), 0);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(et.getWindowToken(), 0);
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

    private void ClickWuliu() {
        final EditText et = new EditText(this);
        et.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
        if (wuliu > 0) {
            et.setText(Double.toString(wuliu));
        }
        et.setFocusable(true);
        et.setFocusableInTouchMode(true);
        et.requestFocus();
        AlertDialog.Builder alert = new AlertDialog.Builder(this).setTitle("编辑最近一次物流费用")
                .setView(et)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String input = et.getText().toString();
                        if (input.equals("")) {
                            Toast.makeText(ModelDetailActivity.this, "物流费用不能为空", Toast.LENGTH_SHORT).show();
                        } else {
                            if (StringUtils.isvalidNumber(input)) {
                                tv_wuliu.setText("¥" + input);
                                wuliu = Double.valueOf(input);
                                Modify();
                            } else {
                                Toast.makeText(ModelDetailActivity.this, "请输入整数或小数值", Toast.LENGTH_SHORT).show();
                            }
                        }
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(et.getWindowToken(), 0);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(et.getWindowToken(), 0);
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

    private void ClickSafecount() {
        final EditText et = new EditText(this);
        et.setInputType(InputType.TYPE_CLASS_NUMBER);
        if (safecount > 0) {
            et.setText(Integer.toString(safecount));
        }
        et.setFocusable(true);
        et.setFocusableInTouchMode(true);
        et.requestFocus();
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
                                Toast.makeText(ModelDetailActivity.this, "请输入整数值", Toast.LENGTH_SHORT).show();
                            }
                        }
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(et.getWindowToken(), 0);

                        Modify();

                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(et.getWindowToken(), 0);
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

    private void ClickClientid() {
        final EditText et = new EditText(this);
        et.setText(clientid);
        et.setFocusable(true);
        et.setFocusableInTouchMode(true);
        et.requestFocus();
        AlertDialog.Builder alert = new AlertDialog.Builder(this).setTitle("编辑客户编码/图号")
                .setView(et)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String input = et.getText().toString();
                        tv_clientid.setText(input);
                        clientid = input;
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(et.getWindowToken(), 0);
                        Modify();

                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(et.getWindowToken(), 0);
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

    private void ClickSupply() {
        final EditText et = new EditText(this);
        et.setText(suppliername);
        et.setFocusable(true);
        et.setFocusableInTouchMode(true);
        et.requestFocus();
        AlertDialog.Builder alert = new AlertDialog.Builder(this).setTitle("编辑供应商信息")
                .setView(et)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String input = et.getText().toString();
                        tv_suppliername.setText(input);
                        suppliername = input;
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(et.getWindowToken(), 0);
                        Modify();

                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(et.getWindowToken(), 0);
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

    private void ClickCheck() {

        if (checkAlert == null) {
            checkAlert = new AlertDialog.Builder(ModelDetailActivity.this)
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
                            Modify();
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            checkAlert.dismiss();
                        }
                    })
                    .setSingleChoiceItems(checkData, needcheck, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            needcheck = which;
                        }
                    }).create();
        }
        checkAlert.show();
    }

    private int defaultcategoryindex = 0;

    private void ClickCategory() {
        if (typeData == null) {
            Session.CheckRefreshToken(new RefreshTokenListener() {
                @Override
                public void RefreshTokenResult(final int resultcode) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (resultcode == Codes.Code_Success) {
                                SCSDK.getInstance().GetTypeList(Session.getInstance().getShopCode(), Session.getInstance().getToken(), new SCResponseListener() {
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
                                                        if (!typecode.equals("") && typecode.equals(temptype.typecode)) {
                                                            defaultcategoryindex = i;
                                                        }
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
                                                Toast.makeText(ModelDetailActivity.this, errormsg, Toast.LENGTH_SHORT).show();

                                            }
                                        });
                                    }
                                });
                            } else {
                                Toast.makeText(ModelDetailActivity.this, SCExceptionCodeList.getExceptionMsg(resultcode), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            });
        } else {
            ShowTypeAlert();
        }

    }

    private void ShowTypeAlert() {
        if (typeAlert == null) {
            typeAlert = new AlertDialog.Builder(ModelDetailActivity.this)
                    .setTitle("选择型号分类")
                    .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            tv_category.setText(typename);
                            typeAlert.dismiss();
                            Modify();
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            typeAlert.dismiss();
                        }
                    })
                    .setSingleChoiceItems(typeData, defaultcategoryindex, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            typecode = typenameandcode.get(typeData[which]);
                            typename = typeData[which];
                        }
                    }).create();
        }
        typeAlert.show();
    }

    private int defaultstoreindex = -1;

    private void ClickStore() {
        if (storeData == null) {
            Session.CheckRefreshToken(new RefreshTokenListener() {
                @Override
                public void RefreshTokenResult(final int resultcode) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (resultcode == Codes.Code_Success) {
                                SCSDK.getInstance().GetStoreList(Session.getInstance().getShopCode(), Session.getInstance().getToken(), new SCResponseListener() {
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
                                                        if (storecode.equals(tempstore.code)) {
                                                            defaultstoreindex = i;
                                                        }
                                                    }
                                                    if (storecode.equals("")) {
                                                        defaultstoreindex = -1;
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
                                                Toast.makeText(ModelDetailActivity.this, errormsg, Toast.LENGTH_SHORT).show();

                                            }
                                        });
                                    }
                                });
                            } else {
                                Toast.makeText(ModelDetailActivity.this, SCExceptionCodeList.getExceptionMsg(resultcode), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            });
        } else {
            ShowStoreAlert();
        }

    }

    private String newchoosestore = "";
    private String newchoosestorecode = "";

    private void ShowStoreAlert() {
        if (storeAlert == null) {
            storeAlert = new AlertDialog.Builder(ModelDetailActivity.this)
                    .setTitle("选择所属仓库")
                    .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (!newchoosestore.equals("") && !storename.equals(newchoosestore)) {
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
                                Toast.makeText(ModelDetailActivity.this, "修改成功,请继续选择所属货架", Toast.LENGTH_SHORT).show();
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
                    .setSingleChoiceItems(storeData, defaultstoreindex, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            newchoosestorecode = storenameandcode.get(storeData[which]);
                            newchoosestore = storeData[which];
                        }
                    }).create();
        }
        storeAlert.show();
    }

    private int defaultrackindex = -1;

    private void ClickRack() {
        if (storecode != null && !storecode.equals("")) {
            if (rackData == null) {
                Session.CheckRefreshToken(new RefreshTokenListener() {
                    @Override
                    public void RefreshTokenResult(final int resultcode) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (resultcode == Codes.Code_Success) {
                                    SCSDK.getInstance().GetRackList(Session.getInstance().getShopCode(), storecode, Session.getInstance().getToken(), new SCResponseListener() {
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
                                                            if (!rackcode.equals("") && rackcode.equals(temprack.code)) {
                                                                defaultrackindex = i;
                                                            }
                                                        }
                                                        if (rackcode.equals("")) {
                                                            defaultrackindex = -1;
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
                                                    Toast.makeText(ModelDetailActivity.this, errormsg, Toast.LENGTH_SHORT).show();
                                                    rackData = null;
                                                    racknameandcode.clear();
                                                }
                                            });
                                        }
                                    });
                                } else {
                                    rackData = null;
                                    racknameandcode.clear();
                                    Toast.makeText(ModelDetailActivity.this, SCExceptionCodeList.getExceptionMsg(resultcode), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
            } else {
                ShowRackAlert(false);
            }
        } else {
            Toast.makeText(ModelDetailActivity.this, "请先选择所属仓库", Toast.LENGTH_SHORT).show();
        }

    }

    private String newchooserack = "";
    private String newchooserackcode = "";

    private void ShowRackAlert(boolean isfresh) {
        if (rackData != null && rackData.length > 0) {
            if (isfresh) {
                newchooserack = "";
                newchooserackcode = "";
                rackAlert = new AlertDialog.Builder(ModelDetailActivity.this)
                        .setTitle("选择所属货架")
                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (!newchooserack.equals("") && !rackname.equals(newchooserack)) {
                                    rackname = newchooserack;
                                    rackcode = newchooserackcode;
                                    locationData = null;
                                    tv_location.setText("");
                                    locationcode = "";
                                    locationname = "";
                                    tv_rack.setText(rackname);
                                    Toast.makeText(ModelDetailActivity.this, "修改成功,请继续选择所在位置", Toast.LENGTH_SHORT).show();
                                    ClickLocation();
                                } else if (newchooserack.equals("")) {
                                    rackname = newchooserack;
                                    rackcode = newchooserackcode;
                                    tv_rack.setText(rackname);
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
                        .setSingleChoiceItems(rackData, defaultrackindex, new DialogInterface.OnClickListener() {
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

    private int defaultlocationindex = -1;

    private void ClickLocation() {
        if (rackcode != null && !rackcode.equals("")) {
            if (locationData == null) {
                Session.CheckRefreshToken(new RefreshTokenListener() {
                    @Override
                    public void RefreshTokenResult(final int resultcode) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (resultcode == Codes.Code_Success) {
                                    SCSDK.getInstance().GetPositionList(Session.getInstance().getShopCode(), storecode, rackcode, Session.getInstance().getToken(), new SCResponseListener() {
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
                                                            if (!locationcode.equals("") && locationcode.equals(tempposition.code)) {
                                                                defaultlocationindex = i;
                                                            }
                                                        }
                                                        if (locationcode.equals("")) {
                                                            defaultlocationindex = -1;
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
                                                    Toast.makeText(ModelDetailActivity.this, errormsg, Toast.LENGTH_SHORT).show();
                                                    locationData = null;
                                                    locationnameandcode.clear();
                                                }
                                            });
                                        }
                                    });
                                } else {
                                    locationData = null;
                                    locationnameandcode.clear();
                                    Toast.makeText(ModelDetailActivity.this, SCExceptionCodeList.getExceptionMsg(resultcode), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
            } else {
                ShowLocationAlert(false);
            }
        } else {
            Toast.makeText(ModelDetailActivity.this, "请先选择所属货架", Toast.LENGTH_SHORT).show();
        }

    }

    private String tempcode = "";
    private String tempname = "";

    private void ShowLocationAlert(boolean isfresh) {
        if (locationData != null && locationData.length > 0) {
            tempcode = "";
            tempname = "";
            if (isfresh) {
                locationAlert = new AlertDialog.Builder(ModelDetailActivity.this)
                        .setTitle("选择所在位置")
                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (!tempname.equals("")) {
                                    locationname = tempname;
                                    locationcode = tempcode;
                                    tv_location.setText(locationname);
                                    locationAlert.dismiss();
                                    Modify();
                                } else {
                                    Toast.makeText(ModelDetailActivity.this, "请选择所属位置", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                locationAlert.dismiss();
                            }
                        })
                        .setSingleChoiceItems(locationData, defaultlocationindex, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                tempcode = locationnameandcode.get(locationData[which]);
                                tempname = locationData[which];
                            }
                        }).create();
            }
            locationAlert.show();
        }
    }

    private void Modify() {
        Session.CheckRefreshToken(new RefreshTokenListener() {
            @Override
            public void RefreshTokenResult(final int resultcode) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (resultcode == Codes.Code_Success) {
                            if (!CheckModifyData()) {
                                return;
                            }
                            SCSDK.getInstance().ModifyModel(Session.getInstance().getShopCode(), productid, Session.getInstance().getToken(), productname, storecode, rackcode, locationcode, typecode, danwei, guige, needcheck,
                                    compressPic, lastbuyin, wuliu, clientid, suppliername, safecount, remark, new SCResponseListener() {
                                        @Override
                                        public void onResult(Object result) {
                                            final SCResult.ModelDetailResult modify = (SCResult.ModelDetailResult) result;
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    productid = modify.modelcode;
                                                    originalPic = modify.modelpicurl;
                                                    Toast.makeText(ModelDetailActivity.this, "编辑成功", Toast.LENGTH_SHORT).show();

                                                }
                                            });
                                        }

                                        @Override
                                        public void onError(int code, final String errormsg) {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Toast.makeText(ModelDetailActivity.this, errormsg, Toast.LENGTH_SHORT).show();

                                                }
                                            });
                                        }
                                    });
                        } else {
                            Toast.makeText(ModelDetailActivity.this, SCExceptionCodeList.getExceptionMsg(resultcode), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    private boolean CheckModifyData() {
        if (rackcode.equals("")) {
            Toast.makeText(ModelDetailActivity.this, "编辑失败!所属货架不能为空", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (locationcode.equals("")) {
            Toast.makeText(ModelDetailActivity.this, "编辑失败!所在位置不能为空", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
