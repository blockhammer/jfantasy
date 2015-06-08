<#assign s=JspTaglibs["/WEB-INF/tlds/struts-tags.tld"]/>
<script type="text/javascript">
    $(function () {
        $("#saveForm").ajaxForm({
            success: function (data) {
                $('#pager').pager().reload();
                top.$.msgbox({
                    msg: "保存成功",
                    type: "success"
                });
                $page$.backpage();
            }
        });

        $('#'+$('[name="type"]').val()).show();
        $('[name="type"]').change(function(){
            $('#ftp').hide().find('select').attr("disabled","disabled");
            $('#jdbc').hide().find('select').attr("disabled","disabled");
            $('#local').hide().find('textfield').attr("disabled","disabled");
            if($(this).val()=="ftp"){
                $('#ftp').show().find('select').removeAttr("disabled");
            };
            if($(this).val()=="jdbc"){
                $('#jdbc').show().find('select').removeAttr("disabled");
            };
            if($(this).val()=="local"){
                $('#local').show().find('textfield').removeAttr("disabled");
            };

        });

    });
</script>
<div class="example-box" style="padding-left:10px;padding-right:10px;">
    <div class="example-code">
        <div class="content-box box-toggle button-toggle">
            <h3 class="content-box-header primary-bg">
                <span class="float-left">文件管理器编辑</span>
                <a href="javascript:void(0)" class="float-right icon-separator btn toggle-button" title="Toggle Box">
                    <i class="glyph-icon icon-toggle icon-chevron-up"></i>
                </a>
            </h3>
            <div class="content-box-wrapper">
            <@s.form id="saveForm" namespace="/system/filemanager" action="save" method="post" cssClass="center-margin">
                <@s.hidden name="id" value="%{fm.id}"/>
                <div class="row">
                    <div class="col-md-6">
                        <div class="form-row">
                            <div class="form-label col-md-3">
                                <label for="">
                                    编码：
                                </label>
                            </div>
                            <div class="form-input col-md-9">
                                <div class="append-left">
                                    <@s.textfield name="id" value="%{fm.id}" disabled="true"/>
                                </div>
                            </div>
                        </div>
                        <div class="form-row">
                            <div class="form-label col-md-3">
                                <label for="">
                                    类型：
                                </label>
                            </div>
                            <div class="form-checkbox-radio col-md-9">
                                <div class="append-left">
                                    <@s.radio name="type" value="%{fm.type}" list="@com.fantasy.file.bean.enums.FileManagerType@values()"  listValue="value" />
                                </div>
                            </div>
                        </div>
                        <div class="form-row">
                            <div class="form-label col-md-3">
                                <label for="">
                                    备注：
                                </label>
                            </div>
                            <div class="form-input col-md-9">
                                <div class="append-left">
                                    <@s.textarea name="description" cssStyle="width: 880px; height: 118px;"/>
                                </div>

                            </div>
                        </div>
                        <div class="form-row">
                            <div style="float: left;padding-right: 50px;">
                                <a href="javascript:void(0);" class="btn medium primary-bg radius-all-4 menu-save4"  onclick="$('#saveForm').submit();return false;" title="保存"  >
                                         <span class="button-content">
                                                 <i class="glyph-icon icon-save float-left"></i>
                                             保存
                                          </span>
                                </a>
                            </div>
                            <div style="float: left;">
                                <a href="javascript:void(0);" class="btn medium primary-bg radius-all-4 switch menu-view back-page "  title="返回" >
                                         <span class="button-content">
                                              <i class="glyph-icon icon-reply"></i>
                                              返回
                                        </span>
                                </a>
                            </div>
                        </div>
                    </div>

                    <div class="col-md-6">
                        <div class="form-row">
                            <div class="form-label col-md-3">
                                <label for="">
                                    名称：
                                </label>
                            </div>
                            <div class="form-input col-md-9">
                                <div class="append-right">
                                    <@s.textfield name="name" value="%{fm.name}"/>
                                </div>
                            </div>
                        </div>
                        <div class="form-row" id="ftp" style="display:none;">
                            <div class="form-label col-md-3">
                                <label for="">
                                    ftp配置属性：
                                </label>
                            </div>
                            <div class="form-input col-md-9">
                                <div class="append-right">
                                    <@s.select cssClass="chosen-select" name="ftpConfig.id" list="@com.fantasy.common.service.FtpConfigService@findftps()" listKey="id" listValue="name" value="%{fm.ftpConfig.id}" />
                                </div>
                            </div>
                        </div>
                        <div class="form-row" id="local" style="display: none;">
                            <div class="form-label col-md-3">
                                <label for="">
                                    Local配置属性：
                                </label>
                            </div>
                            <div class="form-input col-md-9">
                                <div class="append-right">
                                    <@s.textfield name="localDefaultDir" value="%{fm.localDefaultDir}"/>
                                </div>
                            </div>
                        </div>
                        <div class="form-row" id="jdbc" style="display:none;">
                            <div class="form-label col-md-3">
                                <label for="">
                                    jdbc配置属性：
                                </label>
                            </div>
                            <div class="form-input col-md-9">
                                <div class="append-right">
                                    <@s.select cssClass="chosen-select" name="jdbcConfig.id" list="@com.fantasy.common.service.JdbcConfigService@jdbcConfigs()" listKey="id" listValue="driver" value="%{fm.jdbcConfig.id}"/>
                                </div>
                            </div>
                        </div>
                    </div>


                </div>
            </@s.form>

            </div>

        </div>
    </div>
</div>