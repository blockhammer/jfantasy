package org.jfantasy.pay.product;

import org.jfantasy.pay.bean.Payment;
import org.jfantasy.pay.bean.Refund;
import org.jfantasy.pay.error.PayException;
import org.jfantasy.pay.product.order.Order;
import org.jfantasy.pay.service.PaymentContext;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.web.util.HtmlUtils;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 基类 - 支付产品
 */
public abstract class PayProductSupport implements PayProduct {

    protected final static Log LOG = LogFactory.getLog(PayProductSupport.class);

    protected String id;//支付产品ID
    protected String name;// 支付产品名称
    protected String bargainorIdName;// 商户ID参数名称
    protected String description;// 支付产品描述
    protected String bargainorKeyName;// 密钥参数名称
    protected String shroffAccountName;//收款方账号名称
    protected CurrencyType[] currencyTypes;// 支持货币类型
    protected String logoPath;// 支付产品LOGO路径

    @Override
    public Refund refund(Refund refund){
        return null;
    }

    @Override
    public Object app(Payment payment, Order order) throws PayException{
        return null;
    }

    /**
     * 验证签名
     *
     * @param parameters 请求参数
     * @return 是否验证通过
     */
    public boolean verifySign(Map<String, String> parameters){
        return false;
    }

    /**
     * 根据参数集合组合参数字符串（忽略空值参数）
     *
     * @param params 请求参数
     * @return 参数字符串
     */
    protected String getParameterString(Map<String, String> params) {
        List<String> keys = new ArrayList<String>(params.keySet());
        Collections.sort(keys);
        AtomicReference<StringBuffer> stringBuffer = new AtomicReference<StringBuffer>(new StringBuffer());
        for (String key : keys) {
            String value = params.get(key);
            if (StringUtils.isNotEmpty(value)) {
                stringBuffer.get().append("&").append(key).append("=").append(value);
            }
        }
        stringBuffer.get().deleteCharAt(0);
        return stringBuffer.get().toString();
    }

    protected static Map<String, String> paraFilter(Map<String, String> sArray) {
        Map<String, String> result = new HashMap<String, String>();
        if (sArray == null || sArray.isEmpty()) {
            return result;
        }
        for (String key : sArray.keySet()) {
            String value = sArray.get(key);
            if (value == null || "".equals(value) || "sign".equalsIgnoreCase(key) || "sign_type".equalsIgnoreCase(key)) {
                continue;
            }
            result.put(key, value);
        }
        return result;
    }

    public String buildRequest(Map<String, String> sParaTemp) {
        return this.buildRequest(sParaTemp, "post", "确定");
    }

    /**
     * 建立请求，以表单HTML形式构造（默认）
     *
     * @param sParaTemp     请求参数数组
     * @param strMethod     提交方式。两个值可选：post、get
     * @param strButtonName 确认按钮显示文字
     * @return 提交表单HTML文本
     */
    public String buildRequest(Map<String, String> sParaTemp, String strMethod, String strButtonName) {
        //待请求参数数组
        List<String> keys = new ArrayList<String>(sParaTemp.keySet());

        StringBuilder sbHtml = new StringBuilder();

        //sbHtml.append("<form id=\"alipaysubmit\" name=\"alipaysubmit\" action=\"").append(getPaymentUrl()).append("\" method=\"").append(strMethod).append("\">\n");

        for (String key : keys) {
            String value = sParaTemp.get(key);
            sbHtml.append(key).append(":<input type=\"hidden\" name=\"").append(key).append("\" value=\"").append(HtmlUtils.htmlEscape(value)).append("\"/><br/>\n");
        }

        //submit按钮控件请不要含有name属性
        sbHtml.append("<input type=\"submit\" value=\"").append(strButtonName).append("\" style=\"display:none;\">\n</form>");

        sbHtml.append("\n<script>document.forms['alipaysubmit'].submit();</script>");

        if (LOG.isDebugEnabled()) {
            LOG.debug(sbHtml);
        }

        return sbHtml.toString();
    }

    public String getPayreturnMessage(String paymentSn) {
        return "<html><head><meta http-equiv=\"content-type\" content=\"text/html; charset=utf-8\" /><title>页面跳转中..</title></head><body onload=\"javascript: document.forms[0].submit();\"><form action=\"" + PaymentContext.getContext().getResultUrl(paymentSn) + "\"></form></body></html>";
    }

    public String getPaynotifyMessage(String paymentSn) {
        return null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonIgnore
    public String getDescription() {
        return description;
    }

    @Override
    public String getShroffAccountName() {
        return shroffAccountName;
    }

    public void setShroffAccountName(String shroffAccountName) {
        this.shroffAccountName = shroffAccountName;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBargainorIdName() {
        return bargainorIdName;
    }

    public void setBargainorIdName(String bargainorIdName) {
        this.bargainorIdName = bargainorIdName;
    }

    public String getBargainorKeyName() {
        return bargainorKeyName;
    }

    public void setBargainorKeyName(String bargainorKeyName) {
        this.bargainorKeyName = bargainorKeyName;
    }

    public CurrencyType[] getCurrencyTypes() {
        return currencyTypes;
    }

    public void setCurrencyTypes(CurrencyType[] currencyTypes) {
        this.currencyTypes = currencyTypes;
    }

    public String getLogoPath() {
        return logoPath;
    }

    public void setLogoPath(String logoPath) {
        this.logoPath = logoPath;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}