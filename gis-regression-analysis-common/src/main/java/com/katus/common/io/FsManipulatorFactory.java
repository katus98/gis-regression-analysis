package com.katus.common.io;

/**
 * @author SUN Katus
 * @version 1.0, 2021-05-24
 */
public final class FsManipulatorFactory {

    public static FsManipulator create(String uri) {
        String title = uri.substring(0, uri.indexOf(":"));
        FsManipulator fsManipulator;
        switch (title) {
            case "file":
                // todo
            case "hdfs":
                // todo
            default:
                fsManipulator = LocalFsManipulator.getInstance();
        }
        return fsManipulator;
    }

    public static FsManipulator create() {
        return FsManipulatorFactory.create("file:///");
    }
}
