import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.HashMap;

public class Test {

    public static void main(String[] args) throws IOException {
        final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/84.0.4147.105 Safari/537.36";
        String loginFormUrl = "https://vinabiz.org/account/login";
        String loginActionUrl = "https://vinabiz.org/account/login";
        String email = "thanhngociso99@gmail.com";
        String password = "Yenphong99@";

        HashMap<String, String> cookies = new HashMap<>();
        HashMap<String, String> formData = new HashMap<>();

        Connection connection = Jsoup.connect(loginFormUrl);

        Connection.Response loginForm = connection.method(Connection.Method.GET).userAgent(USER_AGENT).execute();
        Document loginDoc = loginForm.parse();


        cookies.putAll(loginForm.cookies()); // save the cookies, this will be passed on to next request
        /**
         * Get the value of authenticity_token with the CSS selector we saved before
         **/
        Element element = loginDoc.getElementById("login-form");
        String authToken = element.childNode(5).attr("value");

        formData.put("__RequestVerificationToken", authToken);
        formData.put("email", email);
        formData.put("password", password);
        formData.put("rememberMe", "true");

        System.out.println(authToken);
        Connection.Response homePage = connection
                .method(Connection.Method.POST)
                .cookies(cookies)
                .userAgent(USER_AGENT)
                .ignoreHttpErrors(true)
                .execute();

        System.out.println(homePage.parse().html());
    }
}
