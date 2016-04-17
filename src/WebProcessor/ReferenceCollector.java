package WebProcessor; /**
 * Created by George on 18.03.2016.
 */


import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.concurrent.*;

public class ReferenceCollector implements Runnable {

    private ConnectionProperties connectionProperties;

    ReferenceCollector(ConnectionProperties connectionProperties) {
        System.out.println("ReferenceCollector for" + connectionProperties.getEngine());
        this.connectionProperties = connectionProperties;
    }

    private static LinkedBlockingQueue<String> allReferences = new LinkedBlockingQueue<>();
    private static LinkedBlockingQueue<String> allDistinctDomains = new LinkedBlockingQueue<>();

    public static LinkedBlockingQueue<String> getAllReferences() {
        return allReferences;
    }

    private static Pattern patternDomainName;
    private Matcher matcher;
    private static final String DOMAIN_NAME_PATTERN
            = "([a-zA-Z0-9]([a-zA-Z0-9\\-]{0,61}[a-zA-Z0-9])?\\.)+[a-zA-Z]{2,6}";

    static {
        patternDomainName = Pattern.compile(DOMAIN_NAME_PATTERN);
    }

    public String getDomainName(String url) {
        String domainName = "";
        matcher = patternDomainName.matcher(url);
        if (matcher.find()) {
            domainName = matcher.group(0).toLowerCase().trim();
        }
        return domainName;
    }

    @Override
    public void run() {
        Document htmlPage = null;
        try {
            //URLEncoder.encode(this.connectionProperties.getQuery(), "UTF-8")
            String query = (this.connectionProperties.getEngine() + this.connectionProperties.getQuery());
            htmlPage = Jsoup
                    .connect(query)
                    .userAgent(ConnectionProperties.getUserAgent())
                    .timeout(2000).get();
        } catch (MalformedURLException ignored) {

        } catch (IOException e) {
            e.printStackTrace();
        }
        //TODO call findReferencesInHtml
        /*since if search cannot find any results there will be no references
        and it will cause NullPointerException.*/

    }

    private String remakeElementAsString(Element element) {
        int indexOfHttp = element.toString().indexOf("http");
        int indexOfEnding = element.toString().indexOf("&amp");
        return element.toString().substring(indexOfHttp, indexOfEnding);
    }

    public static void refreshCollectorData() {
        allReferences.clear();
        allDistinctDomains.clear();
    }


    //TODO find out ways to save references in general.
    public void findReferencesInHtml(Document page, SearchEngine engine) {
        switch (engine.getEngine()) {
            case "https://google.com/search?q=":
                findReferencesInGoogle(page);
                break;
            case "https://yandex.ru/search/?text=":
                findReferences(page,"y","data-cid=","href=","onmousedown=",6,2);
                break;
            case "https://bing.com/search?q=":
                findReferences(page,"b","li class=\"b_algo\"","href=","ID=SERP",6,5);
                break;
            case "https://duckduckgo.com/html/search/?q=":
                findReferences(page,"d","<h2 class=\"result__title\">","href=","\"",6,0);
                break;
            case "https://go.mail.ru/search?q=":
                findReferences(page,"m","\"url\"","http","http",0,0);
                break;
            default:
                throw new IllegalArgumentException("unsupported engine");
        }
    }

    private void findReferences(Document page, String EngineLiteral, String SplitRegex, String searchIndexStartRegex,
                                String searchIndexEndRegex, int offsetStart, int offsetEnd){
        String html = page.toString();
        if(EngineLiteral.equals("m")){
            String scriptBlock;
            int start = html.indexOf("<script>var go");
            int end = html.indexOf("</script>", start + 7);
            scriptBlock = html.substring(start, end);
            html = scriptBlock;
        }
        String [] split = html.split(SplitRegex);
        ArrayList<String> linkCandidates = new ArrayList<>(Arrays.asList(split));
        if(EngineLiteral.equals("y")){
            linkCandidates.removeIf(e -> e.contains("aria-label="));
        }else{
            linkCandidates.remove(0);
        }
        ArrayList<String> references = new ArrayList<>();
        for(String link : linkCandidates){
            references.add(getReferenceValue(link, searchIndexStartRegex,searchIndexEndRegex,offsetStart,offsetEnd));
        }
    }

    private String getReferenceValue(String element,String searchIndexStartRegex,
                                     String searchIndexEndRegex, int offsetStart, int offsetEnd ){
        String answer = null;
        int startIndex = element.indexOf(searchIndexStartRegex);
        int endIndex = element.indexOf(searchIndexEndRegex,startIndex + 6);
        if(startIndex ==-1){
            return null;
        }
        if(endIndex > startIndex + 6){
            answer = element.substring(startIndex + offsetStart,endIndex+offsetEnd);
        }
        return answer;
    }

    private void findReferencesInGoogle(Document page) {
        if (page != null) {
            Elements links = page.select("a[href]");
            links.removeIf(element -> !element.attr("href").startsWith("/url?q="));
            links.removeIf(element -> element.toString().startsWith("<a class="));
            links.forEach(element -> {
                if (!allDistinctDomains.contains(getDomainName(element.toString()))) {
                    allDistinctDomains.add(getDomainName(element.toString()));
                    allReferences.add(remakeElementAsString(element));
                }
            });
        }
    }
}
