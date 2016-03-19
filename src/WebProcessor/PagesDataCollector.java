package WebProcessor;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by George on 19.03.2016.
 */
public class PagesDataCollector implements Runnable {

    private String pageUrl;

    public PagesDataCollector(String pageUrl) {
        this.pageUrl = pageUrl;
    }

    private static final String userAgent = ConnectionProperties.getUserAgent();

    private static LinkedBlockingQueue<String> pagesTexts = new LinkedBlockingQueue<>();

    public static LinkedBlockingQueue<String> getPages() {
        return pagesTexts;
    }

    @Override
    public void run() {
        Document htmlPage = null;
        try {
            htmlPage = Jsoup.connect(pageUrl)
                    .userAgent(userAgent).timeout(2000).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        pagesTexts.add(htmlPage.body().text());
    }

    public static void refreshCollectorData() {
        pagesTexts.clear();
    }
}
