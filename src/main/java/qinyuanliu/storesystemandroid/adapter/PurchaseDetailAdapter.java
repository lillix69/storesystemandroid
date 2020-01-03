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
 * Created by qinyuanliu on 2019/4/15.
 */

public class PurchaseDetailAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<SCResult.PurchaseStoreDetailInfo> list = new ArrayList<>();

    public void setModellist(ArrayList<SCResult.PurchaseStoreDetailInfo> data) {
        list = data;
    }

    public PurchaseDetailAdapter(Context context, ArrayList<SCResult.PurchaseStoreDetailInfo> modelList) {
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
        PurchaseDetailAdapter.ViewHolder tag = null;
        final SCResult.PurchaseStoreDetailInfo purchase = list.get(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.listitem_purchasedetail, null);
            convertView.setTag(tag);
        } else {
            tag = (PurchaseDetailAdapter.ViewHolder) convertView.getTag();
        }
        tag = new PurchaseDetailAdapter.ViewHolder();
        tag.tv_modelname = (TextView) convertView.findViewById(R.id.tv_modelname);
        tag.tv_count = (TextView) convertView.findViewById(R.id.tv_count);
        tag.tv_cost = (TextView) convertView.findViewById(R.id.tv_cost);
        tag.tv_guige = (TextView) convertView.findViewById(R.id.tv_guige);
        tag.tv_supply = (TextView) convertView.findViewById(R.id.tv_supply);
        tag.tv_remark = (TextView) convertView.findViewById(R.id.tv_remark);
        tag.tv_sns = (TextView) convertView.findViewById(R.id.tv_sns);
        tag.tv_modelname.setText("物料名称:"+purchase.modelname);
        tag.tv_count.setText("入库数量:"+Integer.toString(purchase.count));
        tag.tv_cost.setText("采购单价:"+purchase.price);
        tag.tv_guige.setText("物料规格:"+purchase.spec);
        tag.tv_supply.setText("供应商:"+purchase.supplier);
        tag.tv_remark.setText("备注:"+ purchase.remark);
        if(purchase.ismin == 0){
            tag.tv_sns.setVisibility(View.VISIBLE);
            tag.tv_sns.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!purchase.sns.equals("")) {
                        List<String> templist = Arrays.asList(purchase.sns.split(","));
                        String[] items = new String[templist.size()];
                        int index = 0;
                        for (String tempid : templist) {
                            items[index] = tempid;
                            index++;
                        }
                        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
                        alertBuilder.setTitle("入库序列号明细");
                        alertBuilder.setItems(items, null);
                        alertBuilder.setCancelable(true);
                        alertDialog1 = alertBuilder.create();
                        alertDialog1.show();
                    } else {
                        Toast.makeText(context, "入库序列号为空！", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        else{
            tag.tv_sns.setVisibility(View.GONE);
        }
        return convertView;
    }


    private class ViewHolder {
        TextView tv_modelname;
        TextView tv_cost;
        TextView tv_guige;
        TextView tv_count;
TextView tv_supply;
        TextView tv_remark;
        TextView tv_sns;
    }
}
