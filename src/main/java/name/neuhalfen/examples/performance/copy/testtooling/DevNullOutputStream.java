package name.neuhalfen.examples.performance.copy.testtooling;

import java.io.IOException;
import java.io.OutputStream;


public class DevNullOutputStream extends OutputStream {

    private long bytesWritten = 0;

    @Override
    public void write(int i) throws IOException {
        // ignore
        bytesWritten++;
    }

    @Override
    public void write(byte[] b) throws IOException {
        // ignore
        bytesWritten += b.length;
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        // ignore
        bytesWritten += len;
    }

    public long getBytesWritten() {
        return bytesWritten;
    }
}
