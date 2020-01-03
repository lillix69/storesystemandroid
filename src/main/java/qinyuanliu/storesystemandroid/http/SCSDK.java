package qinyuanliu.storesystemandroid.http;

import qinyuanliu.storesystemandroid.http.util.ThreadPoolUtils;

/**
 * Created by lillix on 5/30/18.
 */
public class SCSDK {
    private static volatile SCSDK instance = null;

    private SCSDK() {

        System.out.println("==========初始化sdk=============");
    }

    public static SCSDK getInstance() {
        if (instance == null) {
            synchronized (SCSDK.class) {
                if (instance == null) {
                    instance = new SCSDK();
                }
            }
        }
        return instance;
    }

    public void ServerConfig(String server, int port){
        Constants.API_SERVER_PORT = port;
        Constants.SERVER_IP = server;
    }


    //APP端登录
    public void Login(String shopcode,String loginname, String password, String deviceid, SCResponseListener listener) {
        String sendbody = "loginname=" + loginname + "&password=" + password + "&deviceid=" + deviceid + "&devicetype=1" + "&shopcode=" + shopcode;
        String sendurl =  Constants.SERVER_IP + ":" + Constants.API_SERVER_PORT + Constants.TEST_IP + "/login";

        HttpRunnable runnable = new HttpRunnable();
        runnable.setSendurl(sendurl);
        runnable.setSendbody(sendbody);
        runnable.setListener(listener);
        runnable.setType("LOGIN");
        ThreadPoolUtils.execute(runnable);
    }
    //设备登录账户与绑定
    public void BindPushAccount(String shopcode,String token, String regid, SCResponseListener listener){
        String sendbody =  "token=" + token + "&shopcode=" + shopcode + "&regid=" + regid;
        String sendurl =  Constants.SERVER_IP + ":" + Constants.API_SERVER_PORT + Constants.TEST_IP + "/binddevice";

        HttpRunnable runnable = new HttpRunnable();
        runnable.setSendurl(sendurl);
        runnable.setSendbody(sendbody);
        runnable.setListener(listener);
        runnable.setType("BINDPUSH");
        ThreadPoolUtils.execute(runnable);
    }
    //APP端退出
    public void Logout(String shopcode,String loginname, String token, String deviceid, String regid, SCResponseListener listener) {
        String sendbody = "loginname=" + loginname + "&token=" + token + "&deviceid=" + deviceid + "&devicetype=1"+ "&shopcode=" + shopcode + "&regid=" + regid;
        String sendurl =  Constants.SERVER_IP + ":" + Constants.API_SERVER_PORT + Constants.TEST_IP + "/loginout";

        HttpRunnable runnable = new HttpRunnable();
        runnable.setSendurl(sendurl);
        runnable.setSendbody(sendbody);
        runnable.setListener(listener);
        runnable.setType("LOGOUT");
        ThreadPoolUtils.execute(runnable);
    }

    public void SearchModel(String shopcode,String keyword, String token, SCResponseListener listener) {
        String tempsendbody =  "token=" + token+ "&shopcode=" + shopcode;
        StringBuffer sendbody = new StringBuffer(tempsendbody);
        if(keyword != null){
            sendbody.append("&keyword=").append(keyword);
        }
        String sendurl = Constants.SERVER_IP + ":" + Constants.API_SERVER_PORT + Constants.TEST_IP + "/searchmodel";

        HttpRunnable runnable = new HttpRunnable();
        runnable.setSendurl(sendurl);
        runnable.setSendbody(sendbody.toString());
        runnable.setListener(listener);
        runnable.setType("SEARCHMODEL");
        ThreadPoolUtils.execute(runnable);
    }

    public void GetTypeList(String shopcode,String token, SCResponseListener listener) {
        String sendbody = "token=" + token+ "&shopcode=" + shopcode;
        String sendurl = Constants.SERVER_IP + ":" + Constants.API_SERVER_PORT + Constants.TEST_IP + "/typelist";

        HttpRunnable runnable = new HttpRunnable();
        runnable.setSendurl(sendurl);
        runnable.setSendbody(sendbody);
        runnable.setListener(listener);
        runnable.setType("TYPELIST");
        ThreadPoolUtils.execute(runnable);
    }

    public void GetStoreList(String shopcode,String token, SCResponseListener listener) {
        String sendbody = "token=" + token+ "&shopcode=" + shopcode;
        String sendurl =  Constants.SERVER_IP + ":" + Constants.API_SERVER_PORT + Constants.TEST_IP + "/storelist";

        HttpRunnable runnable = new HttpRunnable();
        runnable.setSendurl(sendurl);
        runnable.setSendbody(sendbody);
        runnable.setListener(listener);
        runnable.setType("STORELIST");
        ThreadPoolUtils.execute(runnable);
    }

    public void GetRackList(String shopcode,String storecode, String token, SCResponseListener listener) {
        String sendbody = "storecode=" + storecode + "&token=" + token + "&shopcode=" + shopcode;
        String sendurl =  Constants.SERVER_IP + ":" + Constants.API_SERVER_PORT + Constants.TEST_IP + "/racklist";

        HttpRunnable runnable = new HttpRunnable();
        runnable.setSendurl(sendurl);
        runnable.setSendbody(sendbody);
        runnable.setListener(listener);
        runnable.setType("RACKLIST");
        ThreadPoolUtils.execute(runnable);
    }

    public void GetPositionList(String shopcode,String storecode, String rackcode, String token, SCResponseListener listener) {
        String sendbody = "storecode=" + storecode + "&rackcode=" + rackcode + "&token=" + token + "&shopcode=" + shopcode;
        String sendurl = Constants.SERVER_IP + ":" + Constants.API_SERVER_PORT + Constants.TEST_IP + "/positionlist";

        HttpRunnable runnable = new HttpRunnable();
        runnable.setSendurl(sendurl);
        runnable.setSendbody(sendbody);
        runnable.setListener(listener);
        runnable.setType("POSITIONLIST");
        ThreadPoolUtils.execute(runnable);
    }

    public void ScanProductDetail(String shopcode,String sn, String token, SCResponseListener listener) {
        String sendbody = "sn=" + sn + "&token=" + token + "&shopcode=" + shopcode;
        String sendurl =  Constants.SERVER_IP + ":" + Constants.API_SERVER_PORT + Constants.TEST_IP + "/modeldetailbysn";

        HttpRunnable runnable = new HttpRunnable();
        runnable.setSendurl(sendurl);
        runnable.setSendbody(sendbody);
        runnable.setListener(listener);
        runnable.setType("SCANDETAIL");
        ThreadPoolUtils.execute(runnable);
    }

    //查询系统支持的型号数字标识集合
    public void GetModelrule(String shopcode, String token, SCResponseListener listener) {
        String sendbody = "token=" + token + "&shopcode=" + shopcode;
        String sendurl =  Constants.SERVER_IP + ":" + Constants.API_SERVER_PORT + Constants.TEST_IP + "/modelrulelist";

        HttpRunnable runnable = new HttpRunnable();
        runnable.setSendurl(sendurl);
        runnable.setSendbody(sendbody);
        runnable.setListener(listener);
        runnable.setType("MODELRULE");
        ThreadPoolUtils.execute(runnable);
    }

