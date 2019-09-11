package area.guias.pfc;

import java.util.ArrayList;

import android.graphics.Bitmap;

public class TechnicalSettingsArray implements TechnicalSettingsStore{
	private ArrayList<TechnicalSetting> technical_settings_array;

	
	
	
	
	public TechnicalSettingsArray(){
		technical_settings_array = new ArrayList<TechnicalSetting>();	
	}
	

	
	

	@Override
	public void setBitmapImage(int id, Bitmap bitmapImage) {
		for (int i=0;i<technical_settings_array.size();i++){
			if (technical_settings_array.get(i).getId() == id){
				technical_settings_array.get(i).setBitmapImage(bitmapImage);
				break;
			}
		}		
		
	}


	
	
	
	@Override
	public void saveTechnicalSetting(TechnicalSetting technical_setting) {
		technical_settings_array.add(technical_setting);		
	}

	
	
	
	
	@Override
	public void deleteList() {
		technical_settings_array.clear();
	}

	
	
	
	
	@Override
	public ArrayList<TechnicalSetting> technical_settings_List() {
		return technical_settings_array;
	}

	
	
	
	
	@Override
	public void deleteTechnicalSetting(int id) {
		for (int i=0; i<technical_settings_array.size();i++){
			if (technical_settings_array.get(i).getId() == id){
				technical_settings_array.remove(i);
			}
		}
	}





	@Override
	public int size() {
		return technical_settings_array.size();
	}





	@Override
	public TechnicalSetting get(int position) {
		return technical_settings_array.get(position);
	}

}
