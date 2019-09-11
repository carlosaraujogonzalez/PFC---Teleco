package area.guias.pfc;

import java.util.ArrayList;

import android.graphics.Bitmap;

public interface ApplicationsStore {
	
	public void guardarAplicacion(Application aplicacion);
		
	public void borrarLista();

	public ArrayList<Application> listaAplicaciones();
	
	public void setImage(int id, Bitmap imagen);
		   
	public void borrarAplicacion(int id);
	
	public Application getApplication(int position);
			
}
