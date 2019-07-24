package scannel.ui;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import com.thingmagic.ReaderException;

import javafx.application.Platform;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import scannel.ReaderUtility;

public class SettingFrame extends AnchorPane implements EventHandler {
	
	private boolean isConnected;
	private TextField ip;
	private TextField port;
	private Button btn_connect;
	private Label icon;
//	private CommPortIdentifier cpi;
	
	private int count = 0;

	public SettingFrame() {
		// TODO Auto-generated constructor stub
		super();
		
		this.setPadding(new Insets(20, 0, 0, 20));
		
		initComponents();
		
	}
	
	private void initComponents() {
		Label ip_title = new Label("IP :");
		ip_title.setFont(new Font("Arial", 16));
		ip_title.setStyle("-fx-font-weight: bold;");
		ip_title.setLayoutX(20);
		ip_title.setLayoutY(20);
		
		ip = new TextField("192.168.100.124");
		ip.setLayoutX(20);
		ip.setLayoutY(45);
		
		Label port_title = new Label("Port :");
		port_title.setFont(new Font("Arial", 16));
		port_title.setStyle("-fx-font-weight: bold;");
		port_title.setLayoutX(20);
		port_title.setLayoutY(100);
		
		port = new TextField("4001");
		port.setLayoutX(20);
		port.setLayoutY(125);
		port.setPrefWidth(100);
		

		this.getChildren().addAll(ip_title, ip, port_title, port);
		
		btn_connect = new Button("Connect");
		btn_connect.setOnAction(this);
		btn_connect.setFont(new Font("Arial", 20));
		AnchorPane.setLeftAnchor(btn_connect, 0.0);
		AnchorPane.setTopAnchor(btn_connect, 200.0);
		btn_connect.setPrefWidth(150);
//		AnchorPane.setBottomAnchor(btn_connect, 30.0);
		this.getChildren().add(btn_connect);
		
		
//		System.out.println( "Path: " + getClass().getResource("/").toExternalForm());
		icon = new Label();
		Image img = new Image(getClass().getResourceAsStream("/resource/scannel.jpg"));
		icon.setGraphic(new ImageView(img));
		AnchorPane.setLeftAnchor(icon, 5.0);
		AnchorPane.setBottomAnchor(icon, 30.0);
		this.getChildren().add(icon);
	}

	public void setConnectionStatus(boolean status) {
		isConnected = status;
	}
	
	public boolean isConnected() {
		return isConnected;
	}

	@Override
	public void handle(Event event) {
		// TODO Auto-generated method stub
		if (event.getSource() == btn_connect) {
			System.out.println("button connect clicked!");
		}
		
		if (ip.getText().equals("") || port.getText().equals("")) {
			System.out.println("Please input information about reader ip and port!");
			return;
		}
		
		pressConnectButton();
//		testTimer();
	}
	
	private void pressConnectButton() {
		if (isConnected) {
			// TODO disconnect from reader
			System.out.println("Disconnect from reader.");
			ReaderUtility.getInstance().disconnectReader();
			isConnected = false;
			btn_connect.setText("Connect");
			
		} else {
			// TODO connect to reader
			System.out.println("Connect to reader: "+ip.getText()+":"+port.getText());
			try {
				ReaderUtility.getInstance().connectTCPReader(ip.getText(), port.getText());
				btn_connect.setText("Disconnect");
				isConnected = true;
//				btn_connect.setDisable(true);
			} catch (ReaderException e) {
			//	TODO Auto-generated catch block
				e.printStackTrace();
				isConnected = false;
				btn_connect.setText("Connect");
			}
			
		}
	}
	
	private void testTimer() {
		Timer t = new Timer();
		t.schedule(new TimerTask() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Platform.runLater(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						System.out.println("count = "+(count++));
						System.out.println((new Date()).toString());
					}
					
				});
			}
			
		}, 0, 500);
	}
}
