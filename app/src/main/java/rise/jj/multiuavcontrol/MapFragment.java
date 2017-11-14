package rise.jj.multiuavcontrol;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;

/**
 * Created by yizhu_000 on 11/13/2017.
 */

public class MapFragment extends Fragment implements OnMapReadyCallback{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Log.d("MapFragment", "Creating map fragment");

        View view = inflater.inflate(R.layout.fragment_map_layout, container, false);

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

    }
}
