package qinyuanliu.storesystemandroid.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import qinyuanliu.storesystemandroid.R;
import qinyuanliu.storesystemandroid.http.SCResult;

/**
 * Created by qinyuanliu on 2019/5/25.
 */

public class ValueAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<SCResult.ModelValue> list = new ArrayList<>();

    public void setModellist(ArrayList<SCResult.ModelValue> data) {
        list = data;
    }

    public ValueAdapter(Context context, ArrayList<SCResult.ModelValue> modelList) {
        this.context = context;
        this.list = modelList;
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
        ValueAdapter.ViewHolder tag = null;
        final SCResult.ModelValue value = list.get(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.listitem_value, null);
            convertView.setTag(tag);
        } else {
            tag = (ValueAdapter.ViewHolder) convertView.getTag();
        }
        tag = new ValueAdapter.ViewHolder();
        tag.tv_modelname = (TextView) convertView.findViewById(R.id.tv_modelname);
        tag.tv_spec = (TextView) convertView.findViewById(R.id.tv_spec);
        tag.tv_lastprice = (TextView) convertView.findViewById(R.id.tv_lastprice);
        tag.tv_store = (TextView) convertView.findViewById(R.id.tv_store);
        tag.tv_storevalue = (TextView) convertView.findViewById(R.id.tv_storevalue);
        tag.tv_out = (TextView) convertView.findViewById(R.id.tv_out);
        tag.tv_outvalue = (TextView) convertView.findViewById(R.id.tv_outvalue);

        tag.tv_modelname.setText("名称:"+value.modelname);
        tag.tv_spec.setText("规格:"+value.spec);
        tag.tv_lastprice.setText("最近成本单价:"+value.lastedprice);
        tag.tv_store.setText("当前库存:"+Integer.toString(value.store));
        tag.tv_storevalue.setText("当前库存价值:"+value.storevalue);
        tag.tv_out.setText("已出库:"+Integer.toString(value.outstore));
        tag.tv_outvalue.setText("已出库价值:"+value.outstorevalue);

        return convertView;
    }


    private class ViewHolder {
        TextView tv_modelname;
        TextView tv_spec;
        TextView tv_lastprice;
        TextView tv_store;
                TextView tv_storevalue;
        TextView tv_out;
        TextView tv_outvalue;

    }
}
