package scannel;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import com.thingmagic.Gen2;
import com.thingmagic.ReadListener;
import com.thingmagic.Reader;
import com.thingmagic.ReaderException;
import com.thingmagic.SerialReader.SetUserProfileOption;
import com.thingmagic.SerialReader.UserConfigOp;
import com.thingmagic.SerialTransportTCP;
import com.thingmagic.SimpleReadPlan;
import com.thingmagic.TMConstants;
import com.thingmagic.TagProtocol;
import com.thingmagic.TagReadData;

import javafx.application.Platform;

import com.thingmagic.Reader.Region;

public class ReaderUtility implements ReadListener {

	private static ReaderUtility ru;
	private static Reader myReader;
	private static TagList tagReadList;
	private static Timer readTimer;
	private static TimerTask task;
	private final static long INTERVAL = 500;
	
	private static boolean isConnected;
	private static boolean isReading;
	
	private static DataUpdateListener updateListener;
	
	private static String filter_str;
	
	private final static boolean RASPi_TEST = true;
	private Properties prop;
	
//	private static SimpleReadPlan myReadPlan;
	
//	private boolean showReadCount;
	public final static boolean USE_TIMER = true;
	
	
	private ReaderUtility() {
		// TODO Auto-generated constructor stub
		if (RASPi_TEST) {
			this.loadConfig();
			
			if (prop != null) {
				System.out.println("port = "+prop.getProperty("port"));
			}
		}
	}

	public static ReaderUtility getInstance() {
		if (ru == null) {
			ru = new ReaderUtility();
		}
		return ru;
	}
	
	public void destroy() {
		if (readTimer != null) {
			readTimer.cancel();
		}
		
		if (myReader != null) {
			myReader.destroy();
			myReader = null;
		}
		
		if (tagReadList != null) {
			tagReadList.reset();
			tagReadList = null;
		}
		
		if (ru != null) {
			ru = null;
		}
	}
	
	private void initiateTimer() {
		readTimer = new Timer();
		task = new TimerTask() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Platform.runLater(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						readData();
						if (updateListener != null) {
							updateListener.dataUpdate();
						}
					}
					
				});
			}
			
		};
	}
	
	private void createTCPReader(String ip, String port) throws ReaderException {
		Reader.setSerialTransport("tcp", new SerialTransportTCP.Factory());
		myReader = Reader.create("tcp://"+ip+":"+port);
		System.out.println("create reader!");
	}
	
