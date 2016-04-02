package WebProcessor;


import org.jsoup.Connection;
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
        System.out.println("Created Pagecollector :" + pageUrl);
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

            //htmlPage = Jsoup.connect(pageUrl)
            //        .userAgent(userAgent).timeout(1000).get();
            Connection connection = Jsoup.connect(pageUrl).userAgent(userAgent).timeout(1500);
            Connection.Response response = connection.execute();
            connection.timeout(2500);
            if(response.statusCode() == 200){
                htmlPage = connection.get();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (htmlPage != null) {
            pagesTexts.add(htmlPage.body().text());
        }
        try {
            savePagesData(htmlPage.body().text(), pageUrl);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void refreshCollectorData() {
        pagesTexts.clear();
    }

    /**
     * saves pages text data in specified directory.
     *
     * @param url is the path in file systems where produced pages will be stored.
     * @throws IOException because file writing there.
     */
    public void savePagesData(String pageText, String url) throws IOException {
        OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(/*path + "/" + url*/i++ + ".txt"));
        writer.write(pageText);
        writer.flush();
        writer.close();
    }

    private int i = 0;
}
