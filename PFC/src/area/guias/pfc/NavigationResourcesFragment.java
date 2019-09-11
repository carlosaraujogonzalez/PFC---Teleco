package area.guias.pfc;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

public class NavigationResourcesFragment extends Fragment {
	private View rootView;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		if (rootView != null) {
	        ViewGroup parent = (ViewGroup) rootView.getParent();
	        if (parent != null) parent.removeView(rootView);
	    }
	    try {
	        rootView = inflater.inflate(R.layout.resources_layout, container, false);
	    } catch (InflateException e) {
	    	
	    }	
		return rootView;
	}
	
	
	
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Configuration configuration = getResources().getConfiguration();
	    onConfigurationChanged(configuration);
	}

	
	@SuppressLint("NewApi")
	@Override
    public void onConfigurationChanged(Configuration newConfig) {
		Log.d("NavigationDrawer", "onConfigurationChanged");
        super.onConfigurationChanged(newConfig);          
        
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
        	RelativeLayout rl = (RelativeLayout) rootView.findViewById(R.id.resources_relative_layout);
        	rl.setBackground(getResources().getDrawable(R.drawable.fondo_recursos));                                     
        } 
        else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
        	RelativeLayout rl = (RelativeLayout) rootView.findViewById(R.id.resources_relative_layout);
        	rl.setBackground(getResources().getDrawable(R.drawable.fondo_land_recursos));
        }
	}       
}
