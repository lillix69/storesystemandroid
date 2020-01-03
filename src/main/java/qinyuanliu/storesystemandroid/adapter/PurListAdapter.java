package qinyuanliu.storesystemandroid.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import qinyuanliu.storesystemandroid.R;
import qinyuanliu.storesystemandroid.listener.SelectPurItemListener;
import qinyuanliu.storesystemandroid.listener.ShowPurModelListener;
import qinyuanliu.storesystemandroid.model.PurModel;


/**
 * Created by qinyuanliu on 2019/9/9.
 */

public class PurListAdapter extends BaseAdapter {
    private Context context;
    private final SelectPurItemListener callback;
    private ShowPurModelListener showmodelListener;
    private ArrayList<PurModel> list = new ArrayList<>();
    public void setModellist(ArrayList<PurModel> data) {
        list = data;
    }

    public PurListAdapter(Context context, ArrayList<PurModel> modelList, SelectPurItemListener l, ShowPurModelListener l1) {
        this.context = context;
        this.list = modelList;
        this.callback = l;
        showmodelListener = l1;
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
        PurListAdapter.ViewHolder tag = null;
        final PurModel pur = list.get(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.listitem_purlist, null);
            convertView.setTag(tag);
        } else {
            tag = (PurListAdapter.ViewHolder) convertView.getTag();
        }
        tag = new PurListAdapter.ViewHolder();
        tag.tv_ordercode = (TextView) convertView.findViewById(R.id.tv_ordercode);
        tag.tv_orderdate = (TextView) convertView.findViewById(R.id.tv_orderdate);
        tag.tv_allcost = (TextView) convertView.findViewById(R.id.tv_allcost);
        tag.tv_remark = (TextView) convertView.findViewById(R.id.tv_remark);
        tag.tv_count = (TextView) convertView.findViewById(R.id.tv_count);
        tag.tv_ordercode.setText("采购单号:"+pur.orderCode);
        tag.tv_orderdate.setText("下单日期:"+pur.orderDate);
        tag.tv_allcost.setText("采购总价:"+pur.totalPrice);
        tag.tv_remark.setText("备注:"+pur.remark);
        tag.tv_count.setText("采购总数:"+pur.purchaseNum);
        tag.tv_showmodel = (TextView) convertView.findViewById(R.id.tv_showmodel);
        tag.tv_showmodel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
showmodelListener.ShowPurModel(pur.orderCode);
            }
        });
        tag.btn_select = (Button) convertView.findViewById(R.id.btn_select);
        tag.btn_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callback != null) {
                    callback.ClickOrder(pur.orderCode, !pur.isSelected);
                }
            }
        });
        if (pur.isSelected) {
            tag.btn_select.setSelected(true);
        } else {
            tag.btn_select.setSelected(false);
        }

        return convertView;
    }


    private class ViewHolder {
        TextView tv_ordercode;
        TextView tv_orderdate;
        TextView tv_count;
        TextView tv_allcost;
        TextView tv_showmodel;
        TextView tv_remark;
        Button btn_select;
    }
}
