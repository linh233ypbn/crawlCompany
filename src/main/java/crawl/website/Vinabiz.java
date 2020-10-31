package crawl.website;

import adapter.AdapterDB;
import module.Commune;
import module.Company;
import module.District;
import module.Info;
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

            getCompany(provinceUrl, 11000, 11000);
            AdapterDB.addCompanies(allCompanies);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
                getCompany(temp.link, 10051, 10100);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public List<Company> getCompany(String  link, int start, int end) throws IOException {
        List<Company> companyList = new ArrayList<>();
        String page = "/" + start;
        int index = start;
        do {
            System.out.println("Page "+ index);
            if(!parseHTMLFrom(link + page))
                break;
            Elements companies = doc.getElementsByClass("row margin-right-15 margin-left-10");
            crawlCompany(companyList, companies);
            index++;
            page = "/" + index;
        } while(index < end);
        return companyList;
    }

    public void crawlCompany(List<Company> companies, Elements elements) throws IOException {
        for(Element element : elements){
            Company company = new Company();

            company.address = getCompanyInfo(element, Info.ADDRESS);
            company.name    = getCompanyInfo(element, Info.NAME);
            company.link    = getCompanyInfo(element, Info.LINK);
            if(!isAvailable(element)) continue;

            Element data = parseHTMLCompany(company.link);
            if(data == null) continue;

            company.date            = getCompanyInfo(data, Info.DATE);
            company.representative  = getCompanyInfo(data, Info.REPRESENTATIVE);
            company.behavior        = getCompanyInfo(data, Info.BEHAVIOR);

            getDriverSelenium(company.link);

            company.phoneNumber     = getCompanyInfo(data, Info.PHONE_NUMBER);
            company.email           = getCompanyInfo(data, Info.EMAIL);
            company.taxCode         = getCompanyInfo(data, Info.TAX_CODE);
            company.idDistrict      = getCompanyInfo(data, Info.ID_DISTRICT);


            if(company.phoneNumber.isEmpty() || company.phoneNumber.equals(""))
                continue;

            System.out.println(company.phoneNumber);

            companies.add(company);
            allCompanies.add(company);
        }
    }

    public boolean parseHTMLFrom(String url){
        try {
            doc = Jsoup.connect(url).timeout(15000).get();
        } catch (IOException e) {
            System.err.println("Failed to load HTML + [" + url + "]");
            return false;
        }
        return true;
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
                case ID_DISTRICT:
                case PHONE_NUMBER:
                case EMAIL:
                case TAX_CODE:
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
                case TAX_CODE:
                    webElement = driver.findElement(By.xpath("//div[@class='widget-body no-padding']/div/p/b[1]"));
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
            result = element.childNode(1).childNode(1).childNode(1).attr("style") == "";
        } catch (Exception e) {
            System.err.println("Failed to check company Available");
            return false;
        }
        return result;
    }
}
