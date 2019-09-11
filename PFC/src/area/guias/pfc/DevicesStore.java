package area.guias.pfc;

import java.util.ArrayList;

import android.graphics.Bitmap;

public interface DevicesStore {

		public void guardarDispositivo(Device dispositivo);
	
		public void borrarLista();

	    public ArrayList<Device> listaDispositivos();
	    
	    public void setImage(int id, Bitmap imagen);
	    
	    public void borrarDispositivo(int id);
	    
	    public Device getDevice(int position);
	    
}
