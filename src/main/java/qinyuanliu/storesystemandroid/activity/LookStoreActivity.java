package qinyuanliu.storesystemandroid.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.longsh.optionframelibrary.OptionBottomDialog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import qinyuanliu.storesystemandroid.R;
import qinyuanliu.storesystemandroid.adapter.ScanModelAdapter;
import qinyuanliu.storesystemandroid.common.Session;
import qinyuanliu.storesystemandroid.http.Codes;
import qinyuanliu.storesystemandroid.http.SCExceptionCodeList;
import qinyuanliu.storesystemandroid.http.SCResponseListener;
import qinyuanliu.storesystemandroid.http.SCResult;
import qinyuanliu.storesystemandroid.http.SCSDK;
import qinyuanliu.storesystemandroid.listener.ClickDeleteListener;
import qinyuanliu.storesystemandroid.listener.RefreshTokenListener;
import qinyuanliu.storesystemandroid.model.FahuoModel;
import qinyuanliu.storesystemandroid.util.DensityUtil;

/**
 * Created by lillix on 5/28/18.
 */
public class LookStoreActivity extends BaseActivity {
    private Button btn_back;
    private ImageView img_pic;
    private TextView tv_id;
    private TextView tv_name;
    private TextView tv_typename;
    private TextView tv_storelocation;
    private TextView tv_guige;
    private TextView tv_systemcount;
    private EditText edt_acturalcount;
    private TextView tv_look;
    private RelativeLayout min_instoreview;
    private TextView tv_incount;
    private RelativeLayout instoreview;
    private GridView inid_gridview;
    private Button btn_print;
    private RelativeLayout min_outstoreview;
    private TextView tv_outcount;
    private RelativeLayout unmin_outstoreview;
    private ImageView img_scan;
    private GridView outid_gridview;

