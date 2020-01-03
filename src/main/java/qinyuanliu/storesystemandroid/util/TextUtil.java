package qinyuanliu.storesystemandroid.util;

/**
 * Created by qinyuanliu on 2019/4/21.
 */

public class TextUtil {
    public static boolean isEmpty(String str){
        if(str != null && str.length()>0){
            return false;
        }
        return true;
    }
}
