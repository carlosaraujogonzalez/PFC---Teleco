package area.guias.pfc;

import java.util.ArrayList;
import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

public class ActivitySequence implements Parcelable{
	// VARIABLES
		public int id;
		public String title;
		public String description;
		public String imagePath;
		public String image_Url;
		public String image_Uri;
		public Bitmap imageBitmap;
		public ArrayList<Integer> activitiesIds;
		
		// CONSTRUCTOR
		public ActivitySequence(){
			this.id = -1;
			this.title = "Título";
			this.description = "Descripción";
			this.imagePath = null;
			this.image_Url = null;
			this.image_Uri = null;
			this.imageBitmap = null;
			this.activitiesIds = new ArrayList<Integer>();
		}

		public ActivitySequence(Parcel in){
			readFromParcel(in);
		}
		
		public static final Parcelable.Creator<ActivitySequence> CREATOR = new Parcelable.Creator<ActivitySequence>() {
			
	        public ActivitySequence createFromParcel(Parcel in) {
	        	return new ActivitySequence(in);
	        }

			@Override
			public ActivitySequence[] newArray(int size) {
				return new ActivitySequence[size];
			}      
	    };
	    
		@Override
		public int describeContents() {
			return 0;
		}

		@Override
		public void writeToParcel(Parcel dest, int arg1) {
			dest.writeInt(id);
			dest.writeString(title);
			dest.writeString(description);
			dest.writeString(imagePath);
			dest.writeString(image_Url);
			dest.writeString(image_Uri);
			dest.writeSerializable(activitiesIds);	
			//dest.writeParcelable(imageBitmap, arg1);
		}
		
		@SuppressWarnings("unchecked")
		private void readFromParcel(Parcel in) {
			id = in.readInt();
			title = in.readString();
			description = in.readString();
			imagePath = in.readString();
			image_Url = in.readString();
			image_Uri = in.readString();
			activitiesIds = (ArrayList<Integer>) in.readSerializable();
			//imageBitmap = in.readParcelable(Bitmap.class.getClassLoader());
		}

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public String getImage_Url() {
			return image_Url;
		}

		public void setImage_Url(String image_Url) {
			this.image_Url = image_Url;
		}

		public String getImage_Uri() {
			return image_Uri;
		}

		public void setImage_Uri(String image_Uri) {
			this.image_Uri = image_Uri;
		}

		public Bitmap getImageBitmap() {
			return imageBitmap;
		}

		public void setImageBitmap(Bitmap imageBitmap) {
			this.imageBitmap = imageBitmap;
		}

		public ArrayList<Integer> getActivities() {
			return activitiesIds;
		}

		public void setActivities(ArrayList<Integer> activitiesIds) {
			this.activitiesIds = activitiesIds;
		}

		public static Parcelable.Creator<ActivitySequence> getCreator() {
			return CREATOR;
		}
		
		
}
