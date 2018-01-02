package edu.cmu.cs.stage3.alice.authoringtool.util;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;

import edu.cmu.cs.stage3.alice.authoringtool.JAliceFrame;

public class ChangeLogger implements edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateListener {
	protected edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool authoringTool;
	protected edu.cmu.cs.stage3.alice.core.World world;
	private Package authoringToolPackage = Package.getPackage( "edu.cmu.cs.stage3.alice.authoringtool" );
    String dataDirectory;

    protected PrintWriter printWriter = null;
    

	public ChangeLogger(edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool authoringTool) {
          this.authoringTool = authoringTool;
          authoringTool.addAuthoringToolStateListener(this);
          
          //dataDirectory = edu.cmu.cs.stage3.alice.authoringtool.JAlice.getAliceHomeDirectory() + "\\loggingData\\";
          //System.out.println(edu.cmu.cs.stage3.alice.authoringtool.JAlice.getAliceHomeDirectory());
          dataDirectory = Configuration.getValue( authoringToolPackage, "directories.worldsDirectory" ) + System.getProperty( "file.separator" ) + "loggingData";
	}

        public void pushUndoableRedoable( edu.cmu.cs.stage3.alice.authoringtool.util.UndoableRedoable ur ) {
          if (ur instanceof edu.cmu.cs.stage3.alice.authoringtool.util.ChildChangeUndoableRedoable) {
            record(  ((edu.cmu.cs.stage3.alice.authoringtool.util.ChildChangeUndoableRedoable)ur).getLogString()  );
          } else if (ur instanceof edu.cmu.cs.stage3.alice.authoringtool.util.ObjectArrayPropertyUndoableRedoable) {
            record(  ((edu.cmu.cs.stage3.alice.authoringtool.util.ObjectArrayPropertyUndoableRedoable)ur).getLogString()  );
          } else if (ur instanceof edu.cmu.cs.stage3.alice.authoringtool.util.PropertyUndoableRedoable) {
            record(  ((edu.cmu.cs.stage3.alice.authoringtool.util.PropertyUndoableRedoable)ur).getLogString()  );
          } else if (ur instanceof edu.cmu.cs.stage3.alice.authoringtool.util.OneShotUndoableRedoable) {
            record(  ((edu.cmu.cs.stage3.alice.authoringtool.util.OneShotUndoableRedoable)ur).getLogString()  );
          }
        }
        
        public void recordInstructorIntervention (String type, String comment) {
 			String logString = "TIME=<" + System.currentTimeMillis() + "> "  + "TYPE=<" + type +  "> " + "COMMENT=<" + comment + ">";
 			record(logString);
        }

        protected void record(String toRecord) {
        	
          if (toRecord != null && authoringTool.instructorInControl) {
            if (printWriter != null) {printWriter.println(toRecord); printWriter.flush();}
          }
        }
        
        protected void recordWorldEvent(long time, String type){
			String logString = "TIME=<" + time + "> " + "EVENT=<World> " + "TYPE=<" + type +">";    
			record(logString);    	
        }


	///////////////////////////////////////////////
	// AuthoringToolStateListener interface
	///////////////////////////////////////////////
    private File file = null;
        
	public void stateChanged( edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev ) {

	}

	public void worldUnLoading( edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev ) {
		if (printWriter != null && JAliceFrame.isLogging == true) {
	        printWriter.close();
	        printWriter = null;
	        if (file.length() == 0) { 
	        	file.delete(); 
	        	file = null; 
	        }
		}
	}

	public void worldLoaded( edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev ) {
		if (JAliceFrame.isLogging == true && file == null) {
			file = new File(dataDirectory);
			if (!file.exists()){
				file.mkdir();
			}
			file = new File(dataDirectory + System.getProperty( "file.separator" ) + System.currentTimeMillis() + ".txt");
			
			try {
				printWriter = new PrintWriter(new FileOutputStream(file));
			} catch (FileNotFoundException fnfe) {
				fnfe.printStackTrace();
			}
		}
	}

	public void stateChanging( edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev ) {}
	public void worldLoading( edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev ) {}
	public void worldStarting( edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev ) {}
	public void worldStopping( edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev ) {}
	public void worldPausing( edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev ) {}
	public void worldSaving( edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev ) {}
	public void worldUnLoaded( edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev ) {}
	public void worldStarted( edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev ) {
		this.recordWorldEvent(System.currentTimeMillis(), "start");
	}
	public void worldStopped( edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev ) {
		this.recordWorldEvent(System.currentTimeMillis(), "stop");
	}
	public void worldPaused( edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev ) {
		this.recordWorldEvent(System.currentTimeMillis(), "pause");
	}
	public void worldSaved( edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev ) {
		this.recordWorldEvent(System.currentTimeMillis(), "save");
	}
}