    //是否需要检验，0：不需要 1：需要
    public void AddModel(String shopcode,String modelname, String token, String typecode,String modelrulecode, String spec,
                         String remark, String modelpic, String storecode,String rackcode, String positioncode, String unit, int min,int needcheck, SCResponseListener listener) {
        String tempsendbody = "modelname=" + modelname + "&token=" + token +  "&shopcode=" + shopcode + "&typecode=" + typecode + "&spec=" + spec + "&modelrulecode=" + modelrulecode;
        StringBuffer sendbody = new StringBuffer(tempsendbody);
        if (modelpic != null) {
            sendbody.append("&modelpic=").append(modelpic);
        }
        if (remark != null) {
            sendbody.append("&remark=").append(remark);
        }
        if (storecode != null) {
            sendbody.append("&storecode=").append(storecode);
        }
        if (rackcode != null) {
            sendbody.append("&rackcode=").append(rackcode);
        }
        if (positioncode != null) {
            sendbody.append("&positioncode=").append(positioncode);
        }
        if (unit != null) {
            sendbody.append("&unit=").append(unit);
        }
        if (min > 0) {
            sendbody.append("&min=").append(min);
        }
        if (needcheck == 0 || needcheck == 1) {
            sendbody.append("&needcheck=").append(needcheck);
        }
        String sendurl =Constants.SERVER_IP + ":" + Constants.API_SERVER_PORT + Constants.TEST_IP + "/addmodel";

        HttpRunnable runnable = new HttpRunnable();
        runnable.setSendurl(sendurl);
        runnable.setSendbody(sendbody.toString());
        runnable.setListener(listener);
        runnable.setType("ADDMODEL");
        ThreadPoolUtils.execute(runnable);
    }
    public void ModifyModel(String shopcode,String modelcode, String token,
                            String modelname,String storecode, String rackcode, String positioncode, String typecode, String unit, String spec,
                         int needcheck, String modelpic, double lastedbuyin, double lastedtrace, String customercode,String suppliername, int min,String remark, SCResponseListener listener) {
        String tempsendbody = "modelcode=" + modelcode +"&modelname=" + modelname + "&token=" + token + "&storecode=" + storecode + "&rackcode=" + rackcode + "&positioncode=" + positioncode + "&typecode=" + typecode
                + "&unit=" + unit + "&spec=" + spec + "&needcheck=" + needcheck + "&shopcode=" + shopcode;
        StringBuffer sendbody = new StringBuffer(tempsendbody);

        if (modelpic != null) {
            sendbody.append("&modelpic=").append(modelpic);
        }
        if (lastedbuyin >= 0) {
            sendbody.append("&lastedbuyin=").append(lastedbuyin);
        }
        if (lastedtrace >= 0) {
            sendbody.append("&lastedtrace=").append(lastedtrace);
        }
        if (customercode != null) {
            sendbody.append("&customercode=").append(customercode);
        }
        if (suppliername != null) {
            sendbody.append("&suppliername=").append(suppliername);
        }
        if(remark != null){
            sendbody.append("&remark=").append(remark);
        }

        if (min >= 0) {
            sendbody.append("&min=").append(min);
        }

        String sendurl =  Constants.SERVER_IP + ":" + Constants.API_SERVER_PORT + Constants.TEST_IP + "/modifymodel";

        HttpRunnable runnable = new HttpRunnable();
        runnable.setSendurl(sendurl);
        runnable.setSendbody(sendbody.toString());
        runnable.setListener(listener);
        runnable.setType("MODIFYMODEL");
        ThreadPoolUtils.execute(runnable);
    }

    public void GetProductDetail(String shopcode,String modelcode, String token, SCResponseListener listener) {
        String sendbody = "modelcode=" + modelcode + "&token=" + token + "&shopcode=" + shopcode;
        String sendurl = Constants.SERVER_IP + ":" + Constants.API_SERVER_PORT + Constants.TEST_IP + "/getmodel";

        HttpRunnable runnable = new HttpRunnable();
        runnable.setSendurl(sendurl);
        runnable.setSendbody(sendbody);
        runnable.setListener(listener);
        runnable.setType("GETDETAIL");
        ThreadPoolUtils.execute(runnable);
    }

    public void PrintBarcode(String shopcode,String modelcode, int count ,String token, SCResponseListener listener) {
        String sendbody = "modelcode=" + modelcode + "&count=" + count + "&token=" + token + "&shopcode=" + shopcode;
        String sendurl = Constants.SERVER_IP + ":" + Constants.API_SERVER_PORT + Constants.TEST_IP + "/printbarcode";

        HttpRunnable runnable = new HttpRunnable();
        runnable.setSendurl(sendurl);
        runnable.setSendbody(sendbody);
        runnable.setListener(listener);
        runnable.setType("PRINTBARCODE");
        ThreadPoolUtils.execute(runnable);
    }

    //出库原因 0：生产出库 1：补发出库 2：售后出库 3：成品出库 9：其它
    //sns:序列号集合，多个半角逗号隔开
    public void OutStore(String shopcode,String modelcode, int count , int reason, String token,String sns, String remark,String projectcode, String projectmodelcode,SCResponseListener listener) {
        String tempsendbody = "modelcode=" + modelcode + "&count=" + count + "&reason=" + reason + "&token=" + token + "&shopcode=" + shopcode + "&projectcode=" + projectcode + "&projectmodelcode=" + projectmodelcode;
        StringBuffer sendbody = new StringBuffer(tempsendbody);
        if(sns != null){
            sendbody.append("&sns=").append(sns);
        }
        if(remark != null){
            sendbody.append("&remark=").append(remark);
        }
        String sendurl =  Constants.SERVER_IP + ":" + Constants.API_SERVER_PORT + Constants.TEST_IP + "/outstore";

        HttpRunnable runnable = new HttpRunnable();
        runnable.setSendurl(sendurl);
        runnable.setSendbody(sendbody.toString());
        runnable.setListener(listener);
        runnable.setType("OUTSTORE");
        ThreadPoolUtils.execute(runnable);
    }

    //入库类型 0：采购入库 1：生产入库 2：售后入库9：其它
    public void InStore(String shopcode,String modelcode, int count , int reason, String token,String sns, String remark, SCResponseListener listener) {
        String tempsendbody = "modelcode=" + modelcode + "&count=" + count + "&reason=" + reason + "&token=" + token + "&shopcode=" + shopcode;
        StringBuffer sendbody = new StringBuffer(tempsendbody);
        if(sns != null){
            sendbody.append("&sns=").append(sns);
        }
        if(remark != null){
            sendbody.append("&remark=").append(remark);
        }
        String sendurl = Constants.SERVER_IP + ":" + Constants.API_SERVER_PORT + Constants.TEST_IP + "/instore";

        HttpRunnable runnable = new HttpRunnable();
        runnable.setSendurl(sendurl);
        runnable.setSendbody(sendbody.toString());
        runnable.setListener(listener);
        runnable.setType("INSTORE");
        ThreadPoolUtils.execute(runnable);
    }

