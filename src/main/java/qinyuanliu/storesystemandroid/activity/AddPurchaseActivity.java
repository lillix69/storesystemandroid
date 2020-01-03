package qinyuanliu.storesystemandroid.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;


import qinyuanliu.storesystemandroid.R;
import qinyuanliu.storesystemandroid.adapter.AddPurchaseModelAdapter;
import qinyuanliu.storesystemandroid.adapter.SaleSnsAdapter;
import qinyuanliu.storesystemandroid.common.Session;
import qinyuanliu.storesystemandroid.http.Codes;
import qinyuanliu.storesystemandroid.http.SCExceptionCodeList;
import qinyuanliu.storesystemandroid.http.SCResponseListener;
import qinyuanliu.storesystemandroid.http.SCResult;
import qinyuanliu.storesystemandroid.http.SCSDK;
import qinyuanliu.storesystemandroid.http.util.Json;
import qinyuanliu.storesystemandroid.listener.AddModelScanListener;
import qinyuanliu.storesystemandroid.listener.DeleteSnsListener;
import qinyuanliu.storesystemandroid.listener.EditCostListener;
import qinyuanliu.storesystemandroid.listener.EditCountListener;
import qinyuanliu.storesystemandroid.listener.RefreshTokenListener;
import qinyuanliu.storesystemandroid.listener.RemoveAddPurListener;
import qinyuanliu.storesystemandroid.listener.SelectModelListener;
import qinyuanliu.storesystemandroid.listener.SelectSupplyListener;
import qinyuanliu.storesystemandroid.listener.ShowAddPurSnsListener;
import qinyuanliu.storesystemandroid.model.AddPurchaseModel;
import qinyuanliu.storesystemandroid.model.FahuoModel;
import qinyuanliu.storesystemandroid.model.NoticeModel;
import qinyuanliu.storesystemandroid.util.DateUtil;
import qinyuanliu.storesystemandroid.util.TextUtil;

/**
 * Created by qinyuanliu on 2019/4/21.
 */

public class AddPurchaseActivity extends BaseActivity {
    private Button btn_back;
    private TextView tv_date;
    private TextView tv_name;
    private TextView tv_addmodel;
    private TextView tv_addorder;
    private ListView listview_models;
    private TextView tv_confirm;

    final int supplyrequest = 1000;
    final int modelrequest = 1001;
    final int chooseorderrequest = 1002;
    final int SELECTMODEL = 1004;
    final int SCANMODEL = 1005;
    private AddPurchaseModelAdapter addPurchaseModelAdapter;
    private ArrayList<AddPurchaseModel> addmodelList = new ArrayList<>();
    int currenteditindex = 0;

