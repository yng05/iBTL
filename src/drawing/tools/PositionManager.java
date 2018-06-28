package drawing.tools;

import java.awt.geom.Line2D;
import java.util.ArrayList;

public class PositionManager {
    private ArrayList<Line2D> lines = new ArrayList<Line2D>();
    private ArrayList<String> years = new ArrayList<String>();
    private ArrayList<ArrayList<Line2D>> monthsOfYears = new ArrayList<ArrayList<Line2D>>();
    private int markedLine = -1;

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
			for(int i = 1; i<12;i ++) {
				monthlines.add(new Line2D.Double(xstart + (i*gap), ytop, xstart + (i*gap), ybottom));
			}
			
			monthsOfYears.add(monthlines);
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
			for(int i = 1; i<12;i ++) {
				monthlines.add(new Line2D.Double(xstart + (i*gap), ytop, xstart + (i*gap), ybottom));
			}
			
			monthsOfYears.set(before, monthlines);
    	}
    	
    	if(after != -1) {
    		double xstart = lines.get(markedLine).getX1();
			double xend = lines.get(after).getX1();
			double ytop = lines.get(markedLine).getY1();
			double ybottom = lines.get(markedLine).getY2();
			
			double gap = (xend - xstart) / 12;
			
			ArrayList<Line2D> monthlines = new ArrayList<Line2D>();
			for(int i = 1; i<12;i ++) {
				monthlines.add(new Line2D.Double(xstart + (i*gap), ytop, xstart + (i*gap), ybottom));
			}
			
			monthsOfYears.set(markedLine, monthlines);
    	}
    }
    
    public void reset() {

    }
}
