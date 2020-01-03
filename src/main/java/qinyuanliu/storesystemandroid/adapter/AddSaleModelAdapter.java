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

import java.util.ArrayList;

import qinyuanliu.storesystemandroid.R;
import qinyuanliu.storesystemandroid.listener.AddModelScanListener;
import qinyuanliu.storesystemandroid.listener.EditCostListener;
import qinyuanliu.storesystemandroid.listener.EditCountListener;
import qinyuanliu.storesystemandroid.listener.EditSendCountListener;
import qinyuanliu.storesystemandroid.listener.RemoveAddSaleListener;
import qinyuanliu.storesystemandroid.listener.ShowSaleSnsListener;
import qinyuanliu.storesystemandroid.model.AddPurchaseModel;
import qinyuanliu.storesystemandroid.model.AddSaleModel;

/**
 * Created by qinyuanliu on 2019/4/24.
 */

public class AddSaleModelAdapter extends BaseAdapter {
    private Context context;
    private EditCountListener countListener;//销售数量
    private EditCostListener costListener;//销售单价
    private RemoveAddSaleListener removeListener;//移除


    private ArrayList<AddSaleModel> list = new ArrayList<>();
    public void setModellist(ArrayList<AddSaleModel> data) {
        list = data;
    }

    public AddSaleModelAdapter(Context context, ArrayList<AddSaleModel> modelList, RemoveAddSaleListener l,
                               EditCountListener l2, EditCostListener l3) {
        this.context = context;
        this.list = modelList;
        this.countListener = l2;
        this.costListener = l3;
        this.removeListener = l;

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
    public View getView(int position, View convertView, ViewGroup parent) {
        AddSaleModelAdapter.ViewHolder tag = new AddSaleModelAdapter.ViewHolder();
        final AddSaleModel model = list.get(position);

       // if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.listitem_salemodel, null);
            convertView.setTag(tag);
//        } else {
//            tag = (AddSaleModelAdapter.ViewHolder) convertView.getTag();
//        }
   //     tag = new AddSaleModelAdapter.ViewHolder();
        tag.tv_remove = (TextView) convertView.findViewById(R.id.tv_remove);
        tag.tv_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeListener.RemoveAddSale(model.getModelCode());
            }
        });
        tag.tv_modelspec = (TextView) convertView.findViewById(R.id.tv_modelspec);
        tag.tv_modelname = (TextView) convertView.findViewById(R.id.tv_modelname);
        tag.edt_cost = (EditText) convertView.findViewById(R.id.edt_cost);
        tag.edt_salecount = (EditText) convertView.findViewById(R.id.edt_salecount);
        tag.edt_salecount.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (countListener != null) {
                    countListener.EditCount(0,model.getModelCode(), s.toString());
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
                if (costListener != null) {
                    costListener.EditCost(0,model.getModelCode(), newcoststr);
                }
            }
        });

        //=====赋值
        tag.tv_modelname.setText(model.getModelName());
        tag.tv_modelspec.setText(model.getSpec());
        tag.edt_cost.setText(model.getCost());
        tag.edt_salecount.setText(model.getSaleCount());

        return convertView;
    }


    private class ViewHolder {
        TextView tv_remove;
        TextView tv_modelname;
        TextView tv_modelspec;
        EditText edt_salecount;
        EditText edt_cost;

    }
}
