package area.guias.pfc;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;

public class DontPressWithParentImageButton extends ImageButton {

    public DontPressWithParentImageButton(Context context) {
        super(context);
    }

    public DontPressWithParentImageButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DontPressWithParentImageButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setPressed(boolean pressed) {
        if (pressed && getParent() instanceof View && ((View) getParent()).isPressed()) {
        	setBackgroundColor(getResources().getColor(R.color.blue_transparent));
            return;
        }
        super.setPressed(pressed);
        
    }
    

}
