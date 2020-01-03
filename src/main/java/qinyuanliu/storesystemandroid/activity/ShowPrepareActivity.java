package qinyuanliu.storesystemandroid.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import qinyuanliu.storesystemandroid.R;
import qinyuanliu.storesystemandroid.adapter.SubmitPrepareAdapter;
import qinyuanliu.storesystemandroid.common.Session;
import qinyuanliu.storesystemandroid.http.Codes;
import qinyuanliu.storesystemandroid.http.SCExceptionCodeList;
import qinyuanliu.storesystemandroid.http.SCResponseListener;
import qinyuanliu.storesystemandroid.http.SCResult;
import qinyuanliu.storesystemandroid.http.SCSDK;
import qinyuanliu.storesystemandroid.listener.ChangePrepareListener;
import qinyuanliu.storesystemandroid.listener.RefreshTokenListener;
import qinyuanliu.storesystemandroid.listener.SelectPrepareListener;
import qinyuanliu.storesystemandroid.listener.ShowPrepareSnsListener;
import qinyuanliu.storesystemandroid.model.SubmitPrepareModel;

/**
 * Created by qinyuanliu on 2018/12/6.
 */

public class ShowPrepareActivity extends BaseActivity{
    private Button btn_back;
    private TextView tv_modelname;
    private TextView tv_modelspec;
    private TextView tv_count;
    private TextView tv_buzhou;
    private ListView listview_prepare;
    private Button btn_out;
    private Button btn_scan;
    private SubmitPrepareAdapter prepareAdapter;
    ArrayList<SubmitPrepareModel> datalist;

