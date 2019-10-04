package myfreeer.sql.extractor.formatter.impl;

import org.junit.Test;
import org.mockito.Mockito;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Base64;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.Assert.assertEquals;

public class StringFormatterTest {

    private static final Random RANDOM = ThreadLocalRandom.current();
    private static final String RANDOM_STR;

    static {
        final byte[] bytes = new byte[RANDOM.nextInt(256) + 256];
        RANDOM.nextBytes(bytes);
        RANDOM_STR = Base64.getEncoder().encodeToString(bytes);
    }

    private static final StringFormatter FORMATTER = new StringFormatter();

    private ResultSet resultSet(final String data) throws SQLException {
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
    public void type() {
        assertEquals(FORMATTER.type(), Types.VARCHAR);
    }

    @Test
    public void nullValue() throws SQLException {
        assertEquals("null", FORMATTER.format(resultSet(null), 1));
    }

    @Test
    public void simpleString() throws SQLException {
        assertEquals("(q'[" + RANDOM_STR + "]')",
                FORMATTER.format(resultSet(RANDOM_STR), 1));
    }

    @Test
    public void escapingString() throws SQLException {
        assertEquals("(q'[" + RANDOM_STR + "]' || ']' || q'[" + RANDOM_STR + "]')",
                FORMATTER.format(resultSet(RANDOM_STR + ']' + RANDOM_STR), 1));
    }

    @Test
    public void escapingStringAtEnd() throws SQLException {
        assertEquals("(q'[" + RANDOM_STR + "]' || ']')",
                FORMATTER.format(resultSet(RANDOM_STR + ']'), 1));
    }
}