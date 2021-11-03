import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.HashMap;
import java.util.Map;

public class Test {
    private static final String URL = "https://vinabiz.org/account/login";
    static String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.93 Safari/537.36";

    public static void main(final String[] args) throws Exception {

        Map<String, String> headers = new HashMap<String, String>();
        headers.put("accept-encoding", "gzip, deflate, br");

        Connection.Response loginForm = Jsoup.connect(URL).userAgent(userAgent).headers(headers).execute();
        Map<String, String> cookies = loginForm.cookies();
        Document html = loginForm.parse();

        String authToken = html.select("input#token").first().attr("value");
        System.out.println("Found authToken:" + authToken);

        Map<String, String> formData = new HashMap<String, String>();
        formData.put("email", "thanhngociso99@gmail.com");
        formData.put("password", "Yenphong99@");
        formData.put("token", authToken);
        headers.put("Content-Type", "application/x-www-form-urlencoded");

        System.out.println("cookies before login:");
        System.out.println(cookies);
        System.out.println(" Logged in cookie present? " + cookies.containsKey("s4icookuser"));

        Connection.Response afterLoginPage = Jsoup.connect(URL).cookies(cookies).headers(headers)
                .userAgent(userAgent).data(formData).method(Connection.Method.POST).referrer(URL).execute();
        // update cookies
        cookies = afterLoginPage.cookies();

        System.out.println("cookies after login:");
        System.out.println(cookies);
        System.out.println(" Logged in cookie present? " + cookies.containsKey("s4icookuser"));

        Connection.Response homePage = Jsoup.connect(URL).cookies(cookies).method(Connection.Method.GET).userAgent(userAgent)
                .referrer(URL).followRedirects(true).referrer(URL).headers(headers).execute();

        Document doc = homePage.parse();
        System.out.println(doc);
    }
}
