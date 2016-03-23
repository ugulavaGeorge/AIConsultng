package WebProcessor;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by George on 19.03.2016.
 */
public class PagesDataCollector implements Runnable {

    private String pageUrl;
    private String path;

    public PagesDataCollector(String pageUrl, String path) {
        this.pageUrl = pageUrl;
        this.path = path;
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
                    .userAgent(userAgent).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        pagesTexts.add(htmlPage.body().text());
    }

    public static void refreshCollectorData() {
        pagesTexts.clear();
    }

    /**
     * saves pages text data in specified directory.
     * @param url is the path in file systems where produced pages will be stored.
     * @throws IOException because file writing there.
     */
    public void savePagesData(String pageText, String url) throws IOException {
        OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(path + "/" + url + ".txt"));
        writer.write(pageText);
        writer.flush();
        writer.close();
    }

}
