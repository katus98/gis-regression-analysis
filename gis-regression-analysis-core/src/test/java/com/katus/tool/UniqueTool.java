package com.katus.tool;

import com.katus.common.io.FsManipulator;
import com.katus.common.io.FsManipulatorFactory;
import com.katus.common.util.Strings;
import com.katus.exception.InvalidParamException;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author SUN Katus
 * @version 1.0, 2021-10-05
 */
public class UniqueTool {

    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            throw new InvalidParamException();
        }
        String filename = args[0];
        FsManipulator manipulator = FsManipulatorFactory.create();
        manipulator.readToLines(filename)
                .stream()
                .flatMap(line -> {
                    List<String> items = Strings.splitToList(line, ",");
                    return Strings.splitToListWithoutEmpty(items.get(1).substring(1, items.get(1).length() - 1), ",").stream();
                }).collect(Collectors.toSet())
                .forEach(System.out::println);
    }
}
