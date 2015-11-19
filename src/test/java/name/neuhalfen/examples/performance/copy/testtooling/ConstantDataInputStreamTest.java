package name.neuhalfen.examples.performance.copy.testtooling;

import org.junit.Test;

import java.io.IOException;

import static name.neuhalfen.examples.performance.copy.testtooling.ConstantDataInputStream.VALUE;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


public class ConstantDataInputStreamTest {

    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(ConstantDataInputStreamTest.class);

    @Test
    public void returns_correctNumber_ofBytes() throws IOException {
        final long sampleSize = 31337;
        ConstantDataInputStream sut = new ConstantDataInputStream(sampleSize);

        long bytesRead = 0;
        while (sut.read() != -1) {
            bytesRead++;
        }

        assertThat("We read the correct amount of bytes", bytesRead, is(sampleSize));
    }

    @Test
    public void returns_correctValue_ofBytes() throws IOException {
        final long sampleSize = 3;
        ConstantDataInputStream sut = new ConstantDataInputStream(sampleSize);

        long bytesRead = 0;

        for (int b = sut.read(); b != -1; b = sut.read()) {
            assertThat("We read the correct value of bytes @" + bytesRead, b, is(VALUE));
            bytesRead++;
        }
    }
}