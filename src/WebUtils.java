import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebUtils {

    /**
     * Downloads a page at the baseURL + page location.
     * If page variable contains "https://" then it will ignore the baseURL and assume the page
     * argument represents the full path.
     *      Example if URL = https://en.wikipedia.org/wiki/Iowa_State_University
     *          baseURL = https://en.wikipedia.org/
     *          page = wiki/Iowa_State_University
     * @param baseURL
     * @param page
     * @return
     */
    public static InputStream getURLStreamer(String baseURL, String page){
        if(!page.contains("https://")){
            page = combinePaths(baseURL, page);
        }

        try {
            URL url = new URL(page);
            InputStream is = url.openStream();  // throws an IOException

            return is;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Returns the requested page as a String
     * @param baseURL
     * @param page
     * @return
     */
    public static String getPageAsString(String baseURL, String page){
        BufferedReader br = new BufferedReader(new InputStreamReader(getURLStreamer(baseURL,page)));
        String str = "";
        String line;
        try {
            while ((line = br.readLine()) != null) {
                str += line+"\n";
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return str;
    }

    /**
     * Returns all links that meet standards
     * @param page
     */
    public static List<String> extractLinks(String page){
        List<String> links = new ArrayList<>();

        Matcher m = Pattern.compile("href=\"(/wiki/.*?)\"").matcher(page);
        while(m.find()){
            MatchResult result = m.toMatchResult();
            links.add(result.group(1));
        }

        return links;
    }

    /**
     * Page is valid if it contains all topics
     * @param page
     * @param topics
     * @return
     */
    public static boolean pageHasTopics(String page, List<String> topics){
        boolean isValid = true;
        for(String s: topics){
            isValid = isValid && page.contains(s);
            if(isValid == false) break;
        }
        return isValid;
    }

    /**
     * Guarantees two paths are combined together in a valid format
     * @param a
     * @param b
     * @return
     */
    public static String combinePaths(String a, String b){
        if(a.endsWith("/") && b.startsWith("/")){
            return a + b.substring(1);
        }else if(!a.endsWith("/") && !b.startsWith("/")){
            return a + "/" + b;
        }else{
            return a + b;
        }
    }

}
