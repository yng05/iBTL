package drawing;
import java.awt.*;       
import java.awt.event.*; 
import java.awt.geom.Line2D;
import java.util.ArrayList;

import javax.swing.*;

import data.DataManager;
import data.PreferencesManager;
import drawing.tools.*;
import interaction.Dashboard;

public class Window extends JFrame {  
   private final String APPLICATION_NAME = "inBetweenTheLines";
   
   private DrawingCanvas canvas;
   private Dashboard dashboard;
   private PositionManager pManager = new PositionManager();
   private DataManager dManager = new DataManager("finaldata.csv");
   private PreferencesManager prManager = new PreferencesManager();
   private int xFirst = -1;
   private int yFirst = -1;
   private final int TOP_BUFFER = 59;
   private double BOTTOM_LINE_END;
   
   
   public Window() {
	  init();
      
      Container cp = getContentPane();
      cp.setLayout(new FlowLayout());
      
      canvas = new DrawingCanvas(pManager, dManager, prManager);
      cp.add(canvas);
      
      dashboard = new Dashboard(canvas, prManager, dManager);
      cp.add(dashboard);
      
   
      canvas.addMouseListener(new MouseAdapter() {
          @Override
          public void mouseClicked(MouseEvent evt) {
        	  if(evt.getClickCount() == 1) {
            	  try {
        				Robot r = new Robot();
        				canvas.pickColumn(r.getPixelColor(evt.getX(), evt.getY() + TOP_BUFFER));

      	  			} catch (AWTException e) {
      	  				
      	  				e.printStackTrace();
      	  			}          		  
        	  }if(evt.getClickCount() == 2) {
        		  Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        		  Double xCenter = screenSize.getWidth() / 2;
        		  Double yCenter = (screenSize.getHeight() - 400D) / 2;
        		  
        		  Double xClicked = (double)evt.getX();
        		  Double yClicked = (double)evt.getY();
        		  
        		  Double xDiff = xCenter - xClicked;
        		  Double yDiff = yCenter - yClicked;
        		  
        		  Double zoomDiff = 3.0D - prManager.getZoom();
        		  
        		  canvas.magnifier(xDiff, yDiff);
        		 
        	  }    	  
          }
          @Override
          public void mousePressed(MouseEvent evt) {
        	 pManager.detectTheLine(evt.getX(), prManager.getOffsetLeft(), prManager.getZoom());
        	 if(pManager.getMarkedLine() == -1) {
        		 xFirst = evt.getX();
        		 yFirst = evt.getY();
        	 }
             canvas.getgManager().detectGraph((int)evt.getX(), (int)evt.getY());
          }
          @Override
          public void mouseReleased(MouseEvent evt) {
        	 pManager.releaseTheLine();
        	 xFirst = -1;
        	 yFirst = -1;
          }          
       });

      canvas.addMouseMotionListener(new MouseMotionAdapter() {
         @Override
         public void mouseDragged(MouseEvent evt) {
        	int xDest = evt.getX();
        	int yDest = evt.getY();
        	if(xFirst > -1) {
        		int xDiff = xDest - xFirst;
        		prManager.setOffsetNormalLeft(prManager.getOffsetNormalLeft() + xDiff);        			
        		        		
        		xFirst = xDest;
        	}
        	
        															
        	if(yFirst > -1) {
        		int yDiff = yDest - yFirst;
        		prManager.setOffsetNormalTop(prManager.getOffsetNormalTop() + yDiff);
        		yFirst = yDest;        		
        	}
        	
        	if(xFirst == -1 && yFirst == -1){
        		pManager.moveTheLine(xDest / prManager.getZoom(), prManager.getOffsetNormalLeft() / prManager.getZoom(),prManager.isAccordion());
            }
        	canvas.flushPaths();

            repaint();  
         }

         public void mouseMoved(MouseEvent evt) {
        	try {
				Robot r = new Robot();
				canvas.printTitle(r.getPixelColor(evt.getX(), evt.getY() + TOP_BUFFER), evt.getX(), evt.getY());

			} catch (AWTException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
         }
      });
      
      canvas.addMouseWheelListener(new MouseWheelListener() {

		@Override
		public void mouseWheelMoved(MouseWheelEvent arg0) {
			// TODO Auto-generated method stub
			if(arg0.getWheelRotation() == -1) {
				if(prManager.getZoom() + 0.1 > 3) {
					return;
				}				
			}else if (arg0.getWheelRotation() == 1) {
				if(prManager.getZoom() - 0.1 == 0.9) {
					return;
				}				
			}	
			
			prManager.setZoom(prManager.getZoom() + (0.1 * arg0.getWheelRotation() * -1));
			Double zoom = prManager.getZoom();
			int scW = canvas.getWidth();
			int scH = canvas.getHeight();
			Double offL = 0D;
			if(prManager.getOffsetNormalLeft() > 0) {
				 offL = -scW * (zoom - 1.0) / ((scW) / (scW/2 - prManager.getOffsetNormalLeft()));
			}else {
				offL = -scW * (zoom - 1.0) / ((scW) / (scW/2 - (prManager.getOffsetNormalLeft()) + -1));
			}
			Double offT = 0D;
			if(prManager.getOffsetNormalLeft() > 0) {
				offT = -scH * (zoom - 1.0) / ((scH) / (scH/2 - prManager.getOffsetNormalTop()));
			}else {
				offT = -scH * (zoom - 1.0) / ((scH) / (scH/2 - (prManager.getOffsetNormalTop()) + -1));
			}
			
			prManager.setOffsetZoomLeft(offL);
			prManager.setOffsetZoomTop(offT);
			repaint();
		}
          
       });
      

      
      setExtendedState(JFrame.MAXIMIZED_BOTH); 
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  
      setTitle(APPLICATION_NAME); 
      setVisible(true);          
	      
  }
   
  public void 	init() {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		BOTTOM_LINE_END = screenSize.getHeight() - 400D;
  }

}