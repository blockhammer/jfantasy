package com.fantasy.payment.product;

import com.fantasy.framework.util.web.WebUtil;
import com.fantasy.payment.bean.Payment;
import com.fantasy.payment.bean.PaymentConfig;
import com.fantasy.payment.order.OrderDetails;
import com.fantasy.payment.service.PaymentContext;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 支付宝（担保交易）
 */
public class AlipayPartner extends AbstractAlipayPaymentProduct {
    /**
     * 支付宝消息验证地址
     */
    public static final String PAYMENT_URL = "https://mapi.alipay.com/gateway.do?_input_charset=" + input_charset;// 支付请求URL

    @Override
    public String getPaymentUrl() {
        return PAYMENT_URL;
    }

    @Override
    public Map<String, String> getParameterMap(Map<String, String> parameters) {
        PaymentContext context = PaymentContext.getContext();
        PaymentConfig paymentConfig = context.getPaymentConfig();
        OrderDetails orderDetails = context.getOrderDetails();
        Payment payment = context.getPayment();

        String body = orderDetails.getSubject();// 订单描述
        String logistics_fee = "0";// 物流费用
        String logistics_payment = "SELLER_PAY";// 物流支付方式（SELLER_PAY：卖家承担运费、BUYER_PAY：买家承担运费）
        String logistics_type = "EXPRESS";// 物流类型（EXPRESS：快递、POST：平邮、EMS：EMS）
        String notify_url = PaymentContext.getContext().getNotifyUrl(payment.getSn());// 消息通知URL
        AtomicReference<String> out_trade_no = new AtomicReference<String>(payment.getSn());// 支付编号
        String partner = paymentConfig.getBargainorId();// 合作身份者ID
        String payment_type = "1";// 支付类型（固定值：1）
        String price = String.format("%.2f", orderDetails.getPayableFee());// 总金额（单位：元）
        String quantity = "1";// 商品数量
        String return_url = PaymentContext.getContext().getReturnUrl(payment.getSn());// 回调处理URL
        String seller_id = paymentConfig.getSellerEmail();// 商家ID 如："shenzhenying@haoluesoft.com";
        String service = "create_partner_trade_by_buyer";// 接口类型（create_partner_trade_by_buyer：担保交易）
        String show_url = PaymentContext.getContext().getShowUrl(orderDetails.getSN());// 支付结果显示URL
        String sign_type = "MD5";//签名加密方式（MD5）
        AtomicReference<String> subject = new AtomicReference<String>(payment.getSn());// 订单的名称、标题、关键字等
        String key = paymentConfig.getBargainorKey();// 密钥

        //TODO 担保支付时的收货人信息如何设置
        String receive_name = "昊略软件";//收货人姓名
        String receive_address = "上海市徐汇区田林路140号28号楼G09";//收货人地址
        String receive_zip = "200233";//收货人邮编
        String receive_phone = "0571-88158090";//收货人电话号码
        String receive_mobile = "15921884771";//收货人手机号码

        // 生成签名
        Map<String, String> signMap = new LinkedHashMap<String, String>();

        signMap.put("service", service);
        signMap.put("partner", partner);
        signMap.put("_input_charset", input_charset);
        signMap.put("payment_type", payment_type);
        signMap.put("notify_url", notify_url);
        signMap.put("return_url", return_url);
        signMap.put("seller_email", seller_id);
        signMap.put("out_trade_no", out_trade_no.get());
        signMap.put("subject", subject.get());
        signMap.put("price", price);
        signMap.put("quantity", quantity);
        signMap.put("logistics_fee", logistics_fee);
        signMap.put("logistics_type", logistics_type);
        signMap.put("logistics_payment", logistics_payment);
        signMap.put("body", body);
        signMap.put("show_url", show_url);
        signMap.put("receive_name", receive_name);
        signMap.put("receive_address", receive_address);
        signMap.put("receive_zip", receive_zip);
        signMap.put("receive_phone", receive_phone);
        signMap.put("receive_mobile", receive_mobile);
        String sign = DigestUtils.md5Hex(getParameterString(signMap) + key);
        // 参数处理
        Map<String, String> parameterMap = new HashMap<String, String>(paraFilter(signMap));
        parameterMap.put("sign_type", sign_type);
        parameterMap.put("sign", sign);
        return parameterMap;
    }

    @Override
    public boolean verifySign(Map<String, String> parameters) {
        PaymentConfig paymentConfig = PaymentContext.getContext().getPaymentConfig();
        Map<String, String> params = new HashMap<String, String>();
        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            //乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
            params.put(entry.getKey(), WebUtil.transformCoding(entry.getValue(), "ISO-8859-1", "utf-8"));
        }
        //移除回调链接中的 paymentSn sign不是true，与安全校验码、请求时的参数格式（如：带自定义参数等）、编码格式有关
        params.remove("sn");
        return StringUtils.equals(params.get("sign"), DigestUtils.md5Hex(getParameterString(paraFilter(params)) + paymentConfig.getBargainorKey())) && verifyResponse(paymentConfig.getBargainorId(), params.get("notify_id"));
    }

    @Override
    public PayResult parsePayResult(Map<String, String> parameters) {
        Map<String, String> params = new HashMap<String, String>();
        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            //乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
            params.put(entry.getKey(), WebUtil.transformCoding(entry.getValue(), "ISO-8859-1", "utf-8"));
        }

        PayResult payResult = new PayResult();
        payResult.setPaymentSN(params.get("out_trade_no"));//支付编号
        payResult.setTradeNo(params.get("trade_no"));//交易流水号
        payResult.setTotalFee(BigDecimal.valueOf(Double.valueOf(params.get("total_fee"))));//交易金额
        payResult.setStatus(StringUtils.equals("WAIT_SELLER_SEND_GOODS", params.get("trade_status")) ? PayResult.PayStatus.success : PayResult.PayStatus.failure);
        return payResult;
    }

}