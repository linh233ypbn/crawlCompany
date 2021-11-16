package crawl.website;

import common.BCDTXPath;
import module.Company;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.text.PDFTextStripper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Select;

import javax.imageio.ImageIO;
import javax.print.attribute.standard.MediaSize;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BCDT {
    public static final String BASE_URL = "https://bocaodientu.dkkd.gov.vn/";
    public static WebDriver driver;

    public static final String NEW_TYPE = "NEW";
    public static void main(String[] args) throws Exception {
        goToListCompany();
        int currentPage = 1;
        while(currentPage <= 25){
            List<WebElement> pages = driver.findElements(By.xpath("//tr[@class='Pager']//table//td"));
            if(currentPage == 1){
                for(int i = 1; i <= 5; i++){
                    crawl();
                    pages.get(i).click();
                }
                currentPage = 6;
            } else {
                for(int i = 3; i <= 7; i++){
                    crawl();
                    if(i == 7 && currentPage == 21) return;
                    pages.get(i).click();
                }
                currentPage += 5;
            }
        }
    }

    public static void crawlCompany(){
        Company company = new Company();

    }

    public static String getNameFromPDF() throws IOException, InterruptedException {
        convertPDFToImage();
        String data = convertImageToText();
        String[] arr = data.split("\n");
        String res = "null";
        for(String s : arr){
            if(s.startsWith("* Họ và tên: ")){
                res = s.replaceAll("\\* Họ và tên: ", "").replaceAll("Giới tính:", "-");
                break;
            }
        }
        File file = new File(BCDTXPath.PDF_FILE_PATH);
        file.delete();
        return res;
    }

    public static String convertImageToText(){
        File imageFile = new File("ok.jpeg");
        Tesseract tesseract = new Tesseract();
        String data = "";
        try {
            tesseract.setDatapath("D:\\Image\\tessdata");
            tesseract.setLanguage("vie");

            // the path of your tess data folder
            // inside the extracted file
            data = tesseract.doOCR(imageFile);

            // path of your image file
           // System.out.print(data);
        }
        catch (TesseractException e) {
            e.printStackTrace();
        }
        return data;
    }

    public static void convertPDFToImage() throws IOException, InterruptedException {
        Thread.sleep(3000);
        File file = new File(BCDTXPath.PDF_FILE_PATH);
        PDDocument document = PDDocument.load(file);
        String[] arr = new PDFTextStripper().getText(document).split("\n");
        String phone = "";
        String email = "";
        for(int i = 0; i < arr.length; i++){
            if(arr[i].startsWith("Email:")){
                phone = arr[i - 1].replace("\r", "");
                email = arr[i].replace("\r", "");
                break;
            }
        }

        System.out.println("Phone: " + phone + " | " + email);
        PDFRenderer pr = new PDFRenderer (document);

        int page = document.getNumberOfPages();
        BufferedImage bi = pr.renderImageWithDPI (page - 1, 300);
        ImageIO.write (bi, "JPEG", new File ("ok.jpeg"));
    }

    public static void goToListCompany(){
        System.setProperty("webdriver.chrome.driver", "chromedriver.exe");
        System.setProperty("webdriver.gecko.driver", "geckodriver.exe");

        driver = new ChromeDriver();
        driver.get(BASE_URL);

        clickToElementByXpath(BCDTXPath.FIRST_MENU);
        clickToElementByXpath(BCDTXPath.BUTTON_FIND);
        selectValueForElementByXpath(BCDTXPath.PUBLISH_TYPE, NEW_TYPE);
        clickToElementByXpath(BCDTXPath.FindPublish.SUBMIT_FILTER);
    }

    public static void crawl() throws IOException, InterruptedException {

        List<WebElement> elements = driver.findElements(By.xpath("//table[@class='gridview']//tr"));
        List<WebElement> downloadButtons = driver.findElements(By.xpath("//table[@class='gridview']//input"));
        for(int i = 1; i <= 20; i++){
            WebElement element = elements.get(i);
            String[] info = element.getText().split("\n");
            String name = info[4];
            String code = info[5].replaceFirst("MÃ SỐ DN: ", "");
            String province = info[6].replaceFirst("Tỉnh ", "");
            downloadButtons.get(i - 1).click();
            String representative = getNameFromPDF();
            System.out.println(name + " | " + code + " | " + province + " | " + representative);
        }

//        List<WebElement> elements = driver.findElements(By.xpath(BCDTXPath.FindPublish.LIST_PDF));
//        elements.forEach(e -> {
//            e.click();
//            try {
//                File pdfFile = new File(BCDTXPath.PDF_FILE_PATH);
//                PDDocument document = PDDocument.load(pdfFile);
//                document.close();
//            } catch (IOException ex) {
//                ex.printStackTrace();
//            }
//        });
    }

    public static void clickToElementByXpath(String xpath){
        driver.findElement(By.xpath(xpath)).click();
    }
    public static void selectValueForElementByXpath(String xpath, String value){
        new Select(driver.findElement(By.xpath(xpath))).selectByValue(value);
    }
}
