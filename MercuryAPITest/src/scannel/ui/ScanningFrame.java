package scannel.ui;

import java.util.Timer;
import java.util.TimerTask;

import com.thingmagic.Gen2;
import com.thingmagic.ReaderException;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import scannel.DataUpdateListener;
import scannel.ReaderUtility;
import scannel.TagList;
import scannel.TagTableData;
import scannel.TagUnit;

public class ScanningFrame extends AnchorPane implements EventHandler, DataUpdateListener {
	
	private boolean isReading;
	private Button btn_start;
	private Button btn_clear;
	private TextField power;
	private AntennaFrame antenna_list;
	private ChoiceBox<String> cb_session;
	private ChoiceBox<String> cb_target;
//	private ChoiceBox cb_region;
	
	private Label tag_num;
	private RFIDTable table;
	
	private Timer updateTimer;
	private TimerTask task;
	
//	private final static String[] SESSION_OPTIONS = {"0", "1", "2", "3"};
//	private final static String[] TARGET_OPTIONS = {"AB", "BA", "A", "B"};
	

	public ScanningFrame() {
		// TODO Auto-generated constructor stub
		super();
		
//		this.setPrefSize(940, 700);
		this.setPadding(new Insets(20, 20, 20, 20));
		this.initComponents();
		ReaderUtility.getInstance().setDataUpdateListener(this);
		
	}

	public ScanningFrame(Node... children) {
		super(children);
		// TODO Auto-generated constructor stub
	}

	private void initComponents() {
		
		btn_start = new Button("Start");
		btn_start.setPrefSize(60, 20);
		btn_start.setOnAction(this);
		AnchorPane.setLeftAnchor(btn_start, 10.0);
		AnchorPane.setTopAnchor(btn_start, 20.0);
		this.getChildren().add(btn_start);
		
		btn_clear = new Button("Clear");
		btn_clear.setPrefSize(60, 20);
		btn_clear.setOnAction(this);
		AnchorPane.setLeftAnchor(btn_clear, 80.0);
		AnchorPane.setTopAnchor(btn_clear, 20.0);
		this.getChildren().add(btn_clear);
		
		Label power_title = new Label("RF Power (dbm):");
		power_title.setFont(new Font("Arial", 16));
		power_title.setStyle("-fx-font-weight: bold;");
		AnchorPane.setLeftAnchor(power_title, 10.0);
		AnchorPane.setTopAnchor(power_title, 90.0);
		this.getChildren().add(power_title);
		
		power = new TextField("24");
		power.setPrefWidth(60);
		AnchorPane.setLeftAnchor(power, 10.0);
		AnchorPane.setTopAnchor(power, 120.0);
		this.getChildren().add(power);
		
		
		
		antenna_list = new AntennaFrame();
//		antenna_list.setPrefSize(300, 200);
		antenna_list.setVgap(15);
		antenna_list.setHgap(30);
		antenna_list.setPadding(new Insets(20, 10, 20, 10));
		antenna_list.initComponents();
		AnchorPane.setTopAnchor(antenna_list, 160.0);
		AnchorPane.setLeftAnchor(antenna_list, 0.0);
		this.getChildren().add(antenna_list);
		
		
		Label session_title = new Label("Session: ");
		session_title.setFont(new Font("Arial", 16));
		session_title.setStyle("-fx-font-weight: bold;");
		AnchorPane.setLeftAnchor(session_title, 10.0);
		AnchorPane.setTopAnchor(session_title, 300.0);
		this.getChildren().add(session_title);
		
		cb_session = new ChoiceBox<String>(FXCollections.observableArrayList("0", "1", "2", "3"));
		cb_session.setTooltip(new Tooltip("Select a session value"));
		cb_session.setValue("0");
		AnchorPane.setTopAnchor(cb_session, 300.0);
		AnchorPane.setLeftAnchor(cb_session, 90.0);
		this.getChildren().add(cb_session);
		
		
		Label target_title = new Label("Target: ");
		target_title.setFont(new Font("Arial", 16));
		target_title.setStyle("-fx-font-weight: bold;");
		AnchorPane.setLeftAnchor(target_title, 10.0);
		AnchorPane.setTopAnchor(target_title, 340.0);
		this.getChildren().add(target_title);
		
		cb_target = new ChoiceBox<String>(FXCollections.observableArrayList("AB", "BA", "A", "B"));
		cb_target.setValue("A");
		AnchorPane.setTopAnchor(cb_target, 340.0);
		AnchorPane.setLeftAnchor(cb_target, 90.0);
		this.getChildren().add(cb_target);
		
		
		tag_num = new Label(String.format("%4d", 2000));
		this.setDisplayNumber(0);
		tag_num.setFont(new Font("Arial", 200));
		tag_num.setStyle("-fx-border-color: black; -fx-background-color: lightgray;");
		tag_num.setAlignment(Pos.CENTER);
		tag_num.setPrefSize(500, 350);
		AnchorPane.setLeftAnchor(tag_num, 250.0);
		AnchorPane.setRightAnchor(tag_num, 0.0);
		AnchorPane.setTopAnchor(tag_num, 20.0);
		this.getChildren().add(tag_num);
		
		table = new RFIDTable();
		AnchorPane.setLeftAnchor(table, 10.0);
		AnchorPane.setTopAnchor(table, 400.0);
		AnchorPane.setBottomAnchor(table, 10.0);
		AnchorPane.setRightAnchor(table, 0.0);
		this.getChildren().add(table);
		
		
	}
	
