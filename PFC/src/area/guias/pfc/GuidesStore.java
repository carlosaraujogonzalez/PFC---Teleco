package area.guias.pfc;

import java.util.ArrayList;

import android.graphics.Bitmap;

public interface GuidesStore {
	
	public void guardarGuia(Guide guia);
	
	public Guide getGuide(int position);

	public void borrarLista();

    public ArrayList<Guide> listaGuias();

    public void setImage(int id, Bitmap imagen);

}
