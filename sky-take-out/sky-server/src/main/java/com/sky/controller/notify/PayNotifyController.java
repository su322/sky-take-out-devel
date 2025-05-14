package com.sky.controller.notify;

import com.alibaba.druid.support.json.JSONUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sky.properties.WeChatProperties;
import com.sky.service.OrderService;
import com.wechat.pay.contrib.apache.httpclient.util.AesUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.entity.ContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

//不要删，灰色是因为在括号里没检测到
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * 支付回调相关接口
 */
@RestController
@RequestMapping("/notify")
@Slf4j
public class PayNotifyController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private WeChatProperties weChatProperties;

    /**
     * 支付成功回调
     *
     * @param request
     */
    @RequestMapping("/paySuccess")
    public void paySuccessNotify(HttpServletRequest request, HttpServletResponse response) throws Exception {
//        //读取数据
//        String body = readData(request);
//        log.info("支付成功回调：{}", body);
//
//        //数据解密
//        String plainText = decryptData(body);
//        log.info("解密后的文本：{}", plainText);
//
//        JSONObject jsonObject = JSON.parseObject(plainText);
//        String outTradeNo = jsonObject.getString("out_trade_no");//商户平台订单号
//        String transactionId = jsonObject.getString("transaction_id");//微信支付交易号
//
//        log.info("商户平台订单号：{}", outTradeNo);
//        log.info("微信支付交易号：{}", transactionId);
//
//        //业务处理，修改订单状态、来单提醒
//        orderService.paySuccess(outTradeNo);
//
//        //给微信响应
//        responseToWeixin(response);

        //模拟请求 TODO 这步就里面的修改数据库有用了，模拟的请求响应好像没用
        try {
            //读取数据
            String body = request instanceof MockHttpServletRequest ?
                    new String(((MockHttpServletRequest)request).getContentAsByteArray(), StandardCharsets.UTF_8) :
                    readData(request);

            log.info("支付回调数据：{}", body);

            // 如果是模拟请求，跳过解密直接处理
            JSONObject jsonObject;
            if (request instanceof MockHttpServletRequest) {
                jsonObject = JSON.parseObject(body);
            } else {
                String plainText = decryptData(body);
                jsonObject = JSON.parseObject(plainText);
            }

            String outTradeNo = jsonObject.getString("out_trade_no");
            orderService.paySuccess(outTradeNo);
            // 给微信响应
            responseToWeixin(response);
        } catch (Exception e) {
            log.error("支付回调处理失败", e);
            response.setStatus(500);
        }
    }

    /**
     * 读取数据
     *
     * @param request
     * @return
     * @throws Exception
     */
    private String readData(HttpServletRequest request) throws Exception {
        BufferedReader reader = request.getReader();
        StringBuilder result = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            if (result.length() > 0) {
                result.append("\n");
            }
            result.append(line);
        }
        return result.toString();
    }

    /**
     * 数据解密
     *
     * @param body
     * @return
     * @throws Exception
     */
    private String decryptData(String body) throws Exception {
        JSONObject resultObject = JSON.parseObject(body);
        JSONObject resource = resultObject.getJSONObject("resource");
        String ciphertext = resource.getString("ciphertext");
        String nonce = resource.getString("nonce");
        String associatedData = resource.getString("associated_data");

        AesUtil aesUtil = new AesUtil(weChatProperties.getApiV3Key().getBytes(StandardCharsets.UTF_8));
        //密文解密
        String plainText = aesUtil.decryptToString(associatedData.getBytes(StandardCharsets.UTF_8),
                nonce.getBytes(StandardCharsets.UTF_8),
                ciphertext);

        return plainText;
    }

    /**
     * 给微信响应
     * @param response
     */
    private void responseToWeixin(HttpServletResponse response) throws Exception{
        response.setStatus(200);
        HashMap<Object, Object> map = new HashMap<>();
        map.put("code", "SUCCESS");
        map.put("message", "SUCCESS");
        response.setHeader("Content-type", ContentType.APPLICATION_JSON.toString());
        response.getOutputStream().write(JSONUtils.toJSONString(map).getBytes(StandardCharsets.UTF_8));
        response.flushBuffer();
    }
}
