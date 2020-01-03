package qinyuanliu.storesystemandroid.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import qinyuanliu.storesystemandroid.R;
import qinyuanliu.storesystemandroid.activity.ShowQuestionActivity;
import qinyuanliu.storesystemandroid.http.SCResult;
import qinyuanliu.storesystemandroid.listener.SelectQuestionListener;

/**
 * Created by qinyuanliu on 2019/5/25.
 */

public class QuestionAdapter extends BaseAdapter {
    private Context context;
    private final SelectQuestionListener callback;
    private ArrayList<SCResult.QuestionModel> list = new ArrayList<>();

    public void setModellist(ArrayList<SCResult.QuestionModel> data) {
        list = data;
    }

    public QuestionAdapter(Context context, ArrayList<SCResult.QuestionModel> modelList, SelectQuestionListener l) {
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
        QuestionAdapter.ViewHolder tag = null;
        final SCResult.QuestionModel question = list.get(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.listitem_question, null);
            convertView.setTag(tag);
        } else {
            tag = (QuestionAdapter.ViewHolder) convertView.getTag();
        }
        tag = new QuestionAdapter.ViewHolder();
        tag.tv_date = (TextView) convertView.findViewById(R.id.tv_date);
        tag.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
        tag.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
        tag.item = (RelativeLayout) convertView.findViewById(R.id.questionitem);
        tag.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callback != null) {

                    callback.ClickQuestion(question.questioncode);
                }
            }
        });
        tag.tv_date.setText("反馈时间:"+question.questiontime);
        tag.tv_name.setText("反馈人:"+question.creater);
        tag.tv_title.setText("标题:"+question.title);
        return convertView;
    }


    private class ViewHolder {
        TextView tv_date;
        TextView tv_name;
        TextView tv_title;
        RelativeLayout item;

    }
}
