package qinyuanliu.storesystemandroid.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import qinyuanliu.storesystemandroid.R;

/**
 * Created by qinyuanliu on 2019/2/25.
 */

public class ScanErrorActivity extends BaseActivity{
    private Button btn_back;
private TextView tv_scanstr;
    private TextView tv_error;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanerror);

String scanstr = getIntent().getStringExtra("ScanString");
        String errorstr = getIntent().getStringExtra("ErrorString");
        btn_back = (Button) findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

            }
        });
        tv_scanstr = (TextView)findViewById(R.id.tv_scanstr);
        tv_scanstr.setText(scanstr);
        tv_error = (TextView)findViewById(R.id.tv_error);
        tv_error.setText(errorstr);
    }
}
