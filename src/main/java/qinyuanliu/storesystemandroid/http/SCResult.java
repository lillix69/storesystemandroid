package qinyuanliu.storesystemandroid.http;

import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by lillix on 5/30/18.
 */
public class SCResult extends Object{
    public SCResult() {
    }

    public class Result {
        public int code;
        public String errormsg;

        public Result() {
        }
    }

    public class LoginAccountResult {
        public int code;
        public String errormsg;
        public String loginname;
        public String nickname;
        public String token;//用于登录账户相关业务操作的认证,后面部分接口需要用到，注意token过期时间为一小时，过期后需要客户端重新登录
        public String shopname;

        public LoginAccountResult() {
        }
    }

    public class ModelResult {
        public int code;
        public String errormsg;
        public List<Model> models;

        public ModelResult() {
        }
    }

    public class Model {
        public String modelcode;
        public String modelname;
        public String spec;
        public String typename;//型号分类名称
        public int store;//库存
        public String unit;//单位
        public String sn;//不为空时表示时是具体带序列号的产品
        public String postion;//仓库货架位置

        public Model() {
        }
    }


    public class TypeResult {
        public int code;
        public String errormsg;
        public List<Type> modeltypes;

        public TypeResult() {
        }
    }

    public class Type {
        public String typecode;
        public String typename;

        public Type() {
        }
    }


    public class StoreResult {
        public int code;
        public String errormsg;
        public List<Store> stores;

        public StoreResult() {
        }
    }

    public class Store {
        public String code;
        public String name;

        public Store() {
        }
    }

    public class RackResult {
        public int code;
        public String errormsg;
        public List<Rack> racks;

        public RackResult() {
        }
    }

    public class Rack {
        public String code;
        public String name;

        public Rack() {
        }
    }

    public class PositionResult {
        public int code;
        public String errormsg;
        public List<Position> positions;

        public PositionResult() {
        }
    }

    public class Position {
        public String code;
        public String name;

        public Position() {
        }
    }

    public class ModelDetailResult {
        public int code;
        public String errormsg;
        public String modelcode;
        public String modelname;
        public String modelpicurl;
        public String storecode;
        public String rackcode;
        public String positioncode;
        public String storename;
        public String rackname;
        public String positionname;
        public int store;
        public int ismin;//是否是元器件 0：不是 1：是,
        public String typecode;
        public String typename;
        public String unit;
        public String spec;//guige
        public double lastedbuyin;
        public double lastedtrace;
        public String customercode;
        public int min;//安全阀值
        public int needcheck;//是否需要检验，0：不需要 1：需要
        public String suppliername;
        public String suppliercode;
        public String remark;
public int allowbatch;//是否允许配料，0：不允许 1：允许

        public ModelDetailResult() {
        }
    }

    public class BarcodeResult {
        public int code;
        public String errormsg;
        public List<Barcode> codes;

        public BarcodeResult() {
        }
    }

    public class Barcode {
        public String barcode;

        public Barcode() {
        }
    }


    public class ReasonResult {
        public int code;
        public String errormsg;
        public List<Reason> types;

        public ReasonResult() {
        }
    }

    public class Reason {
        public int code;
        public String name;

        public Reason() {
        }
    }

    public class ScanResult {
        public int code;
        public String errormsg;
        public int result;// 0:产品  1：型号 2：出入权限二维码 3：批量条码扫码二维码
        public String valuecode;

        public ScanResult() {
        }
    }

