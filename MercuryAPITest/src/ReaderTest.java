import java.util.EnumSet;

import javax.xml.bind.DatatypeConverter;

import com.thingmagic.Gen2;
import com.thingmagic.Reader;
import com.thingmagic.ReaderException;
import com.thingmagic.SerialTransportTCP;
import com.thingmagic.SimpleReadPlan;
import com.thingmagic.TMConstants;
import com.thingmagic.TagProtocol;
import com.thingmagic.TagReadData;
import com.thingmagic.Reader.Region;

import scannel.ReaderUtility;

public class ReaderTest {

	public ReaderTest() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
//		ReaderUtility.getInstance().readTest();
		
		try {
//			Reader myReader = Reader.create("tmr:///dev/ttyS0");
			Reader.setSerialTransport("tcp", new SerialTransportTCP.Factory());
			Reader myReader = Reader.create("tcp://192.168.100.160:4001");
			System.out.println("create serial reader...");
			
			myReader.connect();
			System.out.println("connect reader with serial port!");
			
			myReader.paramSet(TMConstants.TMR_PARAM_REGION_ID, Region.NA);
			
			EnumSet memBanks = EnumSet.of(Gen2.Bank.EPC, Gen2.Bank.GEN2BANKTIDENABLED, Gen2.Bank.GEN2BANKUSERENABLED);
			
			Gen2.Select select = new Gen2.Select(false, Gen2.Bank.EPC, 32, 8, DatatypeConverter.parseHexBinary("10"));
			Gen2.ReadData readOp = new Gen2.ReadData(memBanks, 0, (byte) 0);
			Gen2.ReadData readTID = new Gen2.ReadData(Gen2.Bank.GEN2BANKTIDENABLED, 0, (byte) 0);
			
			SimpleReadPlan plan = new SimpleReadPlan(new int[] {4}, TagProtocol.GEN2, null, readOp, 1000);
	        myReader.paramSet(TMConstants.TMR_PARAM_READ_PLAN, plan);
	        myReader.paramSet(TMConstants.TMR_PARAM_RADIO_READPOWER, new Integer(2700));
	        
	        String epc;
	        TagReadData[] trd = myReader.read(1000);
			System.out.println("tag number = "+trd.length);
			for (int i=0; i<trd.length; i++){
				epc = trd[i].epcString();
				byte[] data = trd[i].getUserMemData();
				System.out.println(i+". EPC read: " + epc);
				System.out.println("epc data length: "+trd[i].getTag().epcBytes().length);
//				for (int j=0; j<data.length; j++) {
//					System.out.println("data:"+data[j]);
//				}
				System.out.println("TID: "+DatatypeConverter.printHexBinary(trd[i].getTIDMemData()));
				System.out.println("USER BANK: "+DatatypeConverter.printHexBinary(trd[i].getUserMemData()));
			}
			
			myReader.paramSet("/reader/tagop/antenna", 4);
//			short[] writeData = {0x1234, 0x5678};
//			Gen2.BlockWrite writeop = new Gen2.BlockWrite(Gen2.Bank.USER, 0, (byte) 2, writeData);
//			myReader.executeTagOp(writeop, null);
			
//			for (int i=0; ;i++) {
//				try {
//					Gen2.ReadData readOp = new Gen2.ReadData(Gen2.Bank.USER, i, (byte) 0x01);
//					short[] readData = (short[]) myReader.executeTagOp(readOp, null);
//					System.out.printf("%04x-", readData[0]);
//				} catch (ReaderException e) {
//					System.out.println();
//					e.printStackTrace();
//					break;
//				}
//			}
			
			
//			short[] readData = (short[]) myReader.executeTagOp(readOp, select);
////			System.out.println("\nRead Data after block write operation : "+readData.length);
//			for (short data: readData) {
//				System.out.printf("%04x-", data);
//			}
			
			myReader.destroy();
		} catch (ReaderException e) {
			e.printStackTrace();
		}
		
//		MyClass mc1 = new MyClass("Before!");
//		System.out.println("myStr = "+mc1.getStr());
//		
//		modifyStr(mc1);
//		
//		System.out.println("After modified, myStr = "+mc1.getStr());
		
	}

	private static void modifyStr(MyClass mc) {
		mc.setStr("After");
		System.out.println("str = "+mc.getStr());
	}
	
}
