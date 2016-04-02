package WebProcessor;

import WebProcessor.WebProcessor;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.*;

/**
 * Created by George on 23.03.2016.
 */
public class WebWorker {

    public WebWorker() {
    }

    public void performSearchingWithResults(ArrayList<String> queries, String path) {
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
        try {
            queries.forEach(element -> {
                WebProcessor webProcessor = new WebProcessor(element, path);
                executor.execute(webProcessor);
            });
            executor.shutdown();
            executor.awaitTermination(70, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
