package xkayad00.Engine;

public class Camera{

	public double xPos, yPos;
	public double viewScale=1.0;
	int width, height;
	int mouseX, mouseY;
	private double viewWidth=200;
	private double viewHeight=200;

	Camera(long xPos, long yPos){
		this.xPos=xPos;
		this.yPos=yPos;
	}

	public void changePos(double x, double y){
		this.xPos=x;
		this.yPos=y;
	}

	public void move(double x, double y){
		xPos+=x;
		yPos+=y;
	}

	public void zoom(double delta){
		viewScale+=delta;
		updateScales();
	}

	public void setView(double width, double height){
		viewWidth=width;
		viewHeight=height;
		updateScales();
	}

	public double screenXScale(){
		return viewScale;
	}
	public double screenYScale(){
		return viewScale;
	}

	public int screenXSize(double worldSize){
		return (int) (worldSize*viewScale);
	}
	public int screenYSize(double worldSize){
		return (int) (worldSize*viewScale);
	}
	public int screenXPos(double worldPos){
		return (int) ((worldPos-xPos)*viewScale+getPanelWidth()/2);
	}
	public int screenYPos(double worldPos){
		return (int) ((worldPos-yPos)*viewScale+getPanelHeight()/2);
	}

	private void updateScales(){
		updateScales(width,height);
	}
	void updateScales(int width, int height){
		this.width=width;
		this.height=height;
		double intendedRatio=viewHeight/viewWidth;
		double actualRatio=(double)getPanelHeight()/getPanelWidth();
		if(intendedRatio<actualRatio){
			viewScale=getPanelWidth()/viewWidth;
		}else{
			viewScale=getPanelHeight()/viewHeight;
		}
	};

	public double screenToWorldXPos(int mouseX){
		return (mouseX-getPanelWidth()/2)/viewScale+xPos;
	}
	public double screenToWorldYPos(int mouseY){
		return (mouseY-getPanelHeight()/2)/(-viewScale)+yPos;
	}
	public int getPanelWidth(){
		return width;
	}
	public int getPanelHeight(){
		return height;
	}
	float getXPos(){
		return 0f;
	}
	float getYPos(){
		return 0f;
	}
}
