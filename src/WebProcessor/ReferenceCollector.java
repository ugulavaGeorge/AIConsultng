package WebProcessor; /**
 * Created by George on 18.03.2016.
 */

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
            htmlPage = Jsoup
                    .connect(this.connectionProperties.getEngine() + this.connectionProperties.getQuery())
                    .userAgent(ConnectionProperties.getUserAgent())
                    .timeout(2000).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Elements links = htmlPage.select("a[href]");
        links.removeIf(element -> !element.attr("href").startsWith("/url?q="));
        links.removeIf(element -> element.toString().startsWith("<a class="));
        links.forEach(element -> {
            if (!allDistinctDomains.contains(getDomainName(element.toString()))) {
                allDistinctDomains.add(getDomainName(element.toString()));
                allReferences.add(remakeElementAsString(element));
            }
        });
        //allReferences.forEach(reference -> System.out.println(reference + "\n\n"));
    }

    private String remakeElementAsString(Element element) {
        int indexOfHttp = element.toString().indexOf("http");
        int indexOfEnding = element.toString().indexOf("&amp");
        return element.toString().substring(indexOfHttp, indexOfEnding);
    }

    public static void refreshCollectorData(){
        allReferences.clear();
        allDistinctDomains.clear();
    }
}
