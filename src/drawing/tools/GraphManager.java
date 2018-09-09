package drawing.tools;

import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import data.DataManager.Data;

public class GraphManager {
	private PositionManager pManager;
	private Double max, min;
	private Double lineLength;
	private Double range, step;
	
    private ArrayList<Line2D> lines;
    private ArrayList<String> years;
    private ArrayList<Line2D> monthsOfYears;
    private ArrayList<double[][]> graphs = new ArrayList<>();


	public GraphManager(PositionManager pManager, Double max, Double min) {
		this.pManager = pManager;
		this.max = max;
		this.min = min;
		Double range = max - min;
		lineLength = pManager.getLines().get(0).getY2() - pManager.getLines().get(0).getY1();
		step = lineLength / range;
	}
	
	public GeneralPath createStraightField(int year, ArrayList<Data> data, boolean isSecond, int ratio) {
		Double offset = 0D;
		if(isSecond) {
			offset = (pManager.getLines().get(0).getY2() - pManager.getLines().get(0).getY1()) / 2;
		}
		double step = this.step / ratio;
		
		if(year + 1 == pManager.getLines().size()) {
			return null;
		}
		Line2D firstBorder = pManager.getLines().get(year);
		Line2D secondBorder = pManager.getLines().get(year + 1);
		
		GeneralPath gp = new GeneralPath();
		monthsOfYears = pManager.getMonthLines(year);
				
		
		
		for(int i = 0; i < data.size() - 1; i++) {
			int datayear = data.get(i).getYear();
			int month = data.get(i).getMonth();
			int day = data.get(i).getDay();
			Double dayGap = (secondBorder.getX1() - firstBorder.getX1()) / (12*31);
			Double value = data.get(i).getData();
			Line2D border = null;
			
			if(month == 1) {
				border = firstBorder;
			}else {
				border = monthsOfYears.get(month - 2);
			}
			
			if(i == 0) {
				gp.moveTo((float)border.getX1() + (day -1) * dayGap, border.getY2() - step * value - offset);
			}else {
				if(value != 0) {					
					gp.lineTo((float)border.getX1() + (day -1) * dayGap, border.getY2() - step * value - offset);						
				}			
			}
		}
		Double value = data.get(data.size()-1).getData();
		gp.lineTo((float)secondBorder.getX1(), secondBorder.getY2() - step * value - offset);
		gp.lineTo((float)secondBorder.getX1(), secondBorder.getY2() - offset);
		gp.lineTo((float)firstBorder.getX1(), firstBorder.getY2() - offset);
		
		gp.closePath();
		
		graphs.add(getPoints(gp));
		
		return gp;
	}
	
	public GeneralPath createSmallMultiples(int year, ArrayList<Data> data, int columns, int col) {
		double SSStep = (double) (step/columns);
		double bottomBuffer = (double)(col * (lineLength/columns));
		if(year + 1 == pManager.getLines().size()) {
			return null;
		}
		Line2D firstBorder = pManager.getLines().get(year);
		Line2D secondBorder = pManager.getLines().get(year + 1);
		
		GeneralPath gp = new GeneralPath();
		monthsOfYears = pManager.getMonthLines(year);
		
		for(int i = 0; i < data.size() - 1; i++) {
			int month = data.get(i).getMonth();
			int day = data.get(i).getDay();
			Double dayGap = (secondBorder.getX1() - firstBorder.getX1()) / (12*31);
			Double value = data.get(i).getData();
			Line2D border = null;
			
			if(month == 1) {
				border = firstBorder;
			}else {
				border = monthsOfYears.get(month - 2);
			}
			
			if(i == 0) {
				gp.moveTo((float)firstBorder.getX1(), firstBorder.getY2() - bottomBuffer - SSStep * value);
			}else {
				if(value != 0) {
					gp.lineTo((float)border.getX1() + (day -1) * dayGap, border.getY2() - bottomBuffer - SSStep * value);	
				}			
			}
		}
		
		Double value = data.get(data.size()-1).getData();
		gp.lineTo((float)secondBorder.getX1(), secondBorder.getY2() - SSStep * value - bottomBuffer);
		gp.lineTo((float)secondBorder.getX1(), secondBorder.getY2() - bottomBuffer);

		gp.lineTo((float)firstBorder.getX1(), firstBorder.getY2() - bottomBuffer);
		
		gp.closePath();
		
		return gp;
	}
	
	
	public GeneralPath createSimpleLineGraph(int year, ArrayList<Data> data, boolean isSecond, int ratio) {		
		Double offset = 0D;
		if(isSecond) {
			offset = (pManager.getLines().get(0).getY2() - pManager.getLines().get(0).getY1()) / 2;
		}
		double step = this.step / ratio;
		
		if(year + 1 == pManager.getLines().size()) {
			return null;
		}
		Line2D firstBorder = pManager.getLines().get(year);
		Line2D secondBorder = pManager.getLines().get(year + 1);
		
		GeneralPath gp = new GeneralPath();
		monthsOfYears = pManager.getMonthLines(year);
				
		
		
		for(int i = 0; i < data.size()-1; i++) {
			int month = data.get(i).getMonth();
			int day = data.get(i).getDay();
			Double dayGap = (secondBorder.getX1() - firstBorder.getX1()) / (12*31);
			Double value = data.get(i).getData();
			Line2D border = null;
			
			if(month == 1) {
				border = firstBorder;
			}else {
				border = monthsOfYears.get(month - 2);
			}
			
			if(i == 0) {
				gp.moveTo((float)border.getX1() + (day -1) * dayGap, border.getY2() - step * value - offset);
			}else {
				if(value != 0) {
					gp.lineTo((float)border.getX1() + (day -1) * dayGap, border.getY2() - step * value - offset);	
				}			
			}
		}
		Double value = data.get(data.size()-1).getData();
		gp.lineTo((float)secondBorder.getX1(), secondBorder.getY2() - step * value - offset);

		return gp;
	}
	
