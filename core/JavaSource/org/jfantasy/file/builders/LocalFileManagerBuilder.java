package org.jfantasy.file.builders;

import org.jfantasy.file.FileManagerBuilder;
import org.jfantasy.file.bean.enums.FileManagerType;
import org.jfantasy.file.manager.LocalFileManager;

import java.util.Map;

public class LocalFileManagerBuilder implements FileManagerBuilder<LocalFileManager> {

    @Override
    public FileManagerType getType() {
        return FileManagerType.local;
    }

    @Override
    public LocalFileManager register(Map<String,String> params) {
        String defaultDir = params.get("defaultDir");
        if (defaultDir == null) {
            throw new RuntimeException(" LocalFileManager 未配置 defaultDir 项 ." + params);
        }
        return new LocalFileManager(defaultDir);
    }
}
