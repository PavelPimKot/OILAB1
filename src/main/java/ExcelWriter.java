import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;

public class ExcelWriter {
    public static final String PHASE = "phase.xlsx";
    public static final String AMPLITUDE = "amplitude.xlsx";
    public static final String FFT_PHASE = "fft-phase.xlsx";
    public static final String FFT_AMPLITUDE = "fft-amplitude.xlsx";

    public static void write(String filePath, List<Double> xList, List<Double> yList, List<List<Double>> zList) {
        String resourceFilePath = Thread.currentThread().getContextClassLoader().getResource(filePath).getPath();
        try (FileInputStream file = new FileInputStream(resourceFilePath);
             Workbook workbook = new XSSFWorkbook(file)) {
            Sheet sheet = workbook.getSheetAt(0);
            Row xRow = sheet.createRow(1);
            int columnNum = 2;
            for (Double x : xList) {
                Cell cell = xRow.createCell(columnNum++);
                cell.setCellValue(x);
            }

            int rowNum = 2;
            for (Double y : yList) {
                Row row = sheet.createRow(rowNum++);
                Cell cell = row.createCell(0);
                cell.setCellValue(y);
            }

            rowNum = 2;
            columnNum = 2;
            for (List<Double> row : zList) {
                Row sheetRow = sheet.getRow(rowNum++);
                for (Double z : row) {
                    Cell cell = sheetRow.createCell(columnNum++);
                    cell.setCellValue(z);
                }
                columnNum = 2;
            }

            FileOutputStream outputStream = new FileOutputStream(resourceFilePath);
            workbook.write(outputStream);
            workbook.close();
            outputStream.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
