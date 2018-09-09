package data;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class DataManager {
	private String path;
	private String packagePath = "/src/data/";

	private ArrayList<Column> columns = new ArrayList<>();
	private ArrayList<String> dates = new ArrayList<>();
	private ArrayList<String> years = new ArrayList<>();

	private Double min = 0.0;
	private Double max = 0.0;

	
	
    
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
	
	public ArrayList<Data> getYearData(int col, int year){
		ArrayList<Data> yearData = new ArrayList<>();
		for(int d = 0; d < columns.get(col).getData().size(); d++) {
			if(year == columns.get(col).getData().get(d).getYear()) {
				Data data = new Data(columns.get(col).getData().get(d).data, columns.get(col).getData().get(d).year, columns.get(col).getData().get(d).month, columns.get(col).getData().get(d).day);
				int offset = 0;
				while(data.getData() == 0D && d - offset > 0) {
					offset++;
					data = new Data(columns.get(col).getData().get(d - offset).data, columns.get(col).getData().get(d).year, columns.get(col).getData().get(d).month, columns.get(col).getData().get(d).day);
				}
				yearData.add(data);
			}
			if(year +1 == columns.get(col).getData().get(d).getYear()){
				Data data = new Data(columns.get(col).getData().get(d).data, columns.get(col).getData().get(d).year, columns.get(col).getData().get(d).month, columns.get(col).getData().get(d).day);
				yearData.add(data);
				break;
			}
		}
		
		return yearData;
	}
	
	public ArrayList<Data> getNegativeYearData(int col, int year){		
		ArrayList<Data> yearData = new ArrayList<>();
		for(int d = 0; d < columns.get(col).getData().size(); d++) {
			if(year == columns.get(col).getData().get(d).getYear()) {
				Data data = new Data((-1) * columns.get(col).getData().get(d).data, columns.get(col).getData().get(d).year, columns.get(col).getData().get(d).month, columns.get(col).getData().get(d).day);
				int offset = 0;
				while(data.getData() == 0D && d - offset > 0) {
					offset++;
					data = new Data((-1) * columns.get(col).getData().get(d - offset).data, columns.get(col).getData().get(d).year, columns.get(col).getData().get(d).month, columns.get(col).getData().get(d).day);
				}
				yearData.add(data);
			}
			if(year +1 == columns.get(col).getData().get(d).getYear()){
				Data data = new Data((-1) * columns.get(col).getData().get(d).data, columns.get(col).getData().get(d).year, columns.get(col).getData().get(d).month, columns.get(col).getData().get(d).day);
				yearData.add(data);
				break;
			}
		}
		
		return yearData;
	}
	
	public void init() {
		BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ";";
        String year = "";

        try {

            br = new BufferedReader(new FileReader(path));
            int i = 0;
            int columnAmount = 0;
            ArrayList<String[]> rows = new ArrayList<>();
            while ((line = br.readLine()) != null) {
            	line = line.replace("?", "");
            	line = line.replace("\"", "");
                String[] parts = line.split(cvsSplitBy);          
                if(parts.length < 20) {
                	String[] newParts = new String[20];
                	for(int p = 0; p < parts.length; p++) {
                		newParts[p] = parts[p];
                	}
                	for(int np = parts.length; np < 20; np++) {
                		newParts[np] = "0";
                	}
                	
                	parts = newParts;
                }
                
                rows.add(parts);
            }
            for(int d = 1; d < rows.get(0).length; d++) {
    			columns.add(new Column(rows.get(0)[d]));
    		}
            rows.remove(0);
            Collections.reverse(rows);
            
            for(int r = 0; r < rows.size(); r++) {
        		String dateStamp[] = rows.get(r)[0].split("-");
        		
        		if(!year.equals(dateStamp[0])) {
        			year = dateStamp[0];
        			years.add(year);
        		}
        		
        		for(int v = 1; v < rows.get(0).length; v++) {
        			String dat = rows.get(r)[v];
        			Double val = 0.0;
        			if(!dat.equals("")) {
        				val = Double.parseDouble(dat);
        			}       			
        			
        			Data data = new Data(val, Integer.valueOf(dateStamp[0]), Integer.valueOf(dateStamp[1]), Integer.valueOf(dateStamp[2]));
        			columns.get(v-1).addData(data);
        			
        			if(val < min) {
        				min = val;
        			}
        			
        			if(val > max) {
        				max = val;
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
		ArrayList<Data> data = new ArrayList<>();
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
		public ArrayList<Data> getData() {
			return data;
		}
		public void addData(Data data) {
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
	
	public class Data{
		private Double data;
		private Integer year;
		private Integer month;
		private Integer day;
		
		public Double getData() {
			return data;
		}		

		public void increaseData(Double data) {
			this.data += data;
		}
		
		public void decreaseData(Double data) {
			this.data -= data;
		}
		
		public void setData(Double data) {
			this.data = data;
		}

		public Integer getYear() {
			return year;
		}

		public Integer getMonth() {
			return month;
		}
		
		public Integer getDay() {
			return day;
		}

		public Data(Double data, Integer year, Integer month, Integer day) {
			this.data = data;
			this.year = year;
			this.month = month;
			this.day = day;
		}
	}

	
}