//	public void createTCPReader(String readerURI) throws ReaderException{
//		Reader.setSerialTransport("tcp", new SerialTransportTCP.Factory());
//		myReader = Reader.create(readerURI);
//	}
	
	private void createSerialReader(String port) throws ReaderException{
		String url = "tmr:///dev/"+port;
		myReader = Reader.create(url);
		System.out.println("create serial reader...");
	}
	
	public void connectTCPReader(String ip, String port) throws ReaderException{
		if (myReader != null){
			
			if (myReader != null) {
				myReader.destroy();
				myReader = null;
			}
		} 
		
		this.createTCPReader(ip, port);
		myReader.connect();
		tagReadList = new TagList();
		isConnected = true;
		System.out.println("connect reader with TCP/IP!");
	}
	
	public void connectSerialReader(String serialPort) throws ReaderException {
		if (myReader != null){
			
			if (myReader != null) {
				myReader.destroy();
				myReader = null;
			}
		} 
		
		this.createSerialReader(serialPort);
		myReader.connect();
		tagReadList = new TagList();
		isConnected = true;
		System.out.println("connect reader with serial port!");
	}
	
	public void disconnectReader() {
		if (myReader != null) {
			 if (!USE_TIMER) {
				 myReader.stopReading();
			 } else {
				 if (readTimer != null) {
					 readTimer.cancel();
					 readTimer = null;
				 }
			 }
			myReader.destroy();
			myReader = null;
			isConnected = false;
			isReading = false;
		} else {
			System.out.println("[ReaderUtility] failed to disconnect from reader cause reader is null..");
		}
	}
	
	public void setSession(Gen2.Session session) throws ReaderException {
		if (myReader == null) {
			System.out.println("Reader is not initialized...");
			return;
		}
		
		System.out.println("set Session:"+session.toString());
		myReader.paramSet(TMConstants.TMR_PARAM_GEN2_SESSION, session);
	}
	
	public void setTargetFlag(Gen2.Target target) throws ReaderException {
		if (myReader == null) {
			System.out.println("Reader is not initialized...");
			return;
		}
		
		System.out.println("set target:"+target.toString());
		myReader.paramSet(TMConstants.TMR_PARAM_GEN2_TARGET, target);
	}
	
	public void setRFPower(int power) throws ReaderException {
		if (myReader == null) {
			System.out.println("Reader is not initialized...");
			return;
		}
		
		System.out.println("set RF power:"+power);
		myReader.paramSet(TMConstants.TMR_PARAM_RADIO_READPOWER, new Integer(power*100));
	}
	
	public void setRegion(Region region) throws ReaderException {
		if (myReader == null) {
			System.out.println("Reader is not initialized...");
			return;
		}
		
		System.out.println("set Region: "+region.toString());
		myReader.paramSet(TMConstants.TMR_PARAM_REGION_ID, region);
	}
	
	public void getSupportedRegion() throws ReaderException {
		if (myReader == null) {
			System.out.println("Reader is not initialized...");
			return;
		}
		
		myReader.paramGet(TMConstants.TMR_PARAM_REGION_SUPPORTEDREGIONS);
	}
	
	public void startReading(int[] antenna) throws ReaderException {
		if (myReader == null) {
			System.out.println("Reader is not initialized...");
			return;
		}
		
		System.out.println("start reading");
		myReader.paramSet(TMConstants.TMR_PARAM_REGION_ID, Region.NA);
		SimpleReadPlan plan = new SimpleReadPlan(antenna, TagProtocol.GEN2, null, null, 1000);
        myReader.paramSet(TMConstants.TMR_PARAM_READ_PLAN, plan);
        
        // set up the timeout value
        myReader.paramSet(TMConstants.TMR_PARAM_TRANSPORTTIMEOUT, 30000);
		
        if (USE_TIMER) {
        	initiateTimer();
        	readTimer.schedule(task, 0, INTERVAL);
        } else {
        	myReader.addReadListener(this);
        	myReader.startReading();
        }
        
		isReading = true;
		System.out.println("start reading tags...");
		
		
		//UserConfigOp op = new UserConfigOp(SetUserProfileOption.SAVE);
	}
	
	public void stopReading() {
		if (USE_TIMER) {
			readTimer.cancel();
			readTimer = null;
		} else {
			myReader.stopReading();
		}
		isReading = false;
	}
	
	public void resetData() {
		if (tagReadList != null) {
			tagReadList.reset();
		}
	}
	
	public boolean isConnected() {
		return isConnected;
	}
	
	public boolean isReading() {
		return isReading;
	}
	
	public int totalTagRead() {
		return tagReadList.size();
	}
	
	
	// return a copy of the tag data
	public TagList getTagData() {
//		if (tagReadList != null) {
//			TagUnit[] tags = new TagUnit[tagReadList.size()];
//			for (int i=0; i<tagReadList.size(); i++) {
//				TagUnit tmp = tagReadList.get(i);
//				tags[i] = new TagUnit(tmp.getEPC(), tmp.getReadCount());
//			}
//			
//			return tags;
//		} else {
//			return null;
//		}
		return tagReadList;
	}
	
	public void setDataUpdateListener(DataUpdateListener listener) {
		this.updateListener = listener;
	}
	
	public void removeDataUpdateListener() {
		this.updateListener = null;
	}
	
	private void parseTagData(TagReadData[] trd){
		for (int i=0; i<trd.length; i++){
			parseTagData(trd[i]);
		}
	}

	private void parseTagData(TagReadData trd) {
		// if the epc of the tag doesn't match the filter format, then ignore the tag data
		if ((filter_str != null) && !trd.epcString().startsWith(filter_str)) {
			return;
		}
		tagReadList.addTag(trd);
	}
	
	private void readData() {
		// TODO Auto-generated method stub
		String epc;
		TagReadData[] trd;
		try {
			trd = myReader.read(300);
			System.out.println("tag number = "+trd.length);
			this.parseTagData(trd);
			
		} catch (ReaderException e1) {
			// TODO Auto-generated catch block
			System.out.println("Stop reading due to the following exception:");
			e1.printStackTrace();
			readTimer.cancel();
		}
	}

	@Override
	public void tagRead(Reader r, TagReadData t) {
		// TODO Auto-generated method stub
		this.parseTagData(t);
	}
	
	public void setFilter(String str) {
		filter_str = str;
	}
	
	private void removeFilter() {
		filter_str = null;
	}
	
	private void loadConfig() {
		try {
			FileInputStream fis = new FileInputStream("config.ini");
			prop = new Properties();
			prop.load(fis);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			prop = null;
		} catch (IOException e) {
			e.printStackTrace();
			prop = null;
		}
	}
	
//	public void readTest() {
//		if (prop != null) {
//			String port = prop.getProperty("port");
//			try {
//				this.connectSerialReader(port);
//				
//				myReader.paramSet(TMConstants.TMR_PARAM_REGION_ID, Region.NA);
//				SimpleReadPlan plan = new SimpleReadPlan(new int[] {1}, TagProtocol.GEN2, null, null, 1000);
//		        myReader.paramSet(TMConstants.TMR_PARAM_READ_PLAN, plan);
//		        myReader.paramSet(TMConstants.TMR_PARAM_RADIO_READPOWER, new Integer(2700));
//		        
//		        String epc;
//		        TagReadData[] trd = myReader.read(3000);
//				System.out.println("tag number = "+trd.length);
//				for (int i=0; i<trd.length; i++){
//					epc = trd[i].epcString();
//					System.out.println(i+". EPC read: " + epc);
//				}
//			} catch (ReaderException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//	}
}
