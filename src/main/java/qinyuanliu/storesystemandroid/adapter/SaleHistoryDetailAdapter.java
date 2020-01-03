package qinyuanliu.storesystemandroid.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import qinyuanliu.storesystemandroid.R;
import qinyuanliu.storesystemandroid.http.SCResult;
import qinyuanliu.storesystemandroid.listener.ShowSaleSnsListener;

/**
 * Created by qinyuanliu on 2019/8/5.
 */

public class SaleHistoryDetailAdapter extends BaseAdapter {
    private Context context;
    private ShowSaleSnsListener listener;
    private ArrayList<SCResult.StoreOrderModel> list = new ArrayList<>();

    public void setModellist(ArrayList<SCResult.StoreOrderModel> data) {
        list = data;
    }

    public SaleHistoryDetailAdapter(Context context, ArrayList<SCResult.StoreOrderModel> modelList, ShowSaleSnsListener l) {
        this.context = context;
        this.list = modelList;
        this.listener = l;
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
        SaleHistoryDetailAdapter.ViewHolder tag = null;
        final SCResult.StoreOrderModel storeorder = list.get(position);

      //  if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.listitem_salehistorydetail, null);
            convertView.setTag(tag);
//        } else {
//            tag = (SaleHistoryDetailAdapter.ViewHolder) convertView.getTag();
//        }
        tag = new SaleHistoryDetailAdapter.ViewHolder();
        tag.tv_modelname = (TextView) convertView.findViewById(R.id.tv_modelname);
      //  tag.tv_spec = (TextView) convertView.findViewById(R.id.tv_spec);
        tag.tv_salecount = (TextView) convertView.findViewById(R.id.tv_salecount);
        tag.tv_sendcount = (TextView) convertView.findViewById(R.id.tv_sendcount);
tag.snsview = (RelativeLayout)convertView.findViewById(R.id.snsview);
        tag.tv_showsns = (TextView) convertView.findViewById(R.id.tv_showsns);
        tag.tv_snscount = (TextView) convertView.findViewById(R.id.tv_snscount);

        if(storeorder.sns.equals("")){
            tag.snsview.setVisibility(View.GONE);
        }
        else{
            List<String> templist = Arrays.asList(storeorder.sns.split(","));
            tag.snsview.setVisibility(View.VISIBLE);
            tag.tv_showsns.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
listener.ShowSaleSns(storeorder.sns);
                }
            });
            tag.tv_snscount.setText("数量:"+Integer.toString(templist.size()));
        }
        tag.tv_modelname.setText(storeorder.modelname + "/" + storeorder.spec);
      //  tag.tv_spec.setText(storeorder.spec);
        tag.tv_salecount.setText(Integer.toString(storeorder.salescount));
        tag.tv_sendcount.setText(Integer.toString(storeorder.storecount));


        return convertView;
    }


    private class ViewHolder {
        TextView tv_modelname;
       // TextView tv_spec;
        TextView tv_salecount;
        TextView tv_sendcount;
        RelativeLayout snsview;
        TextView tv_showsns;
        TextView tv_snscount;
    }
}
