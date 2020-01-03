package qinyuanliu.storesystemandroid.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import qinyuanliu.storesystemandroid.R;
import qinyuanliu.storesystemandroid.adapter.PurchaseAdapter;
import qinyuanliu.storesystemandroid.common.Session;
import qinyuanliu.storesystemandroid.http.Codes;
import qinyuanliu.storesystemandroid.http.SCExceptionCodeList;
import qinyuanliu.storesystemandroid.http.SCResponseListener;
import qinyuanliu.storesystemandroid.http.SCResult;
import qinyuanliu.storesystemandroid.http.SCSDK;
import qinyuanliu.storesystemandroid.listener.RefreshTokenListener;
import qinyuanliu.storesystemandroid.listener.SelectPurchaseListener;
import qinyuanliu.storesystemandroid.util.DateUtil;

/**
 * Created by qinyuanliu on 2019/4/13.
 */

public class FragmentPurchase extends Fragment {
    private TextView tv_begindate;
    private TextView tv_enddate;
    private TextView tv_search;
    private TextView tv_add;
    private ListView listview_purchase;
    private EditText edt_keyword;
    private TextView tv_nopower;
    private RelativeLayout filterview;

    private PurchaseAdapter purchaseAdapter;
    private ArrayList<SCResult.PurchaseStoreOrderInfo> datalist;

    private Calendar calendarStart = Calendar.getInstance();
    private Calendar calendarEnd = Calendar.getInstance();
    private int startYear = 0;
    private int startMonth = 0;
    private int startDay = 0;
    private int endYear = 0;
    private int endMonth = 0;
    private int endDay = 0;
    private String begindate = "";
    private String enddate = "";
    private String keyword = "";

    public static FragmentPurchase newInstance() {

        FragmentPurchase fragmentPurchase = new FragmentPurchase();

        return fragmentPurchase;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initData();
    }

