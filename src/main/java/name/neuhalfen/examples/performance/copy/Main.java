package name.neuhalfen.examples.performance.copy;


import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class Main {


    public static void main(String[] args) {
        if (args.length != 3) {
            System.err.format("Usage %s [buffered|unbuffered|in_blocks] sourceFile destFile\n", "java -jar xxx.jar");
            System.exit(-1);
        } else {
            final String bufferMode = args[0];

            final File sourceFile = new File(args[1]);
            final File destFile = new File(args[2]);
            try {


                final long startTime = System.currentTimeMillis();

                final Copy command = new Copy();

                final FileInputStream inputStream = new FileInputStream(sourceFile);
                if (bufferMode.equalsIgnoreCase("unbuffered")) {
                    System.out.format("-- Write bytes; using NO write buffer\n");

                    FileOutputStream fileOutputStream = new FileOutputStream(destFile);
                    command.copyWriteBytes(inputStream, fileOutputStream);
                    fileOutputStream.close();

                } else if (bufferMode.equalsIgnoreCase("buffered")) {
                    final int BUFFSIZE = 8 * 1024;
                    System.out.format("-- Write bytes; Using a write buffer of %d bytes\n", BUFFSIZE);

                    FileOutputStream fileOutputStream = new FileOutputStream(destFile);
                    BufferedOutputStream bufferedStream = new BufferedOutputStream(fileOutputStream, BUFFSIZE);
                    command.copyWriteBytes(inputStream, bufferedStream);
                    bufferedStream.close();
                    fileOutputStream.close();

                } else if (bufferMode.equalsIgnoreCase("in_blocks")) {
                    System.out.format("-- Write blocks; Using NO write buffer\n");
                    FileOutputStream fileOutputStream = new FileOutputStream(destFile);
                    command.copyInBlocks(inputStream, fileOutputStream);
                    fileOutputStream.close();
                } else {
                    throw new IllegalArgumentException("expect  buffered|unbuffered|in_blocks");
                }
                inputStream.close();


                long endTime = System.currentTimeMillis();

                System.out.format("Copy took %.2f s\n", ((double) endTime - startTime) / 1000);
            } catch (Exception e) {
                System.err.format("ERROR: %s", e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
