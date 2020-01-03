package qinyuanliu.storesystemandroid.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import qinyuanliu.storesystemandroid.R;
import qinyuanliu.storesystemandroid.listener.ShowPrepareSnsListener;
import qinyuanliu.storesystemandroid.model.NoticeModel;
import qinyuanliu.storesystemandroid.model.PrepareDetailModel;
import qinyuanliu.storesystemandroid.util.DensityUtil;

/**
 * Created by qinyuanliu on 2018/12/6.
 */

public class ShowPrepareDetailAdapter extends BaseAdapter {
    private Context context;
    private ShowPrepareSnsListener listener;
    private ArrayList<PrepareDetailModel> datalist = new ArrayList<>();
    public void setModellist(ArrayList<PrepareDetailModel> data) {
        datalist = data;
    }

    public ShowPrepareDetailAdapter(Context context, ArrayList<PrepareDetailModel> dataList, ShowPrepareSnsListener l1) {
        this.context = context;
        this.datalist = dataList;
        this.listener = l1;
    }

    @Override
    public int getCount() {
        return datalist.size();
    }

    @Override
    public Object getItem(int position) {
        return datalist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView( int position, View convertView, ViewGroup parent) {
        ShowPrepareDetailAdapter.ViewHolder tag = null;
        final PrepareDetailModel item = datalist.get(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.listitem_preparedetail, null);
            convertView.setTag(tag);
        } else {
            tag = (ShowPrepareDetailAdapter.ViewHolder) convertView.getTag();
        }
        tag = new ShowPrepareDetailAdapter.ViewHolder();
        tag.tv_count = convertView.findViewById(R.id.tv_count);
        tag.tv_modelname = convertView.findViewById(R.id.tv_modelname);
        tag.tv_modelspec = convertView.findViewById(R.id.tv_modelspec);
        tag.tv_show = convertView.findViewById(R.id.tv_show);
        tag.tv_show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean oldflag = item.getIsShow();
                if (listener != null) {
                    listener.ShowPrepareSns(item.getModelcode(), !oldflag);
                }
            }
        });
        tag.tv_showdetail = convertView.findViewById(R.id.tv_showdetail);
        tag.tv_modelspec.setText(item.getModelspec());
        tag.tv_modelname.setText(item.getModelname());
        tag.tv_count.setText(item.getModelcount());
        if (item.getIsSN()) {
            tag.tv_show.setVisibility(View.VISIBLE);
            ViewGroup.LayoutParams lp = tag.tv_showdetail.getLayoutParams();
            if (item.getIsShow()) {
                tag.tv_show.setText("收起序列号");
                lp.height = DensityUtil.dip2px(context, 20 * item.getSnscount());
                tag.tv_showdetail.setLayoutParams(lp);
                tag.tv_showdetail.setVisibility(View.VISIBLE);
                tag.tv_showdetail.setText(item.getSns());
            } else {
                tag.tv_show.setText("展开序列号");
                tag.tv_showdetail.setVisibility(View.GONE);
            }
        } else {
            tag.tv_show.setVisibility(View.GONE);
        }

        return convertView;
    }

    private class ViewHolder {
        TextView tv_modelname;
        TextView tv_modelspec;
        TextView tv_count;
        TextView tv_show;//展开
        TextView tv_showdetail;//展开详细内容
    }
}
