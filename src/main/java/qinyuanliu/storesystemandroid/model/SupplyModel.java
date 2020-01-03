package qinyuanliu.storesystemandroid.model;

/**
 * Created by qinyuanliu on 2019/4/22.
 */

public class SupplyModel {
    public SupplyModel(String name, String code){
        supplyName = name;
        supplyCode = code;
    }

    public String supplyName = "";
    public boolean isSelected = false;
    public String supplyCode= "";
}
