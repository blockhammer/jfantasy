package org.jfantasy.wx.framework.intercept;

import org.jfantasy.wx.framework.exception.WeiXinException;
import org.jfantasy.wx.framework.message.WeiXinMessage;
import org.jfantasy.wx.framework.session.WeiXinSession;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 记录接收及发生的消息
 */
public class LogInterceptor implements WeiXinMessageInterceptor {

    private final static Log LOG = LogFactory.getLog(LogInterceptor.class);

    @Override
    public WeiXinMessage intercept(WeiXinSession session, WeiXinMessage message, Invocation invocation) throws WeiXinException {

        LOG.debug("接收到的消息内容:" + message.getContent());

        WeiXinMessage outMessage = invocation.invoke();

        LOG.debug("回复的消息内容:" + (outMessage == null ? "null" : outMessage.getContent()));

        return outMessage;
    }

}
