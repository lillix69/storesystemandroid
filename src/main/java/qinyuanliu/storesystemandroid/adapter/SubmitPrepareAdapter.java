package qinyuanliu.storesystemandroid.adapter;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import qinyuanliu.storesystemandroid.R;
import qinyuanliu.storesystemandroid.listener.ChangePrepareListener;
import qinyuanliu.storesystemandroid.listener.SelectPrepareListener;
import qinyuanliu.storesystemandroid.listener.ShowPrepareSnsListener;
import qinyuanliu.storesystemandroid.model.SubmitPrepareModel;
import qinyuanliu.storesystemandroid.util.DensityUtil;

/**
 * Created by qinyuanliu on 2018/12/10.
 */

public class SubmitPrepareAdapter extends BaseAdapter {
    private Context context;
    private ShowPrepareSnsListener listener;
    private SelectPrepareListener selectlistener;
    private ChangePrepareListener changePrepareListener;
    private ArrayList<SubmitPrepareModel> datalist = new ArrayList<>();
    public void setModellist(ArrayList<SubmitPrepareModel> data) {
        datalist = data;
    }

    public SubmitPrepareAdapter(Context context, ArrayList<SubmitPrepareModel> dataList, ShowPrepareSnsListener l1, SelectPrepareListener l2,ChangePrepareListener l3) {
        this.context = context;
        this.datalist = dataList;
        this.listener = l1;
        this.selectlistener = l2;
        this.changePrepareListener = l3;
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        SubmitPrepareAdapter.ViewHolder tag = null;
        final SubmitPrepareModel item = datalist.get(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.listitem_submitprepare, null);
            convertView.setTag(tag);
        } else {
            tag = (SubmitPrepareAdapter.ViewHolder) convertView.getTag();
        }
        tag = new SubmitPrepareAdapter.ViewHolder();
        tag.tv_modelname = convertView.findViewById(R.id.tv_modelname);
        tag.tv_modelname.setText(item.getModelname());
        tag.tv_modelspec = convertView.findViewById(R.id.tv_modelspec);
        tag.tv_modelspec.setText(item.getModelspec());
        tag.btn_select = convertView.findViewById(R.id.btn_select);
        tag.btn_select.setSelected(item.getIsChoose());
        tag.btn_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectlistener != null){
                    selectlistener.SelectPrepare(item.getModelcode(),!item.getIsChoose());
                }
            }
        });
        tag.tv_pos = convertView.findViewById(R.id.tv_pos);
        tag.tv_pos.setText(item.getPosition());
        tag.nosnsview = convertView.findViewById(R.id.nosnsview);
        tag.edt_inputneedout = convertView.findViewById(R.id.edt_inputneedout);
        tag.clear = convertView.findViewById(R.id.view_clear);
        tag.clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(changePrepareListener != null){
                    changePrepareListener.ChangeNeedcount(item.getModelcode());
                }
            }
        });
        //以下代码会出错
//        tag.edt_inputneedout.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
////                if(s.length()>0) {
////                    item.setNeedcount(Integer.valueOf(s.toString()));
////                    if(changePrepareListener != null){
////                        changePrepareListener.ChangeNeedcount(item.getModelcode(),Integer.valueOf(s.toString()));
////                    }
////                }
////                else{
////                    item.setNeedcount(0);
////                    if(changePrepareListener != null){
////                        changePrepareListener.ChangeNeedcount(item.getModelcode(),0);
////                    }
////                }
//
//            }
//        });
        tag.snsview = convertView.findViewById(R.id.snsview);
        tag.tv_needout = convertView.findViewById(R.id.tv_needout);
        tag.tv_scanout =  convertView.findViewById(R.id.tv_scanout);
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
        if (item.getIsSN()) {
            tag.snsview.setVisibility(View.VISIBLE);
            tag.nosnsview.setVisibility(View.GONE);
            tag.tv_needout.setText(Integer.toString(item.getNeedcount()));
            tag.tv_scanout.setText(Integer.toString(item.getSnscount()));
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
            tag.snsview.setVisibility(View.GONE);
            tag.nosnsview.setVisibility(View.VISIBLE);
            tag.tv_show.setVisibility(View.GONE);
            tag.edt_inputneedout.setText(Integer.toString(item.getNeedcount()));
        }

        return convertView;
    }

    private class ViewHolder {
        TextView tv_modelname;
        TextView tv_modelspec;
        Button btn_select;
        TextView tv_pos;
View clear;

        RelativeLayout nosnsview;
        EditText edt_inputneedout;
        RelativeLayout snsview;
        TextView tv_needout;
        TextView tv_scanout;
        TextView tv_show;//展开
        TextView tv_showdetail;//展开详细内容
    }

}
