package WebProcessor;

/**
 * Created by George on 18.03.2016.
 */
public class SearchEngine {

    private String engine;

    private static int numberOfSearchEngines;

    SearchEngine(int engine){
        switch (engine){
            case 0 : this.engine = "https://google.com/search?q=";
                     break;
            case 1 : this.engine = "https://yandex.ru/search/?text=";
                     break;
            case 2 : this.engine = "https://bing.com/search?q=";
                     break;
            case 3 : this.engine = "https://duckduckgo.com/html/search/?q=";
                     break;
            case 4 : this.engine = "https://go.mail.ru/search?q=";
                     break;
            default: throw new IllegalArgumentException("unsupported engine");
        }
    }

    public String getEngine() {
        return engine;
    }

    public static int getNumberOfSearchEngines() {
        return numberOfSearchEngines;
    }

    public static void setNumberOfSearchEngines(int numberOfSearchEngines) {
        SearchEngine.numberOfSearchEngines = numberOfSearchEngines;
    }
}
