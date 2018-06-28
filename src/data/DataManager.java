package data;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class DataManager {
	private String path;
	private String packagePath = "/src/data/";

	private ArrayList<Column> columns = new ArrayList<>();
	private ArrayList<String> dates = new ArrayList<>();

	private Double min;
	private Double max;

	
	
    
	public DataManager(String name) {
		String current = null;
		try {
			current = new java.io.File( "." ).getCanonicalPath();
		} catch (IOException e) {
			e.printStackTrace();
		}
		path = current + packagePath + name;
		
		init();
	}
	
	public void init() {
		BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ";";

        try {

            br = new BufferedReader(new FileReader(path));
            int i = 0;
            int columnAmount = 0;
            ArrayList<String[]> rows = new ArrayList<>();
            while ((line = br.readLine()) != null) {
            	line = line.replace("?", "");
            	line = line.replace("\"", "");
                String[] parts = line.split(cvsSplitBy);                
                rows.add(parts);
            }
            
            for(int r = 0; r < rows.size(); r++) {
            	if(r == 0) {
            		for(int d = 1; d < rows.get(0).length; d++) {
            			columns.add(new Column(rows.get(0)[d]));
            		}
            	}else {
            		dates.add(rows.get(r)[0]);
            		for(int d = 1; d < rows.get(r).length; d++) {
            			String rec = rows.get(r)[d];
            			Double value;
        				if(rec == null || rec.isEmpty() || rec.equals("") || rec.equals("-")) {
        					value = 0.0;
        				}else {
            				value = Double.parseDouble(rec);        					
        				}
        				
        				
        				if(max == null) {
        					max = value;
        				}
        				if(min == null) {
        					min = value;
        				}
        				
        				if(value < min) {
        					min = value;
        				}
        				
        				if(value > max) {
        					max = value;
        				}
        				
            			columns.get(d - 1).addData(value);
            		}
            	}         	
            
            }
            
            return;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
	}
	
	public ArrayList<Column> getColumns() {
		return columns;
	}

	public void setColumns(ArrayList<Column> columns) {
		this.columns = columns;
	}

	public ArrayList<String> getDates() {
		return dates;
	}

	public void setDates(ArrayList<String> dates) {
		this.dates = dates;
	}
	
	public ArrayList<String> getYears(){
		ArrayList<String> years = new ArrayList<>();
		for(int i = 0;i < dates.size(); i+=12) {
			String[] parts = dates.get(i).split("-");
			if(!parts[0].isEmpty()){
				years.add(parts[0]);				
			}
		}
		return years;
	}
	

	public Double getMin() {
		return min;
	}

	public Double getMax() {
		return max;
	}
	
	public class Column{	
		String name;
		ArrayList<Double> data = new ArrayList<>();
		boolean active = true;
		Color color;
		
		public Column(String name){
			this.name = name;		
		}	

		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public Double[] getData() {
			Double[] returnArray = new Double[data.size()];
			returnArray = data.toArray(returnArray);
			return returnArray;
		}
		public void addData(Double data) {
			this.data.add(data);
		}
		public boolean isActive() {
			return active;
		}
		public void setActive(boolean active) {
			this.active = active;
		}

		public Color getColor() {
			return color;
		}

		public void setColor(Color color) {
			this.color = color;
		}
		
	}

	
}
