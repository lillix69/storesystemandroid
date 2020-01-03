package qinyuanliu.storesystemandroid.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import qinyuanliu.storesystemandroid.R;
import qinyuanliu.storesystemandroid.adapter.NoticeAdapter;
import qinyuanliu.storesystemandroid.common.Session;
import qinyuanliu.storesystemandroid.http.Codes;
import qinyuanliu.storesystemandroid.http.SCExceptionCodeList;
import qinyuanliu.storesystemandroid.http.SCResponseListener;
import qinyuanliu.storesystemandroid.http.SCResult;
import qinyuanliu.storesystemandroid.http.SCSDK;
import qinyuanliu.storesystemandroid.listener.RefreshTokenListener;
import qinyuanliu.storesystemandroid.listener.SelectNoticeListener;
import qinyuanliu.storesystemandroid.model.NoticeModel;

/**
 * Created by qinyuanliu on 2018/11/19.
 */

public class NoticeActivity extends BaseActivity {
    private Button btn_back;
    private TextView tv_edit;
    private TextView tv_delete;
    private TextView tv_cancle;
    private ListView noticeview;

    private NoticeAdapter noticeAdapter;
    private ArrayList<NoticeModel> datalist;

    @Override
    protected void onResume() {
        super.onResume();
        refreshData();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice);

        initView();
    }

    private void initView() {
        noticeview = (ListView) findViewById(R.id.listview_notice);
        btn_back = (Button) findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tv_edit = (TextView) findViewById(R.id.tv_edit);
        tv_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_edit.setVisibility(View.INVISIBLE);
                tv_delete.setVisibility(View.VISIBLE);
                tv_cancle.setVisibility(View.VISIBLE);
                noticeAdapter.setEditable(true);
                noticeAdapter.notifyDataSetChanged();
            }
        });
        tv_delete = (TextView) findViewById(R.id.tv_delete);
        tv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
ClickDelete();
            }
        });
        tv_cancle = (TextView) findViewById(R.id.tv_cancle);
        tv_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_edit.setVisibility(View.VISIBLE);
                tv_delete.setVisibility(View.INVISIBLE);
                tv_cancle.setVisibility(View.INVISIBLE);
                noticeAdapter.setEditable(false);
                noticeAdapter.notifyDataSetChanged();
            }
        });
    }

    private void refreshData() {
        datalist = new ArrayList<>();
        Session.CheckRefreshToken(new RefreshTokenListener() {
            @Override
            public void RefreshTokenResult(final int resultcode) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (resultcode == Codes.Code_Success) {
                            SCSDK.getInstance().GetPushList(Session.getInstance().getShopCode(), Session.getInstance().getToken(), new SCResponseListener() {
                                @Override
                                public void onResult(Object result) {
                                    final SCResult.PushListResult res = (SCResult.PushListResult) result;
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                           if(res.details != null && res.details.size()>0){
                                               for (SCResult.PushListInfo push:
                                                    res.details) {
                                                   NoticeModel notice = new NoticeModel(push.workcode, push.workname, push.workcontent, push.worktime);
                                                   datalist.add(notice);
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
                                            Toast.makeText(NoticeActivity.this, errormsg, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            });
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    refreshListview();
                                    Toast.makeText(NoticeActivity.this, SCExceptionCodeList.getExceptionMsg(resultcode), Toast.LENGTH_SHORT).show();

                                }
                            });
                             }
                    }
                });
            }
        });
    }

    private void refreshListview() {
        if (noticeAdapter == null) {
            noticeAdapter = new NoticeAdapter(NoticeActivity.this, datalist, new SelectNoticeListener() {
                @Override
                public void SelectNotice(String noticecode, boolean isSelected) {
                    for (NoticeModel notice : datalist) {
                        if (notice.noticecode.equals(noticecode)) {
                            notice.isSelected = isSelected;
                            noticeAdapter.setModellist(datalist);
                            noticeAdapter.notifyDataSetChanged();
                            break;
                        }
                    }
                }
            });
            noticeview.setAdapter(noticeAdapter);
        } else {
            noticeAdapter.setModellist(datalist);
            noticeAdapter.notifyDataSetChanged();
        }
    }

    private void ClickDelete(){
        boolean flag = false;
        String deletestr = "";
        int count = 0;
        for (NoticeModel notice : datalist) {
            if (notice.isSelected) {
               flag = true;
                if(count>0){
                    deletestr = deletestr + ",";
               }
               deletestr = deletestr + notice.noticecode;
                count += 1;
            }
        }
        if(flag){
            final String workcodes = deletestr;
            Session.CheckRefreshToken(new RefreshTokenListener() {
                @Override
                public void RefreshTokenResult(final int resultcode) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (resultcode == Codes.Code_Success) {
                                SCSDK.getInstance().DeletePush(Session.getInstance().getShopCode(), Session.getInstance().getToken(),workcodes,  new SCResponseListener() {
                                    @Override
                                    public void onResult(Object result) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                               //同步本地datalist
                                                ArrayList<NoticeModel> newdatalist = new ArrayList<NoticeModel>();
                                                for(int i=0; i<datalist.size(); i++){
                                                    NoticeModel notice = datalist.get(i);
                                                    if(!notice.isSelected){
                                                        newdatalist.add(new NoticeModel(notice.noticecode, notice.noticetitle, notice.noticecontent, notice.noticetime));
                                                    }
                                                }
                                                datalist = newdatalist;
                                                noticeAdapter.setModellist(datalist);
                                                refreshListview();
                                            }
                                        });
                                    }

                                    @Override
                                    public void onError(int code, final String errormsg) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(NoticeActivity.this, errormsg, Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                });
                            } else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(NoticeActivity.this, SCExceptionCodeList.getExceptionMsg(resultcode), Toast.LENGTH_SHORT).show();

                                    }
                                });
                            }
                        }
                    });
                }
            });
        }
        else{
            Toast.makeText(NoticeActivity.this, "请选择要删除的消息提醒！", Toast.LENGTH_SHORT).show();
        }
    }
}
