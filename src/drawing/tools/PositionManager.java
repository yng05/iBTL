package drawing.tools;

import java.awt.Toolkit;
import java.awt.geom.Line2D;
import java.util.ArrayList;

public class PositionManager {
    private ArrayList<Line2D> lines = new ArrayList<Line2D>();
    private ArrayList<String> years = new ArrayList<String>();
    private ArrayList<ArrayList<Line2D>> monthsOfYears = new ArrayList<ArrayList<Line2D>>();
    private ArrayList<ArrayList<Line2D>> daysOfMonths = new ArrayList<ArrayList<Line2D>>();
    private int markedLine = -1;
    private boolean isInMagnifierView = false;

	private double fullGap = 200D;
    
	public ArrayList<Line2D> getLines() {
		return lines;
	}
	
	public ArrayList<ArrayList<Line2D>> getMonthsOfYears() {
		return monthsOfYears;
	}

	public ArrayList<Line2D> getMonthLines(int ordinal) {
		return monthsOfYears.get(ordinal);
	}
	
	public ArrayList<ArrayList<Line2D>> getDaysOfMonths() {
		return daysOfMonths;
	}

	public void setDaysOfMonths(ArrayList<ArrayList<Line2D>> daysOfMonths) {
		this.daysOfMonths = daysOfMonths;
	}

	public void addLine(Line2D line, String year) {
		lines.add(line);
		years.add(year);
		if(lines.size() > 1) {
			
			double xstart = lines.get(lines.size() -2).getX1();
			double xend = lines.get(lines.size() -1).getX1();
			double ytop = lines.get(lines.size() -1).getY1();
			double ybottom = lines.get(lines.size() -1).getY2();
			
			fullGap = xend - xstart;
			double gap = (xend - xstart) / 12;
			
			ArrayList<Line2D> monthlines = new ArrayList<Line2D>();
			ArrayList<Line2D> daylines = new ArrayList<Line2D>();
			for(int i = 1; i<=12;i ++) {
				Line2D monthline = new Line2D.Double(xstart + (i*gap), ytop, xstart + (i*gap), ybottom);
				monthlines.add(monthline);
				int days = 30;
				if(i == 1 || i == 3 || i == 5 || i == 7 || i == 8 || i == 10 || i==12) {
					days = 31;
				}else if(i == 2) {
					int yearInt = Integer.parseInt(year);					
					if(yearInt%4 == 0) {
						days = 29;
					}else {
						days = 28;
					}
				}
				
				double xmstart = monthline.getX1() - gap;
				double daygap = gap/days;
				for(int j = 1; j<days; j++) {
					if(j%5 == 0) {
						Line2D dayline = new Line2D.Double(xmstart + j*daygap, ytop, xmstart + (j*daygap), ybottom);
						daylines.add(dayline);	
					}
				}
			}
			
			monthsOfYears.add(monthlines);
			daysOfMonths.add(daylines);
		}
	}
    
    public void detectTheLine(double x, double offset, double zoom) {
    	for(int i = 0; i< lines.size(); i++) {
    		if((lines.get(i).getX1() * zoom) + offset > x - 5 && (lines.get(i).getX1()  * zoom) + offset < x+5) {
    			markedLine = i;
    		}
    	}
    }
    
    public void releaseTheLine() {
    	markedLine = -1;
    }
    
    public int getMarkedLine() {
		return markedLine;
	}
    
    public void moveTheLine(double x, double offset, boolean accordion) {    	
    	Double y1 = lines.get(markedLine).getY1();
    	Double y2 = lines.get(markedLine).getY2();
    	Double xPrev = -1D, xNext = -1D ,xFirst = -1D , xLast = -1D;
    	
    	Double xMarked = lines.get(markedLine).getX1();
    	if(markedLine > 0) {
    		xPrev = lines.get(markedLine - 1).getX1();  
    		xFirst = lines.get(0).getX1();  
    	}
    	if(markedLine + 1 < lines.size()) {
    		xNext = lines.get(markedLine + 1).getX1();   
    		xLast = lines.get(lines.size()-1).getX1();   
    	}
    	
    	if((xPrev > -1D && (x - offset) - xPrev < 5) || (xNext > -1D && xNext - (x - offset) < 5)) {
    		return;
    	}
    	

    	lines.get(markedLine).setLine(new Line2D.Double(x - offset, y1, x - offset, y2));
    	rectificateTheMonthLines(markedLine);	
    	
    	if(accordion) {
    		if(xMarked - xPrev < fullGap/2) {
    			for(int i = 1; i < markedLine; i++) {
    				double xNew = ((xMarked - xFirst)/markedLine * i) + xFirst;
        			lines.get(i).setLine(new Line2D.Double(xNew, y1, xNew, y2));
        			rectificateTheMonthLines(i);
    			}
    		}   
    		if(xNext - xMarked < fullGap/2) {
    			for(int i = markedLine + 1; i < lines.size() - 1 ; i++) {
    				double xNew = ((xLast - xMarked)/(lines.size() - markedLine - 1) * (i - markedLine)) + xMarked;
        			lines.get(i).setLine(new Line2D.Double(xNew, y1, xNew, y2));
        			rectificateTheMonthLines(i);
    			}
    		}    		
    	}  		

    }
    
