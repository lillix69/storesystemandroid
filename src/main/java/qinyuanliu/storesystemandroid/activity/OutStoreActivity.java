package qinyuanliu.storesystemandroid.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.longsh.optionframelibrary.OptionBottomDialog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import qinyuanliu.storesystemandroid.R;
import qinyuanliu.storesystemandroid.adapter.ScanModelAdapter;
import qinyuanliu.storesystemandroid.common.Session;
import qinyuanliu.storesystemandroid.http.Codes;
import qinyuanliu.storesystemandroid.http.SCExceptionCodeList;
import qinyuanliu.storesystemandroid.http.SCResponseListener;
import qinyuanliu.storesystemandroid.http.SCResult;
import qinyuanliu.storesystemandroid.http.SCSDK;
import qinyuanliu.storesystemandroid.listener.ClickDeleteListener;
import qinyuanliu.storesystemandroid.listener.RefreshTokenListener;
import qinyuanliu.storesystemandroid.util.DensityUtil;

/**
 * Created by lillix on 5/28/18.
 */
public class OutStoreActivity extends BaseActivity {
    private Button btn_back;
    private TextView tv_id;
    private TextView tv_name;
    private TextView tv_storename;
    private TextView tv_rack;
    private TextView tv_location;
    private TextView tv_storecount;
    private TextView tv_category;
    private TextView tv_danwei;
    private TextView tv_guige;
    private TextView tv_out;
    private EditText edt_remark;
    private TextView tv_reason;
    private EditText edt_mincount;
    private GridView unmincount_gridview;
    private ImageView img_scan;
    private RelativeLayout unmin_countview;
    private TextView tv_unmincount;
    private RelativeLayout min_countview;
    private TextView tv_project;
    private String[] projectData;
    private HashMap<String, String> projectnamecode = new HashMap<>();
    private AlertDialog projectAlert;
    private RelativeLayout projectview;
//private RelativeLayout projectmodelview;
 //   private TextView tv_projectmodel;
//    private String[] projectmodelData;
//    private HashMap<String, SCResult.DevelopInfo> projectmodelMap = new HashMap<>();
//    private AlertDialog projectmodelAlert;

    private boolean parentflag;
    private String productid = "";
    private int outcount = 0;
    private boolean isMin = false;
    private String sns = null;
    private List<String> modellist = new ArrayList<>();
    private String remark = null;
    private int reason = -1;
    private String reasonstr = "";
    private String typecode = "";
    private AlertDialog reasonAlert;
    private HashMap<String, Integer> reasonnamecode = new HashMap<>();
    private String[] reasonData;
    private ScanModelAdapter scanmodelAdapter;
    final int SCANMODEL = 1000;

    private int project = -1;
    private String projectcode = "";
    private String projectname = "";
    private int isneed = 0;
//    private String projectmodelcode = "";
//    private String projectmodelname = "";

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) { //resultCode为回传的标记，我在B中回传的是RESULT_OK
            case SCANMODEL:
                if (resultCode == RESULT_OK) {
                    Bundle b = data.getExtras();
                    ArrayList<String> templist = b.getStringArrayList("modellist");
                    for (final String tempid : templist) {
//                        if (!modellist.contains(tempid)) {
//                            modellist.add(tempid);
//                            outcount++;
//                        }
                        SCSDK.getInstance().GetProductdetailByScan(Session.getInstance().getShopCode(),tempid, Session.getInstance().getToken(), new SCResponseListener() {
                            @Override
                            public void onResult(Object result) {
                                final SCResult.ProductDetailResult product = (SCResult.ProductDetailResult) result;
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (productid.equals(product.modelcode)) {
                                            if (!modellist.contains(tempid)) {
                                                modellist.add(tempid);
                                                outcount++;
                                                tv_unmincount.setText(Integer.toString(outcount));
                                            }
                                            LoadGridview();

                                        } else {
                                            Toast.makeText(OutStoreActivity.this, "条码【" + tempid + "】不是当前型号！", Toast.LENGTH_SHORT).show();

                                        }
                                    }
                                });
                            }

                            @Override
                            public void onError(final int code, final String errormsg) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(OutStoreActivity.this, errormsg, Toast.LENGTH_SHORT).show();

                                    }
                                });
                            }
                        });
                    }
