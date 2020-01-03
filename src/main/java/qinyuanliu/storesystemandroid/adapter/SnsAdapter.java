package qinyuanliu.storesystemandroid.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import qinyuanliu.storesystemandroid.R;
import qinyuanliu.storesystemandroid.http.SCResult;
import qinyuanliu.storesystemandroid.listener.ClickDeleteListener;
import qinyuanliu.storesystemandroid.listener.ClickPrintListener;

/**
 * Created by qinyuanliu on 2018/9/6.
 */

public class SnsAdapter  extends BaseAdapter {
    private Context context;
    private final ClickPrintListener callback;
    private ArrayList<SCResult.SnsModel> snslist = new ArrayList<>();
    public void setModellist(ArrayList<SCResult.SnsModel> data) {
        snslist = data;
    }

    public SnsAdapter (Context context, ArrayList<SCResult.SnsModel> snsList, ClickPrintListener l) {
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
        SnsAdapter.ViewHolder tag = null;
        final String sns = snslist.get(position).sn;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.listview_sns, null);
            convertView.setTag(tag);
        }else {
            tag = (SnsAdapter.ViewHolder) convertView.getTag();
        }

        tag = new SnsAdapter.ViewHolder();
        tag.tv_modelsns = (TextView) convertView.findViewById(R.id.tv_modelsns);
        tag.tv_modelsns.setText(sns);
        tag.btn_print = (Button) convertView.findViewById(R.id.btn_print);
        tag.btn_print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callback != null) {
                    callback.onClickPrint(sns);
                }
            }
        });


        return convertView;
    }


    private class ViewHolder {
       Button btn_print;
        TextView tv_modelsns;
    }
}
