package interaction;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import data.DataManager;
import data.DataManager.Column;
import data.PreferencesManager;
import drawing.tools.DrawingCanvas;
import drawing.tools.GraphManager;
import drawing.tools.PositionManager;

public class Dashboard extends JPanel{
	DrawingCanvas canvas;
	PreferencesManager prManager;
	DataManager dManager;
	
	private Double frameW  = 640D;
    private Double frameH= 480D;
 
    private static final Color BACKGROUND = Color.WHITE;
    
    private double TOP_LINE_END = 1080D - 400D;
    private double BOTTOM_LINE_END = 1080D - 20D;
    
    JCheckBox cbAccordion = new JCheckBox(new CheckboxAction("Accordion Off"));
    
    JRadioButton optionLine = new JRadioButton("Line");
    JRadioButton optionArea = new JRadioButton("Area");
    JRadioButton optionStackedB = new JRadioButton("Stacked(Baseline)");
    JRadioButton optionStackedC = new JRadioButton("Stacked(Centered)");
    JRadioButton optionBraided = new JRadioButton("Braided");
    JRadioButton optionSmallMultiples = new JRadioButton("Small Multiples");
    
    JRadioButton optionLA = new JRadioButton("Line/Area");
    JRadioButton optionLB = new JRadioButton("Line/Braided");
    JRadioButton optionLSC = new JRadioButton("Line/Stacked(Centered)");
    JRadioButton optionLSB = new JRadioButton("Line/Stacked(Baseline)");
    
    JRadioButton optionAB = new JRadioButton("Area/Braided");
    JRadioButton optionASC = new JRadioButton("Area/Stacked(Centered)");
    JRadioButton optionASB = new JRadioButton("Area/Stacked(Baseline)");    

    JRadioButton optionBSC = new JRadioButton("Braided/Stacked(Centered)");
    JRadioButton optionBSB = new JRadioButton("Braided/Stacked(Baseline)");
    
    JRadioButton optionSCSB = new JRadioButton("Stacked(Centered)/Stacked(Baseline)");
    
    
    
    ButtonGroup groupType = new ButtonGroup();
    
    JButton btnReset = new JButton("Reset");
	
	public Dashboard(DrawingCanvas canvas, PreferencesManager prManager, DataManager dManager) {
		this.canvas = canvas;
		this.prManager = prManager;
		this.dManager = dManager;
		
		frameW = 1920D;
		frameH = 400D;
    	setPreferredSize(new Dimension((int)Math.round(frameW), (int)Math.round(frameH)));

    	add(cbAccordion);
    	
    	groupType.add(optionLine);
    	groupType.add(optionArea);
    	groupType.add(optionStackedC);
    	groupType.add(optionStackedB);
    	groupType.add(optionBraided);
    	groupType.add(optionSmallMultiples);
    	
    	groupType.add(optionLA);
    	groupType.add(optionLB);    	
    	groupType.add(optionLSC);
    	groupType.add(optionLSB);
    	
    	groupType.add(optionAB);
    	groupType.add(optionASC);
    	groupType.add(optionASB);
    	
    	groupType.add(optionBSC);
    	groupType.add(optionBSB);
    	
    	groupType.add(optionSCSB);
    	
    	RadioButtonActionListener actionListener = new RadioButtonActionListener();
    	
    	optionLine.setSelected(true);
    	optionLine.addActionListener(actionListener);
    	optionArea.addActionListener(actionListener);
    	optionStackedC.addActionListener(actionListener);
    	optionStackedB.addActionListener(actionListener);
    	optionBraided.addActionListener(actionListener);
    	optionSmallMultiples.addActionListener(actionListener);
    	
    	optionLA.addActionListener(actionListener);
    	optionLB.addActionListener(actionListener);    	
    	optionLSC.addActionListener(actionListener);
    	optionLSB.addActionListener(actionListener);
    	
    	optionAB.addActionListener(actionListener);
    	optionASC.addActionListener(actionListener);
    	optionASB.addActionListener(actionListener);
    	
    	optionBSC.addActionListener(actionListener);
    	optionBSB.addActionListener(actionListener);
    	
    	optionSCSB.addActionListener(actionListener);   	
    	
    	
    	add(optionLine);
    	add(optionArea);
    	add(optionStackedC);
    	add(optionStackedB);
    	add(optionBraided);
    	add(optionSmallMultiples);
    	
    	add(optionLA);
    	add(optionLB);
    	add(optionLSC);
    	add(optionLSB);
    	
    	add(optionAB);
    	add(optionASC);
    	add(optionASB);
    	
    	add(optionBSC);
    	add(optionBSB);
    	
    	add(optionSCSB);
    	
    	btnReset.addActionListener(new ActionListener() {
    	    @Override
    	    public void actionPerformed(ActionEvent evt) {
    	        canvas.reset();
    	    }
    	});
    	
    	add(btnReset);
    	
    	for(int d=0; d<dManager.getColumns().size(); d++) {
        	JCheckBox cBox = new JCheckBox(dManager.getColumns().get(d).getName());    	
    	    cBox.addActionListener(actionListenerCBox);
    	    cBox.setSelected(true);
    	    add(cBox);
    	}
    	
	    prManager.addStyle("Line");
	   
	}
	
