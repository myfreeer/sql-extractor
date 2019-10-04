package myfreeer.sql.extractor.formatter.impl;

import org.junit.Test;
import org.mockito.Mockito;

import java.io.StringReader;
import java.sql.NClob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import static org.junit.Assert.assertEquals;

public class NClobFormatterTest extends ClobFormatterTest {
    private final NClobFormatter formatter = new NClobFormatter();

    @Override
    protected ClobFormatter formatter() {
        return formatter;
    }

    @Override
    protected ResultSet resultSet(final String data) throws SQLException {
        final ResultSet resultSet = Mockito.mock(ResultSet.class);
        Mockito.when(resultSet.next()).thenReturn(true).thenReturn(false);
        if (data == null) {
            Mockito.when(resultSet.getString(Mockito.anyInt()))
                    .thenReturn(null).thenReturn(null);
            Mockito.when(resultSet.getClob(Mockito.anyInt()))
                    .thenReturn(null).thenReturn(null);
        } else {
            final NClob clob = Mockito.mock(NClob.class);
            Mockito.when(clob.getCharacterStream())
                    .thenReturn(new StringReader(data))
                    .thenReturn(new StringReader(data));
            Mockito.when(resultSet.getString(Mockito.anyInt()))
                    .thenReturn(data).thenReturn(data);
            Mockito.when(resultSet.getNClob(Mockito.anyInt()))
                    .thenReturn(clob).thenReturn(clob);
        }
        return resultSet;
    }

    @Test
    public void type() {
        assertEquals(formatter().type(), Types.NCLOB);
    }

    @Test
    public void toClobFn() {
        assertEquals(formatter().toClobFn(), "TO_NCLOB");
    }

}
