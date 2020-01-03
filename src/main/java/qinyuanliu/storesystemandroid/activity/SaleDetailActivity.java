package qinyuanliu.storesystemandroid.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import qinyuanliu.storesystemandroid.R;
import qinyuanliu.storesystemandroid.adapter.SaleDetailAdapter;
import qinyuanliu.storesystemandroid.common.Session;
import qinyuanliu.storesystemandroid.http.Codes;
import qinyuanliu.storesystemandroid.http.SCExceptionCodeList;
import qinyuanliu.storesystemandroid.http.SCResponseListener;
import qinyuanliu.storesystemandroid.http.SCResult;
import qinyuanliu.storesystemandroid.http.SCSDK;
import qinyuanliu.storesystemandroid.http.util.Json;
import qinyuanliu.storesystemandroid.listener.RefreshTokenListener;

/**
 * Created by qinyuanliu on 2019/4/15.
 */

public class SaleDetailActivity extends BaseActivity {
    private Button btn_back;
    private ListView listview_models;
    private TextView tv_client;
    private TextView tv_salecode;
    private TextView tv_storestatus;
    private TextView tv_money;
    private TextView tv_paystatus;
    private TextView tv_remark;
    private TextView tv_tel;
    private TextView tv_address;
    private TextView tv_modify;
    private TextView tv_history;
    private TextView tv_fahuo;
    private TextView tv_jiesuan;

    private String salecode;
    private ArrayList<SCResult.SaleOrderModel> datalist;
    private SaleDetailAdapter saleDetailAdapter;
    private String name;
    private String tel;
    private String remark;
    private String allcost;
    private String receivername;
    private String receivertel;
    private String receiveraddress;


    @Override
    protected void onResume(){

        super.onResume();
        initData();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        salecode = getIntent().getStringExtra("salecode");

        setContentView(R.layout.activity_saledetail);
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
        tv_salecode = (TextView) findViewById(R.id.tv_salecode);
        tv_client = (TextView) findViewById(R.id.tv_client);
        tv_storestatus = (TextView)findViewById(R.id.tv_storestatus);
        tv_paystatus = (TextView)findViewById(R.id.tv_paystatus);
        tv_money = (TextView)findViewById(R.id.tv_money);
        tv_remark = (TextView)findViewById(R.id.tv_remark);
                tv_tel = (TextView)findViewById(R.id.tv_tel);
        tv_address = (TextView)findViewById(R.id.tv_address);
        listview_models = (ListView) findViewById(R.id.listview_models);

        tv_modify = (TextView)findViewById(R.id.tv_modify);
        tv_modify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SaleDetailActivity.this, EditCustomerActivity.class);
                intent.putExtra("saleordercode",salecode);
                intent.putExtra("salecustomername",name);
                intent.putExtra("salecustomertel",tel);
                intent.putExtra("saleremark",remark);
                intent.putExtra("receivername",receivername);
                intent.putExtra("receivertel",receivertel);
                intent.putExtra("receiveraddress",receiveraddress);
                startActivity(intent);
            }
        });
        tv_history = (TextView)findViewById(R.id.tv_history);
        tv_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SaleDetailActivity.this, SaleHistoryActivity.class);
                intent.putExtra("saleordercode",salecode);
                intent.putExtra("salecustomername",name);
                intent.putExtra("salecustomertel",tel);
                intent.putExtra("saleremark",remark);
                intent.putExtra("receiveraddress",receiveraddress);
                startActivity(intent);
            }
        });
        tv_fahuo = (TextView)findViewById(R.id.tv_fahuo);
        tv_fahuo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SaleDetailActivity.this, FahuoActivity.class);
                intent.putExtra("saleordercode",salecode);
                intent.putExtra("clientinfo",name+"/"+tel);
                ArrayList<String> strlist = new ArrayList<String>();
                for (SCResult.SaleOrderModel info: datalist) {
                    strlist.add(Json.encode(info));
                }
                intent.putStringArrayListExtra("modellist",strlist);
                startActivity(intent);
            }
        });
        tv_jiesuan = (TextView)findViewById(R.id.tv_jiesuan);
        tv_jiesuan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SaleDetailActivity.this, CloseOrderActivity.class);
                intent.putExtra("saleordercode",salecode);
                intent.putExtra("salecustomername",name);
                intent.putExtra("salecustomertel",tel);
                intent.putExtra("saleremark",remark);
                intent.putExtra("saleallcost",allcost);
                startActivity(intent);
            }
        });
    }

    private void initData() {
        datalist = new ArrayList<>();
        Session.CheckRefreshToken(new RefreshTokenListener() {
            @Override
            public void RefreshTokenResult(final int resultcode) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (resultcode == Codes.Code_Success) {
                            SCSDK.getInstance().GetSaleOrderDetail(salecode, Session.getInstance().getShopCode(), Session.getInstance().getToken(), new SCResponseListener() {
                                @Override
                                public void onResult(Object result) {
                                    final SCResult.SaleOrderStoreResult res = (SCResult.SaleOrderStoreResult) result;
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            tv_salecode.setText(res.saleordercode);
                                            tv_client.setText(res.customername);
                                            name = res.customername;
                                            tv_tel.setText(res.customertel);
                                            tel = res.customertel;
                                            receivername = res.receivername;
                                            receivertel = res.receiver;
                                            receiveraddress = res.address;
                                            tv_address.setText(res.address);
                                            tv_storestatus.setText(res.storestatustext);
                                            tv_paystatus.setText(res.paytatustext);
                                            tv_money.setText(res.money);
                                            allcost = res.money;
                                            tv_remark.setText(res.remark);
                                            remark = res.remark;
                                            if (res.salemodels != null && res.salemodels.size() > 0) {
                                                datalist = res.salemodels;
                                            }
                                           //showmodifycustomerbutton;//0不显示  1显示
                                            if(res.showmodifycustomerbutton == 1){
                                                tv_modify.setVisibility(View.VISIBLE);
                                            }
                                            else{
                                                tv_modify.setVisibility(View.GONE);
                                            }
                                            // buttonstatus; //0全部不显示  1只显示去发货按钮  2只显示去结算按钮 3全部显示
                                            if(res.buttonstatus == 0){
                                                tv_fahuo.setVisibility(View.GONE);
                                                tv_jiesuan.setVisibility(View.GONE);
                                            }
                                            else if(res.buttonstatus == 1){

                                                tv_fahuo.setVisibility(View.VISIBLE);
                                                tv_jiesuan.setVisibility(View.GONE);
                                            }
                                            else if(res.buttonstatus == 2){
                                                tv_fahuo.setVisibility(View.GONE);
                                                tv_jiesuan.setVisibility(View.VISIBLE);
                                            }
                                            else if(res.buttonstatus == 3){

                                                tv_jiesuan.setVisibility(View.VISIBLE);
                                                tv_fahuo.setVisibility(View.VISIBLE);
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
                                            Toast.makeText(SaleDetailActivity.this, errormsg, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            });
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(SaleDetailActivity.this, SCExceptionCodeList.getExceptionMsg(resultcode), Toast.LENGTH_SHORT).show();

                                }
                            });
                        }
                    }
                });
            }
        });
    }

    private void refreshListview() {
        if (saleDetailAdapter == null) {
            saleDetailAdapter = new SaleDetailAdapter(SaleDetailActivity.this, datalist);
            listview_models.setAdapter(saleDetailAdapter);
        } else {
            saleDetailAdapter.setModellist(datalist);
            saleDetailAdapter.notifyDataSetChanged();
        }
    }
}
