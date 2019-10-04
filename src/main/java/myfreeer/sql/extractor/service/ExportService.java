package myfreeer.sql.extractor.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import myfreeer.sql.extractor.formatter.impl.BlobFormatter;
import myfreeer.sql.extractor.util.OracleClobToBlob;
import myfreeer.sql.extractor.util.StringBuilderWriter;
import org.springframework.core.NestedRuntimeException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.Writer;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExportService {
  private final FormatService formatService;
  private final JdbcTemplate jdbcTemplate;
  private final Random random = new Random();

  /**
   * export all tables to string
   *
   * @return sql script in string
   * @throws IOException should never happen
   */
  public String exportAllTables() throws IOException {
    return exportAllTables(Collections.emptySet());
  }

  /**
   * export all tables to string
   *
   * @param excludeTables exclude tables, should be lower case
   * @return sql script in string
   * @throws IOException should never happen
   */
  public String exportAllTables(final Set<String> excludeTables) throws IOException {
    final StringBuilderWriter writer = new StringBuilderWriter();
    final String clobToBlobFnName = exportAllTables(writer, excludeTables);
    if (clobToBlobFnName != null) {
      return OracleClobToBlob.getFunc(clobToBlobFnName) +
          writer.toString() +
          OracleClobToBlob.getDropFunc(clobToBlobFnName);
    }
    return writer.toString();
  }

  /**
   * export tables to string
   *
   * @param tables        table names
   * @param excludeTables exclude tables, should be same case with tables
   * @return sql script in string
   * @throws IOException should never happen
   */
  public String exportTables(@NonNull final List<String> tables,
                             @NonNull final Set<String> excludeTables) throws IOException {
    final StringBuilderWriter writer = new StringBuilderWriter();
    final String clobToBlobFnName = exportTables(writer, tables, excludeTables);
    if (clobToBlobFnName != null) {
      return OracleClobToBlob.getFunc(clobToBlobFnName) +
          writer.toString() +
          OracleClobToBlob.getDropFunc(clobToBlobFnName);
    }
    return writer.toString();
  }

  /**
   * export one table to writer
   *
   * @param writer    writer to write sql script
   * @param tableName table name
   * @return clob to blob helper function name, null if not needed
   * @throws IOException writer IO fail
   */
  public String exportTable(
      @NonNull final Writer writer, @NonNull final String tableName,
      @Nullable final String clobToBlobFnName) throws IOException {
    try {
      return jdbcTemplate.query("select * from " + tableName, rs -> {
        try {
          String realClobToBlobFnName = clobToBlobFnName;
          final ResultSetMetaData metaData = rs.getMetaData();
          final int columnCount = metaData.getColumnCount();
          final String[] columnName = new String[columnCount + 1];
          final int[] columnType = new int[columnCount + 1];
          boolean hasBlob = false;
          BlobFormatter blobFormatter = null;
          for (int i = 0, j; i < columnCount; i++) {
            j = i + 1;
            columnName[j] = metaData.getColumnName(j);
            columnType[j] = metaData.getColumnType(j);
          }
          final String col = String.join(", ",
              Arrays.asList(columnName).subList(1, columnName.length));
          int row = 0;
          while (rs.next()) {
            ++row;
            writer.write("insert into ");
            writer.write(tableName);
            writer.write(" ( ");
            writer.write(col);
            writer.write(" )");
            writer.write(" values ");
            writer.write("(");

            for (int i = 0, j; i < columnCount; i++) {
              j = i + 1;
              if (!hasBlob && columnType[j] == Types.BLOB) {
                hasBlob = true;
                if (StringUtils.isEmpty(realClobToBlobFnName)) {
                  realClobToBlobFnName = "CLOB_TO_BLOB_" + Long.toHexString(random.nextLong());
                }
                blobFormatter = new BlobFormatter(realClobToBlobFnName);
              }
              try {
                if (columnType[j] == Types.BLOB) {
                  if (blobFormatter == null) {
                    log.error("blobFormatter == null");
                    writer.write("null");
                  } else {
                    blobFormatter.format(writer, rs, j);
                  }
                } else {
                  formatService.format(writer, rs, j, columnType[j]);
                }
              } catch (SQLException | RuntimeException e) {
                log.error("Failed to export data at table {}, row {}, col {} {}, type {}",
                    tableName, row, j, columnName[j], columnType[j], e);
                throw e;
              }
              if (j < columnCount) {
                writer.write(',');
              }
            }
            writer.write(");\n");
          }
          return realClobToBlobFnName;
        } catch (IOException e) {
          throw new IoExceptionWrapper(e);
        }
      });
    } catch (IoExceptionWrapper e) {
      final Throwable cause = e.getCause();
      if (cause instanceof IOException) {
        throw (IOException) cause;
      }
      throw e;
    }
  }

  /**
   * export all tables to writer
   *
   * @param writer writer to write sql script
   * @return clob to blob helper function name, null if not needed
   * @throws IOException writer IO fail
   */
  public String exportAllTables(final Writer writer) throws IOException {
    return exportAllTables(writer, Collections.emptySet());
  }

  /**
   * export all tables to writer
   *
   * @param writer        writer to write sql script
   * @param excludeTables exclude tables, should be lower case
   * @return clob to blob helper function name, null if not needed
   * @throws IOException writer IO fail
   */
  @Nullable
  public String exportAllTables(
      @NonNull final Writer writer,
      @NonNull final Set<String> excludeTables) throws IOException {
    final Instant beginInstant = Instant.now();
    log.info("Full data export begins at {}", beginInstant);
    log.info("Querying tables...");
    final List<String> tables = jdbcTemplate.queryForList(
        "select lower(TABLE_NAME) from USER_TABLES order by TABLE_NAME", String.class);
    log.info("Query table complete, {} tables found.", tables.size());
    final String s = exportTables(writer, tables, excludeTables);

    log.info("Full data export completed at {}, took {} ms",
        Instant.now(), System.currentTimeMillis() - beginInstant.toEpochMilli());
    return s;
  }

  /**
   * export tables to writer
   *
   * @param writer        writer to write sql script
   * @param tables        table names
   * @param excludeTables exclude tables, should be same case with tables
   * @return clob to blob helper function name, null if not needed
   * @throws IOException writer IO fail
   */
  public String exportTables(
      final Writer writer,
      final List<String> tables,
      final Set<String> excludeTables) throws IOException {
    final int size = tables.size();
    String clobToBlobFnName = null;
    for (int i = 0; i < size; i++) {
      final String table = tables.get(i);
      if (excludeTables.contains(table)) {
        log.info("[{}/{}] Skip exporting table {} ...", i + 1, size, table);
        continue;
      }
      log.info("[{}/{}] Exporting table {} ...", i + 1, size, table);
      writer.write("-- ------------------------------------------------------------\n");
      writer.write("--            ");
      writer.write(table);
      writer.write("\n-- ------------------------------------------------------------\n");
      try {
        final String nextClobToBlobFnName = exportTable(writer, table, clobToBlobFnName);
        if (clobToBlobFnName == null && nextClobToBlobFnName != null) {
          clobToBlobFnName = nextClobToBlobFnName;
        }
        log.info("[{}/{}] Export table {} complete.", i + 1, size, table);
      } catch (RuntimeException e) {
        writer.write("--  Export ");
        writer.write(table);
        writer.write(" failed.");
        log.error("[{}/{}] Export table {} fail.", i + 1, size, table, e);
      }
    }
    return clobToBlobFnName;
  }

  public static class IoExceptionWrapper extends NestedRuntimeException {

    private static final long serialVersionUID = 8768051255727525629L;

    public IoExceptionWrapper(final String msg) {
      super(msg);
    }

    public IoExceptionWrapper(final String msg, final Throwable cause) {
      super(msg, cause);
    }

    public IoExceptionWrapper(final Throwable cause) {
      super(cause.getMessage(), cause);
    }
  }


}