    public void CheckStore(String shopcode,String modelcode, int count , String token,String sns,  SCResponseListener listener) {
        String tempsendbody = "modelcode=" + modelcode + "&count=" + count +  "&token=" + token + "&shopcode=" + shopcode;
        StringBuffer sendbody = new StringBuffer(tempsendbody);
        if(sns != null){
            sendbody.append("&sns=").append(sns);
        }

        String sendurl =  Constants.SERVER_IP + ":" + Constants.API_SERVER_PORT + Constants.TEST_IP + "/checkstore";

        HttpRunnable runnable = new HttpRunnable();
        runnable.setSendurl(sendurl);
        runnable.setSendbody(sendbody.toString());
        runnable.setListener(listener);
        runnable.setType("CHECKSTORE");
        ThreadPoolUtils.execute(runnable);
    }

    //0：出库 1：入库
    public void GetReason(String shopcode,String typecode, int flag , String token,  SCResponseListener listener) {
        String sendbody = "typecode=" + typecode + "&flag=" + flag +  "&token=" + token + "&shopcode=" + shopcode;
        String sendurl =  Constants.SERVER_IP + ":" + Constants.API_SERVER_PORT + Constants.TEST_IP + "/stocktypelist";

        HttpRunnable runnable = new HttpRunnable();
        runnable.setSendurl(sendurl);
        runnable.setSendbody(sendbody);
        runnable.setListener(listener);
        runnable.setType("GETREASON");
        ThreadPoolUtils.execute(runnable);
    }

    //扫一扫—根据条码判断类型// 0:产品  1：型号  2：出入权限二维码
public void GetTypeByScan(String shopcode,String sn,String token,  SCResponseListener listener){
    String sendbody = "sn=" + sn +  "&token=" + token + "&shopcode=" + shopcode;
    String sendurl =  Constants.SERVER_IP + ":" + Constants.API_SERVER_PORT + Constants.TEST_IP + "/gettypebysn";
System.out.println("扫码参数======"+sendbody);
    HttpRunnable runnable = new HttpRunnable();
    runnable.setSendurl(sendurl);
    runnable.setSendbody(sendbody);
    runnable.setListener(listener);
    runnable.setType("GETSCANTYPE");
    ThreadPoolUtils.execute(runnable);
}

    //扫一扫—根据条码查询型号信息
    public void GetModeldetailByScan(String shopcode,String sn,String token,  SCResponseListener listener){
        String sendbody = "sn=" + sn +  "&token=" + token + "&shopcode=" + shopcode;
        String sendurl =  Constants.SERVER_IP + ":" + Constants.API_SERVER_PORT + Constants.TEST_IP + "/modeldetailbysn";

        HttpRunnable runnable = new HttpRunnable();
        runnable.setSendurl(sendurl);
        runnable.setSendbody(sendbody);
        runnable.setListener(listener);
        runnable.setType("GETMODELDETAIL");
        ThreadPoolUtils.execute(runnable);
    }

    //扫一扫—根据条码查询产品信息
    public void GetProductdetailByScan(String shopcode,String sn,String token,  SCResponseListener listener){
        String sendbody = "sn=" + sn +  "&token=" + token + "&shopcode=" + shopcode;
        String sendurl =  Constants.SERVER_IP + ":" + Constants.API_SERVER_PORT + Constants.TEST_IP + "/productdetailbysn";

        HttpRunnable runnable = new HttpRunnable();
        runnable.setSendurl(sendurl);
        runnable.setSendbody(sendbody);
        runnable.setListener(listener);
        runnable.setType("GETPRODUCTDETAIL");
        ThreadPoolUtils.execute(runnable);
    }

    //产品明细—确认序列号流程状态
    public void ConfirmProduct(String sn, String shopcode,String token,  SCResponseListener listener){
        String sendbody = "sn=" + sn +  "&token=" + token + "&shopcode=" + shopcode;
        String sendurl = Constants.SERVER_IP + ":" + Constants.API_SERVER_PORT + Constants.TEST_IP + "/confirmsnflow";

        HttpRunnable runnable = new HttpRunnable();
        runnable.setSendurl(sendurl);
        runnable.setSendbody(sendbody);
        runnable.setListener(listener);
        runnable.setType("CONFIRMPRODUCT");
        ThreadPoolUtils.execute(runnable);
    }

    public void GetVersion(String shopcode, SCResponseListener listener){
        String sendbody = "shopcode=" + shopcode;
        String sendurl =  Constants.SERVER_IP + ":" + Constants.API_SERVER_PORT + Constants.TEST_IP + "/getversion";

        HttpRunnable runnable = new HttpRunnable();
        runnable.setSendurl(sendurl);
        runnable.setSendbody(sendbody);
        runnable.setListener(listener);
        runnable.setType("GETVERSION");
        ThreadPoolUtils.execute(runnable);

    }

    public void SearchModelSns(String shopcode,String modelcode,String token,  SCResponseListener listener){
        String sendbody = "modelcode=" + modelcode +  "&token=" + token + "&shopcode=" + shopcode;
        String sendurl =  Constants.SERVER_IP + ":" + Constants.API_SERVER_PORT + Constants.TEST_IP + "/searchmodelsns";

        HttpRunnable runnable = new HttpRunnable();
        runnable.setSendurl(sendurl);
        runnable.setSendbody(sendbody);
        runnable.setListener(listener);
        runnable.setType("SEARCHMODELSNS");
        ThreadPoolUtils.execute(runnable);

    }

    public void PrintSpecificSn(String shopcode,String sn,String token,  SCResponseListener listener){
        String sendbody = "sn=" + sn +  "&token=" + token + "&shopcode=" + shopcode;
        String sendurl =  Constants.SERVER_IP + ":" + Constants.API_SERVER_PORT + Constants.TEST_IP + "/printspecialsn";

        HttpRunnable runnable = new HttpRunnable();
        runnable.setSendurl(sendurl);
        runnable.setSendbody(sendbody);
        runnable.setListener(listener);
        runnable.setType("PRINTSN");
        ThreadPoolUtils.execute(runnable);
    }

    public void GetQRCode(String shopcode,String token,  SCResponseListener listener){
        String sendbody =  "token=" + token + "&shopcode=" + shopcode;
        String sendurl =  Constants.SERVER_IP + ":" + Constants.API_SERVER_PORT + Constants.TEST_IP + "/getmyqrcode";

        HttpRunnable runnable = new HttpRunnable();
        runnable.setSendurl(sendurl);
        runnable.setSendbody(sendbody);
        runnable.setListener(listener);
        runnable.setType("GETQRCODE");
        ThreadPoolUtils.execute(runnable);
    }

