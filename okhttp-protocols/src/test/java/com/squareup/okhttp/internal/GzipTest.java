package com.squareup.okhttp.internal;

import com.squareup.okhttp.internal.bytes.Deadline;
import com.squareup.okhttp.internal.bytes.GzipSource;
import com.squareup.okhttp.internal.bytes.OkBuffer;
import com.squareup.okhttp.internal.bytes.OkBuffers;
import com.squareup.okhttp.internal.bytes.Sink;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;
import junit.framework.TestCase;
import org.junit.Test;

public class GzipTest extends TestCase {
  @Test public void test() throws Exception {
    String original = "It's a UNIX system! I know this!";
    OkBuffer originalBuffer = buffer(original);
    OkBuffer gzipped = gzip(originalBuffer);
    OkBuffer gunzipped = gunzip(gzipped);
    assertEquals(original, gunzipped.readUtf8((int) gunzipped.byteCount()));
  }

  /** Use DeflaterOutputStream to deflate source. */
  private OkBuffer gzip(OkBuffer buffer) throws IOException {
    OkBuffer result = new OkBuffer();
    Sink sink = OkBuffers.sink(new GZIPOutputStream(OkBuffers.outputStream(result)));
    sink.write(buffer, buffer.byteCount(), Deadline.NONE);
    sink.close(Deadline.NONE);
    return result;
  }

  private OkBuffer gunzip(OkBuffer gzipped) throws IOException {
    OkBuffer result = new OkBuffer();
    GzipSource source = new GzipSource(gzipped);
    while (source.read(result, Integer.MAX_VALUE, Deadline.NONE) != -1) {
    }
    return result;
  }

  public OkBuffer buffer(String s) {
    OkBuffer result = new OkBuffer();
    result.writeUtf8(s);
    return result;
  }

  /** Returns a gzipped copy of {@code bytes}. */
  public byte[] gzip(byte[] bytes) throws IOException {
    ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
    OutputStream gzippedOut = new GZIPOutputStream(bytesOut);
    gzippedOut.write(bytes);
    gzippedOut.close();
    return bytesOut.toByteArray();
  }
}
