package myfreeer.sql.extractor.formatter.impl;

import myfreeer.sql.extractor.util.StringBuilderWriter;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Base64;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.Assert.assertEquals;

public class StringFormatterTest {

  private static final String RANDOM_STR;
  private static final StringFormatter FORMATTER = new StringFormatter();

  static {
    final Random random = ThreadLocalRandom.current();
    final byte[] bytes = new byte[random.nextInt(256) + 256];
    random.nextBytes(bytes);
    RANDOM_STR = Base64.getEncoder().encodeToString(bytes);
  }

  protected static String getRandomStr() {
    return RANDOM_STR;
  }

  protected ResultSet resultSet(final String data) throws SQLException {
    final ResultSet resultSet = Mockito.mock(ResultSet.class);
    Mockito.when(resultSet.next()).thenReturn(true).thenReturn(false);
    if (data == null) {
      Mockito.when(resultSet.getString(Mockito.anyInt()))
          .thenReturn(null).thenReturn(null);
    } else {
      Mockito.when(resultSet.getString(Mockito.anyInt()))
          .thenReturn(data).thenReturn(data);
    }
    return resultSet;
  }

  @Test
  public void streamResultEqualsStringResult() throws IOException, SQLException {
    assertStreamResultEqualsStringResult(null);
    assertStreamResultEqualsStringResult("1");
    assertStreamResultEqualsStringResult("111");
    assertStreamResultEqualsStringResult(RANDOM_STR);
    assertStreamResultEqualsStringResult(RANDOM_STR + ']' + RANDOM_STR);
    assertStreamResultEqualsStringResult(RANDOM_STR + ']');
  }

  private void assertStreamResultEqualsStringResult(String string) throws SQLException, IOException {
    final ResultSet resultSet = resultSet(string);
    final StringBuilderWriter writer = new StringBuilderWriter();
    getFormatter().format(writer, resultSet, 1);
    assertEquals(writer.toString(), getFormatter().format(resultSet, 1));
  }

  @Test
  public void type() {
    assertEquals(Types.VARCHAR, getFormatter().type());
  }

  @Test
  public void nullValue() throws SQLException {
    assertEquals("null", getFormatter().format(resultSet(null), 1));
  }

  @Test
  public void simpleString() throws SQLException {
    assertEquals("(q'[" + RANDOM_STR + "]')",
        getFormatter().format(resultSet(RANDOM_STR), 1));
  }

  @Test
  public void escapingString() throws SQLException {
    assertEquals("(q'[" + RANDOM_STR + "]' || ']' || q'[" + RANDOM_STR + "]')",
            getFormatter().format(resultSet(RANDOM_STR + ']' + RANDOM_STR), 1));
  }

  @Test
  public void escapingStringAtEnd() throws SQLException {
    assertEquals("(q'[" + RANDOM_STR + "]' || ']')",
            getFormatter().format(resultSet(RANDOM_STR + ']'), 1));
  }

  @Test
  public void prefix() {
    assertEquals("(q'[", getFormatter().prefix());
  }

  protected StringFormatter getFormatter() {
    return FORMATTER;
  }

  @Test
  public void escapingDelimiter() {
    assertEquals("]' || ']' || q'[", getFormatter().escapingDelimiter());
  }

  @Test
  public void suffix() {
    assertEquals("]')", getFormatter().suffix());
  }

  @Test
  public void escapingSuffix() {
    assertEquals("]' || ']')", getFormatter().escapingSuffix());
  }
}