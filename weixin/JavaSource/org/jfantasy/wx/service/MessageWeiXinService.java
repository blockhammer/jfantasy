package org.jfantasy.wx.service;

import org.jfantasy.file.bean.FileDetail;
import org.jfantasy.file.service.FileUploadService;
import org.jfantasy.framework.dao.Pager;
import org.jfantasy.framework.dao.hibernate.PropertyFilter;
import org.jfantasy.framework.util.jackson.JSON;
import org.jfantasy.wx.bean.Message;
import org.jfantasy.wx.bean.User;
import org.jfantasy.wx.dao.MessageDao;
import org.jfantasy.wx.framework.exception.WeiXinException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class MessageWeiXinService {
    @Autowired
    private MessageDao messageDao;
    @Autowired
    private UserService userService;
    @Autowired
    private FileUploadService fileUploadService;

    /**
     * 列表查询
     *
     * @param pager   分页
     * @param filters 查询条件
     * @return 分页对象
     */
    public Pager<Message> findPager(Pager<Message> pager, List<PropertyFilter> filters) {
        Pager<Message> p = this.messageDao.findPager(pager, filters);
        for (PropertyFilter pf : filters) {
            if (pf.getFilterName().equals("EQS_userInfo.openid")) {
                for (Message m : p.getPageItems()) {
                    userService.refreshMessage(m.getUser());
                }
                break;
            }
        }
        return p;
    }

    /**
     * 删除消息对象
     *
     * @param ids id数组
     */
    public void delete(Long... ids) {
        for (Long id : ids) {
            messageDao.delete(id);
        }
    }

    /**
     * 获取消息对象
     *
     * @param id
     * @return
     */
    public Message getMessage(Long id) {
        return messageDao.get(id);
    }

    /**
     * 保存消息对象
     *
     * @param message
     * @return
     */
    public Message save(Message message) {
        int result = 0;
        User ui = null;//userService.get(message.getUser().getOpenId());
        long createTime = new Date().getTime();
        message.setCreateTime(createTime);
        if (ui != null) {
            ui.setLastMessageTime(createTime);
            if (message.getType() != null && message.getType().equals("send")) {
                ui.setLastLookTime(createTime);
            }
        }
        this.messageDao.save(message);
        return message;
    }

    /**
     * 根据消息类型保存消息对象 更具msgType保存mediaId的消息的媒体文件
     * @param message
     * @return
     * @throws org.jfantasy.wx.framework.exception.WeiXinException
     */
    public Message saveByType(Message message) throws WeiXinException {
        String dir=message.getMsgType()+"Wx";
        String media=null;
        if(message.getMsgType().equals("video")){
            List<FileDetail> fileDetails=new ArrayList<FileDetail>();
            fileDetails.add(getMedia(message.getMediaId(), dir));
            fileDetails.add(getMedia(message.getThumbMediaId(),dir));
            media=JSON.serialize(fileDetails);
        }else if(dir.equals("imageWx")||dir.equals("voiceWx")){
            media=JSON.serialize(getMedia(message.getMediaId(),dir));
        }else if(message.getMediaId()!=null){
            media=JSON.serialize(getMedia(message.getMediaId(),"mediaWx"));
        }
        message.setMediaData(media);
        this.messageDao.save(message);
        return message;
    }

    public FileDetail getMedia(String mediaId,String dir) throws WeiXinException {
        FileDetail fileDetail=null;
//        File file=null;
//
//        try {
//            file=weixinConfigInit.getUtil().mediaDownload(mediaId);
//            String rename=Long.toString(new Date().getTime())+Integer.toString(new Random().nextInt(900000)+100000)+"."+ WebUtil.getExtension(file.getName());
//            fileDetail=fileUploadService.upload(file, FileUtil.getMimeType(file),rename,dir);
//        } catch (WxErrorException e) {
//            throw WeiXinException.wxExceptionBuilder(e);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }finally {
//            if(file!=null) file.delete();
//        }
        return fileDetail;
    }

    public Message createMessage(String msgType, String touser) {
        Message message = new Message();
        message.setMsgType(msgType);
        message.setType("send");
        message.setToUserName(touser);
        return message;
    }

    /**
     * 发送文本消息
     *
     * @param touser  发送人openId
     * @param content 发送内容
     * @return 微信返回码0为成功其他为错误码，可参考微信公众平台开发文档
     */
    public int sendTextMessage(String touser, String content) {
//        Message message = createMessage(WxConsts.CUSTOM_MSG_TEXT, touser);
//        message.setContent(content);
//        WxMpService service = weixinConfigInit.getUtil();
//        WxMpCustomMessage customMessage = WxMpCustomMessage.TEXT().toUser(touser).content(content).build();
//        try {
//            service.customMessageSend(customMessage);
//        } catch (WxErrorException e) {
//            if (e.getError().getErrorCode() == 45015) {
//                throw new RuntimeException("该用户48小时之内未与您发送信息");
//            }
//            return e.getError().getErrorCode();
//        }
//        save(message);
        return 0;
    }

    public int sendModelMessage(){
        return 0;
    }

}
