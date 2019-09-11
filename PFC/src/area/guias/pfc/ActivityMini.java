package area.guias.pfc;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by adrianbouza on 25/02/14.
 */
public class ActivityMini implements Parcelable {

    private int id;
    private String name;
    private String element_image_file_name;
    private Bitmap image;
    private String short_description;
    private String description;
    private boolean checked;
    private String checkedString;

    public ActivityMini(int id, String name, String element_image_file_name, Bitmap image, String short_description) {
        this.id = id;
        this.name = name;
        this.element_image_file_name = element_image_file_name;
        this.image = image;
        this.short_description = short_description;
    }

    public ActivityMini() {
        this.id = -1;
        this.name = null;
        this.element_image_file_name = null;
        this.image = null;
        this.short_description = null;
        this.description = null;
        this.checked = false;
        this.checkedString = "Añadir";
    }


    public ActivityMini(Parcel in){
		readFromParcel(in);
	}
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getElement_image_file_name() {
        return element_image_file_name;
    }

    public void setElement_image_file_name(String element_image_file_name) {
        this.element_image_file_name = element_image_file_name;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }
    
    public String getShortDescription(){
    	return short_description;
    }

    public void setShortDescription(String str){
    	if (str.length() >= 100) this.short_description = str.substring(0,99)+"...";
    	else this.short_description = str;
    }
    
    public String getDescription(){
    	return description;
    }

    public void setDescription(String Description){
    	this.description = Description;
    }

    public boolean getChecked(){
    	return checked;
    }

    public void setChecked(boolean value){
    	this.checked = value;
    }
    
    public String getCheckedString(){
    	return checkedString;
    }
    
    public void setCheckedString(String str){
    	this.checkedString = str;
    }
    
    
    
    
    @Override
    public String toString() {
        return "MiniActivity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", element_image_file_name='" + element_image_file_name + '\'' +
                '}';
    }

    
    
    
    @Override
    public int describeContents() {
        return 0;
    }

    
    
    
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(element_image_file_name);
        dest.writeString(short_description);
    }

    
    private void readFromParcel(Parcel in) {
		id = in.readInt();
		name = in.readString();
		element_image_file_name = in.readString();
		short_description = in.readString();
    }
    
    
    
    public static final Parcelable.Creator<ActivityMini> CREATOR = new Creator<ActivityMini>() {
        @Override
        public ActivityMini createFromParcel(Parcel source) {
            return new ActivityMini(source);
        }

        @Override
        public ActivityMini[] newArray(int size) {
            return new ActivityMini[size];
        }
    };
}
