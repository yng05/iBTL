package drawing.tools;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.io.LineNumberInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import javax.swing.JPanel;
import javax.swing.Timer;

import data.DataManager;
import data.DataManager.Column;
import data.DataManager.Data;
import data.PreferencesManager;

public class DrawingCanvas extends JPanel {

    private Double canvasW  = 640D;
    private Double canvasH= 480D;
 
	public static final Color LINE_COLOR = Color.BLACK;
    private static final Color CANVAS_BACKGROUND = Color.WHITE;
    private PositionManager pManager;
    private DataManager dManager;
    private GraphManager gManager;
    private PreferencesManager prManager;

    private BufferedImage bImg;
    public double TOP_LINE_END = 50D;
    private double BOTTOM_LINE_END;
    
    Color[] colors;
    int infoPanelX = Integer.MIN_VALUE;
    int infoPanelY = Integer.MIN_VALUE;
    String text = "";
    SelectedColumn selectedCol;
    SelectedColumn selectedColSecond;
    
    public GraphManager getgManager() {
		return gManager;
	}


	public DrawingCanvas(PositionManager pManager, DataManager dManager, PreferencesManager prManager) {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		canvasW = screenSize.getWidth();
		canvasH = screenSize.getHeight() - 400D;
		BOTTOM_LINE_END = (Double)canvasH - 50D;
    	setPreferredSize(new Dimension((int)Math.round(canvasW), (int)Math.round(canvasH)));
    	
    	this.pManager = pManager;
    	this.dManager = dManager;
    	this.prManager = prManager;
    	
    	doPositioning();
    	
    	gManager = new GraphManager(pManager, dManager.getMax(), dManager.getMin());
    }


	public void setPositionManager(PositionManager pManager) {
    	this.pManager = pManager;
    }
    
