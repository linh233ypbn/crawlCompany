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
        ExcelHelper excelHelper = new ExcelHelper();
        excelHelper.export(adapterDB.getCompanies("select * from companies"));
      //  excelHelper.export(adapterDB.getCompanies("select * from companies where behavior like '%sản xuất điện%' or behavior like '%Lắp đặt hệ thống điện%'"));
//        Vinabiz vinabiz = new Vinabiz();
//        vinabiz.crawlProvince("/categories/tinhthanh/ha-noi/310030003100");
//        vinabiz.updateTaxCode();
        System.out.println(java.time.LocalDate.now());
    }
}