package guru.qa;

import com.codeborne.pdftest.PDF;
import com.codeborne.pdftest.matchers.ContainsExactText;
import com.codeborne.xlstest.XLS;
import com.opencsv.CSVReader;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import static org.hamcrest.MatcherAssert.assertThat;

public class ZipTest {

    @Test
    void zipParsingTest() throws Exception {
        ZipFile zipFile = new ZipFile(new File("src/test/resources/zip/zip-test.zip"));
        ZipInputStream is = new ZipInputStream(ZipTest.class.getClassLoader().getResourceAsStream("zip/zip-test.zip"));
        ZipEntry entry;
        while ((entry = is.getNextEntry()) != null) {
            try (InputStream stream = zipFile.getInputStream(entry)) {
                if (entry.getName().equals("junit-user-guide-5.pdf")) {
                    PDF pdf = new PDF(stream);
                    Assertions.assertEquals(166, pdf.numberOfPages);
                    assertThat(pdf, new ContainsExactText("JUnit 5 User Guide"));
                }
                if (entry.getName().equals("XLSXfile.xlsx")) {
                    XLS xls = new XLS(stream);
                    String stringCellValue = xls.excel.getSheetAt(0).getRow(2).getCell(2).getStringCellValue();
                    org.assertj.core.api.Assertions.assertThat(stringCellValue).contains("Samsung");
                }
                if (entry.getName().equals("CSVfile.csv")) {
                    try (CSVReader reader = new CSVReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
                        List<String[]> content = reader.readAll();
                        org.assertj.core.api.Assertions.assertThat(content).contains(
                                new String[]{"iPhone", "13"},
                                new String[]{"Xiaomi", "Redmi Note 4"},
                                new String[]{"Samsung", "Galaxy S20"}
                        );
                    }
                }
            }
        }
    }
}
