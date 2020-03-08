package myfreeer.sql.extractor.formatter.impl;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class NumberFormatterTest {

  private static final NumberFormatter FORMATTER = new NumberFormatter();

  private ResultSet resultSet(final Number data) throws SQLException {
    final ResultSet resultSet = Mockito.mock(ResultSet.class);
    Mockito.when(resultSet.next()).thenReturn(true).thenReturn(false);
    BigDecimal bigDecimal;
    if (data instanceof Long || data instanceof Integer) {
      bigDecimal = BigDecimal.valueOf(data.longValue());
    } else if (data instanceof Double) {
      bigDecimal = BigDecimal.valueOf(data.doubleValue());
    } else if (data instanceof BigInteger) {
      bigDecimal = new BigDecimal((BigInteger) data);
    } else if (data instanceof BigDecimal) {
      bigDecimal = (BigDecimal) data;
    } else {
      bigDecimal = null;
    }
    if (bigDecimal == null) {
      Mockito.when(resultSet.getBigDecimal(Mockito.anyInt()))
          .thenReturn(null).thenReturn(null);
    } else {
      Mockito.when(resultSet.getBigDecimal(Mockito.anyInt()))
          .thenReturn(bigDecimal).thenReturn(bigDecimal);
    }
    return resultSet;
  }

  @Test
  public void type() {
    assertEquals(FORMATTER.type(), Types.NUMERIC);
  }

  @Test
  public void nullValue() throws SQLException {
    assertEquals("null", FORMATTER.format(resultSet(null), 1));
  }

  @Test
  public void intValue() throws SQLException {
    final int i = ThreadLocalRandom.current().nextInt();
    assertEquals(Integer.toString(i), FORMATTER.format(resultSet(i), 1));
  }

  @Test
  public void longValue() throws SQLException {
    final long i = ThreadLocalRandom.current().nextLong();
    assertEquals(Long.toString(i), FORMATTER.format(resultSet(i), 1));
  }

  @Test
  public void doubleValue() throws SQLException {
    final double i = ThreadLocalRandom.current().nextDouble();
    assertEquals(Double.toString(i), FORMATTER.format(resultSet(i), 1));
  }

  @Test
  public void bigIntegerValue() throws SQLException {
    final BigInteger i = BigInteger.valueOf(ThreadLocalRandom.current().nextLong())
        .multiply(BigInteger.valueOf(ThreadLocalRandom.current().nextLong()));
    assertEquals(i.toString(), FORMATTER.format(resultSet(i), 1));
  }

  @Test
  public void bigDecimalValue() throws SQLException {
    final BigDecimal i = BigDecimal.valueOf(ThreadLocalRandom.current().nextDouble())
        .multiply(BigDecimal.valueOf(ThreadLocalRandom.current().nextDouble()));
    assertEquals(i.toString(), FORMATTER.format(resultSet(i), 1));
  }
}