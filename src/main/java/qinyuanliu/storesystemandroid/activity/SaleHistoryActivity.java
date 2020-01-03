package qinyuanliu.storesystemandroid.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;

import qinyuanliu.storesystemandroid.R;
import qinyuanliu.storesystemandroid.adapter.SaleHistoryAdapter;
import qinyuanliu.storesystemandroid.common.Session;
import qinyuanliu.storesystemandroid.http.Codes;
import qinyuanliu.storesystemandroid.http.SCExceptionCodeList;
import qinyuanliu.storesystemandroid.http.SCResponseListener;
import qinyuanliu.storesystemandroid.http.SCResult;
import qinyuanliu.storesystemandroid.http.SCSDK;
import qinyuanliu.storesystemandroid.listener.RefreshTokenListener;
import qinyuanliu.storesystemandroid.listener.ShowSaleHistoryListener;
import qinyuanliu.storesystemandroid.listener.ShowSalePicListener;


/**
 * Created by qinyuanliu on 2019/8/4.
 */

public class SaleHistoryActivity extends BaseActivity {
    private Button btn_back;
    private ListView listview_salehistory;
    private TextView tv_salecode;
    private TextView tv_clientname;
    private TextView tv_clienttel;
    private TextView tv_address;
    private TextView tv_remark;

    private String tel;
    private String address;
    private String name;
    private String remark;
    private String saleordercode;
    private SaleHistoryAdapter salehistoryAdapter;
    private ArrayList<SCResult.StoreOrder> datalist = new ArrayList<>();

    private String customerinfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        saleordercode = getIntent().getStringExtra("saleordercode");
        name = getIntent().getStringExtra("salecustomername");
        tel = getIntent().getStringExtra("salecustomertel");
        remark = getIntent().getStringExtra("saleremark");
        address = getIntent().getStringExtra("receiveraddress");

        setContentView(R.layout.activity_salehistory);
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
        listview_salehistory = (ListView) findViewById(R.id.listview_salehistory);
        tv_salecode = (TextView) findViewById(R.id.tv_salecode);
        tv_salecode.setText(saleordercode);
        tv_clientname = (TextView) findViewById(R.id.tv_clientname);
        tv_clientname.setText(name);
        tv_clienttel = (TextView) findViewById(R.id.tv_clienttel);
        tv_clienttel.setText(tel);
        tv_address = (TextView) findViewById(R.id.tv_address);
        tv_address.setText(address);
        tv_remark = (TextView) findViewById(R.id.tv_remark);
        tv_remark.setText(remark);
    }

    private void initData() {
        Session.CheckRefreshToken(new RefreshTokenListener() {
            @Override
            public void RefreshTokenResult(final int resultcode) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (resultcode == Codes.Code_Success) {
                            SCSDK.getInstance().ListSaleorderStores(saleordercode, Session.getInstance().getShopCode(), Session.getInstance().getToken(), new SCResponseListener() {
                                @Override
                                public void onResult(Object result) {
                                    final SCResult.StoreOrderResult res = (SCResult.StoreOrderResult) result;
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            customerinfo = res.customername + "/" + res.customertel;
                                            datalist = res.storeorders;
                                            refreshListview();
                                        }
                                    });
                                }

                                @Override
                                public void onError(int code, final String errormsg) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(SaleHistoryActivity.this, errormsg, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            });
                        } else {
                            Toast.makeText(SaleHistoryActivity.this, SCExceptionCodeList.getExceptionMsg(resultcode), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }


    private void refreshListview() {
        if (salehistoryAdapter == null) {
            salehistoryAdapter = new SaleHistoryAdapter(SaleHistoryActivity.this, datalist, new ShowSaleHistoryListener() {
                @Override
                public void ShowSaleHistory(String ordercode, String orderdate, String picurl, String orderremark) {
                    Intent intent = new Intent(SaleHistoryActivity.this, SaleHistoryDetailActivity.class);
                    intent.putExtra("storeordercode",ordercode);
                    intent.putExtra("saleordercode",saleordercode);
                    intent.putExtra("customerinfo",customerinfo);
                    intent.putExtra("storeorderdate",orderdate);
                    intent.putExtra("picurl",picurl);
                    intent.putExtra("remark",orderremark);
                    startActivity(intent);
                }
            }, new ShowSalePicListener() {
                @Override
                public void ShowSalePic(String picurl) {
                    ClickImage(picurl);
                }
            });
            listview_salehistory.setAdapter(salehistoryAdapter);
        } else {
            salehistoryAdapter.setModellist(datalist);
            salehistoryAdapter.notifyDataSetChanged();
        }
    }

    private void ClickImage(String url) {
        WindowManager wm = this.getWindowManager();
        int width = wm.getDefaultDisplay().getWidth() / 2;
        final ImageView pic = new ImageView(SaleHistoryActivity.this);
        pic.setAdjustViewBounds(true);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                width, LinearLayout.LayoutParams.WRAP_CONTENT);
        pic.setLayoutParams(layoutParams);
        pic.setMaxWidth(width);
        pic.setMaxHeight(width);
        Glide.with(SaleHistoryActivity.this).load(url)
                .placeholder(R.drawable.icon_defaultimg)
                .error(R.drawable.icon_defaultimg)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(pic);

        new AlertDialog.Builder(this).setTitle("物流图片")
                .setView(pic)
                .setPositiveButton("关闭", null)
                .show();
    }
}
