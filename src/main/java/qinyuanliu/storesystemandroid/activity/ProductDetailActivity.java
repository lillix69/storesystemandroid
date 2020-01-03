package qinyuanliu.storesystemandroid.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.HashMap;

import qinyuanliu.storesystemandroid.R;
import qinyuanliu.storesystemandroid.common.Session;
import qinyuanliu.storesystemandroid.http.Codes;
import qinyuanliu.storesystemandroid.http.SCExceptionCodeList;
import qinyuanliu.storesystemandroid.http.SCResponseListener;
import qinyuanliu.storesystemandroid.http.SCResult;
import qinyuanliu.storesystemandroid.http.SCSDK;
import qinyuanliu.storesystemandroid.listener.RefreshTokenListener;

/**
 * Created by lillix on 6/25/18.
 */
public class ProductDetailActivity extends BaseActivity{
    private Button btn_back;
    private ImageView img_pic;
    private TextView tv_id;
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
    private Button btn_out;
    private Button btn_in;
    private TextView tv_check;
    private TextView tv_suppliername;
    //  private TextView tv_storecount;
    private TextView tv_remark;
    private TextView tv_pstatus;
    private Button btn_detail;
    private LinearLayout showdetailview;
    private Button btn_confirm;
    private Button btn_scan;
private RelativeLayout snhistoryview;

    private String productid;
    private String modelid;
    private String typeid;
    private String modelname;
    private String remark;
    private String guige;
    private String storename;
    private String rackname;
    private String locationname;
    private String picurl;
    private String storecode;
    private String rackcode;
    private String locationcode;
    private AlertDialog storeAlert;
    private String[] storeData;
    private HashMap<String, String> storenameandcode = new HashMap<>();
    private AlertDialog rackAlert;
    private String[] rackData;
    private HashMap<String, String> racknameandcode = new HashMap<>();
    private AlertDialog locationAlert;
    private String[] locationData;
    private HashMap<String, String> locationnameandcode = new HashMap<>();

