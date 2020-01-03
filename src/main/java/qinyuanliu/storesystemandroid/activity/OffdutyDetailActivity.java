package qinyuanliu.storesystemandroid.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import qinyuanliu.storesystemandroid.R;
import qinyuanliu.storesystemandroid.adapter.OffdutyDetailAdapter;
import qinyuanliu.storesystemandroid.adapter.OffdutyHistoryAdapter;
import qinyuanliu.storesystemandroid.adapter.SeparatedListAdapter;
import qinyuanliu.storesystemandroid.common.Session;
import qinyuanliu.storesystemandroid.http.Codes;
import qinyuanliu.storesystemandroid.http.SCExceptionCodeList;
import qinyuanliu.storesystemandroid.http.SCResponseListener;
import qinyuanliu.storesystemandroid.http.SCResult;
import qinyuanliu.storesystemandroid.http.SCSDK;
import qinyuanliu.storesystemandroid.listener.RefreshTokenListener;
import qinyuanliu.storesystemandroid.listener.RemarkListener;
import qinyuanliu.storesystemandroid.listener.SelectDutyoffDetailListener;
import qinyuanliu.storesystemandroid.listener.ShowDutyoffDetailListener;
import qinyuanliu.storesystemandroid.listener.ShowOffdutyHistoryListener;
import qinyuanliu.storesystemandroid.model.DutyoffDetailModel;
import qinyuanliu.storesystemandroid.model.ProductModel;
import qinyuanliu.storesystemandroid.util.DensityUtil;

/**
 * Created by qinyuanliu on 2018/10/18.
 */