    public class ProductDetailResult {
        public int code;
        public String errormsg;
        public String sn;
        public int currentstatus;//产品状态1：待入库(蓝色) 2：已入库(绿色) 3：待检验（黄色）4：已出库（红色） 5：已报废（深灰色）
        public String currentstatusname;
        public String modelcode;
        public String modelname;
        public String modelpicurl;
        public String storecode;
        public String rackcode;
        public String positioncode;
        public String storename;
        public String rackname;
        public String positionname;
        public int store;
        public String typecode;
        public String typename;
        public String unit;
        public String spec;//guige
        public double lastedbuyin;
        public double lastedtrace;
        public String customercode;
        public int min;//安全阀值
        public int needcheck;//是否需要检验，0：不需要 1：需要
        public String suppliername;
        public String remark;
        public int isneedconfirmflow;//是否需要确认流程 0：不需要1：需要
public String confrimbuttontext;
        public int islaststep;//是否是最后一步 0不是  1是

        public ProductDetailResult() {
        }
    }

    public class VersionResult {
        public int code;
        public String errormsg;
        public int version;
        public String downloadurl;

        public VersionResult() {
        }
    }
    public class VersionRemarkResult {
        public int code;
        public String errormsg;
        public String remark;


        public VersionRemarkResult() {
        }
    }
    public class SnsResult {
        public int code;
        public String errormsg;
        public ArrayList<SnsModel> sninfos;

        public SnsResult() {
        }
    }

    public class SnsModel {
        public String sn;
        public String remark;

        public SnsModel() {
        }
    }

    public class QRcodeResult {
        public int code;
        public String errormsg;
        public String codevalue;

        public QRcodeResult() {
        }
    }

    public class SummeryResult {
        public int code;
        public String errormsg;
        public String summary;
        public int todaystatus;//今日下班报表状态 0：未确认 1：已确认
        public String confirmtime;

        public SummeryResult() {
        }
    }

    public class DutyoffModelDetail {
        public int code;
        public String errormsg;
        public String time;
        public String typename;
        public int count;

        public DutyoffModelDetail() {
        }
    }

    public class DutyoffModel {
        public int code;
        public String errormsg;
        public String modelcode;
        public String modelname;
        public String modelspec;
        public int count;
        public int status;////0：未确认 1：已确认
        public String remark;
        public ArrayList<DutyoffModelDetail> details;

        public DutyoffModel() {
        }
    }

    public class ProduceDetailModel{
        public String time;
        public String sn;
        public ProduceDetailModel(){}

    }
    public class ProduceModel{
        public String routecode;
        public String stepcode;
        public String modelcode;
        public String modelname;
        public String modelspec;
        public int count;
        public int status;////0：未确认 1：已确认
        public String remark;
        public ArrayList<ProduceDetailModel> details;

        public ProduceModel(){}
    }
    public class ProductStatusModel{

        public String summary;
        public String routecode;
        public String stepcode;
public ArrayList<ProduceModel> models;

        public ProductStatusModel(){}
    }
    public class DutyoffResult {
        public int code;
        public String errormsg;
        public String date;
        public String weekofday;
        public int status;//0:未确认 1：已确认
        public int outstoremodelcount;//今日出库型号种类数量
        public ArrayList<DutyoffModel> outstoremodels;
        public int instoremodelcount;////今日入库型号种类数量
        public ArrayList<DutyoffModel> instoremodels;
public ArrayList<ProductStatusModel> productstatusinfos;

        public DutyoffResult() {
        }
    }

    public class DutyoffHistory{
        public int status;//0：未确认 1：已确认
        public String date;
        public String statusname;
        public String confirmtime;

        public DutyoffHistory(){}
    }

    public class DutyoffHistoryResult{
        public int code;
        public String errormsg;
        public ArrayList<DutyoffHistory> details;

        public DutyoffHistoryResult(){}
    }

    public class ProveResult {
        public int code;
        public String errormsg;
        public String modelcode;
        public int power;// 0:全部权限 1：只有入库权限 2：只有出库权限 3只有出入库权限
                          //4只有盘库权限   5只有入库盘库权限   6只有出库盘库权限

        public ProveResult() {
        }
    }

    public class PushCountResult {
        public int code;
        public String errormsg;
        public int count;

        public PushCountResult() {
        }
    }