    @Override
    public void onResume() {
        super.onResume();
        Session.CheckRefreshToken(new RefreshTokenListener() {
            @Override
            public void RefreshTokenResult(final int resultcode) {

                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (resultcode == Codes.Code_Success) {
                                SCSDK.getInstance().GetAccountPower(Session.getInstance().getShopCode(), Session.getInstance().getToken(), new SCResponseListener() {
                                    @Override
                                    public void onResult(Object result) {
                                        final SCResult.AccountPowerResult res = (SCResult.AccountPowerResult) result;
                                        if (getActivity() != null) {
                                            getActivity().runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Session.getInstance().setMenupower(res.power);
                                                    if (Session.getInstance().getMenupower() == 0 || Session.getInstance().getMenupower() == 2) {
                                                        filterview.setVisibility(View.VISIBLE);
                                                        listview_purchase.setVisibility(View.VISIBLE);
                                                        tv_nopower.setVisibility(View.GONE);
                                                    } else {
                                                        tv_nopower.setVisibility(View.VISIBLE);
                                                        filterview.setVisibility(View.GONE);
                                                        listview_purchase.setVisibility(View.GONE);
                                                    }
                                                    LoadPurchaseList();
                                                }
                                            });
                                        }

                                    }

                                    @Override
                                    public void onError(int code, final String errormsg) {
                                        if (getActivity() != null) {
                                            getActivity().runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                      Toast.makeText(getContext(), errormsg, Toast.LENGTH_SHORT).show();
//                                                    if (Session.getInstance().getMenupower() == 0 || Session.getInstance().getMenupower() == 2) {
//                                                        filterview.setVisibility(View.VISIBLE);
//                                                        listview_purchase.setVisibility(View.VISIBLE);
//                                                        tv_nopower.setVisibility(View.GONE);
//                                                    } else {
//                                                        tv_nopower.setVisibility(View.VISIBLE);
//                                                        filterview.setVisibility(View.GONE);
//                                                        listview_purchase.setVisibility(View.GONE);
//                                                    }
//                                                    LoadPurchaseList();
                                                }
                                            });
                                        }
                                    }
                                });
                            } else {
                                Toast.makeText(getContext(), SCExceptionCodeList.getExceptionMsg(resultcode), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_purchase, container, false);
        tv_begindate = (TextView) v.findViewById(R.id.tv_begindate);
        tv_begindate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClickStartDate();
            }
        });
        tv_begindate.setText(begindate);
        tv_enddate = (TextView) v.findViewById(R.id.tv_enddate);
        tv_enddate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClickEndDate();
            }
        });
        tv_enddate.setText(enddate);
        tv_search = (TextView) v.findViewById(R.id.tv_search);
        tv_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClickSearch();
            }
        });
        tv_add = (TextView) v.findViewById(R.id.tv_add);
        tv_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClickAdd();
            }
        });
        listview_purchase = (ListView) v.findViewById(R.id.listview_purchase);
        filterview = (RelativeLayout) v.findViewById(R.id.filterview);
        tv_nopower = (TextView) v.findViewById(R.id.tv_nopower);
        edt_keyword = (EditText) v.findViewById(R.id.edt_keyword);
        edt_keyword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                keyword = s.toString();
            }
        });
        return v;
    }

    private void initData() {
        calendarStart.setTime(new Date());
        calendarStart.add(Calendar.DATE, -30);
        startYear = calendarStart.get(Calendar.YEAR);
        startMonth = calendarStart.get(Calendar.MONTH);
        startDay = calendarStart.get(Calendar.DATE);
        begindate = DateUtil.GetDateString(startYear, startMonth + 1, startDay);

        calendarEnd.setTime(new Date());
        endYear = calendarEnd.get(Calendar.YEAR);
        endMonth = calendarEnd.get(Calendar.MONTH);
        endDay = calendarEnd.get(Calendar.DATE);
        enddate = DateUtil.GetDateString(endYear, endMonth + 1, endDay);
    }

    private void ClickStartDate() {
        View view = View.inflate(getContext(), R.layout.date_picker, null);
        final DatePicker datePicker = (DatePicker) view.findViewById(R.id.new_act_date_picker);
        datePicker.init(startYear, startMonth, startDay, null);
        // Build DateTimeDialog
        AlertDialog.Builder begindatetimeBuilder = new AlertDialog.Builder(getContext());
        begindatetimeBuilder.setView(view);
        begindatetimeBuilder.setTitle("选择查询采购开始时间");
        begindatetimeBuilder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startYear = datePicker.getYear();
                startMonth = datePicker.getMonth();
                startDay = datePicker.getDayOfMonth();
                String str = DateUtil.GetDateString(startYear, startMonth + 1, startDay);
                begindate = str;
                tv_begindate.setText(str);
            }
        });
        begindatetimeBuilder.show();
    }

    private void ClickEndDate() {
        View view = View.inflate(getContext(), R.layout.date_picker, null);
        final DatePicker datePicker = (DatePicker) view.findViewById(R.id.new_act_date_picker);
        datePicker.init(endYear, endMonth, endDay, null);

        AlertDialog.Builder enddatetimeBuilder = new AlertDialog.Builder(getContext());
        enddatetimeBuilder.setView(view);
        enddatetimeBuilder.setTitle("选择查询采购结束时间");
        enddatetimeBuilder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                endYear = datePicker.getYear();
                endMonth = datePicker.getMonth();
                endDay = datePicker.getDayOfMonth();
                String str = DateUtil.GetDateString(endYear, endMonth + 1, endDay);
                enddate = str;
                tv_enddate.setText(str);
            }
        });
        enddatetimeBuilder.show();
    }

    private void ClickAdd() {
        if (getActivity() != null) {
            Intent add = new Intent(getActivity(), AddPurchaseActivity.class);
            getActivity().startActivity(add);
        }
    }

    private void ClickSearch() {
        LoadPurchaseList();
    }

    private void LoadPurchaseList() {
        datalist = new ArrayList<>();
        Session.CheckRefreshToken(new RefreshTokenListener() {
            @Override
            public void RefreshTokenResult(final int resultcode) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (resultcode == Codes.Code_Success) {
                            SCSDK.getInstance().GetPurchaseStoreOrders(keyword, begindate, enddate, Session.getInstance().getShopCode(), Session.getInstance().getToken(), new SCResponseListener() {
                                @Override
                                public void onResult(Object result) {
                                    final SCResult.PurchaseStoreOrderResult res = (SCResult.PurchaseStoreOrderResult) result;
                                    if (getActivity() != null) {
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (res.orders != null && res.orders.size() > 0) {
                                                    for (SCResult.PurchaseStoreOrderInfo purchase :
                                                            res.orders) {
                                                        datalist.add(purchase);
                                                    }
                                                }
                                                refreshListview();
                                            }
                                        });
                                    }
                                }

                                @Override
                                public void onError(int code, final String errormsg) {
                                    if (getActivity() != null) {
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                refreshListview();
                                                Toast.makeText(getContext(), errormsg, Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                }
                            });
                        } else {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    refreshListview();
                                    Toast.makeText(getContext(), SCExceptionCodeList.getExceptionMsg(resultcode), Toast.LENGTH_SHORT).show();

                                }
                            });
                        }
                    }
                });
            }
        });
    }

    private void refreshListview() {
        if (purchaseAdapter == null) {
            purchaseAdapter = new PurchaseAdapter(getContext(), datalist, new SelectPurchaseListener() {
                @Override
                public void SelectPurchase(String purchasecode) {
                    Intent detail = new Intent(getActivity(), PurchaseDetailActivity.class);
                    detail.putExtra("purchasecode", purchasecode);
                    startActivity(detail);
                }
            });
            listview_purchase.setAdapter(purchaseAdapter);
        } else {
            purchaseAdapter.setModellist(datalist);
            purchaseAdapter.notifyDataSetChanged();
        }
    }
}
