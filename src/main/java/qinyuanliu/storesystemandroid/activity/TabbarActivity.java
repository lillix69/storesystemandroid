package qinyuanliu.storesystemandroid.activity;

import android.accounts.Account;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import qinyuanliu.storesystemandroid.R;
import qinyuanliu.storesystemandroid.common.Session;
import qinyuanliu.storesystemandroid.http.SCResponseListener;
import qinyuanliu.storesystemandroid.http.SCResult;
import qinyuanliu.storesystemandroid.http.SCSDK;


/**
 * Created by qinyuanliu on 2019/4/13.
 */

public class TabbarActivity extends BaseActivity implements View.OnClickListener {
    private FragmentMain fragmentMain;
    private FragmentPurchase fragmentPurchase;
    private FragmentSale fragmentSale;
    private TextView mainBtn, purchaseBtn, saleBtn;
private RelativeLayout saleview, mainview, purchaseview;
    private ImageView img_sale, img_pur, img_main;

    private long mExitTime = 0;
    private int currentindex = 1;

    //    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK)
//            return true;//不执行父类点击事件
//        return super.onKeyDown(keyCode, event);//继续执行父类其他点击事件
//    }
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            exit();
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    private void exit() {
        if ((System.currentTimeMillis() - mExitTime) > 2000) {
            Toast.makeText(TabbarActivity.this, "再按一次应用将进入后台", Toast.LENGTH_SHORT).show();
            mExitTime = System.currentTimeMillis();
        } else {

           // moveTaskToBack(false);
            Session.getInstance().setIsbackrun(true);
            Intent intent = new Intent();
intent.setAction("android.intent.action.MAIN");
intent.addCategory("android.intent.category.HOME");
startActivity(intent);

        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabbar);
        initView();
        ClickMainBtn();


    }

    @Override
    public void onResume() {
        super.onResume();
        Session.getInstance().setContext(TabbarActivity.this);
    }

    private void initView() {
        mainBtn = (TextView) findViewById(R.id.layout_main);
        purchaseBtn = (TextView) findViewById(R.id.layout_purchase);
        saleBtn = (TextView) findViewById(R.id.layout_sale);
        mainview = (RelativeLayout)findViewById(R.id.mainview);
        purchaseview = (RelativeLayout)findViewById(R.id.purchaseview);
        saleview = (RelativeLayout)findViewById(R.id.saleview);
        img_main = (ImageView)findViewById(R.id.img_main);
        img_pur = (ImageView)findViewById(R.id.img_pur);
        img_sale = (ImageView)findViewById(R.id.img_sale);
        mainview.setOnClickListener(this);
        purchaseview.setOnClickListener(this);
        saleview.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.mainview:
                if (currentindex != 1) {
                    currentindex = 1;
                    ClickMainBtn();
                }

                break;
            case R.id.purchaseview:
                if (currentindex != 2) {
                    currentindex = 2;
                    ClickPurchaseBtn();
                }
                break;
            case R.id.saleview:
                if (currentindex != 3) {
                    currentindex = 3;
                    ClickSaleBtn();
                }
                break;
            default:
                System.out.print("============================none");
                break;
        }
    }

    private void ClickMainBtn() {
        fragmentMain = FragmentMain.newInstance();

        FragmentTransaction fragmentTransaction = this
                .getSupportFragmentManager().beginTransaction();

        fragmentTransaction.replace(R.id.frame_content, fragmentMain);
        fragmentTransaction.commit();

        mainBtn.setSelected(true);
        purchaseBtn.setSelected(false);
        saleBtn.setSelected(false);
        mainview.setSelected(true);
        purchaseview.setSelected(false);
saleview.setSelected(false);
        img_main.setSelected(true);
        img_pur.setSelected(false);
        img_sale.setSelected(false);
    }

    private void ClickPurchaseBtn() {
        fragmentPurchase = FragmentPurchase.newInstance();

        FragmentTransaction fragmentTransaction = this
                .getSupportFragmentManager().beginTransaction();

        fragmentTransaction.replace(R.id.frame_content, fragmentPurchase);
        fragmentTransaction.commit();

        mainBtn.setSelected(false);
        purchaseBtn.setSelected(true);
        saleBtn.setSelected(false);
        mainview.setSelected(false);
        purchaseview.setSelected(true);
        saleview.setSelected(false);
        img_main.setSelected(false);
        img_pur.setSelected(true);
        img_sale.setSelected(false);
    }

    private void ClickSaleBtn() {
        fragmentSale = FragmentSale.newInstance();

        FragmentTransaction fragmentTransaction = this
                .getSupportFragmentManager().beginTransaction();

        fragmentTransaction.replace(R.id.frame_content, fragmentSale);
        fragmentTransaction.commit();

        mainBtn.setSelected(false);
        purchaseBtn.setSelected(false);
        saleBtn.setSelected(true);
        mainview.setSelected(false);
        purchaseview.setSelected(false);
        saleview.setSelected(true);
        img_main.setSelected(false);
        img_pur.setSelected(false);
        img_sale.setSelected(true);
    }
}
