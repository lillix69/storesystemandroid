package qinyuanliu.storesystemandroid.model;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by qinyuanliu on 2019/7/19.
 */

public class AddSaleModel {
    public AddSaleModel(String code, String modelname,  String guige) {
        modelCode = code;
        modelName = modelname;
        spec = guige;
    }

    private String spec = "";
    public void setSpec(String s){spec = s;}
    public String getSpec(){return spec;}

    private String cost = "";
    public void setCost(String c){cost = c;}
    public String getCost(){return cost;}



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




    private String saleCount = "";

    public void setSaleCount(String count) {
        saleCount = count;
    }

    public String getSaleCount() {
        return saleCount;
    }




}
