package rise.jj.multiuavcontrol;

import android.annotation.SuppressLint;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.MAVLink.MAVLinkPacket;
import com.MAVLink.Messages.MAVLinkMessage;
import com.MAVLink.Parser;
import com.MAVLink.common.msg_attitude;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.jjoe64.graphview.series.DataPoint;

import org.zeromq.ZMQ;

//GOOGLE MAPS API KEY: AIzaSyChMeAOGZjRfAxEfh1caaTCCrsXdIrgN9k

public class MainActivity extends AppCompatActivity {

    Handler zmqSubcriberHandler;
    Message msg;

    public static final String ARG_SECTION_NUMBER = "section_number_1";


    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        zmqSubcriberHandler = new Handler();
        Log.d("MAINACTIVITY", "Handler: " + zmqSubcriberHandler.toString());

        ControlFragment controlFragment1 = new ControlFragment();
        //ControlFragment controlFragment2 = new ControlFragment();
        //MapFragment mapFragment = new MapFragment();
        PlaceholderFragment placeholderFragment = new PlaceholderFragment();
        final FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragementTransaction = fragmentManager.beginTransaction();
        fragementTransaction.add(R.id.my_layout, controlFragment1, "ControlFragment1");
        //fragementTransaction.add(R.id.my_layout, controlFragment2, "ControlFragment2");
        //fragementTransaction.add(R.id.my_layout, mapFragment, "MapFragment");
        fragementTransaction.add(R.id.my_layout, placeholderFragment, "PlaceholderFragment");
        fragementTransaction.commit();

        new Thread(new ZmqSubscriberTemp()).start();
        zmqSubcriberHandler = new Handler() {
            public void handleMessage(Message msg) {
                Log.d("zmqSubscriberHandler", "Handling message");
                MAVLinkPacket packet = (MAVLinkPacket) msg.obj;
                if (packet == null) return;
                if (packet.msgid != 30) return;
                ControlFragment controlFragment = (ControlFragment) fragmentManager.findFragmentByTag("ControlFragment1");
                MAVLinkMessage MAVmsg = packet.unpack();
                msg_attitude attMsg = (msg_attitude) MAVmsg;
                controlFragment.appendImuData(attMsg.yaw, attMsg.pitch, attMsg.roll);
            }
        };
    }

    public class ZmqSubscriberTemp implements Runnable {

        MAVLinkPacket packet;
        Parser parser;

        public void run() {

            Log.d("ZMQSUBSCRIBER", "Subscriber started");

            ZMQ.Context context = ZMQ.context(1);
            ZMQ.Socket subscriber = context.socket(ZMQ.SUB);
            parser = new Parser();

            Log.d("ZMQSUBSCRIBER", "Attempting to subscribe to publisher");
            subscriber.connect("tcp://192.168.1.15:5563");
            while (!subscriber.connect("tcp://192.168.1.15:5563")) {
                try {
                    this.wait(1000);
                    Log.d("ZMQSUBSCRIBER", "Could not connect to publisher. Will try again");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Log.d("ZMQSUBSCRIBER", "ERROR 1");
                }
            }
            Log.d("ZMQSUBSCRIBER", "Connected to publisher");
            subscriber.subscribe("B".getBytes());
            while (!Thread.currentThread().isInterrupted()) {
                String address = subscriber.recvStr();
                byte[] contents = subscriber.recv();
                if ((packet = parser.mavlink_parse_char(contents[0] & 0xFF)) == null) continue;
                Log.d("ZMQSUBSCRIBER", "Handling message");
                Message msg = Message.obtain();
                msg.obj = packet;
                zmqSubcriberHandler.sendMessage(msg);
            }
            subscriber.close();
            context.term();
        }
    }
}
