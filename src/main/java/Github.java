import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.HashMap;

public class Github {
    public static void main(String[] args) throws IOException {
        final String USER_AGENT = "\"Mozilla/5.0 (Windows NT\" +\n" +
                "          \" 6.1; WOW64) AppleWebKit/535.2 (KHTML, like Gecko) Chrome/15.0.874.120 Safari/535.2\"";
        String loginFormUrl = "https://github.com/login";
        String loginActionUrl = "https://github.com/session";
        String username = "nvlinh233@gmail.com";
        String password = "ijykqs8w";

        HashMap<String, String> cookies = new HashMap<>();
        HashMap<String, String> formData = new HashMap<>();

        Connection.Response loginForm = Jsoup.connect(loginFormUrl).method(Connection.Method.GET).userAgent(USER_AGENT).execute();
        Document loginDoc = loginForm.parse(); // this is the document that contains response html

        cookies.putAll(loginForm.cookies()); // save the cookies, this will be passed on to next request
        /**
         * Get the value of authenticity_token with the CSS selector we saved before
         **/
        Element element = loginDoc.getElementById("login");
        String authToken = element.childNode(3).childNode(0).attr("value");

        formData.put("commit", "Sign in");
        formData.put("utf8", "e2 9c 93");
        formData.put("login", username);
        formData.put("password", password);
        formData.put("authenticity_token", authToken);

        Connection.Response homePage = Jsoup.connect(loginActionUrl)
                .cookies(cookies)
                .data(formData)
                .method(Connection.Method.POST)
                .userAgent(USER_AGENT)
                .execute();

        System.out.println(homePage.parse().html());
    }
}
