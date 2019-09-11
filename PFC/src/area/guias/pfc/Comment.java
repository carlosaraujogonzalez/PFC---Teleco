package area.guias.pfc;

import android.os.Parcel;
import android.os.Parcelable;

public class Comment implements Parcelable{
		// VARIABLES
		public String user;
		public String comment;
		public String year;
		public String month;
		public String day;
		public String hour;
		
		// CONSTRUCTOR
		public Comment(){
			this.user = null;
			this.comment = null;
			this.year = null;
			this.month = null;
			this.day = null;
			this.hour = null;
		}
		
	    public String getUser() {
	        return this.user;
	    }

	    public void setUser(String name) {
	        this.user = name;
	    }

	    public String getComment() {
	        return this.comment;
	    }

	    public void setComment(String comment) {
	        this.comment = comment;
	    }

	    public Comment(Parcel in){
			readFromParcel(in);
		}
	    
	    public static final Parcelable.Creator<Comment> CREATOR = new Parcelable.Creator<Comment>() {
			
	        public Comment createFromParcel(Parcel in) {
	        	return new Comment(in);
	        }

			@Override
			public Comment[] newArray(int size) {
				return new Comment[size];
			}      
	    };
	    
		@Override
		public int describeContents() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			// TODO Auto-generated method stub
			dest.writeString(user);
			dest.writeString(comment);
			dest.writeString(year);
			dest.writeString(month);
			dest.writeString(day);
			dest.writeString(hour);
		}
	    	    
		private void readFromParcel(Parcel in) {
			user = in.readString();
			comment = in.readString();
			year = in.readString();
			month = in.readString();
			day = in.readString();
			hour = in.readString();
	    }

		public String getYear() {
			return year;
		}

		public void setYear(String year) {
			this.year = year;
		}

		public String getMonth() {
			return month;
		}

		public void setMonth(String month) {
			this.month = month;
		}

		public String getDay() {
			return day;
		}

		public void setDay(String day) {
			this.day = day;
		}

		public String getHour() {
			return hour;
		}

		public void setHour(String hour) {
			this.hour = hour;
		}

		public static Parcelable.Creator<Comment> getCreator() {
			return CREATOR;
		}
		
		
}
