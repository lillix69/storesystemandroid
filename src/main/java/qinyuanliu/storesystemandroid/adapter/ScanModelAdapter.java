package qinyuanliu.storesystemandroid.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import qinyuanliu.storesystemandroid.R;
import qinyuanliu.storesystemandroid.listener.ClickDeleteListener;

/**
 * Created by lillix on 6/5/18.
 */
public class ScanModelAdapter extends BaseAdapter {
    private Context context;
    private final ClickDeleteListener callback;
    private List<String> modellist = new ArrayList<>();
    public void setModellist(List<String> data) {
        modellist = data;
    }

    public ScanModelAdapter (Context context, List<String> modelList, ClickDeleteListener l) {
        this.context = context;
        this.modellist = modelList;
        this.callback = l;
    }

    @Override
    public int getCount() {
        return modellist.size();
    }

    @Override
    public Object getItem(int position) {
        return  modellist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder tag = null;
        final String id = modellist.get(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.griditem_model, null);
            convertView.setTag(tag);
        }else {
            tag = (ViewHolder) convertView.getTag();
        }

        tag = new ViewHolder();
        tag.tv_modelid = (TextView) convertView.findViewById(R.id.tv_modelid);
        tag.tv_modelid.setText(id);
        tag.img_delete = (ImageView)convertView.findViewById(R.id.img_delete);
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callback != null) {
                    callback.DeleteModel( id);
                }
            }
        });


        return convertView;
    }


    private class ViewHolder {
        ImageView img_delete;
        TextView tv_modelid;
    }
}
