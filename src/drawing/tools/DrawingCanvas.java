package drawing.tools;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
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

import data.DataManager;
import data.DataManager.Column;
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
    
    public GraphManager getgManager() {
		return gManager;
	}


	public DrawingCanvas(PositionManager pManager, DataManager dManager, PreferencesManager prManager) {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		canvasW = screenSize.getWidth();
		canvasH = screenSize.getHeight() - 400D;
//		canvasW = 1920D;
//		canvasH = 1080D - 400D;
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
        //Graphics2D g2d = (Graphics2D) g;
        setBackground(CANVAS_BACKGROUND);  // set background color for this JPanel
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
        
        for(int l = 0; l<lines.size();l++) {         
	        if(prManager.getStyle().equals("Line") || prManager.getStyle().equals("Area") || prManager.getStyle().equals("Small Multiples")) {			        	
	        	ArrayList<Column> columns = dManager.getColumns();
	        	for(int c = 0; c < columns.size() -1; c++) {
	        		Double[] values = new Double[13];
	        		for(int m = 0; m < 12; m++) {
	        			int pos = l*12 + m;
	        			Double value = columns.get(c).getData()[pos]; 
	
	            		if(value == null) {
	            			values[m] = 0D;
	            		}else {
	            			values[m] = value; 
	            		}
	        		}
	        		int posFirstOfNext = (l+1)*12;
	        		Double valNext = null;
	        		if(posFirstOfNext < dManager.getDates().size()) {
	        			valNext = columns.get(c).getData()[posFirstOfNext];
	        		}
	        		
	        		if(valNext == null) {
	        			values[12] = 0D;
	        		}else {
	        			values[12] = valNext; 
	        		}
	        		
	        		g2D.setColor(colors[c]);
	        		g2D.setStroke(new BasicStroke(3));
	        		
	        		if(prManager.getStyle().equals("Line")) {
	        			if(!columns.get(c).isActive()) {
	        				continue;
	        			}	        
	        			GeneralPath path = gManager.createSimpleLineGraph(l, values);
	        			if(path != null) {
		        			g2D.draw(path);		        				
	        			}
	        			addPathToSelected("Draw", path, c);
	        		}else if(prManager.getStyle().equals("Area")) {
	        			if(!columns.get(c).isActive()) {
	        				continue;
	        			}	        
	        			GeneralPath path = gManager.createStraightField(l, values);
	        			if(path != null) {
		        			g2D.fill(path);		        				
	        			}
	        			addPathToSelected("Fill", path, c);
	        		}else if(prManager.getStyle().equals("Small Multiples")) {
	        			GeneralPath path = gManager.createSmallMultiples(l, values, columns.size(),c);
	        			if(path != null) {
		        			g2D.fill(path);		        				
	        			}
	        			addPathToSelected("Fill", path, c);
	        		}
	        	}  
	        }
	        else if(prManager.getStyle().equals("Stacked(Baseline)")) {
	        		int deactivatedItems = prManager.getDeactivatedItems();
	        		ArrayList<ValueStack> valStack = new ArrayList<>();
	        		ArrayList<Column> columns = dManager.getColumns();
		        	for(int c = 0; c < columns.size() -1; c++) {
		        		Double[] prevVals = {0D,0D,0D,0D,0D,0D,0D,0D,0D,0D,0D,0D,0D};
		        		if(c > 0) {
		        			prevVals = valStack.get(c).values;
		        			
		        		}else {
		        			valStack.add(new ValueStack(prevVals, null, -1));
		        		}
		        		
		        		
		        		Double[] values = new Double[13];
		        		for(int m = 0; m < 12; m++) {
		        			int pos = l*12 + m;
		        			Double value = columns.get(c).getData()[pos]; 
		
		            		if(value == null || !columns.get(c).isActive()) {
		            			values[m] = 0D + prevVals[m];
		            		}else {
		            			values[m] = value + prevVals[m]; 
		            		}
		        		}
		        		int posFirstOfNext = (l+1)*12;	        		
		        		Double valNext = null;
		        		if(posFirstOfNext < dManager.getDates().size()) {
		        			valNext = columns.get(c).getData()[posFirstOfNext];
		        		}
		        		if(valNext == null || !columns.get(c).isActive()) {
		        			values[12] = 0D + prevVals[12];
		        		}else {
		        			values[12] = valNext + prevVals[12]; 
		        		}  	
		        		
        				valStack.add(new ValueStack(values, colors[c], c));
		        		
	        			
		        	}
		        	double max = findBiggestValue(valStack);
		        	double ratio = 1;
		        	if(max > dManager.getMax()) {
		        		ratio = 5;
		        	}
		        	for(int vs = 1; vs < valStack.size(); vs++) {
		        		g2D.setColor(valStack.get(vs).color);
		        		g2D.setStroke(new BasicStroke(3));
		        		
		        		GeneralPath path = gManager.createStreamGraph(l, valStack.get(vs).values, valStack.get(vs - 1).values, ratio, false);
	        			if(path != null) {
		        			g2D.fill(path);		        				
	        			}
	        			addPathToSelected("Fill", path, vs - 1);
		        	}
	        		
	        } 
	        else if(prManager.getStyle().equals("Stacked(Centered)")) {
        		int deactivatedItems = 0;
        		ArrayList<ValueStack> valStack = new ArrayList<>();
        		ArrayList<Column> columns = dManager.getColumns();
        		boolean isEven = false;
        		int center = columns.size()/2 + 1;
        		if(columns.size()%2==0) {
        			isEven = true;
        			center = columns.size()/2;
        		}
        		
	        	for(int c = 0; c < columns.size() -1; c++) {
	        		Double[] prevVals = {0D,0D,0D,0D,0D,0D,0D,0D,0D,0D,0D,0D,0D};
	        		if(c == 0 || c == center) {
	        			valStack.add(new ValueStack(prevVals, null, -1));	        			
	        		}else {
	        			if(c > center) {
		        			prevVals = valStack.get(c + 1).values;	        				
	        			}else if(c < center) {
		        			prevVals = valStack.get(c).values;	        				
	        			}
	        		}
	        		
	        		Double[] values = new Double[13];
	        		for(int m = 0; m < 12; m++) {
	        			int pos = l*12 + m;
	        			Double value = columns.get(c).getData()[pos];    
	
	            		if(value == null || !columns.get(c).isActive()) {
		            			values[m] = prevVals[m];
	            		}else {
	            			if(c < center) {
		            			values[m] = value + prevVals[m]; 	            				
	            			}else if(c >= center) {
	            				values[m] = prevVals[m] - value; 	   
	            			}
	            		}
	        		}
	        		int posFirstOfNext = (l+1)*12;	        		
	        		Double valNext = null;
	        		if(posFirstOfNext < dManager.getDates().size()) {
	        			valNext = columns.get(c).getData()[posFirstOfNext];
	        		}
	        		if(valNext == null || !columns.get(c).isActive()) {            			
            				values[12] = prevVals[12];  	
	        		}else {
            			if(c < center) {
            				values[12] = valNext + prevVals[12]; 	            				
            			}else if(c >= center) {
            				values[12] = prevVals[12] - valNext;  	   
            			}
	        			
	        		}  	
	        		
        			valStack.add(new ValueStack(values, colors[c], c));
        			
	        	}
        		int ratio = 8;
	        	for(int vs = 1; vs < valStack.size() - 1; vs++) {
	        		if(vs == center + 1) {
	        			continue;
	        		}
	        		
	        		if(vs > center + 1) {
		        		g2D.setColor(valStack.get(vs + 1).color);        				
        			}else if(vs < center) {
		        		g2D.setColor(valStack.get(vs).color);           				
        			}
	        		g2D.setStroke(new BasicStroke(3));
	        		GeneralPath path = gManager.createStreamGraph(l, valStack.get(vs).values, valStack.get(vs - 1).values, ratio, true);
        			if(path != null) {
	        			g2D.fill(path);		        				
        			}

        			addPathToSelected("Fill", path, vs - 1);
	        	}
        		
	        } 
	        else if(prManager.getStyle().equals("Braided")) {
        		ArrayList<ValueStack> valStack = new ArrayList<>();
        		ArrayList<Column> columns = dManager.getColumns();
	        	for(int c = 0; c < columns.size() -1; c++) {
        			if(!columns.get(c).isActive()) {
        				continue;
        			}	
	        		
	        		Double[] values = new Double[13];
	        		for(int m = 0; m < 12; m++) {
	        			int pos = l*12 + m;
	        			Double value = columns.get(c).getData()[pos];    
	
	            		if(value == null) {
	            			values[m] = 0D;
	            		}else {
	            			values[m] = value; 
	            		}
	        		}
	        		int posFirstOfNext = (l+1)*12;
	        		Double valNext = null;
	        		if(posFirstOfNext < dManager.getDates().size()) {
	        			valNext = columns.get(c).getData()[posFirstOfNext];
	        		}  
	        		if(valNext == null) {
	        			values[12] = 0D;
	        		}else {
	        			values[12] = valNext; 
	        		}
	        		
        			valStack.add(new ValueStack(values, colors[c], c));
	        	}
	        	Collections.sort(valStack, HighestPoint);
	        	
	        	for(int vs = 0; vs < valStack.size(); vs++) {
	        		g2D.setColor(valStack.get(vs).color);
	        		g2D.setStroke(new BasicStroke(3));        
	        		
	        		GeneralPath path = gManager.createStraightField(l, valStack.get(vs).values);
	    			if(path != null) {
	        			g2D.fill(path);		
	    			}

        			addPathToSelected("Fill", path, valStack.get(vs).column);	
    			}
	        }	 
	        
	        g2D.setStroke(new BasicStroke(1));
        	g2D.setColor(Color.RED);
        	if(l< lines.size()-1) {
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
            	        g2D.setStroke(new BasicStroke(2));
            			g2D.draw(selectedCol.getPath().get(i));
            	        g2D.setColor(new Color(153, 0, 51, 70));
            			g2D.fill(selectedCol.getPath().get(i));
            		}
            	}
            }
            g2D.setColor(LINE_COLOR);
        	g2D.setStroke(new BasicStroke(3));
        	g2D.drawLine((int)lines.get(l).getX1(), (int)lines.get(l).getY1(), (int)lines.get(l).getX2(), (int)lines.get(l).getY2()); // Draw the line
        	g2D.drawString(dManager.getYears().get(l), (int)lines.get(l).getX1() - 20, (int)lines.get(l).getY2() + 20);

        }  
    	g2D.setStroke(new BasicStroke(1));

    	if(!prManager.getStyle().equals("Small Multiples") && !prManager.getStyle().equals("Stacked(Baseline)") && !prManager.getStyle().equals("Stacked(Centered)")) {
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
    	}
    	
        g2D.drawRect(infoPanelX + 10, infoPanelY - 20, 100, 20);
        g2D.setColor(new Color(255, 255, 204));
        g2D.fillRect(infoPanelX + 10, infoPanelY - 20, 99, 19);
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
    			repaint();
				return;
			}
    	}	
		selectedCol = null;
		repaint();
	}
	
	public void doPositioning() {
		ArrayList<Column> columns = dManager.getColumns();	
		double gapSize = (canvasW - 100D) / (dManager.getYears().size()-1);
		
		if(pManager.getLines().size() != 0) {
			pManager.getLines().clear();
			pManager.getMonthsOfYears().clear();
		}
		
		for(int i = 0; i < dManager.getYears().size(); i++) {
			pManager.addLine(new Line2D.Double(TOP_LINE_END + i * gapSize, TOP_LINE_END, TOP_LINE_END + i * gapSize, BOTTOM_LINE_END), (String)columns.get(i).getName());
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
    	prManager.setOffsetLeft(0D);
    	prManager.setOffsetTop(0D);
    	repaint();
    }
    
    public static Comparator<ValueStack> HighestPoint = new Comparator<ValueStack>() {

    	@Override
		public int compare(ValueStack arg0, ValueStack arg1) {
			List<Double> arg0L = Arrays.asList(arg0.values);
			List<Double> arg1L = Arrays.asList(arg1.values);
			
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
	
	private static double calculateAverage(List <Double> marks) {
		  Double sum = 0D;
		  if(!marks.isEmpty()) {
		    for (Double mark : marks) {
		        sum += mark;
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
	}

	public void flushPaths() {
		if(selectedCol != null) {
			selectedCol.cleanPaths();
		}
	}
	
}
