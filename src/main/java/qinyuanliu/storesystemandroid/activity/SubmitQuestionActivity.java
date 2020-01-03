package qinyuanliu.storesystemandroid.activity;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
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

public class SubmitQuestionActivity extends BaseActivity {
    private Button btn_back;
    private EditText edt_title;
    private EditText edt_describe;
    private TextView tv_submit;
private String describe = "";
    private String title = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_submitquestion);
        initView();
        showKeyboard();
    }

    private void initView() {
        btn_back = (Button) findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm =  (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                if(imm != null) {
                    imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
                }
                finish();
            }
        });
        edt_describe = (EditText) findViewById(R.id.edt_describe);
        edt_describe.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                describe = s.toString();
            }
        });
        edt_title = (EditText) findViewById(R.id.edt_title);
        edt_title.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                title = s.toString();
            }
        });
        tv_submit = (TextView)findViewById(R.id.tv_submit);
        tv_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(title.equals("")){
                    Toast.makeText(SubmitQuestionActivity.this,"请输入问题标题！", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(describe.equals("")){
                    Toast.makeText(SubmitQuestionActivity.this,"请输入问题描述！", Toast.LENGTH_SHORT).show();
                    return;
                }
                ClickSubmit();
            }
        });
    }
    private void showKeyboard() {
        edt_title.requestFocus();
        edt_title.postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(edt_title, 0);
            }
        }, 500);
    }

    private void ClickSubmit(){
        Session.CheckRefreshToken(new RefreshTokenListener() {
            @Override
            public void RefreshTokenResult(final int resultcode) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (resultcode == Codes.Code_Success) {
                            SCSDK.getInstance().SubmitQuestion(Session.getInstance().getShopCode(), Session.getInstance().getToken(), title, describe, new SCResponseListener() {
                                @Override
                                public void onResult(Object result) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(SubmitQuestionActivity.this,"提交成功", Toast.LENGTH_SHORT).show();
                                            InputMethodManager imm =  (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                                            if(imm != null) {
                                                imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
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
                                            Toast.makeText(SubmitQuestionActivity.this, errormsg, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            });
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(SubmitQuestionActivity.this, SCExceptionCodeList.getExceptionMsg(resultcode), Toast.LENGTH_SHORT).show();

                                }
                            });
                        }
                    }
                });
            }
        });
    }
}
