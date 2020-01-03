package qinyuanliu.storesystemandroid.http;

/**
 * Created by lillix on 6/26/17.
 */
public class SCException extends Exception {
    private static final long serialVersionUID = -8404439387685590535L;
    private String errormsg;

    public SCException() {
    }

    public SCException(Exception e, String msg) {
        super(e);
        this.setErrormsg(msg);
    }

    public SCException(String msg) {
        super(msg);
        this.setErrormsg(msg);
    }

    public String getErrormsg() {
        return this.errormsg;
    }

    public void setErrormsg(String errormsg) {
        this.errormsg = errormsg;
    }
}
