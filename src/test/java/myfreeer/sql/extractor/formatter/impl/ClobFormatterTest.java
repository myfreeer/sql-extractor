package myfreeer.sql.extractor.formatter.impl;

import myfreeer.sql.extractor.util.StringBuilderWriter;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.io.StringReader;
import java.sql.Clob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Base64;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ClobFormatterTest {

  private final ClobFormatter formatter = new ClobFormatter();

  protected ClobFormatter formatter() {
    return formatter;
  }

  protected ResultSet resultSet(final String data) throws SQLException {
    final ResultSet resultSet = Mockito.mock(ResultSet.class);
    Mockito.when(resultSet.next()).thenReturn(true).thenReturn(false);
    if (data == null) {
      Mockito.when(resultSet.getString(Mockito.anyInt()))
          .thenReturn(null).thenReturn(null);
      Mockito.when(resultSet.getClob(Mockito.anyInt()))
          .thenReturn(null).thenReturn(null);
    } else {
      final Clob clob = Mockito.mock(Clob.class);
      Mockito.when(clob.getCharacterStream())
          .thenReturn(new StringReader(data))
          .thenReturn(new StringReader(data));
      Mockito.when(resultSet.getString(Mockito.anyInt()))
          .thenReturn(data).thenReturn(data);
      Mockito.when(resultSet.getClob(Mockito.anyInt()))
          .thenReturn(clob).thenReturn(clob);
    }
    return resultSet;
  }

  private String randomStr(final int size) {
    final Random random = ThreadLocalRandom.current();
    final byte[] bytes = new byte[random.nextInt(size) + size];
    random.nextBytes(bytes);
    return Base64.getEncoder().encodeToString(bytes);
  }

  private String randomFixedStr(final int size) {
    return randomStr(size).substring(0, size);
  }

  @Test
  public void type() {
    assertEquals(Types.CLOB, formatter().type());
  }

  @Test
  public void toClobFn() {
    assertEquals("TO_CLOB", formatter().toClobFn());
  }

  @Test
  public void stringPrefix() {
    assertEquals("(q'[", formatter().stringPrefix());
  }

  private void assertStreamResultEqualsStringResult(String string) throws SQLException, IOException {
    final ResultSet resultSet = resultSet(string);
    final StringBuilderWriter writer = new StringBuilderWriter();
    formatter().format(writer, resultSet, 1);
    assertEquals(writer.toString(), formatter().format(resultSet, 1));
  }

  @Test
  public void streamResultEqualsStringResult() throws IOException, SQLException {
    assertStreamResultEqualsStringResult(null);
    assertStreamResultEqualsStringResult(randomStr(256));
    assertStreamResultEqualsStringResult(randomStr(512));
    assertStreamResultEqualsStringResult(randomStr(2000));
    assertStreamResultEqualsStringResult(randomStr(4000));
    assertStreamResultEqualsStringResult(randomStr(4096));
    assertStreamResultEqualsStringResult(randomStr(8192));
    assertStreamResultEqualsStringResult(randomStr(16384));
  }

  @Test
  public void escapingStreamResultEqualsStringResult() throws IOException, SQLException {
    assertStreamResultEqualsStringResult(randomStr(128) + ']' + randomStr(256));
    assertStreamResultEqualsStringResult(randomStr(256) + ']');
    assertStreamResultEqualsStringResult(randomStr(512) + ']');
    assertStreamResultEqualsStringResult(randomFixedStr(1999) + ']' + randomStr(2000));
    assertStreamResultEqualsStringResult(randomFixedStr(2000) + ']' + randomStr(2000));
    assertStreamResultEqualsStringResult(randomFixedStr(2001) + ']' + randomStr(2000));
    assertStreamResultEqualsStringResult(randomFixedStr(3999) + ']');
    assertStreamResultEqualsStringResult(randomFixedStr(4000) + ']');
    assertStreamResultEqualsStringResult(randomStr(4096) + ']' + randomStr(8192));
    assertStreamResultEqualsStringResult(']' + randomStr(16384));
  }

  @Test
  public void shortStringClob() throws SQLException {
    final String s = randomStr(256);
    assertEquals(formatter().toClobFn() + formatter().stringPrefix() + s + "]')",
        formatter().format(resultSet(s), 1));
  }

  @Test
  public void longStringClob() throws SQLException {
    final String s = randomFixedStr(4001);
    assertEquals(
            formatter().toClobFn() +
                    formatter().stringPrefix() + s.substring(0, 999) + "]')\n" +
                    " || " + formatter().toClobFn() +
                    formatter().stringPrefix() + s.substring(999, 1999) + "]')\n" +
                    " || " + formatter().toClobFn() +
                    formatter().stringPrefix() + s.substring(1999, 2999) + "]')\n" +
                    " || " + formatter().toClobFn() +
                    formatter().stringPrefix() + s.substring(2999, 3999) + "]')\n" +
                    " || " + formatter().toClobFn() +
                    formatter().stringPrefix() + s.substring(3999) + "]')",
            formatter().format(resultSet(s), 1));
  }

  @Test
  public void longStringClob2() throws SQLException {
    final String s = randomFixedStr(4000);
    assertEquals(
            formatter().toClobFn() +
                    formatter().stringPrefix() + s.substring(0, 999) + "]')\n" +
                    " || " + formatter().toClobFn() +
                    formatter().stringPrefix() + s.substring(999, 1999) + "]')\n" +
                    " || " + formatter().toClobFn() +
                    formatter().stringPrefix() + s.substring(1999, 2999) + "]')\n" +
                    " || " + formatter().toClobFn() +
                    formatter().stringPrefix() + s.substring(2999, 3999) + "]')\n" +
                    " || " + formatter().toClobFn() +
                    formatter().stringPrefix() + s.substring(3999) + "]')",
            formatter().format(resultSet(s), 1));
  }

  @Test
  public void longStringClobWithEscape() throws SQLException {
    final String s = randomFixedStr(4000);
    assertEquals(
            formatter().toClobFn() +
                    formatter().stringPrefix() + s.substring(0, 999) + "]')\n" +
                    " || " + formatter().toClobFn() +
                    formatter().stringPrefix() + s.substring(999, 1999) + "]')\n" +
                    " || " + formatter().toClobFn() +
                    formatter().stringPrefix() + s.substring(1999, 2999) + "]')\n" +
                    " || " + formatter().toClobFn() +
                    formatter().stringPrefix() + s.substring(2999, 3999) + "]')\n" +
                    " || " + formatter().toClobFn() +
                    formatter().stringPrefix() + s.substring(3999) + "]')\n" +
                    " || " + formatter().toClobFn() + "(']') || " + formatter().toClobFn() +
                    formatter().stringPrefix() + "]')",
            formatter().format(resultSet(s + ']'), 1));
  }
}
