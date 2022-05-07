package guru.qa;

import com.codeborne.pdftest.PDF;
import com.codeborne.pdftest.matchers.ContainsExactText;
import com.codeborne.selenide.Selenide;
import com.codeborne.xlstest.XLS;
import com.opencsv.CSVReader;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static com.codeborne.selenide.Selenide.$;
import static org.hamcrest.MatcherAssert.assertThat;

public class SelenideDownloadTest {

    ClassLoader cl = SelenideDownloadTest.class.getClassLoader();

    @Test
    void downloadTest() throws Exception {

        Selenide.open("https://github.com/junit-team/junit5/blob/main/README.md");
        File textFile = $("#raw-url").download();
        try (InputStream is = new FileInputStream(textFile)) {
            byte[] fileContent = is.readAllBytes();
            String strContent = new String(fileContent, StandardCharsets.UTF_8);
            org.assertj.core.api.Assertions.assertThat(strContent).contains("JUnit 5");
        }

    }

    @Test
    void pdfParsingText() throws Exception {
        InputStream stream = cl.getResourceAsStream("pdf/junit-user-guide-5.8.2.pdf");
        PDF pdf = new PDF(stream);
        Assertions.assertEquals(166, pdf.numberOfPages);
        assertThat(pdf, new ContainsExactText("123"));
    }

    @Test
    void xlsParsingText() throws Exception {
        InputStream stream = cl.getResourceAsStream("xls/sample-xlsx-file.xlsx");
        XLS xls = new XLS(stream);
        String stringCellValue = xls.excel.getSheetAt(0).getRow(3).getCell(1).getStringCellValue();
        org.assertj.core.api.Assertions.assertThat(stringCellValue).contains("Philip");
    }

    @Test
    void csvParsingText() throws Exception {
        try (
                InputStream stream = cl.getResourceAsStream("csv/teachers.csv");
                CSVReader reader = new CSVReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {

            List<String[]> content = reader.readAll();
            org.assertj.core.api.Assertions.assertThat(content).contains(
                    new String[]{"Name", "Surname"},
                    new String[]{"Dmitrii", "Tuchs"},
                    new String[]{"Artem", "Eroshenko"}
            );

        }
    }

    @Test
    void zipParsingTest() throws Exception {
        ZipInputStream is = new ZipInputStream(cl.getResourceAsStream("zip/zip-file.zip"));
        ZipEntry entry;
        while ((entry = is.getNextEntry()) != null) {
            org.assertj.core.api.Assertions.assertThat(entry.getName()).isEqualTo("sample.txt");
        }

    }
}


