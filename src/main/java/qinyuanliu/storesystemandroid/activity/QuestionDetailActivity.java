package qinyuanliu.storesystemandroid.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import qinyuanliu.storesystemandroid.R;
import qinyuanliu.storesystemandroid.common.Session;
import qinyuanliu.storesystemandroid.http.Codes;
import qinyuanliu.storesystemandroid.http.SCExceptionCodeList;
import qinyuanliu.storesystemandroid.http.SCResponseListener;
import qinyuanliu.storesystemandroid.http.SCResult;
import qinyuanliu.storesystemandroid.http.SCSDK;
import qinyuanliu.storesystemandroid.listener.RefreshTokenListener;

/**
 * Created by qinyuanliu on 2019/5/25.
 */

public class QuestionDetailActivity extends BaseActivity {
    private Button btn_back;
private TextView tv_title;
    private TextView tv_date;
    private TextView tv_describe;

    private String questioncode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        questioncode = getIntent().getStringExtra("questioncode");

        setContentView(R.layout.activity_questiondetail);
        initView();
        initData();
    }

    private void initView() {
        btn_back = (Button) findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tv_describe = (TextView)findViewById(R.id.tv_describe);
        tv_date = (TextView)findViewById(R.id.tv_date);
        tv_title = (TextView)findViewById(R.id.tv_title);

    }
    private void initData(){
        Session.CheckRefreshToken(new RefreshTokenListener() {
            @Override
            public void RefreshTokenResult(final int resultcode) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (resultcode == Codes.Code_Success) {
                            SCSDK.getInstance().GetQuestionDetail(Session.getInstance().getShopCode(), Session.getInstance().getToken(), questioncode, new SCResponseListener() {
                                @Override
                                public void onResult(Object result) {
                                    final SCResult.QuestionDetailResult res = (SCResult.QuestionDetailResult) result;
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            tv_describe.setText(res.detail);
                                            tv_title.setText(res.title);
                                            tv_date.setText(res.questiontime);

                                        }
                                    });
                                }

                                @Override
                                public void onError(int code, final String errormsg) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(QuestionDetailActivity.this, errormsg, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            });
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(QuestionDetailActivity.this, SCExceptionCodeList.getExceptionMsg(resultcode), Toast.LENGTH_SHORT).show();

                                }
                            });
                        }
                    }
                });
            }
        });
    }
}