    private boolean parentflag;
    private String productid = "";
    private int acturalcount = -1;
    private int systemcount = 0;
    private boolean isMin = false;
    //打印条码id
    private ArrayList<String> inmodellist = new ArrayList<>();
    private ScanModelAdapter printmodelAdapter;
    private int printcount = 0;
    //扫描model的条形码
    private ArrayList<String> modellist = new ArrayList<>();
    final int SCANMODEL2 = 1002;
    private int scancount = 0;
    private ScanModelAdapter scanmodelAdapter;

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            ClickBack();
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) { //resultCode为回传的标记，我在B中回传的是RESULT_OK
            case SCANMODEL2:
                if (resultCode == RESULT_OK) {
                    Bundle b = data.getExtras();
                    ArrayList<String> templist = b.getStringArrayList("modellist");
//                    for (String tempid : templist) {
//                        if (!modellist.contains(tempid)) {
//                            modellist.add(tempid);
//                            scancount++;
//                        }
//                    }
//                    LoadGridview();
//                    CheckScanOut();
                    for (final String tempid : templist) {
                        SCSDK.getInstance().GetProductdetailByScan(Session.getInstance().getShopCode(), tempid, Session.getInstance().getToken(), new SCResponseListener() {
                            @Override
                            public void onResult(Object result) {
                                final SCResult.ProductDetailResult product = (SCResult.ProductDetailResult) result;
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (productid.equals(product.modelcode)) {
                                            if (!modellist.contains(tempid)) {
                                                modellist.add(tempid);
                                                scancount++;
                                            }
                                            LoadGridview();
                                            CheckScanOut();
                                        } else {
                                            Toast.makeText(LookStoreActivity.this, "条码【" + tempid + "】不是当前型号！", Toast.LENGTH_SHORT).show();

                                        }
                                    }
                                });
                            }

                            @Override
                            public void onError(final int code, final String errormsg) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(LookStoreActivity.this, errormsg, Toast.LENGTH_SHORT).show();

                                    }
                                });
                            }
                        });
                    }
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_look);

        productid = getIntent().getStringExtra("productid");
        parentflag = getIntent().getBooleanExtra("isparentdetail", false);
        initView();
        initData();
    }

    private void initView() {
        btn_back = (Button) findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClickBack();
            }
        });
        img_pic = (ImageView) findViewById(R.id.img_pic);
        tv_id = (TextView) findViewById(R.id.tv_id);
        tv_id.setText(productid);
        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_typename = (TextView) findViewById(R.id.tv_typename);
        tv_storelocation = (TextView) findViewById(R.id.tv_storelocation);
        tv_guige = (TextView) findViewById(R.id.tv_guige);
        tv_systemcount = (TextView) findViewById(R.id.tv_systemcount);
        edt_acturalcount = (EditText) findViewById(R.id.edt_acturalcount);
        edt_acturalcount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String temp = s.toString();
                if (temp.equals("")) {
                    acturalcount = -1;
                } else {
                    acturalcount = Integer.valueOf(temp);
                }
                ShowCheckView();
            }
        });
        edt_acturalcount.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        tv_look = (TextView) findViewById(R.id.tv_look);
        tv_look.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClickCheck();
            }
        });
        min_instoreview = (RelativeLayout) findViewById(R.id.min_instoreview);
        tv_incount = (TextView) findViewById(R.id.tv_incount);
        instoreview = (RelativeLayout) findViewById(R.id.instoreview);
        inid_gridview = (GridView) findViewById(R.id.inid_gridview);
        btn_print = (Button) findViewById(R.id.btn_print);
        btn_print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClickPrint();
            }
        });
        min_outstoreview = (RelativeLayout) findViewById(R.id.min_outstoreview);
        tv_outcount = (TextView) findViewById(R.id.tv_outcount);
        unmin_outstoreview = (RelativeLayout) findViewById(R.id.unmin_outstoreview);
        img_scan = (ImageView) findViewById(R.id.img_scan);
        img_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> stringList = new ArrayList<String>();
                stringList.add("扫码");
                stringList.add("录入");
                final OptionBottomDialog optionBottomDialog = new OptionBottomDialog(LookStoreActivity.this, stringList);
                optionBottomDialog.setItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if (position == 0) {
                            Intent scanintent = new Intent(LookStoreActivity.this, MipcaActivityCapture.class);
                            scanintent.putExtra("isSingle", false);
                            startActivityForResult(scanintent, SCANMODEL2);
                        } else if (position == 1) {
                            final EditText et = new EditText(LookStoreActivity.this);
                            et.setFocusable(true);
                            et.setFocusableInTouchMode(true);
                            et.requestFocus();
                            AlertDialog.Builder alert = new AlertDialog.Builder(LookStoreActivity.this).setTitle("请输入单个编码")
                                    .setView(et)
                                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            final String input = et.getText().toString();
                                            if (input != null && !input.equals("")) {
//                                                //输入编码整理为modellist
//                                                List<String> templist = Arrays.asList(input.split(","));
//                                                for (String tempid : templist) {
//                                                    if (!modellist.contains(tempid)) {
//                                                        modellist.add(tempid);
//                                                        scancount++;
//                                                    }
//                                                }
                                                SCSDK.getInstance().GetProductdetailByScan(Session.getInstance().getShopCode(), input, Session.getInstance().getToken(), new SCResponseListener() {
                                                    @Override
                                                    public void onResult(Object result) {
                                                        final SCResult.ProductDetailResult product = (SCResult.ProductDetailResult) result;
                                                        runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                if (productid.equals(product.modelcode)) {
                                                                    if (!modellist.contains(input)) {
                                                                        modellist.add(input);
                                                                        scancount++;
                                                                    }
                                                                    LoadGridview();
                                                                   CheckScanOut();
                                                                } else {
                                                                    Toast.makeText(LookStoreActivity.this, "条码【" + input + "】不是当前型号！", Toast.LENGTH_SHORT).show();

                                                                }
                                                            }
                                                        });
                                                    }

                                                    @Override
                                                    public void onError(final int code, final String errormsg) {
                                                        runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                Toast.makeText(LookStoreActivity.this, errormsg, Toast.LENGTH_SHORT).show();

                                                            }
                                                        });
                                                    }
                                                });

                                            }
