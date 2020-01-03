package qinyuanliu.storesystemandroid.adapter;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import qinyuanliu.storesystemandroid.R;
import qinyuanliu.storesystemandroid.listener.AddModelScanListener;
import qinyuanliu.storesystemandroid.listener.EditSendCountListener;
import qinyuanliu.storesystemandroid.listener.ShowSaleSnsListener;
import qinyuanliu.storesystemandroid.model.FahuoModel;

/**
 * Created by qinyuanliu on 2019/8/12.
 */

public class FahuoAdapter extends BaseAdapter {
    private Context context;
    private ShowSaleSnsListener showsnsListener;//查看
    private EditSendCountListener sendcountListener;
    private AddModelScanListener scanListener;//扫一扫

    private ArrayList<FahuoModel> list = new ArrayList<>();
    public void setModellist(ArrayList<FahuoModel> data) {
        list = data;
    }

    public FahuoAdapter(Context context, ArrayList<FahuoModel> modelList,ShowSaleSnsListener l1, AddModelScanListener l4, EditSendCountListener l5) {
        this.context = context;
        this.list = modelList;
        this.scanListener = l4;

        this.showsnsListener = l1;
        this.sendcountListener = l5;
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
        FahuoAdapter.ViewHolder tag = new FahuoAdapter.ViewHolder();
        final FahuoModel model = list.get(position);

        // if (convertView == null) {
        convertView = LayoutInflater.from(context).inflate(R.layout.listitem_fahuo, null);
        convertView.setTag(tag);
//        } else {
//            tag = (AddSaleModelAdapter.ViewHolder) convertView.getTag();
//        }
        //     tag = new AddSaleModelAdapter.ViewHolder();

      //  tag.tv_modelspec = (TextView) convertView.findViewById(R.id.tv_modelspec);
        tag.tv_modelname = (TextView) convertView.findViewById(R.id.tv_modelname);
        tag.tv_salecount = (TextView) convertView.findViewById(R.id.tv_salecount);
        tag.tv_storecount = (TextView) convertView.findViewById(R.id.tv_storecount);

        tag.scancountview = (RelativeLayout) convertView.findViewById(R.id.scancountview);
        tag.tv_scancount = (TextView) convertView.findViewById(R.id.tv_scancount);
        tag.scanview = (RelativeLayout) convertView.findViewById(R.id.scanview);
        tag.scanview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (scanListener != null) {
                    scanListener.AddModelScan(0,model.getModelCode());
                }
            }
        });
        tag.inputcountview = (RelativeLayout) convertView.findViewById(R.id.inputcountview);
        tag.edt_sendcount = (EditText) convertView.findViewById(R.id.edt_sendcount);
        tag.tv_scan = (TextView)convertView.findViewById(R.id.tv_scan);
        tag.tv_showsns = (TextView)convertView.findViewById(R.id.tv_showsns);

        tag.tv_mealname = (TextView)convertView.findViewById(R.id.tv_mealname);
        tag.mealview = (RelativeLayout)convertView.findViewById(R.id.mealview);

        //=====赋值
        tag.tv_modelname.setText(model.getModelName() + "/" + model.getSpec());
       // tag.tv_modelspec.setText(model.getSpec());
        tag.tv_salecount.setText(model.getSaleCount());
        tag.tv_storecount.setText(model.getStoreCount());

        //0表示非套餐 1表示是套餐
if(model.getMealproduct() == 0){
    tag.mealview.setVisibility(View.GONE);
}
else{
    tag.mealview.setVisibility(View.VISIBLE);
    tag.tv_mealname.setText(model.getMealname());
}

        if (model.getIsMin()) {
            tag.edt_sendcount.setVisibility(View.VISIBLE);
            tag.edt_sendcount.addTextChangedListener(new TextWatcher() {

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {

                    if (sendcountListener != null) {
                        sendcountListener.EditSendCount(model.getModelCode(), s.toString());
                    }
                }
            });
            tag.inputcountview.setVisibility(View.VISIBLE);
            tag.scancountview.setVisibility(View.GONE);
            tag.scanview.setVisibility(View.GONE);
        } else {
            tag.tv_scancount.setText(model.getSendCount());
            tag.inputcountview.setVisibility(View.GONE);
            tag.scancountview.setVisibility(View.VISIBLE);
            tag.scanview.setVisibility(View.VISIBLE);
            tag.tv_scan.setVisibility(View.VISIBLE);
            tag.tv_showsns.setVisibility(View.VISIBLE);
            tag.tv_showsns.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(showsnsListener != null){
                        showsnsListener.ShowSaleSns(model.getModelCode());
                    }
                }
            });
            tag.tv_scan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(scanListener != null){
                        scanListener.AddModelScan(0,model.getModelCode());
                    }
                }
            });
        }
        return convertView;
    }


    private class ViewHolder {
        TextView tv_modelname;
//        TextView tv_modelspec;
       TextView tv_salecount;
       TextView tv_storecount;

        RelativeLayout scancountview;
        TextView tv_scancount;
        RelativeLayout scanview;
        TextView tv_scan;
        TextView tv_showsns;

        RelativeLayout inputcountview;
        EditText edt_sendcount;

        RelativeLayout mealview;
        TextView tv_mealname;
    }

}
