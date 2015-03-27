package com.fantasy.framework.struts2;

import com.fantasy.framework.struts2.core.FantasyActionInvocation;
import com.fantasy.framework.struts2.rest.RestActionInvocation;
import com.fantasy.framework.util.common.ObjectUtil;
import com.fantasy.framework.util.regexp.RegexpUtil;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ActionProxy;
import com.opensymphony.xwork2.DefaultActionProxyFactory;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.ValueStack;
import org.apache.struts2.dispatcher.mapper.ActionMapping;

import java.util.Map;

public class FantasyActionProxyFactory extends DefaultActionProxyFactory {

    protected String[] extensions;

    @Inject(value = "struts.action.extension", required = false)
    public void setExtension(String extension) {
        this.extensions = RegexpUtil.split(extension, ",");
        int index = ObjectUtil.indexOf(this.extensions, "");
        while (index != -1) {
            this.extensions = ObjectUtil.remove(this.extensions, "");
            index = ObjectUtil.indexOf(this.extensions, "");
        }
    }

    @Override
    public ActionProxy createActionProxy(String namespace, String actionName, String methodName, Map<String, Object> extraContext, boolean executeResult, boolean cleanupContext) {
        ActionMapping mapping = (ActionMapping) extraContext.get("struts.actionMapping");
        if (mapping == null) {
            ValueStack valueStack = (ValueStack) extraContext.get("com.opensymphony.xwork2.util.ValueStack.ValueStack");
            if (valueStack != null) {
                mapping = (ActionMapping) valueStack.findValue("struts.actionMapping");
            }
        }
        if (mapping == null) {
            throw new RuntimeException("如果你看到这个信息，请记录出错步骤后，联系开发人员");
        }
        if (ObjectUtil.indexOf(this.extensions, mapping.getExtension()) != -1) {
            ActionInvocation inv = new FantasyActionInvocation(extraContext, true);
            container.inject(inv);
            return createActionProxy(inv, namespace, actionName, methodName, executeResult, cleanupContext);
        } else {
            ActionInvocation inv = new RestActionInvocation(extraContext, true);
            container.inject(inv);
            return createActionProxy(inv, namespace, actionName, methodName, executeResult, cleanupContext);
        }
    }

}