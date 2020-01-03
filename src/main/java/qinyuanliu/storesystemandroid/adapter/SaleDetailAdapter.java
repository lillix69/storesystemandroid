package qinyuanliu.storesystemandroid.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import qinyuanliu.storesystemandroid.activity.SaleDetailActivity;
import qinyuanliu.storesystemandroid.http.SCResult;

/**
 * Created by qinyuanliu on 2019/4/15.
 */

public class SaleDetailAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<SCResult.SaleOrderModel> list = new ArrayList<>();

    public void setModellist(ArrayList<SCResult.SaleOrderModel> data) {
        list = data;
    }

    public SaleDetailAdapter(Context context, ArrayList<SCResult.SaleOrderModel> modelList) {
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
        SaleDetailAdapter.ViewHolder tag = new SaleDetailAdapter.ViewHolder();
        final SCResult.SaleOrderModel sale = list.get(position);

      //  if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.listitem_saledetail, null);
            convertView.setTag(tag);
//        } else {
//            tag = (SaleDetailAdapter.ViewHolder) convertView.getTag();
//        }
//        tag = new SaleDetailAdapter.ViewHolder();
        tag.tv_modelname = (TextView) convertView.findViewById(R.id.tv_modelname);
        tag.tv_salecount = (TextView) convertView.findViewById(R.id.tv_salecount);
        tag.tv_cost = (TextView) convertView.findViewById(R.id.tv_cost);
        tag.tv_guige = (TextView) convertView.findViewById(R.id.tv_guige);
        tag.tv_storecount = (TextView) convertView.findViewById(R.id.tv_storecount);
        tag.tv_sns = (TextView) convertView.findViewById(R.id.tv_sns);



        tag.tv_modelname.setText("物料名称:" + sale.modelname);
        tag.tv_salecount.setText("销售数量:" + Integer.toString(sale.salescount));
        tag.tv_cost.setText("销售单价:" + sale.price);
        tag.tv_guige.setText(sale.spec);
        tag.tv_storecount.setText("已发货数量:" + Integer.toString(sale.storedcount));
        //是否是元器件 0：不是 1：是
        if (sale.ismin == 1) {
            tag.tv_sns.setVisibility(View.GONE);
        } else {
            tag.tv_sns.setVisibility(View.VISIBLE);
            tag.tv_sns.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!sale.sns.equals("")) {
                        List<String> templist = Arrays.asList(sale.sns.split(","));
                        String[] items = new String[templist.size()];
                        int index = 0;
                        for (String tempid : templist) {
                            items[index] = tempid;
                            index++;
                        }
                        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
                        alertBuilder.setTitle("出库序列号明细");
                        alertBuilder.setItems(items, null);
                        alertBuilder.setCancelable(true);
                        alertDialog1 = alertBuilder.create();
                        alertDialog1.show();
                    } else {
                        Toast.makeText(context, "出库序列号为空！", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        return convertView;
    }


    private class ViewHolder {
        TextView tv_modelname;
        TextView tv_cost;
        TextView tv_guige;
        TextView tv_salecount;
        TextView tv_storecount;
        TextView tv_sns;
    }
}
