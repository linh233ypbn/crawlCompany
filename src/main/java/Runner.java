import adapter.AdapterDB;
import crawl.website.Vinabiz;
import export.ExcelHelper;
import module.Info;

import java.sql.Connection;
import java.sql.Statement;

import static java.sql.DriverManager.getConnection;

public class Runner {
    public static void main(String[] args) throws Exception {
        AdapterDB adapterDB = new AdapterDB("jdbc:mysql://localhost:3306/ct_db");
        ExcelHelper excelHelper = new ExcelHelper();
//        excelHelper.export(adapterDB.getCompanies("select * from companies"));
        ExcelHelper.exportMongoCompany(AdapterDB.getMongoCompanies("select * from companies where month(date_input) = 3"));
//        Vinabiz vinabiz = new Vinabiz();
//        vinabiz.crawlProvince("/categories/tinhthanh/tp-ho-chi-minh/370030003100");
//        vinabiz.updateTaxCode();
        System.out.println(java.time.LocalDate.now());
    }
}