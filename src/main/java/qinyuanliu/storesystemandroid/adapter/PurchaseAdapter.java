package qinyuanliu.storesystemandroid.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.util.ArrayList;
import qinyuanliu.storesystemandroid.R;
import qinyuanliu.storesystemandroid.http.SCResult;
import qinyuanliu.storesystemandroid.listener.SelectPurchaseListener;


/**
 * Created by qinyuanliu on 2019/4/14.
 */

public class PurchaseAdapter extends BaseAdapter {
    private Context context;
    private final SelectPurchaseListener callback;
    private ArrayList<SCResult.PurchaseStoreOrderInfo> list = new ArrayList<>();

    public void setModellist(ArrayList<SCResult.PurchaseStoreOrderInfo> data) {
        list = data;
    }

    public PurchaseAdapter(Context context, ArrayList<SCResult.PurchaseStoreOrderInfo> modelList, SelectPurchaseListener l) {
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
        PurchaseAdapter.ViewHolder tag = null;
        final SCResult.PurchaseStoreOrderInfo purchase = list.get(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.listitem_purchase, null);
            convertView.setTag(tag);
        } else {
            tag = (PurchaseAdapter.ViewHolder) convertView.getTag();
        }
        tag = new PurchaseAdapter.ViewHolder();
        tag.tv_date = (TextView) convertView.findViewById(R.id.tv_date);
        tag.tv_username = (TextView) convertView.findViewById(R.id.tv_username);
        tag.tv_num = (TextView) convertView.findViewById(R.id.tv_num);
        tag.tv_purchasecode = (TextView) convertView.findViewById(R.id.tv_purchasecode);
        tag.item = (RelativeLayout) convertView.findViewById(R.id.item);
        tag.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callback != null) {
                  callback.SelectPurchase(purchase.instorecode);
                }
            }
        });
        tag.tv_date.setText("入库日期:"+purchase.instoredate);
        tag.tv_username.setText("入库人:"+purchase.username);
        tag.tv_num.setText("入库物料总数:"+purchase.totalstorenum);
        tag.tv_purchasecode.setText("单号:"+purchase.instorecode);
        return convertView;
    }


    private class ViewHolder {
        TextView tv_date;
        TextView tv_username;
        TextView tv_purchasecode;
        TextView tv_num;
        RelativeLayout item;

    }
}
