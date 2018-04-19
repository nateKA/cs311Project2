import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebPage {

    private String URL;
    private String baseURL;
    private String page = null;
    private static int serverRequests = 0;
    public static Debugger debugger = null;
    private HashMap<Integer, String> topics = new HashMap<>();

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

    /**
     * Finds the topics and puts them into a hashMap for instant access
     */
    private void extractTopics(){
        Matcher m = Pattern.compile("<li class=\"toclevel-\\d+ tocsection-\\d+\"><a href=\"#(.*)\">").matcher(page);
        while(m.find()){
            MatchResult result = m.toMatchResult();
            String topic = result.group(1);
            topics.put(topic.hashCode(),topic);
        }
    }

    /**
     * Page is valid if it contains all topics
     * @param page
     * @param topics
     * @return
     */
    public boolean containsTopics(ArrayList<String> topics){
        if(topics == null || topics.size() == 0) return true;
        if(page == null)download();
        if(this.topics.size() == 0)extractTopics();

        boolean contains = true;
        for(String topic: topics){
            String value = this.topics.get(topic.hashCode());
            contains = contains && value != null && value.equalsIgnoreCase(topic);

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
            PrintWriter pw = new PrintWriter("complexity");
            pw.println(page.getPage());
            pw.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
