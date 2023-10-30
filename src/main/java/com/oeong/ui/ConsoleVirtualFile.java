package com.oeong.ui;

import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.openapi.util.NlsSafe;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileSystem;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author mzyupc@163.com
 */
public class ConsoleVirtualFile extends VirtualFile {
    private final String name;
    private final Project project;

    @Override
    public @NotNull FileType getFileType() {
        return new FileType() {
            @Override
            public @NonNls @NotNull String getName() {
                return null;
            }

            @Override
            public @NlsContexts.Label @NotNull String getDescription() {
                return null;
            }

            @Override
            public @NlsSafe @NotNull String getDefaultExtension() {
                return null;
            }

            @Override
            public Icon getIcon() {
                return null;
            }

            @Override
            public boolean isBinary() {
                return false;
            }
        };
    }

    public ConsoleVirtualFile(String name, Project project) {
        this.project = project;
        this.name = name;
    }

    @Override
    public @NotNull
    String getName() {
        return name;
    }

    @Override
    public @NotNull VirtualFileSystem getFileSystem() {
        return project.getService(VirtualFileSystem.class);
    }

    @Override
    public @NonNls
    @NotNull String getPath() {
        return name;
    }

    @Override
    public boolean isWritable() {
        return true;
    }

    @Override
    public boolean isDirectory() {
        return false;
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public VirtualFile getParent() {
        return null;
    }

    @Override
    public VirtualFile[] getChildren() {
        return new VirtualFile[0];
    }

    @Override
    public @NotNull OutputStream getOutputStream(Object o, long l, long l1) throws IOException {
        throw new UnsupportedOperationException("Unsupported Operation");
    }

    @Override
    public byte[] contentsToByteArray() throws IOException {
        return new byte[0];
    }

    @Override
    public long getTimeStamp() {
        return 0;
    }

    @Override
    public long getLength() {
        return 0;
    }

    @Override
    public void refresh(boolean b, boolean b1, @Nullable Runnable runnable) {

    }

    @Override
    public @NotNull InputStream getInputStream() throws IOException {
        return null;
    }

    @Override
    public long getModificationStamp() {
        return 0;
    }
}
