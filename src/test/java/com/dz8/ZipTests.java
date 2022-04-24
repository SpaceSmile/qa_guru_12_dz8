package com.dz8;

import com.codeborne.pdftest.PDF;
import com.codeborne.pdftest.assertj.Assertions;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.zip.ZipFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import com.codeborne.xlstest.XLS;
import com.opencsv.CSVReader;
import java.io.InputStreamReader;
import java.util.List;

public class ZipTests {
    public static String resZName = "src/test/resources/zip/sampleZ.zip";
    public static String zName = "zip/sampleZ.zip";
    static ClassLoader cl = ZipTests.class.getClassLoader();
    public static String pName = "sampleP.pdf";
    public static String xName = "sampleX.xlsx";
    public static String tName = "sampleT.txt";
    public static String cName = "sampleC.csv";

    @Test
    void parseZipPDFTest() throws Exception {
        ZipFile zf = new ZipFile(new File(resZName));
        ZipInputStream is = new ZipInputStream(cl.getResourceAsStream(zName));
        ZipEntry entry;
        while ((entry = is.getNextEntry()) != null) {
            try (InputStream inputStream = zf.getInputStream(entry)) {
                if (entry.getName().equals(pName)) {
                    PDF pdf = new PDF(inputStream);
                   Assertions.assertThat(entry.getName()).isEqualTo(pName);
                   Assertions.assertThat(pdf.text).contains("www.gasexp.ru");
                }
                if (entry.getName().equals(xName)) {
                    XLS xls = new XLS(inputStream);
                    String stringCellValue = xls.excel.getSheetAt(0).getRow(1).getCell(0).getStringCellValue();
                    Assertions.assertThat(entry.getName()).isEqualTo(xName);
                    Assertions.assertThat(stringCellValue).contains("First Name");
                }
                if (entry.getName().equals(tName)) {
                    byte[] fileContent = is.readAllBytes();
                    String strContent = new String(fileContent, StandardCharsets.UTF_8);
                    Assertions.assertThat(strContent).contains("Utilitatis causa amicitia est quaesita");
                }
                if (entry.getName().equals(cName)) {
                    CSVReader reader = new CSVReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                    List<String[]> content = reader.readAll();
                    Assertions.assertThat(entry.getName()).isEqualTo(cName);
                    Assertions.assertThat(content).containsAnyOf(
                            new String[] {"name", "sku", "subtitle", ""}
                    );
                }
            }
        }
    }
}