    public class PushListInfo{
        public String workcode;
        public String workname;
        public String worktime;
        public String workcontent;

        public PushListInfo(){

        }
    }

    public class PushListResult {
        public int code;
        public String errormsg;
        public ArrayList<PushListInfo> details;

        public PushListResult() {
        }
    }

    public class StepInfo{
        public String stepcode;
        public String stepname;

        public StepInfo(){

        }
    }
    public class StepResult {
        public int code;
        public String errormsg;
        public ArrayList<StepInfo> steps;

        public StepResult() {
        }
    }

    public class PrepareInfo{
        public String modelcode;
        public String modelname;
        public String modelspec;
public int count;
        public String position;
        public int issn;//0：不是带序列号的 1：带序列号的

        public PrepareInfo(){

        }
    }
    public class PrepareResult {
        public int code;
        public String errormsg;
        public String modelcode;
        public String modelname;
        public String spec;
        public String stepcode;
        public String stepname;
        public int count;
        public ArrayList<PrepareInfo> details;

        public PrepareResult() {
        }
    }

    public class SubmitPrepare{
        public String modelcode;
        public String modelname;
        public String modelspec;
        public int count;
        public String position;
        public int issn;//0：不是带序列号的 1：带序列号的
        public ArrayList<String> sns;

        public SubmitPrepare(){

        }
    }
    public class SubmitPrepareResult {
        public int code;
        public String errormsg;
        public String modelcode;
        public String modelname;
        public String spec;
        public String stepcode;
        public String stepname;
        public int count;
        public ArrayList<SubmitPrepare> details;

        public SubmitPrepareResult() {
        }
    }

    public class IsneedProjectResult {
        public int code;
        public String errormsg;
        public int isneed;//0不需要 1需要
        public String projectcode;//可为空
        public String projectname;//可为空


        public IsneedProjectResult() {
        }
    }

    public class ProjectInfo{
        public String projectcode;
        public String projectname;

        public ProjectInfo(){

        }
    }
    public class ProjectResult {
        public int code;
        public String errormsg;
        public ArrayList<ProjectInfo> projects;

        public ProjectResult() {
        }
    }

    public class SnHistoryResult {
        public int code;
        public String errormsg;
        public String historyinfo;

        public SnHistoryResult() {
        }
    }


    public class DevelopInfo{
        public String projectmodelcode;
        public String projectmodelname;
        public String projectcode;
        public String projectname;

        public DevelopInfo(){}
    }

    public class DevelopResult{
        public int code;
        public String errormsg;
        public ArrayList<DevelopInfo> projectmodels;

        public DevelopResult(){}
    }

    public class PurchaseOrderInfo{
        public String purchaseordercode;
        public String purchasedate;
        public String username;
        public String allmoney;

        public PurchaseOrderInfo(){}
    }

    public class PurchaseOrderResult{
        public int code;
        public String errormsg;
        public ArrayList<PurchaseOrderInfo> purchaseorders;

        public PurchaseOrderResult(){}
    }
    public class PurchaseStoreOrderInfo{
        public String instorecode;
        public String instoredate;
        public String username;
        public String totalstorenum;

        public PurchaseStoreOrderInfo(){}
    }

    public class PurchaseStoreOrderResult{
        public int code;
        public String errormsg;
        public ArrayList<PurchaseStoreOrderInfo> orders;

        public PurchaseStoreOrderResult(){}
    }
     public class PurchaseDetailInfo{
         public String lineno;
         public String modelcode;
         public String modelname;
         public String spec;
         public String price;
         public int count;
         public String supplier;
         public String suppliercode;
         public String remark;
         public int ismin;//是否是带序列号 1：不是 0：是
         public String sns;

         public PurchaseDetailInfo(){}
     }
    public class PurchaseDetailResult{
        public int code;
        public String errormsg;
        public String purchaseordercode;
        public String purchasedate;
        public String username;
        public ArrayList<PurchaseDetailInfo> purchasemodels;

