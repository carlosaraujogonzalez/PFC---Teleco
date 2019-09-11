package area.guias.pfc;

import java.util.ArrayList;

import android.graphics.Bitmap;

public class PersonsArray implements PersonsStore{
	private ArrayList<Person> personsArray;

	
	
	
	
	public PersonsArray(){
		personsArray = new ArrayList<Person>();	
	}
	

	
	

	@Override
	public void setBitmapImage(int id, Bitmap bitmapImage) {
		for (int i=0;i<personsArray.size();i++){
			if (personsArray.get(i).getId() == id){
				personsArray.get(i).setBitmapImage(bitmapImage);
				break;
			}
		}		
		
	}


	
	
	
	@Override
	public void savePerson(Person person) {
		personsArray.add(person);		
	}

	
	
	
	
	@Override
	public void deleteList() {
		personsArray.clear();
	}

	
	
	
	
	@Override
	public ArrayList<Person> personsList() {
		return personsArray;
	}

	
	
	
	
	@Override
	public void deletePerson(int id) {
		for (int i=0; i<personsArray.size();i++){
			if (personsArray.get(i).getId() == id){
				personsArray.remove(i);
			}
		}
	}





	@Override
	public int size() {
		return personsArray.size();
	}





	@Override
	public Person get(int position) {
		return personsArray.get(position);
	}

}
