import WebProcessor.WebWorker;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by George on 24.03.2016.
 */
public class Main {
    public static void main(String[] args) {
        ArrayList<String> queries = new ArrayList<>();
        queries.add("студенты");
        queries.add("приёмная комиссия 2016");
        queries.add("Лебедев Андрей");
        String path = "C:/MyProjects/project/AIConsultng.git/src/outputData";
        WebWorker worker = new WebWorker();
        worker.performSearchingWithResults(queries,path);
    }
}
