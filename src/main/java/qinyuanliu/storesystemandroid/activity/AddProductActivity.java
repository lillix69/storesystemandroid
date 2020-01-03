package qinyuanliu.storesystemandroid.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
 * Created by lillix on 5/28/18.
 */
public class AddProductActivity extends BaseActivity implements HeadImageUtil.CropHandler{
    private Button btn_back;
    private ImageView img_pic;
    private TextView tv_name;
    private TextView tv_storename;
    private TextView tv_rack;
    private TextView tv_location;
    private TextView tv_category;
    private TextView tv_danwei;
    private TextView tv_guige;
    private TextView tv_price;
    private TextView tv_wuliu;
    private TextView tv_clientid;
    private TextView tv_safecount;
    private TextView tv_check;
    private TextView tv_suppliername;
    private TextView tv_add;

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
    private int needcheck =0;
    private String suppliername = "";
   // private Bitmap newpic = null;
    private String compressPic = null;
    private Bitmap bitmapPhoto;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        initView();

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

    }


    private void ClickImage() {
        if(compressPic != null){
            WindowManager wm = this.getWindowManager();
            int width = wm.getDefaultDisplay().getWidth()/2;
            final ImageView pic = new ImageView(AddProductActivity.this);
            pic.setAdjustViewBounds(true);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    width, LinearLayout.LayoutParams.WRAP_CONTENT);
            pic.setLayoutParams(layoutParams);
            pic.setMaxWidth(width);
            pic.setMaxHeight(width);
          pic.setImageBitmap(bitmapPhoto);

            new AlertDialog.Builder(this).setTitle("型号图片")
                    .setView(pic)
                    .setPositiveButton("修改", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            PickImage();
                        }
                    })
                    .setNegativeButton("取消", null)
                    .show();
        }
       else{
            PickImage();
        }
    }

    private void PickImage(){
        Intent galleryIntent = HeadImageUtil
                .getCropHelperInstance()
                .buildGalleryIntent();
        startActivityForResult(galleryIntent,
                HeadImageUtil.REQUEST_GALLERY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        HeadImageUtil.getCropHelperInstance().sethandleResultListerner(AddProductActivity.this, requestCode, resultCode,
                data);
    }

    @Override
    public void onPhotoCropped(Bitmap photo, int requestCode) {
        switch (requestCode) {
            case HeadImageUtil.RE_GALLERY:
               // newpic = HeadImageUtil.makeRoundCorner(photo);
               // img_pic.setImageBitmap(newpic);
                bitmapPhoto = photo;
                img_pic.setImageBitmap(photo);
                Bitmap compresspic = HeadImageUtil.compressImage(photo);
                compressPic = HeadImageUtil.BitmapToString(compresspic);

                break;

        }

    }

    @Override
    public void onCropCancel() {

    }

    @Override
    public void onCropFailed(String message) {

    }

    @Override
    public Activity getContext() {
        return AddProductActivity.this;
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
                            Toast.makeText(AddProductActivity.this, "名称不能为空", Toast.LENGTH_SHORT).show();
                        } else {
                            tv_name.setText(input);
                            productname = input;
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
                            Toast.makeText(AddProductActivity.this, "单位不能为空", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(AddProductActivity.this, "规格不能为空", Toast.LENGTH_SHORT).show();
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

    private void ClickLastBuyin() {
        final EditText et = new EditText(this);
        et.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
        et.setFocusable(true);
        et.setFocusableInTouchMode(true);
        et.requestFocus();
        if(lastbuyin>0) {
            et.setText(Double.toString(lastbuyin));
        }
        AlertDialog.Builder alert =  new AlertDialog.Builder(this).setTitle("编辑最近一次进货单价")
                .setView(et)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String input = et.getText().toString();
                        if (input.equals("")) {
                            Toast.makeText(AddProductActivity.this, "进货单价不能为空", Toast.LENGTH_SHORT).show();
                        } else {
                            if (StringUtils.isvalidNumber(input)) {
                                tv_price.setText("¥" + input);
                                lastbuyin = Double.valueOf(input);
                            } else {
                                Toast.makeText(AddProductActivity.this, "请输入整数或小数值", Toast.LENGTH_SHORT).show();
                            }
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(et.getWindowToken(), 0) ;
                        }
                    }
                })
                .setNegativeButton("取消",new DialogInterface.OnClickListener() {
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

    private void ClickWuliu() {
        final EditText et = new EditText(this);
        et.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
        et.setFocusable(true);
        et.setFocusableInTouchMode(true);
        et.requestFocus();
        if(wuliu>0) {
            et.setText(Double.toString(wuliu));
        }
        AlertDialog.Builder alert = new AlertDialog.Builder(this).setTitle("编辑物流费用")
                .setView(et)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String input = et.getText().toString();
                        if (input.equals("")) {
                            Toast.makeText(AddProductActivity.this, "物流费用不能为空", Toast.LENGTH_SHORT).show();
                        } else {
                            if (StringUtils.isvalidNumber(input)) {
                                tv_wuliu.setText("¥" + input);
                                wuliu = Double.valueOf(input);
                            } else {
                                Toast.makeText(AddProductActivity.this, "请输入整数或小数值", Toast.LENGTH_SHORT).show();
                            }
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
                                Toast.makeText(AddProductActivity.this, "请输入整数值", Toast.LENGTH_SHORT).show();
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

    private void ClickSupply() {
        final EditText et = new EditText(this);
        et.setText(suppliername);
        et.setFocusable(true);
        et.setFocusableInTouchMode(true);
        et.requestFocus();
        AlertDialog.Builder alert =  new AlertDialog.Builder(this).setTitle("编辑供应商信息")
                .setView(et)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String input = et.getText().toString();
                        tv_suppliername.setText(input);
                        suppliername = input;
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
            checkAlert = new AlertDialog.Builder(AddProductActivity.this)
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
                                                Toast.makeText(AddProductActivity.this, errormsg, Toast.LENGTH_SHORT).show();

                                            }
                                        });
                                    }
                                });
                            } else {
                                Toast.makeText(AddProductActivity.this, SCExceptionCodeList.getExceptionMsg(resultcode), Toast.LENGTH_SHORT).show();
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
            typeAlert = new AlertDialog.Builder(AddProductActivity.this)
                    .setTitle("选择型号分类")
                    .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
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
                            typecode = typenameandcode.get(typeData[which]);
                            typename = typeData[which];
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
                                                Toast.makeText(AddProductActivity.this, errormsg, Toast.LENGTH_SHORT).show();

                                            }
                                        });
                                    }
                                });
                            } else {
                                Toast.makeText(AddProductActivity.this, SCExceptionCodeList.getExceptionMsg(resultcode), Toast.LENGTH_SHORT).show();
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
            storeAlert = new AlertDialog.Builder(AddProductActivity.this)
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
                                Toast.makeText(AddProductActivity.this, "修改成功,请继续选择所属货架", Toast.LENGTH_SHORT).show();
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
                                                    Toast.makeText(AddProductActivity.this, errormsg, Toast.LENGTH_SHORT).show();
                                                    rackData = null;
                                                    racknameandcode.clear();
                                                }
                                            });
                                        }
                                    });
                                } else {
                                    rackData = null;
                                    racknameandcode.clear();
                                    Toast.makeText(AddProductActivity.this, SCExceptionCodeList.getExceptionMsg(resultcode), Toast.LENGTH_SHORT).show();
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
            Toast.makeText(AddProductActivity.this, "请先选择所属仓库", Toast.LENGTH_SHORT).show();
        }

    }

    private String newchooserack="";
    private String newchooserackcode="";
    private void ShowRackAlert(boolean isfresh) {
        if (rackData != null && rackData.length > 0) {
            if(isfresh) {
                rackAlert = new AlertDialog.Builder(AddProductActivity.this)
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
                                    Toast.makeText(AddProductActivity.this, "修改成功,请继续选择所在位置", Toast.LENGTH_SHORT).show();
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
                                                    Toast.makeText(AddProductActivity.this, errormsg, Toast.LENGTH_SHORT).show();
                                                    locationData = null;
                                                    locationnameandcode.clear();
                                                }
                                            });
                                        }
                                    });
                                } else {
                                    locationData = null;
                                    locationnameandcode.clear();
                                    Toast.makeText(AddProductActivity.this, SCExceptionCodeList.getExceptionMsg(resultcode), Toast.LENGTH_SHORT).show();
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
            Toast.makeText(AddProductActivity.this, "请先选择所属货架", Toast.LENGTH_SHORT).show();
        }

    }

    private void ShowLocationAlert(boolean isfresh) {
        if (locationData != null && locationData.length > 0) {
            if(isfresh) {
                locationAlert = new AlertDialog.Builder(AddProductActivity.this)
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
//                            SCSDK.getInstance().AddModel(Session.getInstance().getShopCode(),productname,Session.getInstance().getToken(), storecode, rackcode, locationcode, typecode, danwei, guige, needcheck,
//                                    compressPic, lastbuyin, wuliu, clientid, suppliername, safecount, new SCResponseListener() {
//                                        @Override
//                                        public void onResult(Object result) {
//                                            runOnUiThread(new Runnable() {
//                                                @Override
//                                                public void run() {
//                                                    Toast.makeText(AddProductActivity.this, "提交成功", Toast.LENGTH_SHORT).show();
//                          finish();
//                                                }
//                                            });
//                                        }
//
//                                        @Override
//                                        public void onError(int code, final String errormsg) {
//                                            runOnUiThread(new Runnable() {
//                                                @Override
//                                                public void run() {
//                                                    Toast.makeText(AddProductActivity.this, errormsg, Toast.LENGTH_SHORT).show();
//
//                                                }
//                                            });
//                                        }
//                                    });
                        } else {
                            Toast.makeText(AddProductActivity.this, SCExceptionCodeList.getExceptionMsg(resultcode), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    private boolean CheckAddData() {
        if (productname.equals("")) {
            Toast.makeText(AddProductActivity.this, "提交失败!型号名称不能为空", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (storecode.equals("")) {
            Toast.makeText(AddProductActivity.this, "提交失败!所属仓库不能为空", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (rackcode.equals("")) {
            Toast.makeText(AddProductActivity.this, "提交失败!所属货架不能为空", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (locationcode.equals("")) {
            Toast.makeText(AddProductActivity.this, "提交失败!所在位置不能为空", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (danwei.equals("")) {
            Toast.makeText(AddProductActivity.this, "提交失败!单位不能为空", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (guige.equals("")) {
            Toast.makeText(AddProductActivity.this, "提交失败!规格不能为空", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (needcheck <0) {
            Toast.makeText(AddProductActivity.this, "提交失败!请选择是否需要校验", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}




