package com.katus.common.io;

import java.io.*;

/**
 * @author SUN Katus
 * @version 1.0, 2021-11-01
 */
public class LocalLineIterator implements LineIterator {
    private final BufferedReader reader;
    private String nextLine;
    private boolean nextJudge = false, hasNext = true;

    LocalLineIterator(Reader reader) {
        this.reader = new BufferedReader(reader);
    }

    @Override
    public boolean hasNext() {
        if (!nextJudge) {
            try {
                this.nextLine = reader.readLine();
                this.hasNext = nextLine != null;
            } catch (IOException e) {
                this.nextLine = null;
                this.hasNext = false;
            }
            this.nextJudge = true;
        }
        return hasNext;
    }

    @Override
    public String next() {
        if (!nextJudge) {
            this.hasNext = hasNext();
        }
        this.nextJudge = false;
        return nextLine;
    }

    @Override
    public void close() throws IOException {
        reader.close();
    }
}
