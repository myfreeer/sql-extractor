package myfreeer.sql.extractor;

import lombok.extern.slf4j.Slf4j;
import myfreeer.sql.extractor.properties.ExportProperties;
import myfreeer.sql.extractor.service.ExportService;
import myfreeer.sql.extractor.util.OracleClobToBlob;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Command line entry
 */
@SpringBootApplication
@Slf4j
public class SqlExtractorApplication {
  private static final int DEFAULT_BUFFER_SIZE = 8192;

  public static void main(String[] args) throws IOException {
    final ConfigurableApplicationContext application =
        SpringApplication.run(SqlExtractorApplication.class, args);
    final Boolean isTest = application.getEnvironment()
        .getProperty("export.test", Boolean.class, false);
    if (isTest) {
      return;
    }
    final ExportProperties exportProperties = application.getBean(ExportProperties.class);
    final ExportService exportService = application.getBean(ExportService.class);
    final String exportFilePath = exportProperties.getFilePath();
    if (exportFilePath == null) {
      throw new IllegalArgumentException("exportFilePath must not be null");
    }
    Path tempFile = null;
    try {
      tempFile = Files.createTempFile("sql_extract_temp_", ".sql");
      String clobToBlobFnName;
      try (final BufferedWriter writer = Files.newBufferedWriter(tempFile, UTF_8)) {
        if (exportProperties.getType() == ExportProperties.ExportType.TABLE) {
          clobToBlobFnName = exportService.exportTables(writer,
              exportProperties.lowerCaseTables(),
              exportProperties.lowerCaseExcludeTables());
        } else {
          clobToBlobFnName = exportService.exportAllTables(writer,
              exportProperties.lowerCaseExcludeTables());
        }
        writer.flush();
      }
      final Path path = Paths.get(exportFilePath);
      if (clobToBlobFnName == null) {
        Files.move(tempFile, path);
      } else {
        try (final OutputStream os = Files.newOutputStream(path);
             final InputStream is = Files.newInputStream(tempFile)) {
          os.write(OracleClobToBlob.getFunc(clobToBlobFnName).getBytes(UTF_8));
          // copy
          final byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
          int read;
          while ((read = is.read(buffer, 0, DEFAULT_BUFFER_SIZE)) >= 0) {
            os.write(buffer, 0, read);
          }
          os.write(OracleClobToBlob.getDropFunc(clobToBlobFnName).getBytes(UTF_8));
          os.flush();
        }
      }
    } catch (IOException | RuntimeException e) {
      log.error("Unknown Exception", e);
      throw e;
    } finally {
      if (tempFile != null) {
        Files.deleteIfExists(tempFile);
      }
      System.exit(SpringApplication.exit(application));
    }
  }

}
