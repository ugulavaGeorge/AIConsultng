package WebProcessor;

/**
 * Created by George on 18.03.2016.
 */
public enum SearchEngine {
    google(1),
    yandex(2),
    bing(3),
    aport(4),
    duckduckgo(5),
    mail(6);

    private String engine;

    SearchEngine(int engine){
        switch (engine){
            case 1 : this.engine = "https://google.com/search?q=";
                     break;
            case 2 : this.engine = "https://yandex.ru/search/?text=";
                     break;
            case 3 : this.engine = "https://bing.com/search?q=";
                     break;
            case 4 : this.engine = "www.aport.ru/search/?q=";
                     break;
            case 5 : this.engine = "https://duckduckgo.com/?q=";
                     break;
            case 6 : this.engine = "go.mail.ru/search?q=";
                     break;
            default: throw new IllegalArgumentException("unsupported engine");
        }
    }
}
