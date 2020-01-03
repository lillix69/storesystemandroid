package qinyuanliu.storesystemandroid.adapter;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import qinyuanliu.storesystemandroid.R;
import qinyuanliu.storesystemandroid.activity.FahuoActivity;
import qinyuanliu.storesystemandroid.listener.AddModelScanListener;
import qinyuanliu.storesystemandroid.listener.EditCostListener;
import qinyuanliu.storesystemandroid.listener.EditCountListener;
import qinyuanliu.storesystemandroid.listener.RemoveAddPurListener;
import qinyuanliu.storesystemandroid.listener.SelectModelListener;
import qinyuanliu.storesystemandroid.listener.SelectSupplyListener;
import qinyuanliu.storesystemandroid.listener.ShowAddPurSnsListener;
import qinyuanliu.storesystemandroid.model.AddPurchaseModel;

/**
 * Created by qinyuanliu on 2019/4/23.
 */

public class AddPurchaseModelAdapter extends BaseAdapter {
    private Context context;
    private SelectSupplyListener supplyListener;
    private EditCountListener countListener;
    private EditCostListener costListener;
    private AddModelScanListener scanListener;
    private RemoveAddPurListener removeListener;
    private ShowAddPurSnsListener showsnsListener;
    private SelectModelListener selectmodelListener;
    private ArrayList<AddPurchaseModel> list = new ArrayList<>();

    public void setModellist(ArrayList<AddPurchaseModel> data) {
        list = data;
    }


    public AddPurchaseModelAdapter(Context context, ArrayList<AddPurchaseModel> modelList,
                                   SelectSupplyListener l1, EditCountListener l2, EditCostListener l3, AddModelScanListener l4,
                                   RemoveAddPurListener l5, ShowAddPurSnsListener l6, SelectModelListener l7) {
        this.context = context;
        this.list = modelList;
        this.supplyListener = l1;
        this.countListener = l2;
        this.costListener = l3;
        this.scanListener = l4;
        this.removeListener = l5;
        this.showsnsListener = l6;
        this.selectmodelListener = l7;
    }


    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        AddPurchaseModelAdapter.ViewHolder tag = new AddPurchaseModelAdapter.ViewHolder();
        final AddPurchaseModel model = list.get(position);

