package qinyuanliu.storesystemandroid.activity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
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
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import qinyuanliu.storesystemandroid.R;
import qinyuanliu.storesystemandroid.adapter.SaleHistoryDetailAdapter;
import qinyuanliu.storesystemandroid.common.Session;
import qinyuanliu.storesystemandroid.http.Codes;
import qinyuanliu.storesystemandroid.http.SCExceptionCodeList;
import qinyuanliu.storesystemandroid.http.SCResponseListener;
import qinyuanliu.storesystemandroid.http.SCResult;
import qinyuanliu.storesystemandroid.http.SCSDK;
import qinyuanliu.storesystemandroid.listener.RefreshTokenListener;
import qinyuanliu.storesystemandroid.listener.ShowSaleHistoryListener;
import qinyuanliu.storesystemandroid.listener.ShowSalePicListener;
import qinyuanliu.storesystemandroid.listener.ShowSaleSnsListener;

/**
 * Created by qinyuanliu on 2019/8/4.
 */

public class SaleHistoryDetailActivity extends BaseActivity {
    private Button btn_back;
    private TextView tv_saleordercode;
    private TextView tv_client;
    private TextView tv_saleorderdate;
    private TextView tv_storeordercode;
    private TextView tv_showpic;
    private ImageView img_pic;
    private TextView tv_remark;
    private ListView listview_saledetail;

    private String storeordercode;
    private String saleordercode;
    private String customerinfo;
    private String storeorderdate;
    private String picurl;

private SaleHistoryDetailAdapter saleHistoryDetailAdapter;
    private ArrayList<SCResult.StoreOrderModel> datalist = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        saleordercode = getIntent().getStringExtra("saleordercode");
        storeordercode = getIntent().getStringExtra("storeordercode");
        customerinfo = getIntent().getStringExtra("customerinfo");
        storeorderdate = getIntent().getStringExtra("storeorderdate");
        picurl = getIntent().getStringExtra("picurl");

        setContentView(R.layout.activity_salehistorydetail);
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
        tv_saleordercode = (TextView) findViewById(R.id.tv_saleordercode);
        tv_saleordercode.setText(saleordercode);
        tv_saleorderdate = (TextView) findViewById(R.id.tv_saleorderdate);
        tv_saleorderdate.setText(storeorderdate);
        tv_client = (TextView) findViewById(R.id.tv_client);
        tv_client.setText(customerinfo);
        tv_storeordercode = (TextView) findViewById(R.id.tv_storeordercode);

        tv_showpic = (TextView) findViewById(R.id.tv_showpic);
        tv_showpic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (picurl != null && !picurl.equals("")) {
                    Toast.makeText(SaleHistoryDetailActivity.this, "未设置物流图片", Toast.LENGTH_SHORT).show();
                } else {
                    ClickImage(picurl);
                }
            }
        });
        tv_remark = (TextView) findViewById(R.id.tv_remark);

        img_pic = (ImageView) findViewById(R.id.img_pic);
        if (picurl != null && !picurl.equals("")) {
            Glide.with(SaleHistoryDetailActivity.this).load(picurl)
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            Log.i("GlideException", "====" + e);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            Log.i("GlideReady", "======载入成功");
                            return false;
                        }
                    })
                    .placeholder(R.drawable.icon_defaultimg)
                    .error(R.drawable.icon_defaultimg)
                    //    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .into(img_pic);
        } else {
            img_pic.setImageResource(R.drawable.icon_defaultimg);
        }
        listview_saledetail = (ListView) findViewById(R.id.listview_saledetail);

    }

    private void initData() {
        Session.CheckRefreshToken(new RefreshTokenListener() {
            @Override
            public void RefreshTokenResult(final int resultcode) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (resultcode == Codes.Code_Success) {
                            SCSDK.getInstance().GetSaleStoreorder(saleordercode, storeordercode,Session.getInstance().getShopCode(), Session.getInstance().getToken(), new SCResponseListener() {
                                @Override
                                public void onResult(Object result) {
                                    final SCResult.SaleStoreOrderResult res = (SCResult.SaleStoreOrderResult) result;
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            datalist = res.storeordermodels;
                                            tv_remark.setText(res.logisticsremark);
                                            tv_storeordercode.setText(res.logisticscode);
                                            refreshListview();
                                        }
                                    });
                                }

                                @Override
                                public void onError(int code, final String errormsg) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(SaleHistoryDetailActivity.this, errormsg, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            });
                        } else {
                            Toast.makeText(SaleHistoryDetailActivity.this, SCExceptionCodeList.getExceptionMsg(resultcode), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    private AlertDialog alertDialog1;
    private void refreshListview() {
        if (saleHistoryDetailAdapter == null) {
            saleHistoryDetailAdapter = new SaleHistoryDetailAdapter(SaleHistoryDetailActivity.this, datalist, new ShowSaleSnsListener() {
                @Override
                public void ShowSaleSns(String modelcode) {
                    //modelcode是sns list
                    List<String> snslist = Arrays.asList(modelcode.split(","));
                    if(snslist.size()>0){
                        String[] items = new String[snslist.size()];
                        int index = 0;
                        for (String tempid : snslist) {
                            items[index] = tempid;
                            index++;
                        }
                        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(SaleHistoryDetailActivity.this);
                        alertBuilder.setTitle("出库序列号明细");
                        alertBuilder.setItems(items, null);
                        alertBuilder.setCancelable(true);
                        alertDialog1 = alertBuilder.create();
                        alertDialog1.show();
                    }
                    else{
                        Toast.makeText(SaleHistoryDetailActivity.this, "出库序列号为空！", Toast.LENGTH_SHORT).show();
                    }

                }
            });
            listview_saledetail.setAdapter(saleHistoryDetailAdapter);
        } else {
            saleHistoryDetailAdapter.setModellist(datalist);
            saleHistoryDetailAdapter.notifyDataSetChanged();
        }
    }

    private void ClickImage(String url) {
        WindowManager wm = this.getWindowManager();
        int width = wm.getDefaultDisplay().getWidth() / 2;
        final ImageView pic = new ImageView(SaleHistoryDetailActivity.this);
        pic.setAdjustViewBounds(true);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                width, LinearLayout.LayoutParams.WRAP_CONTENT);
        pic.setLayoutParams(layoutParams);
        pic.setMaxWidth(width);
        pic.setMaxHeight(width);
        Glide.with(SaleHistoryDetailActivity.this).load(url)
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