        public PurchaseDetailResult(){}
    }

    public class PurchaseStoreDetailResult{
        public int code;
        public String errormsg;
        public String instorecode;
        public String instoredate;
        public String username;
        public ArrayList<PurchaseStoreDetailInfo> purchasemodels;

        public PurchaseStoreDetailResult(){}
    }
    public class PurchaseStoreDetailInfo{
        public String modelcode;
        public String modelname;
        public String spec;
        public String price;
        public int count;
        public String supplier;
        public String remark;
        public int ismin;//是否是带序列号 1：不是 0：是
        public String sns;

        public PurchaseStoreDetailInfo(){}
    }


    public class SupplierInfo{
        public String supplierCode;
        public String suppliername;

        public SupplierInfo(){}
    }
    public class SupplierResult{
        public int code;
        public String errormsg;
        public ArrayList<SupplierInfo> suppliers;
        public SupplierResult(){}
    }


    public class PurchaseInfo{
        public String ordercode;
        public String orderdate;
        public int purchasenum;
        public String totalprice;
        public String remark;

        public PurchaseInfo(){}
    }

    public class PurchaseListResult{
        public int code;
        public String errormsg;
        public ArrayList<PurchaseInfo> orders;
        public PurchaseListResult(){}
    }


    public class PurchaseModel{
        public String lineno;
        public String materielname;
        public String modelcode;
        public String modelname;
        public String materielspec;
        public String price;
        public int count;//入库数量
        public String supplier;
        public String suppliercode;
        public String remark;
        public int ismin;//是否是带序列号 1：不是 0：是
        public String sns;

        public PurchaseModel(){}
    }
    public class PurchaseModelResult{
        public int code;
        public String errormsg;
        public String ordercode;
        public String orderdate;
        public ArrayList<PurchaseModel> models;
        public PurchaseModelResult(){}
    }


    public class SaleOrderInfo{
        public String saleordercode;
        public String saleorderdate;
        public String customername;
        public String customertel;
        public String storestatustext;
        public String money;
        public String paystatustext;
    }
    public class SaleOrderResult{
        public int code;
        public String errormsg;
        public ArrayList<SaleOrderInfo> saleorders;

        public SaleOrderResult(){}
    }

    public class SaleDetailInfo{
        public String lineno;
        public String modelcode;
        public String modelname;
        public String spec;
        public String price;
        public int salescount;//销售数量
        public int storedcount;//已发货数量
        public String sns;//出库序列号
        public int ismin;//是否是元器件 0：不是 1：是, 不是元器件要扫描二维码

        public SaleDetailInfo(){}
    }

    public class SaleDetailResult{
        public int code;
        public String errormsg;
        public String saleordercode;
        public String saledate;
        public String storestatustext;
        public String paytatustext;
        public String customername;
        public String customertel;
        public String receivername;
        public String receiver;//收货人手机
        public String address;
        public String money;
        public String remark;
        public int showmodifycustomerbutton;//0不显示  1显示
        public int buttonstatus; //0全部不显示  1只显示去发货按钮  2只显示去结算按钮 3全部显示
        public ArrayList<SaleDetailInfo> salemodels;
        public SaleDetailResult(){}
    }

    public class AccountPowerResult{
        public int code;
        public String errormsg;
        public int power;//0全部权限  1只有首页权限  2只有首页和采购入库  3 只有首页和销售入库

        public AccountPowerResult(){}
    }

    public class ListQuestionResult{
        public int code;
        public String errormsg;
public ArrayList<QuestionModel> questions;
        public ListQuestionResult(){}
    }

    public class QuestionModel{
        public String questioncode;
        public String title;
        public String questiontime;
        public String creater;
        public int status;//状态 暂时用不到 0：未解决 1：已解决

        public QuestionModel(){}
    }

