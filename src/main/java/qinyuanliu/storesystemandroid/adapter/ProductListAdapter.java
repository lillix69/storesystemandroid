package qinyuanliu.storesystemandroid.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import qinyuanliu.storesystemandroid.R;
import qinyuanliu.storesystemandroid.listener.ClickInStoreListener;
import qinyuanliu.storesystemandroid.listener.ClickLookStoreListener;
import qinyuanliu.storesystemandroid.listener.ClickOutStoreListener;
import qinyuanliu.storesystemandroid.listener.ClickPositionListener;
import qinyuanliu.storesystemandroid.listener.ClickProductListener;
import qinyuanliu.storesystemandroid.model.ProductModel;

/**
 * Created by lillix on 5/28/18.
 */
public class ProductListAdapter extends BaseAdapter {
    private Context context;
    private ClickProductListener productListener;
    private ClickInStoreListener inListener;
    private ClickOutStoreListener outListener;
    private ClickLookStoreListener lookListener;
    private ClickPositionListener poslistener;
    private ArrayList<ProductModel> datalist = new ArrayList<>();

    public void setDatalist(ArrayList<ProductModel> list) {
        datalist = list;
    }

    public ProductListAdapter(Context context, ArrayList<ProductModel> dataList, ClickProductListener l1, ClickInStoreListener l2, ClickOutStoreListener l3, ClickLookStoreListener l4, ClickPositionListener l5) {
        this.context = context;
        this.datalist = dataList;
        this.productListener = l1;
        this.inListener = l2;
        this.outListener = l3;
        this.lookListener = l4;
        this.poslistener = l5;
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
        final ProductModel product = datalist.get(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.listitem_product, null);
            convertView.setTag(tag);
        } else {
            tag = (ViewHolder) convertView.getTag();
        }
        tag = new ViewHolder();
        tag.tv_index = (TextView)convertView.findViewById(R.id.tv_index);
        tag.tv_index.setText(Integer.toString(position+1) + ".");
        tag.tv_position = (TextView) convertView.findViewById(R.id.tv_position);
        tag.tv_modelname = (TextView) convertView.findViewById(R.id.tv_name);
        tag.tv_storecount = (TextView) convertView.findViewById(R.id.tv_storecount);
        tag.tv_typename = (TextView) convertView.findViewById(R.id.tv_typename);
        tag.tv_spec = (TextView) convertView.findViewById(R.id.tv_spec);
        tag.btn_in = (TextView) convertView.findViewById(R.id.btn_in);
        tag.btn_out = (TextView) convertView.findViewById(R.id.btn_out);
        tag.btn_look = (TextView) convertView.findViewById(R.id.btn_look);
        tag.btn_pos = (TextView)convertView.findViewById(R.id.btn_pos);
        tag.tv_position.setText(product.getPosition());
        tag.tv_modelname.setText(product.getTitle());
        tag.tv_storecount.setText(Integer.toString(product.getStoreCount()));
        tag.tv_typename.setText(product.getTypename());
        tag.tv_spec.setText(product.getSpec());

        tag.btn_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (inListener != null) {
                  inListener.onClickInStore(product.getModelCode());
                }
            }
        });
        tag.btn_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (outListener != null) {
                    outListener.onClickOutStore(product.getModelCode());
                }
            }
        });
        tag.btn_look.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lookListener != null) {
                    lookListener.onClickLookStore(product.getModelCode());
                }
            }
        });
        tag.btn_pos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(poslistener != null){
                    poslistener.ClickPosition(product.getModelCode());
                }
            }
        });
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(productListener != null){
                    productListener.onClickProduct(product.getModelCode(),null,"");
                    // Toast.makeText(MainviewActivity.this, errormsg, Toast.LENGTH_SHORT).show();

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
TextView btn_pos;
        TextView btn_in;
        TextView btn_out;
        TextView btn_look;
    }
}