package scannel.ui;

import java.util.ArrayList;

import javafx.geometry.Insets;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;

public class AntennaFrame extends GridPane {
	
	private CheckBox[] antenna;

	public AntennaFrame() {
		// TODO Auto-generated constructor stub
	}

	public void initComponents() {
		Label ant_title = new Label("Antenna");
		ant_title.setFont(new Font("Arial", 16));
		ant_title.setStyle("-fx-font-weight: bold;");
		this.add(ant_title, 0, 0, 2, 1);
		
		antenna = new CheckBox[4];
		for (int i=0; i<4; i++) {
			antenna[i] = new CheckBox("Antenna "+(i+1));
			int index_x = i%2;
			int index_y = 1 + i/2;
			this.add(antenna[i], index_x, index_y);
		}
		antenna[0].setSelected(true);
	}
	
	public int[] getAntennaList() {
		ArrayList<String> list = new ArrayList<String>();
		
		for (int i=0; i<antenna.length; i++) {
			if (antenna[i].isSelected()) {
				list.add(new String(""+(i+1)));
			}
		}
		
		System.out.print("Antenna List:");
		int[] ant_list = new int[list.size()];
		for (int j=0;j<list.size();j++) {
			ant_list[j] = Integer.parseInt(list.get(j));
			System.out.print(" "+ant_list[j]);
		}
		
		return ant_list;
	}
}