//                    tv_unmincount.setText(Integer.toString(outcount));
//                    LoadGridview();
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_out);

        parentflag = getIntent().getBooleanExtra("isparentdetail",false);
        productid = getIntent().getStringExtra("productid");
        initView();
        initData();
    }

    private void initView() {
        btn_back = (Button) findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(parentflag) {
                    Intent data = new Intent();
                    setResult(RESULT_CANCELED, data);
                }
                finish();
            }
        });
        tv_id = (TextView) findViewById(R.id.tv_id);
        tv_id.setText(productid);
        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_storename = (TextView) findViewById(R.id.tv_storename);
        tv_rack = (TextView) findViewById(R.id.tv_rack);
        tv_location = (TextView) findViewById(R.id.tv_location);
        tv_storecount = (TextView) findViewById(R.id.tv_storecount);
        tv_category = (TextView) findViewById(R.id.tv_category);
        tv_danwei = (TextView) findViewById(R.id.tv_danwei);
        tv_guige = (TextView) findViewById(R.id.tv_guige);
        tv_out = (TextView) findViewById(R.id.tv_out);
        tv_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClickOut();
            }
        });
        tv_reason = (TextView) findViewById(R.id.tv_reason);
        tv_reason.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClickReason();
            }
        });
        edt_mincount = (EditText) findViewById(R.id.edt_mincount);
        edt_mincount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s)
            {
                String temp = s.toString();
                if(temp.equals("")){
                    outcount = 0;
                }else {
                    outcount = Integer.valueOf(temp);
                }
            }
        });
        unmincount_gridview = (GridView) findViewById(R.id.unmincount_gridview);
        tv_unmincount = (TextView)findViewById(R.id.tv_unmincount);
        img_scan = (ImageView) findViewById(R.id.img_scan);
        img_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                List<String> stringList = new ArrayList<String>();
                stringList.add("扫码");
                stringList.add("录入");
                final OptionBottomDialog optionBottomDialog = new OptionBottomDialog(OutStoreActivity.this, stringList);
                optionBottomDialog.setItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if(position == 0){
                            Intent scanintent = new Intent(OutStoreActivity.this, MipcaActivityCapture.class);
                            scanintent.putExtra("isSingle", false);
                            startActivityForResult(scanintent, SCANMODEL);
                        }
                        else if(position == 1){
                            final EditText et = new EditText(OutStoreActivity.this);
                            et.setFocusable(true);
                            et.setFocusableInTouchMode(true);
                            et.requestFocus();
                            AlertDialog.Builder alert = new AlertDialog.Builder(OutStoreActivity.this).setTitle("请输入单个编码")
                                    .setView(et)
                                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            final String input = et.getText().toString();
                                            if(input != null && !input.equals("")) {
//                                                //输入编码整理为modellist
//                                                List<String> templist = Arrays.asList(input.split(","));
//                                                for (String tempid : templist) {
//                                                    if (!modellist.contains(tempid)) {
//                                                        modellist.add(tempid);
//                                                        outcount++;
//                                                    }
//                                                }
//                                                tv_unmincount.setText(Integer.toString(outcount));
                                                SCSDK.getInstance().GetProductdetailByScan(Session.getInstance().getShopCode(), input, Session.getInstance().getToken(), new SCResponseListener() {
                                                    @Override
                                                    public void onResult(Object result) {
                                                        final SCResult.ProductDetailResult product = (SCResult.ProductDetailResult) result;
                                                        runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                if (productid.equals(product.modelcode)) {
                                                                    if (!modellist.contains(input)) {
                                                                        modellist.add(input);
                                                                        outcount++;
                                                                        tv_unmincount.setText(Integer.toString(outcount));
                                                                    }
                                                                    LoadGridview();

                                                                } else {
                                                                    Toast.makeText(OutStoreActivity.this, "条码【" + input + "】不是当前型号！", Toast.LENGTH_SHORT).show();

                                                                }
                                                            }
                                                        });
                                                    }

                                                    @Override
                                                    public void onError(final int code, final String errormsg) {
                                                        runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                Toast.makeText(OutStoreActivity.this, errormsg, Toast.LENGTH_SHORT).show();

                                                            }
                                                        });
                                                    }
                                                });
                                            }
                                            //刷新编码gridview
                                          //  LoadGridview();

                                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                            imm.hideSoftInputFromWindow(et.getWindowToken(), 0) ;
                                        }
                                    })
                                    .setNegativeButton("取消",  new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                            imm.hideSoftInputFromWindow(et.getWindowToken(), 0) ;
                                        }
                                    });
                            //.show();
                            AlertDialog tempDialog = alert.create();
                            tempDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                                @Override
                                public void onShow(DialogInterface dialog) {
                                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                    imm.showSoftInput(et, InputMethodManager.SHOW_IMPLICIT);
                                }
                            });
                            tempDialog.show();
                        }

                        optionBottomDialog.dismiss();
                    }
                });

            }
        });
        unmin_countview = (RelativeLayout) findViewById(R.id.unmin_countview);
        min_countview = (RelativeLayout) findViewById(R.id.min_countview);
        edt_remark = (EditText) findViewById(R.id.edt_remark);
        edt_remark.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                remark = s.toString();
            }
        });

        projectview = (RelativeLayout) findViewById(R.id.projectview);
        tv_project = (TextView) findViewById(R.id.tv_project);
