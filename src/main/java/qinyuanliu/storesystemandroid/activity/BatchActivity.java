package qinyuanliu.storesystemandroid.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import qinyuanliu.storesystemandroid.R;
import qinyuanliu.storesystemandroid.common.Session;
import qinyuanliu.storesystemandroid.http.Codes;
import qinyuanliu.storesystemandroid.http.SCExceptionCodeList;
import qinyuanliu.storesystemandroid.http.SCResponseListener;
import qinyuanliu.storesystemandroid.http.SCResult;
import qinyuanliu.storesystemandroid.http.SCSDK;
import qinyuanliu.storesystemandroid.listener.RefreshTokenListener;
import qinyuanliu.storesystemandroid.util.DensityUtil;


/**
 * Created by qinyuanliu on 2019/7/21.
 */

public class BatchActivity extends BaseActivity {
    private TextView tv_showsns;
    private TextView tv_snscount;
    private Button btn_scan;
    private TextView tv_reason;
    private Button btn_back;
    private TextView tv_out;
    private EditText edt_remark;

    private String remark = "";
    private ArrayList<String> snslist = new ArrayList<>();
   // private HashSet<String> snslist = new HashSet<>();
    //出库原因 0：生产出库 1：补发出库 2：售后出库 3：成品出库 9：其它
    private String[] reasonData ;
    private HashMap<String, Integer> reasonnamecode = new HashMap<>();
    private int reason = -1;
    private String reasonstr = "";
    private AlertDialog reasonAlert;
    private  AlertDialog scanAlert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_batch);
        initView();

        reasonnamecode.put("生产出库", 0);
        reasonnamecode.put("补发出库", 1);
        reasonnamecode.put("售后出库", 2);
        reasonnamecode.put("成品出库", 3);
        reasonnamecode.put("其它", 9);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK) {
            if (requestCode == 3000) {
                ArrayList<String> deletestr = data.getStringArrayListExtra("deletesns");
                for (String delete : deletestr) {
                    if (snslist.contains(delete)) {
                        snslist.remove(delete);
                    }
                }
                tv_snscount.setText(Integer.toString(snslist.size()));
            } else if (requestCode == 3001) {
                ArrayList<String> scanstr = data.getStringArrayListExtra("modellist");
                for (String scan : scanstr) {
                    if (!snslist.contains(scan)) {
                        snslist.add(scan);
                    }
                }
                tv_snscount.setText(Integer.toString(snslist.size()));
            }
        }
    }

    private void initView() {
        btn_back = (Button) findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tv_out = (TextView) findViewById(R.id.tv_out);
        tv_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClickOut();
            }
        });
        btn_scan = (Button) findViewById(R.id.btn_scan);
        btn_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClickScan();
            }
        });
        tv_snscount = (TextView) findViewById(R.id.tv_snscount);
        tv_showsns = (TextView) findViewById(R.id.tv_showsns);
        tv_showsns.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(snslist.size() == 0){
                    Toast.makeText(BatchActivity.this, "请先输入条码", Toast.LENGTH_SHORT).show();
                    return;
                }
                ClickShow();
            }
        });
        tv_reason = (TextView) findViewById(R.id.tv_reason);
        tv_reason.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClickReason();
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
    }

    private void ClickScan() {
        if(scanAlert == null) {
           scanAlert = new AlertDialog.Builder(this).setTitle("选择扫码方式")
                    .setCancelable(true)
                    .setPositiveButton("扫码", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent scanintent = new Intent(BatchActivity.this, MipcaActivityCapture.class);
                            scanintent.putExtra("isSingle", false);
                            startActivityForResult(scanintent, 3001);
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
        final EditText et = new EditText(BatchActivity.this);
        et.setFocusable(true);
        et.setFocusableInTouchMode(true);
        et.requestFocus();
        AlertDialog.Builder alert = new AlertDialog.Builder(BatchActivity.this).setTitle("请输入条码(多个用逗号隔开)")
                .setView(et)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String input = et.getText().toString();
                        if(input != null && !input.equals("")) {
                            //输入编码整理
                            List<String> templist = Arrays.asList(input.split(","));
                            for (String tempid : templist) {
                                if (!snslist.contains(tempid)) {
                                    snslist.add(tempid);
                                }
                            }
                            tv_snscount.setText(Integer.toString(snslist.size()));
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
    private void ClickOut() {
        if (reason == -1) {
            Toast.makeText(BatchActivity.this, "请选择出库原因!", Toast.LENGTH_SHORT).show();
            return;
        }
        if(snslist.size()==0){
            Toast.makeText(BatchActivity.this, "请输入条码!", Toast.LENGTH_SHORT).show();
            return;
        }
        Session.CheckRefreshToken(new RefreshTokenListener() {
            @Override
            public void RefreshTokenResult(final int resultcode) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (resultcode == Codes.Code_Success) {
                            String sns = "";
                            for (String snsstr:
                                snslist ) {
                                if(!sns.equals("")){
                                   sns = sns + ",";
                                }
                                sns = sns + snsstr;
                            }
                            SCSDK.getInstance().BatchStore(sns, remark, reason,Session.getInstance().getShopCode(), Session.getInstance().getToken(), new SCResponseListener() {
                                @Override
                                public void onResult(final Object result) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(BatchActivity.this, "操作成功!", Toast.LENGTH_SHORT).show();
finish();
                                        }
                                    });
                                }

                                @Override
                                public void onError(int code, final String errormsg) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(BatchActivity.this, errormsg, Toast.LENGTH_SHORT).show();

                                        }
                                    });
                                }
                            });
                        } else {
                            Toast.makeText(BatchActivity.this, SCExceptionCodeList.getExceptionMsg(resultcode), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    private void ClickShow() {
        String snsstr = "";
        for (String sns : snslist) {
            if (!snsstr.equals("")) {
                snsstr = snsstr + ",";
            }
            snsstr = snsstr + sns;
        }
        Intent intent = new Intent(BatchActivity.this, EditBatchStoreActivity.class);
        intent.putExtra("snsstr", snsstr);
        startActivityForResult(intent, 3000);
    }

    //出库原因 0：生产出库 1：补发出库 2：售后出库 3：成品出库 9：其它
    private String tempreasonstr = "";
    private int tempreason = -1;

    private void ClickReason() {
        if(reasonData == null){
                Session.CheckRefreshToken(new RefreshTokenListener() {
                    @Override
                    public void RefreshTokenResult(final int resultcode) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (resultcode == Codes.Code_Success) {
                                    SCSDK.getInstance().GetReason(Session.getInstance().getShopCode(),"BATCHOUT", 0, Session.getInstance().getToken(), new SCResponseListener() {
                                        @Override
                                        public void onResult(Object result) {
                                            final SCResult.ReasonResult res = (SCResult.ReasonResult) result;
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    if (res.types != null && res.types.size() > 0) {
                                                        reasonData = new String[res.types.size()];
                                                        for(int i=0; i<res.types.size(); i++){
                                                            SCResult.Reason tempreason = res.types.get(i);
                                                            reasonData[i] = tempreason.name;
                                                            reasonnamecode.put(tempreason.name,tempreason.code);
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
                                                    Toast.makeText(BatchActivity.this, errormsg, Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                    });
                                } else {
                                    Toast.makeText(BatchActivity.this, SCExceptionCodeList.getExceptionMsg(resultcode), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });

        }
        else{
            ShowReasonAlert();
        }
    }

    private void ShowReasonAlert(){
        if (reasonAlert == null) {
            reasonAlert = new AlertDialog.Builder(BatchActivity.this)
                    .setTitle("选择出库原因")
                    .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            reason = tempreason;
                            reasonstr = tempreasonstr;
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
                    .setSingleChoiceItems(reasonData, -1, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            tempreasonstr = reasonData[which];
                            tempreason = reasonnamecode.get(tempreasonstr);


                        }
                    }).create();
        }
        reasonAlert.show();
    }

}
