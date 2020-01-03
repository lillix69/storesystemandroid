package qinyuanliu.storesystemandroid.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import qinyuanliu.storesystemandroid.R;
import qinyuanliu.storesystemandroid.adapter.NoticeAdapter;
import qinyuanliu.storesystemandroid.adapter.PurListAdapter;
import qinyuanliu.storesystemandroid.common.Session;
import qinyuanliu.storesystemandroid.http.Codes;
import qinyuanliu.storesystemandroid.http.SCExceptionCodeList;
import qinyuanliu.storesystemandroid.http.SCResponseListener;
import qinyuanliu.storesystemandroid.http.SCResult;
import qinyuanliu.storesystemandroid.http.SCSDK;
import qinyuanliu.storesystemandroid.http.util.Json;
import qinyuanliu.storesystemandroid.listener.RefreshTokenListener;
import qinyuanliu.storesystemandroid.listener.SelectNoticeListener;
import qinyuanliu.storesystemandroid.listener.SelectPurItemListener;
import qinyuanliu.storesystemandroid.listener.ShowPurModelListener;
import qinyuanliu.storesystemandroid.model.NoticeModel;
import qinyuanliu.storesystemandroid.model.PurModel;
import qinyuanliu.storesystemandroid.util.DateUtil;

/**
 * Created by qinyuanliu on 2019/9/9.
 */

public class GetPurListActivity extends BaseActivity {
    private Button btn_back;
    private TextView tv_begindate;
    private TextView tv_enddate;
    private TextView tv_search;
    private TextView tv_confirm;
    private ListView listview_purlist;