    public class QuestionDetailResult{
        public int code;
        public String errormsg;
        public String questioncode;
        public String title;
        public String questiontime;
        public String creater;
        public int status;//状态 暂时用不到 0：未解决 1：已解决
        public String detail;

        public QuestionDetailResult(){}
    }

    public class ModelValueResult{
        public int code;
        public String errormsg;
        public String summary;
        public ArrayList<ModelValue> models;
        public ModelValueResult(){}
    }

    public class ModelValue{
        public String modelcode;
        public String modelname;
        public String spec;//规格
        public String lastedprice;
        public int store;//当前库存
        public String storevalue;
        public int outstore;//已出库
        public String outstorevalue;
    }

    public class ModelPowerResult{
        public int code;
        public String errormsg;
        public int modelpower;//新增型号 0：无权限 1：可以新增型号 2：可以修改型号 3：可以新增和修改型号

        public ModelPowerResult(){}
    }

    public class CheckSNResult{
        public int code;
        public String errormsg;
        public ArrayList<CheckShop> shops;

        public CheckSNResult(){}
    }

    public class CheckShop{
        public String shopname;
        public String shopcode;//门店号
        public String server;
        public String port;
    }

    public class ModelRuleResult{
        public int code;
        public String errormsg;
        public ArrayList<ModelRule> modelrules;

    }

    public class ModelRule{
        public String modelrulecode;
        public String modelrulename;
    }

    public class PayModeResult{
        public int code;
        public String errormsg;
        public ArrayList<PayMode> paymodes;
    }

    public class PayMode{
        public int modecode;
        public String modename;
    }

    public class StoreOrderResult{
        public int code;
        public String errormsg;
        public String saleordercode;
        public String saledate;
        public String storestatustext;
        public String paystatustext;
        public String customername;
        public String customertel;
        public String receivername;
        public String receiver;
        public String address;
        public String remark;
        public ArrayList<StoreOrder> storeorders;
    }
    public class StoreOrder{
        public String storeordercode;
        public String storeorderdate;
        public String logisticsurl;
        public String logisticscode;
        public int ismin;//是否是带序列号 1：不是 0：是
        public String remark;
    }

    public class SaleStoreOrderResult{
        public int code;
        public String errormsg;
        public String saleordercode;
        public String saledate;//下单日期
        public String storedate;//发货日期
        public String paystatustext;
        public String customername;
        public String customertel;
        public String receivername;
        public String receiver;
        public String address;
        public String logisticscode;
        public String logisticsurl;
        public String logisticsremark;
        public String remark;
        public ArrayList<StoreOrderModel> storeordermodels;
    }

    public class SaleOrderStoreResult{
        public int code;
        public String errormsg;
        public String saleordercode;
        public String saledate;//下单日期
        public String storestatustext;
        public String paytatustext;
        public String customername;
        public String customertel;
        public String receivername;
        public String receiver;
        public String address;
        public String money;
        public String remark;
        public int showmodifycustomerbutton;//是否显示修改客户信息按钮 0：不显示 1：显示
        public int buttonstatus;//发货和结算的按钮是否显示  0：全部不显示 1：只显示 去发货按钮 2：只显示去结算按钮 3：全部显示
        public ArrayList<SaleOrderModel> salemodels;
    }
    public class SaleOrderModel{
        public String lineno;
        public int ismin;//是否是带序列号 1：不是 0：是
        public String modelcode;
        public String modelname;
        public String spec;
        public String price;
        public int salescount;//销售数量
        public int storedcount;//已发货数量
        public int mealproduct;//0表示非套餐 1表示是套餐
        public String mealproductname;
        public String sns;//出库序列号
    }

    public class StoreOrderModel{
        public String modelcode;
        public String modelname;
        public int salescount;
        public int storecount;
        public String sns;
        public String spec;

    }

    public class AddSaleOrderResult{
        public int code;
        public String errormsg;
        public String saleordercode;
    }

}

