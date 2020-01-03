package qinyuanliu.storesystemandroid.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

import qinyuanliu.storesystemandroid.R;
import qinyuanliu.storesystemandroid.listener.ClickChangeAccountListener;
import qinyuanliu.storesystemandroid.model.AccountModel;

/**
 * Created by qinyuanliu on 2019/2/17.
 */

public class AccountAdapter extends BaseAdapter {
    private Context context;
    private ClickChangeAccountListener listener;
    private ArrayList<AccountModel> accountlist = new ArrayList<>();
    public void setAccountlist(ArrayList<AccountModel> data) {
        accountlist = data;
    }

    public AccountAdapter (Context context, ArrayList<AccountModel> List, ClickChangeAccountListener l) {
        this.context = context;
        this.accountlist = List;
        this.listener = l;
    }

    @Override
    public int getCount() {
        return accountlist.size();
    }

    @Override
    public Object getItem(int position) {
        return  accountlist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        AccountAdapter.ViewHolder tag = null;
        final AccountModel account = accountlist.get(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.listitem_account, null);
            convertView.setTag(tag);
        }else {
            tag = (AccountAdapter.ViewHolder) convertView.getTag();
        }

        tag = new AccountAdapter.ViewHolder();
        tag.btn_select = (Button)convertView.findViewById(R.id.btn_select);
        if(account.isSelected){
            tag.btn_select.setVisibility(View.VISIBLE);
        }
        else{
            tag.btn_select.setVisibility(View.INVISIBLE);
        }
        tag.tv_account = (TextView) convertView.findViewById(R.id.tv_account);
        tag.tv_nick = (TextView) convertView.findViewById(R.id.tv_nick);
        tag.tv_store = (TextView) convertView.findViewById(R.id.tv_store);
        tag.tv_account.setText(account.loginaccount);
        tag.tv_store.setText(account.storename);
        tag.tv_nick.setText("("+account.nick+")");
        tag.tv_pwd = (TextView)convertView.findViewById(R.id.tv_pwd);
        tag.tv_pwd.setText("密码:"+account.loginpwd);
        tag.accountview = (RelativeLayout)convertView.findViewById(R.id.accountview);
        tag.accountview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null){
                    listener.ClickChange(account);
                }
            }
        });

        return convertView;
    }


    private class ViewHolder {
        TextView tv_account;
        TextView tv_nick;
        TextView tv_store;
        RelativeLayout accountview;
        Button btn_select;
        TextView tv_pwd;
    }

}
