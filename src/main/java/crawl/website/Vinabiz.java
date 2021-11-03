package crawl.website;

import adapter.AdapterDB;
import module.Commune;
import module.Company;
import module.District;
import module.Info;
import org.apache.commons.codec.binary.Base64;
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
import org.openqa.selenium.firefox.FirefoxDriver;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

public class Vinabiz {
    public static final String BASE_URL = "https://vinabiz.org";
    public static final String LOGIN_URL = "https://vinabiz.org/account/login";
    public static final String EMAIL = "thanhngociso99@gmail.com";
    public static final String PASSWORD = "Yenphong99@";

    public static ArrayList<Company> allCompanies = null;
    public static Document doc = null;

    public static WebDriver driver;
    public static int cnt;

    private static String base64login = new String(Base64.encodeBase64((EMAIL + ":" + PASSWORD).getBytes()));

    public Vinabiz(){
        System.setProperty("webdriver.chrome.driver", "chromedriver.exe");
        System.setProperty("webdriver.gecko.driver", "geckodriver.exe");
        allCompanies = new ArrayList<>();

        driver = new ChromeDriver();
        driver.get(LOGIN_URL);

        WebElement webElement = driver.findElement(By.xpath("//input[contains(@type,'email')]"));
        webElement.sendKeys(EMAIL);

        webElement = driver.findElement(By.xpath("//input[contains(@type,'password')]"));
        webElement.sendKeys(PASSWORD);

        webElement = driver.findElement(By.xpath("//button[contains(@class,'btn btn-primary')]"));
        webElement.click();

        String login = EMAIL + ":" + PASSWORD;


        cnt = 0;
    }

    public void crawlProvince(String provinceUrl){
        String provinceName = "empty";
        provinceUrl = "https://vinabiz.org" + provinceUrl;
        parseHTMLFrom( provinceUrl);
        try {
            provinceName = doc.getElementsByClass("page-title txt-color-blueDark")
                    .get(0)
                    .childNode(7)
                    .childNode(1)
                    .childNode(0).toString();

            //getCompany(provinceUrl, 13269, 13500); HCM
//            getCompany(provinceUrl, 16300, 16400);
//            getCompany(provinceUrl, 80, 100);
//            getCompany(provinceUrl, 130, 200); //Long An
//            getCompany(provinceUrl, 600, 800); //Hai Phong
//            getCompany(provinceUrl, 8000, 8392); //Ha Noi
            getCompany(provinceUrl, 3000, 9500);
            //AdapterDB.addCompanies(allCompanies);
        } catch (Exception e) {
            e.printStackTrace();
        }
        driver.close();
    }

