package qinyuanliu.storesystemandroid.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import qinyuanliu.storesystemandroid.R;
import qinyuanliu.storesystemandroid.http.SCResult;

/**
 * Created by qinyuanliu on 2019/9/11.
 */

public class ModelDetailApater extends BaseAdapter {
    private Context context;
    private ArrayList<SCResult.PurchaseModel> list = new ArrayList<>();

    public void setModellist(ArrayList<SCResult.PurchaseModel> data) {
        list = data;
    }

    public ModelDetailApater(Context context, ArrayList<SCResult.PurchaseModel> modelList) {
        this.context = context;
        this.list = modelList;
    }
    private AlertDialog alertDialog1;

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
        ModelDetailApater.ViewHolder tag = null;
        final SCResult.PurchaseModel model = list.get(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.listitem_purmodeldetail, null);
            convertView.setTag(tag);
        } else {
            tag = (ModelDetailApater.ViewHolder) convertView.getTag();
        }
        tag = new ModelDetailApater.ViewHolder();
        tag.tv_modelname = (TextView) convertView.findViewById(R.id.tv_modelname);
        tag.tv_count = (TextView) convertView.findViewById(R.id.tv_count);
        tag.tv_cost = (TextView) convertView.findViewById(R.id.tv_cost);
        tag.tv_guige = (TextView) convertView.findViewById(R.id.tv_guige);
        tag.tv_supply = (TextView) convertView.findViewById(R.id.tv_supply);
        tag.tv_remark = (TextView) convertView.findViewById(R.id.tv_remark);
        tag.tv_modelname.setText("物料名称:"+ model.materielname);
        tag.tv_count.setText("采购数量:"+ Integer.toString(model.count));
        tag.tv_cost.setText("采购单价:"+ model.price);
        tag.tv_guige.setText("物料规格:"+ model.materielspec);
        tag.tv_supply.setText("供应商:"+ model.supplier);
        tag.tv_remark.setText("备注:"+ model.remark);

        return convertView;
    }


    private class ViewHolder {
        TextView tv_modelname;
        TextView tv_cost;
        TextView tv_guige;
        TextView tv_count;
        TextView tv_supply;
        TextView tv_remark;
    }
}
