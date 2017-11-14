package rise.jj.multiuavcontrol;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.Random;

/**
 * Created by yizhu_000 on 11/13/2017.
 */

public class ControlFragment extends Fragment {

    private final Handler mHandler = new Handler();;
    private Runnable newData;
    private LineGraphSeries<DataPoint> mSeries;
    private double graphLastXValue;
    Random mRand;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Log.d("ControlFragment", "Creating control fragment");

        View view = inflater.inflate(R.layout.fragment_control_layout, container, false);

        final Handler handler = new Handler();
        mRand = new Random(System.currentTimeMillis());
        graphLastXValue = 0d;

        GraphView graph = (GraphView) view.findViewById(R.id.graph);
        mSeries = new LineGraphSeries<>();
        graph.addSeries(mSeries);
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(400);
        graph.getViewport().setMinY(-180);
        graph.getViewport().setMaxY(180);


        newData = new Runnable() {
            @Override
            public void run() {
                graphLastXValue += 1d;
                mSeries.appendData(new DataPoint(graphLastXValue, mRand.nextDouble()),true, 400);
                handler.postDelayed(this, 20);
            }
        };
        handler.postDelayed(newData, 1000);

        return view;
    }

    private DataPoint[] generateData() {
        int count = 30;
        DataPoint[] values = new DataPoint[count];
        for (int i = 0; i < count; i++) {
            double x = i;
            double f = mRand.nextDouble() * 0.15 + 0.3;
            double y = Math.sin(i * f + 2) + mRand.nextDouble() * 0.3;
            DataPoint v = new DataPoint(x, y);
            values[i] = v;
        }
        return values;
    }

}
