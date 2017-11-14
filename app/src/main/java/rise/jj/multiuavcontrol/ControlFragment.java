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
import android.widget.Button;
import android.widget.TextView;

import com.MAVLink.Messages.MAVLinkMessage;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.zeromq.*;

import com.MAVLink.common.*;
import com.MAVLink.*;
import com.MAVLink.enums.*;

import java.util.Random;

/**
 * Created by yizhu_000 on 11/13/2017.
 */

public class ControlFragment extends Fragment implements View.OnClickListener {

    private final Handler mHandler = new Handler();;
    private Runnable newData;
    private LineGraphSeries<DataPoint> yawSeries;
    private LineGraphSeries<DataPoint> pitchSeries;
    private LineGraphSeries<DataPoint> rollSeries;
    private double graphLastXValue;
    Random mRand;
    View view;
    Button button;
    ZMQ.Socket publisher;
    ZMQ.Context context;
    int armingState;
    TextView armingStateTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Log.d("ControlFragment", "Creating control fragment");

        View view = inflater.inflate(R.layout.fragment_control_layout, container, false);


        armingState = 0;
        final Handler handler = new Handler();
        mRand = new Random(System.currentTimeMillis());
        graphLastXValue = 0d;

        context = ZMQ.context(1);
        publisher = context.socket(ZMQ.PUB);
        publisher.bind("tcp://*:5565");

        GraphView graph = (GraphView) view.findViewById(R.id.graph);
        yawSeries = new LineGraphSeries<>();
        pitchSeries = new LineGraphSeries<>();
        rollSeries = new LineGraphSeries<>();
        graph.addSeries(yawSeries);
        graph.addSeries(pitchSeries);
        graph.addSeries(rollSeries);
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(100);
        graph.getViewport().setMinY(-180);
        graph.getViewport().setMaxY(180);

        button = (Button) view.findViewById(R.id.button_id);
        button.setOnClickListener(this);
        armingStateTextView = (TextView) view.findViewById(R.id.arming_state);

        return view;
    }

    public void appendImuData (double yaw, double pitch, double roll) {
        graphLastXValue += 1d;
        yawSeries.appendData(new DataPoint(graphLastXValue, yaw * 180/(Math.PI)), true, 100);
        pitchSeries.appendData(new DataPoint(graphLastXValue, pitch * 180/(Math.PI)), true, 100);
        rollSeries.appendData(new DataPoint(graphLastXValue, roll * 180/(Math.PI)), true, 100);
    }


    @Override
    public void onClick(View view) {
        Log.d("ARMING", "Started arming process");
        msg_command_long msg = new msg_command_long();
        msg.target_system = 1;
        msg.command = 400;
        if (armingState == 0)  {
            armingState = 1;
            armingStateTextView.setText("Arming status:\nArmed");
            button.setText("DISARM");
        }
        else  {
            armingState = 0;
            armingStateTextView.setText("Arming status:\nDisarmed");
            button.setText("ARM");
        }
        msg.param1 = armingState;
        MAVLinkPacket packet = msg.pack();
        publisher.sendMore("C");
        publisher.send(packet.encodePacket());
        Log.d("ARMING", "Sent arming message");
    }
}