    private String modelcode;
    private String stepcode;
    private int count;
    private ArrayList<String> modelcodeList = new ArrayList<>();

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) { //resultCode为回传的标记，我在B中回传的是RESULT_OK
            case 2000:
                if (resultCode == RESULT_OK) {
                    for (SubmitPrepareModel prepare : datalist) {
                        if (prepare.getIsSN()) {
                            //将原有的sns与session里的同步
                            HashSet<String> newsns = Session.getInstance().getScansnsMap().get(prepare.getModelcode());
                            if (newsns != null) {
                                String tempsnsstr = "";
                                int tempsnscount = 1;
                                for (String tempsns : newsns) {
                                    if (tempsnscount > 1) {
                                        tempsnsstr = tempsnsstr + "\n";
                                    }
                                    tempsnsstr = tempsnsstr + tempsns;
                                    tempsnscount++;
                                }
                                prepare.setSnslist(newsns);
                                prepare.setSnscount(newsns.size());
                                prepare.setSns(tempsnsstr);
                            }
                        }
                    }
                    refreshListview();
                }
                break;
            default:
                break;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showprepare);

        modelcode = getIntent().getStringExtra("prepare_modelcode");
        stepcode = getIntent().getStringExtra("prepare_stepcode");
        count = getIntent().getIntExtra("prepare_count", 0);
        Session.getInstance().getScansnsMap().clear();
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
        tv_modelname = (TextView) findViewById(R.id.tv_modelname);
        tv_modelspec = (TextView) findViewById(R.id.tv_modelspec);
        tv_count = (TextView) findViewById(R.id.tv_count);
        tv_buzhou = (TextView) findViewById(R.id.tv_buzhou);
        btn_out = (Button) findViewById(R.id.btn_out);
        btn_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClickOut();
            }
        });
        btn_scan = (Button) findViewById(R.id.btn_scan);
        btn_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent scanintent = new Intent(ShowPrepareActivity.this, ScanPrepareActivity.class);
                scanintent.putStringArrayListExtra("modelcodelist", modelcodeList);
                startActivityForResult(scanintent, 2000);
            }
        });
        listview_prepare = (ListView) findViewById(R.id.listview_prepare);
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
                            SCSDK.getInstance().CaculatePrepare(Session.getInstance().getShopCode(), Session.getInstance().getToken(), modelcode, count, stepcode, new SCResponseListener() {
                                @Override
                                public void onResult(Object result) {
                                    final SCResult.PrepareResult res = (SCResult.PrepareResult) result;
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (res != null) {
                                                tv_count.setText(Integer.toString(res.count));
                                                tv_modelname.setText(res.modelname);
                                                tv_modelspec.setText(res.spec);
                                                tv_buzhou.setText(res.stepname);
                                                if (res.details != null && res.details.size() > 0) {
                                                    for (SCResult.PrepareInfo prepare :
                                                            res.details) {
                                                        boolean isSN = prepare.issn == 1;
                                                        SubmitPrepareModel newprepare = new SubmitPrepareModel(prepare.modelname, prepare.modelcode, prepare.modelspec, isSN, prepare.position, prepare.count);
                                                        datalist.add(newprepare);
                                                        modelcodeList.add(prepare.modelcode);
                                                    }
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
                                            Toast.makeText(ShowPrepareActivity.this, errormsg, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            });
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    refreshListview();
                                    Toast.makeText(ShowPrepareActivity.this, SCExceptionCodeList.getExceptionMsg(resultcode), Toast.LENGTH_SHORT).show();

                                }
                            });
                        }
                    }
                });
            }
        });
    }

    private int needcount = 0;
    private void refreshListview() {
        if (prepareAdapter == null) {
            prepareAdapter = new SubmitPrepareAdapter(ShowPrepareActivity.this, datalist, new ShowPrepareSnsListener() {
                @Override
                public void ShowPrepareSns(String modelcode, boolean isShow) {
                    for (SubmitPrepareModel preparemodel : datalist) {
                        if (preparemodel.getModelcode().equals(modelcode)) {
                            preparemodel.setIsShow(isShow);
                            prepareAdapter.setModellist(datalist);
                            prepareAdapter.notifyDataSetChanged();
                            break;
                        }
                    }

                }

            }, new SelectPrepareListener() {
                @Override
                public void SelectPrepare(String modelcode, boolean isSelected) {
                    for (SubmitPrepareModel preparemodel : datalist) {
                        if (preparemodel.getModelcode().equals(modelcode)) {
                            preparemodel.setIsChoose(isSelected);
                            prepareAdapter.setModellist(datalist);
                            prepareAdapter.notifyDataSetChanged();
                            break;
                        }
                    }
                }
            }, new ChangePrepareListener() {
                @Override
                public void ChangeNeedcount(final String modelcode) {

                    LayoutInflater inflater = LayoutInflater.from(ShowPrepareActivity.this);// 渲染器
                    View customdialog2view = inflater.inflate(R.layout.popup_needcount,
                            null);
                    EditText prepareEdt = (EditText) customdialog2view.findViewById(R.id.edt_needcount);
                    prepareEdt.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {

                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            String temp = s.toString();
                            if (temp.length() > 0) {
                                needcount = Integer.valueOf(temp);
                            } else {
                                needcount = 0;
                            }
                        }
                    });
                    AlertDialog.Builder builder = new AlertDialog.Builder(ShowPrepareActivity.this);
                    builder.setTitle("修改配料数量");
                    builder.setView(customdialog2view);
                    builder.setNegativeButton("取消",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (needcount == 0) {
                                Toast.makeText(ShowPrepareActivity.this, "数量不能为0！", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            for (SubmitPrepareModel preparemodel : datalist) {
                                if (preparemodel.getModelcode().equals(modelcode)) {
                                    preparemodel.setNeedcount(needcount);
                                    prepareAdapter.setModellist(datalist);
                                    prepareAdapter.notifyDataSetChanged();
                                    break;
                                }
                            }
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            });
            listview_prepare.setAdapter(prepareAdapter);
        } else {
            prepareAdapter.setModellist(datalist);
            prepareAdapter.notifyDataSetChanged();
        }
    }

    private String detailstr = "";

    private void ClickOut() {
        //验证select的项是否存在数量为0
        boolean isselected = false;
        for (SubmitPrepareModel submit : datalist) {
            if (submit.getIsChoose()) {
                if (submit.getIsSN()) {
                    if (submit.getSnscount() == 0) {
                        Toast.makeText(ShowPrepareActivity.this, submit.getModelname() + "出库条形码为空！", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } else {
                    if (submit.getNeedcount() == 0) {
                        Toast.makeText(ShowPrepareActivity.this, submit.getModelname() + "出库数量为空！", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                isselected = true;
            }
        }
        if (!isselected) {
            Toast.makeText(ShowPrepareActivity.this, "请选择要出库的物品！", Toast.LENGTH_SHORT).show();
            return;
        }
        //提交api
        //出库物料明细，格式 用料型号编码-数量-序列号集合, 用料型号编码-数量-序列号集合,例如:
        // 000001-2-{8212121,9131231}；000002-2-{8212121,9131231}；000003-2
        detailstr = "";
        int submitcount = 0;
        for (SubmitPrepareModel submit : datalist) {
            if (submit.getIsChoose()) {
                submitcount++;
                if (submit.getIsSN()) {
                    if (submitcount > 1) {
                        detailstr = detailstr + ";";
                    }
                    String snsdetailstr = "";
                    int snsindex = 1;
                    for (String sns : submit.getSnslist()) {
                        if (snsindex > 1) {
                            snsdetailstr = snsdetailstr + ",";
                        }
                        snsdetailstr = snsdetailstr + sns;
                        snsindex++;
                    }
                    detailstr = detailstr + submit.getModelcode() + "-" + Integer.toString(submit.getSnslist().size()) + "-" + "{" + snsdetailstr + "}";
                } else {
                    if (submitcount > 1) {
                        detailstr = detailstr + ";";
                    }
                    detailstr = detailstr + submit.getModelcode() + "-" + Integer.toString(submit.getNeedcount());
                }

            }
        }

        Session.CheckRefreshToken(new RefreshTokenListener() {
            @Override
            public void RefreshTokenResult(final int resultcode) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (resultcode == Codes.Code_Success) {
                            SCSDK.getInstance().SubmitPrepare(Session.getInstance().getShopCode(), Session.getInstance().getToken(), modelcode, count, stepcode, detailstr, new SCResponseListener() {
                                @Override
                                public void onResult(Object result) {
                                    final SCResult.SubmitPrepareResult res = (SCResult.SubmitPrepareResult) result;
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(ShowPrepareActivity.this, "出库成功！", Toast.LENGTH_SHORT).show();
                                            Session.getInstance().setSubmitprepare(res);
                                            startActivity(new Intent(ShowPrepareActivity.this, PrepareDetailActivity.class));
                                            finish();
                                        }
                                    });
                                }

                                @Override
                                public void onError(int code, final String errormsg) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(ShowPrepareActivity.this, errormsg, Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                }
                            });
                        } else {
                            Toast.makeText(ShowPrepareActivity.this, SCExceptionCodeList.getExceptionMsg(resultcode), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

    }
}
