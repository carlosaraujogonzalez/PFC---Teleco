package area.guias.pfc;

import android.graphics.Bitmap;

import java.util.ArrayList;

/**
 * Created by adrianbouza on 25/02/14.
 */
public class ActivitiesArray<T> extends ArrayList<T> implements ActivitiesStore {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ArrayList<ActivityMini> list;

    public ActivitiesArray() {
        this.list = new ArrayList<ActivityMini>();
    }

    @Override
    public void saveActivity(ActivityMini activity) {
        this.list.add(activity);
    }

    @Override
    public void deleteList() {
        this.list.clear();
    }

    @Override
    public ArrayList<ActivityMini> getList() {
        return this.list;
    }

    @Override
    public void setImage(int id, Bitmap image) {
        for(ActivityMini mi : this.list){
            if(mi.getId() == id){
                mi.setImage(image);
                break;
            }
        }
    }
    
    @Override
    public void removeActivity(int id){
    	for (int i=0; i<list.size(); i++){
    		if (list.get(i).getId() == id){
    			list.remove(i);
    			break;
    		}
    	}
    }
}