    public void ConfirmInoutStore(String shopcode,String token,String qrcodenum,  SCResponseListener listener){
        String sendbody =  "token=" + token + "&shopcode=" + shopcode + "&qrcodenum=" + qrcodenum;
        String sendurl =  Constants.SERVER_IP + ":" + Constants.API_SERVER_PORT + Constants.TEST_IP + "/confirmstoreaction";

        HttpRunnable runnable = new HttpRunnable();
        runnable.setSendurl(sendurl);
        runnable.setSendbody(sendbody);
        runnable.setListener(listener);
        runnable.setType("CONFIRMINOUT");
        ThreadPoolUtils.execute(runnable);
    }

//获取登录账户今日下班报表概览
public void GetDutyoffSummery(String shopcode,String token, SCResponseListener listener){
    String sendbody =  "token=" + token + "&shopcode=" + shopcode;
    String sendurl = Constants.SERVER_IP + ":" + Constants.API_SERVER_PORT + Constants.TEST_IP + "/gettodayworkview";

    HttpRunnable runnable = new HttpRunnable();
    runnable.setSendurl(sendurl);
    runnable.setSendbody(sendbody);
    runnable.setListener(listener);
    runnable.setType("GETSUMMERY");
    ThreadPoolUtils.execute(runnable);
}

//获取登录账户指定日期下班报表明细,yyyy-MM-dd
public void GetDutyoffDetail(String shopcode,String token, String date, SCResponseListener listener){
    String sendbody =  "token=" + token + "&shopcode=" + shopcode + "&date=" + date;
    String sendurl =  Constants.SERVER_IP + ":" + Constants.API_SERVER_PORT + Constants.TEST_IP + "/getdayworkdetail";

    HttpRunnable runnable = new HttpRunnable();
    runnable.setSendurl(sendurl);
    runnable.setSendbody(sendbody);
    runnable.setListener(listener);
    runnable.setType("GETDUTYDETAIL");
    ThreadPoolUtils.execute(runnable);
}

//提交指定日期下班报表
// 出库确认明细，格式 型号编号-status-remark,型号编号- status-remark, 型号编号- status-remark半角逗号隔开,其中 status 0:表示未确认 1：表示确认
//入库确认明细，格式 型号编号-status-remark,型号编号- status-remark, 型号编号- status-remark半角逗号隔开,其中 status 0:表示未确认 1：表示确认
  //生产确认明细，格式 流程编号-步骤编号-型号编号-status-remark, 流程编号-步骤编号-型号编号- status-remark, 流程编号-步骤编号-型号编号- status-remark
   // 半角逗号隔开,其中 status 0:表示未确认 1：表示确认  示例：rd001-0001-0-备注, rd001-1-0002-1, rd002-2-0001-1-备注

    public void SubmitDutyoffDetail(String shopcode,String token, String date, String outstores,String instores, String productstatus,  SCResponseListener listener){
    String sendbody =  "token=" + token + "&shopcode=" + shopcode + "&date=" + date + "&outstores=" +outstores + "&instores=" +instores + "&productstatus=" + productstatus;
    String sendurl =  Constants.SERVER_IP + ":" + Constants.API_SERVER_PORT + Constants.TEST_IP + "/submitdaywork";

    HttpRunnable runnable = new HttpRunnable();
    runnable.setSendurl(sendurl);
    runnable.setSendbody(sendbody);
    runnable.setListener(listener);
    runnable.setType("SUBMITDETAIL");
    ThreadPoolUtils.execute(runnable);
}

//查询账户下班报表历史,0：未确认 1：已确认 2：全部
public void GetDutyoffHistory(String shopcode,String token, String begindate,String enddate, int status, SCResponseListener listener){
    String sendbody =  "token=" + token + "&shopcode=" + shopcode + "&begindate=" + begindate + "&enddate=" +enddate + "&status=" +status;
    String sendurl =  Constants.SERVER_IP + ":" + Constants.API_SERVER_PORT + Constants.TEST_IP + "/searchdayworks";

    HttpRunnable runnable = new HttpRunnable();
    runnable.setSendurl(sendurl);
    runnable.setSendbody(sendbody);
    runnable.setListener(listener);
    runnable.setType("GETHISTORY");
    ThreadPoolUtils.execute(runnable);
}

//获取登录账户对指定型号的权限
public void GetProve(String shopcode,String token, String modelcode, SCResponseListener listener){
    String sendbody =  "token=" + token + "&shopcode=" + shopcode + "&modelcode=" + modelcode;
    String sendurl = Constants.SERVER_IP + ":" + Constants.API_SERVER_PORT + Constants.TEST_IP + "/getaccountpowerofmodel";

    HttpRunnable runnable = new HttpRunnable();
    runnable.setSendurl(sendurl);
    runnable.setSendbody(sendbody);
    runnable.setListener(listener);
    runnable.setType("GETPROVE");
    ThreadPoolUtils.execute(runnable);
}

//获取登录账户未处理消息数
public void GetPushCount(String shopcode,String token,SCResponseListener listener){
    String sendbody =  "token=" + token + "&shopcode=" + shopcode ;
    String sendurl =  Constants.SERVER_IP + ":" + Constants.API_SERVER_PORT + Constants.TEST_IP + "/getunhandleworkcount";

    HttpRunnable runnable = new HttpRunnable();
    runnable.setSendurl(sendurl);
    runnable.setSendbody(sendbody);
    runnable.setListener(listener);
    runnable.setType("GETPUSHCOUNT");
    ThreadPoolUtils.execute(runnable);
}

//获取登录账户未处理消息列表
public void GetPushList(String shopcode,String token,SCResponseListener listener){
    String sendbody =  "token=" + token + "&shopcode=" + shopcode ;
    String sendurl = Constants.SERVER_IP + ":" + Constants.API_SERVER_PORT + Constants.TEST_IP + "/getunhandleworklist";

    HttpRunnable runnable = new HttpRunnable();
    runnable.setSendurl(sendurl);
    runnable.setSendbody(sendbody);
    runnable.setListener(listener);
    runnable.setType("GETPUSHLIST");
    ThreadPoolUtils.execute(runnable);
}

//删除登录账户未处理消息,多个用半角逗号隔开
public void DeletePush(String shopcode,String token, String workcodes, SCResponseListener listener){
    String sendbody =  "token=" + token + "&shopcode=" + shopcode + "&workcodes=" + workcodes;
    String sendurl = Constants.SERVER_IP + ":" + Constants.API_SERVER_PORT + Constants.TEST_IP + "/deleteunhandlework";

    HttpRunnable runnable = new HttpRunnable();
    runnable.setSendurl(sendurl);
    runnable.setSendbody(sendbody);
    runnable.setListener(listener);
    runnable.setType("DELETEPUSH");
    ThreadPoolUtils.execute(runnable);
}

//获取指定型号的生产步骤集合
public void GetPrepareStep(String shopcode,String token, String modelcode, SCResponseListener listener){
    String sendbody =  "token=" + token + "&shopcode=" + shopcode + "&modelcode=" + modelcode;
    String sendurl =  Constants.SERVER_IP + ":" + Constants.API_SERVER_PORT + Constants.TEST_IP + "/listmodelsteps";

    HttpRunnable runnable = new HttpRunnable();
    runnable.setSendurl(sendurl);
    runnable.setSendbody(sendbody);
    runnable.setListener(listener);
    runnable.setType("GETSTEP");
    ThreadPoolUtils.execute(runnable);
}

//计算指定型号指定步骤的配料信息
public void CaculatePrepare(String shopcode,String token, String modelcode, int count, String stepcode, SCResponseListener listener){
    String sendbody =  "token=" + token + "&shopcode=" + shopcode + "&modelcode=" + modelcode + "&count=" + count + "&stepcode=" + stepcode;
    String sendurl =  Constants.SERVER_IP + ":" + Constants.API_SERVER_PORT + Constants.TEST_IP + "/calculatemodelbatchinfo";

    HttpRunnable runnable = new HttpRunnable();
    runnable.setSendurl(sendurl);
    runnable.setSendbody(sendbody);
    runnable.setListener(listener);
    runnable.setType("CACULATEPREP");
    ThreadPoolUtils.execute(runnable);
}

//提交指定型号配料出库信息
    //出库物料明细，格式 用料型号编码-数量-序列号集合, 用料型号编码-数量-序列号集合,例如:
           // 000001-2-{8212121,9131231},000002-2-{8212121,9131231},000003-2
    public void SubmitPrepare(String shopcode,String token, String modelcode, int count, String stepcode,String details, SCResponseListener listener){
    String sendbody =  "token=" + token + "&shopcode=" + shopcode + "&modelcode=" + modelcode + "&count=" + count + "&stepcode=" + stepcode + "&details=" + details;
    String sendurl =  Constants.SERVER_IP + ":" + Constants.API_SERVER_PORT + Constants.TEST_IP + "/submitmodelbatchinfo";

    HttpRunnable runnable = new HttpRunnable();
    runnable.setSendurl(sendurl);
    runnable.setSendbody(sendbody);
    runnable.setListener(listener);
    runnable.setType("SUBMITPREP");
    ThreadPoolUtils.execute(runnable);
}

//获取指定型号出入库是否要选择项目
public void IsNeedProject(String shopcode,String token, String modelcode, SCResponseListener listener){
    String sendbody =  "token=" + token + "&shopcode=" + shopcode + "&modelcode=" + modelcode ;
    String sendurl =  Constants.SERVER_IP + ":" + Constants.API_SERVER_PORT + Constants.TEST_IP + "/isneedproject";

    HttpRunnable runnable = new HttpRunnable();
    runnable.setSendurl(sendurl);
    runnable.setSendbody(sendbody);
    runnable.setListener(listener);
    runnable.setType("ISNEED");
    ThreadPoolUtils.execute(runnable);
}

//获取当前门店正在进行的项目
public void GetProject(String shopcode,String token, SCResponseListener listener){
    String sendbody =  "token=" + token + "&shopcode=" + shopcode ;
    String sendurl = Constants.SERVER_IP + ":" + Constants.API_SERVER_PORT + Constants.TEST_IP + "/listprojects";

    HttpRunnable runnable = new HttpRunnable();
    runnable.setSendurl(sendurl);
    runnable.setSendbody(sendbody);
    runnable.setListener(listener);
    runnable.setType("GETPROJECT");
    ThreadPoolUtils.execute(runnable);
}


