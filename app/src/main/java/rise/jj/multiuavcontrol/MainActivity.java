package rise.jj.multiuavcontrol;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

public class MainActivity extends AppCompatActivity {

    public static final String ARG_SECTION_NUMBER = "section_number_1";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //GraphFragment graphFragment1 = new GraphFragment();
        //GraphFragment graphFragment2 = new GraphFragment();
        ControlFragment controlFragment = new ControlFragment();
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragementTransaction = fragmentManager.beginTransaction();
        fragementTransaction.add(R.id.my_layout, controlFragment, "ControlFragment");
        //fragementTransaction.add(R.id.my_layout, graphFragment1, "GraphFragment1");
        //fragementTransaction.add(R.id.my_layout, graphFragment2, "GraphFragment2");
        fragementTransaction.commit();
    }

    public void onSectionAttached(int number) {

    }
}
