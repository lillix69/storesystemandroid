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
import qinyuanliu.storesystemandroid.listener.ConfirmOffdutyListener;
import qinyuanliu.storesystemandroid.listener.ShowOffdutyHistoryListener;

/**
 * Created by qinyuanliu on 2018/10/19.
 */

public class OffdutyHistoryAdapter extends BaseAdapter {
    private Context context;
    private ShowOffdutyHistoryListener showListener;
    private ConfirmOffdutyListener confirmListener;
    private ArrayList<SCResult.DutyoffHistory> datalist = new ArrayList<>();

    public void setDatalist(ArrayList<SCResult.DutyoffHistory> list) {
        datalist = list;
    }

    public OffdutyHistoryAdapter(Context context, ArrayList<SCResult.DutyoffHistory> dataList, ShowOffdutyHistoryListener l1, ConfirmOffdutyListener l2) {
        this.context = context;
        this.datalist = dataList;
        this.showListener = l1;
        this.confirmListener = l2;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder tag = null;
        final SCResult.DutyoffHistory item = datalist.get(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.listitem_offdutyhistory, null);
            convertView.setTag(tag);
        } else {
            tag = (ViewHolder) convertView.getTag();
        }
        tag = new ViewHolder();
        tag.tv_status = (TextView) convertView.findViewById(R.id.tv_status);
        tag.tv_date = (TextView) convertView.findViewById(R.id.tv_date);
        tag.tv_confirmdate = (TextView) convertView.findViewById(R.id.tv_confirmdate);
        tag.btn_show = (Button) convertView.findViewById(R.id.btn_show);
        tag.btn_confirm = (Button) convertView.findViewById(R.id.btn_confirm);
        tag.tv_date.setText(item.date);
        tag.tv_confirmdate.setText(item.confirmtime);
        tag.btn_show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (showListener != null) {
                    showListener.ShowHistory(item.date, item.status);
                }

            }
        });
        tag.btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (confirmListener != null) {
                    confirmListener.ConfirmHistory(item.date, item.status);
                }
            }
        });
        //0：未确认 1：已确认
        if (item.status == 0) {
            tag.btn_confirm.setVisibility(View.VISIBLE);
            tag.btn_show.setVisibility(View.GONE);
            tag.tv_status.setText("未确认");
        } else {
            tag.btn_confirm.setVisibility(View.GONE);
            tag.btn_show.setVisibility(View.VISIBLE);
            tag.tv_status.setText("已确认");
        }
        return convertView;
    }

    private class ViewHolder {
        TextView tv_date;
        TextView tv_status;
        TextView tv_confirmdate;
        Button btn_confirm;
        Button btn_show;

    }
}
