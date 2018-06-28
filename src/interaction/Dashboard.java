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
    	RadioButtonActionListener actionListener = new RadioButtonActionListener();
    	
    	optionLine.setSelected(true);
    	optionLine.addActionListener(actionListener);
    	optionArea.addActionListener(actionListener);
    	optionStackedC.addActionListener(actionListener);
    	optionStackedB.addActionListener(actionListener);
    	optionBraided.addActionListener(actionListener);
    	optionSmallMultiples.addActionListener(actionListener);
    	add(optionLine);
    	add(optionArea);
    	add(optionStackedC);
    	add(optionStackedB);
    	add(optionBraided);
    	add(optionSmallMultiples);
    	
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
	 
	        if (button == optionLine) {	 
	            prManager.setStyle("Line");
	        } else if (button == optionArea) {
	            prManager.setStyle("Area");     
	        }  else if (button == optionStackedB) {
	            prManager.setStyle("Stacked(Baseline)");    
	        } else if (button == optionStackedC) {
	            prManager.setStyle("Stacked(Centered)");    
	        } else if (button == optionBraided) {
	            prManager.setStyle("Braided");     
	        }  else if (button == optionSmallMultiples) {
	            prManager.setStyle("Small Multiples");    
	        } 
	        
	        canvas.flushSelected();
	        canvas.repaint();
	    }
	}
	
}
