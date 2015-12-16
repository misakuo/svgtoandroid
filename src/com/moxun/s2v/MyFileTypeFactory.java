package com.moxun.s2v;

import com.intellij.ide.highlighter.XmlFileType;
import com.intellij.openapi.fileTypes.FileTypeConsumer;
import com.intellij.openapi.fileTypes.FileTypeFactory;

/**
 * Created by moxun on 15/12/14.
 */
public class MyFileTypeFactory extends FileTypeFactory {
    @Override
    public void createFileTypes(FileTypeConsumer fileTypeConsumer) {
        fileTypeConsumer.consume(XmlFileType.INSTANCE,"svg");
    }
}
