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
import qinyuanliu.storesystemandroid.listener.SelectBatchSnsListener;
import qinyuanliu.storesystemandroid.model.BatchSnsModel;


/**
 * Created by qinyuanliu on 2019/7/21.
 */

public class BatchSnsAdapter extends BaseAdapter {
    private Context context;
    private final SelectBatchSnsListener callback;
    private ArrayList<BatchSnsModel> list = new ArrayList<>();
    public void setModellist(ArrayList<BatchSnsModel> data) {
        list = data;
    }

    public BatchSnsAdapter(Context context, ArrayList<BatchSnsModel> modelList, SelectBatchSnsListener l) {
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
        BatchSnsAdapter.ViewHolder tag = null;
        final BatchSnsModel sns = list.get(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.listitem_batchsns, null);
            convertView.setTag(tag);
        } else {
            tag = (BatchSnsAdapter.ViewHolder) convertView.getTag();
        }
        tag = new BatchSnsAdapter.ViewHolder();
        tag.tv_sns = (TextView) convertView.findViewById(R.id.tv_sns);
        tag.btn_select = (Button) convertView.findViewById(R.id.btn_select);
        tag.btn_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callback != null) {
                    System.out.println(sns.sns + "====" + !sns.isSelected);
                    callback.ClickBatchSns(sns.sns, !sns.isSelected);

                }
            }
        });
        tag.tv_sns.setText(sns.sns);
            if (sns.isSelected) {
                tag.btn_select.setSelected(true);
            } else {
                tag.btn_select.setSelected(false);
            }

        return convertView;
    }


    private class ViewHolder {
        TextView tv_sns;
        Button btn_select;
    }
}
