package qinyuanliu.storesystemandroid.model;

/**
 * Created by qinyuanliu on 2018/11/20.
 */

public class NoticeModel {
    public NoticeModel(String code, String title, String content, String time){
        noticecode = code;
        noticecontent = content;
        noticetime = time;
        noticetitle = title;
    }
    public String noticecode = "";
    public String noticetitle = "";
    public String noticetime = "";
    public String noticecontent = "";
    public boolean isSelected = false;
}
