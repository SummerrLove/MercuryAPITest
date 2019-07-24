package application;
	
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import scannel.ReaderUtility;
import scannel.ui.MainFrame;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;


public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
			MainFrame root = new MainFrame();
			Scene scene = new Scene(root, 1200, 700);
			primaryStage.setScene(scene);
			primaryStage.setTitle("Scannel Reader Utility");
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void stop(){
		System.out.println("Application closed!");
		// call ReaderUtility to stop reading and disconnect from reader
		ReaderUtility.getInstance().destroy();
	}
	
	public static void main(String[] args) {
		launch(args);
		
		
		
	}
}
