package qinyuanliu.storesystemandroid.activity;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import qinyuanliu.storesystemandroid.R;
import qinyuanliu.storesystemandroid.adapter.QuestionAdapter;
import qinyuanliu.storesystemandroid.common.Session;
import qinyuanliu.storesystemandroid.http.Codes;
import qinyuanliu.storesystemandroid.http.SCExceptionCodeList;
import qinyuanliu.storesystemandroid.http.SCResponseListener;
import qinyuanliu.storesystemandroid.http.SCResult;
import qinyuanliu.storesystemandroid.http.SCSDK;
import qinyuanliu.storesystemandroid.listener.RefreshTokenListener;
import qinyuanliu.storesystemandroid.listener.SelectQuestionListener;
import qinyuanliu.storesystemandroid.model.SubmitPrepareModel;

/**
 * Created by qinyuanliu on 2019/5/25.
 */

public class ShowQuestionActivity extends BaseActivity {

    private Button btn_back;
    private TextView tv_submit;
    private ListView listview_question;
    private QuestionAdapter questionAdapter;
    private ArrayList<SCResult.QuestionModel> datalist;

    @Override
    public void onResume(){
        super.onResume();
        initData();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showquestion);

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
        tv_submit = (TextView)findViewById(R.id.tv_submit);
        tv_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ShowQuestionActivity.this,SubmitQuestionActivity.class));
            }
        });
        listview_question = (ListView)findViewById(R.id.listview_question);
    }
    private void initData(){
        datalist = new ArrayList<>();
        Session.CheckRefreshToken(new RefreshTokenListener() {
            @Override
            public void RefreshTokenResult(final int resultcode) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (resultcode == Codes.Code_Success) {
                            SCSDK.getInstance().ListQuestion(Session.getInstance().getShopCode(), Session.getInstance().getToken(),  new SCResponseListener() {
                                @Override
                                public void onResult(Object result) {
                                    final SCResult.ListQuestionResult res = (SCResult.ListQuestionResult) result;
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (res != null) {
                                                if (res.questions != null && res.questions.size() > 0) {
                                                    datalist = res.questions;
                                                }
                                            }
                                            refreshListview();
                                        }
                                    });
                                }

                                @Override
                                public void onError(int code, final String errormsg) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            refreshListview();
                                            Toast.makeText(ShowQuestionActivity.this, errormsg, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            });
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    refreshListview();
                                    Toast.makeText(ShowQuestionActivity.this, SCExceptionCodeList.getExceptionMsg(resultcode), Toast.LENGTH_SHORT).show();

                                }
                            });
                        }
                    }
                });
            }
        });
    }

    private void refreshListview(){
        if(questionAdapter == null){
            questionAdapter = new QuestionAdapter(ShowQuestionActivity.this, datalist, new SelectQuestionListener() {
                @Override
                public void ClickQuestion(String questioncode) {

                    Intent detail = new Intent(ShowQuestionActivity.this, QuestionDetailActivity.class);
                    detail.putExtra("questioncode", questioncode);
                    startActivity(detail);
                }
            });
            listview_question.setAdapter(questionAdapter);
        }
        else{
            questionAdapter.setModellist(datalist);
            questionAdapter.notifyDataSetChanged();
        }
    }
}


