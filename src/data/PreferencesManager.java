package data;

public class PreferencesManager {
	private boolean accordion = false;
	private double offsetLeft = 0D;
	private double offsetTop = 0D;
	private double zoom = 1;
	private int deactivatedItems = 0;
	
	private String style = "Line";

	public boolean isAccordion() {
		return accordion;
	}

	public void setAccordion(boolean accordion) {
		this.accordion = accordion;
	}

	public double getOffsetLeft() {
		return offsetLeft;
	}

	public void setOffsetLeft(double offsetLeft) {
		this.offsetLeft = offsetLeft;
	}

	public double getOffsetTop() {
		return offsetTop;
	}

	public void setOffsetTop(double offsetTop) {
		this.offsetTop = offsetTop;
	}

	public double getZoom() {
		return zoom;
	}

	public void setZoom(double zoom) {
		if(zoom >= 1.0 && zoom <= 3.0) {
			this.zoom = zoom;
		}
	}

	public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
	}
	
	public int getDeactivatedItems() {
		return deactivatedItems;
	}

	public void dItUp() {
		deactivatedItems++;
	}
	
	public void dItDown() {
		deactivatedItems--;
	}
	
	
	
	
}
