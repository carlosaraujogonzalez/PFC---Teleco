package area.guias.pfc;

import java.util.ArrayList;

import android.graphics.Bitmap;

public interface ToolsStore {
		
		public Tool get(int position);
		
		public void saveTool(Tool tool);
		
		public int size();
	
		public void deleteList();

	    public ArrayList<Tool> toolsList();
	    
	    public void setBitmapImage(int id, Bitmap bitmapImage);
	    
	    public void deleteTool(int id);
}
