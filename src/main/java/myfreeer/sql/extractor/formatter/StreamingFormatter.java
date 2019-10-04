package myfreeer.sql.extractor.formatter;

import java.io.IOException;
import java.io.Writer;
import java.sql.ResultSet;
import java.sql.SQLException;

public interface StreamingFormatter<T> extends Formatter<T> {
  /**
   * format value to sql script to writer
   *
   * @param writer    for writing sql to
   * @param resultSet resultSet
   * @param col       col no
   * @throws SQLException ResultSet fails
   * @throws IOException  writer IO fails
   */
  void format(Writer writer, ResultSet resultSet, int col) throws SQLException, IOException;
}
