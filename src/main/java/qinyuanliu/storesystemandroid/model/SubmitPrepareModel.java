package qinyuanliu.storesystemandroid.model;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by qinyuanliu on 2018/12/10.
 */

public class SubmitPrepareModel {
    public SubmitPrepareModel (String mname, String mcode, String mspec, boolean issns, String pos, int ncount) {
        modelname = mname;
        modelcode = mcode;
        modelspec = mspec;
        isSN = issns;
        position = pos;
        needcount = ncount;
    }
private String position = "";
    public String getPosition(){return position;}
    public void setPosition(String p){position = p;}


    private String modelname = "";

    public void setModelname(String mn) {
        modelname = mn;
    }

    public String getModelname() {
        return modelname;
    }

    private String modelcode = "";

    public void setModelcode(String mc) {
        modelcode = mc;
    }

    public String getModelcode() {
        return modelcode;
    }

    private String modelspec = "";

    public void setModelspec(String ms) {
        modelspec = ms;
    }

    public String getModelspec() {
        return modelspec;
    }

    private boolean isSN = false;

    public void setIsSN(boolean is) {
        isSN = is;
    }

    public boolean getIsSN() {
        return isSN;
    }


    private int needcount = 0;

    public void setNeedcount(int count) {
        needcount = count;
    }

    public int getNeedcount() {
        return needcount;
    }


    private boolean isChoose = true;

    public void setIsChoose(boolean s) {
        isChoose = s;
    }
    public boolean getIsChoose() {
        return isChoose;
    }
    private boolean isShow = false;

    public void setIsShow(boolean isshow) {
        isShow = isshow;
    }

    public boolean getIsShow() {
        return isShow;
    }

    private String sns = "";

    public void setSns(String sns1) {
        sns = sns1;
    }

    public String getSns() {
        return sns;
    }

    private HashSet<String> snslist = new HashSet<>();
    public HashSet<String> getSnslist(){return snslist;}
    public void setSnslist(HashSet<String> list){snslist = list;}

    private int snscount = 0;

    public void setSnscount(int count) {
        snscount = count;
    }

    public int getSnscount() {
        return snscount;
    }
}
