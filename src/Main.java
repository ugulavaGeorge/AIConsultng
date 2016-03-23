import WebProcessor.WebProcessor;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by George on 23.03.2016.
 */
public class Main {
    public static void main(String[] args) {
        String path = "C:/MyProjects/project/AIConsultng.git/src/outputData";
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
        for (int i = 0; i < args.length; i++) {
            WebProcessor webProcessor = new WebProcessor(args[i], path);
            executor.execute(webProcessor);
        }
        executor.shutdown();
    }
}
