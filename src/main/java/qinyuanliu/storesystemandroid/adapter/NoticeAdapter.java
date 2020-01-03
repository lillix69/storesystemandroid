package qinyuanliu.storesystemandroid.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import qinyuanliu.storesystemandroid.R;
import qinyuanliu.storesystemandroid.listener.SelectNoticeListener;
import qinyuanliu.storesystemandroid.model.NoticeModel;

/**
 * Created by qinyuanliu on 2018/11/20.
 */

public class NoticeAdapter extends BaseAdapter {
    private Context context;
    private final SelectNoticeListener callback;
    private ArrayList<NoticeModel> list = new ArrayList<>();

    public void setModellist(ArrayList<NoticeModel> data) {
        list = data;
    }

    private boolean isEditable = false;

    public void setEditable(boolean flag) {
        isEditable = flag;
    }

    public NoticeAdapter(Context context, ArrayList<NoticeModel> modelList, SelectNoticeListener l) {
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
        NoticeAdapter.ViewHolder tag = null;
        final NoticeModel notice = list.get(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.listitem_notice, null);
            convertView.setTag(tag);
        } else {
            tag = (NoticeAdapter.ViewHolder) convertView.getTag();
        }
        tag = new NoticeAdapter.ViewHolder();
        tag.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
        tag.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
        tag.tv_content = (TextView) convertView.findViewById(R.id.tv_content);
        tag.selectview = (RelativeLayout) convertView.findViewById(R.id.selectview);
        tag.btn_select = (Button) convertView.findViewById(R.id.btn_select);
        tag.tv_content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callback != null) {
                    callback.SelectNotice(notice.noticecode, !notice.isSelected);
                    System.out.println(notice.noticecode + "====" + !notice.isSelected);
                }
            }
        });
        tag.tv_title.setText(notice.noticetitle);
        tag.tv_time.setText(notice.noticetime);
        tag.tv_content.setText(notice.noticecontent);
        if (isEditable) {
            tag.selectview.setVisibility(View.VISIBLE);
            if (notice.isSelected) {
                tag.btn_select.setSelected(true);
            } else {
                tag.btn_select.setSelected(false);
            }
        } else {
            tag.selectview.setVisibility(View.GONE);
        }
        return convertView;
    }


    private class ViewHolder {
        TextView tv_title;
        TextView tv_time;
        TextView tv_content;
        RelativeLayout selectview;
        Button btn_select;
    }
}
