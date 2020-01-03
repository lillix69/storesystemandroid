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
import qinyuanliu.storesystemandroid.http.SCResult;
import qinyuanliu.storesystemandroid.listener.DeleteSnsListener;

/**
 * Created by qinyuanliu on 2019/8/3.
 */

public class SaleSnsAdapter extends BaseAdapter {
    private Context context;
    private final DeleteSnsListener callback;
    private ArrayList<String> snslist = new ArrayList<>();
    public void setModellist(ArrayList<String> data) {
        snslist = data;
    }

    public SaleSnsAdapter (Context context, ArrayList<String> snsList, DeleteSnsListener l) {
        this.context = context;
        this.snslist = snsList;
        this.callback = l;
    }

    @Override
    public int getCount() {
        return snslist.size();
    }

    @Override
    public Object getItem(int position) {
        return  snslist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SaleSnsAdapter.ViewHolder tag = null;
        final String sns = snslist.get(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.listview_salesns, null);
            convertView.setTag(tag);
        }else {
            tag = (SaleSnsAdapter.ViewHolder) convertView.getTag();
        }

        tag = new SaleSnsAdapter.ViewHolder();
        tag.tv_modelsns = (TextView) convertView.findViewById(R.id.tv_modelsns);
        tag.tv_modelsns.setText(sns);
        tag.btn_delete = (Button) convertView.findViewById(R.id.btn_delete);
        tag.btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callback != null) {
                    callback.DeleteSns(sns);
                }
            }
        });


        return convertView;
    }


    private class ViewHolder {
        Button btn_delete;
        TextView tv_modelsns;
    }
}