    //获取当前门店研发型号列表
    public void GetDevelopModel(String modelcode, String shopcode,String token, SCResponseListener listener){
        String sendbody =  "token=" + token + "&shopcode=" + shopcode + "&modelcode=" + modelcode;
        String sendurl = Constants.SERVER_IP + ":" + Constants.API_SERVER_PORT + Constants.TEST_IP + "/getdevelopmodels";

        HttpRunnable runnable = new HttpRunnable();
        runnable.setSendurl(sendurl);
        runnable.setSendbody(sendbody);
        runnable.setListener(listener);
        runnable.setType("GETDEVELOP");
        ThreadPoolUtils.execute(runnable);
    }

//    //获取当前门店采购单列表
//    public void GetPurchaseOrders(String begindate, String enddate, String shopcode,String token, SCResponseListener listener){
//        String sendbody =  "token=" + token + "&shopcode=" + shopcode ;
//        if(begindate != null){
//            sendbody = sendbody + "&begindate=" + begindate;
//        }
//        if(enddate != null){
//            sendbody = sendbody + "&enddate=" + enddate;
//        }
//        String sendurl =  Constants.SERVER_IP + ":" + Constants.API_SERVER_PORT + Constants.TEST_IP + "/getpurchaseorders";
//
//        HttpRunnable runnable = new HttpRunnable();
//        runnable.setSendurl(sendurl);
//        runnable.setSendbody(sendbody);
//        runnable.setListener(listener);
//        runnable.setType("GETPURORDERS");
//        ThreadPoolUtils.execute(runnable);
//    }

    //获取采购入库单列表
    public void GetPurchaseStoreOrders(String keyword,String begindate, String enddate, String shopcode,String token, SCResponseListener listener){
        String sendbody =  "token=" + token + "&shopcode=" + shopcode ;
        if(begindate != null){
            sendbody = sendbody + "&begindate=" + begindate;
        }
        if(enddate != null){
            sendbody = sendbody + "&enddate=" + enddate;
        }
        if(keyword != null){
            sendbody = sendbody + "&keyword=" + keyword;
        }
        String sendurl =  Constants.SERVER_IP + ":" + Constants.API_SERVER_PORT + Constants.TEST_IP + "/getpurchasestoreorders";

        HttpRunnable runnable = new HttpRunnable();
        runnable.setSendurl(sendurl);
        runnable.setSendbody(sendbody);
        runnable.setListener(listener);
        runnable.setType("GETPURSTOREORDERS");
        ThreadPoolUtils.execute(runnable);
    }

//    //获取采购明细
//    public void GetPurchaseDetail(String purchaseordercode, String shopcode,String token, SCResponseListener listener){
//        String sendbody =  "token=" + token + "&shopcode=" + shopcode + "&purchaseordercode=" + purchaseordercode;
//        String sendurl = Constants.SERVER_IP + ":" + Constants.API_SERVER_PORT + Constants.TEST_IP + "/getpurchaseorderdetail";
//
//        HttpRunnable runnable = new HttpRunnable();
//        runnable.setSendurl(sendurl);
//        runnable.setSendbody(sendbody);
//        runnable.setListener(listener);
//        runnable.setType("GETPURDETAIL");
//        ThreadPoolUtils.execute(runnable);
//    }