    public void setDataManager(DataManager dManager) {
    	this.dManager = dManager;
    }

	   
	private void doDrawing(Graphics g) {
	    AffineTransform coordTransform = new AffineTransform();
		Graphics2D g2D = (Graphics2D) g;
        setBackground(CANVAS_BACKGROUND); 
        g2D.setColor(LINE_COLOR);
        coordTransform.translate(prManager.getOffsetLeft(), prManager.getOffsetTop());
        coordTransform.scale(prManager.getZoom(), prManager.getZoom());
        g2D.setTransform(coordTransform);
        
        ArrayList<Line2D> lines = pManager.getLines();
        
        if(colors == null) {
        	colors = setColors(dManager.getColumns().size());
        	for(int col = 0; col < dManager.getColumns().size(); col++) {
        		dManager.getColumns().get(col).setColor(colors[col]);
        	}
        }
       	
    	g2D.setRenderingHint(
    		    RenderingHints.KEY_TEXT_ANTIALIASING,
    		    RenderingHints.VALUE_TEXT_ANTIALIAS_ON);	
    	
    	ArrayList<Column> columns = dManager.getColumns();
        
        for(int l = 0; l<lines.size(); l++) {
        	boolean isSecond = false;
	        if(prManager.getStyle().contains("Line")) {
	        	for(int c = 0; c < columns.size() - 1; c++) {
	        		if(!columns.get(c).isActive()) {
	        			continue;
	        		}
	                g2D.setColor(columns.get(c).getColor());	     
	        		int year = Integer.parseInt(dManager.getYears().get(l));
	            	ArrayList<Data> yearsData = dManager.getYearData(c, year);	        		
	        		g2D.setStroke(new BasicStroke(2));
	        		
	        		
	        		GeneralPath path = gManager.createSimpleLineGraph(l, yearsData, isSecond, prManager.getStyle().size());
        			if(path != null) {
	        			g2D.draw(path);		        				
        			}
        			
        			if(!isSecond) {
        				addPathToSecondSelected("Draw", path, c);
        			}else {
            			addPathToSelected("Draw", path, c);         				
        			}     		

	        	}  
    			isSecond = true;  
	        }
	        if(prManager.getStyle().contains("Area")) {
	        	for(int c = 0; c < columns.size() - 1; c++) {
	        		if(!columns.get(c).isActive()) {
	        			continue;
	        		}
	                g2D.setColor(columns.get(c).getColor());	     
	        		int year = Integer.parseInt(dManager.getYears().get(l));
	            	ArrayList<Data> yearsData = dManager.getYearData(c, year);	        		
	        		g2D.setStroke(new BasicStroke(2));
	        		
	        		        		
	        		GeneralPath path = gManager.createStraightField(l, yearsData, isSecond, prManager.getStyle().size());
        			if(path != null) {
	        			g2D.fill(path);		        				
        			}
        			if(!isSecond) {
        				addPathToSecondSelected("Fill", path, c);
        			}else {
            			addPathToSelected("Fill", path, c);         				
        			}        			

	        	}  
    			isSecond = true;  
	        }
	        if(prManager.getStyle().contains("Small Multiples")) {
	        	for(int c = 0; c < columns.size() - 1; c++) {
	        		if(!columns.get(c).isActive()) {
	        			continue;
	        		}
	                g2D.setColor(columns.get(c).getColor());	     
	        		int year = Integer.parseInt(dManager.getYears().get(l));
	            	ArrayList<Data> yearsData = dManager.getYearData(c, year);	        		
	        		g2D.setStroke(new BasicStroke(2));
	        		
	        		
	        		if(prManager.getStyle().contains("Small Multiples")) {		        		
		        		GeneralPath path = gManager.createSmallMultiples(l, yearsData, columns.size(), c);
	        			if(path != null) {
		        			g2D.fill(path);		        				
	        			}

	        			if(!isSecond) {
	        				addPathToSecondSelected("Fill", path, c);
	        			}else {
	            			addPathToSelected("Fill", path, c);         				
	        			}   
        			}

	        	}  
	        }
	        if(prManager.getStyle().contains("Braided")) {
	        	ArrayList<YearData> gapData = new ArrayList<>();
	        	for(int c = 0; c < columns.size() - 1; c++) {
	        		if(!columns.get(c).isActive()) {
	        			continue;
	        		}     
	        		int year = Integer.parseInt(dManager.getYears().get(l));
	            	ArrayList<Data> yearsData = dManager.getYearData(c, year);	
	            	YearData yData = new YearData(yearsData, columns.get(c).getColor(), c);
	            	gapData.add(yData);	            	
	        	}
	        	
	        	Collections.sort(gapData, HighestPoint);
	        	
	        	for (YearData yearsData : gapData) {
	                g2D.setColor(yearsData.color);	
	        		GeneralPath path = gManager.createStraightField(l, yearsData.data, isSecond, prManager.getStyle().size());
        			if(path != null) {
	        			g2D.fill(path);		        				
        			}

        			if(!isSecond) {
        				addPathToSecondSelected("Fill", path, yearsData.column);
        			}else {
            			addPathToSelected("Fill", path, yearsData.column);         				
        			}   
				}
    			isSecond = true;
	        }
	        if(prManager.getStyle().contains("Stacked(Baseline)")) {// || prManager.getStyle().equals("Stacked(Centered)")
            	ArrayList<Data> yearsData = null;	   
            	ArrayList<Data> yearsDataPrev = null;
            	ArrayList<Column> activeColumns = new ArrayList<DataManager.Column>();
            	
            	for(int c = 0; c < columns.size(); c++) {
	        		if(columns.get(c).isActive()) {
	        			activeColumns.add(columns.get(c));
	        		}  
            	}
            	
	        	for(int c = 0; c < activeColumns.size(); c++) {
	        		int dataIndex = columns.indexOf(activeColumns.get(c));
	                g2D.setColor(activeColumns.get(c).getColor());               
	                
	        		int year = Integer.parseInt(dManager.getYears().get(l));
	            	if(c== 0) {
		            	yearsData = dManager.getYearData(dataIndex, year);	   
		            	yearsDataPrev = null;
	            	}
	            	else{
	            		if(c == 1) {
	            			yearsData = dManager.getYearData(dataIndex, year);	   
	            			yearsDataPrev = dManager.getYearData(dataIndex - 1, year);
	            		}
	            		            		
	            		for(int y = 0; y < yearsData.size(); y++) {
	            			yearsDataPrev.get(y).setData(yearsData.get(y).getData());
	            		}
	            		
	            		ArrayList<Data> yd = dManager.getYearData(dataIndex, year);
	            		
	            		for(int y = 0; y < yd.size(); y++) {
	            			yearsData.get(y).increaseData(yd.get(y).getData());
	            		}
	            	}
	            	
	        		g2D.setStroke(new BasicStroke(1));
	        		
	        		
	        		GeneralPath path = gManager.createStreamGraph(l, yearsData, yearsDataPrev, 12 * prManager.getStyle().size(), false,isSecond);
        			if(path != null) {
	        			g2D.fill(path);		        				
        			}      	
        			
        			if(!isSecond) {
        				addPathToSecondSelected("Fill", path, dataIndex);
        			}else {
            			addPathToSelected("Fill", path, dataIndex);         				
        			}   

	        	}  	        	

    			isSecond = true;
	        }
	        if(prManager.getStyle().contains("Stacked(Centered)")) {// || prManager.getStyle().equals("Stacked(Centered)")
            	ArrayList<Data> yearsData = null;	   
            	ArrayList<Data> yearsDataPrev = null;
            	int threshold = columns.size()/2;
            	ArrayList<Column> activeColumns = new ArrayList<DataManager.Column>();
            	
            	for(int c = 0; c < columns.size(); c++) {
	        		if(columns.get(c).isActive()) {
	        			activeColumns.add(columns.get(c));
	        		}  
            	}
            	
	        	for(int c = 0; c < activeColumns.size(); c++) {
	        		int dataIndex = columns.indexOf(activeColumns.get(c));
	        		if(c < threshold) {
		                g2D.setColor(activeColumns.get(c).getColor());               
		                
		        		int year = Integer.parseInt(dManager.getYears().get(l));
		            	if(c == 0) {
			            	yearsData = dManager.getYearData(dataIndex, year);	   
			            	yearsDataPrev = null;
		            	}
		            	else { 
		            		if(c == 1) {   
		            			yearsDataPrev = dManager.getYearData(dataIndex - 1, year);
		            		}
		            	
		            		for(int y = 0; y < yearsData.size(); y++) {
		            			yearsDataPrev.get(y).setData(yearsData.get(y).getData());
		            		}
		            		
		            		ArrayList<Data> yd = dManager.getYearData(dataIndex, year);
		            		
		            		for(int y = 0; y < yd.size(); y++) {
		            			yearsData.get(y).increaseData(yd.get(y).getData());
		            		}
		            	}
		            	
		        		g2D.setStroke(new BasicStroke(2));
		        		
		        		
		        		GeneralPath path = gManager.createStreamGraph(l, yearsData, yearsDataPrev, 12 * prManager.getStyle().size(), true, isSecond);
	        			if(path != null) {
		        			g2D.fill(path);		        				
	        			}
	        			addPathToSelected("Fill", path, c);
	        		}else {
	        			g2D.setColor(activeColumns.get(c).getColor());               
		                
		        		int year = Integer.parseInt(dManager.getYears().get(l));
		            	if(c == threshold) {
			            	yearsData = dManager.getNegativeYearData(dataIndex, year);	   
			            	yearsDataPrev = null;
		            	}
		            	else {
		            		if(c == threshold + 1) {   
				            	yearsDataPrev = dManager.getNegativeYearData(dataIndex - 1, year);
			            	}
			            	
		            		for(int y = 0; y < yearsData.size(); y++) {
		            			yearsDataPrev.get(y).setData(yearsData.get(y).getData());
		            		}
		            		
		            		ArrayList<Data> yd = dManager.getNegativeYearData(dataIndex, year);
		            		
		            		for(int y = 0; y < yd.size(); y++) {
		            			yearsData.get(y).increaseData(yd.get(y).getData());
		            		}
			            	
	            		}
		            	
		        		g2D.setStroke(new BasicStroke(2));
		        		
		        		
		        		GeneralPath path = gManager.createStreamGraph(l, yearsData, yearsDataPrev, 12 * prManager.getStyle().size(), true, isSecond);
	        			if(path != null) {
		        			g2D.fill(path);		        				
	        			}

	        			if(!isSecond) {
	        				addPathToSecondSelected("Fill", path, dataIndex);
	        			}else {
	            			addPathToSelected("Fill", path, dataIndex);         				
	        			} 
	        		}

	        	} 
	        	isSecond = true;
	        }
	        
	        
	        g2D.setStroke(new BasicStroke(1));
        	if(l< lines.size()-1) {
        		if(pManager.isInMagnifierView()) {
		        	g2D.setColor(Color.LIGHT_GRAY);
		        	ArrayList<Line2D> daylines = pManager.getDaysOfMonths().get(l);
	
		        	for(int d = 0; d<daylines.size();d++) {
		            	g2D.drawLine((int)daylines.get(d).getX1(), (int)daylines.get(d).getY1(), (int)daylines.get(d).getX2(), (int)daylines.get(d).getY2());
		            }
	        	}
	        	
            	g2D.setColor(Color.RED);
	        	ArrayList<Line2D> monthLines = pManager.getMonthLines(l);
	        	for(int m = 0; m<monthLines.size();m++) {
	            	g2D.drawLine((int)monthLines.get(m).getX1(), (int)monthLines.get(m).getY1(), (int)monthLines.get(m).getX2(), (int)monthLines.get(m).getY2());
	            }
	        	
        	}    
            if(selectedCol != null) {
            	for(int i = 0; i < selectedCol.getPath().size(); i++) {
        			if(selectedCol.getPath().get(i) == null) {
        				continue;
        			}

            		if(selectedCol.type == "Draw") {
            	        g2D.setColor(Color.RED);
            	        g2D.setStroke(new BasicStroke(3));
            			g2D.draw(selectedCol.getPath().get(i));
            		}else if(selectedCol.type == "Fill") {
            	        g2D.setColor(Color.BLACK);
            	        g2D.setStroke(new BasicStroke(1));
            			g2D.draw(selectedCol.getPath().get(i));
            	        g2D.setColor(new Color(153, 0, 51, 70));
            			g2D.fill(selectedCol.getPath().get(i));
            		}
            	}
            }   
            if(selectedColSecond != null) {
            	for(int i = 0; i < selectedColSecond.getPath().size(); i++) {
        			if(selectedColSecond.getPath().get(i) == null) {
        				continue;
        			}

            		if(selectedColSecond.type == "Draw") {
            	        g2D.setColor(Color.RED);
            	        g2D.setStroke(new BasicStroke(3));
            			g2D.draw(selectedColSecond.getPath().get(i));
            		}else if(selectedColSecond.type == "Fill") {
            	        g2D.setColor(Color.BLACK);
            	        g2D.setStroke(new BasicStroke(1));
            			g2D.draw(selectedColSecond.getPath().get(i));
            	        g2D.setColor(new Color(153, 0, 51, 70));
            			g2D.fill(selectedColSecond.getPath().get(i));
            		}
            	}
            }
            g2D.setColor(LINE_COLOR);
        	g2D.setStroke(new BasicStroke(3));
        	g2D.drawLine((int)lines.get(l).getX1(), (int)lines.get(l).getY1(), (int)lines.get(l).getX2(), (int)lines.get(l).getY2()); // Draw the line
        	g2D.drawString(dManager.getYears().get(l), (int)lines.get(l).getX1() - 20, (int)lines.get(l).getY2() + 20);

        }  
    	g2D.setStroke(new BasicStroke(1));

    	if(prManager.getStyle().contains("Line") || prManager.getStyle().contains("Area") || prManager.getStyle().contains("Braided")) {
    		if(prManager.getStyle().size() == 1) {
		    	int valueRangeX = (int)lines.get(0).getX1() - 30;
		    	int valueRangeY = (int)lines.get(0).getY2();
		    	
		    	double rangeStep = (dManager.getMax() - dManager.getMin()) / 9;
		    	double lineStep = ((int)lines.get(0).getY1() - (int)lines.get(0).getY2())/9;
	            
		    	for(int rng = 0; rng < 10; rng++) {
		    		int stepLine = (int) (valueRangeY + rng*lineStep);
	    	        g2D.setColor(Color.BLACK);
		    		g2D.drawString(String.valueOf((int)(dManager.getMin() + rng*rangeStep)), valueRangeX, stepLine);
	
	    	        g2D.setColor(new Color(0, 0, 0, 50));
		    		g2D.drawLine((int)lines.get(0).getX1(), stepLine - 5, (int)lines.get(lines.size()-1).getX1(), stepLine - 5);
		    	}
	    	}else {
    			int valueRangeX = (int)lines.get(0).getX1() - 30;
		    	int valueRangeY = ((int)lines.get(0).getY2() - (int)lines.get(0).getY1())  / 2;
		    	double rangeStep = (dManager.getMax() - dManager.getMin()) / 18;
		    	double lineStep = ((int)lines.get(0).getY1() - (int)lines.get(0).getY2())/18;
	    		for(int s = 0; s<=prManager.getStyle().size() - 1; s++) {
	    			if(!prManager.getStyle().get(s).equals("Line") && !prManager.getStyle().get(s).equals("Area") && !prManager.getStyle().get(s).equals("Braided")) {
	    				continue;
	    			}
	    			
			    	int bottom = valueRangeY * (2 - s) + (int)lines.get(0).getY1();
			    	
			    	int limit = 10;
			    	if(s == 0) {
			    		limit = 9;
			    	}
			    	for(int rng = 0; rng < limit; rng++) {
			    		int stepLine = (int) (bottom + rng*lineStep);
		    	        g2D.setColor(Color.BLACK);
			    		g2D.drawString(String.valueOf((int)(dManager.getMin() + rng*rangeStep)), valueRangeX, stepLine);
		
		    	        g2D.setColor(new Color(0, 0, 0, 50));
			    		g2D.drawLine((int)lines.get(0).getX1(), stepLine - 5, (int)lines.get(lines.size()-1).getX1(), stepLine - 5);
			    	}
	    		}
	    	}
    	}
    	
        g2D.drawRect(infoPanelX + 10, infoPanelY - 20, 200, 20);
        g2D.setColor(new Color(255, 255, 204));
        g2D.fillRect(infoPanelX + 10, infoPanelY - 20, 199, 19);
        g2D.setColor(Color.BLACK);
        g2D.drawString(text, infoPanelX + 10, infoPanelY - 3);
       
    }

	
	public void printTitle(Color color, int x, int y) {
		for(int col = 0; col < dManager.getColumns().size(); col++) {    		
    		if(color.equals(dManager.getColumns().get(col).getColor())) {
    			infoPanelX = (int) ((int) (x - prManager.getOffsetLeft())/prManager.getZoom());
    			infoPanelY = (int) ((int) (y - prManager.getOffsetTop())/prManager.getZoom());    			
    			text = dManager.getColumns().get(col).getName();// + "/" + color + "/" + x + "/" + y;
    			repaint();
				return;
			}
    	}
		
		infoPanelX = Integer.MIN_VALUE;
		infoPanelY = Integer.MIN_VALUE;
		text = "";

		repaint();
	}
	
