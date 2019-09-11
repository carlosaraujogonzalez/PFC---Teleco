package area.guias.pfc;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ItemHolderView extends RelativeLayout {
	private TextView title, description, keywords;
	private ImageView image;
	private CheckBox checkbox;
	private View horizontalLine;
	private ItemHolder myHolder;
	

	public ItemHolderView(Context context){
		super(context);
		inflate(context, R.layout.item_view, this);
	}
	
	public void createViews(){
		myHolder = (ItemHolder) this.getTag();
	    title = myHolder.title;
	    title.setTypeface(null, Typeface.BOLD);
	    description = myHolder.description;
	    keywords = myHolder.keywords;
	    image = myHolder.image;
	    checkbox = myHolder.checkbox;
	    horizontalLine = myHolder.horizontalLine;
	}
	
	public void setTitle(String title){
		this.title.setText(title);
	}
	
	public void setDescription(String description){
		this.description.setText(description);
	}
	
	public void setKeywords(String keywords){
		this.keywords.setText(keywords);
	}
	
	public void setImage(Bitmap image){
		this.image.setImageBitmap(image);
	}
	
	public void setCheckboxText(String text){
		this.checkbox.setText(text);
	}
	
	public void setChecked(boolean value){
		this.checkbox.setChecked(value);;
	}
	
	
	public void setCheckboxVisibility(int visibility){
		this.checkbox.setVisibility(visibility);
	}
	
	public void setHorizontalLineVisibility(int visibility){
		this.horizontalLine.setVisibility(visibility);
	}
}
