package qinyuanliu.storesystemandroid.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by qinyuanliu on 2019/4/23.
 */

public class AddPurchaseModel {

    public AddPurchaseModel(String materialname, String l, String code, String modelname, boolean ismin, String spec,String ordercode, String s,String p, ArrayList<String> sns,String count, String scode) {
        modelCode = code;
        modelName = modelname;
        isMin = ismin;
        this.spec = spec;
        orderCode = ordercode;
        supply = s;
        cost= p;
        sn = sns;
        storeCount = count;
        mname = materialname;
        lineno = l;
    }

    private String mname = "";
    public void setMname(String m){mname = m;}
    public String getMname(){return mname;}

    private String lineno= "";
    public void setLineno(String l){lineno = l;}
    public String getLineno(){return lineno;}

private String supply = "";
    public void setSupply(String s){supply = s;}
    public String getSupply(){return supply;}

    private String supplyCode = "";
    public void setSupplyCode(String scode){supplyCode = scode;}
    public String getSupplyCode(){return supplyCode;}

    private String cost = "";
    public void setCost(String c){cost = c;}
    public String getCost(){return cost;}

    private String spec = "";
    public void setSpec(String s){spec = s;}
    public String getSpec(){return spec;}

    private ArrayList<String> sn = new ArrayList<>();

    public void setSn(ArrayList<String> ss) {
        sn = ss;
    }

    public ArrayList<String> getSn() {
        return sn;
    }

    private String orderCode = "";

    public void setOrderCode(String o) {
        orderCode = o;
    }

    public String getOrderCode() {
        return orderCode;
    }

    private String modelCode = "";

    public void setModelCode(String p) {
        modelCode = p;
    }

    public String getModelCode() {
        return modelCode;
    }

    private String modelName = "";

    public void setModelName(String t) {
        modelName = t;
    }

    public String getModelName() {
        return modelName;
    }


    private String storeCount = "";

    public void setStoreCount(String count) {
        storeCount = count;
    }

    public String getStoreCount() {
        return storeCount;
    }

    private boolean isMin = false;
    public void setIsMin(boolean flag){isMin = flag;}
    public boolean getIsMin(){return isMin;}
}
