import adapter.AdapterDB;
import crawl.website.Vinabiz;
import export.ExcelHelper;
import module.Info;

import java.sql.Connection;
import java.sql.Statement;

import static java.sql.DriverManager.getConnection;

public class Runner {
    public static void main(String[] args) throws Exception {
        AdapterDB adapterDB = new AdapterDB();
//        ExcelHelper excelHelper = new ExcelHelper();
//        excelHelper.export(adapterDB.getCompanies("select * from companies where year(date) <= 2018 and iddistrict <= 519 and iddistrict >= 496 order by iddistrict"));
        Vinabiz vinabiz = new Vinabiz();
        vinabiz.crawlProvince("/categories/tinhthanh/tp-ho-chi-minh/370030003100");
    }
}





