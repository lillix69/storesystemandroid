package qinyuanliu.storesystemandroid.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.HashMap;

import qinyuanliu.storesystemandroid.R;
import qinyuanliu.storesystemandroid.common.Session;
import qinyuanliu.storesystemandroid.http.Codes;
import qinyuanliu.storesystemandroid.http.SCExceptionCodeList;
import qinyuanliu.storesystemandroid.http.SCResponseListener;
import qinyuanliu.storesystemandroid.http.SCResult;
import qinyuanliu.storesystemandroid.http.SCSDK;
import qinyuanliu.storesystemandroid.listener.RefreshTokenListener;

/**
 * Created by lillix on 6/28/18.
 */
public class ProductOutActivity extends BaseActivity {
    private Button btn_back;
    private TextView tv_pid;
    private TextView tv_mid;
    private EditText edt_remark;
    private TextView tv_reason;
    private TextView tv_out;
    private String[] reasonData;
    private HashMap<String, Integer> reasonnamecode = new HashMap<>();
    private AlertDialog reasonAlert;
    private TextView tv_storename;
    private TextView tv_rack;
    private TextView tv_location;
    private TextView tv_guige;
    private TextView tv_name;
    private ImageView img_pic;
    private TextView tv_project;
    private String[] projectData;
    private HashMap<String, String> projectnamecode = new HashMap<>();
    private AlertDialog projectAlert;
private RelativeLayout projectview;
//    private RelativeLayout projectmodelview;
 //   private TextView tv_projectmodel;
//    private String[] projectmodelData;
//    private HashMap<String, SCResult.DevelopInfo> projectmodelMap = new HashMap<>();
//    private AlertDialog projectmodelAlert;

    private String productid;
    private String modelid;
    private String typeid;
    private String modelname;
    private String guige;
    private String storename;
    private String rackname;
    private String locationname;
    private String picurl;
    private String remark = null;
    private int isneed;

    private int project = -1;
    private String projectcode = "";
    private String projectname = "";
    private int reason = -1;
    private String reasonstr = "";
//    private String projectmodelcode = "";
//    private String projectmodelname = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_productout);

        isneed = getIntent().getIntExtra("isneed", 0);
        projectcode = getIntent().getStringExtra("projectcode");
        projectname = getIntent().getStringExtra("projectname");

