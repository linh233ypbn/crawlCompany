package crawl.website;

import common.BCDTXPath;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.rendering.PDFRenderer;
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
    public static void main(String[] args) throws IOException {
        convertImageToText();
    }

    public static void convertImageToText(){
        File imageFile = new File("Capture.PNG");
        Tesseract tesseract = new Tesseract();
        try {
            //tesseract.setDatapath("D:\\Download\\Tess4J-3.4.8-src\\Tess4J\\tessdata");
            tesseract.setDatapath("D:\\Image\\tessdata");
            tesseract.setLanguage("vie");

            // the path of your tess data folder
            // inside the extracted file
            String text
                    = tesseract.doOCR(imageFile);

            // path of your image file
            System.out.print(text);
        }
        catch (TesseractException e) {
            e.printStackTrace();
        }
    }

    public static void convertPDFToImage() throws IOException {
        File pdfFile = new File(BCDTXPath.PDF_FILE_PATH);
        PDDocument document = PDDocument.load(pdfFile);
        PDFRenderer pr = new PDFRenderer (document);
        BufferedImage bi = pr.renderImageWithDPI (1, 300);
        ImageIO.write (bi, "JPEG", new File ("ok1.jpeg"));
    }

    public static void crawl(){
        System.setProperty("webdriver.chrome.driver", "chromedriver.exe");
        System.setProperty("webdriver.gecko.driver", "geckodriver.exe");

        driver = new ChromeDriver();
        driver.get(BASE_URL);

        clickToElementByXpath(BCDTXPath.FIRST_MENU);
        clickToElementByXpath(BCDTXPath.BUTTON_FIND);
        selectValueForElementByXpath(BCDTXPath.PUBLISH_TYPE, NEW_TYPE);
        clickToElementByXpath(BCDTXPath.FindPublish.SUBMIT_FILTER);

        List<WebElement> elements = driver.findElements(By.xpath(BCDTXPath.FindPublish.LIST_PDF));
        elements.forEach(e -> {
            e.click();
            try {
                File pdfFile = new File(BCDTXPath.PDF_FILE_PATH);
                PDDocument document = PDDocument.load(pdfFile);
                document.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
    }

    public static void clickToElementByXpath(String xpath){
        driver.findElement(By.xpath(xpath)).click();
    }
    public static void selectValueForElementByXpath(String xpath, String value){
        new Select(driver.findElement(By.xpath(xpath))).selectByValue(value);
    }
}
