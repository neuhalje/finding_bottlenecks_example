package name.neuhalfen.examples.performance.copy;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Copy {


    public long copyInBlocks(InputStream src, OutputStream dst) throws IOException {
        final byte[] buffer = new byte[8192];

        long bytesCopied = 0;
        for (int bytesRead = src.read(buffer); bytesRead != -1; bytesRead = src.read(buffer)) {
            dst.write(buffer, 0, bytesRead);
            bytesCopied += bytesRead;
        }

        return bytesCopied;
    }


    public long copyWriteBytes(InputStream src, OutputStream dst) throws IOException {
        final byte[] buffer = new byte[8192];

        long bytesCopied = 0;
        for (int bytesRead = src.read(buffer); bytesRead != -1; bytesRead = src.read(buffer)) {

            for (int i = 0; i < bytesRead; i++) {
                dst.write(buffer[i]);
                bytesCopied++;
            }
        }
        return bytesCopied;
    }
}
