package WebProcessor;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by George on 19.03.2016.
 */
public class WebProcessor implements Runnable {

    public WebProcessor(String query, String path) {
        this.query = query;
        this.path = path;
    }

    private static int attemptNumber = 0;

    private String query;
    private String path;
    private LinkedBlockingQueue<String> references = new LinkedBlockingQueue<>();

    public void setQuery(String query) {
        this.query = query;
    }

    public String getQuery() {
        return query;
    }


    public void collectReferences() {
        SearchEngine.setNumberOfSearchEngines(6);
        int n = SearchEngine.getNumberOfSearchEngines();
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
        for (int i = 0; i < n; i++) {
            ReferenceCollector referenceCollector = new ReferenceCollector
                    (new ConnectionProperties(new SearchEngine(i), getQuery()));
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            executor.execute(referenceCollector);
        }
        executor.shutdown();
        try {
            executor.awaitTermination(30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        references.addAll(ReferenceCollector.getAllReferences());
        saveReferencesInfile(ReferenceCollector.getAllReferences());
        ReferenceCollector.refreshCollectorData();
    }

    public void collectPagesData() {
        System.out.println("Collecting pages data...");
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
        try {
            references.forEach(element -> {
                PagesDataCollector collector = new PagesDataCollector(element, path);
                executor.execute(collector);
            });
            executor.awaitTermination(30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        PagesDataCollector.refreshCollectorData();
    }

    public void saveReferencesInfile(LinkedBlockingQueue<String> links) {
        try {
            OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(attemptNumber + ".txt"));
            for (String link : links) {
                writer.write(link);
                writer.write("\n");
                writer.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void run() {
        try {
            collectReferences();
            Thread.sleep(3000);
            collectPagesData();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
