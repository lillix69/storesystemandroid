package qinyuanliu.storesystemandroid.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

import qinyuanliu.storesystemandroid.R;
import qinyuanliu.storesystemandroid.http.SCResult;
import qinyuanliu.storesystemandroid.listener.SelectSaleListener;

/**
 * Created by qinyuanliu on 2019/4/15.
 */

public class SaleAdapter extends BaseAdapter {
    private Context context;
    private final SelectSaleListener callback;
    private ArrayList<SCResult.SaleOrderInfo> list = new ArrayList<>();

    public void setModellist(ArrayList<SCResult.SaleOrderInfo> data) {
        list = data;
    }

    public SaleAdapter(Context context, ArrayList<SCResult.SaleOrderInfo> modelList, SelectSaleListener l) {
        this.context = context;
        this.list = modelList;
        this.callback = l;
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
        SaleAdapter.ViewHolder tag = null;
        final SCResult.SaleOrderInfo sale = list.get(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.listitem_sale, null);
            convertView.setTag(tag);
        } else {
            tag = (SaleAdapter.ViewHolder) convertView.getTag();
        }
        tag = new SaleAdapter.ViewHolder();
        tag.tv_date = (TextView) convertView.findViewById(R.id.tv_date);
        tag.tv_client = (TextView) convertView.findViewById(R.id.tv_client);
        tag.tv_tel = (TextView) convertView.findViewById(R.id.tv_tel);
        tag.tv_salecode = (TextView) convertView.findViewById(R.id.tv_salecode);
        tag.tv_storestatus = (TextView)convertView.findViewById(R.id.tv_storestatus);
        tag.tv_paystatus = (TextView)convertView.findViewById(R.id.tv_paystatus);
        tag.tv_money =  (TextView)convertView.findViewById(R.id.tv_money);
        tag.item = (RelativeLayout) convertView.findViewById(R.id.item);
        tag.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callback != null) {
                    callback.SelectSale(sale.saleordercode);
                }
            }
        });
        tag.tv_date.setText("下单日期:"+sale.saleorderdate);
        tag.tv_client.setText("客户姓名:"+sale.customername);
        tag.tv_tel.setText("手机:"+sale.customertel);
        tag.tv_salecode.setText("单号:"+sale.saleordercode);
        tag.tv_storestatus.setText("发货状态:"+sale.storestatustext);
        tag.tv_paystatus.setText("付款状态:"+sale.paystatustext);
        tag.tv_money.setText("总金额:"+sale.money);
        return convertView;
    }


    private class ViewHolder {
        TextView tv_date;
        TextView tv_client;
        TextView tv_salecode;
        TextView tv_tel;
        TextView tv_storestatus;
        TextView tv_money;
        TextView tv_paystatus;
        RelativeLayout item;

    }
}
