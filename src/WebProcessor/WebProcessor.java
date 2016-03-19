package WebProcessor;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by George on 19.03.2016.
 */
public class WebProcessor {

    public WebProcessor(String query) {
        this.query = query;
    }

    private String query;
    private LinkedBlockingQueue<String> references;

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
            executor.execute(referenceCollector);
        }
        executor.shutdown();
        references = ReferenceCollector.getAllReferences();
        ReferenceCollector.refreshLinksData();
    }

    public void collectTextPages(){

    }
}