	public void pickColumn(Color color) {
		for(int col = 0; col < dManager.getColumns().size(); col++) {    		
    		if(color.equals(dManager.getColumns().get(col).getColor())) {
    			selectedCol = new SelectedColumn(col);
    			selectedColSecond = new SelectedColumn(col);
    			repaint();
				return;
			}
    	}	
		selectedCol = null;
		selectedColSecond = null;
		repaint();
	}
	
	public void doPositioning() {
		ArrayList<Column> columns = dManager.getColumns();	
		double gapSize = (canvasW - 100D) / (dManager.getYears().size()-1);
		
		if(pManager.getLines().size() != 0) {
			pManager.getLines().clear();
			pManager.getMonthsOfYears().clear();
			pManager.getDaysOfMonths().clear();
		}
		
		for(int i = 0; i < dManager.getYears().size(); i++) {
			pManager.addLine(new Line2D.Double(TOP_LINE_END + i * gapSize, TOP_LINE_END, TOP_LINE_END + i * gapSize, BOTTOM_LINE_END), dManager.getYears().get(i));
		}
	}

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        doDrawing(g);
    }
    public Color[] setColors(int size){
    	Color[] colors = new Color[size];
    	
    	for(int i = 0; i < size; i++) {
    		colors[i] = generateColor();
    	}
    	return colors;
    }
    
    public Color generateColor() {
    	Random rand = new Random();
    	float r = rand.nextFloat();
    	float g = rand.nextFloat();
    	float b = rand.nextFloat();
    	
    	return new Color(r, g, b);
    }
    
    public void reset() {
    	doPositioning();
    	prManager.setZoom(1D);
    	prManager.setOffsetNormalLeft(0D);
    	prManager.setOffsetNormalTop(0D);
    	prManager.setOffsetZoomLeft(0D);
    	prManager.setOffsetZoomTop(0D);
    	if(pManager.isInMagnifierView()) {
    		selectedCol = null;
    		selectedColSecond = null;
    		pManager.setInMagnifierView(false);
    	}
    	
    	repaint();
    }
    
    public static Comparator<YearData> HighestPoint = new Comparator<YearData>() {

    	@Override
		public int compare(YearData arg0, YearData arg1) {
			List<Data> arg0L = arg0.data;
			List<Data> arg1L = arg1.data;
			
			return (int) (calculateAverage(arg1L) - calculateAverage(arg0L));
		}
	};
	
	public class ValueStack{
		public Double[] values;
		public Color color;
		public int column;
		
		public ValueStack(Double[] values, Color color, int column) {
			this.values = values;
			this.color = color;
			this.column = column;
		}
	}
	
	private static double calculateAverage(List <Data> marks) {
		  Double sum = 0D;
		  if(!marks.isEmpty()) {
		    for (Data mark : marks) {
		        sum += mark.getData();
		    }
		    return sum / marks.size();
		  }
		  return sum;
		}
    
	private double findBiggestValue(ArrayList<ValueStack> valueStack) {
		double biggest = Double.MIN_VALUE;
		for(int vs = 0; vs < valueStack.size(); vs++) {
			Double[] values = valueStack.get(vs).values;
			double max = Collections.max(Arrays.asList(values));
			if(max > biggest) {
				biggest = max;
			}
		}
		return biggest;
	}
	public void addPathToSelected(String type, GeneralPath gp, int col) {
		if(selectedCol != null && selectedCol.getOrdinal() == col) {
			selectedCol.setType(type);
			selectedCol.addPath(gp);
		}
	}
	public void addPathToSecondSelected(String type, GeneralPath gp, int col) {
		if(selectedColSecond != null && selectedColSecond.getOrdinal() == col) {
			selectedColSecond.setType(type);
			selectedColSecond.addPath(gp);
		}
	}
	
	public class SelectedColumn{
		private ArrayList<GeneralPath> path = new ArrayList<>();
		int ordinal;
		String type;

		public SelectedColumn(int ordinal) {
			this.ordinal = ordinal;
		}
		
		public ArrayList<GeneralPath> getPath() {
			return path;
		}

		public void addPath(GeneralPath path) {
			if(this.path.size()  > pManager.getLines().size() - 1) {
				return;
			}
			this.path.add(path);
		}
		
		public void cleanPaths() {
			path = new ArrayList<>();
		}

		public int getOrdinal() {
			return ordinal;
		}

		public void setOrdinal(int ordinal) {
			this.ordinal = ordinal;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}
	}
	
	public void flushSelected() {
		selectedCol = null;
		selectedColSecond = null;
	}

	public void flushPaths() {
		if(selectedCol != null) {
			selectedCol.cleanPaths();
		}
	}
	
	public void magnifier(Double xDiff, Double yDiff) {	
		if(pManager.isInMagnifierView()) {
			return;
		}
		Double yGap = (double)Math.abs(yDiff.intValue()) / 10D;
		Double xGap = (double)Math.abs(xDiff.intValue()) / 10D;

		  
		ActionListener action = new ActionListener() {
			int i = 0;
            @Override
            public void actionPerformed(ActionEvent e) {
				
	  			  if(xDiff < 0) {
	  				  prManager.setOffsetNormalLeft(prManager.getOffsetNormalLeft() - xGap * 3);   
	  			  }else {
	  				  prManager.setOffsetNormalLeft(prManager.getOffsetNormalLeft() + xGap * 3); 
	  			  }
	  			  
	  			  repaint();
	              if(i == 10) {
	            	((Timer)e.getSource()).stop();
	              }
	              i++;
	            	
	            }
            	
        };
        Timer t = new Timer(50, action);
        t.setRepeats(true);
        t.setInitialDelay(0);
        t.start();

    	pManager.magnifierView();
    	pManager.setInMagnifierView(!pManager.isInMagnifierView());

		flushSelected();
		repaint();
        
	}
	
	private class YearData{
		public ArrayList<Data> data;
		public Color color;
		public int column;
		
		public YearData(ArrayList<Data> data, Color color, int column) {
			this.data = data;
			this.color = color;
			this.column = column;
		}
	}
	
}
