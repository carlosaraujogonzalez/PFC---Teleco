package area.guias.pfc;

import java.util.ArrayList;

import android.os.Parcel;
import android.os.Parcelable;


public class Requirement  implements Parcelable{
	// VARIABLES
		public ArrayList<Tool> tools;
		public ArrayList<Content> contents;
		public ArrayList<Trip> events;
		public ArrayList<Person> colaborators;
		
		// CONSTRUCTOR
		public Requirement(){
			this.tools = new ArrayList<Tool>();
			this.contents = new ArrayList<Content>();
			this.events = new ArrayList<Trip>();
			this.colaborators = new ArrayList<Person>();
		}
		
		public Requirement(Parcel in){
	    	this();
			readFromParcel(in);
		}
	    
	    public static final Parcelable.Creator<Requirement> CREATOR = new Parcelable.Creator<Requirement>() {
			
	        public Requirement createFromParcel(Parcel in) {
	        	return new Requirement(in);
	        }

			@Override
			public Requirement[] newArray(int size) {
				return new Requirement[size];
			}      
	    };
	    
		@Override
		public int describeContents() {
			return 0;
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			dest.writeList(tools);
			dest.writeList(contents);
			dest.writeList(events);
			dest.writeList(colaborators);
		}
		
		
		
		
		// We have to read in the same order that we write
		private void readFromParcel(Parcel in) {
			in.readTypedList(tools, Tool.CREATOR);
			in.readTypedList(contents, Content.CREATOR);
			in.readTypedList(events, Trip.CREATOR);
			in.readTypedList(colaborators, Person.CREATOR);
	    }
		
	    public ArrayList<Tool> getTools() {
	        return this.tools;
	    }

	    public void setTool(Tool tool) {
	        this.tools.add(tool);
	    }

	    public ArrayList<Content> getContents() {
	        return this.contents;
	    }

	    public void setContent(Content content) {
	        this.contents.add(content);
	    }

	    public ArrayList<Trip> getEvents() {
	        return this.events;
	    }

	    public void setEvent(Trip event) {
	        this.events.add(event);
	    }
	    
	    public ArrayList<Person> getColaborators() {
	        return this.colaborators;
	    }

	    public void setColaborator(Person colaborator) {
	        this.colaborators.add(colaborator);
	    }
	    
}
