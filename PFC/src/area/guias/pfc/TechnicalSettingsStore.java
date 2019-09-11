package area.guias.pfc;

import java.util.ArrayList;

import android.graphics.Bitmap;

public interface TechnicalSettingsStore {
		
		public TechnicalSetting get(int position);
		
		public void saveTechnicalSetting(TechnicalSetting technical_setting);
		
		public int size();
	
		public void deleteList();

	    public ArrayList<TechnicalSetting> technical_settings_List();
	    
	    public void setBitmapImage(int id, Bitmap bitmapImage);
	    
	    public void deleteTechnicalSetting(int id);
}
