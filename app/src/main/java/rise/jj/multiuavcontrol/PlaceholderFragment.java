package rise.jj.multiuavcontrol;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by yizhu_000 on 11/14/2017.
 */

public class PlaceholderFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Log.d("ControlFragment", "Creating control fragment");

        View view = inflater.inflate(R.layout.fragment_placeholder_layout, container, false);

        return view;
    }
}
