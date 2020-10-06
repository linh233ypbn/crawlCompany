import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.SocketTimeoutException;
import java.util.*;

public class Solution {
    public static final int COLUMN_INDEX_ID                 = 0;
    public static final int COLUMN_INDEX_NAME               = 1;
    public static final int COLUMN_INDEX_REPRESENTATIVE     = 2;
    public static final int COLUMN_INDEX_PHONE              = 3;
    public static final int COLUMN_INDEX_ADDRESS            = 4;
    public static final int COLUMN_INDEX_BEHAVIOR           = 5;
    public static final int COLUMN_INDEX_LINK               = 6;
    public static final int COLUMN_INDEX_DATE               = 7;
    public static final int COLUMN_INDEX_EMAIL              = 8;
    public static final int COLUMN_INDEX_TAXCODE            = 9;

    private static CellStyle cellStyleFormatNumber = null;
    public static int cntCompany;

    public static final String BASE_URL = "https://vinabiz.org";
    public static final String LOGIN_URL = "https://vinabiz.org/account/login";
    public static final String EMAIL = "thanhngociso99@gmail.com";
    public static final String PASSWORD = "Yenphong99@";
    public static WebDriver driver;
    public static int cntCrawl;
    public static List<Company> allCompanies = new ArrayList<>();
    public static String sheetName;

    public static Workbook wb2007;
    public static Sheet sheet;
    public static String path = "data.xlsx";
    public static void main(String[] args) throws IOException {
        System.setProperty("webdriver.chrome.driver", "chromedriver2.exe");
        driver = new ChromeDriver();

        driver.get(LOGIN_URL);
        WebElement webElement = driver.findElement(By.xpath("//input[contains(@type,'email')]"));
        webElement.sendKeys(EMAIL);
        webElement = driver.findElement(By.xpath("//input[contains(@type,'password')]"));
        webElement.sendKeys(PASSWORD);
        webElement = driver.findElement(By.xpath("//button[contains(@class,'btn btn-primary')]"));
        webElement.click();

        String linkProvince = "https://vinabiz.org/categories/tinhthanh/ha-nam/310031003100";
        Document doc = null;
        doc = Jsoup.connect(linkProvince).get();

//        Elements elements = doc.getElementsByClass("btn btn-labeled btn-default btn-block");
//        for(Element e : elements) {
//            String str = e.attr("href");
//            crawlDistrict(str);
//        }
        crawlDistrict("/categories/quanhuyen/quan-hoan-kiem/31003000310030003500");
        System.out.println("----------------------" + sheetName + "----------------------");
    }

