package area.guias.pfc;

import java.util.ArrayList;

import android.graphics.Bitmap;

public interface TripsStore {
		
		public Trip get(int position);
		
		public void saveTrip(Trip tool);
		
		public int size();
	
		public void deleteList();

	    public ArrayList<Trip> tripsList();
	    
	    public void setBitmapImage(int id, Bitmap bitmapImage);
	    
	    public void deleteTrip(int id);
}
