package no.uio.cesar.Utils;

import com.jjoe64.graphview.GraphView;

public class Graph {

    private final int MAX_Y = 2500;
    private final int MIN_Y = 1500;


    public static void changeParams(GraphView graph) {
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(1700);
        graph.getViewport().setMaxY(2000);

        graph.getViewport().setXAxisBoundsManual(true);

        graph.getViewport().setScalable(true);
        graph.getViewport().setScalableY(true);

        graph.getGridLabelRenderer().setHumanRounding(true);
    }
}
