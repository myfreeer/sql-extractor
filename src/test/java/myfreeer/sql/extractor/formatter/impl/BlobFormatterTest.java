package myfreeer.sql.extractor.formatter.impl;

import myfreeer.sql.extractor.util.StringBuilderWriter;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import static myfreeer.sql.extractor.formatter.impl.BlobFormatter.HEX_ARRAY;
import static org.junit.Assert.assertEquals;

public class BlobFormatterTest {
  private static final byte[] TEST_DATA1 = new byte[256];
  private static final byte[] TEST_DATA2 = new byte[1234];
  private static final Random RANDOM = ThreadLocalRandom.current();

  static {
    RANDOM.nextBytes(TEST_DATA1);
    RANDOM.nextBytes(TEST_DATA2);
  }

  private ResultSet resultSet(final byte[] data) throws SQLException {
    final ResultSet resultSet = Mockito.mock(ResultSet.class);
    final Blob blob = Mockito.mock(Blob.class);
    Mockito.when(resultSet.next()).thenReturn(true).thenReturn(false);
    if (data == null) {
      Mockito.when(resultSet.getBlob(Mockito.anyInt()))
          .thenReturn(null).thenReturn(null);
    } else {
      Mockito.when(resultSet.getBlob(Mockito.anyInt()))
          .thenReturn(blob).thenReturn(blob);
      Mockito.when(blob.getBinaryStream())
          .thenReturn(new ByteArrayInputStream(data))
          .thenReturn(new ByteArrayInputStream(data));
    }
    return resultSet;
  }

  static String toHex(byte[] bytes, int begin, int end) {
    if (end < 0) {
      end = bytes.length;
    }
    final char[] hexChars = new char[(end - begin) << 1];
    for (int j = begin, offset, v; j < end; j++) {
      v = bytes[j] & 0xFF;
      offset = (j - begin) << 1;
      hexChars[offset] = HEX_ARRAY[v >>> 4];
      hexChars[offset + 1] = HEX_ARRAY[v & 0x0F];
    }
    return new String(hexChars);
  }

  @Test
  public void type() {
    final String s = Long.toHexString(RANDOM.nextLong());
    final BlobFormatter blobFormatter = new BlobFormatter(s);
    assertEquals(blobFormatter.type(), Types.BLOB);
  }

  @Test
  public void fnName() {
    final String s = Long.toHexString(RANDOM.nextLong());
    final BlobFormatter blobFormatter = new BlobFormatter(s);
    assertEquals(blobFormatter.getFnName(), s);
  }

  private void assertStreamResultEqualsStringResult(ResultSet resultSet) throws SQLException, IOException {
    final StringBuilderWriter writer = new StringBuilderWriter();
    final String s = Long.toHexString(RANDOM.nextLong());
    final BlobFormatter blobFormatter = new BlobFormatter(s);
    blobFormatter.format(writer, resultSet, 1);
    assertEquals(writer.toString(), blobFormatter.format(resultSet, 1));
  }

  @Test
  public void shortStringStreamResultEqualsStringResult() throws SQLException, IOException {
    final ResultSet resultSet = resultSet(TEST_DATA1);
    assertStreamResultEqualsStringResult(resultSet);
  }


  @Test
  public void longStringStreamResultEqualsStringResult() throws SQLException, IOException {
    final ResultSet resultSet = resultSet(TEST_DATA2);
    assertStreamResultEqualsStringResult(resultSet);
  }

  @Test
  public void nullStreamResultEqualsStringResult() throws SQLException, IOException {
    final ResultSet resultSet = resultSet(null);
    assertStreamResultEqualsStringResult(resultSet);
  }

  @Test
  public void shortString() throws SQLException {
    final ResultSet resultSet = resultSet(TEST_DATA1);
    final String s = Long.toHexString(RANDOM.nextLong());
    final BlobFormatter blobFormatter = new BlobFormatter(s);
    assertEquals(s + "(TO_CLOB('" + toHex(TEST_DATA1, 0, -1) + "'))",
        blobFormatter.format(resultSet, 1));
  }

  @Test
  public void longString() throws SQLException {
    final ResultSet resultSet = resultSet(TEST_DATA2);
    final String s = Long.toHexString(RANDOM.nextLong());
    final BlobFormatter blobFormatter = new BlobFormatter(s);
    assertEquals(s + "(TO_CLOB('" +
        toHex(TEST_DATA2, 0, 512) +
        "') || TO_CLOB('" +
        toHex(TEST_DATA2, 512, 1024) +
        "') || TO_CLOB('" +
        toHex(TEST_DATA2, 1024, -1) +
        "'))", blobFormatter.format(resultSet, 1));
  }

  @Test
  public void nullValue() throws SQLException {
    final ResultSet resultSet = resultSet(null);
    final String s = Long.toHexString(RANDOM.nextLong());
    final BlobFormatter blobFormatter = new BlobFormatter(s);
    assertEquals("null", blobFormatter.format(resultSet, 1));
  }
}