package export;

import crawl.website.Vinabiz;
import module.Company;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Scanner;

public class ExcelHelper {
    public static final int COLUMN_INDEX_ID = 0;
    public static final int COLUMN_INDEX_NAME = 1;
    public static final int COLUMN_INDEX_REPRESENTATIVE = 2;
    public static final int COLUMN_INDEX_PHONE = 3;
    public static final int COLUMN_INDEX_ADDRESS = 4;
    public static final int COLUMN_INDEX_BEHAVIOR = 5;
    public static final int COLUMN_INDEX_LINK = 6;
    public static final int COLUMN_INDEX_DATE = 7;
    public static final int COLUMN_INDEX_EMAIL = 8;
    public static final int COLUMN_INDEX_TAXCODE = 9;

    private static String mSheetName = "default";
    private static Workbook wb2007;
    private static Sheet sheet;
    private static String path = "-ResultSet.xlsx";

    private static CellStyle cellStyleFormatNumber = null;

    public ExcelHelper() {

        wb2007 = new XSSFWorkbook();
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
        path = timeStamp + path;
        //create sheet
        sheet = wb2007.createSheet(mSheetName);
    }

    public static void export(ArrayList<Company> companies) throws IOException {
        int rowIndex = 0;
        writeHeader(sheet, rowIndex);
        rowIndex++;

        for (Company company : companies) {
            // Create row
            Row row = sheet.createRow(rowIndex);
            // Write data on row
            writeBook(company, row, rowIndex);
            rowIndex++;
        }

        // Auto resize column witdth
        int numberOfColumn = sheet.getRow(0).getPhysicalNumberOfCells();
        autoSizeColumn(sheet, numberOfColumn);

        // Create file excel
        createOutputFile(wb2007, path);
        System.out.println("Done!!!");
    }

    // Write header with format
    private static void writeHeader(Sheet sheet, int rowIndex) {
        // create CellStyle
        CellStyle cellStyle = createStyleForHeader(sheet);

        // Create row
        Row row = sheet.createRow(rowIndex);

        // Create cells
        Cell cell = row.createCell(COLUMN_INDEX_ID);
        cell.setCellStyle(cellStyle);
        cell.setCellValue("Id");

        cell = row.createCell(COLUMN_INDEX_NAME);
        cell.setCellStyle(cellStyle);
        cell.setCellValue("Name");

        cell = row.createCell(COLUMN_INDEX_REPRESENTATIVE);
        cell.setCellStyle(cellStyle);
        cell.setCellValue("Representative");

        cell = row.createCell(COLUMN_INDEX_PHONE);
        cell.setCellStyle(cellStyle);
        cell.setCellValue("Phone number");

        cell = row.createCell(COLUMN_INDEX_ADDRESS);
        cell.setCellStyle(cellStyle);
        cell.setCellValue("Address");

        cell = row.createCell(COLUMN_INDEX_BEHAVIOR);
        cell.setCellStyle(cellStyle);
        cell.setCellValue("Behavior");

        cell = row.createCell(COLUMN_INDEX_LINK);
        cell.setCellStyle(cellStyle);
        cell.setCellValue("Link");

        cell = row.createCell(COLUMN_INDEX_DATE);
        cell.setCellStyle(cellStyle);
        cell.setCellValue("Date");

        cell = row.createCell(COLUMN_INDEX_EMAIL);
        cell.setCellStyle(cellStyle);
        cell.setCellValue("Email");

        cell = row.createCell(COLUMN_INDEX_TAXCODE);
        cell.setCellStyle(cellStyle);
        cell.setCellValue("Tax");
    }

    // Create CellStyle for header
    private static CellStyle createStyleForHeader(Sheet sheet) {
        // Create font
        Font font = sheet.getWorkbook().createFont();
        font.setFontName("Times New Roman");
        font.setBold(true);
        font.setFontHeightInPoints((short) 14); // font size
        font.setColor(IndexedColors.WHITE.getIndex()); // text color

        // Create CellStyle
        CellStyle cellStyle = sheet.getWorkbook().createCellStyle();
        cellStyle.setFont(font);
        cellStyle.setFillForegroundColor(IndexedColors.BLUE.getIndex());
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        cellStyle.setBorderBottom(BorderStyle.THIN);
        return cellStyle;
    }

