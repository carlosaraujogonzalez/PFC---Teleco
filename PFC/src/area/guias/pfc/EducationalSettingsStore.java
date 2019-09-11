package area.guias.pfc;

import java.util.ArrayList;

import android.graphics.Bitmap;

public interface EducationalSettingsStore {
		
		public EducationalSetting get(int position);
		
		public void saveEducationalSetting(EducationalSetting educational_setting);
		
		public int size();
	
		public void deleteList();

	    public ArrayList<EducationalSetting> educational_settings_List();
	    
	    public void setBitmapImage(int id, Bitmap bitmapImage);
	    
	    public void deleteEducationalSetting(int id);
}
