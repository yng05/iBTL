package data;

import java.util.ArrayList;

public class PreferencesManager {
	private boolean accordion = false;
	private double offsetNormalLeft = 0D;
	private double offsetNormalTop = 0D;
	private double offsetZoomLeft = 0D;
	private double offsetZoomTop = 0D;
	private double zoom = 1;
	private int deactivatedItems = 0;
	
	
	private ArrayList<String> styles = new ArrayList<>();

	public boolean isAccordion() {
		return accordion;
	}

	public void setAccordion(boolean accordion) {
		this.accordion = accordion;
	}

	public double getOffsetLeft() {
		return offsetNormalLeft + offsetZoomLeft;
	}


	public double getOffsetTop() {
		return offsetNormalTop + offsetZoomTop;
	}
	

	public double getOffsetNormalLeft() {
		return offsetNormalLeft;
	}

	public void setOffsetNormalLeft(double offsetNormalLeft) {
		this.offsetNormalLeft = offsetNormalLeft;
	}

	public double getOffsetNormalTop() {
		return offsetNormalTop;
	}

	public void setOffsetNormalTop(double offsetNormalTop) {
		this.offsetNormalTop = offsetNormalTop;
	}

	public double getOffsetZoomLeft() {
		return offsetZoomLeft;
	}

	public void setOffsetZoomLeft(double offsetZoomLeft) {
		this.offsetZoomLeft = offsetZoomLeft;
	}

	public double getOffsetZoomTop() {
		return offsetZoomTop;
	}

	public void setOffsetZoomTop(double offsetZoomTop) {
		this.offsetZoomTop = offsetZoomTop;
	}

	public double getZoom() {
		return zoom;
	}

	public void setZoom(double zoom) {
		if(zoom >= 1.0 && zoom <= 3.0) {
			this.zoom = zoom;
		}
	}

	public ArrayList<String> getStyle() {
		return styles;
	}

	public void addStyle(String style) {
		this.styles.add(style);
	}
	
	public void resetStyle() {
		this.styles.clear();
	};
	
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
