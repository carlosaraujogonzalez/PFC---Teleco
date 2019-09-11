package area.guias.pfc;

import java.io.File;

import android.net.Uri;

public class FilePath {
	// VARIABLES
		public File file;
		public String path;
		public Uri uri;
		
		// CONSTRUCTOR
		public FilePath(){
			this.file = null;
			this.path = null;
			this.uri = null;
		}
		
		public void setFile(String path){
			this.file = new File(path);
		}
		
		public File getFile(){
			return this.file;
		}
		
		public void setPath(String p){
			this.path = p;
		}
		
		public String getPath(){
			return this.path;
		}
		
		public void setUri(Uri u){
			this.uri = u;
		}
		
		public Uri getUri(){
			return this.uri;
		}
}
