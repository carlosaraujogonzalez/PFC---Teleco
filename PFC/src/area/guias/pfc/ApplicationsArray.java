package area.guias.pfc;

import java.util.ArrayList;

import android.graphics.Bitmap;

public class ApplicationsArray implements ApplicationsStore{
	private ArrayList<Application> aplicacionesArray;

	public ApplicationsArray(){
		aplicacionesArray = new ArrayList<Application>();	
	}
	
	// Esta clase tiene que implementar los métodos de la interfaz.
	// Los métodos de la interfaz vienen precedidos de @Override
	
	@Override
	public void guardarAplicacion(Application aplicacion) {
		aplicacionesArray.add(aplicacion);		
	}

	@Override
	public void borrarLista() {
		aplicacionesArray.clear();
	}

	@Override
	public ArrayList<Application> listaAplicaciones() {
		return aplicacionesArray;
	}

	@Override
	public void setImage(int id, Bitmap image) {
		for (int i=0;i<aplicacionesArray.size();i++){
			if (aplicacionesArray.get(i).id == id){
				aplicacionesArray.get(i).image = image;
				break;
			}
		}		
		
	}

	@Override
	public void borrarAplicacion(int id) {
		for (int i=0; i<aplicacionesArray.size();i++){
			if (aplicacionesArray.get(i).id == id){
				aplicacionesArray.remove(i);
			}
		}
	}

	@Override
	public Application getApplication(int position) {
		return aplicacionesArray.get(position);		
	}


}
