package area.guias.pfc;

import java.util.ArrayList;

import android.graphics.Bitmap;

public class GuidesArray implements GuidesStore {
	private ArrayList<Guide> guiasArray;
	
	public GuidesArray(){
		guiasArray = new ArrayList<Guide>();
	}
		
	@Override
	public void guardarGuia(Guide guia) {
		guiasArray.add(guia);
	}

	@Override
	public void borrarLista() {
		guiasArray.clear();
	}
	
	@Override
	public ArrayList<Guide> listaGuias() {
		return guiasArray;
	}

	@Override
	public void setImage(int id, Bitmap imagen) {
		for (int i=0;i<guiasArray.size();i++){
			if (guiasArray.get(i).id == id){
				guiasArray.get(i).header.imageBitmap = imagen;
				break;
			}
		}			
	}

	@Override
	public Guide getGuide(int position) {
		return guiasArray.get(position);
	}

}
