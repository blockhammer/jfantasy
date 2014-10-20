<#assign s=JspTaglibs["/WEB-INF/tlds/struts-tags.tld"]/>
<script type="text/javascript">
$(function(){
    window.list = $('#bannerParameter').list($('#bannerParameterForm'));
    list.form.data('form').on('change',function(data,templateName,ele){
        if(templateName == 'edit'){
            var bannerUploader = $('#bannerUploader',this.target).upload({data:{'dir':'goodsImages'},theme:'image',size:'300x150'},!!data.bannerImage?[data.bannerImage]:[]);
            bannerUploader.on('uploadComplete',function(){
                var _data = bannerUploader.getData()[0];
                var data = ele.getData();
                data.bannerImage = _data;
                ele.setData(data);
            });
        }else{
            if(!!data.bannerImage) {
                $('.bitemimg').attr('src', data.bannerImage.absolutePath);
            }
        }
    });
    list.view.setJSON(<@s.property value="@com.fantasy.framework.util.jackson.JSON@serialize(banner.bannerItems)" escapeHtml="false" default="[]"/>);
    $("#saveForm").ajaxForm({
        beforeSerialize : function(zhis, options){
            var _data = list.getData();
            _data.each(function(){
                this.bannerImageStore = this.bannerImage.fileManagerId + ':' + this.bannerImage.absolutePath;
            });
            options.data = {'bannerItems':_data};
        },
        success :function(data){
            $('#pager').pager().reload();
            $.msgbox({
                msg : "保存成功",
                type : "success"
            });
            $(".back-page").backpage();
        }
	});
	
});
</script>

<div class="pad10L pad10R">
    <div class="example-box">
        <@s.form id="saveForm" namespace="/cms/banner" action="save" method="post" cssClass="center-margin">
            <@s.hidden name="id" value="%{banner.id}"/>
                <div class="row">
                    <div class="col-md-6">
                        <div class="form-row">
                            <div class="form-label col-md-3">
                                <label for="">
                                    <@s.text name="banner.key"/>：
                                </label>
                            </div>
                            <div class="form-input col-md-9">
                                <div class="append-left">
                                    <@s.textfield name="key" id="key" value="%{banner.key}"/>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-6">
                        <div class="form-row">
                            <div class="form-label col-md-3">
                                <label for="">
                                    <@s.text name="banner.name"/>：
                                </label>
                            </div>
                            <div class="form-input col-md-9">
                                <div class="append-left">
                                    <@s.textfield name="name" id="name" value="%{banner.name}"/>
                                </div>
                            </div>
                        </div>
                     </div>
                    <div class="col-md-6">
                        <div class="form-row">
                            <div class="form-label col-md-3">
                                <label for="">
                                    <@s.text name="banner.size"/>：
                                </label>
                            </div>
                            <div class="form-input col-md-9">
                                <div class="append-left">
                                    <@s.textfield name="size" id="size" value="%{banner.size}"/>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-6">
                        <div class="form-row">
                            <div class="form-label col-md-3">
                                <label for="">
                                </label>
                            </div>
                            <div class="form-input col-md-9">
                                <div class="append-left">
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-6">
                        <div class="form-row">
                            <div class="form-label col-md-3">
                                <label for="">
                                    <@s.text name="banner.description"/>：
                                </label>
                            </div>
                            <div class="form-input col-md-9">
                                <div class="append-left">
                                    <@s.textarea name="description" id="description" cssStyle="width:940px;height: 160px;" value="%{banner.description}"/>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            <#include "bannerParameter.ftl">
        <div class="form-row" style="text-align: center;">
            <div>
                <div style="float: left;padding-right: 50px;padding-left: 27px;">
                    <a href="javascript:void(0);" class="btn medium primary-bg radius-all-4 menu-save4"  onclick="$('#saveForm').submit();return false;" title="保存"  >
                            <span class="glyph-icon icon-separator">
                                 <i class="glyph-icon icon-save"></i>
                            </span>
                             <span class="button-content">
                                    保存
                             </span>
                    </a>
                </div>
                <div style="float: left;">
                    <a href="javascript:void(0);" class="btn medium primary-bg radius-all-4 switch menu-view back-page "  title="返回" >
                            <span class="glyph-icon icon-separator">
                                  <i class="glyph-icon icon-reply"></i>
                            </span>
                             <span class="button-content">
                                    返回
                             </span>
                    </a>
                </div>
            </div>
        </div>
        </@s.form>
    </div>
</div>