    public static void crawlDistrict(String link){
        cntCrawl = 0;
        allCompanies = new ArrayList<>();

        District gb = new District(1, "Gia Binh");

        List<String> districts = new ArrayList<>();
        Document doc = null;
        try {
            doc = Jsoup.connect("https://vinabiz.org" + link).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Element element = doc.getElementById("wid-id-12");

        sheetName = element.childNode(1).childNode(3).childNode(1).childNode(0).toString();

        //create workbook

        try {
            wb2007 = new XSSFWorkbook(new FileInputStream(path));
        } catch (IOException e) {
            e.printStackTrace();
        }

        //create sheet
        sheet = wb2007.createSheet(sheetName);

        Elements communes = doc.getElementsByClass("btn btn-labeled btn-default btn-block");
        int id = 1;
        for (Element e : communes){
            Commune temp = new Commune();
            temp.id = id++;
            temp.name = e.childNode(1).childNode(0).childNode(0).toString();
            temp.cntCompany = Integer.parseInt(e.childNode(2).childNode(0).toString());
            temp.link = BASE_URL + e.attr("href");
            gb.communies.add(temp);
            try {
                getCompany(temp.link);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        try {
            export();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void export() throws IOException {
        int rowIndex = 0;
        writeHeader(sheet, rowIndex);
        rowIndex++;

        for(Company company : allCompanies){
            // Create row
            Row row = sheet.createRow(rowIndex);
            // Write data on row
            writeBook(company, row);
            rowIndex++;
        }

        // Auto resize column witdth
        int numberOfColumn = sheet.getRow(0).getPhysicalNumberOfCells();
        autosizeColumn(sheet, numberOfColumn);

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
    private static void writeBook(Company company, Row row) {
        cntCompany++;
        if (cellStyleFormatNumber == null) {
            // Format number
            short format = (short)BuiltinFormats.getBuiltinFormat("#,##0");
            // DataFormat df = workbook.createDataFormat();
            // short format = df.getFormat("#,##0");

            //Create CellStyle
            Workbook workbook = row.getSheet().getWorkbook();
            cellStyleFormatNumber = workbook.createCellStyle();
            cellStyleFormatNumber.setDataFormat(format);
        }

        Cell cell = row.createCell(COLUMN_INDEX_ID);
        cell.setCellValue(cntCompany);

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
    private static void autosizeColumn(Sheet sheet, int lastColumn) {
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

    public static List<Company> getCompany(String  link) throws IOException {
        List<Company> companyList = new ArrayList<>();
        Document docCompany = null;
        String page = "/10";
        int index = 10;
        do {
            try {
                docCompany = Jsoup.connect(link + page).timeout(15000).get();
            } catch (SocketTimeoutException e){
                System.out.println("No page " + index);
                return companyList;
            }
            Elements companies = docCompany.getElementsByClass("row margin-right-15 margin-left-10");
            crawlCompany(companyList, companies);
            index++;
            page = "/" + index;
        } while(index < 15);
        return companyList;
    }

    public static void crawlCompany(List<Company> companies, Elements elements) throws IOException {
        for(Element element : elements){
            System.out.println(cntCrawl++);
            Company company = new Company();

            company.address = element.childNode(1).childNode((element.childNode(1).childNodeSize() - 1)).toString();
            company.name = element.childNode(1).childNode(1).childNode(1).childNode(0).toString();
            //check is active ?
            Boolean status = element.childNode(1).childNode(1).childNode(1).attr("style") == "";
            if(!status)
                continue;;
            //link to company
            String link = BASE_URL + element.childNode(1).childNode(1).childNode(1).attr("href");
            company.link = link;

            try{
                Connection.Response response = Jsoup.connect(link)
                        .method(Connection.Method.GET)
                        .timeout(Integer.MAX_VALUE)
                        .execute();
            } catch (Exception e){
                continue;
            }
            Document docCom = Jsoup.connect(link)
                    .followRedirects(true)
                    .ignoreContentType(true)
                    .timeout(12000) // optional
                    .header("Accept-Language", "pt-BR,pt;q=0.8") // missing
                    .header("Accept-Encoding", "gzip,deflate,sdch") // missing
                    .userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/32.0.1700.107 Safari/537.36") // missing
                    .referrer("https://vinabiz.org/") // optional
                    .execute()
                    .parse();
            String htmlString = docCom.html();
            Elements e = docCom.getElementsByClass("table table-bordered");
            Element data = e.get(0);
            Node childNode = data.childNode(1).childNode(4).childNode(7);
            if(childNode.childNodeSize() > 0)
                company.date = childNode.childNode(0).toString();
            childNode = data.childNode(1).childNode(20).childNode(3);
            if(childNode.childNodeSize() == 0) continue;
            company.representative = childNode.childNode(0).toString();
            if(data.childNode(1).childNode(16).childNodeSize() > 3)
                company.phoneNumber = data.childNode(1).childNode(16).childNode(3).childNode(0).toString();
            else continue;
            company.behavior = data.childNode(1).childNode(36).childNode(3).childNode(0).toString();
            WebElement webElement = null;
            if(cntCompany == 5){
                System.out.println();
            }
            try {
                driver.get(company.link);
            } catch (Exception ex){
                continue;
            }
            //get phone
            try{
                webElement = driver.findElement(By.xpath("//table[@class='table table-bordered']/tbody/tr[9]/td[2]/b"));
            } catch (Exception ex){
                System.out.println("NO PHONE");
                continue;
            }
            company.phoneNumber = webElement.getText();
            if(company.phoneNumber.equals(""))
                continue;
            //get email
            boolean flag = true;
            try{
                webElement = driver.findElement(By.xpath("//table[@class='table table-bordered']/tbody/tr[10]/td[2]/a"));
            } catch (Exception ex){
                System.out.println("NO EMAIL");
                flag = false;
            }
            if(flag)
                company.email = webElement.getText();
            try {
                webElement = driver.findElement(By.xpath("//div[@class='widget-body no-padding']/div/p/b[1]"));
                company.taxCode = webElement.getText();
            } catch (Exception ex){
                System.out.println("NO TAX CODE");
            }

            System.out.println(company.phoneNumber);
            //check if not have phone number
            //if(company.phoneNumber.equals(" ")) continue;
            companies.add(company);
            allCompanies.add(company);
        }
    }
}

class Company{
    public String name;
    public String behavior;
    public String address;
    public String phoneNumber;
    public String representative;
    public String link;
    public String date;
    public String email;
    public String taxCode;
}

class Commune{
    public int id;
    public int cntCompany;
    public String link;
    public String name;
    public List<Company> companyList;

    Commune(){
        companyList = new ArrayList<>();
    }
}

class District{
    public int id;
    public String name;
    public Comparator<Commune> cntCompanyComparator = new Comparator<Commune>() {
        @Override
        public int compare(Commune d1, Commune d2) {
            return d1.cntCompany - d2.cntCompany;
        }
    };
    public PriorityQueue<Commune> communies;

    public District(int i, String n) {
        Commune a = new Commune();
        id = i;
        name = n;
        communies = new PriorityQueue<Commune>(cntCompanyComparator);
    }
}
