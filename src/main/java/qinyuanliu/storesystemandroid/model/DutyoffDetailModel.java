package qinyuanliu.storesystemandroid.model;

/**
 * Created by qinyuanliu on 2018/10/22.
 */

public class DutyoffDetailModel {

    public DutyoffDetailModel(String mname, String mcode, String mspec, String mcount, int count, String detailtext, String r, boolean status, String header, String route, String step){
modelname = mname;
        modelcode = mcode;
        modelspec = mspec;
        modelcount = mcount;
        detail = detailtext;
        detailcount = count;
        remark = r;
        isChoose = status;
        sectionHeader = header;
        routecode = route;
        stepcode = step;
    }

    private boolean isChoose=false;//status 0:表示未确认 1：表示确认
    public void setIsChoose(boolean s){isChoose = s;}
    public boolean getIsChoose(){return isChoose;}

    private String modelname = "";
    public void setModelname(String mn){modelname = mn;}
    public String getModelname(){return modelname;}

    private String modelcode = "";
    public void setModelcode(String mc){modelcode = mc;}
    public String getModelcode(){return modelcode;}

    private String modelspec = "";
    public void setModelspec(String ms){modelspec = ms;}
    public String getModelspec(){return modelspec;}

    private String modelcount = "";
    public void setModelcount(String mc){modelcount = mc;}
    public String getModelcount(){return modelcount;}

    private String remark = "";
    public void setRemark(String r){remark = r;}
    public String getRemark(){return remark;}

    private String detail = "";
    public void setDetail(String d){detail = d;}
    public String getDetail(){return detail;}

    private int detailcount = 0;
    public void setDetailcount(int c){detailcount = c;}
    public int getDetailcount(){return detailcount;}

    private boolean isShow = false;
    public void setIsShow(boolean isshow){isShow = isshow;}
    public boolean getIsShow(){return isShow;}

public String sectionHeader = "";
    public String routecode = "";
    public String stepcode = "";


}
