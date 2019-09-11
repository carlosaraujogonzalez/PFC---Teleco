package area.guias.pfc;

import java.util.ArrayList;

import android.graphics.Bitmap;

public interface PersonsStore {
		
		public Person get(int position);
		
		public void savePerson(Person person);
		
		public int size();
	
		public void deleteList();

	    public ArrayList<Person> personsList();
	    
	    public void setBitmapImage(int id, Bitmap bitmapImage);
	    
	    public void deletePerson(int id);
}
