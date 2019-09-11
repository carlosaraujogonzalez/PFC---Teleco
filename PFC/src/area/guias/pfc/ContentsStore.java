package area.guias.pfc;

import java.util.ArrayList;

import android.graphics.Bitmap;

public interface ContentsStore {
		
		public Content get(int position);
		
		public void saveContent(Content content);
		
		public int size();
	
		public void deleteList();

	    public ArrayList<Content> contentsList();
	    
	    public void setBitmapImage(int id, Bitmap bitmapImage);
	    
	    public void deleteContent(int id);
}
