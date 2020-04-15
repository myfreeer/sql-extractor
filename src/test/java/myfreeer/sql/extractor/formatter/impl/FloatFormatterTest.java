package myfreeer.sql.extractor.formatter.impl;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FloatFormatterTest {
    private static final FloatFormatter FORMATTER = new FloatFormatter();

    private ResultSet resultSet(final Float data) throws SQLException {
        final ResultSet resultSet = Mockito.mock(ResultSet.class);
        Mockito.when(resultSet.next()).thenReturn(true).thenReturn(false);
        if (data == null) {
            Mockito.when(resultSet.getObject(Mockito.anyInt(), Mockito.eq(Float.class)))
                    .thenReturn(null).thenReturn(null);
        } else {
            Mockito.when(resultSet.getObject(Mockito.anyInt(), Mockito.eq(Float.class)))
                    .thenReturn(data).thenReturn(data);
        }
        return resultSet;
    }

    @Test
    public void type() {
        assertEquals(Types.FLOAT, FORMATTER.type());
    }

    @Test
    void format() throws SQLException {
        Random random = ThreadLocalRandom.current();
        for (int i = 0; i < random.nextInt(100) + 100; i++) {
            float num = random.nextFloat();
            assertEquals(Float.toString(num), FORMATTER.format(resultSet(num), i));
        }
    }


    @Test
    public void nullValue() throws SQLException {
        assertEquals("null", FORMATTER.format(resultSet(null), 1));
    }
}