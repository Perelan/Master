package no.uio.cesar.Utils;

import com.jjoe64.graphview.GraphView;

public class Graph {

    private final static int MAX_Y = 2500;
    private final static int MIN_Y = 1500;


    public static void changeParams(GraphView graph) {
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(MIN_Y);
        graph.getViewport().setMaxY(MAX_Y);

        graph.getViewport().setXAxisBoundsManual(true);

        graph.getViewport().setScalable(true);
        graph.getViewport().setScalableY(true);

        graph.getGridLabelRenderer().setHumanRounding(true);
    }
}
