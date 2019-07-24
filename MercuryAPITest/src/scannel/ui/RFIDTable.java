package scannel.ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import scannel.TagTableData;

public class RFIDTable extends AnchorPane {

	private TableView<TagTableData> table;
	private TableColumn<TagTableData, String> epcCol;
	private TableColumn<TagTableData, Integer> countCol;
	private TableColumn<TagTableData, String> timeCol;
	
	
	public RFIDTable() {
		
		initComponents();
		
	}
	
	private void initComponents() {
		
		epcCol = new TableColumn<TagTableData, String>("EPC");
		epcCol.setPrefWidth(400.0);
		epcCol.setCellValueFactory(new PropertyValueFactory("epc"));
		countCol = new TableColumn<TagTableData, Integer>("Read Count");
		countCol.setPrefWidth(150.0);
		countCol.setCellValueFactory(new PropertyValueFactory("readCount"));
		timeCol = new TableColumn<TagTableData, String>("Time");
		timeCol.setPrefWidth(300.0);
		timeCol.setCellValueFactory(new PropertyValueFactory("time"));

		table = new TableView<TagTableData>();
		table.getColumns().setAll(epcCol, countCol, timeCol);
		AnchorPane.setLeftAnchor(table, 0.0);
		AnchorPane.setTopAnchor(table, 0.0);
		AnchorPane.setBottomAnchor(table, 0.0);
		AnchorPane.setRightAnchor(table, 0.0);
		this.getChildren().add(table);
	}
	
	private void createSampleData() {
		ObservableList<TagTableData> dataList = FXCollections.observableArrayList();
		
		for (int i=0; i<5; i++) {
			TagTableData data = new TagTableData();
			data.setEpc("aaa00"+i);
			data.setReadCount(i*13 + 2);
			data.setTime("2018/01/01");
			dataList.add(data);
		}
		
		table.setItems(dataList);
	}
	
	public void setTableData(ObservableList<TagTableData> dataList) {
		table.setItems(dataList);
	}
}
