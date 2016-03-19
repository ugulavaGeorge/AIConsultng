package WebProcessor;

/**
 * Created by George on 19.03.2016.
 */
public class ConnectionProperties {

    private SearchEngine engine;
    private String query;
    private static final String UserAgent = "Mozilla/5.0 (Windows NT 6.1; WOW64) ";

    public ConnectionProperties(SearchEngine engine, String query) {
        this.engine = engine;
        this.query = query;
    }

    public ConnectionProperties() {
    }

    public SearchEngine getEngine() {
        return engine;
    }

    public String getQuery() {
        return query;
    }

    public static String getUserAgent() {
        return UserAgent;
    }

    public void setEngine(SearchEngine engine) {
        this.engine = engine;
    }

    public void setQuery(String query) {
        this.query = query;
    }
}
