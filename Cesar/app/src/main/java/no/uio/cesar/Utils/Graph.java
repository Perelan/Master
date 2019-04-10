package no.uio.cesar.Utils;

import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Graph {

    private final static int MAX_Y = 2500;
    private final static int MIN_Y = 1500;

    private static SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());

    public static void changeParams(GraphView graph) {

        graph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
            @Override
            public String formatLabel(double value, boolean isValueX) {

                if (isValueX) {
                    return sdf.format(new Date((long) value));
                } else {
                    return super.formatLabel(value, isValueX);
                }
            }
        });

        graph.getGridLabelRenderer().setNumHorizontalLabels(4); // only 4 because of the space

        /*
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(MIN_Y);
        graph.getViewport().setMaxY(MAX_Y);

        graph.getViewport().setXAxisBoundsManual(true);*/

        graph.getGridLabelRenderer().setLabelVerticalWidth(100);

        graph.getViewport().setScalable(true);
        graph.getViewport().setScalableY(true);

        graph.getGridLabelRenderer().setHumanRounding(true);
    }
}
