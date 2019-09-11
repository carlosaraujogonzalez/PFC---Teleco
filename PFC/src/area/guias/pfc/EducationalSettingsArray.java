package area.guias.pfc;

import java.util.ArrayList;

import android.graphics.Bitmap;

public class EducationalSettingsArray implements EducationalSettingsStore{
	private ArrayList<EducationalSetting> educational_settings_array;

	
	
	
	
	public EducationalSettingsArray(){
		educational_settings_array = new ArrayList<EducationalSetting>();	
	}
	

	
	

	@Override
	public void setBitmapImage(int id, Bitmap bitmapImage) {
		for (int i=0;i<educational_settings_array.size();i++){
			if (educational_settings_array.get(i).getId() == id){
				educational_settings_array.get(i).setBitmapImage(bitmapImage);
				break;
			}
		}		
		
	}


	
	
	
	@Override
	public void saveEducationalSetting(EducationalSetting educational_setting) {
		educational_settings_array.add(educational_setting);		
	}

	
	
	
	
	@Override
	public void deleteList() {
		educational_settings_array.clear();
	}

	
	
	
	
	@Override
	public ArrayList<EducationalSetting> educational_settings_List() {
		return educational_settings_array;
	}

	
	
	
	
	@Override
	public void deleteEducationalSetting(int id) {
		for (int i=0; i<educational_settings_array.size();i++){
			if (educational_settings_array.get(i).getId() == id){
				educational_settings_array.remove(i);
			}
		}
	}





	@Override
	public int size() {
		return educational_settings_array.size();
	}





	@Override
	public EducationalSetting get(int position) {
		return educational_settings_array.get(position);
	}

}
