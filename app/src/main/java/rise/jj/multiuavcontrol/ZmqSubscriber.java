package rise.jj.multiuavcontrol;

/**
 * Created by yizhu_000 on 11/13/2017.
 */

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.MAVLink.MAVLinkPacket;
import com.MAVLink.Parser;

import org.zeromq.*;

public class ZmqSubscriber implements Runnable {

    MAVLinkPacket packet;
    Parser parser;
    Handler handler;

    public ZmqSubscriber(Handler handler) {
        this.handler = handler;
        Log.d("ZMQSUBSCRIBER", "Handler: " + handler.toString());
    }

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
            int thing = contents[0];
            if ((packet = parser.mavlink_parse_char(thing)) == null) continue;
            Log.d("ZMQSUBSCRIBER", "Handling message");
            Message msg = Message.obtain();
            msg.obj = packet;
            handler.sendMessage(msg);
        }
        subscriber.close();
        context.term();
    }
}
