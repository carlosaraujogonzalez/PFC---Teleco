package area.guias.pfc;

import java.util.ArrayList;

import android.graphics.Bitmap;

public class DevicesArray implements DevicesStore{
	private ArrayList<Device> dispositivosArray;
	
	public DevicesArray(){
		dispositivosArray = new ArrayList<Device>();	
	}
	
	@Override
	public void guardarDispositivo(Device dispositivo) {
		dispositivosArray.add(dispositivo);		
	}

	@Override
	public void borrarLista() {
		dispositivosArray.clear();
	}

	@Override
	public ArrayList<Device> listaDispositivos() {
		return dispositivosArray;
	}

	@Override
	public void setImage(int id, Bitmap image) {
		for (int i=0;i<dispositivosArray.size();i++){
			if (dispositivosArray.get(i).id == id){
				dispositivosArray.get(i).image = image;
				break;
			}
		}		
		
	}

	@Override
	public void borrarDispositivo(int id) {
		for (int i=0; i<dispositivosArray.size();i++){
			if (dispositivosArray.get(i).id == id){
				dispositivosArray.remove(i);
			}
		}
		
	}

	@Override
	public Device getDevice(int position) {
		return dispositivosArray.get(position);		
	}

}
