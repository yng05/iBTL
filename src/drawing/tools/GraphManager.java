package drawing.tools;

import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
	
	public GeneralPath createStraightField(int year, Double[] values) {
		if(year + 1 == pManager.getLines().size()) {
			return null;
		}
		Line2D firstBorder = pManager.getLines().get(year);
		Line2D secondBorder = pManager.getLines().get(year + 1);
		
		GeneralPath gp = new GeneralPath();
		monthsOfYears = pManager.getMonthLines(year);
		
		gp.moveTo((float)firstBorder.getX1(), firstBorder.getY2() - step * values[0]);
		for(int i = 1; i < monthsOfYears.size(); i++) {
			Line2D border = monthsOfYears.get(i);
			gp.lineTo((float)border.getX1(), border.getY2() - step * values[i+1]);
		}
		
		gp.lineTo((float)secondBorder.getX1(), secondBorder.getY2() - step * values[values.length - 1]);
		gp.lineTo((float)secondBorder.getX1(), secondBorder.getY2());

		gp.lineTo((float)firstBorder.getX1(), firstBorder.getY2());
		
		gp.closePath();
		
		//graphs.add(getPoints(gp));
		
		return gp;
	}
	
	public GeneralPath createSmallMultiples(int year, Double[] values, int columns, int col) {
		double SSStep = (double) (step/columns);
		double bottomBuffer = (double)(col * (lineLength/columns));
		if(year + 1 == pManager.getLines().size()) {
			return null;
		}
		Line2D firstBorder = pManager.getLines().get(year);
		Line2D secondBorder = pManager.getLines().get(year + 1);
		
		GeneralPath gp = new GeneralPath();
		monthsOfYears = pManager.getMonthLines(year);
		
		gp.moveTo((float)firstBorder.getX1(), firstBorder.getY2() - bottomBuffer - SSStep * values[0]);
		for(int i = 1; i < monthsOfYears.size(); i++) {
			Line2D border = monthsOfYears.get(i);
			gp.lineTo((float)border.getX1(), border.getY2() - bottomBuffer - SSStep * values[i+1]);
		}
		
		gp.lineTo((float)secondBorder.getX1(), secondBorder.getY2() - bottomBuffer - SSStep * values[values.length - 1]);
		gp.lineTo((float)secondBorder.getX1(), secondBorder.getY2() - bottomBuffer);

		gp.lineTo((float)firstBorder.getX1(), firstBorder.getY2() - bottomBuffer);
		
		gp.closePath();
		
		return gp;
	}
	
	
	public GeneralPath createSimpleLineGraph(int year, Double[] values) {
		if(year + 1 == pManager.getLines().size()) {
			return null;
		}
		Line2D firstBorder = pManager.getLines().get(year);
		Line2D secondBorder = pManager.getLines().get(year + 1);
		
		GeneralPath gp = new GeneralPath();
		monthsOfYears = pManager.getMonthLines(year);
		
		gp.moveTo((float)firstBorder.getX1(), firstBorder.getY2() - step * values[0]);
		for(int i = 1; i < monthsOfYears.size(); i++) {
			Line2D border = monthsOfYears.get(i);
			gp.lineTo((float)border.getX1(), border.getY2() - step * values[i+1]);
		}
		
		gp.lineTo((float)secondBorder.getX1(), secondBorder.getY2() - step * values[values.length - 1]);
		
		
		return gp;
	}
	
	public GeneralPath createStreamGraph(int year, Double[] values, Double[] prevVals, double ratio, boolean isCentered) {
		double offset = 0D;
		if(isCentered) {
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
		
		gp.moveTo((float)firstBorder.getX1(), firstBorder.getY2() - step * values[0] - offset);
		for(int i = 1; i < monthsOfYears.size(); i++) {
			Line2D border = monthsOfYears.get(i);
			gp.lineTo((float)border.getX1(), border.getY2() - step * values[i+1] - offset);
		}

		gp.lineTo((float)secondBorder.getX1(), secondBorder.getY2() - step * values[values.length - 1] - offset);
		gp.lineTo((float)secondBorder.getX1(), secondBorder.getY2() - step * prevVals[prevVals.length - 1] - offset);
		
		for(int i = monthsOfYears.size() -1; i > 0 ; i--) {
			Line2D border = monthsOfYears.get(i);
			gp.lineTo((float)border.getX1(), border.getY2() - step * prevVals[i + 1] - offset);
		}

		gp.lineTo((float)firstBorder.getX1(), firstBorder.getY2() - step * prevVals[0] - offset);
		gp.lineTo((float)firstBorder.getX1(), firstBorder.getY1());
		
		gp.closePath();
		
		//graphs.add(getPoints(gp));
		
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
