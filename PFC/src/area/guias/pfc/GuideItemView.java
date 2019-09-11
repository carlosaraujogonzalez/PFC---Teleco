package area.guias.pfc;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class GuideItemView extends RelativeLayout {
	private ImageView imagen;
	private TextView titulo, descripcion, keywords;
	private ItemHolder myHolder;

	public GuideItemView(Context context){
		super(context);
		inflate(context, R.layout.guide_item_view, this);
	}
	
	public void createViews(){
		myHolder = (ItemHolder) this.getTag();
	    titulo = myHolder.title;
	    titulo.setTypeface(null, Typeface.BOLD);
	    descripcion = myHolder.description;
	    keywords = myHolder.keywords;
	    imagen = myHolder.image;
	}
	
	public void setTitle(String title){
		titulo.setText(title);
	}
	
	public void setDescription(String description){
		descripcion.setText(description);
	}
	
	public void setKeywords(String keyword){
		keywords.setText(keyword);
	}
	
	public void setImage(Bitmap img){
		imagen.setImageBitmap(img);
	}
	
}
