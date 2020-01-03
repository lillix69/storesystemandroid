package qinyuanliu.storesystemandroid.activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import qinyuanliu.storesystemandroid.R;
import qinyuanliu.storesystemandroid.adapter.OffdutyHistoryAdapter;
import qinyuanliu.storesystemandroid.common.Session;
import qinyuanliu.storesystemandroid.http.Codes;
import qinyuanliu.storesystemandroid.http.SCExceptionCodeList;
import qinyuanliu.storesystemandroid.http.SCResponseListener;
import qinyuanliu.storesystemandroid.http.SCResult;
import qinyuanliu.storesystemandroid.http.SCSDK;
import qinyuanliu.storesystemandroid.listener.ConfirmOffdutyListener;
import qinyuanliu.storesystemandroid.listener.RefreshTokenListener;
import qinyuanliu.storesystemandroid.listener.ShowOffdutyHistoryListener;
import qinyuanliu.storesystemandroid.util.DateUtil;

/**
 * Created by qinyuanliu on 2018/10/12.
 */

public class OffdutyHistoryActivity extends BaseActivity{
    private Button btn_back;
    private Button btn_search;
    private TextView tv_timestart;
    private TextView tv_timeend;
    private TextView tv_status;
    private PopupWindow statusPopup;
    private ListView listview;
    private TextView tv_nohistory;
    private RelativeLayout showview;
    private DatePickerDialog dialogChooseStartdate;
    private DatePickerDialog dialogChooseEnddate;
    private Calendar calendardate = Calendar.getInstance();
    private String startDate = "";
    private String endDate = "";
    private int status = 2;//0:未确认 1：已确认
    private OffdutyHistoryAdapter listAdapter;
    private ArrayList<SCResult.DutyoffHistory> datalist = new ArrayList<>();


    @Override
    protected void onResume(){
super.onResume();
        SearchHistory();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offdutyhistory);

