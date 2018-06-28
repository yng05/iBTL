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
   private DataManager dManager = new DataManager("hicp-2015-100--monthly-data-annu (5).csv");
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
        	  try {
  				Robot r = new Robot();
  				canvas.pickColumn(r.getPixelColor(evt.getX(), evt.getY() + TOP_BUFFER));

	  			} catch (AWTException e) {
	  				
	  				e.printStackTrace();
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
        		prManager.setOffsetLeft(prManager.getOffsetLeft() + xDiff);        			
        		        		
        		xFirst = xDest;
        	}
        	if(yFirst > -1) {
        		int yDiff = yDest - yFirst;
        		prManager.setOffsetTop(prManager.getOffsetTop() + yDiff);
        		yFirst = yDest;        		
        	}
        	if(xFirst == -1 && yFirst == -1){
        		pManager.moveTheLine(xDest / prManager.getZoom(), prManager.getOffsetLeft() / prManager.getZoom(),prManager.isAccordion());
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
				prManager.setZoom(prManager.getZoom() + 0.1);
				Double zoom = prManager.getZoom();
				int scW = canvas.getWidth();
				int scH = canvas.getHeight();
				Double offL = prManager.getOffsetLeft();
				Double offT = prManager.getOffsetTop();
				offL -= (zoom*scW - (zoom - 0.1)*scW)/2;
				offT -= (zoom*scH - (zoom - 0.1)*scH)/2;
				prManager.setOffsetLeft(offL);
				prManager.setOffsetTop(offT);
				repaint();
			}else if (arg0.getWheelRotation() == 1) {
				if(prManager.getZoom() - 0.1 == 0.9) {
					return;
				}
				prManager.setZoom(prManager.getZoom() - 0.1);
				Double zoom = prManager.getZoom();
				int scW = canvas.getWidth();
				int scH = canvas.getHeight();
				Double offL = prManager.getOffsetLeft();
				Double offT = prManager.getOffsetTop();
				offL -= (zoom*scW - (zoom + 0.1)*scW)/2;
				offT -= (zoom*scH - (zoom + 0.1)*scH)/2;
				prManager.setOffsetLeft(offL);
				prManager.setOffsetTop(offT);
				repaint();
			}
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