    private Calendar calendarStart = Calendar.getInstance();
    private Calendar calendarEnd = Calendar.getInstance();
    private int startYear = 0;
    private int startMonth = 0;
    private int startDay = 0;
    private int endYear = 0;
    private int endMonth = 0;
    private int endDay = 0;
    private String begindate = "";
    private String enddate = "";
private ArrayList<PurModel> datalist = new ArrayList<>();
private PurListAdapter purlistAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_getpurlist);

        initData();
        initView();
        LoadPurList();
    }

    private void initView() {
        btn_back = (Button) findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tv_begindate = (TextView) findViewById(R.id.tv_begindate);
        tv_begindate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClickStartDate();
            }
        });
        tv_begindate.setText(begindate);
        tv_enddate = (TextView) findViewById(R.id.tv_enddate);
        tv_enddate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClickEndDate();
            }
        });
        tv_enddate.setText(enddate);
        tv_search = (TextView) findViewById(R.id.tv_search);
        tv_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClickSearch();
            }
        });
        tv_confirm = (TextView) findViewById(R.id.tv_confirm);
        tv_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClickConfirm();
            }
        });
        listview_purlist = (ListView)findViewById(R.id.listview_purlist);
    }

    private void initData(){
        calendarStart.setTime(new Date());
        calendarStart.add(Calendar.DATE, -30);
        startYear = calendarStart.get(Calendar.YEAR);
        startMonth = calendarStart.get(Calendar.MONTH);
        startDay = calendarStart.get(Calendar.DATE);
        begindate = DateUtil.GetDateString(startYear, startMonth + 1, startDay);

        calendarEnd.setTime(new Date());
        endYear = calendarEnd.get(Calendar.YEAR);
        endMonth = calendarEnd.get(Calendar.MONTH);
        endDay = calendarEnd.get(Calendar.DATE);
        enddate = DateUtil.GetDateString(endYear, endMonth + 1, endDay);
    }

    private void ClickStartDate() {
        View view = View.inflate(GetPurListActivity.this, R.layout.date_picker, null);
        final DatePicker datePicker = (DatePicker) view.findViewById(R.id.new_act_date_picker);
        datePicker.init(startYear, startMonth, startDay, null);
        // Build DateTimeDialog
        AlertDialog.Builder begindatetimeBuilder = new AlertDialog.Builder(GetPurListActivity.this);
        begindatetimeBuilder.setView(view);
        begindatetimeBuilder.setTitle("选择查询下单开始时间");
        begindatetimeBuilder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startYear = datePicker.getYear();
                startMonth = datePicker.getMonth();
                startDay = datePicker.getDayOfMonth();
                String str = DateUtil.GetDateString(startYear, startMonth + 1, startDay);
                begindate = str;
                tv_begindate.setText(str);
            }
        });
        begindatetimeBuilder.show();
    }

    private void ClickEndDate() {
        View view = View.inflate(GetPurListActivity.this, R.layout.date_picker, null);
        final DatePicker datePicker = (DatePicker) view.findViewById(R.id.new_act_date_picker);
        datePicker.init(endYear, endMonth, endDay, null);

        AlertDialog.Builder enddatetimeBuilder = new AlertDialog.Builder(GetPurListActivity.this);
        enddatetimeBuilder.setView(view);
        enddatetimeBuilder.setTitle("选择查询下单结束时间");
        enddatetimeBuilder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                endYear = datePicker.getYear();
                endMonth = datePicker.getMonth();
                endDay = datePicker.getDayOfMonth();
                String str = DateUtil.GetDateString(endYear, endMonth + 1, endDay);
                enddate = str;
                tv_enddate.setText(str);
            }
        });
        enddatetimeBuilder.show();
    }

    private void ClickSearch() {
        LoadPurList();
    }

    private void LoadPurList(){
        datalist=new ArrayList<>();
        Session.CheckRefreshToken(new RefreshTokenListener() {
            @Override
            public void RefreshTokenResult(final int resultcode) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (resultcode == Codes.Code_Success) {
                            SCSDK.getInstance().GetPurchaseList(Session.getInstance().getShopCode(), Session.getInstance().getToken(),begindate,enddate, new SCResponseListener() {
                                @Override
                                public void onResult(Object result) {
                                    final SCResult.PurchaseListResult res = (SCResult.PurchaseListResult) result;
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if(res.orders != null && res.orders.size()>0){
                                                for (SCResult.PurchaseInfo pur: res.orders) {
                                                    PurModel newpur = new PurModel(pur.ordercode, pur.orderdate,Integer.toString(pur.purchasenum),pur.totalprice,pur.remark);
                                                    datalist.add(newpur);
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
                                            Toast.makeText(GetPurListActivity.this, errormsg, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            });
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    refreshListview();
                                    Toast.makeText(GetPurListActivity.this, SCExceptionCodeList.getExceptionMsg(resultcode), Toast.LENGTH_SHORT).show();

                                }
                            });
                        }
                    }
                });
            }
        });
    }
    private void refreshListview() {
        if (purlistAdapter == null) {
            purlistAdapter = new PurListAdapter(GetPurListActivity.this, datalist, new SelectPurItemListener() {
                @Override
                public void ClickOrder(String ordercode, boolean isselected) {
                    for (PurModel pur:datalist) {
                        if(pur.orderCode.equals(ordercode)){
                            pur.isSelected = true;
                        }
                        else{
                            pur.isSelected = false;
                        }
                    }
                    purlistAdapter.setModellist(datalist);
                    purlistAdapter.notifyDataSetChanged();
                }
            }, new ShowPurModelListener() {
                @Override
                public void ShowPurModel(String ordercode) {
                    Intent intent = new Intent(GetPurListActivity.this, ShowPurDetailActivity.class);
                    intent.putExtra("ordercode", ordercode);
                    startActivity(intent);
                }
            });
            listview_purlist.setAdapter(purlistAdapter);
        } else {
            purlistAdapter.setModellist(datalist);
            purlistAdapter.notifyDataSetChanged();
        }
    }

    String tempordercode = "";
    private void ClickConfirm(){
        for (PurModel pur:datalist) {
            if(pur.isSelected){
                tempordercode = pur.orderCode;
            }
        }
        if(tempordercode.equals("")){
            Toast.makeText(GetPurListActivity.this,"请选择一个采购单！", Toast.LENGTH_SHORT).show();
        }
        Session.CheckRefreshToken(new RefreshTokenListener() {
            @Override
            public void RefreshTokenResult(final int resultcode) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (resultcode == Codes.Code_Success) {
                            SCSDK.getInstance().GetPurchaseModel(Session.getInstance().getShopCode(), Session.getInstance().getToken(),tempordercode, new SCResponseListener() {
                                @Override
                                public void onResult(Object result) {
                                    final SCResult.PurchaseModelResult res = (SCResult.PurchaseModelResult) result;
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Intent data = new Intent();
                                            data.putExtra("modellist", Json.encode(res));
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
                                            Toast.makeText(GetPurListActivity.this, errormsg, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            });
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(GetPurListActivity.this, SCExceptionCodeList.getExceptionMsg(resultcode), Toast.LENGTH_SHORT).show();

                                }
                            });
                        }
                    }
                });
            }
        });
    }
}
