package com.microservices.grpc.util;

import java.io.File;
import java.io.FilenameFilter;

public class FileFilter implements FilenameFilter {
    String prefix;

    public FileFilter(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public boolean accept(File dir, String name) {
        return name.startsWith(prefix);
    }
}
