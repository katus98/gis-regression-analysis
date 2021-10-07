package com.katus;

import com.katus.exception.InvalidParamException;
import lombok.Data;

import java.io.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author SUN Katus
 * @version 1.0, 2021-10-07
 */
@Data
public class PrepareDistance {
    private final ReentrantLock LOCK = new ReentrantLock();
    private BufferedWriter writer;

    public PrepareDistance(String outputFile) throws IOException {
        this.writer = new BufferedWriter(new FileWriter(outputFile));
    }

    public class DistanceProcessTask implements Callable<String> {
        private final String filename;
        private final BufferedReader reader;

        public DistanceProcessTask(String inputFile) throws FileNotFoundException {
            this.filename = inputFile;
            this.reader = new BufferedReader(new FileReader(inputFile));
        }

        @Override
        public String call() throws Exception {
            System.out.println("Thread: " + filename + " started!");
            // skip first line
            reader.readLine();
            String line;
            long count = 0;
            while ((line = reader.readLine()) != null) {
                String[] items = line.split(",");
                String[] pIds = items[1].split(" - ");
                int start = Integer.parseInt(pIds[0]);
                int end = Integer.parseInt(pIds[1]);
                double length;
                try {
                    length = Double.parseDouble(items[5]);
                } catch (Exception e) {
                    length = Double.MAX_VALUE;
                }
                int id = (end << 16) + start;
                String result = String.format("%d,%d,%d,%f\n", id, start, end, length);
                // todo: prepare a buffer area to avoid writing frequently
                LOCK.lock();
                writer.write(result);
                LOCK.unlock();
                if (++count % 5000000 == 0) {
                    System.out.println("Thread: " + filename + " has written " + count +" lines!");
                }
            }
            LOCK.lock();
            writer.flush();
            LOCK.unlock();
            reader.close();
            System.out.println("Thread: " + filename + " finished!");
            return filename;
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        if (args.length < 2) {
            throw new InvalidParamException();
        }
        PrepareDistance prepareDistance = new PrepareDistance(args[0] + args[1]);
        ExecutorService executorService = Executors.newFixedThreadPool(Math.min(Runtime.getRuntime().availableProcessors() / 2, args.length - 2));
        for (int i = 2; i < args.length; i++) {
            DistanceProcessTask task = prepareDistance.new DistanceProcessTask(args[0] + args[i]);
            executorService.submit(task);
        }
        executorService.shutdown();
        while (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
            System.out.println("Main: All threads are running!");
        }
        System.out.println("Main: All threads finished!");
    }
}
