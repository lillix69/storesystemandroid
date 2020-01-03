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
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import qinyuanliu.storesystemandroid.R;
import qinyuanliu.storesystemandroid.http.Constants;

/**
 * Created by qinyuanliu on 2019/3/5.
 */

public class LockSettingActivity extends BaseActivity {
    private Button btn_back;
    private Button btn_config;
    private EditText edt_newlock;
    private TextView tv_oldlock;
private String newlock = "";
    private String oldlock;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locksetting);

        sp = getSharedPreferences("storesystemdemo", MODE_PRIVATE);
       oldlock = sp.getString("lockCode","8888");
        initView();
    }
    private void initView() {
        btn_back = (Button) findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
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
        edt_newlock = (EditText) findViewById(R.id.edt_newlock);
        edt_newlock.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                newlock = s.toString();
            }
        });
        edt_newlock.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        tv_oldlock = (TextView)findViewById(R.id.tv_oldlock);
        tv_oldlock.setText(oldlock);
    }

    private void ClickConfig(){
        if(newlock.equals("")){
            Toast.makeText(LockSettingActivity.this, "新密码不能为空！", Toast.LENGTH_SHORT).show();
        }
        else{
            SharedPreferences.Editor ed = sp.edit();
            ed.putString("lockCode",newlock);
            ed.commit();
            Toast.makeText(LockSettingActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
