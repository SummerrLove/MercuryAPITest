package scannel.ui;

import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Separator;
import javafx.scene.layout.AnchorPane;

public class MainFrame extends AnchorPane {

	private SettingFrame setting;
	private ScanningFrame scanning;
	
	public MainFrame() {
		// TODO Auto-generated constructor stub
		initComponents();
		// Set background color to white
		this.setStyle("-fx-background: white;");
	}

	public MainFrame(Node... arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	private void initComponents() {
		
		Separator separator = new Separator();
		separator.setOrientation(Orientation.VERTICAL);
		AnchorPane.setLeftAnchor(separator, 250.0);
		AnchorPane.setTopAnchor(separator, 0.0);
		AnchorPane.setBottomAnchor(separator, 0.0);
		
		setting = new SettingFrame();
		AnchorPane.setLeftAnchor(setting, 0.0);
		AnchorPane.setTopAnchor(setting, 0.0);
		AnchorPane.setBottomAnchor(setting, 0.0);
		
		scanning = new ScanningFrame();
		AnchorPane.setLeftAnchor(scanning, 260.0);
		AnchorPane.setRightAnchor(scanning, 20.0);
		AnchorPane.setTopAnchor(scanning, 0.0);
		AnchorPane.setBottomAnchor(scanning, 0.0);
		
		this.getChildren().addAll(setting, separator, scanning);
	}
}
