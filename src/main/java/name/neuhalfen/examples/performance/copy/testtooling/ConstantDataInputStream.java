package name.neuhalfen.examples.performance.copy.testtooling;


import java.io.IOException;
import java.io.InputStream;

/**
 * Generate source data -- fast and really boring!
 */
public class ConstantDataInputStream extends InputStream {

    final static int VALUE = 'X';
    private long bytesLeft;

    //
    public ConstantDataInputStream(final long size) {
        this.bytesLeft = size;
    }

    @Override
    public int read() throws IOException {

        if (bytesLeft > 0) {
            bytesLeft--;

            return VALUE;
        } else {
            return -1;
        }
    }

    @Override
    public int read(byte[] b) throws IOException {
        if (bytesLeft > 0) {
            int bytesRead = (int) Math.min(bytesLeft, b.length);
            bytesLeft -= bytesRead;

            for (int i = 0; i < bytesRead; i++) {
                b[i] = VALUE;
            }
            return bytesRead;
        } else {
            return -1;
        }
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        if (off == 0 && len == b.length) {
            return read(b);
        } else {
            throw new UnsupportedOperationException(String.format("read(byte[%d],offset=%d,len=%d)", b.length, off, len));
        }

    }

    @Override
    public boolean markSupported() {
        return false;
    }

    @Override
    public int available() throws IOException {
        return (int) bytesLeft;
    }
}
