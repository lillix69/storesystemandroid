package qinyuanliu.storesystemandroid.model;

/**
 * Created by qinyuanliu on 2018/12/7.
 */

public class PrepareDetailModel {
    public PrepareDetailModel(String mname, String mcode, String mspec, String mcount, String snsdetail, int scount, boolean issns) {
        modelname = mname;
        modelcode = mcode;
        modelspec = mspec;
        modelcount = mcount;
        isSN = issns;
        sns = snsdetail;
        snscount = scount;
    }


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

    private String modelcount = "";

    public void setModelcount(String mc) {
        modelcount = mc;
    }

    public String getModelcount() {
        return modelcount;
    }

    private boolean isSN = false;

    public void setIsSN(boolean is) {
        isSN = is;
    }

    public boolean getIsSN() {
        return isSN;
    }

    private String sns = "";

    public void setSns(String sns1) {
        sns = sns1;
    }

    public String getSns() {
        return sns;
    }

    private int snscount = 0;

    public void setSnscount(int count) {
        snscount = count;
    }

    public int getSnscount() {
        return snscount;
    }

    private boolean isShow = false;

    public void setIsShow(boolean isshow) {
        isShow = isshow;
    }

    public boolean getIsShow() {
        return isShow;
    }
}
