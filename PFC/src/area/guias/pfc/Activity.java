package area.guias.pfc;

import java.util.ArrayList;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;


public class Activity implements Parcelable {
	// VARIABLES
		public int id;
		public String name;
		public String imageUri;
		public String imageUrl;
		public Bitmap bitmapImage;
		public String description;
		public ArrayList<String> keywords;
		public String start;
		public String end;
		public String progress;
		public ArrayList<Box> boxes;
		public ArrayList<Requirement> requirements;
		
		// CONSTRUCTOR
		public Activity(){
			this.id = -1;
			this.name = null;
			this.imageUri = null;
			this.imageUrl = null;
			this.bitmapImage = null;
			this.description = null;
			this.keywords = new ArrayList<String>();
			this.start = null;
			this.end = null;
			this.progress = null;
			this.boxes = new ArrayList<Box>();
			this.requirements = new ArrayList<Requirement>();
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

	    public String getImageUri() {
	        return imageUri;
	    }

	    public void setImageUri(String uri) {
	        this.imageUri = uri;
	    }
	    	
	    public String getImageUrl() {
	        return imageUrl;
	    }

	    public void setImageUrl(String url) {
	        this.imageUrl = url;
	    }
	    
	    public Bitmap getBitmapImage() {
	        return bitmapImage;
	    }

	    public void setBitmapImage(Bitmap bm) {
	        this.bitmapImage = bm;
	    }
	    
	    public String getDescription(){
	    	return description;
	    }

	    public void setDescription(String Description){
	    	this.description = Description;
	    }
	    
	    public ArrayList<String> getKeywords(){
	    	return keywords;
	    }

	    public void setKeywords(String keyword){
	    	this.keywords.add(keyword);
	    }
	    
	    public String getStart(){
	    	return start;
	    }

	    public void setStart(String Start){
	    	this.start = Start;
	    }
	    
	    public String getEnd(){
	    	return this.end;
	    }

	    public void setEnd(String End){
	    	this.end = End;
	    }
	    
	    public String getProgress(){
	    	return this.progress;
	    }

	    public void setProgress(String Progress){
	    	this.progress = Progress;
	    }
	    
	    public ArrayList<Box> getBox(){
	    	return this.boxes;
	    }

	    public void setBox(Box box){
	    	this.boxes.add(box);
	    }
	    
	    public ArrayList<Requirement> getRequirements(){
	    	return this.requirements;
	    }

	    public void setRequirement(Requirement requirement){
	    	this.requirements.add(requirement);
	    }

	    public Activity(Parcel in){
	    	this();
			readFromParcel(in);
		}
	    
	    public static final Parcelable.Creator<Activity> CREATOR = new Parcelable.Creator<Activity>() {
			
	        public Activity createFromParcel(Parcel in) {
	        	return new Activity(in);
	        }

			@Override
			public Activity[] newArray(int size) {
				return new Activity[size];
			}      
	    };
	    
		@Override
		public int describeContents() {
			return 0;
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			dest.writeInt(id);
			dest.writeString(name);
			dest.writeString(imageUri);
			dest.writeString(imageUrl);
			//dest.writeParcelable(bitmapImage, flags);
			dest.writeString(description);
			dest.writeSerializable(keywords);		
			dest.writeString(start);
			dest.writeString(end);
			dest.writeString(progress);
			dest.writeList(boxes);
			dest.writeList(requirements);
		}
		
		
		
		
		// We have to read in the same order that we write
		@SuppressWarnings("unchecked")
		private void readFromParcel(Parcel in) {
			id = in.readInt();
			name = in.readString();
			imageUri = in.readString();
			imageUrl = in.readString();
			//bitmapImage = in.readParcelable(Bitmap.class.getClassLoader());
			description = in.readString();
			keywords = (ArrayList<String>) in.readSerializable();
			start = in.readString();
			end = in.readString();
			progress = in.readString();
			in.readTypedList(boxes, Box.CREATOR);
			in.readTypedList(requirements, Requirement.CREATOR);
	    }
}
