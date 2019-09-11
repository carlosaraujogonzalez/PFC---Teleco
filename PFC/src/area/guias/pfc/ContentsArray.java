package area.guias.pfc;

import java.util.ArrayList;

import android.graphics.Bitmap;

public class ContentsArray implements ContentsStore{
	private ArrayList<Content> contentsArray;

	
	
	
	
	public ContentsArray(){
		contentsArray = new ArrayList<Content>();	
	}
	

	
	

	@Override
	public void setBitmapImage(int id, Bitmap bitmapImage) {
		for (int i=0;i<contentsArray.size();i++){
			if (contentsArray.get(i).getId() == id){
				contentsArray.get(i).setBitmapImage(bitmapImage);
				break;
			}
		}		
		
	}


	
	
	
	@Override
	public void saveContent(Content content) {
		contentsArray.add(content);		
	}

	
	
	
	
	@Override
	public void deleteList() {
		contentsArray.clear();
	}

	
	
	
	
	@Override
	public ArrayList<Content> contentsList() {
		return contentsArray;
	}

	
	
	
	
	@Override
	public void deleteContent(int id) {
		for (int i=0; i<contentsArray.size();i++){
			if (contentsArray.get(i).getId() == id){
				contentsArray.remove(i);
			}
		}
	}





	@Override
	public int size() {
		return contentsArray.size();
	}





	@Override
	public Content get(int position) {
		return contentsArray.get(position);
	}

}