        // if (convertView == null) {
        convertView = LayoutInflater.from(context).inflate(R.layout.listitem_addpurchasemodel, null);
        convertView.setTag(tag);
        //  }

//        else {
//            tag = (AddPurchaseModelAdapter.ViewHolder) convertView.getTag();
//        }
        //tag = new AddPurchaseModelAdapter.ViewHolder();
        tag.tv_remove = (TextView) convertView.findViewById(R.id.tv_remove);
        tag.tv_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeListener.RemoveAddPur(position);
            }
        });
        tag.tv_materialname = (TextView) convertView.findViewById(R.id.tv_materialname);
        tag.tv_modelspec = (TextView) convertView.findViewById(R.id.tv_modelspec);
        tag.ordermodelcodeview = (RelativeLayout)convertView.findViewById(R.id.ordermodelcodeview);
        tag.tv_ordermodelcode = (TextView) convertView.findViewById(R.id.tv_ordermodelcode);
        tag.modelcodeview = (RelativeLayout)convertView.findViewById(R.id.modelcodeview);
        tag.tv_modelcode = (TextView) convertView.findViewById(R.id.tv_modelcode);
        tag.scancountview = (RelativeLayout) convertView.findViewById(R.id.scancountview);
        tag.tv_scancount = (TextView) convertView.findViewById(R.id.tv_scancount);
        tag.scanview = (RelativeLayout) convertView.findViewById(R.id.scanview);
        tag.tv_scan = (TextView) convertView.findViewById(R.id.tv_scan);
        tag.tv_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (scanListener != null) {
                    scanListener.AddModelScan(position,model.getModelCode());
                }
            }
        });
        tag.tv_showsns = (TextView) convertView.findViewById(R.id.tv_showsns);
        tag.tv_showsns.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (model.getSn().size() == 0) {
                    Toast.makeText(context, "已扫序列号为空!", Toast.LENGTH_SHORT).show();
                } else {
                    ArrayList<String> snslist = new ArrayList<>();
                    for (String sn : model.getSn()) {
                        snslist.add(sn);
                    }
                    showsnsListener.ShowAddPurSns(snslist);
                }
            }
        });
        tag.inputcountview = (RelativeLayout) convertView.findViewById(R.id.inputcountview);
        tag.edt_cost = (EditText) convertView.findViewById(R.id.edt_cost);
        tag.edt_count = (EditText) convertView.findViewById(R.id.edt_count);
        tag.tv_supply = (TextView) convertView.findViewById(R.id.tv_supply);
        tag.tv_supply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (supplyListener != null) {
                    supplyListener.SelectSupply(position,model.getModelCode());
                }
            }
        });
        tag.edt_count.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (countListener != null) {
                    countListener.EditCount(position,model.getModelCode(), s.toString());
                }
            }
        });
        final EditText cost = tag.edt_cost;
        tag.edt_cost.addTextChangedListener(new TextWatcher() {
            boolean deleteLastChar = false;// 是否需要删除末尾

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().contains(".")) {
                    // 如果点后面有超过3位数值,则删掉最后一位
                    int length = s.length() - s.toString().lastIndexOf(".");
                    // 说明后面有4位数值
                    deleteLastChar = length >= 4;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s == null || s.toString().equals("")) {
                    return;
                }
                String newcoststr = s.toString();
                if (deleteLastChar) {

                    // 设置新的截取的字符串
                    newcoststr = s.toString().substring(0, s.toString().length() - 1);
                    cost.setText(newcoststr);
                    // 光标强制到末尾
                    cost.setSelection(cost.getText().length());
                }
//                // 以小数点开头，前面自动加上 "0"
//                if (s.toString().startsWith(".")) {
//                    tag.edt_cost.setText("0" + s);
//                    tag.edt_cost.setSelection(tag.edt_cost.getText().length());
//                }

                if (costListener != null) {
                    costListener.EditCost(position,model.getModelCode(), newcoststr);
                }
            }
        });
        tag.caigoucodeview = (RelativeLayout) convertView.findViewById(R.id.caigoucodeview);
        tag.tv_caigoucode = (TextView) convertView.findViewById(R.id.tv_caigoucode);
        if (!model.getOrderCode().equals("")) {
            tag.caigoucodeview.setVisibility(View.VISIBLE);
            tag.ordermodelcodeview.setVisibility(View.VISIBLE);
            tag.modelcodeview.setVisibility(View.GONE);
            tag.tv_caigoucode.setText(model.getOrderCode());
            if (model.getModelCode().equals("")) {
                tag.tv_ordermodelcode.setText("请选择型号");
            } else {
                tag.tv_ordermodelcode.setText(model.getModelName());
            }
            tag.tv_ordermodelcode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectmodelListener.SelectModel(position);
                }
            });
        } else {
            tag.ordermodelcodeview.setVisibility(View.GONE);
            tag.modelcodeview.setVisibility(View.VISIBLE);
            tag.caigoucodeview.setVisibility(View.GONE);

        }
        tag.tv_materialname.setText(model.getMname());
tag.tv_modelcode.setText(model.getModelName());
        tag.tv_modelspec.setText(model.getSpec());
        tag.tv_supply.setText(model.getSupply());
        tag.edt_cost.setText(model.getCost());
        if (model.getIsMin()) {
            tag.edt_count.setText(model.getStoreCount());
            tag.inputcountview.setVisibility(View.VISIBLE);
            tag.scancountview.setVisibility(View.GONE);
            tag.scanview.setVisibility(View.GONE);
        } else {
            tag.tv_scancount.setText(model.getStoreCount());
            tag.inputcountview.setVisibility(View.GONE);
            tag.scancountview.setVisibility(View.VISIBLE);
            tag.scanview.setVisibility(View.VISIBLE);
        }
        return convertView;
    }


    private class ViewHolder {
        TextView tv_materialname;
        TextView tv_remove;
        TextView tv_modelspec;

        RelativeLayout ordermodelcodeview;
        TextView tv_ordermodelcode;
        RelativeLayout modelcodeview;
        TextView tv_modelcode;

        RelativeLayout scanview;
        TextView tv_scan;
        RelativeLayout scancountview;
        TextView tv_showsns;
        TextView tv_scancount;
        RelativeLayout inputcountview;
        EditText edt_count;

        EditText edt_cost;
        TextView tv_supply;
        RelativeLayout caigoucodeview;
        TextView tv_caigoucode;
    }
}
