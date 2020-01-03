package qinyuanliu.storesystemandroid.model;

/**
 * Created by lillix on 5/28/18.
 */
public class ProductModel {
    public ProductModel(String id, String modelname, String p, String s, int count, String t, String ss) {
        modelCode = id;
        title = modelname;
        position = p;
        spec = s;
        storeCount = count;
        typename = t;
        sn = ss;
    }

private Boolean ismin = false;
    private void setIsmin(Boolean flag){ismin = flag;}
    private Boolean getIsmin(){return ismin;}


    private String sn = "";

    public void setSn(String ss) {
        sn = ss;
    }

    public String getSn() {
        return sn;
    }

    private String modelCode = "";

    public void setModelCode(String p) {
        modelCode = p;
    }

    public String getModelCode() {
        return modelCode;
    }

    private String title = "";

    public void setTitle(String t) {
        title = t;
    }

    public String getTitle() {
        return title;
    }

    private String spec = "";

    public void setSpec(String s) {
        spec = s;
    }

    public String getSpec() {
        return spec;
    }

    private String position = "";

    public void setPosition(String p) {
        position = p;
    }

    public String getPosition() {
        return position;
    }

    private String typename = "";

    public void setTypename(String t) {
        typename = t;
    }

    public String getTypename() {
        return typename;
    }

    private int storeCount = 0;

    public void setStoreCount(int count) {
        storeCount = count;
    }

    public int getStoreCount() {
        return storeCount;
    }
}
