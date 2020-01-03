package qinyuanliu.storesystemandroid.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import qinyuanliu.storesystemandroid.R;
import qinyuanliu.storesystemandroid.common.Session;
import qinyuanliu.storesystemandroid.http.Codes;
import qinyuanliu.storesystemandroid.http.SCExceptionCodeList;
import qinyuanliu.storesystemandroid.http.SCResponseListener;
import qinyuanliu.storesystemandroid.http.SCSDK;
import qinyuanliu.storesystemandroid.listener.RefreshTokenListener;

/**
 * Created by qinyuanliu on 2019/9/26.
 */

public class BatchStoreActivity extends BaseActivity {
    private Button btn_back;
    private String guid;
    private Button btn_scan;
    private TextView tv_showsns;
    private TextView tv_snscount;
    private TextView tv_batch;
    private ArrayList<String> snslist = new ArrayList<>();
    //private HashSet<String> snslist = new HashSet<>();
    private  AlertDialog scanAlert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_batchstore);

        guid = getIntent().getStringExtra("productid");

        initView();
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
        btn_scan = (Button) findViewById(R.id.btn_scan);
        btn_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClickScan();
            }
        });
        tv_showsns = (TextView) findViewById(R.id.tv_showsns);
        tv_showsns.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(snslist.size() == 0){
                    Toast.makeText(BatchStoreActivity.this, "请先扫描条码", Toast.LENGTH_SHORT).show();
                    return;
                }
                ClickShow();
            }
        });
        tv_batch = (TextView) findViewById(R.id.tv_batch);
        tv_batch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClickBatch();
            }
        });
        tv_snscount = (TextView) findViewById(R.id.tv_snscount);

    }

    private void ClickBatch() {
        if(snslist.size()==0){
            Toast.makeText(BatchStoreActivity.this, "请扫描条码!", Toast.LENGTH_SHORT).show();
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
                            SCSDK.getInstance().BatchTransfer(guid,sns,Session.getInstance().getShopCode(), Session.getInstance().getToken(), new SCResponseListener() {
                                @Override
                                public void onResult(final Object result) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(BatchStoreActivity.this, "操作成功!", Toast.LENGTH_SHORT).show();
                                            finish();
                                        }
                                    });
                                }

                                @Override
                                public void onError(int code, final String errormsg) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(BatchStoreActivity.this, errormsg, Toast.LENGTH_SHORT).show();

                                        }
                                    });
                                }
                            });
                        } else {
                            Toast.makeText(BatchStoreActivity.this, SCExceptionCodeList.getExceptionMsg(resultcode), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    private void ClickScan(){
        if(scanAlert == null) {
            scanAlert = new AlertDialog.Builder(this).setTitle("选择扫码方式")
                    .setCancelable(true)
                    .setPositiveButton("扫码", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent scanintent = new Intent(BatchStoreActivity.this, MipcaActivityCapture.class);
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
        final EditText et = new EditText(BatchStoreActivity.this);
        et.setFocusable(true);
        et.setFocusableInTouchMode(true);
        et.requestFocus();
        AlertDialog.Builder alert = new AlertDialog.Builder(BatchStoreActivity.this).setTitle("请输入条码(多个用逗号隔开)")
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

    private void ClickShow() {
        String snsstr = "";
        for (String sns : snslist) {
            if (!snsstr.equals("")) {
                snsstr = snsstr + ",";
            }
            snsstr = snsstr + sns;
        }
        Intent intent = new Intent(BatchStoreActivity.this, EditBatchStoreActivity.class);
        intent.putExtra("snsstr", snsstr);
        startActivityForResult(intent, 3000);
    }
}