        initView();
        initStatusPopup();//报警类型弹框
        initDatePicker();
    }

    private void initView() {
        showview = (RelativeLayout) findViewById(R.id.showview);
        tv_nohistory = (TextView) findViewById(R.id.tv_nohistory);
        btn_search = (Button) findViewById(R.id.btn_search);
        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchHistory();
            }
        });
        listview = (ListView) findViewById(R.id.listview_offdutyhistory);
        btn_back = (Button) findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tv_status = (TextView) findViewById(R.id.tv_status);
        tv_status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                statusPopup.showAsDropDown(tv_status, 0, 0);

            }
        });
        tv_timestart = (TextView) findViewById(R.id.tv_timestart);
        tv_timeend = (TextView) findViewById(R.id.tv_timeend);
        tv_timeend.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialogChooseEnddate.show();
            }
        });
        tv_timestart.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialogChooseStartdate.show();
            }
        });
    }

    private void initDatePicker(){
        java.text.SimpleDateFormat df = new java.text.SimpleDateFormat("yyyy-MM-dd");

        //开始日期
        Calendar c = Calendar.getInstance();
        c.setTime(new Date(c.getTime().getTime() - 1000 * 60 * 60 * 24 * 7));
        startDate = df.format(c.getTime());
        tv_timestart.setText(startDate);
        int startYear = Integer.parseInt(startDate.substring(0, 4));
        int startMonth = Integer.parseInt(startDate.substring(5, 7)) - 1;
        int startDay = Integer.parseInt(startDate.substring(8, 10));
        dialogChooseStartdate = new DatePickerDialog(OffdutyHistoryActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                calendardate.set(Calendar.YEAR, year);
                calendardate.set(Calendar.MONTH, monthOfYear);
                calendardate.set(Calendar.DATE, dayOfMonth);
                String tempdate = DateUtil.formatDate(null, calendardate.getTime());
                tv_timestart.setText(tempdate);
                startDate = tempdate;
            }
        }, startYear, startMonth, startDay);

        //结束日期
        Calendar c2 = Calendar.getInstance();
        c2.setTime(new Date(c2.getTime().getTime()));
        endDate = df.format(c2.getTime());
        tv_timeend.setText(endDate);
        int endYear = Integer.parseInt(endDate.substring(0, 4));
        int endMonth = Integer.parseInt(endDate.substring(5, 7)) - 1;
        int endDay = Integer.parseInt(endDate.substring(8, 10));
        dialogChooseEnddate = new DatePickerDialog(OffdutyHistoryActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                calendardate.set(Calendar.YEAR, year);
                calendardate.set(Calendar.MONTH, monthOfYear);
                calendardate.set(Calendar.DATE, dayOfMonth);
                String tempdate = DateUtil.formatDate(null, calendardate.getTime());
                tv_timeend.setText(tempdate);
                endDate = tempdate;
            }
        }, endYear, endMonth, endDay);
    }

    private void initStatusPopup() {
        if (statusPopup == null) {
            View popupView = getLayoutInflater().inflate(R.layout.popup_offduty_status, null);
            statusPopup = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            statusPopup.setFocusable(true);
            statusPopup.setBackgroundDrawable(new ColorDrawable(0x00000000));
            popupView.findViewById(R.id.btn_all).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tv_status.setText("全部");
                    status = 2;
                    statusPopup.dismiss();

                }
            });
            popupView.findViewById(R.id.btn_no).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tv_status.setText("未确认");
                    status = 0;
                    statusPopup.dismiss();

                }
            });
            popupView.findViewById(R.id.btn_yes).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tv_status.setText("已确认");
                    status = 1;
                    statusPopup.dismiss();

                }
            });

        }
    }

    private void RefreshListview() {
        if (listAdapter == null) {
            listAdapter = new OffdutyHistoryAdapter(OffdutyHistoryActivity.this, datalist, new ShowOffdutyHistoryListener() {
                @Override
                public void ShowHistory(String date, int status) {
                    Intent intent = new Intent(OffdutyHistoryActivity.this, ShowOffdutyDetailActivity.class);
                    intent.putExtra("offdutydate", date);
                    startActivity(intent);
                }
            }, new ConfirmOffdutyListener() {
                @Override
                public void ConfirmHistory(String date, int status) {
                    Intent intent = new Intent(OffdutyHistoryActivity.this, OffdutyDetailActivity.class);
                    intent.putExtra("offdutydate", date);
                    startActivity(intent);
                }
            });
            listview.setAdapter(listAdapter);
        } else {
            listAdapter.setDatalist(datalist);
            listAdapter.notifyDataSetChanged();
        }
    }


    private void SearchHistory() {
        Session.CheckRefreshToken(new RefreshTokenListener() {
            @Override
            public void RefreshTokenResult(final int resultcode) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (resultcode == Codes.Code_Success) {
                            SCSDK.getInstance().GetDutyoffHistory(Session.getInstance().getShopCode(), Session.getInstance().getToken(), startDate, endDate, status, new SCResponseListener() {
                                @Override
                                public void onResult(Object result) {
                                    final SCResult.DutyoffHistoryResult res = (SCResult.DutyoffHistoryResult) result;
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (res.details != null && res.details.size() > 0) {
                                                datalist = res.details;
                                                RefreshListview();
                                                showview.setVisibility(View.VISIBLE);
                                                tv_nohistory.setVisibility(View.GONE);
                                            } else {
                                                showview.setVisibility(View.GONE);
                                                tv_nohistory.setVisibility(View.VISIBLE);
                                            }
                                        }
                                    });
                                }

                                @Override
                                public void onError(int code, final String errormsg) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            showview.setVisibility(View.GONE);
                                            tv_nohistory.setVisibility(View.VISIBLE);
                                            Toast.makeText(OffdutyHistoryActivity.this, errormsg, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            });
                        } else {
                            showview.setVisibility(View.GONE);
                            tv_nohistory.setVisibility(View.VISIBLE);
                            Toast.makeText(OffdutyHistoryActivity.this, SCExceptionCodeList.getExceptionMsg(resultcode), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
}
