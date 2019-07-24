package scannel;

public class TagUnit {

	private String epc;
	private int readCount;
	
	public TagUnit(String epc) {
		// TODO Auto-generated constructor stub
		this.epc = epc;
		readCount = 0;
	}
	
	public TagUnit(String epc, int count){
		this.epc = epc;
		readCount = count;
	}

	public void setEPC(String epc){
		this.epc = epc;
	}
	
	public String getEPC(){
		return this.epc;
	}
	
	public void addReadCount(int count){
		readCount += count;
	}
	
	public int getReadCount(){
		return readCount;
	}
	
	public void reset(){
		epc = null;
		readCount = 0;
	}
}
