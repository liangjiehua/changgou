package entity;


import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;

import java.util.Random;

/**
 * 发送短信
 */
public class SMSUtils {

    public static final String VALIDATE_CODE = "SMS_174807964"; //畅购验证码模板


    /**
     * 发送验证码
     * @param phone 手机号
     */
    public static String sendMessage(String phone) throws ClientException {
        //设置超时时间
        System.setProperty("sun.net.client.defaultConnectTimeout","10000");
        System.setProperty("sun.net.client.defaultReadTimeout","10000");
        //初始化ascClient需要的参数
        final String product = "Dysmsapi";//短信API产品名（固定，无需更改）
        final String domain = "dysmsapi.aliyuncs.com";//短信api域名，固定
        //accesskey
        final String accessKeyId = "LTAI4FnrEcAkWaxFCus4CqgE";
        final String accessKeySecret = "nyacqV4HEivYhc08ZHH9tyaZJzVOmd";
        //初始化ascClient
        IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId, accessKeySecret);
        DefaultProfile.addEndpoint("cn-hangzhou","cn-hangzhou",product,domain);
        IAcsClient acsClient = new DefaultAcsClient(profile);
        //组装请求参数
        SendSmsRequest request = new SendSmsRequest();
        //post请求
        request.setMethod(MethodType.POST);
        //设置手机号
        request.setPhoneNumbers(phone);
        //设置短信签名
        request.setSignName("畅购");
        //短信模板
        request.setTemplateCode(VALIDATE_CODE);
        //验证码内容
        String code = getCode(6);
        request.setTemplateParam("{\"code\":\"" + code + "\"}");
        //发送短信
        SendSmsResponse acsResponse = acsClient.getAcsResponse(request);
        if (acsResponse.getCode() != null && acsResponse.getCode().equals("OK")){
            return code;
        }
        return null;
    }


    /**
     * 生成随机验证码
     * @param length
     * @return
     */
    private static String getCode(int length) {
        Integer code = null;
        if (length == 4){
            code = new Random().nextInt(999);//随机生成随机数，最大为999
            if (code < 1000){
                //确保验证码为4位数
                code = code + 1000;
            }
        }else if (length == 6){
            code = new Random().nextInt(999999);
            if (code < 100000){
                code = code + 100000;//保证验证码为6位数
            }
        }else {
            throw new RuntimeException("只能生成4/6位数的验证码");
        }
        return code + "";
    }
}
