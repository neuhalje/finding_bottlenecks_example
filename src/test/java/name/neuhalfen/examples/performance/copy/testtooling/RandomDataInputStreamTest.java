package name.neuhalfen.examples.performance.copy.testtooling;

import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertThat;


public class RandomDataInputStreamTest {
    private final int KB = 1024;
    private final int MB = 1024 * KB;
    private final long GB = 1024 * MB;
    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(RandomDataInputStreamTest.class);

    @Test
    public void returns_correctNumber_ofBytes() throws IOException {
        final long sampleSize = 31337;
        RandomDataInputStream sut = new RandomDataInputStream(sampleSize);

        long bytesRead = 0;
        while (sut.read() != -1) {
            bytesRead++;
        }

        assertThat("We read the correct amount of bytes", bytesRead, is(sampleSize));
    }

    @Ignore("Only run on demand -- heavily depends on system load")
    @Test
    public void measureRandomDataInputStreamMaxPerformance_serial() throws IOException {
        final int sampleSizeMB = 10;
        RandomDataInputStream sut = new RandomDataInputStream(sampleSizeMB * MB);

        long start = System.currentTimeMillis();
        while (sut.read() != -1) {
        }
        long end = System.currentTimeMillis();

        double MBBperMilliSecond = ((double) sampleSizeMB) / (end - start);
        double MBperSecond = MBBperMilliSecond * 1000 ;
        String msg = String.format("RandomDataInputStream: delivers ~%f.2 MB/s", MBperSecond);
        LOGGER.debug(msg);

        assertThat("We need a fake serial source data: more than 30 MB/s", MBperSecond, greaterThan(30.0));
    }


    @Ignore("Only run on demand -- heavily depends on system load")
    @Test
    public void measureRandomDataInputStreamMaxPerformance_block() throws IOException {
        final int sampleSizeMB = 100;
        RandomDataInputStream sut = new RandomDataInputStream(sampleSizeMB * MB);

        final byte buffer[] = new byte[1024];

        long start = System.currentTimeMillis();
        while (sut.read(buffer) != -1) {
        }
        long end = System.currentTimeMillis();

        double MBBperMilliSecond = ((double) sampleSizeMB) / (end - start);
        double MBperSecond = MBBperMilliSecond * 1000;
        String msg = String.format("RandomDataInputStream: delivers ~%f.2 MB/s", MBperSecond);
        LOGGER.debug(msg);

        assertThat("We need ultra fast fake source data for blocks: more than 100 MB/s", MBperSecond, greaterThan(100.0));
    }
}