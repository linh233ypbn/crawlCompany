package common;

public interface BCDTXPath {
    String FIRST_MENU = "/html[1]/body[1]/form[1]/div[3]/div[2]/div[2]/div[1]/div[2]/div[1]/div[1]/div[2]/div[1]/ul[1]/li[1]/span[1]";
    String BUTTON_FIND = "/html[1]/body[1]/form[1]/div[3]/div[2]/div[2]/div[1]/div[2]/div[1]/div[1]/div[2]/div[1]/ul[1]/li[1]/ul[1]/li[1]/a[1]";
    String PUBLISH_TYPE = "/html[1]/body[1]/form[1]/div[3]/div[2]/div[2]/div[1]/div[2]/div[1]/div[2]/div[1]/div[1]/fieldset[1]/div[1]/table[1]/tbody[1]/tr[2]/td[1]/p[1]/select[1]";
    String PDF_FILE_PATH = Constants.DOWNLOAD_DIR + "\\new_announcement.pdf";
    interface FindPublish{
        String SUBMIT_FILTER = "/html[1]/body[1]/form[1]/div[3]/div[2]/div[2]/div[1]/div[2]/div[1]/div[2]/div[1]/div[1]/fieldset[1]/div[1]/p[1]/input[1]";
        String LIST_PDF = "/html[1]/body[1]/form[1]/div[3]/div[2]/div[2]/div[1]/div[2]/div[1]/div[2]/div[1]/div[2]/fieldset[1]/table[1]/tbody[1]/tr[1]/td[1]/div[1]/table[1]/tbody[1]/tr/td[7]";
    }
}
