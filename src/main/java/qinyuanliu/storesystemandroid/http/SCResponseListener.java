package qinyuanliu.storesystemandroid.http;

/**
 * Created by lillix on 6/28/17.
 */
public interface SCResponseListener<T> {
    void onResult(T result);

    void onError(int code, String errormsg);

}