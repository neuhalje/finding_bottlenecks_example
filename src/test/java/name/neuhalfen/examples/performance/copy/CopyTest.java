package name.neuhalfen.examples.performance.copy;

import name.neuhalfen.examples.performance.copy.testtooling.ConstantDataInputStream;
import name.neuhalfen.examples.performance.copy.testtooling.DevNullOutputStream;
import name.neuhalfen.examples.performance.copy.testtooling.HashingOutputStream;
import org.hamcrest.text.IsEqualIgnoringCase;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.equalToIgnoringCase;
import static org.junit.Assert.assertThat;


public class CopyTest {

    private final static String THIRTEEN_X_SHA256 = "be41af6967efb3d4558b71b541d2351c9427fcea1f7a339914ce69089f996150";

    @Test
    public void copyInBlocks_copies_correctAmountOfData() throws IOException {
        InputStream input = new ConstantDataInputStream(13);
        DevNullOutputStream dst = new DevNullOutputStream();
        Copy sut = new Copy();

        long bytesCopied = sut.copyInBlocks(input, dst);
        dst.close();

        assertThat("Reported the correct number of bytes copied", bytesCopied, is(13l));
        assertThat("Copied the correct number of bytes", dst.getBytesWritten(), is(13l));
    }


    @Test
    public void copyInBlocks_maintains_data_integrity() throws IOException, NoSuchAlgorithmException {
        InputStream input = new ConstantDataInputStream(13);
        HashingOutputStream dst = HashingOutputStream.sha256();
        Copy sut = new Copy();

        sut.copyInBlocks(input, dst);

        dst.close();
        assertThat("Correctly copied all bytes", dst.toString(), equalToIgnoringCase(THIRTEEN_X_SHA256));
    }


    @Test
    public void copyWriteBytes_copies_correctAmountOfData() throws IOException {
        InputStream input = new ConstantDataInputStream(13);
        DevNullOutputStream dst = new DevNullOutputStream();
        Copy sut = new Copy();

        long bytesCopied = sut.copyWriteBytes(input, dst);
        dst.close();

        assertThat("Reported the correct number of bytes copied", bytesCopied, is(13l));
        assertThat("Copied the correct number of bytes", dst.getBytesWritten(), is(13l));
    }


    @Test
    public void copyWriteBytes_maintains_data_integrity() throws IOException, NoSuchAlgorithmException {
        InputStream input = new ConstantDataInputStream(13);
        HashingOutputStream dst = HashingOutputStream.sha256();
        Copy sut = new Copy();

        sut.copyWriteBytes(input, dst);

        dst.close();
        assertThat("Correctly copied all bytes", dst.toString(), equalToIgnoringCase(THIRTEEN_X_SHA256));
    }
}