    //获取采购入库单明细
    public void GetPurchaseStoreDetail(String instorecode, String shopcode,String token, SCResponseListener listener){
        String sendbody =  "token=" + token + "&shopcode=" + shopcode + "&instorecode=" + instorecode;
        String sendurl = Constants.SERVER_IP + ":" + Constants.API_SERVER_PORT + Constants.TEST_IP + "/getpurchasestoreorderdetail";

        HttpRunnable runnable = new HttpRunnable();
        runnable.setSendurl(sendurl);
        runnable.setSendbody(sendbody);
        runnable.setListener(listener);
        runnable.setType("GETPURSTOREDETAIL");
        ThreadPoolUtils.execute(runnable);
    }

    //获取采购单列表
    public void GetPurchaseList(String shopcode,String token, String begindate, String enddate, SCResponseListener listener){
        String sendbody =  "token=" + token + "&shopcode=" + shopcode ;
        String sendurl =  Constants.SERVER_IP + ":" + Constants.API_SERVER_PORT + Constants.TEST_IP + "/getpurchaselist";
        if(begindate != null){
            sendbody = sendbody + "&begindate=" + begindate;
        }
        if(enddate != null){
            sendbody = sendbody + "&enddate=" + enddate;
        }
        HttpRunnable runnable = new HttpRunnable();
        runnable.setSendurl(sendurl);
        runnable.setSendbody(sendbody);
        runnable.setListener(listener);
        runnable.setType("GETPURLIST");
        ThreadPoolUtils.execute(runnable);
    }

    //获取采购单型号
    public void GetPurchaseModel(String shopcode,String token,String ordercode, SCResponseListener listener){
        String sendbody =  "token=" + token + "&shopcode=" + shopcode + "&ordercode=" + ordercode;
        String sendurl =  Constants.SERVER_IP + ":" + Constants.API_SERVER_PORT + Constants.TEST_IP + "/getpurchasemodels";

        HttpRunnable runnable = new HttpRunnable();
        runnable.setSendurl(sendurl);
        runnable.setSendbody(sendbody);
        runnable.setListener(listener);
        runnable.setType("GETPURMODEL");
        ThreadPoolUtils.execute(runnable);
    }

    //获取门店供应商信息列表
    public void GetSuppliers(String shopcode,String token, String Keyword,SCResponseListener listener){
        String sendbody =  "token=" + token + "&shopcode=" + shopcode ;
        if(!Keyword.equals("")){
            sendbody = sendbody + "&Keyword=" + Keyword;
        }
        String sendurl =  Constants.SERVER_IP + ":" + Constants.API_SERVER_PORT + Constants.TEST_IP + "/getsupplierlist";

        HttpRunnable runnable = new HttpRunnable();
        runnable.setSendurl(sendurl);
        runnable.setSendbody(sendbody);
        runnable.setListener(listener);
        runnable.setType("GETSUPPLIERS");
        ThreadPoolUtils.execute(runnable);
    }

    //获取门店供应商信息列表(=====修改)
    //型号编码-入库数量-采购单价-供应商编码-采购单编号-采购单项编号(lineno)-[序列号1:序列号2:序列号3],
    //型号编码-入库数量-采购单价-供应商编码-[序列号1:序列号2:序列号3],
   // 型号编码-入库数量-采购单价-供应商编码-采购单编号-采购单项编号(lineno),
    //型号编码-入库数量-采购单价-供应商编码

    public void AddPurchaseOrder(String instoredate, String models, String shopcode,String token, SCResponseListener listener){
        String sendbody =  "token=" + token + "&shopcode=" + shopcode + "&models=" + models + "&instoredate=" + instoredate;
        String sendurl = Constants.SERVER_IP + ":" + Constants.API_SERVER_PORT + Constants.TEST_IP + "/addpurchaseorder";

        HttpRunnable runnable = new HttpRunnable();
        runnable.setSendurl(sendurl);
        runnable.setSendbody(sendbody);
        runnable.setListener(listener);
        runnable.setType("ADDPURORDER");
        ThreadPoolUtils.execute(runnable);
    }

    //获取当前门店销售单列表
    //结束时间默认当前时间，开始时间默认结束时间前一个月
    //storestatus  0全部  1未发货  2分单发货  3发货完成
    //paystatus 0 全部  1未付款  2已付款
    public void GetSaleOrders(int storestatus, int paystatus, String customer, String begindate, String enddate, String shopcode,String token, SCResponseListener listener){
        String sendbody =  "token=" + token + "&shopcode=" + shopcode + "&storestatus=" + storestatus + "&paystatus=" + paystatus;
        if(customer != null){
            sendbody = sendbody + "&customer=" + customer;
         }
        if(begindate != null){
            sendbody = sendbody + "&begindate=" + begindate;
        }
        if(enddate != null){
            sendbody = sendbody + "&enddate=" + enddate;
        }
        String sendurl =  Constants.SERVER_IP + ":" + Constants.API_SERVER_PORT + Constants.TEST_IP + "/getsaleorders";

        HttpRunnable runnable = new HttpRunnable();
        runnable.setSendurl(sendurl);
        runnable.setSendbody(sendbody);
        runnable.setListener(listener);
        runnable.setType("GETSALEORDERS");
        ThreadPoolUtils.execute(runnable);
    }



    //新增销售出库单
    //型号编码-销售数量-销售单价，型号编码-销售数量-销售单价
    public void AddSaleOrder(String customername, String models, String shopcode, String customertel, String remark, String address,String token, SCResponseListener listener){
        String sendbody =  "token=" + token + "&shopcode=" + shopcode + "&models=" + models + "&customername=" + customername;
        if(customertel != null){
        sendbody = sendbody + "&customertel=" + customertel;
        }
        if(address != null){
            sendbody = sendbody + "&address=" + address;
        }
        if(remark != null){
            sendbody = sendbody + "&remark=" + remark;
        }
        String sendurl =  Constants.SERVER_IP + ":" + Constants.API_SERVER_PORT + Constants.TEST_IP + "/addsaleorder";

        HttpRunnable runnable = new HttpRunnable();
        runnable.setSendurl(sendurl);
        runnable.setSendbody(sendbody);
        runnable.setListener(listener);
        runnable.setType("ADDSALEORDER");
        ThreadPoolUtils.execute(runnable);
    }

    //查看同一位置的库存型号信息
    public void GetModelByPosition(String modelcode, String shopcode,String token, SCResponseListener listener){
        String sendbody =  "token=" + token + "&shopcode=" + shopcode + "&modelcode=" + modelcode;
        String sendurl = Constants.SERVER_IP + ":" + Constants.API_SERVER_PORT + Constants.TEST_IP + "/searchmodelbyposition";

        HttpRunnable runnable = new HttpRunnable();
        runnable.setSendurl(sendurl);
        runnable.setSendbody(sendbody);
        runnable.setListener(listener);
        runnable.setType("GETMODELBYP");
        ThreadPoolUtils.execute(runnable);
    }

