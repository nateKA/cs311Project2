import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
public class WebPage {

    private String URL;
    private String baseURL;
    private String page = null;
    private static int serverRequests = 0;
    private HashMap<Integer, String> topics = new HashMap<>();

    public WebPage(String baseURL, String URL) {
        this.URL = URL;
        this.baseURL = baseURL;

    }

    public void download(){
        page = WebUtils.getPageAsString(baseURL, URL);
        page = page.replaceFirst("[\\s\\S]*?<p>","");
        if(serverRequests >= 25){
            try {
                System.out.println("\tSLEEPING");
                Thread.sleep(3000);
                serverRequests = 0;
            }catch (Exception e){

            }
        }
        serverRequests++;
    }

    public String getURL() {
        return URL;
    }

    public String getBaseURL() {
        return baseURL;
    }

    public String getPage() {
        return page;
    }

    public List<String> getLinks(){
        if(page == null)download();
        return WebUtils.extractLinks(page);
    }



    /**
     * Page is valid if it contains all topics
     * @param topics
     * @return
     */
    public boolean containsTopics(ArrayList<String> topics){
        if(topics == null || topics.size() == 0) return true;
        if(page == null)download();

        boolean contains = true;
        for(String topic: topics){
            contains = contains && topics.contains(topic);

            if(!contains)break;
        }

        return contains;
    }

    @Override
    public int hashCode(){
        return WebUtils.combinePaths(baseURL,URL).hashCode();
    }

    public static void main(String[] args){
        WebPage page = new WebPage(WikiCrawler.BASE_URL,"/wiki/Complexity");
        page.download();
        try{
            PrintWriter pw = new PrintWriter("complexity.txt");
            pw.println(page.getPage());
            pw.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
