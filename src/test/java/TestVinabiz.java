import adapter.AdapterDB;
import crawl.website.Vinabiz;
import export.ExcelHelper;
import org.junit.Test;

public class TestVinabiz {
    @Test
    public void testGetListProvince(){
        Vinabiz vinabiz = new Vinabiz();
        vinabiz.getListProvince();
    }
    @Test
    public void testAdapter(){
        AdapterDB adapterDB = new AdapterDB();
    }
    @Test
    public void importExcelFile() throws Exception {
        AdapterDB adapterDB = new AdapterDB();
        String provinceName = "An Giang";
        String query = "select district_id from districts where district_name like 'unknown' and province_id = (select province_id from provinces where province_name like '" + provinceName + "')";
        System.out.println(AdapterDB.getInfoQuery(query));
    }
}
