package area.guias.pfc;

import android.os.Parcel;
import android.os.Parcelable;


public class Scroll implements Parcelable{
	static final int PAGE_ELEMENTS = 15;
	static final int METHOD_SEARCH = 1;
	static final int METHOD_GET = 2;
	static final int METHOD_NONE = 0;
	int page;
	int scrollTotalCount;
	int method;
	String search;
	
	public Scroll(){
		this.page=1;
		this.scrollTotalCount=PAGE_ELEMENTS;
		this.method = 0;
		this.search = null;
	}
	
	public Scroll(Parcel in){
		readFromParcel(in);
	}	
	
	public static final Parcelable.Creator<Scroll> CREATOR = new Parcelable.Creator<Scroll>() {
		
        public Scroll createFromParcel(Parcel in) {
        	return new Scroll(in);
        }

		@Override
		public Scroll[] newArray(int size) {
			return new Scroll[size];
		}      
    };
    
    
    
    
	@Override
	public int describeContents() {
		return 0;
	}

	
	
	
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// GENERAL
		dest.writeInt(page);
		dest.writeInt(scrollTotalCount);
		dest.writeInt(method);
		dest.writeString(search);
	}
	
	

	
	
	private void readFromParcel(Parcel in) {
		 // GENERAL
		 page = in.readInt();
		 scrollTotalCount = in.readInt();
		 method = in.readInt();
		 search = in.readString();
    }
	
	public int getPage(){
		return this.page;
	}
	
	public void setPage(int num){
		this.page = num;
	}
	
	public int getTotalCount(){
		return this.scrollTotalCount;
	}
	
	public void setTotalCount(int num){
		this.scrollTotalCount = num;
	}
	
	public int getPageElements(){
		return PAGE_ELEMENTS;
	}

	public int getScrollTotalCount() {
		return scrollTotalCount;
	}

	public void setScrollTotalCount(int scrollTotalCount) {
		this.scrollTotalCount = scrollTotalCount;
	}

	public int getMethod() {
		return method;
	}

	public void setMethod(int method) {
		this.method = method;
	}

	public static int getMethodSearch() {
		return METHOD_SEARCH;
	}

	public static int getMethodGet() {
		return METHOD_GET;
	}

	public static int getMethodNone() {
		return METHOD_NONE;
	}

	public static Parcelable.Creator<Scroll> getCreator() {
		return CREATOR;
	}

	public String getSearch() {
		return search;
	}

	public void setSearch(String search) {
		this.search = search;
	}
	
}
