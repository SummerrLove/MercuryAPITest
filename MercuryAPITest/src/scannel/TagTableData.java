package scannel;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class TagTableData {

	private StringProperty epc = new SimpleStringProperty(this, "epc");
	private IntegerProperty readCount = new SimpleIntegerProperty(this, "readCount");
	private StringProperty time = new SimpleStringProperty(this, "time");
	
	
	public TagTableData() {
		// TODO Auto-generated constructor stub
	}

	public void setEpc(String value) {
		epc.set(value);
	}
	
	public String getEpc() {
		return epc.get();
	}
	
	public StringProperty epcProperty() {
		return epc;
	}
	
	public void setReadCount(int value) {
		readCount.set(value);
	}
	
	public int getReadCount() {
		return readCount.get();
	}
	
	public IntegerProperty readCountProperty() {
		return readCount;
	}
	
	public void setTime(String value) {
		time.set(value);
	}
	
	public String getTime() {
		return time.get();
	}
	
	public StringProperty timeProperty() {
		return time;
	}
}
