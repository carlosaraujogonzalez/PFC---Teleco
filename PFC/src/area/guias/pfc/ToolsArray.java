package area.guias.pfc;

import java.util.ArrayList;

import android.graphics.Bitmap;

public class ToolsArray implements ToolsStore{
	private ArrayList<Tool> toolsArray;

	
	
	
	
	public ToolsArray(){
		toolsArray = new ArrayList<Tool>();	
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
	public void saveTool(Tool tool) {
		toolsArray.add(tool);		
	}

	
	
	
	
	@Override
	public void deleteList() {
		toolsArray.clear();
	}

	
	
	
	
	@Override
	public ArrayList<Tool> toolsList() {
		return toolsArray;
	}

	
	
	
	
	@Override
	public void deleteTool(int id) {
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
	public Tool get(int position) {
		return toolsArray.get(position);
	}

}
