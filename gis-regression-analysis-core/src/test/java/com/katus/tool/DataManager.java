package com.katus.tool;

import com.katus.common.io.FsManipulator;
import com.katus.common.io.FsManipulatorFactory;
import com.katus.exception.InvalidParamException;

/**
 * @author SUN Katus
 * @version 1.0, 2021-10-05
 */
public class DataManager {

    public static void main(String[] args) {
        if (args.length < 1) {
            throw new InvalidParamException();
        }
        String targetFilename = args[0];
        FsManipulator manipulator = FsManipulatorFactory.create();

    }
}
