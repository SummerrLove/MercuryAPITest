/*
 * 
 *  Sample program that demonstartes the usage of custom transport
 */
package samples;


import com.thingmagic.*;

/**
 *
 * 
 */
public class ReadCustomTransport 
{
    
  static readasync.SerialPrinter serialPrinter;
  static readasync.StringPrinter stringPrinter;
  static TransportListener currentListener;

  static void usage()
  { 
    System.out.printf("Usage: Please provide valid arguments, such as:\n"
                + "ReadCustomTransport [-v] [reader-uri] [--ant n[,n...]] \n" +
                  "-v  Verbose: Turn on transport listener\n" +
                  "reader-uri  Reader URI: customschemename://readerIP:port e.g.,\"tcp://10.11.115.46:4150\"\n"
                + "--ant  Antenna List: e.g., \"--ant 1\", \"--ant 1,2\"\n" 
                + "e.g: tcp://10.11.115.32:14150 --ant 1,2 \n ");
    System.exit(1);
  }

   public static void setTrace(Reader r, String args[])
  {
    if (args[0].toLowerCase().equals("on"))
    {
        r.addTransportListener(Reader.simpleTransportListener);
        currentListener = Reader.simpleTransportListener;
    }
    else if (currentListener != null)
    {
        r.removeTransportListener(Reader.simpleTransportListener);
    }
  }

   static class SerialPrinter implements TransportListener
  {
    public void message(boolean tx, byte[] data, int timeout)
    {
      System.out.print(tx ? "Sending: " : "Received:");
      for (int i = 0; i < data.length; i++)
      {
        if (i > 0 && (i & 15) == 0)
          System.out.printf("\n         ");
        System.out.printf(" %02x", data[i]);
      }
      System.out.printf("\n");
    }
  }

  static class StringPrinter implements TransportListener
  {
    public void message(boolean tx, byte[] data, int timeout)
    {
      System.out.println((tx ? "Sending:\n" : "Receiving:\n") +
                         new String(data));
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
     /**
       * Add the custom transport scheme before calling Reader.create().
       * This can be done by using function Reader.setSerialTransport().
       * It accepts two arguments. scheme and Factory object.
       * scheme: the custom transport scheme name. For demonstration using scheme as "tcp".
       * Factory object: reference to the serial transport.
       */
     
        Reader.setSerialTransport("tcp", new SerialTransportTCP.Factory());  
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

        String model = r.paramGet("/reader/version/model").toString();
        Boolean checkPort = (Boolean)r.paramGet(TMConstants.TMR_PARAM_ANTENNA_CHECKPORT);
        if ((model.equalsIgnoreCase("M6e Micro") || model.equalsIgnoreCase("M6e Nano") ||
            (false == checkPort)) && antennaList == null)
        {
            System.out.println("Module doesn't has antenna detection support, please provide antenna list");
            r.destroy();
            usage();
        }

        SimpleReadPlan plan = new SimpleReadPlan(antennaList, TagProtocol.GEN2, null, null, 1000);
        r.paramSet(TMConstants.TMR_PARAM_READ_PLAN, plan);
      
        TagReadData[] tagReads = r.read(1000);
        for (TagReadData tr : tagReads)
        {
            System.out.println(tr.toString());
        }

        ReadExceptionListener exceptionListener = new readasync.TagReadExceptionReceiver();
        r.addReadExceptionListener(exceptionListener);
        // Create and add tag listener
        ReadListener rl = new readasync.PrintListener();
        r.addReadListener(rl);
        // search for tags in the background
        r.startReading();
        Thread.sleep(1000);
        r.stopReading();

        r.removeReadListener(rl);
        r.removeReadExceptionListener(exceptionListener);

        // Shut down reader
        r.destroy();
    } 
    catch (ReaderException re)
    {
      System.out.println("ReaderException: " + re.getMessage());
    }
    catch (Exception re)
    {
        System.out.println("Exception: " + re.getMessage());
    }
  }

  static class PrintListener implements ReadListener
  {
    public void tagRead(Reader r, TagReadData tr)
    {
      System.out.println("Background read: " + tr.toString());
    }

  }

  static class TagReadExceptionReceiver implements ReadExceptionListener
  {      
        public void tagReadException(com.thingmagic.Reader r, ReaderException re)
        {
            if(re.getMessage().equals("Connection Lost"))
            {
                System.exit(1);
            }
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
