package qinyuanliu.storesystemandroid.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import qinyuanliu.storesystemandroid.R;
import qinyuanliu.storesystemandroid.adapter.BatchSnsAdapter;
import qinyuanliu.storesystemandroid.listener.SelectBatchSnsListener;
import qinyuanliu.storesystemandroid.model.BatchSnsModel;

/**
 * Created by qinyuanliu on 2019/7/21.
 */

public class EditBatchStoreActivity extends BaseActivity {
    private Button btn_back;
private TextView tv_delete;
    private ListView batch_listview;
    private Button btn_all;
private TextView tv_all;
    private ArrayList<BatchSnsModel> snslist = new ArrayList<>();
    private BatchSnsAdapter batchsnsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editbatchstore);

      String snsstr = getIntent().getStringExtra("snsstr");
        if(snsstr != null && !snsstr.equals("")) {
            List<String> templist = Arrays.asList(snsstr.split(","));
            for (String sns: templist) {
                snslist.add(new BatchSnsModel(sns));
            }
        }
        initView();
        RefreshListview();
    }

    private void initView() {
        btn_back = (Button) findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tv_delete = (TextView)findViewById(R.id.tv_delete);
        tv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClickDelete();
            }
        });
        batch_listview = (ListView)findViewById(R.id.batch_listview);
        btn_all = (Button)findViewById(R.id.btn_all);
        btn_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClickAll();
            }
        });
        tv_all = (TextView)findViewById(R.id.tv_all);
        tv_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClickAll();
            }
        });
    }

    private void RefreshListview(){
        if(batchsnsAdapter == null){
            batchsnsAdapter = new BatchSnsAdapter(EditBatchStoreActivity.this, snslist, new SelectBatchSnsListener() {
                @Override
                public void ClickBatchSns(String sns, boolean isselected) {
                    for (int i =0 ;i<snslist.size(); i++) {
                        if(snslist.get(i).sns.equals(sns)){
                            snslist.get(i).isSelected = isselected;
                            if(!isselected){
                                btn_all.setSelected(false);
                            }
                            break;
                        }
                    }
                    RefreshListview();
                }
            });
            batch_listview.setAdapter(batchsnsAdapter);
        }
        else{
            batchsnsAdapter.setModellist(snslist);
            batchsnsAdapter.notifyDataSetChanged();
        }
    }

    private void ClickDelete(){
        ArrayList<String> deletesns = new ArrayList<>();
        for (int i =0 ;i<snslist.size(); i++) {
            if(snslist.get(i).isSelected) {
                deletesns.add(snslist.get(i).sns);
            }
        }

        //将要删除的sns返回，用逗号隔开
        Intent data = new Intent();
        data.putStringArrayListExtra("deletesns", deletesns);
        setResult(RESULT_OK, data);
        Toast.makeText(EditBatchStoreActivity.this, "操作成功！", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void ClickAll(){
        btn_all.setSelected(true);
        for (int i =0 ;i<snslist.size(); i++) {
               snslist.get(i).isSelected = true;
        }
        RefreshListview();
    }
}