    //获取登录账户的菜单权限
    public void GetAccountPower(String shopcode,String token, SCResponseListener listener){
        String sendbody =  "token=" + token + "&shopcode=" + shopcode ;
        String sendurl = Constants.SERVER_IP + ":" + Constants.API_SERVER_PORT + Constants.TEST_IP + "/getaccountpowerofmenu";

        HttpRunnable runnable = new HttpRunnable();
        runnable.setSendurl(sendurl);
        runnable.setSendbody(sendbody);
        runnable.setListener(listener);
        runnable.setType("GETACCOUNTP");
        ThreadPoolUtils.execute(runnable);
    }

    //查看当前登录账户反馈问题列表
    public void ListQuestion(String shopcode,String token, SCResponseListener listener){
        String sendbody =  "token=" + token + "&shopcode=" + shopcode ;
        String sendurl =  Constants.SERVER_IP + ":" + Constants.API_SERVER_PORT + Constants.TEST_IP + "/listquestions";

        HttpRunnable runnable = new HttpRunnable();
        runnable.setSendurl(sendurl);
        runnable.setSendbody(sendbody);
        runnable.setListener(listener);
        runnable.setType("LISTQ");
        ThreadPoolUtils.execute(runnable);
    }

    //查看单个问题明细
    public void GetQuestionDetail(String shopcode,String token, String questioncode,SCResponseListener listener){
        String sendbody =  "token=" + token + "&shopcode=" + shopcode + "&questioncode=" + questioncode ;
        String sendurl = Constants.SERVER_IP + ":" + Constants.API_SERVER_PORT + Constants.TEST_IP + "/getquestiondetail";

        HttpRunnable runnable = new HttpRunnable();
        runnable.setSendurl(sendurl);
        runnable.setSendbody(sendbody);
        runnable.setListener(listener);
        runnable.setType("QUESTIOND");
        ThreadPoolUtils.execute(runnable);
    }

    //反馈问题
    public void SubmitQuestion(String shopcode,String token, String title, String detail, SCResponseListener listener){
        String sendbody =  "token=" + token + "&shopcode=" + shopcode + "&title=" + title + "&detail=" + detail ;
        String sendurl =  Constants.SERVER_IP + ":" + Constants.API_SERVER_PORT + Constants.TEST_IP + "/submitquestion";

        HttpRunnable runnable = new HttpRunnable();
        runnable.setSendurl(sendurl);
        runnable.setSendbody(sendbody);
        runnable.setListener(listener);
        runnable.setType("SUBMITQ");
        ThreadPoolUtils.execute(runnable);
    }

    //查询物料价值统计结果列表
    public void SearchModelValue(String shopcode,String token, String key,SCResponseListener listener){
        String sendbody =  "token=" + token + "&shopcode=" + shopcode;
        if(key != null && !key.equals("")){
            sendbody = sendbody + "&key=" + key;
        }
        String sendurl = Constants.SERVER_IP + ":" + Constants.API_SERVER_PORT + Constants.TEST_IP + "/searchmodelvalue";

        HttpRunnable runnable = new HttpRunnable();
        runnable.setSendurl(sendurl);
        runnable.setSendbody(sendbody);
        runnable.setListener(listener);
        runnable.setType("SEARCHMV");
        ThreadPoolUtils.execute(runnable);
    }


    //获取登录账户操作权限
    public void GetAccountModelPower(String shopcode,String token,SCResponseListener listener){
        String sendbody =  "token=" + token + "&shopcode=" + shopcode;
        String sendurl =  Constants.SERVER_IP + ":" + Constants.API_SERVER_PORT + Constants.TEST_IP + "/getaccountpowerofaction";

        HttpRunnable runnable = new HttpRunnable();
        runnable.setSendurl(sendurl);
        runnable.setSendbody(sendbody);
        runnable.setListener(listener);
        runnable.setType("GETMODELPOWER");
        ThreadPoolUtils.execute(runnable);
    }

    //验证序列号
    public void CheckSN(String sn,SCResponseListener listener){
        String sendbody =  "sn=" + sn;
        String sendurl = "https://app.fssocks.com/api/erp/checksn";

        HttpRunnable runnable = new HttpRunnable();
        runnable.setSendurl(sendurl);
        runnable.setSendbody(sendbody);
        runnable.setListener(listener);
        runnable.setType("CHECKSN");
        ThreadPoolUtils.execute(runnable);
    }

    //获取门店结算信息集合
    public void GetPayModes(String shopcode,String token,SCResponseListener listener){
        String sendbody =  "token=" + token + "&shopcode=" + shopcode;
        String sendurl =  Constants.SERVER_IP + ":" + Constants.API_SERVER_PORT + Constants.TEST_IP + "/getpaymodes";

        HttpRunnable runnable = new HttpRunnable();
        runnable.setSendurl(sendurl);
        runnable.setSendbody(sendbody);
        runnable.setListener(listener);
        runnable.setType("GETPAYMODES");
        ThreadPoolUtils.execute(runnable);
    }

    //结算销售单
    public void SettleSaleOrder(String saleordercode, int paymode, double acturalmoney, String remark, String shopcode,String token,SCResponseListener listener){
        String sendbody =  "token=" + token + "&shopcode=" + shopcode + "&saleordercode=" + saleordercode + "&paymode=" + paymode + "&acturalmoney=" + acturalmoney;
       if(remark != null){
           sendbody = sendbody + "&remark=" + remark;
       }
        String sendurl =  Constants.SERVER_IP + ":" + Constants.API_SERVER_PORT + Constants.TEST_IP + "/settlesaleorder";

        HttpRunnable runnable = new HttpRunnable();
        runnable.setSendurl(sendurl);
        runnable.setSendbody(sendbody);
        runnable.setListener(listener);
        runnable.setType("SETTLESALEORDER");
        ThreadPoolUtils.execute(runnable);
    }

    //销售单发货
    //storedate发货日期默认当天
    //models=====项唯一编码-型号编码-发货数量-[序列号1:序列号2:序列号3],项唯一编码-型号编码-发货数量
    public void StoreSaleOrder(String saleordercode, String storedate, String logisticscode, String logisticspic, String remark, String models, String shopcode,String token,SCResponseListener listener){
        String sendbody =  "token=" + token + "&shopcode=" + shopcode + "&saleordercode=" + saleordercode + "&storedate=" + storedate + "&models=" + models;
        if(remark != null){
            sendbody = sendbody + "&remark=" + remark;
        }
        if(logisticscode != null){
            sendbody = sendbody + "&logisticscode=" + logisticscode;
        }
        if(logisticspic != null)
        {
            sendbody = sendbody + "&logisticspic=" + logisticspic;
        }
        String sendurl =  Constants.SERVER_IP + ":" + Constants.API_SERVER_PORT + Constants.TEST_IP + "/storesaleorder";

        HttpRunnable runnable = new HttpRunnable();
        runnable.setSendurl(sendurl);
        runnable.setSendbody(sendbody);
        runnable.setListener(listener);
        runnable.setType("STORESALEORDER");
        ThreadPoolUtils.execute(runnable);
    }

