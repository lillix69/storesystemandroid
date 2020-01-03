package qinyuanliu.storesystemandroid.activity;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import qinyuanliu.storesystemandroid.R;
import qinyuanliu.storesystemandroid.common.Session;
import qinyuanliu.storesystemandroid.http.Constants;
import qinyuanliu.storesystemandroid.http.SCSDK;

/**
 * Created by lillix on 7/4/18.
 */
public class ServerConfigActivity extends BaseActivity {
    private Button btn_back;
    private Button btn_config;
    private EditText edt_server;
    private EditText edt_port;
    private EditText edt_shopcode;
    private String server = "";
    private String port = "";
    private String shopcode= "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_serverconfig);

       // SharedPreferences sp = getSharedPreferences("storesystemdemo", MODE_PRIVATE);
        server = Constants.SERVER_IP;
        port = Integer.toString(Constants.API_SERVER_PORT);
        initView();
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
        btn_config = (Button) findViewById(R.id.btn_config);
        btn_config.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClickConfig();
            }
        });
        edt_server = (EditText) findViewById(R.id.edt_server);
        edt_server.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                server = s.toString();
            }
        });
        edt_server.setText(server);
        edt_server.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        edt_port = (EditText) findViewById(R.id.edt_port);
        edt_port.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                port = s.toString();
            }
        });
        edt_port.setText(port);
        shopcode = Session.getInstance().getShopCode();
        edt_shopcode = (EditText) findViewById(R.id.edt_shopcode);
        edt_shopcode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                shopcode = s.toString();
            }
        });
        edt_shopcode.setText(shopcode);
    }

    private void ClickConfig(){
        if(!server.equals("") && !port.equals("") && !shopcode.equals("")){
            Session.getInstance().setShopCode(shopcode);
            SharedPreferences sp = getSharedPreferences("storesystemdemo", MODE_PRIVATE);
            SharedPreferences.Editor ed = sp.edit();
            ed.putString("shopcode",shopcode);
            ed.putString("serverip", server);
            ed.putInt("serverport",Integer.valueOf(port));
            ed.commit();

            SCSDK.getInstance().ServerConfig(server,Integer.valueOf(port));
            Toast.makeText(ServerConfigActivity.this, "修改完成", Toast.LENGTH_SHORT).show();
            finish();
        }
        else{
            Toast.makeText(ServerConfigActivity.this, "操作失败!输入内容不能为空", Toast.LENGTH_SHORT).show();
        }
    }
}
