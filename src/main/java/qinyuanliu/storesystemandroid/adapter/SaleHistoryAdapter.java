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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;

import qinyuanliu.storesystemandroid.R;
import qinyuanliu.storesystemandroid.activity.ModelDetailActivity;
import qinyuanliu.storesystemandroid.http.SCResult;
import qinyuanliu.storesystemandroid.listener.SelectQuestionListener;
import qinyuanliu.storesystemandroid.listener.ShowSaleHistoryListener;
import qinyuanliu.storesystemandroid.listener.ShowSalePicListener;

/**
 * Created by qinyuanliu on 2019/8/4.
 */

public class SaleHistoryAdapter extends BaseAdapter {
    private Context context;
    private ShowSaleHistoryListener callback;
    private ShowSalePicListener showpicListener;
    private ArrayList<SCResult.StoreOrder> list = new ArrayList<>();

    public void setModellist(ArrayList<SCResult.StoreOrder> data) {
        list = data;
    }

    public SaleHistoryAdapter(Context context, ArrayList<SCResult.StoreOrder> modelList, ShowSaleHistoryListener l, ShowSalePicListener l2) {
        this.context = context;
        this.list = modelList;
        this.callback = l;
        this.showpicListener = l2;
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
        SaleHistoryAdapter.ViewHolder tag = null;
        final SCResult.StoreOrder storeorder = list.get(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.listitem_salehistory, null);
            convertView.setTag(tag);
        } else {
            tag = (SaleHistoryAdapter.ViewHolder) convertView.getTag();
        }
        tag = new SaleHistoryAdapter.ViewHolder();
        tag.tv_ordercode = (TextView) convertView.findViewById(R.id.tv_ordercode);
        tag.tv_orderdate = (TextView) convertView.findViewById(R.id.tv_orderdate);
        tag.tv_remark = (TextView) convertView.findViewById(R.id.tv_remark);
        tag.img_pic = (ImageView) convertView.findViewById(R.id.img_pic);
        if (storeorder.logisticsurl != null && !storeorder.logisticsurl.equals("")) {
            Glide.with(context).load(storeorder.logisticsurl)
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            Log.i("GlideException", "====" + e);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            Log.i("GlideReady", "======载入成功");
                            return false;
                        }
                    })
                    .placeholder(R.drawable.icon_defaultimg)
                    .error(R.drawable.icon_defaultimg)
                    //    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .into(tag.img_pic);
        } else {
            tag.img_pic.setImageResource(R.drawable.icon_defaultimg);
        }
        tag.tv_showpic =  (TextView) convertView.findViewById(R.id.tv_showpic);
        tag.tv_showpic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               showpicListener.ShowSalePic(storeorder.logisticsurl);
            }
        });
        tag.tv_showdetail = (TextView) convertView.findViewById(R.id.tv_showdetail);
        tag.tv_showdetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
callback.ShowSaleHistory(storeorder.storeordercode,storeorder.storeorderdate,storeorder.logisticsurl, storeorder.remark);
            }
        });
        tag.tv_ordercode.setText("物流单号:"+storeorder.logisticscode);
        tag.tv_orderdate.setText("发货日期:"+storeorder.storeorderdate);
        tag.tv_remark.setText("物流备注:"+storeorder.remark);
        return convertView;
    }


    private class ViewHolder {
        TextView tv_ordercode;
        TextView tv_orderdate;
        ImageView img_pic;
        TextView tv_showpic;
        TextView tv_remark;
        TextView tv_showdetail;
    }
}
