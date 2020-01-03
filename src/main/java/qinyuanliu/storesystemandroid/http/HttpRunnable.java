package qinyuanliu.storesystemandroid.http;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;

import qinyuanliu.storesystemandroid.common.Session;
import qinyuanliu.storesystemandroid.http.util.CheckSumBuilder;
import qinyuanliu.storesystemandroid.http.util.Json;
import qinyuanliu.storesystemandroid.http.util.StringUtils;


/**
 * Created by lillix on 6/27/17.
 */
public class HttpRunnable implements Runnable {
    private String sendurl;

    public void setSendurl(String tempsendurl) {
        sendurl = tempsendurl;
    }

    private String sendbody;

    public void setSendbody(String tempsendbody) {
        sendbody = tempsendbody;
    }

    private String type;

    public void setType(String temptype) {
        type = temptype;
    }

    private SCResponseListener listener;
    public void setListener(SCResponseListener templistener) {
        listener = templistener;
    }


    @Override
    public void run() {
        String responseStr = "";
        DataOutputStream dataout = null;
        BufferedReader bf = null;
        HttpURLConnection connection = null;
        try {
            URL url = new URL(sendurl);

            //  System.out.println("请求地址："+sendurl);
            // 将url 以 open方法返回的urlConnection 连接强转为HttpURLConnection连接
            // 此时cnnection只是为一个连接对象,待连接中
            connection = (HttpURLConnection) url.openConnection();
            // 设置连接输出流为true,默认false (post 请求是以流的方式隐式的传递参数)
            connection.setDoOutput(true);
            // 设置连接输入流为true
            connection.setDoInput(true);
            // 设置请求方式为post
            connection.setRequestMethod("POST");
            // post请求缓存设为false
            connection.setUseCaches(false);
            // 设置该HttpURLConnection实例是否自动执行重定向
            connection.setInstanceFollowRedirects(true);
            //如果不设置超时（timeout），在网络异常的情况下，可能会导致程序僵死而不继续往下执行。
            //设置连接主机超时（单位：毫秒）
            connection.setConnectTimeout(8000);
            //设置从主机读取数据超时（单位：毫秒）
            connection.setReadTimeout(14000);


            //头部
            connection.setRequestProperty("AppKey", Constants.APPKEY);
            String nonce = StringUtils.getRandomNonce();
            String curTime = StringUtils.getCurrentTime();
            connection.setRequestProperty("Nonce", nonce);
            connection.setRequestProperty("CurTime", curTime);
            // System.out.println("\n传递头："+this.appkey+"===="+nonce+"===="+curTime);
            connection
                    .setRequestProperty("CheckSum", CheckSumBuilder
                            .getCheckSum(Constants.APPSECRET, nonce, curTime));
            connection.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded;charset=utf-8");
           // Log.i("+++++++",sendbody);

            byte[] xmlData = sendbody.getBytes("UTF-8");
            connection.setRequestProperty("Content-Length",
                    String.valueOf(xmlData.length));

            // 建立连接
            // (请求未开始,直到connection.getInputStream()方法调用时才发起,以上各个参数设置需在此方法之前进行)
            connection.connect();
           // System.out.println("传递参数："+sendbody);
            // 创建输入输出流,用于往连接里面输出携带的参数
            dataout = new DataOutputStream(connection.getOutputStream());
            // 将参数输出到连接
            dataout.write(xmlData);

            // 输出完成后刷新并关闭流
            dataout.flush();

            if (connection.getResponseCode() == 200) {
                // 连接发起请求,处理服务器响应 (从连接获取到输入流并包装为bufferedReader)
                bf = new BufferedReader(new InputStreamReader(
                        connection.getInputStream(), "UTF-8"));
                String line;
                StringBuilder sb = new StringBuilder(); // 用来存储响应数据

                // 循环读取流,若不到结尾处
                while ((line = bf.readLine()) != null) {
                    sb.append(line);
                    // sb.append(line).append(System.getProperty("line.separator"));
                }

                responseStr = sb.toString();
               System.out.println("\n返回数据:" + responseStr);


                if (listener != null) {
                    if (type.equals("LOGIN")) {
                        SCResult.LoginAccountResult result = Json.decode(responseStr,
                                SCResult.LoginAccountResult.class);
                        if (result.code != Codes.Code_Success) {
                            listener.onError(result.code, result.errormsg);

                        } else {
                            listener.onResult(result);
                        }
                    }
                    else if (type.equals("LOGOUT")) {
                        SCResult.Result result = Json.decode(responseStr,
                                SCResult.Result.class);
                        if (result.code != Codes.Code_Success) {
                            if(result.code == Codes.Code_316){
                                Session.getInstance().setLastTokenTimestamp(0);
                            }
                                listener.onError(result.code, result.errormsg);
                        } else {
                            listener.onResult(result);
                        }
                    }
                    else if (type.equals("SEARCHMODEL")) {
                        SCResult.ModelResult result = Json.decode(responseStr,
                                SCResult.ModelResult.class);
                        if (result.code != Codes.Code_Success) {
                            if(result.code == Codes.Code_316){
                                Session.getInstance().setLastTokenTimestamp(0);
                            }
                            listener.onError(result.code, result.errormsg);
                        } else {
                            listener.onResult(result);
                        }
                    }
                    else if (type.equals("TYPELIST")) {
                        SCResult.TypeResult result = Json.decode(responseStr,
                                SCResult.TypeResult.class);
                        if (result.code != Codes.Code_Success) {
                            if(result.code == Codes.Code_316){
                                Session.getInstance().setLastTokenTimestamp(0);
                            }
                            listener.onError(result.code, result.errormsg);
                        } else {
                            listener.onResult(result);
                        }
                    }
                    else if (type.equals("STORELIST")) {
                        SCResult.StoreResult result = Json.decode(responseStr,
                                SCResult.StoreResult.class);
                        if (result.code != Codes.Code_Success) {
                            if(result.code == Codes.Code_316){
                                Session.getInstance().setLastTokenTimestamp(0);
                            }
                            listener.onError(result.code, result.errormsg);
                        } else {
                            listener.onResult(result);
                        }
                    }
                    else if (type.equals("RACKLIST")) {
                        SCResult.RackResult result = Json.decode(responseStr,
                                SCResult.RackResult.class);
                        if (result.code != Codes.Code_Success) {
                            if(result.code == Codes.Code_316){
                                Session.getInstance().setLastTokenTimestamp(0);
                            }
                            listener.onError(result.code, result.errormsg);
                        } else {
                            listener.onResult(result);
                        }
                    }
                    else if (type.equals("POSITIONLIST")) {
                        SCResult.PositionResult result = Json.decode(responseStr,
                                SCResult.PositionResult.class);
                        if (result.code != Codes.Code_Success) {
                            if(result.code == Codes.Code_316){
                                Session.getInstance().setLastTokenTimestamp(0);
                            }
                            listener.onError(result.code, result.errormsg);
                        } else {
                            listener.onResult(result);
                        }
                    }
                    else if (type.equals("SCANDETAIL")) {
                        SCResult.ModelDetailResult result = Json.decode(responseStr,
                                SCResult.ModelDetailResult.class);
                        if (result.code != Codes.Code_Success) {
                            if(result.code == Codes.Code_316){
                                Session.getInstance().setLastTokenTimestamp(0);
                            }
                            listener.onError(result.code, result.errormsg);
                        } else {
                            listener.onResult(result);
                        }
                    }
                    else if (type.equals("ADDMODEL")) {
                        SCResult.Result result = Json.decode(responseStr,
                                SCResult.Result.class);
                        if (result.code != Codes.Code_Success) {
                            if(result.code == Codes.Code_316){
                                Session.getInstance().setLastTokenTimestamp(0);
                            }
                            listener.onError(result.code, result.errormsg);
                        } else {
                            listener.onResult(result);
                        }
                    }
                    else if (type.equals("MODIFYMODEL")) {
                        SCResult.ModelDetailResult result = Json.decode(responseStr,
                                SCResult.ModelDetailResult.class);
                        if (result.code != Codes.Code_Success) {
                            if(result.code == Codes.Code_316){
                                Session.getInstance().setLastTokenTimestamp(0);
                            }
                            listener.onError(result.code,result.errormsg);
                        } else {
                            listener.onResult(result);
                        }
                    }
                    else if (type.equals("GETDETAIL")) {
                        SCResult.ModelDetailResult result = Json.decode(responseStr,
                                SCResult.ModelDetailResult.class);
                        if (result.code != Codes.Code_Success) {
                            if(result.code == Codes.Code_316){
                                Session.getInstance().setLastTokenTimestamp(0);
                            }
                            listener.onError(result.code, result.errormsg);
                        } else {
                            listener.onResult(result);
                        }
                    }
                    else if (type.equals("PRINTBARCODE")) {
                        SCResult.BarcodeResult result = Json.decode(responseStr,
                                SCResult.BarcodeResult.class);
                        if (result.code != Codes.Code_Success) {
                            if(result.code == Codes.Code_316){
                                Session.getInstance().setLastTokenTimestamp(0);
                            }
                            listener.onError(result.code, result.errormsg);
                        } else {
                            listener.onResult(result);
                        }
                    }
                    else if (type.equals("OUTSTORE")) {
                        SCResult.Result result = Json.decode(responseStr,
                                SCResult.Result.class);
                        if (result.code != Codes.Code_Success) {
                            if(result.code == Codes.Code_316){
                                Session.getInstance().setLastTokenTimestamp(0);
                            }
                            listener.onError(result.code, result.errormsg);
                        } else {
                            listener.onResult(result);
                        }
                    }
                    else if (type.equals("INSTORE")) {
                        SCResult.Result result = Json.decode(responseStr,
                                SCResult.Result.class);
                        if (result.code != Codes.Code_Success) {
                            if(result.code == Codes.Code_316){
                                Session.getInstance().setLastTokenTimestamp(0);
                            }
                            listener.onError(result.code, result.errormsg);
                        } else {
                            listener.onResult(result);
                        }
                    }
                    else if (type.equals("CHECKSTORE")) {
                        SCResult.Result result = Json.decode(responseStr,
                                SCResult.Result.class);
                        if (result.code != Codes.Code_Success) {
                            if(result.code == Codes.Code_316){
                                Session.getInstance().setLastTokenTimestamp(0);
                            }
                            listener.onError(result.code, result.errormsg);
                        } else {
                            listener.onResult(result);
                        }
                    }
                    else if (type.equals("GETREASON")) {
                        SCResult.ReasonResult result = Json.decode(responseStr,
                                SCResult.ReasonResult.class);
                        if (result.code != Codes.Code_Success) {
                            if(result.code == Codes.Code_316){
                                Session.getInstance().setLastTokenTimestamp(0);
                            }
                            listener.onError(result.code, result.errormsg);
                        } else {
                            listener.onResult(result);
                        }
                    }
                    else if (type.equals("GETSCANTYPE")) {
                        SCResult.ScanResult result = Json.decode(responseStr,
                                SCResult.ScanResult.class);
                        if (result.code != Codes.Code_Success) {
                            if(result.code == Codes.Code_316){
                                Session.getInstance().setLastTokenTimestamp(0);
                            }
                            listener.onError(result.code, result.errormsg);
                        } else {
                            listener.onResult(result);
                        }
                    }
                    else if (type.equals("BATCHTRANS")) {
                        SCResult.Result result = Json.decode(responseStr,
                                SCResult.Result.class);
                        if (result.code != Codes.Code_Success) {
                            if(result.code == Codes.Code_316){
                                Session.getInstance().setLastTokenTimestamp(0);
                            }
                            listener.onError(result.code, result.errormsg);
                        } else {
                            listener.onResult(result);
                        }
                    }

                    else if (type.equals("GETMODELDETAIL")) {
                        SCResult.ModelDetailResult result = Json.decode(responseStr,
                                SCResult.ModelDetailResult.class);
                        if (result.code != Codes.Code_Success) {
                            if(result.code == Codes.Code_316){
                                Session.getInstance().setLastTokenTimestamp(0);
                            }
                            listener.onError(result.code, result.errormsg);
                        } else {
                            listener.onResult(result);
                        }
                    }
                    else if (type.equals("GETPRODUCTDETAIL")) {
                        SCResult.ProductDetailResult result = Json.decode(responseStr,
                                SCResult.ProductDetailResult.class);
                        if (result.code != Codes.Code_Success) {
                            if(result.code == Codes.Code_316){
                                Session.getInstance().setLastTokenTimestamp(0);
                            }
                            listener.onError(result.code, result.errormsg);
                        } else {
                            listener.onResult(result);
                        }
                    }
                    else if (type.equals("GETVERSION")) {
                        SCResult.VersionResult result = Json.decode(responseStr,
                                SCResult.VersionResult.class);
                        if (result.code != Codes.Code_Success) {
                            if(result.code == Codes.Code_316){
                                Session.getInstance().setLastTokenTimestamp(0);
                            }
                            listener.onError(result.code, result.errormsg);
                        } else {
                            listener.onResult(result);
                        }
                    }
                    else if (type.equals("SEARCHMODELSNS")) {
                        SCResult.SnsResult result = Json.decode(responseStr,
                                SCResult.SnsResult.class);
                        if (result.code != Codes.Code_Success) {
                            if(result.code == Codes.Code_316){
                                Session.getInstance().setLastTokenTimestamp(0);
                            }
                            listener.onError(result.code, result.errormsg);
                        } else {
                            listener.onResult(result);
                        }
                    }
                    else if (type.equals("PRINTSN")) {
                        SCResult.Result result = Json.decode(responseStr,
                                SCResult.Result.class);
                        if (result.code != Codes.Code_Success) {
                            if(result.code == Codes.Code_316){
                                Session.getInstance().setLastTokenTimestamp(0);
                            }
                            listener.onError(result.code, result.errormsg);
                        } else {
                            listener.onResult(result);
                        }
                    }
                    else if (type.equals("GETQRCODE")) {
                        SCResult.QRcodeResult result = Json.decode(responseStr,
                                SCResult.QRcodeResult.class);
                        if (result.code != Codes.Code_Success) {
                            if(result.code == Codes.Code_316){
                                Session.getInstance().setLastTokenTimestamp(0);
                            }
                            listener.onError(result.code, result.errormsg);
                        } else {
                            listener.onResult(result);
                        }
                    }
                    else if (type.equals("CONFIRMINOUT")) {
                        SCResult.Result result = Json.decode(responseStr,
                                SCResult.Result.class);
                        if (result.code != Codes.Code_Success) {
                            if(result.code == Codes.Code_316){
                                Session.getInstance().setLastTokenTimestamp(0);
                            }
                            listener.onError(result.code, result.errormsg);
                        } else {
                            listener.onResult(result);
                        }
                    }
                    else if (type.equals("GETSUMMERY")) {
                        SCResult.SummeryResult result = Json.decode(responseStr,
                                SCResult.SummeryResult.class);
                        if (result.code != Codes.Code_Success) {
                            if(result.code == Codes.Code_316){
                                Session.getInstance().setLastTokenTimestamp(0);
                            }
                            listener.onError(result.code, result.errormsg);
                        } else {
                            listener.onResult(result);
                        }
                    }
                    else if (type.equals("GETDUTYDETAIL")) {
                        SCResult.DutyoffResult result = Json.decode(responseStr,
                                SCResult.DutyoffResult.class);
                        if (result.code != Codes.Code_Success) {
                            if(result.code == Codes.Code_316){
                                Session.getInstance().setLastTokenTimestamp(0);
                            }
                            listener.onError(result.code, result.errormsg);
                        } else {
                            listener.onResult(result);
                        }
                    }
                    else if (type.equals("SUBMITDETAIL")) {
                        SCResult.Result result = Json.decode(responseStr,
                                SCResult.Result.class);
                        if (result.code != Codes.Code_Success) {
                            if(result.code == Codes.Code_316){
                                Session.getInstance().setLastTokenTimestamp(0);
                            }
                            listener.onError(result.code, result.errormsg);
                        } else {
                            listener.onResult(result);
                        }
                    }
                    else if (type.equals("GETHISTORY")) {
                        SCResult.DutyoffHistoryResult result = Json.decode(responseStr,
                                SCResult.DutyoffHistoryResult.class);
                        if (result.code != Codes.Code_Success) {
                            if(result.code == Codes.Code_316){
                                Session.getInstance().setLastTokenTimestamp(0);
                            }
                            listener.onError(result.code, result.errormsg);
                        } else {
                            listener.onResult(result);
                        }
                    }
                    else if (type.equals("GETPROVE")) {
                        SCResult.ProveResult result = Json.decode(responseStr,
                                SCResult.ProveResult.class);
                        if (result.code != Codes.Code_Success) {
                            if(result.code == Codes.Code_316){
                                Session.getInstance().setLastTokenTimestamp(0);
                            }
                            listener.onError(result.code, result.errormsg);
                        } else {
                            listener.onResult(result);
                        }
                    }
                    else if (type.equals("CONFIRMPRODUCT")) {
                        SCResult.Result result = Json.decode(responseStr,
                                SCResult.Result.class);
                        if (result.code != Codes.Code_Success) {
                            if(result.code == Codes.Code_316){
                                Session.getInstance().setLastTokenTimestamp(0);
                            }
                            listener.onError(result.code, result.errormsg);
                        } else {
                            listener.onResult(result);
                        }
                    }
                    else if (type.equals("BINDPUSH")) {
                        SCResult.Result result = Json.decode(responseStr,
                                SCResult.Result.class);
                        if (result.code != Codes.Code_Success) {
                            if(result.code == Codes.Code_316){
                                Session.getInstance().setLastTokenTimestamp(0);
                            }
                            listener.onError(result.code, result.errormsg);
                        } else {
                            listener.onResult(result);
                        }
                    }
                    else if (type.equals("GETPUSHCOUNT")) {
                        SCResult.PushCountResult result = Json.decode(responseStr,
                                SCResult.PushCountResult.class);
                        if (result.code != Codes.Code_Success) {
                            if(result.code == Codes.Code_316){
                                Session.getInstance().setLastTokenTimestamp(0);
                            }
                            listener.onError(result.code, result.errormsg);
                        } else {
                            listener.onResult(result);
                        }
                    }
                    else if (type.equals("GETPUSHLIST")) {
                        SCResult.PushListResult result = Json.decode(responseStr,
                                SCResult.PushListResult.class);
                        if (result.code != Codes.Code_Success) {
                            if(result.code == Codes.Code_316){
                                Session.getInstance().setLastTokenTimestamp(0);
                            }
                            listener.onError(result.code, result.errormsg);
                        } else {
                            listener.onResult(result);
                        }
                    }
                    else if (type.equals("DELETEPUSH")) {
                        SCResult.Result result = Json.decode(responseStr,
                                SCResult.Result.class);
                        if (result.code != Codes.Code_Success) {
                            if(result.code == Codes.Code_316){
                                Session.getInstance().setLastTokenTimestamp(0);
                            }
                            listener.onError(result.code, result.errormsg);
                        } else {
                            listener.onResult(result);
                        }
                    }
                    else if (type.equals("GETSTEP")) {
                        SCResult.StepResult result = Json.decode(responseStr,
                                SCResult.StepResult.class);
                        if (result.code != Codes.Code_Success) {
                            if(result.code == Codes.Code_316){
                                Session.getInstance().setLastTokenTimestamp(0);
                            }
                            listener.onError(result.code, result.errormsg);
                        } else {
                            listener.onResult(result);
                        }
                    }
                    else if (type.equals("CACULATEPREP")) {
                        SCResult.PrepareResult result = Json.decode(responseStr,
                                SCResult.PrepareResult.class);
                        if (result.code != Codes.Code_Success) {
                            if(result.code == Codes.Code_316){
                                Session.getInstance().setLastTokenTimestamp(0);
                            }
                            listener.onError(result.code, result.errormsg);
                        } else {
                            listener.onResult(result);
                        }
                    }
                    else if (type.equals("SUBMITPREP")) {
                        SCResult.SubmitPrepareResult result = Json.decode(responseStr,
                                SCResult.SubmitPrepareResult.class);
                        if (result.code != Codes.Code_Success) {
                            if(result.code == Codes.Code_316){
                                Session.getInstance().setLastTokenTimestamp(0);
                            }
                            listener.onError(result.code, result.errormsg);
                        } else {
                            listener.onResult(result);
                        }
                    }
                    else if (type.equals("ISNEED")) {
                        SCResult.IsneedProjectResult result = Json.decode(responseStr,
                                SCResult.IsneedProjectResult.class);
                        if (result.code != Codes.Code_Success) {
                            if(result.code == Codes.Code_316){
                                Session.getInstance().setLastTokenTimestamp(0);
                            }
                            listener.onError(result.code, result.errormsg);
                        } else {
                            listener.onResult(result);
                        }
                    }
                    else if (type.equals("GETPROJECT")) {
                        SCResult.ProjectResult result = Json.decode(responseStr,
                                SCResult.ProjectResult.class);
                        if (result.code != Codes.Code_Success) {
                            if(result.code == Codes.Code_316){
                                Session.getInstance().setLastTokenTimestamp(0);
                            }
                            listener.onError(result.code, result.errormsg);
                        } else {
                            listener.onResult(result);
                        }
                    }
                    else if (type.equals("GETDEVELOP")) {
                        SCResult.DevelopResult result = Json.decode(responseStr,
                                SCResult.DevelopResult.class);
                        if (result.code != Codes.Code_Success) {
                            if(result.code == Codes.Code_316){
                                Session.getInstance().setLastTokenTimestamp(0);
                            }
                            listener.onError(result.code, result.errormsg);
                        } else {
                            listener.onResult(result);
                        }
                    }
                    else if (type.equals("GETPURORDERS")) {
                        SCResult.PurchaseOrderResult result = Json.decode(responseStr,
                                SCResult.PurchaseOrderResult.class);
                        if (result.code != Codes.Code_Success) {
                            if(result.code == Codes.Code_316){
                                Session.getInstance().setLastTokenTimestamp(0);
                            }
                            listener.onError(result.code, result.errormsg);
                        } else {
                            listener.onResult(result);
                        }
                    }
                    else if (type.equals("GETPURSTOREORDERS")) {
                        SCResult.PurchaseStoreOrderResult result = Json.decode(responseStr,
                                SCResult.PurchaseStoreOrderResult.class);
                        if (result.code != Codes.Code_Success) {
                            if(result.code == Codes.Code_316){
                                Session.getInstance().setLastTokenTimestamp(0);
                            }
                            listener.onError(result.code, result.errormsg);
                        } else {
                            listener.onResult(result);
                        }
                    }

                    else if (type.equals("GETPURDETAIL")) {
                        SCResult.PurchaseDetailResult result = Json.decode(responseStr,
                                SCResult.PurchaseDetailResult.class);
                        if (result.code != Codes.Code_Success) {
                            if(result.code == Codes.Code_316){
                                Session.getInstance().setLastTokenTimestamp(0);
                            }
                            listener.onError(result.code, result.errormsg);
                        } else {
                            listener.onResult(result);
                        }
                    }
                    else if (type.equals("GETPURSTOREDETAIL")) {
                        SCResult.PurchaseStoreDetailResult result = Json.decode(responseStr,
                                SCResult.PurchaseStoreDetailResult.class);
                        if (result.code != Codes.Code_Success) {
                            if(result.code == Codes.Code_316){
                                Session.getInstance().setLastTokenTimestamp(0);
                            }
                            listener.onError(result.code, result.errormsg);
                        } else {
                            listener.onResult(result);
                        }
                    }
                    else if (type.equals("GETPURLIST")) {
                        SCResult.PurchaseListResult result = Json.decode(responseStr,
                                SCResult.PurchaseListResult.class);
                        if (result.code != Codes.Code_Success) {
                            if(result.code == Codes.Code_316){
                                Session.getInstance().setLastTokenTimestamp(0);
                            }
                            listener.onError(result.code, result.errormsg);
                        } else {
                            listener.onResult(result);
                        }
                    }
                    else if (type.equals("GETPURMODEL")) {
                        SCResult.PurchaseModelResult result = Json.decode(responseStr,
                                SCResult.PurchaseModelResult.class);
                        if (result.code != Codes.Code_Success) {
                            if(result.code == Codes.Code_316){
                                Session.getInstance().setLastTokenTimestamp(0);
                            }
                            listener.onError(result.code, result.errormsg);
                        } else {
                            listener.onResult(result);
                        }
                    }

                    else if (type.equals("GETSUPPLIERS")) {
                        SCResult.SupplierResult result = Json.decode(responseStr,
                                SCResult.SupplierResult.class);
                        if (result.code != Codes.Code_Success) {
                            if(result.code == Codes.Code_316){
                                Session.getInstance().setLastTokenTimestamp(0);
                            }
                            listener.onError(result.code, result.errormsg);
                        } else {
                            listener.onResult(result);
                        }
                    }
                    else if (type.equals("ADDPURORDER")) {
                        SCResult.Result result = Json.decode(responseStr,
                                SCResult.Result.class);
                        if (result.code != Codes.Code_Success) {
                            if(result.code == Codes.Code_316){
                                Session.getInstance().setLastTokenTimestamp(0);
                            }
                            listener.onError(result.code, result.errormsg);
                        } else {
                            listener.onResult(result);
                        }
                    }
                    else if (type.equals("GETSALEORDERS")) {
                        SCResult.SaleOrderResult result = Json.decode(responseStr,
                                SCResult.SaleOrderResult.class);
                        if (result.code != Codes.Code_Success) {
                            if(result.code == Codes.Code_316){
                                Session.getInstance().setLastTokenTimestamp(0);
                            }
                            listener.onError(result.code, result.errormsg);
                        } else {
                            listener.onResult(result);
                        }
                    }
                    else if (type.equals("GETSALEDETAIL")) {
                        SCResult.SaleDetailResult result = Json.decode(responseStr,
                                SCResult.SaleDetailResult.class);
                        if (result.code != Codes.Code_Success) {
                            if(result.code == Codes.Code_316){
                                Session.getInstance().setLastTokenTimestamp(0);
                            }
                            listener.onError(result.code, result.errormsg);
                        } else {
                            listener.onResult(result);
                        }
                    }
                    else if (type.equals("ADDSALEORDER")) {
                        SCResult.AddSaleOrderResult result = Json.decode(responseStr,
                                SCResult.AddSaleOrderResult.class);
                        if (result.code != Codes.Code_Success) {
                            if(result.code == Codes.Code_316){
                                Session.getInstance().setLastTokenTimestamp(0);
                            }
                            listener.onError(result.code, result.errormsg);
                        } else {
                            listener.onResult(result);
                        }
                    }
                    else if (type.equals("GETMODELBYP")) {
                        SCResult.ModelResult result = Json.decode(responseStr,
                                SCResult.ModelResult.class);
                        if (result.code != Codes.Code_Success) {
                            if(result.code == Codes.Code_316){
                                Session.getInstance().setLastTokenTimestamp(0);
                            }
                            listener.onError(result.code, result.errormsg);
                        } else {
                            listener.onResult(result);
                        }
                    }
                    else if (type.equals("GETACCOUNTP")) {
                        SCResult.AccountPowerResult result = Json.decode(responseStr,
                                SCResult.AccountPowerResult.class);
                        if (result.code != Codes.Code_Success) {
                            if(result.code == Codes.Code_316){
                                Session.getInstance().setLastTokenTimestamp(0);
                            }
                            listener.onError(result.code, result.errormsg);
                        } else {
                            listener.onResult(result);
                        }
                    }
                    else if (type.equals("LISTQ")) {
                        SCResult.ListQuestionResult result = Json.decode(responseStr,
                                SCResult.ListQuestionResult.class);
                        if (result.code != Codes.Code_Success) {
                            if(result.code == Codes.Code_316){
                                Session.getInstance().setLastTokenTimestamp(0);
                            }
                            listener.onError(result.code, result.errormsg);
                        } else {
                            listener.onResult(result);
                        }
                    }
                    else if (type.equals("QUESTIOND")) {
                        SCResult.QuestionDetailResult result = Json.decode(responseStr,
                                SCResult.QuestionDetailResult.class);
                        if (result.code != Codes.Code_Success) {
                            if(result.code == Codes.Code_316){
                                Session.getInstance().setLastTokenTimestamp(0);
                            }
                            listener.onError(result.code, result.errormsg);
                        } else {
                            listener.onResult(result);
                        }
                    }
                    else if (type.equals("SUBMITQ")) {
                        SCResult.Result result = Json.decode(responseStr,
                                SCResult.Result.class);
                        if (result.code != Codes.Code_Success) {
                            if(result.code == Codes.Code_316){
                                Session.getInstance().setLastTokenTimestamp(0);
                            }
                            listener.onError(result.code, result.errormsg);
                        } else {
                            listener.onResult(result);
                        }
                    }
                    else if (type.equals("SEARCHMV")) {
                        SCResult.ModelValueResult result = Json.decode(responseStr,
                                SCResult.ModelValueResult.class);
                        if (result.code != Codes.Code_Success) {
                            if(result.code == Codes.Code_316){
                                Session.getInstance().setLastTokenTimestamp(0);
                            }
                            listener.onError(result.code, result.errormsg);
                        } else {
                            listener.onResult(result);
                        }
                    }
                    else if (type.equals("GETMODELPOWER")) {
                        SCResult.ModelPowerResult result = Json.decode(responseStr,
                                SCResult.ModelPowerResult.class);
                        if (result.code != Codes.Code_Success) {
                            if(result.code == Codes.Code_316){
                                Session.getInstance().setLastTokenTimestamp(0);
                            }
                            listener.onError(result.code, result.errormsg);
                        } else {
                            listener.onResult(result);
                        }
                    }
                    else if (type.equals("CHECKSN")) {
                        SCResult.CheckSNResult result = Json.decode(responseStr,
                                SCResult.CheckSNResult.class);
                        if (result.code != Codes.Code_Success) {
                            if(result.code == Codes.Code_316){
                                Session.getInstance().setLastTokenTimestamp(0);
                            }
                            listener.onError(result.code, result.errormsg);
                        } else {
                            listener.onResult(result);
                        }
                    }
                    else if (type.equals("MODELRULE")) {
                        SCResult.ModelRuleResult result = Json.decode(responseStr,
                                SCResult.ModelRuleResult.class);
                        if (result.code != Codes.Code_Success) {
                            if(result.code == Codes.Code_316){
                                Session.getInstance().setLastTokenTimestamp(0);
                            }
                            listener.onError(result.code, result.errormsg);
                        } else {
                            listener.onResult(result);
                        }
                    }
                    else if (type.equals("GETPAYMODES")) {
                        SCResult.PayModeResult result = Json.decode(responseStr,
                                SCResult.PayModeResult.class);
                        if (result.code != Codes.Code_Success) {
                            if(result.code == Codes.Code_316){
                                Session.getInstance().setLastTokenTimestamp(0);
                            }
                            listener.onError(result.code, result.errormsg);
                        } else {
                            listener.onResult(result);
                        }
                    }
                    else if (type.equals("SETTLESALEORDER")) {
                        SCResult.Result result = Json.decode(responseStr,
                                SCResult.Result.class);
                        if (result.code != Codes.Code_Success) {
                            if(result.code == Codes.Code_316){
                                Session.getInstance().setLastTokenTimestamp(0);
                            }
                            listener.onError(result.code, result.errormsg);
                        } else {
                            listener.onResult(result);
                        }
                    }
                    else if (type.equals("STORESALEORDER")) {
                        SCResult.Result result = Json.decode(responseStr,
                                SCResult.Result.class);
                        if (result.code != Codes.Code_Success) {
                            if(result.code == Codes.Code_316){
                                Session.getInstance().setLastTokenTimestamp(0);
                            }
                            listener.onError(result.code, result.errormsg);
                        } else {
                            listener.onResult(result);
                        }
                    }
                    else if (type.equals("SALEORDERDETAIL")) {
                        SCResult.SaleOrderStoreResult result = Json.decode(responseStr,
                                SCResult.SaleOrderStoreResult.class);
                        if (result.code != Codes.Code_Success) {
                            if(result.code == Codes.Code_316){
                                Session.getInstance().setLastTokenTimestamp(0);
                            }
                            listener.onError(result.code, result.errormsg);
                        } else {
                            listener.onResult(result);
                        }
                    }

                    else if (type.equals("MODIFYSALEORDERCUSTOMER")) {
                        SCResult.Result result = Json.decode(responseStr,
                                SCResult.Result.class);
                        if (result.code != Codes.Code_Success) {
                            if(result.code == Codes.Code_316){
                                Session.getInstance().setLastTokenTimestamp(0);
                            }
                            listener.onError(result.code, result.errormsg);
                        } else {
                            listener.onResult(result);
                        }
                    }
                    else if (type.equals("LISTSALEORDERSTORE")) {
                        SCResult.StoreOrderResult result = Json.decode(responseStr,
                                SCResult.StoreOrderResult.class);
                        if (result.code != Codes.Code_Success) {
                            if(result.code == Codes.Code_316){
                                Session.getInstance().setLastTokenTimestamp(0);
                            }
                            listener.onError(result.code, result.errormsg);
                        } else {
                            listener.onResult(result);
                        }
                    }
                    else if (type.equals("GETSALESTOREORDER")) {
                        SCResult.SaleStoreOrderResult result = Json.decode(responseStr,
                                SCResult.SaleStoreOrderResult.class);
                        if (result.code != Codes.Code_Success) {
                            if(result.code == Codes.Code_316){
                                Session.getInstance().setLastTokenTimestamp(0);
                            }
                            listener.onError(result.code, result.errormsg);
                        } else {
                            listener.onResult(result);
                        }
                    }
                    else if (type.equals("BATCHSTORE")) {
                        SCResult.Result result = Json.decode(responseStr,
                                SCResult.Result.class);
                        if (result.code != Codes.Code_Success) {
                            if(result.code == Codes.Code_316){
                                Session.getInstance().setLastTokenTimestamp(0);
                            }
                            listener.onError(result.code, result.errormsg);
                        } else {
                            listener.onResult(result);
                        }
                    }
                    else if (type.equals("VERSIONREMARK")) {
                        SCResult.VersionRemarkResult result = Json.decode(responseStr,
                                SCResult.VersionRemarkResult.class);
                        if (result.code != Codes.Code_Success) {
                            if(result.code == Codes.Code_316){
                                Session.getInstance().setLastTokenTimestamp(0);
                            }
                            listener.onError(result.code, result.errormsg);
                        } else {
                            listener.onResult(result);
                        }
                    }
                    else if (type.equals("GetSNHistory")) {
                        SCResult.SnHistoryResult result = Json.decode(responseStr,
                                SCResult.SnHistoryResult.class);
                        if (result.code != Codes.Code_Success) {
                            if(result.code == Codes.Code_316){
                                Session.getInstance().setLastTokenTimestamp(0);
                            }
                            listener.onError(result.code, result.errormsg);
                        } else {
                            listener.onResult(result);
                        }
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            if (listener != null) {
                listener.onError(600,"网络无法到达");
            }
        }

        finally {
            try {
                if (dataout != null) {
                    dataout.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } // 重要且易忽略步骤 (关闭流,切记!)
            try {
                if (bf != null) {
                    bf.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } // 重要且易忽略步骤 (关闭流,切记!)
            if(connection != null) {
                connection.disconnect(); // 销毁连接
            }
        }
    }
}
