package qinyuanliu.storesystemandroid.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

import qinyuanliu.storesystemandroid.R;
import qinyuanliu.storesystemandroid.common.Session;
import qinyuanliu.storesystemandroid.http.Codes;
import qinyuanliu.storesystemandroid.http.SCExceptionCodeList;
import qinyuanliu.storesystemandroid.http.SCResponseListener;
import qinyuanliu.storesystemandroid.http.SCResult;
import qinyuanliu.storesystemandroid.http.SCSDK;
import qinyuanliu.storesystemandroid.listener.RefreshTokenListener;

/**
 * Created by qinyuanliu on 2018/10/12.
 */

public class OffDutyActivity extends BaseActivity {
    private Button btn_back;
    private TextView tv_summery;
    private Button btn_more;
    private Button btn_confirm;
    private TextView tv_status;
    private Button btn_showdetail;

    @Override
    protected void onResume(){
        super.onResume();
        initData();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offduty);

        initView();

    }

    private void initView() {
        btn_showdetail = (Button)findViewById(R.id.btn_showdetail);
        btn_showdetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                java.text.SimpleDateFormat df = new java.text.SimpleDateFormat("yyyy-MM-dd");
                Calendar c = Calendar.getInstance();
                c.setTime(new Date(c.getTime().getTime()));
                String today = df.format(c.getTime());
                Intent showdetail = new Intent(OffDutyActivity.this, ShowOffdutyDetailActivity.class);
                showdetail.putExtra("offdutydate", today);
                startActivity(showdetail);
            }
        });
        btn_back = (Button) findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btn_more = (Button) findViewById(R.id.btn_more);
        btn_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(OffDutyActivity.this, OffdutyHistoryActivity.class));
            }
        });
        btn_confirm = (Button) findViewById(R.id.btn_confirm);
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                java.text.SimpleDateFormat df = new java.text.SimpleDateFormat("yyyy-MM-dd");
                Calendar c = Calendar.getInstance();
                c.setTime(new Date(c.getTime().getTime()));
                String today = df.format(c.getTime());
                Intent showdetail = new Intent(OffDutyActivity.this, OffdutyDetailActivity.class);
                showdetail.putExtra("offdutydate", today);
                startActivity(showdetail);
            }
        });
        tv_status = (TextView) findViewById(R.id.tv_status);
        tv_summery = (TextView) findViewById(R.id.tv_summery);
    }

    private void initData() {
        Session.CheckRefreshToken(new RefreshTokenListener() {
            @Override
            public void RefreshTokenResult(final int resultcode) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (resultcode == Codes.Code_Success) {
                            SCSDK.getInstance().GetDutyoffSummery(Session.getInstance().getShopCode(), Session.getInstance().getToken(), new SCResponseListener() {
                                @Override
                                public void onResult(Object result) {
                                    final SCResult.SummeryResult res = (SCResult.SummeryResult) result;
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            tv_summery.setText(res.summary);
                                            //0：未确认 1：已确认
                                            if (res.todaystatus == 0) {
                                                btn_showdetail.setVisibility(View.GONE);
                                                tv_status.setText("未确认");
                                                btn_confirm.setVisibility(View.VISIBLE);
                                            } else {
                                                btn_showdetail.setVisibility(View.VISIBLE);
                                                tv_status.setText("已确认，确认时间：" + res.confirmtime);
                                                btn_confirm.setVisibility(View.GONE);
                                            }
                                        }
                                    });
                                }

                                @Override
                                public void onError(int code, final String errormsg) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(OffDutyActivity.this, errormsg, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            });
                        } else {
                            Toast.makeText(OffDutyActivity.this, SCExceptionCodeList.getExceptionMsg(resultcode), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
}