    public void crawlDistrict(String link){
        String districtName = "empty";
        parseHTMLFrom("https://vinabiz.org" + link);
        Element element = doc.getElementById("wid-id-12");

        districtName = element.childNode(1).childNode(3).childNode(1).childNode(0).toString();
        District gb = new District(1, districtName);

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
                getCompany(temp.link, 13112, 13120);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void getCompany(String  link, int start, int end) throws IOException {
        String page = "/" + start;
        int index = start;
        do {
            System.err.println("Page "+ index);
            if(!parseHTMLFrom(link + page)){
                index++;
                continue;
            }
            Elements companies = doc.getElementsByClass("row margin-right-15 margin-left-10");
            crawlCompany(companies);
            index++;
            page = "/" + index;
        } while(index < end);
    }

    public void crawlCompany(Elements elements) throws IOException {
        for(Element element : elements){
            System.err.println("=======================[" + cnt++ + "]=======================");
            Company company = new Company();

            company.address = getCompanyInfo(element, Info.ADDRESS);
            company.name    = getCompanyInfo(element, Info.NAME);
            company.link    = getCompanyInfo(element, Info.LINK);

            if(!isAvailable(element)) continue;

            parseHTMLFrom(company.link);

            company.taxCode = getCompanyInfo(element, Info.TAX_CODE);

            if(AdapterDB.isExists(company.taxCode)) {
                System.err.println("Exists.!");
                return;
            }

            Element data = parseHTMLCompany(company.link);
            if(data == null) continue;

            company.date            = getCompanyInfo(data, Info.DATE);
            company.representative  = getCompanyInfo(data, Info.REPRESENTATIVE);
            company.behavior        = getCompanyInfo(data, Info.BEHAVIOR);

            getDriverSelenium(company.link);

            company.phoneNumber     = getCompanyInfo(data, Info.PHONE_NUMBER);
            company.email           = getCompanyInfo(data, Info.EMAIL);
            company.idDistrict      = getCompanyInfo(data, Info.ID_DISTRICT);


            if(company.phoneNumber.isEmpty() || company.phoneNumber.equals("")){
                System.err.println("Ignore.");
                continue;
            }

            System.out.println(company.phoneNumber);

            //allCompanies.add(company);
            AdapterDB.addCompany(company);
        }
    }

    public boolean parseHTMLFrom(String url){
        int TRY = 1;
        while(TRY++ <= 6){
            try {
                doc = Jsoup.connect(url).timeout(15000).header("Authorization", "Basic " + base64login).get();
                return true;
            } catch (IOException e) {
                System.err.println("Failed to load HTML + [" + url + "]");
                return false;
            }
        }
        return false;
    }

    public Element parseHTMLCompany(String url){
        Document docCom;
        Connection.Response response;
        Elements elements;
        Element data = null;
        try {
            response = Jsoup.connect(url)
                    .method(Connection.Method.GET)
                    .timeout(Integer.MAX_VALUE)
                    .execute();
            docCom = Jsoup.connect(url)
                    .followRedirects(true)
                    .ignoreContentType(true)
                    .timeout(12000) // optional
                    .header("Accept-Language", "pt-BR,pt;q=0.8") // missing
                    .header("Accept-Encoding", "gzip,deflate,sdch") // missing
                    .userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/32.0.1700.107 Safari/537.36") // missing
                    .referrer("https://vinabiz.org/") // optional
                    .execute()
                    .parse();
            elements = docCom.getElementsByClass("table table-bordered");
            data = elements.get(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    public String getCompanyInfo(Element element, Info info){
        String result = "empty";
        try {
            switch (info){
                case NAME:
                    result = element.childNode(1).childNode(1).childNode(1).childNode(0).toString();
                    break;
                case BEHAVIOR:
                    result = element.childNode(1).childNode(36).childNode(3).childNode(0).toString();
                    break;
                case ADDRESS:
                    result = element.childNode(1).childNode((element.childNode(1).childNodeSize() - 1)).toString();
                    break;
                case REPRESENTATIVE:
                    result = element.childNode(1).childNode(20).childNode(3).childNode(0).toString();
                    break;
                case LINK:
                    result = BASE_URL + element.childNode(1).childNode(1).childNode(1).attr("href");
                    break;
                case DATE:
                    result = element.childNode(1).childNode(4).childNode(7).childNode(0).toString();
                    try{
                        String date[] = result.split("/");
                        result = date[2] + "-" + date[1] + "-" + date[0];
                    } catch (Exception e){
                        result = "null";
                    }
                    break;
                case TAX_CODE:
                    Elements elements = doc.getElementsByClass("padding-10");
                    Element e = elements.get(0);
                    result = e.childNode(5).childNode(1).childNode(0).toString();
                    System.out.println("TAX_CODE: " + result);
                    break;
                case ID_DISTRICT:
                case PHONE_NUMBER:
                case EMAIL:
                    result = getInfoBySelenium(info);
                    break;
            }
            result.replace('\'', '`');
        } catch (Exception e){
            System.err.println("Failed to load " + info);
        }
        return result;
    }

    public String getIDDistrictFromAddress(String address){
        String query = "select iddistrict from district where district_name like '" + address + "'";
        String idDistrict = AdapterDB.getInfoQuery(query);
        return idDistrict;
    }
    public String getInfoBySelenium(Info info){
        WebElement webElement = null;
        String result = "";
        try{
            switch (info){
                case PHONE_NUMBER:
                    webElement = driver.findElement(By.xpath("//table[@class='table table-bordered']/tbody/tr[9]/td[2]/b"));
                    break;
                case EMAIL:
                    webElement = driver.findElement(By.xpath("//table[@class='table table-bordered']/tbody/tr[10]/td[2]/a"));
                    break;
                case ID_DISTRICT:
                    webElement = driver.findElement(By.xpath("//h1[@class='page-title txt-color-blueDark']/span/span/a[2]"));
                    break;
            }
            result = webElement.getText();
            if(info == Info.ID_DISTRICT)
                result = getIDDistrictFromAddress(result);
        } catch (Exception ex){
//            ex.printStackTrace();
            System.out.println(info + ": empty");
        }
        return result;
    }

    public void getDriverSelenium(String link){
        try {
            driver.get(link);
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public boolean isAvailable(Element element){
        boolean result;
        try {
            String style = element.childNode(1).childNode(1).childNode(1).attr("style");
            result = style.isEmpty();
        } catch (Exception e) {
            System.err.println("Failed to check company Status");
            return false;
        }
        return result;
    }

    public void updateTaxCode(){
        String query = "select * from companies where tax_code in (select tax_code from companies group by tax_code having count(tax_code) > 1)";
        ArrayList<Company> companies = AdapterDB.getCompanies(query);
        int cnt = 0;
        System.out.println("TOTAL: " + companies.size());
        for(Company company : companies){
            parseHTMLFrom(company.link);
            Elements elements = doc.getElementsByClass("padding-10");
            Element e = elements.get(0);
            String taxCode = e.childNode(5).childNode(1).childNode(0).toString();
            cnt++;
            System.out.println(cnt + ". ID: " + company.id + " || TAX: " + taxCode);
            AdapterDB.executeUpdate("update companies set tax_code = '" + taxCode + "' where id = " + company.id + "");
        }
    }
}
