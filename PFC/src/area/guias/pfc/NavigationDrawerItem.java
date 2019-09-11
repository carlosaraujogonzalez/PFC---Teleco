package area.guias.pfc;

public class NavigationDrawerItem {
	private String titulo;
	private int icono;
	
	public NavigationDrawerItem(String title, int icon){
		this.titulo=title;
		this.icono=icon;
	}
	
	public String getTitulo(){
		return this.titulo;
	}
	
	public void setTitulo(String title){
		this.titulo=title;
	}
	
	public int getIcono(){
		return this.icono;
	}
	
	public void setIcono(int icono){
		this.icono = icono;
	}
}
