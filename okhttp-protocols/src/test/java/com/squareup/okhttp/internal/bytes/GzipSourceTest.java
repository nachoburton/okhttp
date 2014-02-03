/*
 * Copyright (C) 2014 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.squareup.okhttp.internal.bytes;

import java.io.IOException;
import java.util.zip.GZIPOutputStream;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class GzipSourceTest {
  @Test public void gunzip() throws Exception {
    String original = "It's a UNIX system! I know this!";
    OkBuffer originalBuffer = buffer(original);
    OkBuffer gzipped = gzip(originalBuffer);
    OkBuffer gunzipped = gunzip(gzipped);
    assertEquals(original, gunzipped.readUtf8((int) gunzipped.byteCount()));
  }

  /** Use GZIPOutputStream to gzip a buffer. */
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
}
