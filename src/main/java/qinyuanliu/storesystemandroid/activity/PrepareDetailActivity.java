package qinyuanliu.storesystemandroid.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import qinyuanliu.storesystemandroid.R;
import qinyuanliu.storesystemandroid.adapter.ShowPrepareDetailAdapter;
import qinyuanliu.storesystemandroid.common.Session;
import qinyuanliu.storesystemandroid.http.SCResult;
import qinyuanliu.storesystemandroid.listener.ShowPrepareSnsListener;
import qinyuanliu.storesystemandroid.model.PrepareDetailModel;

/**
 * Created by qinyuanliu on 2018/12/6.
 */

public class PrepareDetailActivity extends BaseActivity {
    private Button btn_back;
    private TextView tv_modelname;
    private TextView tv_modelspec;
    private TextView tv_count;
    private TextView tv_buzhou;
    private ListView listview_prepare;
    private ShowPrepareDetailAdapter prepareDetailAdapter;
    ArrayList<PrepareDetailModel> datalist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preparedetail);

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
        tv_modelname = (TextView)findViewById(R.id.tv_modelname);
        tv_modelspec = (TextView)findViewById(R.id.tv_modelspec);
        tv_count = (TextView)findViewById(R.id.tv_count);
        tv_buzhou = (TextView)findViewById(R.id.tv_buzhou);
        listview_prepare = (ListView)findViewById(R.id.listview_prepare);
    }

    private void initData(){
        SCResult.SubmitPrepareResult result = Session.getInstance().getSubmitprepare();
        tv_modelname.setText(result.modelname);
        tv_modelspec.setText(result.spec);
        tv_count.setText(Integer.toString(result.count));
        tv_buzhou.setText(result.stepname);
        datalist = new ArrayList<>();
        if(result.details != null && result.details.size()>0){
            for (SCResult.SubmitPrepare prepare: result.details) {
                int snscount = 0;
                String snsdetail = "";
                if(prepare.sns != null && prepare.sns.size()>0){
                    snscount = prepare.sns.size();
                    snsdetail = prepare.sns.get(0);
                    for(int i=1; i<prepare.sns.size(); i++){
                        snsdetail = snsdetail + "\n" + prepare.sns.get(i);
                    }
                }
                boolean isSN = prepare.issn == 1;
                PrepareDetailModel newdetail = new PrepareDetailModel(prepare.modelname,prepare.modelcode,prepare.modelspec,Integer.toString(prepare.count),snsdetail,snscount,isSN);
                datalist.add(newdetail);
            }
        }

       refreshListview();
    }

    private void refreshListview() {
        if (prepareDetailAdapter == null) {
            prepareDetailAdapter = new ShowPrepareDetailAdapter(PrepareDetailActivity.this, datalist, new ShowPrepareSnsListener() {
                @Override
                public void ShowPrepareSns(String modelcode, boolean isShow) {
                    for(PrepareDetailModel preparedetail:datalist) {
                        if(preparedetail.getModelcode().equals(modelcode)) {
                           preparedetail.setIsShow(isShow);
                            prepareDetailAdapter.setModellist(datalist);
                            prepareDetailAdapter.notifyDataSetChanged();
                            break;
                        }
                    }
                }

            });
            listview_prepare.setAdapter(prepareDetailAdapter);
        } else {
            prepareDetailAdapter.setModellist(datalist);
            prepareDetailAdapter.notifyDataSetChanged();
        }
    }
}
