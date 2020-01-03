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
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

import qinyuanliu.storesystemandroid.R;

import qinyuanliu.storesystemandroid.listener.RemarkListener;
import qinyuanliu.storesystemandroid.listener.SelectDutyoffDetailListener;
import qinyuanliu.storesystemandroid.listener.ShowDutyoffDetailListener;
import qinyuanliu.storesystemandroid.model.DutyoffDetailModel;
import qinyuanliu.storesystemandroid.util.DensityUtil;

/**
 * Created by qinyuanliu on 2018/10/22.
 */

public class OffdutyDetailAdapter extends BaseAdapter {
    private Context context;
    private ShowDutyoffDetailListener showListener;
    private RemarkListener remarkListener;
    private SelectDutyoffDetailListener selectListener;
    private ArrayList<DutyoffDetailModel> datalist = new ArrayList<>();

    public void setDatalist(ArrayList<DutyoffDetailModel> list) {
        datalist = list;
    }

    public OffdutyDetailAdapter(Context context, ArrayList<DutyoffDetailModel> dataList, ShowDutyoffDetailListener l1, RemarkListener l2, SelectDutyoffDetailListener l3) {
        this.context = context;
        this.datalist = dataList;
        this.showListener = l1;
        this.remarkListener = l2;
        this.selectListener = l3;
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
        OffdutyDetailAdapter.ViewHolder tag = null;
        final DutyoffDetailModel item = datalist.get(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.listitem_dutyoff, null);

            convertView.setTag(tag);
        } else {
            tag = (OffdutyDetailAdapter.ViewHolder) convertView.getTag();
        }
        tag = new OffdutyDetailAdapter.ViewHolder();
        tag.tv_modelname = (TextView) convertView.findViewById(R.id.tv_modelname);
        tag.tv_modelspec = (TextView) convertView.findViewById(R.id.tv_modelspec);
        tag.tv_modelcount = (TextView) convertView.findViewById(R.id.tv_modelcount);
        tag.edt_remark = (EditText) convertView.findViewById(R.id.edt_remark);
        tag.tv_showdetail = (TextView) convertView.findViewById(R.id.tv_showdetail);
        tag.btn_choose = (Button) convertView.findViewById(R.id.btn_choose);
        tag.tv_show = (TextView) convertView.findViewById(R.id.tv_show);
        tag.tv_modelname.setText(item.getModelname());
        tag.tv_modelspec.setText(item.getModelspec());
        tag.tv_modelcount.setText(item.getModelcount());
        if(item.getIsShow()){
            tag.tv_show.setText("收起");
        }
        else{
            tag.tv_show.setText("展开");
        }
        tag.edt_remark.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                item.setRemark(s.toString());
                if(remarkListener != null){
                    remarkListener.EditRemark(position, s.toString(),item.sectionHeader);
                }
            }
        });
        tag.tv_show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean oldflag = item.getIsShow();
                if (showListener != null) {
                    showListener.ShowDutyoffDetail(position,!oldflag,item.sectionHeader);
                }

            }
        });
        tag.btn_choose.setSelected(item.getIsChoose());
        tag.btn_choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean oldflag = item.getIsChoose();
                if (selectListener != null) {
                   selectListener.SelectDutyoffDetail(position,!oldflag,item.sectionHeader);
                }

            }
        });
        ViewGroup.LayoutParams lp = tag.tv_showdetail.getLayoutParams();
        if(item.getIsShow()){
            lp.height = DensityUtil.dip2px(context,18*item.getDetailcount());
            tag.tv_showdetail.setLayoutParams(lp);
            tag.tv_showdetail.setVisibility(View.VISIBLE);
            tag.tv_showdetail.setText(item.getDetail());
        }
        else{
            tag.tv_showdetail.setVisibility(View.GONE);
        }
        return convertView;
    }

    private class ViewHolder {
        Button btn_choose;//勾选确认按钮
        TextView tv_modelname;
        TextView tv_modelspec;
        TextView tv_modelcount;
       TextView tv_show;//展开
        EditText edt_remark;
        TextView tv_showdetail;//展开详细内容
    }

}
