import java.util.ArrayList;
import java.util.List;

public class WebPage {

    private String URL;
    private String baseURL;
    private String page = null;

    public WebPage(String baseURL, String URL) {
        this.URL = URL;
        this.baseURL = baseURL;

    }

    public void download(){
        page = WebUtils.getPageAsString(baseURL, URL);
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
}