    //修改销售单客户信息
    public void ModeifySaleorderCustomer(String saleordercode,String customername,String customertel, String receivername, String receivertel,  String remark, String address, String shopcode,String token,SCResponseListener listener){
        String sendbody =  "token=" + token + "&shopcode=" + shopcode + "&saleordercode=" + saleordercode + "&customername=" + customername+ "&customertel=" + customertel;
        if(remark != null){
            sendbody = sendbody + "&remark=" + remark;
        }
        if(receivername != null){
            sendbody = sendbody + "&receivername=" + receivername;
        }
        if(receivertel != null)
        {
            sendbody = sendbody + "&receivertel=" + receivertel;
        }
        if(address != null)
        {
            sendbody = sendbody + "&address=" + address;
        }
        String sendurl =  Constants.SERVER_IP + ":" + Constants.API_SERVER_PORT + Constants.TEST_IP + "/modifysaleordercustomerinfo";

        HttpRunnable runnable = new HttpRunnable();
        runnable.setSendurl(sendurl);
        runnable.setSendbody(sendbody);
        runnable.setListener(listener);
        runnable.setType("MODIFYSALEORDERCUSTOMER");
        ThreadPoolUtils.execute(runnable);
    }

    //获取销售单对应的发货单列表
    public void ListSaleorderStores(String saleordercode,String shopcode,String token,SCResponseListener listener){
        String sendbody =  "token=" + token + "&shopcode=" + shopcode + "&saleordercode=" + saleordercode;
        String sendurl =  Constants.SERVER_IP + ":" + Constants.API_SERVER_PORT + Constants.TEST_IP + "/listsaleorderstoreinfos";

        HttpRunnable runnable = new HttpRunnable();
        runnable.setSendurl(sendurl);
        runnable.setSendbody(sendbody);
        runnable.setListener(listener);
        runnable.setType("LISTSALEORDERSTORE");
        ThreadPoolUtils.execute(runnable);
    }

    //获取销售发货单明细
    public void GetSaleStoreorder(String saleordercode, String storeordercode, String shopcode,String token,SCResponseListener listener){
        String sendbody =  "token=" + token + "&shopcode=" + shopcode + "&saleordercode=" + saleordercode + "&storeordercode=" + storeordercode;
        String sendurl =  Constants.SERVER_IP + ":" + Constants.API_SERVER_PORT + Constants.TEST_IP + "/getsalestoreorderinfo";

        HttpRunnable runnable = new HttpRunnable();
        runnable.setSendurl(sendurl);
        runnable.setSendbody(sendbody);
        runnable.setListener(listener);
        runnable.setType("GETSALESTOREORDER");
        ThreadPoolUtils.execute(runnable);
    }

    //批量扫码出库
    public void BatchStore(String sns, String remark, int reason, String shopcode,String token,SCResponseListener listener){
        String sendbody =  "token=" + token + "&shopcode=" + shopcode + "&sns=" + sns + "&reason=" + reason;
        if(remark != null){
            sendbody = sendbody + "&remark=" + remark;
        }
        String sendurl =  Constants.SERVER_IP + ":" + Constants.API_SERVER_PORT + Constants.TEST_IP + "/batchsnoutstore";

        HttpRunnable runnable = new HttpRunnable();
        runnable.setSendurl(sendurl);
        runnable.setSendbody(sendbody);
        runnable.setListener(listener);
        runnable.setType("BATCHSTORE");
        ThreadPoolUtils.execute(runnable);
    }
    //获取销售出库单明细
    public void GetSaleDetail(String saleordercode, String shopcode,String token, SCResponseListener listener){
        String sendbody =  "token=" + token + "&shopcode=" + shopcode + "&saleordercode=" + saleordercode;
        String sendurl =  Constants.SERVER_IP + ":" + Constants.API_SERVER_PORT + Constants.TEST_IP + "/getsaleorderdetail";

        HttpRunnable runnable = new HttpRunnable();
        runnable.setSendurl(sendurl);
        runnable.setSendbody(sendbody);
        runnable.setListener(listener);
        runnable.setType("GETSALEDETAIL");
        ThreadPoolUtils.execute(runnable);
    }

    //获取销售单待发货明细
    public void GetSaleOrderDetail(String saleordercode,  String shopcode,String token,SCResponseListener listener){
        String sendbody =  "saleordercode=" + saleordercode + "&token=" + token + "&shopcode=" + shopcode ;

        String sendurl =  Constants.SERVER_IP + ":" + Constants.API_SERVER_PORT + Constants.TEST_IP + "/getsaleorderstockoutdetail";

        HttpRunnable runnable = new HttpRunnable();
        runnable.setSendurl(sendurl);
        runnable.setSendbody(sendbody);
        runnable.setListener(listener);
        runnable.setType("SALEORDERDETAIL");
        ThreadPoolUtils.execute(runnable);
    }
    //批量扫码同步到PC端,guid二维码扫码返回valuecode字段值
    public void BatchTransfer(String guid,String sns,  String shopcode,String token,SCResponseListener listener){
        String sendbody =  "guid=" + guid + "&sns=" + sns + "&token=" + token + "&shopcode=" + shopcode ;

        String sendurl =  Constants.SERVER_IP + ":" + Constants.API_SERVER_PORT + Constants.TEST_IP + "/batchsntransfer";

        HttpRunnable runnable = new HttpRunnable();
        runnable.setSendurl(sendurl);
        runnable.setSendbody(sendbody);
        runnable.setListener(listener);
        runnable.setType("BATCHTRANS");
        ThreadPoolUtils.execute(runnable);
    }

    //版本说明查看
    public void GetVersionRemark(String version,  String shopcode,SCResponseListener listener){
        String sendbody =  "version=" + version +  "&shopcode=" + shopcode ;

        String sendurl =  Constants.SERVER_IP + ":" + Constants.API_SERVER_PORT + Constants.TEST_IP + "/getversionremark";

        HttpRunnable runnable = new HttpRunnable();
        runnable.setSendurl(sendurl);
        runnable.setSendbody(sendbody);
        runnable.setListener(listener);
        runnable.setType("VERSIONREMARK");
        ThreadPoolUtils.execute(runnable);
    }

    //获取指定序列号条码的进出库历史信息
    public void GetSNHistory(String sn,  String shopcode,String token,SCResponseListener listener){
        String sendbody =  "sn=" + sn + "&token=" + token + "&shopcode=" + shopcode ;

        String sendurl =  Constants.SERVER_IP + ":" + Constants.API_SERVER_PORT + Constants.TEST_IP + "/getsnhistoryinfo";

        HttpRunnable runnable = new HttpRunnable();
        runnable.setSendurl(sendurl);
        runnable.setSendbody(sendbody);
        runnable.setListener(listener);
        runnable.setType("GetSNHistory");
        ThreadPoolUtils.execute(runnable);
    }
}
