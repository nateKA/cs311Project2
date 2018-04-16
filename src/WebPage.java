import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class WebPage {

    private String URL;
    private String baseURL;
    private String page = null;
    private static int serverRequests = 0;
    public static Debugger debugger = null;

    public WebPage(String baseURL, String URL) {
        this.URL = URL;
        this.baseURL = baseURL;

    }

    public void download(){
        page = WebUtils.getPageAsString(baseURL, URL);
        page = page.replaceFirst(".*<[pP]>","");
        if(debugger!=null)
        debugger.println("\tDOWNLOAD #"+(++serverRequests));
        if(serverRequests >= 25){
            try {
                if(debugger!=null)
                debugger.println("\tSLEEPING");
                Thread.sleep(3000);
                serverRequests = 0;
            }catch (Exception e){

            }
        }
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

    public boolean containsTopics(ArrayList<String> topics){
        if(topics == null || topics.size() == 0) return true;
        if(page == null)download();

        return WebUtils.pageHasTopics(page,topics);
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