	ActionListener actionListenerCBox = new ActionListener() {
	      public void actionPerformed(ActionEvent actionEvent) {
	    	JCheckBox abstractButton = (JCheckBox) actionEvent.getSource();
	        String selected = abstractButton.getText();	        

	    	for(int d=0; d<dManager.getColumns().size(); d++) {
	    		Column column = dManager.getColumns().get(d);
	    		if(column.getName().equals(selected)) {
	    			column.setActive(((AbstractButton) actionEvent.getSource()).getModel().isSelected());
	    		}
	    	}
	    	
	    	if(abstractButton.isSelected()) {
	    		prManager.dItDown();
	    	}else {
	    		prManager.dItUp();
	    	}

	    	canvas.repaint();
	      }
    };
	
	class CheckboxAction extends AbstractAction {
	    public CheckboxAction(String text) {
	        super(text);
	    }
	 
	    @Override
	    public void actionPerformed(ActionEvent e) {
	        JCheckBox cb = (JCheckBox) e.getSource();
	        if (cb.isSelected()) {
	        	cb.setText("Accordion On");
	        } else {
	        	cb.setText("Accordion Off");
	        }
	        

            prManager.setAccordion(cb.isSelected());
	    }
	}
	
	class RadioButtonActionListener implements ActionListener {
	    @Override
	    public void actionPerformed(ActionEvent event) {
	        JRadioButton button = (JRadioButton) event.getSource();
	        prManager.resetStyle();
	        if (button == optionLine) {	 
	            prManager.addStyle("Line");
	        } else if (button == optionArea) {
	            prManager.addStyle("Area");     
	        }  else if (button == optionStackedB) {
	            prManager.addStyle("Stacked(Baseline)");    
	        } else if (button == optionStackedC) {
	            prManager.addStyle("Stacked(Centered)");    
	        } else if (button == optionBraided) {
	            prManager.addStyle("Braided");     
	        }  else if (button == optionSmallMultiples) {
	            prManager.addStyle("Small Multiples");    
	        }   else if (button == optionLA) {
	            prManager.addStyle("Line");  
	            prManager.addStyle("Area");   
	        }else if (button == optionLB) {
	            prManager.addStyle("Line");  
	            prManager.addStyle("Braided");   
	        }else if (button == optionLSB) {
	            prManager.addStyle("Line");  
	            prManager.addStyle("Stacked(Baseline)");    
	        }  
	        else if (button == optionLSC) {
	            prManager.addStyle("Line");  
	            prManager.addStyle("Stacked(Centered)");    
	        }else if (button == optionAB) {
	            prManager.addStyle("Area");  
	            prManager.addStyle("Braided");   
	        }else if (button == optionASB) {
	            prManager.addStyle("Area");  
	            prManager.addStyle("Stacked(Baseline)");    
	        }  
	        else if (button == optionASC) {
	            prManager.addStyle("Area");  
	            prManager.addStyle("Stacked(Centered)");    
	        }else if (button == optionBSB) {
	            prManager.addStyle("Braided");  
	            prManager.addStyle("Stacked(Baseline)");   
	        }else if (button == optionBSC) {
	            prManager.addStyle("Braided");  
	            prManager.addStyle("Stacked(Centered)");    
	        }  
	        else if (button == optionSCSB) {
	            prManager.addStyle("Stacked(Centered)");  
	            prManager.addStyle("Stacked(Baseline)");    
	        }  
	        canvas.flushSelected();
	        canvas.repaint();
	    }
	}
	
}
