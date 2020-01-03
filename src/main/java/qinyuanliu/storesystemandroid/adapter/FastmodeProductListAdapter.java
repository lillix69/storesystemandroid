package qinyuanliu.storesystemandroid.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import qinyuanliu.storesystemandroid.R;
import qinyuanliu.storesystemandroid.listener.ClickProductListener;
import qinyuanliu.storesystemandroid.model.ProductModel;

/**
 * Created by qinyuanliu on 2018/7/26.
 */

public class FastmodeProductListAdapter extends BaseAdapter {
    private Context context;
    private ClickProductListener productListener;
    private ArrayList<ProductModel> datalist = new ArrayList<>();

    public void setDatalist(ArrayList<ProductModel> list) {
        datalist = list;
    }

    public FastmodeProductListAdapter(Context context, ArrayList<ProductModel> dataList, ClickProductListener l1) {
        this.context = context;
        this.datalist = dataList;
        this.productListener = l1;
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
        FastmodeProductListAdapter.ViewHolder tag = null;
        final ProductModel product = datalist.get(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.listitem_product_fastmode, null);
            convertView.setTag(tag);
        } else {
            tag = (FastmodeProductListAdapter.ViewHolder) convertView.getTag();
        }
        tag = new FastmodeProductListAdapter.ViewHolder();
        tag.tv_index = (TextView)convertView.findViewById(R.id.tv_index);
        tag.tv_index.setText(Integer.toString(position+1) + ".");
        tag.tv_position = (TextView) convertView.findViewById(R.id.tv_position);
        tag.tv_modelname = (TextView) convertView.findViewById(R.id.tv_name);
        tag.tv_storecount = (TextView) convertView.findViewById(R.id.tv_storecount);
        tag.tv_typename = (TextView) convertView.findViewById(R.id.tv_typename);
        tag.tv_spec = (TextView) convertView.findViewById(R.id.tv_spec);

        tag.tv_position.setText(product.getPosition());
        tag.tv_modelname.setText(product.getTitle());
        tag.tv_storecount.setText(Integer.toString(product.getStoreCount()));
        tag.tv_typename.setText(product.getTypename());
        tag.tv_spec.setText(product.getSpec());

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(productListener != null){
                    productListener.onClickProduct(product.getModelCode(),product.getTitle(),product.getSn());
                }
            }
        });

        return convertView;
    }

    private class ViewHolder {
        TextView tv_position;
        TextView tv_spec;
        TextView tv_modelname;
        TextView tv_typename;
        TextView tv_storecount;
        TextView tv_index;
    }
}
