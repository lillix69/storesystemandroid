package qinyuanliu.storesystemandroid.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import qinyuanliu.storesystemandroid.R;
import qinyuanliu.storesystemandroid.model.SupplyModel;

/**
 * Created by qinyuanliu on 2019/4/22.
 */

public class SupplyAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<SupplyModel> datalist = new ArrayList<>();

    public void setModellist(ArrayList<SupplyModel> data) {
        datalist = data;
    }

    public SupplyAdapter(Context context, ArrayList<SupplyModel> dataList) {
        this.context = context;
        this.datalist = dataList;
    }

    @Override
    public int getCount() {
        return datalist.size();
    }

    @Override
    public Object getItem(int position) {
        return datalist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        SupplyAdapter.ViewHolder tag = null;
        final SupplyModel supply = datalist.get(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.listitem_supply, null);
            convertView.setTag(tag);
        } else {
            tag = (SupplyAdapter.ViewHolder) convertView.getTag();
        }

        tag = new SupplyAdapter.ViewHolder();
        tag.supplyview = (RelativeLayout)convertView.findViewById(R.id.supplyview);
        tag.tv_supply = (TextView) convertView.findViewById(R.id.tv_supply);
        tag.tv_supply.setText(supply.supplyName);
        tag.btn_select = (Button) convertView.findViewById(R.id.btn_select);
        tag.btn_select.setSelected(supply.isSelected);
        tag.btn_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                supply.isSelected = true;
                    for (int i = 0; i < datalist.size(); i++) {
                        if (i != position) {
                            datalist.get(i).isSelected = false;
                        }
                    }

                notifyDataSetChanged();
            }
        });

        return convertView;
    }


    private class ViewHolder {
        RelativeLayout supplyview;
        Button btn_select;
        TextView tv_supply;
    }
}