	private void pressStartButton() {
		checkChoiceBox();
		
		if (!ReaderUtility.getInstance().isConnected()) {
			return;
		}
		
		if (ReaderUtility.getInstance().isReading()) {
			// stop reading RFID tags
			ReaderUtility.getInstance().stopReading();
			btn_start.setText("Start");
			
			
			// If ReaderUtility is not using timer to read data, the table will not be updated.
			// Therefore, an update timer needs to be initiated to update table content.
			if (!ReaderUtility.USE_TIMER) {
				stopUpdateTimer();
			}
			
		} else {
			// start readging RFID tags
			try {
				ReaderUtility.getInstance().setRFPower(Integer.parseInt(power.getText()));
				ReaderUtility.getInstance().startReading(antenna_list.getAntennaList());
				this.setSession();
				this.setTargetFlag();
				btn_start.setText("Stop");
				
				// If ReaderUtility is not using timer to read data, the table will not be updated.
				// Therefore, an update timer needs to be initiated to update table content.
				if (!ReaderUtility.USE_TIMER) {
					startUpdateTimer();
				}
				
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ReaderException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	private void pressClearButton() {
		if (ReaderUtility.getInstance().isConnected()) {
			this.setDisplayNumber(0);
			ReaderUtility.getInstance().resetData();
			dataUpdate();
		} else {
			this.setDisplayNumber(0);
			this.clearTableData();
		}
	}

	private void setDisplayNumber(int num) {
		if (Integer.parseInt(tag_num.getText()) != num){
			tag_num.setText(String.format("%04d", num));
		}
	}
	
	private void clearTableData() {
		table.setTableData(FXCollections.observableArrayList());
	}
	
	@Override
	public void handle(Event event) {
		// TODO Auto-generated method stub
		if (event.getSource() == btn_start) {
			pressStartButton();
		}else if (event.getSource() == btn_clear) {
			pressClearButton();
		}else {
			System.out.println("Unknown event source...");
		}
	}

	@Override
	public void dataUpdate() {
		// TODO Auto-generated method stub
		ObservableList<TagTableData> dataList = FXCollections.observableArrayList();
		TagList tagList = ReaderUtility.getInstance().getTagData();
		
		
		//=========================================
		// Because only JavaFX thread can modify JavaFX UI element, therefore it's required to use Platform.runLater() 
		// for updating the total number of tags
//		Platform.runLater(new Runnable() {
//			@Override
//			public void run() {
//				// TODO Auto-generated method stub
				if (tagList != null) {
					setDisplayNumber(tagList.size());
					
					
					// TODO 
					// The following code is just for temporary use. It's better to combine the tagList of ReaderUtility with
					// the observablelist used for table 
					for (int i=0; i<tagList.size(); i++) {
						TagTableData ttd = new TagTableData();
						ttd.setEpc(tagList.get(i).getEPC());
						ttd.setReadCount(tagList.get(i).getReadCount());
						dataList.add(ttd);
					}
				} else {
					setDisplayNumber(0);
				}
				
				table.setTableData(dataList);
//			}
//		});
		//=========================================
		
		
	}
	
	private void initiateUpdateTimer() {
		updateTimer = new Timer();
		task = new TimerTask() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Platform.runLater(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						dataUpdate();
					}
					
				});
			}
			
		};
	}
	
	private void startUpdateTimer() {
		initiateUpdateTimer();
		updateTimer.schedule(task, 0, 500);
	}
	
	private void stopUpdateTimer() {
		updateTimer.cancel();
		updateTimer = null;
	}
	
	private void checkChoiceBox() {
		switch (cb_session.getSelectionModel().getSelectedIndex()) {
		case 0:
			System.out.println("Set Session: Session 0");
			break;
		case 1:
			System.out.println("Set Session: Session 1");
			break;
		case 2:
			System.out.println("Set Session: Session 2");
			break;
		case 3:
			System.out.println("Set Session: Session 3");
			break;
		}
		
		switch (cb_target.getSelectionModel().getSelectedIndex()) {
		case 0:
			System.out.println("Set Target: A then B");
			break;
		case 1:
			System.out.println("Set Target: B then A");
			break;
		case 2:
			System.out.println("Set Target: Only A");
			break;
		case 3:
			System.out.println("Set Target: Only B");
			break;
		}
	}
	
	private void setSession() throws ReaderException {
		switch (cb_session.getSelectionModel().getSelectedIndex()) {
		case 0:
			System.out.println("Set Session: Session 0");
			ReaderUtility.getInstance().setSession(Gen2.Session.S0);
			break;
		case 1:
			System.out.println("Set Session: Session 1");
			ReaderUtility.getInstance().setSession(Gen2.Session.S1);
			break;
		case 2:
			System.out.println("Set Session: Session 2");
			ReaderUtility.getInstance().setSession(Gen2.Session.S2);
			break;
		case 3:
			System.out.println("Set Session: Session 3");
			ReaderUtility.getInstance().setSession(Gen2.Session.S3);
			break;
		}
	}
	
	private void setTargetFlag() throws ReaderException {
		switch (cb_target.getSelectionModel().getSelectedIndex()) {
		case 0:
			System.out.println("Set Target: A then B");
			ReaderUtility.getInstance().setTargetFlag(Gen2.Target.AB);
			break;
		case 1:
			System.out.println("Set Target: B then A");
			ReaderUtility.getInstance().setTargetFlag(Gen2.Target.BA);
			break;
		case 2:
			System.out.println("Set Target: Only A");
			ReaderUtility.getInstance().setTargetFlag(Gen2.Target.A);
			break;
		case 3:
			System.out.println("Set Target: Only B");
			ReaderUtility.getInstance().setTargetFlag(Gen2.Target.B);
			break;
		}
	}
}