	public GeneralPath createStreamGraph(int year, ArrayList<Data> data, ArrayList<Data> dataPrev, double ratio, boolean isCentered, boolean isSecond) {
		double offset = 0D;
		
		if(isCentered) {
			if(isSecond) {
				offset = (pManager.getLines().get(0).getY2() - pManager.getLines().get(0).getY1()) / 4 * 3;
			}else {
				offset = (pManager.getLines().get(0).getY2() - pManager.getLines().get(0).getY1()) / 2;
			}
		}else {
			if(isSecond) {
				offset = (pManager.getLines().get(0).getY2() - pManager.getLines().get(0).getY1()) / 2;
			}
		}
		
		double step = this.step / ratio;
		if(year + 1 == pManager.getLines().size()) {
			return null;
		}
		Line2D firstBorder = pManager.getLines().get(year);
		Line2D secondBorder = pManager.getLines().get(year + 1);
		
		GeneralPath gp = new GeneralPath();
		monthsOfYears = pManager.getMonthLines(year);
		
		for(int i = 0; i < data.size() - 1; i++) {
			int month = data.get(i).getMonth();
			int day = data.get(i).getDay();
			Double dayGap = (secondBorder.getX1() - firstBorder.getX1()) / (12*31);
			Double value = 0.0;
			if(data.get(i).getData() != 0.0) {
				value = data.get(i).getData();
			}
			Line2D border = null;
			
			if(month == 1) {
				border = firstBorder;
			}else {
				border = monthsOfYears.get(month - 2);
			}
			
			if(i == 0) {
				gp.moveTo((float)border.getX1() + (day -1) * dayGap, border.getY2() - step * value - offset);
			}else {
				gp.lineTo((float)border.getX1() + (day -1) * dayGap, border.getY2() - step * value - offset);
			}
		}
		Double val = data.get(data.size()-1).getData();
		gp.lineTo((float)secondBorder.getX1(), secondBorder.getY2() - step * val - offset);
		
		if(dataPrev != null) {
			Double valPrev = dataPrev.get(dataPrev.size()-1).getData();
			gp.lineTo((float)secondBorder.getX1(), secondBorder.getY2() - step * valPrev - offset);
			for(int i = dataPrev.size() - 2; i > 0; i--) {
				int month = dataPrev.get(i).getMonth();
				int day = dataPrev.get(i).getDay();
				Double dayGap = (secondBorder.getX1() - firstBorder.getX1()) / (12*31);
				Double value = dataPrev.get(i).getData();
				Line2D border = null;
				
				if(month == 1) {
					border = firstBorder;
				}else {
					border = monthsOfYears.get(month - 2);
				}
				
				if(i == 0) {
					gp.moveTo((float)border.getX1() + (day -1) * dayGap, border.getY2() - step * value - offset);
				}else {
					gp.lineTo((float)border.getX1() + (day -1) * dayGap, border.getY2() - step * value - offset);							
				}
			}
			Double valLast = dataPrev.get(0).getData();
			gp.lineTo((float)firstBorder.getX1(), firstBorder.getY2() - step * valLast - offset);
			gp.lineTo((float)firstBorder.getX1(), firstBorder.getY2()- offset);
		}else {
			gp.lineTo((float)secondBorder.getX1(), secondBorder.getY2()- offset);
			gp.lineTo((float)firstBorder.getX1(), firstBorder.getY2()- offset);
		}
		
		
		gp.closePath();
		return gp;
	}
	
	static double[][] getPoints(GeneralPath path) {
	    List<double[]> pointList = new ArrayList<double[]>();
	    double[] coords = new double[6];
	    int numSubPaths = 0;
	    for (PathIterator pi = path.getPathIterator(null);
	         ! pi.isDone();
	         pi.next()) {
	        switch (pi.currentSegment(coords)) {
	        case PathIterator.SEG_MOVETO:
	            pointList.add(Arrays.copyOf(coords, 2));
	            ++ numSubPaths;
	            break;
	        case PathIterator.SEG_LINETO:
	            pointList.add(Arrays.copyOf(coords, 2));
	            break;
	        case PathIterator.SEG_CLOSE:
	            if (numSubPaths > 1) {
	                throw new IllegalArgumentException("Path contains multiple subpaths");
	            }
	            return pointList.toArray(new double[pointList.size()][]);
	        default:
	            throw new IllegalArgumentException("Path contains curves");
	        }
	    }
	    throw new IllegalArgumentException("Unclosed path");
	}
	
	public int detectGraph(int xP, int yP) {
		int match = -1;
		for(int i = 0; i < graphs.size(); i++) {
			double[][] graph = graphs.get(i);
			int xG = (int)graph[0][0];
			int yG = (int)graph[0][1];
			
			if(xG == xP && yG == yP) {
				match = i;
			}
		}
		if(match != -1) {
			return match;
		}else {
			return -1;			
		}
	}

	
}