    private String typecode = "";
    private String danwei = "";
    private String clientid = "";
    private double lastbuyin = 0.0;
    private double wuliu = 0.0;
    private int safecount = 0;
    private int needcheck = 0;
    private String suppliername = "";
    private String compressPic = null;
    private boolean isShown = false;
    private boolean isLaststep = false;
    private int powerlevel = 0;
    private int currentstatus = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_productdetail);

        productid = getIntent().getStringExtra("productid");
        initView();
        initData();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 3001 || requestCode == 3002) {
            if (resultCode == RESULT_OK) {
                finish();
            }
        } else {
            Log.i("======", Integer.toString(requestCode));
        }
    }

    private void initView() {
        snhistoryview = (RelativeLayout)findViewById(R.id.snhistoryview);
        snhistoryview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowSnHistory();
            }
        });
        btn_scan = (Button) findViewById(R.id.btn_scan);
        btn_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(ProductDetailActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(ProductDetailActivity.this, "请先开启手机摄像头使用权限", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Settings.ACTION_SETTINGS);
                    startActivity(intent);
                } else {
                    Intent scanintent = new Intent(ProductDetailActivity.this, MipcaActivityCapture.class);
                    scanintent.putExtra("isSingle", true);
                    startActivity(scanintent);
                    finish();
                }

            }
        });
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
        btn_back = (Button) findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        img_pic = (ImageView) findViewById(R.id.img_pic);
        img_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowPic();
            }
        });
        tv_check = (TextView) findViewById(R.id.tv_check);
        tv_id = (TextView) findViewById(R.id.tv_id);
        tv_id.setText(productid);
        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_pstatus = (TextView) findViewById(R.id.tv_pstatus);
        tv_remark = (TextView) findViewById(R.id.tv_remark);
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
        tv_danwei = (TextView) findViewById(R.id.tv_danwei);
        tv_guige = (TextView) findViewById(R.id.tv_guige);
        tv_price = (TextView) findViewById(R.id.tv_price);
        tv_wuliu = (TextView) findViewById(R.id.tv_wuliu);
        tv_clientid = (TextView) findViewById(R.id.tv_clientid);
        tv_safecount = (TextView) findViewById(R.id.tv_safecount);
        tv_suppliername = (TextView) findViewById(R.id.tv_suppliername);

        btn_out = (Button) findViewById(R.id.btn_out);
        btn_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (powerlevel == 1 || powerlevel == 4 || powerlevel == 5) {
                    Toast.makeText(ProductDetailActivity.this, "该账户没有出库权限！", Toast.LENGTH_SHORT).show();
                } else {
                    if(currentstatus == 4){
                        Toast.makeText(ProductDetailActivity.this, "当前序列号已出库！", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(currentstatus == 5){
                        Toast.makeText(ProductDetailActivity.this, "当前序列号已报废！", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (isValid()) {
                        Session.CheckRefreshToken(new RefreshTokenListener() {
                            @Override
                            public void RefreshTokenResult(final int resultcode) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (resultcode == Codes.Code_Success) {
                        //看是否要project
                        SCSDK.getInstance().IsNeedProject(Session.getInstance().getShopCode(), Session.getInstance().getToken(), modelid, new SCResponseListener() {
                            @Override
                            public void onResult(Object result) {
                                final SCResult.IsneedProjectResult res = (SCResult.IsneedProjectResult) result;
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        //打开产品出库页面
                                        Intent intent = new Intent(ProductDetailActivity.this, ProductOutActivity.class);
                                        intent.putExtra("isneed", res.isneed);
                                        intent.putExtra("projectcode", res.projectcode == null ? "" : res.projectcode);
                                        intent.putExtra("projectname", res.projectname == null ? "" : res.projectname);

                                        intent.putExtra("productid", productid);
                                        intent.putExtra("modelid", modelid);
                                        intent.putExtra("typeid", typeid);
                                        intent.putExtra("modelname", modelname);
                                        intent.putExtra("storename", storename);
                                        intent.putExtra("rackname", rackname);
                                        intent.putExtra("locationname", locationname);
                                        intent.putExtra("picurl", picurl);
                                        intent.putExtra("guige", guige);
                                        startActivityForResult(intent, 3002);
                                    }
                                });
                            }

                            @Override
                            public void onError(int code, final String errormsg) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(ProductDetailActivity.this, errormsg, Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });
                                        } else {
                                            Toast.makeText(ProductDetailActivity.this, SCExceptionCodeList.getExceptionMsg(resultcode), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        });
                    } else {
                        Toast.makeText(ProductDetailActivity.this, "型号的仓库/货架/位置不能为空,请选择完成后再出库", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });

        btn_in = (Button) findViewById(R.id.btn_in);
        btn_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (powerlevel == 2 || powerlevel == 4 || powerlevel == 6) {
                    Toast.makeText(ProductDetailActivity.this, "该账户没有入库权限！", Toast.LENGTH_SHORT).show();
                } else {
                    if(currentstatus == 2){
                        Toast.makeText(ProductDetailActivity.this, "当前序列号已入库！", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(currentstatus == 5){
                        Toast.makeText(ProductDetailActivity.this, "当前序列号已报废！", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (isValid()) {
                        Intent intent = new Intent(ProductDetailActivity.this, ProductInActivity.class);
                        intent.putExtra("productid", productid);
                        intent.putExtra("modelid", modelid);
                        intent.putExtra("typeid", typeid);
                        intent.putExtra("modelname", modelname);
                        intent.putExtra("storename", storename);
                        intent.putExtra("rackname", rackname);
                        intent.putExtra("locationname", locationname);
                        intent.putExtra("picurl", picurl);
                        intent.putExtra("guige", guige);
                        startActivityForResult(intent, 3001);
                    } else {
                        Toast.makeText(ProductDetailActivity.this, "型号的仓库/货架/位置不能为空,请选择完成后再入库", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });

        btn_confirm = (Button) findViewById(R.id.btn_confirm);
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Session.CheckRefreshToken(new RefreshTokenListener() {
                    @Override
                    public void RefreshTokenResult(final int resultcode) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (resultcode == Codes.Code_Success) {
                                    SCSDK.getInstance().ConfirmProduct(productid, Session.getInstance().getShopCode(), Session.getInstance().getToken(), new SCResponseListener() {
                                        @Override
                                        public void onResult(final Object result) {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Toast.makeText(ProductDetailActivity.this, "操作成功", Toast.LENGTH_SHORT).show();
                                                    //islaststep来进行区分
                                                    if (isLaststep) {
                                                        btn_confirm.setVisibility(View.GONE);
                                                        btn_in.setVisibility(View.VISIBLE);
                                                        btn_out.setVisibility(View.VISIBLE);
                                                        tv_pstatus.setText("待入库");
                                                        tv_pstatus.setTextColor(getResources().getColor(R.color.status_blue));
                                                    } else {
                                                        finish();
                                                    }
                                                }
                                            });
                                        }

                                        @Override
                                        public void onError(int code, final String errormsg) {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Toast.makeText(ProductDetailActivity.this, errormsg, Toast.LENGTH_SHORT).show();

                                                }
                                            });
                                        }
                                    });
                                } else {
                                    Toast.makeText(ProductDetailActivity.this, SCExceptionCodeList.getExceptionMsg(resultcode), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
            }
        });
    }

    private boolean isValid() {
        if (storecode.equals("") || rackcode.equals("") || locationcode.equals("")) {
            return false;
        }
        return true;
    }


    private void ShowPic() {
        if (picurl != null && !picurl.equals("")) {
            WindowManager wm = this.getWindowManager();
            int width = wm.getDefaultDisplay().getWidth() / 2;
            final ImageView pic = new ImageView(ProductDetailActivity.this);
            pic.setAdjustViewBounds(true);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    width, LinearLayout.LayoutParams.WRAP_CONTENT);
            pic.setLayoutParams(layoutParams);
            pic.setMaxWidth(width);
            pic.setMaxHeight(width);
            Glide.with(ProductDetailActivity.this).load(picurl)
                    .placeholder(R.drawable.icon_defaultimg)
                    .error(R.drawable.icon_defaultimg)
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .into(pic);

            new AlertDialog.Builder(this).setTitle("产品图片")
                    .setView(pic)
                    .setNegativeButton("关闭", null)
                    .show();
        }

    }

    private void initData() {
        Session.CheckRefreshToken(new RefreshTokenListener() {
            @Override
            public void RefreshTokenResult(final int resultcode) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (resultcode == Codes.Code_Success) {
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

                                            }
                                        });
                                    }
                                }

                                @Override
                                public void onError(int code, final String errormsg) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(ProductDetailActivity.this, errormsg, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            });
                            SCSDK.getInstance().GetProductdetailByScan(Session.getInstance().getShopCode(), productid, Session.getInstance().getToken(), new SCResponseListener() {
                                @Override
                                public void onResult(Object result) {
                                    final SCResult.ProductDetailResult res = (SCResult.ProductDetailResult) result;
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Glide.with(ProductDetailActivity.this).load(res.modelpicurl)
                                                    .placeholder(R.drawable.icon_defaultimg)
                                                    .error(R.drawable.icon_defaultimg)
                                                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                                                    .into(img_pic);
                                            modelid = res.modelcode;
                                            typeid = res.typecode == null ? "" : res.typecode;
                                            modelname = res.modelname == null ? "" : res.modelname;
                                            remark = res.remark == null ? "" : res.remark;
                                            storename = res.storename == null ? "" : res.storename;
                                            rackname = res.rackname == null ? "" : res.rackname;
                                            locationname = res.positionname == null ? "" : res.positionname;
                                            guige = res.spec == null ? "" : res.spec;
                                            picurl = res.modelpicurl;
                                            storecode = res.storecode == null ? "" : res.storecode;
                                            rackcode = res.rackcode == null ? "" : res.rackcode;
                                            locationcode = res.positioncode == null ? "" : res.positioncode;
                                            typecode = res.typecode == null ? "" : res.typecode;
                                            danwei = res.unit == null ? "" : res.unit;
                                            guige = res.spec == null ? "" : res.spec;
                                            lastbuyin = res.lastedbuyin;
                                            wuliu = res.lastedtrace;
                                            clientid = res.customercode == null ? "" : res.customercode;
                                            safecount = res.min;
                                            needcheck = res.needcheck;
                                            suppliername = res.suppliername == null ? "" : res.suppliername;
                                            if (res.islaststep == 1) {
                                                isLaststep = true;
                                            }
                                            //是否需要确认流程 0：不需要1：需要
                                            if (res.isneedconfirmflow == 0) {
                                                btn_confirm.setVisibility(View.GONE);
                                                btn_in.setVisibility(View.VISIBLE);
                                                btn_out.setVisibility(View.VISIBLE);
                                            } else {
                                                btn_in.setVisibility(View.GONE);
                                                btn_out.setVisibility(View.GONE);
                                                btn_confirm.setVisibility(View.VISIBLE);
                                                btn_confirm.setText(res.confrimbuttontext);
                                            }
                                            if (res.currentstatusname != null && !res.currentstatusname.equals("")) {
                                                tv_pstatus.setText(res.currentstatusname);
                                                currentstatus = res.currentstatus;
                                                //产品状态1：待入库(蓝色) 2：已入库(绿色) 3：待检验（黄色）4：已出库（红色） 5：已报废（深灰色）
                                                if (res.currentstatus == 1) {
                                                    tv_pstatus.setTextColor(getResources().getColor(R.color.status_blue));
                                                } else if (res.currentstatus == 2) {
                                                    tv_pstatus.setTextColor(getResources().getColor(R.color.status_green));
                                                } else if (res.currentstatus == 3) {
                                                    tv_pstatus.setTextColor(getResources().getColor(R.color.status_yellow));
                                                } else if (res.currentstatus == 4) {
                                                    tv_pstatus.setTextColor(getResources().getColor(R.color.status_red));
                                                } else if (res.currentstatus == 5) {
                                                    tv_pstatus.setTextColor(getResources().getColor(R.color.status_grey));
                                                }
                                            } else {
                                                tv_pstatus.setText("");
                                            }
                                            tv_name.setText(res.modelname);
                                            tv_remark.setText(res.remark);
                                            tv_storename.setText(res.storename);
                                            tv_rack.setText(res.rackname);
                                            tv_location.setText(res.positionname);
                                            tv_category.setText(res.typename);
                                            tv_danwei.setText(res.unit);
                                            tv_guige.setText(res.spec);
                                            tv_price.setText("¥" + Double.toString(res.lastedbuyin));
                                            tv_wuliu.setText("¥" + Double.toString(res.lastedtrace));
                                            tv_clientid.setText(res.customercode);
                                            //  tv_storecount.setText(Integer.toString(res.store));
                                            tv_safecount.setText(Integer.toString(res.min));
                                            if (res.needcheck == 0) {
                                                tv_check.setText("不需要");
                                            } else {
                                                tv_check.setText("需要");
                                            }
                                            tv_suppliername.setText(res.suppliername);

                                        }
                                    });
                                }

                                @Override
                                public void onError(int code, final String errormsg) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(ProductDetailActivity.this, errormsg, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            });
                        } else {
                            Toast.makeText(ProductDetailActivity.this, SCExceptionCodeList.getExceptionMsg(resultcode), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    //    private void ClickPrint() {
//        Session.CheckRefreshToken(new RefreshTokenListener() {
//            @Override
//            public void RefreshTokenResult(final int resultcode) {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        if (resultcode == Codes.Code_Success) {
//                            SCSDK.getInstance().PrintBarcode(productid, 1, Session.getInstance().getToken(), new SCResponseListener() {
//                                @Override
//                                public void onResult(Object result) {
//                                    runOnUiThread(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            Toast.makeText(ProductDetailActivity.this, "打印成功", Toast.LENGTH_SHORT).show();
//                                        }
//                                    });
//                                }
//
//                                @Override
//                                public void onError(int code, final String errormsg) {
//                                    runOnUiThread(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            Toast.makeText(ProductDetailActivity.this, errormsg, Toast.LENGTH_SHORT).show();
//                                        }
//                                    });
//                                }
//                            });
//                        } else {
//                            Toast.makeText(ProductDetailActivity.this, SCExceptionCodeList.getExceptionMsg(resultcode), Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//            }
//        });
//    }
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
                                                Toast.makeText(ProductDetailActivity.this, errormsg, Toast.LENGTH_SHORT).show();

                                            }
                                        });
                                    }
                                });
                            } else {
                                Toast.makeText(ProductDetailActivity.this, SCExceptionCodeList.getExceptionMsg(resultcode), Toast.LENGTH_SHORT).show();
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
            storeAlert = new AlertDialog.Builder(ProductDetailActivity.this)
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
                                Toast.makeText(ProductDetailActivity.this, "修改成功,请继续选择所属货架", Toast.LENGTH_SHORT).show();
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
                                                    Toast.makeText(ProductDetailActivity.this, errormsg, Toast.LENGTH_SHORT).show();
                                                    rackData = null;
                                                    racknameandcode.clear();
                                                }
                                            });
                                        }
                                    });
                                } else {
                                    rackData = null;
                                    racknameandcode.clear();
                                    Toast.makeText(ProductDetailActivity.this, SCExceptionCodeList.getExceptionMsg(resultcode), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
            } else {
                ShowRackAlert(false);
            }
        } else {
            Toast.makeText(ProductDetailActivity.this, "请先选择所属仓库", Toast.LENGTH_SHORT).show();
        }

    }

    private String newchooserack = "";
    private String newchooserackcode = "";

    private void ShowRackAlert(boolean isfresh) {
        if (rackData != null && rackData.length > 0) {
            if (isfresh) {
                newchooserack = "";
                newchooserackcode = "";
                rackAlert = new AlertDialog.Builder(ProductDetailActivity.this)
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
                                    Toast.makeText(ProductDetailActivity.this, "修改成功,请继续选择所在位置", Toast.LENGTH_SHORT).show();
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
                                                    Toast.makeText(ProductDetailActivity.this, errormsg, Toast.LENGTH_SHORT).show();
                                                    locationData = null;
                                                    locationnameandcode.clear();
                                                }
                                            });
                                        }
                                    });
                                } else {
                                    locationData = null;
                                    locationnameandcode.clear();
                                    Toast.makeText(ProductDetailActivity.this, SCExceptionCodeList.getExceptionMsg(resultcode), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
            } else {
                ShowLocationAlert(false);
            }
        } else {
            Toast.makeText(ProductDetailActivity.this, "请先选择所属货架", Toast.LENGTH_SHORT).show();
        }

    }

    private String tempcode = "";
    private String tempname = "";

    private void ShowLocationAlert(boolean isfresh) {
        if (locationData != null && locationData.length > 0) {
            tempcode = "";
            tempname = "";
            if (isfresh) {
                locationAlert = new AlertDialog.Builder(ProductDetailActivity.this)
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
                                    Toast.makeText(ProductDetailActivity.this, "请选择所属位置", Toast.LENGTH_SHORT).show();
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
                            SCSDK.getInstance().ModifyModel(Session.getInstance().getShopCode(), modelid, Session.getInstance().getToken(), modelname, storecode, rackcode, locationcode, typecode, danwei, guige, needcheck,
                                    compressPic, lastbuyin, wuliu, clientid, suppliername, safecount, remark, new SCResponseListener() {
                                        @Override
                                        public void onResult(Object result) {
                                            final SCResult.ModelDetailResult modify = (SCResult.ModelDetailResult) result;
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    modelid = modify.modelcode;
                                                    picurl = modify.modelpicurl;
                                                    Toast.makeText(ProductDetailActivity.this, "编辑成功", Toast.LENGTH_SHORT).show();

                                                }
                                            });
                                        }

                                        @Override
                                        public void onError(int code, final String errormsg) {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Toast.makeText(ProductDetailActivity.this, errormsg, Toast.LENGTH_SHORT).show();

                                                }
                                            });
                                        }
                                    });
                        } else {
                            Toast.makeText(ProductDetailActivity.this, SCExceptionCodeList.getExceptionMsg(resultcode), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    private void ShowSnHistory(){
        Session.CheckRefreshToken(new RefreshTokenListener() {
            @Override
            public void RefreshTokenResult(final int resultcode) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (resultcode == Codes.Code_Success) {
                            SCSDK.getInstance().GetSNHistory(productid, Session.getInstance().getShopCode(),  Session.getInstance().getToken(), new SCResponseListener() {
                                        @Override
                                        public void onResult(Object result) {
                                            final SCResult.SnHistoryResult snhistory = (SCResult.SnHistoryResult) result;
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {

                                                    AlertDialog.Builder builder = new AlertDialog.Builder(ProductDetailActivity.this);
                                                    builder.setTitle("进出库明细");
                                                    builder.setMessage(snhistory.historyinfo);
                                                    builder.setNegativeButton("关闭", null);
                                                    builder.create().show();

                                                }
                                            });
                                        }

                                        @Override
                                        public void onError(int code, final String errormsg) {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Toast.makeText(ProductDetailActivity.this, errormsg, Toast.LENGTH_SHORT).show();

                                                }
                                            });
                                        }
                                    });
                        } else {
                            Toast.makeText(ProductDetailActivity.this, SCExceptionCodeList.getExceptionMsg(resultcode), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }



}
