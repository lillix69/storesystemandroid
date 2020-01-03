package qinyuanliu.storesystemandroid.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
 * Created by lillix on 6/28/18.
 */
public class ProductInActivity extends BaseActivity {
    private Button btn_back;
    private TextView tv_pid;
    private EditText edt_remark;
    private TextView tv_reason;
    private TextView tv_in;
    private String[] reasonData;
    private HashMap<String, Integer> reasonnamecode = new HashMap<>();
    private String remark = null;
    private int reason = -1;
    private String reasonstr = "";
    private AlertDialog reasonAlert;
    private TextView tv_storename;
    private TextView tv_rack;
    private TextView tv_location;
    private TextView tv_guige;
    private TextView tv_name;
    private ImageView img_pic;

    private String productid;
    private String modelid;
    private String typeid;
    private String modelname;
    private String guige;
    private String storename;
    private String rackname;
    private String locationname;
    private String picurl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_productin);


        productid = getIntent().getStringExtra("productid");
        modelid = getIntent().getStringExtra("modelid");
        typeid = getIntent().getStringExtra("typeid");
        modelname = getIntent().getStringExtra("modelname");
        storename = getIntent().getStringExtra("storename");
        rackname = getIntent().getStringExtra("rackname");
        locationname = getIntent().getStringExtra("locationname");
        picurl = getIntent().getStringExtra("picurl");
        guige = getIntent().getStringExtra("guige");
        initView();
        ClickReason();
    }

    private void initView() {
        btn_back = (Button) findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        edt_remark = (EditText) findViewById(R.id.edt_remark);
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
        tv_in = (TextView) findViewById(R.id.tv_in);
        tv_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClickIn();
            }
        });
        tv_pid = (TextView) findViewById(R.id.tv_pid);
        tv_pid.setText(productid);
        tv_reason = (TextView) findViewById(R.id.tv_reason);
        tv_reason.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClickReason();
            }
        });
        img_pic = (ImageView) findViewById(R.id.img_pic);
        Glide.with(ProductInActivity.this).load(picurl)
                .placeholder(R.drawable.icon_defaultimg)
                .error(R.drawable.icon_defaultimg)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(img_pic);
        tv_storename = (TextView) findViewById(R.id.tv_storename);
        tv_rack = (TextView) findViewById(R.id.tv_rack);
        tv_location = (TextView) findViewById(R.id.tv_location);
        tv_guige = (TextView) findViewById(R.id.tv_guige);
        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_name.setText(modelname);
        tv_storename.setText(storename);
        tv_rack.setText(rackname);
        tv_location.setText(locationname);
        tv_guige.setText(guige);
    }

    private void ClickReason() {
        if (reasonData == null) {

            if (typeid != null && !typeid.equals("")) {
                Session.CheckRefreshToken(new RefreshTokenListener() {
                    @Override
                    public void RefreshTokenResult(final int resultcode) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (resultcode == Codes.Code_Success) {
                                    SCSDK.getInstance().GetReason(Session.getInstance().getShopCode(),typeid, 1, Session.getInstance().getToken(), new SCResponseListener() {
                                        @Override
                                        public void onResult(Object result) {
                                            final SCResult.ReasonResult res = (SCResult.ReasonResult) result;
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    if (res.types != null && res.types.size() > 0) {
                                                        reasonData = new String[res.types.size()];
                                                        for (int i = 0; i < res.types.size(); i++) {
                                                            SCResult.Reason tempreason = res.types.get(i);
                                                            reasonData[i] = tempreason.name;
                                                            reasonnamecode.put(tempreason.name, tempreason.code);
                                                        }
                                                        ShowReasonAlert();
                                                    }
                                                }
                                            });
                                        }

                                        @Override
                                        public void onError(int code, final String errormsg) {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Toast.makeText(ProductInActivity.this, errormsg, Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                    });
                                } else {
                                    Toast.makeText(ProductInActivity.this, SCExceptionCodeList.getExceptionMsg(resultcode), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
            } else {
                Toast.makeText(ProductInActivity.this, "型号类型编码为空!", Toast.LENGTH_SHORT).show();
            }
        } else {
            ShowReasonAlert();
        }
    }

    private void ShowReasonAlert() {
        if (reasonAlert == null) {
            reasonAlert = new AlertDialog.Builder(ProductInActivity.this)
                    .setTitle("选择入库原因")
                    .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            tv_reason.setText(reasonstr);
                            reasonAlert.dismiss();
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            reasonAlert.dismiss();
                        }
                    })
                    .setSingleChoiceItems(reasonData, reason, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            reasonstr = reasonData[which];
                            reason = reasonnamecode.get(reasonstr);
                        }
                    }).create();
        }
        reasonAlert.show();
    }


    private void ClickIn() {
        if (reason < 0) {
            Toast.makeText(ProductInActivity.this, "入库原因不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        Session.CheckRefreshToken(new RefreshTokenListener() {
            @Override
            public void RefreshTokenResult(final int resultcode) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (resultcode == Codes.Code_Success) {
                            SCSDK.getInstance().InStore(Session.getInstance().getShopCode(),modelid, 1, reason, Session.getInstance().getToken(), productid, remark, new SCResponseListener() {
                                @Override
                                public void onResult(Object result) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(ProductInActivity.this, "入库成功", Toast.LENGTH_SHORT).show();
                                            Intent data = new Intent();
                                            setResult(RESULT_OK, data);
                                            finish();

                                        }
                                    });
                                }

                                @Override
                                public void onError(int code, final String errormsg) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(ProductInActivity.this, errormsg, Toast.LENGTH_SHORT).show();

                                        }
                                    });
                                }
                            });

                        } else {
                            Toast.makeText(ProductInActivity.this, SCExceptionCodeList.getExceptionMsg(resultcode), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
}
