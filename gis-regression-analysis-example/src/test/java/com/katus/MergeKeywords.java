package com.katus;

import com.katus.common.io.FsManipulator;
import com.katus.common.io.FsManipulatorFactory;
import com.katus.exception.InvalidParamException;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * @author SUN Katus
 * @version 1.0, 2021-10-18
 */
public class MergeKeywords {

    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            throw new InvalidParamException();
        }
        String outputFilename = args[0];
        FsManipulator fsManipulator = FsManipulatorFactory.create();
        Set<String> keywordList = new HashSet<>();
        for (int i = 1; i < args.length; i++) {
            keywordList.addAll(fsManipulator.readToLines(args[i]));
        }
        fsManipulator.writeTextToFile(outputFilename, keywordList);
    }
}