    public void rectificateTheMonthLines(int markedLine)
    {
    	int before = -1;
    	int after = -1;
    	if(markedLine > 0) {
    		before = markedLine -1;
    	}
    	if(markedLine < lines.size() -1) {
    		after = markedLine + 1;
    	}
    	
    	if(before != -1) {
    		double xstart = lines.get(before).getX1();
			double xend = lines.get(markedLine).getX1();
			double ytop = lines.get(markedLine).getY1();
			double ybottom = lines.get(markedLine).getY2();
			
			double gap = (xend - xstart) / 12;
			
			ArrayList<Line2D> monthlines = new ArrayList<Line2D>();
			ArrayList<Line2D> daylines = new ArrayList<Line2D>();
			for(int i = 1; i<=12;i ++) {
				Line2D monthline = new Line2D.Double(xstart + (i*gap), ytop, xstart + (i*gap), ybottom);
				monthlines.add(monthline);
				
				int days = 30;
				if(i == 1 || i == 3 || i == 5 || i == 7 || i == 8 || i == 10 || i==12) {
					days = 31;
				}else if(i == 2) {
					int year = Integer.valueOf(years.get(markedLine));					
					if(year%4 == 0) {
						days = 29;
					}else {
						days = 28;
					}
				}
				
				double xmstart = monthline.getX1() - gap;
				double daygap = gap/days;
				
				for(int j = 1; j<days; j++) {
					if(j%5 == 0) {
						Line2D dayline = new Line2D.Double(xmstart + j*daygap, ytop, xmstart + (j*daygap), ybottom);
						daylines.add(dayline);	
					}			
				}
			}
			
			monthsOfYears.set(before, monthlines);
			daysOfMonths.set(before, daylines);
    	}
    	
    	if(after != -1) {
    		double xstart = lines.get(markedLine).getX1();
			double xend = lines.get(after).getX1();
			double ytop = lines.get(markedLine).getY1();
			double ybottom = lines.get(markedLine).getY2();
			
			double gap = (xend - xstart) / 12;
			
			ArrayList<Line2D> monthlines = new ArrayList<Line2D>();
			ArrayList<Line2D> daylines = new ArrayList<Line2D>();
			for(int i = 1; i<=12;i ++) {
				Line2D monthline = new Line2D.Double(xstart + (i*gap), ytop, xstart + (i*gap), ybottom);
				monthlines.add(monthline);
				int days = 30;
				if(i == 1 || i == 3 || i == 5 || i == 7 || i == 8 || i == 10 || i==12) {
					days = 31;
				}else if(i == 2) {
					int year = Integer.valueOf(years.get(markedLine));					
					if(year%4 == 0) {
						days = 29;
					}else {
						days = 28;
					}
				}
				
				double xmstart = monthline.getX1() - gap;
				double daygap = gap/days;
				
				for(int j = 1; j<days; j++) {
					if(j%5 == 0) {
						Line2D dayline = new Line2D.Double(xmstart + j*daygap, ytop, xmstart + (j*daygap), ybottom);
						daylines.add(dayline);	
					}			
				}
			}
			
			monthsOfYears.set(markedLine, monthlines);
			daysOfMonths.set(markedLine, daylines);
    	}
    }
    
    public void magnifierView() {    		
		if(isInMagnifierView) {
			return;
		}
    	for(int l = 0; l < lines.size(); l++){
    		Double xPos = lines.get(l).getX1();
    		Double centerXPos = Toolkit.getDefaultToolkit().getScreenSize().getWidth()/2;
    		Double newX = 0D;

    		newX = xPos + 3*(xPos - centerXPos);
    		lines.get(l).setLine(new Line2D.Double(newX, lines.get(l).getY1(), newX, lines.get(l).getY2()));   
    		
    		rectificateTheMonthLines(l);
    	}
    }
    
    
    
    public boolean isInMagnifierView() {
		return isInMagnifierView;
	}

	public void setInMagnifierView(boolean isInMagnifierView) {
		this.isInMagnifierView = isInMagnifierView;
	}

	public void reset() {

    }
}