public class OffdutyDetailActivity extends BaseActivity {
    private Button btn_back;
    private TextView tv_date;
    private TextView tv_day;
    private TextView tv_confirm;
    private TextView tv_outtitle;
    private TextView tv_intitle;
    private ListView listview_in;
    private ListView listview_out;
    private ListView listview_produce;
    private String choosedate = "";
    private OffdutyDetailAdapter inadapter;
    private OffdutyDetailAdapter outadapter;
    private SeparatedListAdapter produceadapter;
    private ArrayList<DutyoffDetailModel> indatalist = new ArrayList<>();
    private ArrayList<DutyoffDetailModel> outdatalist = new ArrayList<>();
    //section-----DutyoffDetailModel list
    private Map<String, ArrayList<DutyoffDetailModel>> producedatalist = new LinkedHashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offdutydetail);

        choosedate = getIntent().getStringExtra("offdutydate");
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
        tv_day = (TextView) findViewById(R.id.tv_day);
        tv_date = (TextView) findViewById(R.id.tv_date);
        tv_outtitle = (TextView) findViewById(R.id.tv_outtitle);
        tv_intitle = (TextView) findViewById(R.id.tv_intitle);
        tv_confirm = (TextView) findViewById(R.id.tv_confirm);
        tv_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //检查未选中项是否写了remark
                for (int m = 0; m < outdatalist.size(); m++) {
                    if (!outdatalist.get(m).getIsChoose() && outdatalist.get(m).getRemark().equals("")) {
                        Toast.makeText(OffdutyDetailActivity.this, "请在未勾选项上填写备注！", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                for (int n = 0; n < indatalist.size(); n++) {
                    if (!indatalist.get(n).getIsChoose() && indatalist.get(n).getRemark().equals("")) {
                        Toast.makeText(OffdutyDetailActivity.this, "请在未勾选项上填写备注！", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                //提交
                Session.CheckRefreshToken(new RefreshTokenListener() {
                    @Override
                    public void RefreshTokenResult(final int resultcode) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (resultcode == Codes.Code_Success) {
                                    //型号编号- status-remark半角逗号隔开,其中 status 0:表示未确认 1：表示确认
                                    String outstores = "";
                                    if (outdatalist.size() > 0) {
                                        outstores = outdatalist.get(0).getModelcode() + "-" + (outdatalist.get(0).getIsChoose() ? "1" : "0") + "-" + outdatalist.get(0).getRemark();

                                        if (outdatalist.size() > 1) {
                                            for (int i = 1; i < outdatalist.size(); i++) {
                                                outstores = outstores + "," + outdatalist.get(i).getModelcode() + "-" + (outdatalist.get(i).getIsChoose() ? "1" : "0") + "-" + outdatalist.get(i).getRemark();
                                            }
                                        }
                                    }
                                    String instores = "";
                                    if (indatalist.size() > 0) {
                                        instores = indatalist.get(0).getModelcode() + "-" + (indatalist.get(0).getIsChoose() ? "1" : "0") + "-" + indatalist.get(0).getRemark();

                                        if (indatalist.size() > 1) {
                                            for (int j = 1; j < indatalist.size(); j++) {
                                                instores = instores + "," + indatalist.get(j).getModelcode() + "-" + (indatalist.get(j).getIsChoose() ? "1" : "0") + "-" + indatalist.get(j).getRemark();
                                            }
                                        }
                                    }
                                    String tempproduce = "";
                                    if(producedatalist.size()>0){
                                           //流程编号-步骤编号-型号编号-status-remark
                                        Iterator iter = producedatalist.entrySet().iterator();
                                        while (iter.hasNext()) {
                                            Map.Entry entry = (Map.Entry) iter.next();
                                            ArrayList<DutyoffDetailModel> tempdetail = (ArrayList<DutyoffDetailModel>) entry.getValue();

                                            for (int k = 0; k < tempdetail.size(); k++) {
                                                tempproduce = tempproduce + tempdetail.get(k).routecode + "-" + tempdetail.get(k).stepcode + "-" + tempdetail.get(k).getModelcode() + "-" +
                                                        (tempdetail.get(k).getIsChoose() ? "1" : "0") + "-" + tempdetail.get(k).getRemark() + ",";
                                            }
                                        }
                                    }
                                    String produces = tempproduce.substring(0,tempproduce.length()-1);
                                    SCSDK.getInstance().SubmitDutyoffDetail(Session.getInstance().getShopCode(), Session.getInstance().getToken(), choosedate, outstores, instores, produces, new SCResponseListener() {
                                        @Override
                                        public void onResult(final Object result) {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Toast.makeText(OffdutyDetailActivity.this, "提交成功！", Toast.LENGTH_SHORT).show();
                                                    finish();
                                                }
                                            });
                                        }

                                        @Override
                                        public void onError(int code, final String errormsg) {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Toast.makeText(OffdutyDetailActivity.this, errormsg, Toast.LENGTH_SHORT).show();

                                                }
                                            });
                                        }
                                    });
                                } else {
                                    Toast.makeText(OffdutyDetailActivity.this, SCExceptionCodeList.getExceptionMsg(resultcode), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
            }
        });
        listview_in = (ListView) findViewById(R.id.listview_in);
        listview_out = (ListView) findViewById(R.id.listview_out);
        listview_produce = (ListView) findViewById(R.id.listview_produce);
        //ScrollView起始位置不是最顶部的解决办法
        listview_produce.setFocusable(false);
        listview_out.setFocusable(false);
        listview_in.setFocusable(false);
    }


    private void initData() {
        Session.CheckRefreshToken(new RefreshTokenListener() {
            @Override
            public void RefreshTokenResult(final int resultcode) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (resultcode == Codes.Code_Success) {
                            SCSDK.getInstance().GetDutyoffDetail(Session.getInstance().getShopCode(), Session.getInstance().getToken(), choosedate, new SCResponseListener() {
                                @Override
                                public void onResult(Object result) {
                                    final SCResult.DutyoffResult res = (SCResult.DutyoffResult) result;
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            tv_day.setText(res.weekofday);
                                            tv_date.setText(res.date);
                                            tv_outtitle.setText("今日完成不同型号出库共" + Integer.toString(res.outstoremodelcount) + "种:");
                                            tv_intitle.setText("今日完成不同型号入库共" + Integer.toString(res.instoremodelcount) + "种:");
                                            if (res.outstoremodels != null) {
                                                ViewGroup.LayoutParams lp = listview_out.getLayoutParams();
                                                lp.height = DensityUtil.dip2px(OffdutyDetailActivity.this, 63 * res.outstoremodels.size());
                                                listview_out.setLayoutParams(lp);
                                                for (int i = 0; i < res.outstoremodels.size(); i++) {
                                                    SCResult.DutyoffModel dutyoff = res.outstoremodels.get(i);
                                                    int detailcount = 0;
                                                    String detailtext = "";
                                                    if (dutyoff.details != null && dutyoff.details.size() > 0) {
                                                        detailcount = dutyoff.details.size();
                                                        SCResult.DutyoffModelDetail tempdetail = dutyoff.details.get(0);
                                                        detailtext = tempdetail.time + "     " + tempdetail.typename + "    数量:" + Integer.toString(tempdetail.count);
                                                        for (int j = 1; j < dutyoff.details.size(); j++) {
                                                            tempdetail = dutyoff.details.get(j);
                                                            detailtext = detailtext + "\n" + tempdetail.time + "     " + tempdetail.typename + "     数量:" + Integer.toString(tempdetail.count);
                                                        }
                                                    }
                                                    DutyoffDetailModel detailmodel = new DutyoffDetailModel(dutyoff.modelname, dutyoff.modelcode, dutyoff.modelspec, "数量:" + Integer.toString(dutyoff.count), detailcount, detailtext, "", false,"","","");
                                                    outdatalist.add(detailmodel);
                                                }
                                            }
                                            if (res.instoremodels != null) {
                                                ViewGroup.LayoutParams lp = listview_in.getLayoutParams();
                                                lp.height = DensityUtil.dip2px(OffdutyDetailActivity.this, 63 * res.instoremodels.size());
                                                listview_in.setLayoutParams(lp);
                                                for (int i = 0; i < res.instoremodels.size(); i++) {
                                                    SCResult.DutyoffModel dutyoff = res.instoremodels.get(i);
                                                    int detailcount = 0;
                                                    String detailtext = "";
                                                    if (dutyoff.details != null && dutyoff.details.size() > 0) {
                                                        detailcount = dutyoff.details.size();
                                                        SCResult.DutyoffModelDetail tempdetail = dutyoff.details.get(0);
                                                        detailtext = tempdetail.time + "     " + tempdetail.typename + "    数量:" + Integer.toString(tempdetail.count);
                                                        for (int j = 1; j < dutyoff.details.size(); j++) {
                                                            tempdetail = dutyoff.details.get(j);
                                                            detailtext = detailtext + "\n" + tempdetail.time + "     " + tempdetail.typename + "     数量:" + Integer.toString(tempdetail.count);
                                                        }
                                                    }
                                                    DutyoffDetailModel detailmodel = new DutyoffDetailModel(dutyoff.modelname, dutyoff.modelcode, dutyoff.modelspec, "数量:" + Integer.toString(dutyoff.count), detailcount, detailtext, "", false,"","","");
                                                    indatalist.add(detailmodel);
                                                }
                                            }
                                            if (res.productstatusinfos != null) {
                                                for (int i = 0; i < res.productstatusinfos.size(); i++) {
                                                    SCResult.ProductStatusModel produce = res.productstatusinfos.get(i);
                                                    ArrayList<DutyoffDetailModel> detail = new ArrayList<>();
                                                    if (produce.models != null && produce.models.size() > 0) {
                                                        for (int j = 0; j < produce.models.size(); j++) {
                                                            SCResult.ProduceModel tempdetail = produce.models.get(j);
                                                            int detailcount = tempdetail.details.size();
                                                            SCResult.ProduceDetailModel tempproducedetsail = tempdetail.details.get(0);
                                                            String detailtext = tempproducedetsail.time + "     SN:" + tempproducedetsail.sn;
                                                            for (int k = 1; k < tempdetail.details.size(); k++) {
                                                                tempproducedetsail = tempdetail.details.get(k);
                                                                detailtext = detailtext + "\n" + tempproducedetsail.time + "     SN:" + tempproducedetsail.sn;
                                                            }
                                                            DutyoffDetailModel detailmodel = new DutyoffDetailModel(tempdetail.modelname, tempdetail.modelcode, tempdetail.modelspec, "数量:" + Integer.toString(tempdetail.count), detailcount, detailtext, "", false,produce.summary,tempdetail.routecode,tempdetail.stepcode);
                                                            detail.add(detailmodel);
                                                        }
                                                    }
                                                    producedatalist.put(produce.summary, detail);
                                                }
                                            }

                                            RefreshListview();
                                        }
                                    });
                                }

                                @Override
                                public void onError(int code, final String errormsg) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(OffdutyDetailActivity.this, errormsg, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            });
                        } else {
                            Toast.makeText(OffdutyDetailActivity.this, SCExceptionCodeList.getExceptionMsg(resultcode), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    private void RefreshListview() {
        if (inadapter == null) {
            inadapter = new OffdutyDetailAdapter(OffdutyDetailActivity.this, indatalist, new ShowDutyoffDetailListener() {
                @Override
                public void ShowDutyoffDetail(int pos, boolean isShow, String sectionheader) {
                    indatalist.get(pos).setIsShow(isShow);
                    if (isShow) {
                        ViewGroup.LayoutParams lp = listview_in.getLayoutParams();
                        lp.height = lp.height + DensityUtil.dip2px(OffdutyDetailActivity.this, 18 * indatalist.get(pos).getDetailcount());
                        listview_in.setLayoutParams(lp);
                    } else {
                        ViewGroup.LayoutParams lp = listview_in.getLayoutParams();
                        lp.height = lp.height - DensityUtil.dip2px(OffdutyDetailActivity.this, 18 * indatalist.get(pos).getDetailcount());
                        listview_in.setLayoutParams(lp);
                    }
                    inadapter.setDatalist(indatalist);
                    inadapter.notifyDataSetChanged();
                }
            }, new RemarkListener() {
                @Override
                public void EditRemark(int pos, String remark, String sectionheader) {
                    indatalist.get(pos).setRemark(remark);
                }
            }, new SelectDutyoffDetailListener() {
                @Override
                public void SelectDutyoffDetail(int pos, boolean isSelected, String sectionheader) {
                    indatalist.get(pos).setIsChoose(isSelected);
                    inadapter.setDatalist(indatalist);
                    inadapter.notifyDataSetChanged();
                }
            });
            listview_in.setAdapter(inadapter);
        } else {
            inadapter.setDatalist(indatalist);
            inadapter.notifyDataSetChanged();
        }

        if (outadapter == null) {
            outadapter = new OffdutyDetailAdapter(OffdutyDetailActivity.this, outdatalist, new ShowDutyoffDetailListener() {
                @Override
                public void ShowDutyoffDetail(int pos, boolean isShow, String sectionheader) {
                    outdatalist.get(pos).setIsShow(isShow);
                    if (isShow) {
                        ViewGroup.LayoutParams lp = listview_out.getLayoutParams();
                        lp.height = lp.height + DensityUtil.dip2px(OffdutyDetailActivity.this, 18 * outdatalist.get(pos).getDetailcount());
                        listview_out.setLayoutParams(lp);
                    } else {
                        ViewGroup.LayoutParams lp = listview_out.getLayoutParams();
                        lp.height = lp.height - DensityUtil.dip2px(OffdutyDetailActivity.this, 18 * outdatalist.get(pos).getDetailcount());
                        listview_out.setLayoutParams(lp);
                    }
                    outadapter.setDatalist(outdatalist);
                    outadapter.notifyDataSetChanged();
                }
            }, new RemarkListener() {
                @Override
                public void EditRemark(int pos, String remark, String sectionheader) {
                    outdatalist.get(pos).setRemark(remark);
                }
            }, new SelectDutyoffDetailListener() {
                @Override
                public void SelectDutyoffDetail(int pos, boolean isSelected, String sectionheader) {
                    outdatalist.get(pos).setIsChoose(isSelected);
                    outadapter.setDatalist(outdatalist);
                    outadapter.notifyDataSetChanged();
                }
            });
            listview_out.setAdapter(outadapter);
        } else {
            outadapter.setDatalist(outdatalist);
            outadapter.notifyDataSetChanged();
        }

        if (produceadapter == null) {
            produceadapter = new SeparatedListAdapter(OffdutyDetailActivity.this);
            Iterator iter = producedatalist.entrySet().iterator();
            int height = 0;
            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();
                String key = (String) entry.getKey();
                ArrayList<DutyoffDetailModel> val = (ArrayList<DutyoffDetailModel>) entry.getValue();
                height = height + 25+63*val.size();
                OffdutyDetailAdapter sectionadapter = new OffdutyDetailAdapter(OffdutyDetailActivity.this, val, new ShowDutyoffDetailListener() {
                    @Override
                    public void ShowDutyoffDetail(int pos, boolean isShow, String sectionheader) {
                        producedatalist.get(sectionheader).get(pos).setIsShow(isShow);
                        if (isShow) {
                            ViewGroup.LayoutParams lp = listview_produce.getLayoutParams();
                            lp.height = lp.height + DensityUtil.dip2px(OffdutyDetailActivity.this, 18 * producedatalist.get(sectionheader).get(pos).getDetailcount());
                            listview_produce.setLayoutParams(lp);
                        } else {
                            ViewGroup.LayoutParams lp = listview_produce.getLayoutParams();
                            lp.height = lp.height - DensityUtil.dip2px(OffdutyDetailActivity.this, 18 * producedatalist.get(sectionheader).get(pos).getDetailcount());
                            listview_produce.setLayoutParams(lp);
                        }
                        produceadapter.notifyDataSetChanged();
                    }
                }, new RemarkListener() {
                    @Override
                    public void EditRemark(int pos, String remark, String sectionheader) {
                        producedatalist.get(sectionheader).get(pos).setRemark(remark);
                    }
                }, new SelectDutyoffDetailListener() {
                    @Override
                    public void SelectDutyoffDetail(int pos, boolean isSelected, String sectionheader) {
                        producedatalist.get(sectionheader).get(pos).setIsChoose(isSelected);
                        produceadapter.notifyDataSetChanged();
                    }
                });
                produceadapter.addSection(key,sectionadapter );
            }
            listview_produce.setAdapter(produceadapter);
            ViewGroup.LayoutParams lp = listview_produce.getLayoutParams();
            lp.height = DensityUtil.dip2px(OffdutyDetailActivity.this,height);
            listview_produce.setLayoutParams(lp);

        } else {
            produceadapter.notifyDataSetChanged();
        }

    }
}