package qinyuanliu.storesystemandroid.model;

/**
 * Created by qinyuanliu on 2019/9/10.
 */

public class PurModel {
    public PurModel(String ordercode, String date, String num, String price, String r){
        orderCode = ordercode;
        orderDate = date;
        purchaseNum = num;
        totalPrice = price;
        remark = r;
    }
    public String  orderCode = "";
    public String  orderDate = "";
    public String purchaseNum;
    public String totalPrice;
    public String remark;
    public boolean isSelected = false;
}
