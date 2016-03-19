package WebProcessor; /**
 * Created by George on 18.03.2016.
 */

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import WebProcessor.SearchEngine;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.concurrent.*;

public class WebPagesCollector {

    WebPagesCollector() {
    }

    LinkedBlockingQueue<String> allReferences = new LinkedBlockingQueue<>();
    LinkedBlockingQueue<String> allDistinctDomains = new LinkedBlockingQueue<>();

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

    public synchronized void dataSearchRequestParsing(String query, SearchEngine engine) throws IOException {
        Document htmlpage = Jsoup
                .connect("http://google.com/search?q=value")
                .userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) ")
                .timeout(2000).get();

        Elements links = htmlpage.select("a[href]");
        links.removeIf(element -> !element.attr("href").startsWith("/url?q="));
        links.removeIf(element -> element.toString().startsWith("<a class="));
        links.forEach(element -> {
            if (!allDistinctDomains.contains(getDomainName(element.toString()))) {
                allDistinctDomains.add(getDomainName(element.toString()));
                allReferences.add(remakeElementAsString(element));
            }
        });
        allReferences.forEach(reference -> System.out.println(reference + "\n\n"));
    }

    private String remakeElementAsString(Element element) {
        int indexOfHttp = element.toString().indexOf("http");
        int indexOfEdindg = element.toString().indexOf("&amp");
        String appropriateReference = element.toString().substring(indexOfHttp, indexOfEdindg);
        return appropriateReference;
    }
}
