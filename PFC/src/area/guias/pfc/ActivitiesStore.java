package area.guias.pfc;

import android.graphics.Bitmap;

import java.util.ArrayList;

/**
 * Created by adrianbouza on 25/02/14.
 */
public interface ActivitiesStore {

    // Guardar Actividad
    public void saveActivity(ActivityMini activity);

    // Borrar Lista
    public void deleteList();

    // Obtener lista de actividades
    public ArrayList<ActivityMini> getList();

    // set image
    public void setImage(int id, Bitmap image);
    
    public void removeActivity(int id);
}
