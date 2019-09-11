package area.guias.pfc;

import java.util.ArrayList;

import android.graphics.Bitmap;

public class TripsArray implements TripsStore{
	private ArrayList<Trip> toolsArray;

	
	
	
	
	public TripsArray(){
		toolsArray = new ArrayList<Trip>();	
	}
	

	
	

	@Override
	public void setBitmapImage(int id, Bitmap bitmapImage) {
		for (int i=0;i<toolsArray.size();i++){
			if (toolsArray.get(i).getId() == id){
				toolsArray.get(i).setBitmapImage(bitmapImage);
				break;
			}
		}		
		
	}


	
	
	
	@Override
	public void saveTrip(Trip tool) {
		toolsArray.add(tool);		
	}

	
	
	
	
	@Override
	public void deleteList() {
		toolsArray.clear();
	}

	
	
	
	
	@Override
	public ArrayList<Trip> tripsList() {
		return toolsArray;
	}

	
	
	
	
	@Override
	public void deleteTrip(int id) {
		for (int i=0; i<toolsArray.size();i++){
			if (toolsArray.get(i).getId() == id){
				toolsArray.remove(i);
			}
		}
	}





	@Override
	public int size() {
		return toolsArray.size();
	}





	@Override
	public Trip get(int position) {
		return toolsArray.get(position);
	}

}