    // Write data
    private static void writeBook(Company company, Row row, int id) {
        if (cellStyleFormatNumber == null) {
            // Format number
            short format = (short) BuiltinFormats.getBuiltinFormat("#,##0");
            // DataFormat df = workbook.createDataFormat();
            // short format = df.getFormat("#,##0");

            //Create CellStyle
            Workbook workbook = row.getSheet().getWorkbook();
            cellStyleFormatNumber = workbook.createCellStyle();
            cellStyleFormatNumber.setDataFormat(format);
        }

        Cell cell = row.createCell(COLUMN_INDEX_ID);
        cell.setCellValue(id);

        cell = row.createCell(COLUMN_INDEX_NAME);
        cell.setCellValue(company.name);

        cell = row.createCell(COLUMN_INDEX_REPRESENTATIVE);
        cell.setCellValue(company.representative);

        cell = row.createCell(COLUMN_INDEX_PHONE);
        cell.setCellValue(company.phoneNumber);

        cell = row.createCell(COLUMN_INDEX_ADDRESS);
        cell.setCellValue(company.address);

        cell = row.createCell(COLUMN_INDEX_BEHAVIOR);
        cell.setCellValue(company.behavior);

        cell = row.createCell(COLUMN_INDEX_LINK);
        cell.setCellValue(company.link);

        cell = row.createCell(COLUMN_INDEX_DATE);
        cell.setCellValue(company.date);

        cell = row.createCell(COLUMN_INDEX_EMAIL);
        cell.setCellValue(company.email);

        cell = row.createCell(COLUMN_INDEX_TAXCODE);
        cell.setCellValue(company.taxCode);
    }

    // Auto resize column width
    private static void autoSizeColumn(Sheet sheet, int lastColumn) {
        for (int columnIndex = 0; columnIndex < lastColumn; columnIndex++) {
            sheet.autoSizeColumn(columnIndex);
        }
    }

    // Create output file
    private static void createOutputFile(Workbook workbook, String excelFilePath) throws IOException {
        try (OutputStream os = new FileOutputStream(excelFilePath)) {
            workbook.write(os);
        }
    }

    public static void readExcelFile(String filePath, Statement statement, String fileName, String districtId) throws Exception {
        Scanner sc = new Scanner(System.in);

        FileInputStream fis = new FileInputStream(new File(fileName));
        XSSFWorkbook wb = new XSSFWorkbook(fis);
        XSSFSheet sheet = wb.getSheetAt(1);
        FormulaEvaluator formulaEvaluator = wb.getCreationHelper().createFormulaEvaluator();
        int index = 1;
        for (Row row : sheet)
        {
            if(index++ == 1) continue;
            int ite = 0;
            String[] value = new String[11];
            for (Cell cell : row)    //iteration over cell using for each loop
            {
                switch (formulaEvaluator.evaluateInCell(cell).getCellType()) {
                    case Cell.CELL_TYPE_NUMERIC:
                        value[ite++] = (int) cell.getNumericCellValue() + "";
                        break;
                    case Cell.CELL_TYPE_STRING:
                        value[ite++] = cell.getStringCellValue();
                        break;
                }
            }
            String query = "";
            value[COLUMN_INDEX_NAME] = value[COLUMN_INDEX_NAME].replace('\'', '`');
            try{
                String date[] = value[7].split("/");
                value[7] = date[2] + "-" + date[1] + "-" + date[0];
            } catch (Exception e){
                value[7] = "null";
            }
            try {
            query = "select * from companies where link like '" + value[COLUMN_INDEX_LINK] + "'";
            ResultSet resultSet = statement.executeQuery(query);
            if(resultSet.next()){
                continue;
            }
            if(value[7].equals("null")){
                query = "insert into companies value(null, '" + value[COLUMN_INDEX_NAME] + "',"
                        + "'" + value[COLUMN_INDEX_REPRESENTATIVE] + "',"
                        + "'" + value[3] + "',"
                        + "'" + value[4] + "',"
                        + "'" + value[5] + "',"
                        + "'" + value[6] + "',"
                        +       value[7] + ","
                        + "'" + value[8] + "',"
                        + "'" + value[9] + "',"
                        + districtId + ")";
            }
            else {
                query = "insert into companies value(null, '" + value[COLUMN_INDEX_NAME] + "',"
                        + "'" + value[COLUMN_INDEX_REPRESENTATIVE] + "',"
                        + "'" + value[3] + "',"
                        + "'" + value[4] + "',"
                        + "'" + value[5] + "',"
                        + "'" + value[6] + "',"
                        + "'" + value[7] + "',"
                        + "'" + value[8] + "',"
                        + "'" + value[9] + "',"
                        + districtId + ")";
            }
                statement.executeUpdate(query);
                System.out.println("Success + 1");
            } catch (Exception e){
                System.err.println(query);
                e.printStackTrace();
            }
        }
    }
}