        productid = getIntent().getStringExtra("productid");
        modelid = getIntent().getStringExtra("modelid");
        typeid = getIntent().getStringExtra("typeid");
        modelname = getIntent().getStringExtra("modelname");
        storename = getIntent().getStringExtra("storename");
        rackname = getIntent().getStringExtra("rackname");
        locationname = getIntent().getStringExtra("locationname");
        picurl = getIntent().getStringExtra("picurl");
        guige = getIntent().getStringExtra("guige");
        initView();


    }

    private void initView() {
        btn_back = (Button) findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
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
        tv_out = (TextView) findViewById(R.id.tv_out);
        tv_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClickOut();
            }
        });
        tv_pid = (TextView) findViewById(R.id.tv_pid);
        tv_pid.setText(productid);
        tv_reason = (TextView) findViewById(R.id.tv_reason);
        tv_reason.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClickReason();
            }
        });
        img_pic = (ImageView) findViewById(R.id.img_pic);
        Glide.with(ProductOutActivity.this).load(picurl)
                .placeholder(R.drawable.icon_defaultimg)
                .error(R.drawable.icon_defaultimg)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(img_pic);
        tv_storename = (TextView) findViewById(R.id.tv_storename);
        tv_rack = (TextView) findViewById(R.id.tv_rack);
        tv_location = (TextView) findViewById(R.id.tv_location);
        tv_guige = (TextView) findViewById(R.id.tv_guige);
        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_name.setText(modelname);
        tv_storename.setText(storename);
        tv_rack.setText(rackname);
        tv_location.setText(locationname);
        tv_guige.setText(guige);

        projectview = (RelativeLayout) findViewById(R.id.projectview);
       // projectmodelview = (RelativeLayout)findViewById(R.id.projectmodelview);
        if (isneed == 0) {
            projectview.setVisibility(View.GONE);
          //  projectmodelview.setVisibility(View.GONE);
        } else {
            projectview.setVisibility(View.VISIBLE);
          //  projectmodelview.setVisibility(View.VISIBLE);
            tv_project = (TextView) findViewById(R.id.tv_project);
            tv_project.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ClickProject();
                }
            });
          //  tv_projectmodel = (TextView)findViewById(R.id.tv_projectmodel);
            if (!projectname.equals("")) {
                tv_project.setText(projectname);
            }

        }
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
                                                Toast.makeText(ProductOutActivity.this, errormsg, Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                });
                            } else {
                                Toast.makeText(ProductOutActivity.this, SCExceptionCodeList.getExceptionMsg(resultcode), Toast.LENGTH_SHORT).show();
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
            projectAlert = new AlertDialog.Builder(ProductOutActivity.this)
                    .setTitle("选择研发型号")
                    .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

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
                            tv_project.setText(projectname);
                        }
                    }).create();
        }
        projectAlert.show();
    }

    private void ClickReason() {
        if (reasonData == null) {
            if (typeid != null && !typeid.equals("")) {
                Session.CheckRefreshToken(new RefreshTokenListener() {
                    @Override
                    public void RefreshTokenResult(final int resultcode) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (resultcode == Codes.Code_Success) {
                                    SCSDK.getInstance().GetReason(Session.getInstance().getShopCode(), typeid, 0, Session.getInstance().getToken(), new SCResponseListener() {
                                        @Override
                                        public void onResult(Object result) {
                                            final SCResult.ReasonResult res = (SCResult.ReasonResult) result;
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    if (res.types != null && res.types.size() > 0) {
                                                        reasonData = new String[res.types.size()];
                                                        for (int i = 0; i < res.types.size(); i++) {
                                                            SCResult.Reason tempreason = res.types.get(i);
                                                            reasonData[i] = tempreason.name;
                                                            reasonnamecode.put(tempreason.name, tempreason.code);
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
                                                    Toast.makeText(ProductOutActivity.this, errormsg, Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                    });
                                } else {
                                    Toast.makeText(ProductOutActivity.this, SCExceptionCodeList.getExceptionMsg(resultcode), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
            } else {
                Toast.makeText(ProductOutActivity.this, "型号类型编码为空!", Toast.LENGTH_SHORT).show();
            }
        } else {
            ShowReasonAlert();
        }
    }

    private String tempreasonstr = "";
    private int tempreason = -1;
    private void ShowReasonAlert() {
        if (reasonAlert == null) {
            reasonAlert = new AlertDialog.Builder(ProductOutActivity.this)
                    .setTitle("选择出库原因")
                    .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
//                            if(isneed == 0){
//                                projectview.setVisibility(View.GONE);
//                            }
//                             else if(reason == 9) {
//                                projectview.setVisibility(View.GONE);
//                              //  projectmodelview.setVisibility(View.GONE);
//                            }
//                            else {
//                                projectview.setVisibility(View.VISIBLE);
                                projectcode = "";
                                projectname = "";

                              //  projectmodelview.setVisibility(View.VISIBLE);
 //                           }
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
                    .setSingleChoiceItems(reasonData, reason, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
//                            reasonstr = reasonData[which];
//                            reason = reasonnamecode.get(reasonstr);
                            tempreasonstr = reasonData[which];
                            tempreason = reasonnamecode.get(tempreasonstr);
                        }
                    }).create();
        }
        reasonAlert.show();
    }


    private void ClickOut() {
        if (reason < 0) {
            Toast.makeText(ProductOutActivity.this, "出库原因不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
//        if(isneed == 1 && reason != 9 && projectcode.equals("")){
//            Toast.makeText(ProductOutActivity.this, "请选择研发项目", Toast.LENGTH_SHORT).show();
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
                            SCSDK.getInstance().OutStore(Session.getInstance().getShopCode(), modelid, 1, reason, Session.getInstance().getToken(), productid, remark, projectcode, "",new SCResponseListener() {
                                @Override
                                public void onResult(Object result) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(ProductOutActivity.this, "出库成功", Toast.LENGTH_SHORT).show();
                                            Intent data = new Intent();
                                            setResult(RESULT_OK, data);
                                            finish();
                                        }
                                    });
                                }

                                @Override
                                public void onError(int code, final String errormsg) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(ProductOutActivity.this, errormsg, Toast.LENGTH_SHORT).show();

                                        }
                                    });
                                }
                            });

                        } else {
                            Toast.makeText(ProductOutActivity.this, SCExceptionCodeList.getExceptionMsg(resultcode), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
}