tv_project.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        ClickProject();
    }
});
//        projectmodelview = (RelativeLayout) findViewById(R.id.projectmodelview);
//        tv_projectmodel = (TextView) findViewById(R.id.tv_projectmodel);
//        tv_projectmodel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ClickProjectModel();
//            }
//        });
    }

    private void initData() {
        Session.CheckRefreshToken(new RefreshTokenListener() {
            @Override
            public void RefreshTokenResult(final int resultcode) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (resultcode == Codes.Code_Success) {
                            SCSDK.getInstance().GetProductDetail(Session.getInstance().getShopCode(),productid, Session.getInstance().getToken(), new SCResponseListener() {
                                @Override
                                public void onResult(Object result) {
                                    final SCResult.ModelDetailResult res = (SCResult.ModelDetailResult) result;
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            productid = res.modelcode;
                                            tv_name.setText(res.modelname);
                                            tv_storename.setText(res.storename);
                                            tv_rack.setText(res.rackname);
                                            tv_location.setText(res.positionname);
                                            tv_storecount.setText(Integer.toString(res.store));
                                            tv_category.setText(res.typename);
                                            tv_danwei.setText(res.unit);
                                            tv_guige.setText(res.spec);
                                            typecode = res.typecode;
                                            if (res.ismin == 1) {
                                                isMin = true;
                                            }
                                            if (isMin) {

                                                min_countview.setVisibility(View.VISIBLE);
                                                unmin_countview.setVisibility(View.GONE);
                                            } else {

                                                min_countview.setVisibility(View.GONE);
                                                unmin_countview.setVisibility(View.VISIBLE);
                                            }
                                            SCSDK.getInstance().IsNeedProject(Session.getInstance().getShopCode(), Session.getInstance().getToken(), productid, new SCResponseListener() {
                                                @Override
                                                public void onResult(Object result) {
                                                    final SCResult.IsneedProjectResult resneed = (SCResult.IsneedProjectResult)result;
                                                    runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            //选择项目view
                                                            if(resneed.isneed == 0){
                                                                isneed = 0;
                                                                projectview.setVisibility(View.GONE);
                                                                projectcode = "";
                                                                projectname = "";
                                                               // projectmodelview.setVisibility(View.GONE);
//                                                                projectmodelcode = "";
//                                                                projectmodelname = "";
                                                            }
                                                            else{
                                                                isneed = 1;
                                                                projectview.setVisibility(View.VISIBLE);
                                                                projectcode = resneed.projectcode;
                                                                projectname = resneed.projectname != null ? resneed.projectname : "";
                                                                if(!projectname.equals("")) {
                                                                    tv_project.setText(projectname);
                                                                }
                                                               // projectmodelview.setVisibility(View.VISIBLE);
                                                            }
                                                        }
                                                    });
                                                }
                                                @Override
                                                public void onError(int code,final String errormsg) {
                                                    runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            Toast.makeText(OutStoreActivity.this, errormsg, Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                                }
                                            });

                                        }
                                    });
                                }

                                @Override
                                public void onError(int code, final String errormsg) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(OutStoreActivity.this, errormsg, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            });
                        } else {
                            Toast.makeText(OutStoreActivity.this, SCExceptionCodeList.getExceptionMsg(resultcode), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    private void LoadGridview() {
        if (scanmodelAdapter == null) {
            scanmodelAdapter = new ScanModelAdapter(OutStoreActivity.this, modellist, new ClickDeleteListener() {
                @Override
                public void DeleteModel(String modelid) {
                    if (modellist.contains(modelid)) {
                        modellist.remove(modelid);
                        outcount--;
                    }
                    tv_unmincount.setText(Integer.toString(outcount));
                    LoadGridview();
                }
            });
            unmincount_gridview.setAdapter(scanmodelAdapter);
        } else {
            scanmodelAdapter.setModellist(modellist);
            scanmodelAdapter.notifyDataSetChanged();
        }
        ViewGroup.LayoutParams params = unmincount_gridview.getLayoutParams();
        params.height = DensityUtil.dip2px(OutStoreActivity.this,25*modellist.size());
        unmincount_gridview.setLayoutParams(params);
    }



    private void ClickProject() {
        if (projectData == null) {
            Session.CheckRefreshToken(new RefreshTokenListener() {
                @Override
                public void RefreshTokenResult(final int resultcode) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (resultcode == Codes.Code_Success) {
                                SCSDK.getInstance().GetProject( Session.getInstance().getShopCode(), Session.getInstance().getToken(), new SCResponseListener() {
                                    @Override
                                    public void onResult(Object result) {
                                        final SCResult.ProjectResult res = (SCResult.ProjectResult) result;
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (res.projects != null && res.projects.size() > 0) {
                                                    projectData = new String[res.projects.size()];
                                                    for (int i = 0; i < res.projects.size(); i++) {
                                                        SCResult.ProjectInfo tempproject = res.projects.get(i);
                                                        projectData[i] = tempproject.projectname;
                                                        projectnamecode.put(tempproject.projectname, tempproject.projectcode);
                                                    }
                                                    ShowProjectAlert();
                                                }
                                            }
                                        });
                                    }

                                    @Override
                                    public void onError(int code, final String errormsg) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(OutStoreActivity.this, errormsg, Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                });
                            } else {
                                Toast.makeText(OutStoreActivity.this, SCExceptionCodeList.getExceptionMsg(resultcode), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            });
        } else {
            ShowProjectAlert();
        }
    }

    private void ShowProjectAlert() {
        if (projectAlert == null) {
            //找到默认project在projectdata里的位置，作为初始化选中项
            if(projectname != null && !projectname.equals("")){
                for(int i=0; i<projectData.length; i++){
                    if(projectData[i].equals(projectname)){
                        project = i;
                        break;
                    }
                }
            }
            // 创建projectalert
            projectAlert = new AlertDialog.Builder(OutStoreActivity.this)
                    .setTitle("选择研发项目")
                    .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            tv_project.setText(projectname);
                            projectAlert.dismiss();
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            projectAlert.dismiss();
                        }
                    })
                    .setSingleChoiceItems(projectData, project, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            projectname = projectData[which];
                           projectcode = projectnamecode.get(projectname);
                        }
                    }).create();
        }
        projectAlert.show();

    }

    private void ClickReason() {
        if(reasonData == null){
            if(typecode != null && !typecode.equals("")) {
                Session.CheckRefreshToken(new RefreshTokenListener() {
                    @Override
                    public void RefreshTokenResult(final int resultcode) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (resultcode == Codes.Code_Success) {
                                    SCSDK.getInstance().GetReason(Session.getInstance().getShopCode(),typecode, 0, Session.getInstance().getToken(), new SCResponseListener() {
                                        @Override
                                        public void onResult(Object result) {
                                            final SCResult.ReasonResult res = (SCResult.ReasonResult) result;
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    if (res.types != null && res.types.size() > 0) {
                                                        reasonData = new String[res.types.size()];
                                                        for(int i=0; i<res.types.size(); i++){
                                                            SCResult.Reason tempreason = res.types.get(i);
                                                            reasonData[i] = tempreason.name;
                                                            reasonnamecode.put(tempreason.name,tempreason.code);
                                                        }
                                                        ShowReasonAlert();
                                                    }
                                                }
                                            });
                                        }

                                        @Override
                                        public void onError(int code, final String errormsg) {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Toast.makeText(OutStoreActivity.this, errormsg, Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                    });
                                } else {
                                    Toast.makeText(OutStoreActivity.this, SCExceptionCodeList.getExceptionMsg(resultcode), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
            }
            else{
                Toast.makeText(OutStoreActivity.this, "型号类型编码为空!", Toast.LENGTH_SHORT).show();
            }
        }
        else{
            ShowReasonAlert();
        }
    }

    private String tempreasonstr = "";
    private int tempreason = -1;
    private void ShowReasonAlert() {
            if (reasonAlert == null) {
                reasonAlert = new AlertDialog.Builder(OutStoreActivity.this)
                        .setTitle("选择出库原因")
                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
//                                if(isneed == 0){
//                                    projectview.setVisibility(View.GONE);
//                                }
//                                else if( reason == 9) {
//                                    projectview.setVisibility(View.GONE);
//                                    projectcode = "";
  //                                  projectname = "";
 //                                   projectmodelview.setVisibility(View.GONE);
//                                    projectmodelcode = "";
//                                    projectmodelname = "";
//                                }
//                                else{
//                                    projectview.setVisibility(View.VISIBLE);
                                    projectcode = "";
                                    projectname = "";
                   //                 projectmodelview.setVisibility(View.VISIBLE);
//                                }
                               tv_project.setText(projectname);
//                                tv_projectmodel.setText(projectmodelname);
                                reason = tempreason;
                                reasonstr = tempreasonstr;
                                tv_reason.setText(reasonstr);
                                reasonAlert.dismiss();
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                reasonAlert.dismiss();
                            }
                        })
                        .setSingleChoiceItems(reasonData, -1, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
//                                reasonstr = reasonData[which];
//                                reason = reasonnamecode.get(reasonstr);
                                tempreasonstr = reasonData[which];
                                tempreason = reasonnamecode.get(tempreasonstr);

                            }
                        }).create();
            }
            reasonAlert.show();
    }

    private void ClickOut() {
        if (!checkOutData()) {
            return;
        }
//        if(isneed == 1 && reason != 9 && projectcode.equals("")){
//            Toast.makeText(OutStoreActivity.this, "请选择研发项目", Toast.LENGTH_SHORT).show();
//            return;
//        }
//        if(isneed == 1 && reason == 9){
//            projectcode = "";
//
//        }
        Session.CheckRefreshToken(new RefreshTokenListener() {
            @Override
            public void RefreshTokenResult(final int resultcode) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (resultcode == Codes.Code_Success) {
                            if (!isMin) {
                                sns = modellist.get(0);
                                for (int i = 1; i < modellist.size(); i++) {
                                    sns = sns + "," + modellist.get(i);
                                }
                            }

                            SCSDK.getInstance().OutStore(Session.getInstance().getShopCode(),productid, outcount, reason, Session.getInstance().getToken(), sns, remark,projectcode,"", new SCResponseListener() {
                                @Override
                                public void onResult(Object result) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(OutStoreActivity.this, "出库成功", Toast.LENGTH_SHORT).show();
                                            if(parentflag) {
                                                Intent data = new Intent();
                                                setResult(RESULT_OK, data);
                                            }
                                            finish();

                                        }
                                    });
                                }

                                @Override
                                public void onError(int code,final String errormsg) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(OutStoreActivity.this, errormsg, Toast.LENGTH_SHORT).show();

                                        }
                                    });
                                }
                            });

                        } else {
                            Toast.makeText(OutStoreActivity.this, SCExceptionCodeList.getExceptionMsg(resultcode), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    private boolean checkOutData() {
        if (outcount == 0) {
            Toast.makeText(OutStoreActivity.this, "出库数量不能为空", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (reason < 0) {
            Toast.makeText(OutStoreActivity.this, "出库原因不能为空", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
