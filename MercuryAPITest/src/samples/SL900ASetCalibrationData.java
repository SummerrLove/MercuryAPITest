/*
 * Simple program to test the SL900A Set Calibration Value function in the Mercury API
 */
package samples;

// Import the API
import com.thingmagic.*;

public class SL900ASetCalibrationData
{
    
  static void usage()
  {
    System.out.printf("Usage: Please provide valid arguments, such as:\n"
                + "SL900ASetCalibrationData [-v] [reader-uri] [--ant n[,n...]] \n" +
                  "-v  Verbose: Turn on transport listener\n" +
                  "reader-uri  Reader URI: e.g., \"tmr:///COM1\", \"tmr://astra-2100d3\"\n"
                + "--ant  Antenna List: e.g., \"--ant 1\", \"--ant 1,2\"\n" 
                + "e.g: tmr:///com1 --ant 1,2 ; tmr://10.11.115.32 --ant 1,2\n ");
        System.exit(1);
  }

  public static void setTrace(Reader r, String args[])
  {    
    if (args[0].toLowerCase().equals("on"))
    {
      r.addTransportListener(r.simpleTransportListener);
    }    
  }
 
  public static void main(String argv[])
  {
    // Program setup
    Reader r = null;
    int nextarg = 0;
    boolean trace = false;
    int[] antennaList = null;

    if (argv.length < 1)
      usage();

    if (argv[nextarg].equals("-v"))
    {
      trace = true;
      nextarg++;
    }
    
    // Create Reader object, connecting to physical device
    try
    {
        String readerURI = argv[nextarg];
        nextarg++;

        for (; nextarg < argv.length; nextarg++)
        {
            String arg = argv[nextarg];
            if (arg.equalsIgnoreCase("--ant"))
            {
                if (antennaList != null)
                {
                    System.out.println("Duplicate argument: --ant specified more than once");
                    usage();
                }
                antennaList = parseAntennaList(argv, nextarg);
                nextarg++;
            }
            else
            {
                System.out.println("Argument " + argv[nextarg] + " is not recognised");
                usage();
            }
        }

        r = Reader.create(readerURI);
        if (trace)
        {
            setTrace(r, new String[] {"on"});
        }
        r.connect();
        if (Reader.Region.UNSPEC == (Reader.Region) r.paramGet("/reader/region/id"))
        {
            Reader.Region[] supportedRegions = (Reader.Region[]) r.paramGet(TMConstants.TMR_PARAM_REGION_SUPPORTEDREGIONS);
            if (supportedRegions.length < 1)
            {
                throw new Exception("Reader doesn't support any regions");
            }
            else
            {
                r.paramSet("/reader/region/id", supportedRegions[0]);
            }
        }

        /**
         * Checking the software version of Sargas.
         * Antenna detection is supported on Sargas from the software versions higher than 5.1.x.x.
         * User has to pass antenna as an argument, if the antenna detection is not supported on
         * the respective reader firmware.
         */
        String model = r.paramGet("/reader/version/model").toString();
        Boolean checkPort = (Boolean)r.paramGet(TMConstants.TMR_PARAM_ANTENNA_CHECKPORT);
        String swVersion = (String) r.paramGet(TMConstants.TMR_PARAM_VERSION_SOFTWARE);
        if ((model.equalsIgnoreCase("M6e Micro") || model.equalsIgnoreCase("M6e Nano") ||
            (model.equalsIgnoreCase("Sargas") && (swVersion.startsWith("5.1")))) && (false == checkPort) && antennaList == null)
        {
            System.out.println("Module doesn't has antenna detection support, please provide antenna list");
            r.destroy();
            usage();
        }
        
        //Use first antenna for tag operation
        if (antennaList != null)
            r.paramSet("/reader/tagop/antenna", antennaList[0]);
        
        //Set the session to session 0
        r.paramSet(TMConstants.TMR_PARAM_GEN2_SESSION, Gen2.Session.S0);

        //Get the region
        Reader.Region region = (Reader.Region) r.paramGet(TMConstants.TMR_PARAM_REGION_ID);
        System.out.println("The current region is " + region);

        //Get the session
        Gen2.Session session = (Gen2.Session) r.paramGet(TMConstants.TMR_PARAM_GEN2_SESSION);
        System.out.println("The current session is " + session);
        
        //Get the read plan
        ReadPlan rp = (ReadPlan) r.paramGet(TMConstants.TMR_PARAM_READ_PLAN);
        System.out.println("The current Read Plan is: " + rp);
        
        //Create the Get Calibration Data tag operation
        Gen2.IDS.SL900A.GetCalibrationData tagOp = new Gen2.IDS.SL900A.GetCalibrationData();
        
        //Use the Get Calibration Data (and SFE Parameters) tag op
        Gen2.IDS.SL900A.CalSfe calSfe = (Gen2.IDS.SL900A.CalSfe)r.executeTagOp(tagOp, null);
        
        
        //Save the current Cal to restore it to the tag after the test
         Gen2.IDS.SL900A.CalibrationData restore_cal = (Gen2.IDS.SL900A.CalibrationData)calSfe.calibrationData;
        
        //Display the Calibration (and SFE Parameters) Data
        System.out.println("Detected Calibration: "+ calSfe);
        
        //Set the Calibration Data to 0x0123456789ABCD (56 bits)
        byte[] test_cal_byte_array = new byte[]{ (byte)0x01, (byte)0x23, (byte)0x45, (byte)0x67, (byte)0x89, (byte)0xAB, (byte)0xCD };
        
        Gen2.IDS.SL900A.CalibrationData test_cal = new Gen2.IDS.SL900A.CalibrationData(test_cal_byte_array, 0);

        //Execute the Set Calibration Data command with test_cal to change its value 
        r.executeTagOp(new Gen2.IDS.SL900A.SetCalibrationData(test_cal), null);
        
        //Use Get Calibration Data to retrieve the new Calibration (and SFE Parameters) from the tag
        Gen2.IDS.SL900A.CalSfe verification_calSfe = (Gen2.IDS.SL900A.CalSfe)r.executeTagOp(tagOp, null);

        //Get the Cal data from the CalSfe data
        Gen2.IDS.SL900A.CalibrationData verification_cal = (Gen2.IDS.SL900A.CalibrationData)verification_calSfe.calibrationData;
        
        //Print the verificationCal
        System.out.println("Verification Cal: " + verification_cal.toString());
        
        //Ensure that the Calibration Data we set matches the current Calibration Data
        System.out.println("Set Calibration Data Succeeded? " + test_cal.toString().equals(verification_cal.toString()));

        //Restore the starting Calibration Data
        r.executeTagOp(new Gen2.IDS.SL900A.SetCalibrationData(restore_cal), null);

        //Get CalSfe of the restored tag
        Gen2.IDS.SL900A.CalSfe restored_calSfe = (Gen2.IDS.SL900A.CalSfe)r.executeTagOp(tagOp, null);
        
        //Make sure that CalSfe is now the same as it was before the test
        System.out.println("Restore Calibration Data Succeeded? " + calSfe.toString().equals(restored_calSfe.toString()));
    } 
    catch (ReaderException re)
    {
      System.out.println("Reader Exception : " + re.getMessage());
    }
    catch (Exception re)
    {
        System.out.println("Exception : " + re.getMessage());
    }
    finally
    {
        // Shut down reader
        r.destroy();
    }
  }
  
  static  int[] parseAntennaList(String[] args,int argPosition)
    {
        int[] antennaList = null;
        try
        {
            String argument = args[argPosition + 1];
            String[] antennas = argument.split(",");
            int i = 0;
            antennaList = new int[antennas.length];
            for (String ant : antennas)
            {
                antennaList[i] = Integer.parseInt(ant);
                i++;
            }
        }
        catch (IndexOutOfBoundsException ex)
        {
            System.out.println("Missing argument after " + args[argPosition]);
            usage();
        }
        catch (Exception ex)
        {
            System.out.println("Invalid argument at position " + (argPosition + 1) + ". " + ex.getMessage());
            usage();
        }
        return antennaList;
    }
}
