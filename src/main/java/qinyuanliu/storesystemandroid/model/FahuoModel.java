package qinyuanliu.storesystemandroid.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by qinyuanliu on 2019/8/12.
 */

public class FahuoModel {
    public FahuoModel(String no,String code, String name, boolean ismin, String guige,String salecount, String storecount, int ismeal, String mealproductname) {
        modelCode = code;
        modelName = name;
        isMin = ismin;
        spec = guige;
        saleCount = salecount;
        storeCount = storecount;
        lino = no;
        mealname = mealproductname;
        mealproduct = ismeal;
    }

    private int mealproduct = 0;
    public void setMealproduct(int meal){mealproduct = meal;}
    public int getMealproduct(){return mealproduct;}
    private String mealname = "";
    public void setMealname(String mname){mealname = mname;}
    public String getMealname(){return mealname;}

private String lino = "";
    public void setLino(String no){lino = no;}
    public String getLino(){return lino;}

    private String storeCount = "";

    public void setStoreCount(String count) {
        storeCount = count;
    }

    public String getStoreCount() {
        return storeCount;
    }

    public String saleCount = "";
    public String getSaleCount(){return saleCount;}
    public void setSaleCount(String sale){saleCount = sale;}

    private String spec = "";
    public void setSpec(String s){spec = s;}
    public String getSpec(){return spec;}

    private String cost = "";
    public void setCost(String c){cost = c;}
    public String getCost(){return cost;}

    private ArrayList<String> sn = new ArrayList<>();

    public void setSn(ArrayList<String> ss) {
        sn = ss;
    }

    public ArrayList<String>getSn() {
        return sn;
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


    private String sendCount = "";

    public void setSendCount(String count) {
        sendCount = count;
    }

    public String getSendCount() {
        return sendCount;
    }


    private boolean isMin = false;
    public void setIsMin(boolean flag){isMin = flag;}
    public boolean getIsMin(){return isMin;}

}