    private SaleSnsAdapter adapter;
    private String instoredate = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addpurchase);

        initView();
        RefreshListview();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) { //resultCode为回传的标记，我在B中回传的是RESULT_OK
            case SELECTMODEL:
                if (resultCode == RESULT_OK) {
                    Bundle b = data.getExtras();
                    final String code = b.getString("modelcode");
                    final String name = b.getString("modelname");
                    Session.CheckRefreshToken(new RefreshTokenListener() {
                        @Override
                        public void RefreshTokenResult(final int resultcode) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (resultcode == Codes.Code_Success) {
                                        SCSDK.getInstance().GetProductDetail(Session.getInstance().getShopCode(), code, Session.getInstance().getToken(), new SCResponseListener() {
                                            @Override
                                            public void onResult(Object result) {
                                                final SCResult.ModelDetailResult res = (SCResult.ModelDetailResult) result;
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        boolean ismin = false;
                                                        if (res.ismin == 1) {
                                                            ismin = true;
                                                        } else {
                                                            addmodelList.get(currenteditindex).setSn(new ArrayList<String>());
                                                        }
                                                        addmodelList.get(currenteditindex).setModelCode(code);
                                                        addmodelList.get(currenteditindex).setModelName(name);
                                                        addmodelList.get(currenteditindex).setIsMin(ismin);
                                                        addmodelList.get(currenteditindex).setStoreCount("");
                                                        RefreshListview();
                                                    }
                                                });
                                            }

                                            @Override
                                            public void onError(int code, final String errormsg) {
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Toast.makeText(AddPurchaseActivity.this, errormsg, Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }
                                        });
                                    } else {
                                        Toast.makeText(AddPurchaseActivity.this, SCExceptionCodeList.getExceptionMsg(resultcode), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    });


                }
                break;
            case supplyrequest:
                if (resultCode == RESULT_OK) {
                    Bundle b = data.getExtras();
                    String code = b.getString("supplycode");
                    String name = b.getString("supplyname");
                    addmodelList.get(currenteditindex).setSupply(name);
                    addmodelList.get(currenteditindex).setSupplyCode(code);
                    RefreshListview();
                }
                break;
            case chooseorderrequest:
                if (resultCode == RESULT_OK) {
                    Bundle b = data.getExtras();
                    String result = b.getString("modellist");
                    SCResult.PurchaseModelResult res = Json.decode(result, SCResult.PurchaseModelResult.class);
                    String ordercode = res.ordercode;
                    ArrayList<SCResult.PurchaseModel> models = res.models;
                    if (models != null && models.size() > 0) {
                        for (SCResult.PurchaseModel model : models) {
                            ArrayList<String> sns = new ArrayList<>();
//                            if (!model.sns.equals("")) {
//                                List<String> templist = Arrays.asList(model.sns.split(","));
//                                for (String tempsn : templist) {
//                                    sns.add(tempsn);
//                                }
//                            }
                            boolean ismin = model.ismin == 0 ? false : true;
                            String modelcount = model.ismin == 0 ? "0" : Integer.toString(model.count);
                            AddPurchaseModel newmodel = new AddPurchaseModel(model.materielname, model.lineno, model.modelcode, model.modelname, ismin, model.materielspec, ordercode, model.supplier, model.price, sns, modelcount, model.suppliercode);
                            addmodelList.add(newmodel);
                        }
                    }
                    RefreshListview();
                }

                break;
            case modelrequest:
                if (resultCode == RESULT_OK) {
                    Bundle b = data.getExtras();
                    final String name = b.getString("modelname");
                    final String code = b.getString("modelcode");

                    Session.CheckRefreshToken(new RefreshTokenListener() {
                        @Override
                        public void RefreshTokenResult(final int resultcode) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (resultcode == Codes.Code_Success) {
                                        SCSDK.getInstance().GetProductDetail(Session.getInstance().getShopCode(), code, Session.getInstance().getToken(), new SCResponseListener() {
                                            @Override
                                            public void onResult(Object result) {
                                                final SCResult.ModelDetailResult res = (SCResult.ModelDetailResult) result;
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        boolean ismin = false;
                                                        if (res.ismin == 1) {
                                                            ismin = true;
                                                        }

                                                        AddPurchaseModel newmodel = new AddPurchaseModel(name, "", code, name, ismin, res.spec, "", res.suppliername, "", new ArrayList<String>(), "", res.suppliercode);
                                                        addmodelList.add(newmodel);
                                                        RefreshListview();
                                                    }
                                                });
                                            }

                                            @Override
                                            public void onError(int code, final String errormsg) {
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Toast.makeText(AddPurchaseActivity.this, errormsg, Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }
                                        });
                                    } else {
                                        Toast.makeText(AddPurchaseActivity.this, SCExceptionCodeList.getExceptionMsg(resultcode), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    });
                }

                break;
            case SCANMODEL:
                if (resultCode == RESULT_OK) {
                    Bundle b = data.getExtras();
                    ArrayList<String> templist = b.getStringArrayList("modellist");

//                    for (String tempid : templist) {
//                        addmodelList.get(currenteditindex).getSn().add(tempid);
//                    }
//                    int count = addmodelList.get(currenteditindex).getSn().size();
//                    addmodelList.get(currenteditindex).setStoreCount(String.valueOf(count));
//                    RefreshListview();
final String currenteditcode =  addmodelList.get(currenteditindex).getModelCode();
                    for (final String tempid : templist) {
                        SCSDK.getInstance().GetProductdetailByScan(Session.getInstance().getShopCode(),tempid, Session.getInstance().getToken(), new SCResponseListener() {
                            @Override
                            public void onResult(Object result) {
                                final SCResult.ProductDetailResult product = (SCResult.ProductDetailResult) result;
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if(currenteditcode.equals(product.modelcode)) {
                                            addmodelList.get(currenteditindex).getSn().add(tempid);
                                            int count = addmodelList.get(currenteditindex).getSn().size();
                                            addmodelList.get(currenteditindex).setStoreCount(String.valueOf(count));
                                            RefreshListview();
                                        }
                                        else{
                                            Toast.makeText(AddPurchaseActivity.this,"条码【" + tempid + "】不是当前型号！", Toast.LENGTH_SHORT).show();

                                        }
                                    }
                                });
                            }

                            @Override
                            public void onError(final int code, final String errormsg) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(AddPurchaseActivity.this, errormsg, Toast.LENGTH_SHORT).show();

                                    }
                                });
                            }
                        });
                    }
                }
                break;
            default:
                break;
        }
    }

    private void initView() {
        btn_back = (Button) findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tv_date = (TextView) findViewById(R.id.tv_date);
        tv_date.setText(DateUtil.getCurrentDateStr());
        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_name.setText(Session.getInstance().getNickname());
        tv_confirm = (TextView) findViewById(R.id.tv_confirm);
        tv_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //入库数量>0, 供应商不为空,cost>0
                boolean isverify = true;
                if (addmodelList.size() == 0) {
                    isverify = false;
                    Toast.makeText(AddPurchaseActivity.this, "请添加型号！", Toast.LENGTH_SHORT).show();
                    return;
                }
                for (AddPurchaseModel model : addmodelList) {
                    if (TextUtil.isEmpty(model.getModelCode())) {
                        isverify = false;
                        Toast.makeText(AddPurchaseActivity.this, model.getModelName() + "型号不能为空！", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (TextUtil.isEmpty(model.getStoreCount())) {
                        isverify = false;
                        Toast.makeText(AddPurchaseActivity.this, model.getModelName() + "入库数量不能为空！", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (TextUtil.isEmpty(model.getCost())) {
                        isverify = false;
                        Toast.makeText(AddPurchaseActivity.this, model.getModelName() + "采购单价不能为空！", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                //提交服务器AddPurchaseOrder
                if (isverify) {
                    //型号编码-入库数量-采购单价-供应商编码-采购单编号-采购单项编号(lineno)-[序列号1:序列号2:序列号3],
                    //型号编码-入库数量-采购单价-供应商编码-[序列号1:序列号2:序列号3],
                    // 型号编码-入库数量-采购单价-供应商编码-采购单编号-采购单项编号(lineno),
                    //型号编码-入库数量-采购单价-供应商编码
                    Session.CheckRefreshToken(new RefreshTokenListener() {
                        @Override
                        public void RefreshTokenResult(final int resultcode) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (resultcode == Codes.Code_Success) {
                                        //1st
                                        String models = addmodelList.get(0).getModelCode() + "-" + addmodelList.get(0).getStoreCount() + "-" + addmodelList.get(0).getCost() + "-" + addmodelList.get(0).getSupplyCode();

                                        if (!addmodelList.get(0).getOrderCode().equals("")) {
                                            models = models + "-" + addmodelList.get(0).getOrderCode() + "-" + addmodelList.get(0).getLineno();
                                        }
                                        if (!addmodelList.get(0).getIsMin()) {
                                            models = models + "-[";
                                            List<String> snlist = new ArrayList(addmodelList.get(0).getSn());
                                            StringBuffer str2 = new StringBuffer();
                                            for (Iterator<String> iterator = snlist.iterator(); iterator.hasNext(); ) {
                                                String string = (String) iterator.next();
                                                str2.append(string);
                                                if (iterator.hasNext()) {
                                                    str2.append(":");
                                                }
                                            }
                                            models = models + str2 + "]";
                                        }

                                        //2....
                                        for (int i = 1; i < addmodelList.size(); i++) {
                                            models = models + "," + addmodelList.get(i).getModelCode() + "-" + addmodelList.get(i).getStoreCount() + "-" + addmodelList.get(i).getCost() + "-" + addmodelList.get(i).getSupplyCode();
                                            if (!addmodelList.get(i).getOrderCode().equals("")) {
                                                models = models + "-" + addmodelList.get(i).getOrderCode() + "-" + addmodelList.get(i).getLineno();
                                            }
                                            if (!addmodelList.get(i).getIsMin()) {
                                                models = models + "-[";
                                                List<String> snlist = new ArrayList(addmodelList.get(i).getSn());
                                                StringBuffer str2 = new StringBuffer();
                                                for (Iterator<String> iterator = snlist.iterator(); iterator.hasNext(); ) {
                                                    String string = (String) iterator.next();
                                                    str2.append(string);
                                                    if (iterator.hasNext()) {
                                                        str2.append(":");
                                                    }
                                                }
                                                models = models + str2 + "]";
                                            }
                                        }
                                        System.out.println("入库models========" + models);
                                        SCSDK.getInstance().AddPurchaseOrder(instoredate, models, Session.getInstance().getShopCode(), Session.getInstance().getToken(), new SCResponseListener() {
                                            @Override
                                            public void onResult(Object result) {
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Toast.makeText(AddPurchaseActivity.this, "入库成功！", Toast.LENGTH_SHORT).show();
                                                        finish();
                                                    }
                                                });
                                            }

                                            @Override
                                            public void onError(int code, final String errormsg) {
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Toast.makeText(AddPurchaseActivity.this, errormsg, Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }
                                        });
                                    } else {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(AddPurchaseActivity.this, SCExceptionCodeList.getExceptionMsg(resultcode), Toast.LENGTH_SHORT).show();

                                            }
                                        });
                                    }
                                }
                            });
                        }
                    });
                }

            }
        });
        tv_addmodel = (TextView) findViewById(R.id.tv_addmodel);
        tv_addmodel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddPurchaseActivity.this, PurchaseSearchActivity.class);
                startActivityForResult(intent, modelrequest);
            }
        });
        tv_addorder = (TextView) findViewById(R.id.tv_addorder);
        tv_addorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddPurchaseActivity.this, GetPurListActivity.class);
                startActivityForResult(intent, chooseorderrequest);
            }
        });
        listview_models = (ListView) findViewById(R.id.listview_models);

    }

    private  AlertDialog scanAlert;
    private void ClickScan(){
        if(scanAlert == null) {
            scanAlert = new AlertDialog.Builder(this).setTitle("选择扫码方式")
                    .setCancelable(true)
                    .setPositiveButton("扫码", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent scanintent = new Intent(AddPurchaseActivity.this, MipcaActivityCapture.class);
                            scanintent.putExtra("isSingle", false);
                            startActivityForResult(scanintent, SCANMODEL);
                        }
                    })
                    .setNegativeButton("手动输入", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            EditSns();
                        }
                    }).create();
        }
        scanAlert.show();
    }
    private void EditSns(){
        final EditText et = new EditText(AddPurchaseActivity.this);
        et.setFocusable(true);
        et.setFocusableInTouchMode(true);
        et.requestFocus();
        AlertDialog.Builder alert = new AlertDialog.Builder(AddPurchaseActivity.this).setTitle("请输入单个条码")
                .setView(et)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String input = et.getText().toString();
                        if(input != null && !input.equals("")) {
                            CheckIsModelSn(input);
//                            //输入编码整理
//                            List<String> templist = Arrays.asList(input.split(","));
//                            for (String tempid : templist) {
//                                addmodelList.get(currenteditindex).getSn().add(tempid);
//                            }
//                            int count = addmodelList.get(currenteditindex).getSn().size();
//                            addmodelList.get(currenteditindex).setStoreCount(String.valueOf(count));
//                            RefreshListview();
                        }

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
    private void CheckIsModelSn(final String checksn){
        SCSDK.getInstance().GetProductdetailByScan(Session.getInstance().getShopCode(), checksn, Session.getInstance().getToken(), new SCResponseListener() {
            @Override
            public void onResult(Object result) {
                final SCResult.ProductDetailResult product = (SCResult.ProductDetailResult) result;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                       String modelcode =  addmodelList.get(currenteditindex).getModelCode();
                        if(modelcode.equals(product.modelcode)) {
                            addmodelList.get(currenteditindex).getSn().add(checksn);
                            int count = addmodelList.get(currenteditindex).getSn().size();
                            addmodelList.get(currenteditindex).setStoreCount(String.valueOf(count));
                            RefreshListview();
                        }
                        else{
                            Toast.makeText(AddPurchaseActivity.this,"条码【" + checksn + "】不是当前型号！", Toast.LENGTH_SHORT).show();

                        }
                    }
                });
            }

            @Override
            public void onError(final int code, final String errormsg) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(AddPurchaseActivity.this, errormsg, Toast.LENGTH_SHORT).show();

                    }
                });
            }
        });

    }


    private void RefreshListview() {
        if (addPurchaseModelAdapter == null) {
            addPurchaseModelAdapter = new AddPurchaseModelAdapter(AddPurchaseActivity.this, addmodelList,
                    new SelectSupplyListener() {
                        @Override
                        public void SelectSupply(int index, String modelcode) {
                            currenteditindex = index;
                            Intent intent = new Intent(AddPurchaseActivity.this, ChooseSupplyActivity.class);
                            startActivityForResult(intent, supplyrequest);
                        }
                    }, new EditCountListener() {
                @Override
                public void EditCount(int index, String modelcode, String count) {
                    addmodelList.get(index).setStoreCount(count);
                }
            }, new EditCostListener() {
                @Override
                public void EditCost(int index, String modelcode, String cost) {
//                    for (int i=0 ;i<addmodelList.size(); i++) {
//                        if (i == index) {
                    addmodelList.get(index).setCost(cost);

                }
            }, new AddModelScanListener() {
                @Override
                public void AddModelScan(int index, String modelcode) {
                    currenteditindex = index;
                    ClickScan();

                }
            }, new RemoveAddPurListener() {
                @Override
                public void RemoveAddPur(int index) {
//                        for (int i=0 ;i<addmodelList.size(); i++) {
//                            if (i == index) {
                    addmodelList.remove(index);
                    RefreshListview();

                }
            }, new ShowAddPurSnsListener() {
                @Override
                public void ShowAddPurSns(ArrayList<String> snslist) {
                    ShowSnsDetail(snslist);
                }
            }, new SelectModelListener() {
                @Override
                public void SelectModel(int index) {
                    currenteditindex = index;
                    Intent scanintent = new Intent(AddPurchaseActivity.this, PurchaseSearchActivity.class);
                    startActivityForResult(scanintent, SELECTMODEL);
                }
            });
            listview_models.setAdapter(addPurchaseModelAdapter);
        } else {
            addPurchaseModelAdapter.setModellist(addmodelList);
            addPurchaseModelAdapter.notifyDataSetChanged();
        }
    }

    private void ShowSnsDetail(final ArrayList<String> snslist) {

        LayoutInflater inflater = LayoutInflater.from(this);// 渲染器
        View customdialog2view = inflater.inflate(R.layout.popup_salesnsdetail,
                null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("已扫出库序列号明细");
        builder.setView(customdialog2view);
        builder.setNegativeButton("关闭",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

//                            for (int i=0 ;i<addmodelList.size(); i++) {
//                                if (i==currenteditindex) {
                        int editcount = addmodelList.get(currenteditindex).getSn().size();
                        addmodelList.get(currenteditindex).setStoreCount(String.valueOf(editcount));
//                                    break;
//                                }
//                            }

                        RefreshListview();

                    }
                });

        ListView listView = (ListView) customdialog2view
                .findViewById(R.id.lv);

        adapter = new SaleSnsAdapter(this, snslist, new DeleteSnsListener() {
            @Override
            public void DeleteSns(String sns) {

//                    for (int i=0 ;i<addmodelList.size(); i++) {
//                        if (i==currenteditindex) {
                addmodelList.get(currenteditindex).getSn().remove(sns);
//                            break;
//                        }
//                    }

                for (int j = 0; j < snslist.size(); j++) {
                    if (snslist.get(j).equals(sns)) {
                        snslist.remove(j);
                        break;
                    }
                }
                adapter.notifyDataSetChanged();
            }
        }
        );
        listView.setAdapter(adapter);
        listView.setItemsCanFocus(false);
        listView.setChoiceMode(ListView.CHOICE_MODE_NONE);

        AlertDialog dialog = builder.create();
        dialog.show();

    }
}