//                                            //刷新编码gridview
//                                            LoadGridview();
//                                            CheckScanOut();

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

                        optionBottomDialog.dismiss();
                    }
                });
            }
        });
        outid_gridview = (GridView) findViewById(R.id.outid_gridview);
    }

    private AlertDialog checkAlert;

    private void ClickBack() {
        if (checkAlert == null) {
            checkAlert = new AlertDialog.Builder(LookStoreActivity.this)
                    .setTitle("是否确认要返回上一页？")
                    .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Finish();
                            checkAlert.dismiss();

                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            checkAlert.dismiss();
                        }
                    })
                    .create();
        }
        checkAlert.show();
    }

    private void Finish() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
        }
        if (parentflag) {
            Intent data = new Intent();
            setResult(RESULT_CANCELED, data);
        }
        finish();
    }

    private void initData() {
        Session.CheckRefreshToken(new RefreshTokenListener() {
            @Override
            public void RefreshTokenResult(final int resultcode) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (resultcode == Codes.Code_Success) {
                            SCSDK.getInstance().GetProductDetail(Session.getInstance().getShopCode(), productid, Session.getInstance().getToken(), new SCResponseListener() {
                                @Override
                                public void onResult(Object result) {
                                    final SCResult.ModelDetailResult res = (SCResult.ModelDetailResult) result;
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            productid = res.modelcode;
                                            tv_name.setText(res.modelname);
                                            tv_typename.setText(res.typename);
                                            tv_storelocation.setText(res.storename + " " + res.rackname + " " + res.positionname);
                                            tv_guige.setText(res.spec);
                                            tv_systemcount.setText(Integer.toString(res.store));
                                            systemcount = res.store;
                                            Glide.with(LookStoreActivity.this).load(res.modelpicurl)
                                                    .placeholder(R.drawable.icon_defaultimg)
                                                    .error(R.drawable.icon_defaultimg)
                                                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                                                    .into(img_pic);
                                            if (res.ismin == 1) {
                                                isMin = true;
                                            }
                                        }
                                    });
                                }

                                @Override
                                public void onError(int code, final String errormsg) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(LookStoreActivity.this, errormsg, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            });
                        } else {
                            Toast.makeText(LookStoreActivity.this, SCExceptionCodeList.getExceptionMsg(resultcode), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    private void ShowCheckView() {
        if (acturalcount < 0 || systemcount == acturalcount) {
            min_instoreview.setVisibility(View.GONE);
            instoreview.setVisibility(View.GONE);
            min_outstoreview.setVisibility(View.GONE);
            unmin_outstoreview.setVisibility(View.GONE);
            tv_look.setVisibility(View.GONE);
        } else {
            if (acturalcount > systemcount) {
                //盘盈
                min_outstoreview.setVisibility(View.GONE);
                unmin_outstoreview.setVisibility(View.GONE);
                if (isMin) {
                    min_instoreview.setVisibility(View.VISIBLE);
                    instoreview.setVisibility(View.GONE);
                    tv_look.setVisibility(View.VISIBLE);
                    tv_incount.setText(Integer.toString(acturalcount - systemcount));
                } else {
                    min_instoreview.setVisibility(View.GONE);
                    instoreview.setVisibility(View.VISIBLE);
                }
            } else if (acturalcount < systemcount) {
                //盘亏
                instoreview.setVisibility(View.GONE);
                min_instoreview.setVisibility(View.GONE);
                if (isMin) {
                    min_outstoreview.setVisibility(View.VISIBLE);
                    unmin_outstoreview.setVisibility(View.GONE);
                    tv_outcount.setText(Integer.toString(systemcount - acturalcount));
                    tv_look.setVisibility(View.VISIBLE);
                } else {
                    min_outstoreview.setVisibility(View.GONE);
                    unmin_outstoreview.setVisibility(View.VISIBLE);
                }
            }
        }

    }


    private void CheckScanOut() {
        if (isMin) {
            if (scancount == systemcount - acturalcount) {
                tv_look.setVisibility(View.VISIBLE);
            } else {
                tv_look.setVisibility(View.GONE);
            }
        } else {
            if (scancount == acturalcount) {
                tv_look.setVisibility(View.VISIBLE);
            } else {
                tv_look.setVisibility(View.GONE);
            }
        }
    }

    private void LoadGridview() {
        if (scanmodelAdapter == null) {
            scanmodelAdapter = new ScanModelAdapter(LookStoreActivity.this, modellist, new ClickDeleteListener() {
                @Override
                public void DeleteModel(String modelid) {
                    if (modellist.contains(modelid)) {
                        modellist.remove(modelid);
                        scancount--;
                    }
                    LoadGridview();
                    CheckScanOut();
                }
            });
            outid_gridview.setAdapter(scanmodelAdapter);
        } else {
            scanmodelAdapter.setModellist(modellist);
            scanmodelAdapter.notifyDataSetChanged();
        }
        ViewGroup.LayoutParams params = outid_gridview.getLayoutParams();
        params.height = DensityUtil.dip2px(LookStoreActivity.this, 25 * modellist.size());
        outid_gridview.setLayoutParams(params);

    }


    private void CheckScanIn() {
        if (printcount == acturalcount - systemcount) {
            tv_look.setVisibility(View.VISIBLE);
        } else {
            tv_look.setVisibility(View.GONE);
        }
    }

    private void LoadPrintGridview() {
        if (printmodelAdapter == null) {
            printmodelAdapter = new ScanModelAdapter(LookStoreActivity.this, inmodellist, new ClickDeleteListener() {
                @Override
                public void DeleteModel(String modelid) {
                    if (inmodellist.contains(modelid)) {
                        inmodellist.remove(modelid);
                        printcount--;
                    }
                    LoadPrintGridview();
                    CheckScanIn();
                }
            });
            inid_gridview.setAdapter(printmodelAdapter);
        } else {
            printmodelAdapter.setModellist(inmodellist);
            printmodelAdapter.notifyDataSetChanged();
        }
        ViewGroup.LayoutParams params = inid_gridview.getLayoutParams();
        params.height = DensityUtil.dip2px(LookStoreActivity.this, 25 * inmodellist.size());
        inid_gridview.setLayoutParams(params);
    }

    private void ClickPrint() {
        final int tempcount = acturalcount - systemcount;
        if (tempcount <= 0) {
            Toast.makeText(LookStoreActivity.this, "打印失败!实际库存数少于系统库存", Toast.LENGTH_SHORT).show();
            return;
        }
        Session.CheckRefreshToken(new RefreshTokenListener() {
            @Override
            public void RefreshTokenResult(final int resultcode) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (resultcode == Codes.Code_Success) {
                            SCSDK.getInstance().PrintBarcode(Session.getInstance().getShopCode(), productid, tempcount, Session.getInstance().getToken(), new SCResponseListener() {
                                @Override
                                public void onResult(Object result) {
                                    final SCResult.BarcodeResult res = (SCResult.BarcodeResult) result;
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (res.codes != null && res.codes.size() > 0) {
                                                for (SCResult.Barcode code : res.codes) {
                                                    inmodellist.add(code.barcode);
                                                    printcount++;
                                                }
                                                LoadPrintGridview();
                                                CheckScanIn();
                                                Toast.makeText(LookStoreActivity.this, "打印成功", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(LookStoreActivity.this, "打印失败", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }

                                @Override
                                public void onError(int code, final String errormsg) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(LookStoreActivity.this, errormsg, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            });
                        } else {
                            Toast.makeText(LookStoreActivity.this, SCExceptionCodeList.getExceptionMsg(resultcode), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }


    private void ClickCheck() {
        Session.CheckRefreshToken(new RefreshTokenListener() {
            @Override
            public void RefreshTokenResult(final int resultcode) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (resultcode == Codes.Code_Success) {
                            StringBuilder snsbuilder = new StringBuilder();
                            if (!isMin) {
                                if (systemcount - acturalcount > 0) {
                                    snsbuilder.append(modellist.get(0));
                                    for (int i = 1; i < modellist.size(); i++) {
                                        snsbuilder.append(",").append(modellist.get(i));
                                    }
                                } else {
                                    snsbuilder.append(inmodellist.get(0));
                                    for (int i = 1; i < inmodellist.size(); i++) {
                                        snsbuilder.append(",").append(inmodellist.get(i));
                                    }
                                }
                            }
                            SCSDK.getInstance().CheckStore(Session.getInstance().getShopCode(), productid, acturalcount, Session.getInstance().getToken(), snsbuilder.toString(), new SCResponseListener() {
                                @Override
                                public void onResult(Object result) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(LookStoreActivity.this, "盘库成功", Toast.LENGTH_SHORT).show();
                                            if (parentflag) {
                                                Intent data = new Intent();
                                                setResult(RESULT_OK, data);
                                            }
                                            finish();

                                        }
                                    });
                                }

                                @Override
                                public void onError(int code, final String errormsg) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(LookStoreActivity.this, errormsg, Toast.LENGTH_SHORT).show();

                                        }
                                    });
                                }
                            });

                        } else {
                            Toast.makeText(LookStoreActivity.this, SCExceptionCodeList.getExceptionMsg(resultcode), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
}
