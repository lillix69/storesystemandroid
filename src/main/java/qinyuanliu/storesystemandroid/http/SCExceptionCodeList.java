package qinyuanliu.storesystemandroid.http;

/**
 * Created by lillix on 6/26/17.
 */
public final class SCExceptionCodeList {
    public SCExceptionCodeList() {
    }

    //外部调用:传入errorcode,返回错误提示信息
    public static String getExceptionMsg(int code) {
        String msg = "未知错误";
        switch(code) {
            case 200:
                return "操作成功";
            case 201:
                return "客户端版本不对,需升级sdk";
            case 301:
            return "接入账号被封禁";
            case 302:
            return "接入用户名或密码错误";
            case 303:
            return "接口访问受限";
            case 304:
                return "header请求超时";
            case 315:
            return "IP受限";
            case 316:
                return "token过期正在刷新中,请稍后再试";
            case 404:
                return "查询结果为空";
            case 405:
            return "请求参数过长";
            case 408:
                return "客户端请求超时";
            case 413:
            return "客户端认证参数有误";
            case 414:
                return "请求参数无效";
            case 415:
                return "客户端网络问题";
            case 416:
                return "该注册的手机号已被注册,可通过点击忘记密码来找回";
            case 417:
                return "登录账户名或密码错误";
            case 420:
                return "不支持此操作";
            case 431:
            return "HTTP重复请求";
            case 500:
            return "服务器内部错误";
            case 503:
            return "服务器繁忙";
            case 600:
                return "网络无法到达";
            case 998:
            return "解包错误";
            case 999:
                return "打包错误";

            default:
                return msg;
        }
    }
}
