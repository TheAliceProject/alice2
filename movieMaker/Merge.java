package movieMaker;

/*
 * @(#)Merge.java	1.2 01/03/13
 *
 * Copyright (c) 1999-2001 Sun Microsystems, Inc. All Rights Reserved.
 *
 * Sun grants you ("Licensee") a non-exclusive, royalty free, license to use,
 * modify and redistribute this software in source and binary code form,
 * provided that i) this copyright notice and license appear on all copies of
 * the software; and ii) Licensee does not utilize the software in a manner
 * which is disparaging to Sun.
 *
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING ANY
 * IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR
 * NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN AND ITS LICENSORS SHALL NOT BE
 * LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING
 * OR DISTRIBUTING THE SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL SUN OR ITS
 * LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT,
 * INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER
 * CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF
 * OR INABILITY TO USE SOFTWARE, EVEN IF SUN HAS BEEN ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGES.
 *
 * This software is not designed or intended for use in on-line control of
 * aircraft, air traffic, aircraft navigation or aircraft communications; or in
 * the design, construction, operation or maintenance of any nuclear
 * facility. Licensee represents and warrants that it will not use or
 * redistribute the Software for such purposes.
 */

//import java.io.File;
//import java.io.FileDescriptor;
import edu.cmu.cs.stage3.lang.Messages;
import java.io.IOException;
import java.util.Vector;
import javax.media.ConfigureCompleteEvent;
import javax.media.Controller;
import javax.media.ControllerEvent;
import javax.media.ControllerListener;
import javax.media.DataSink;
import javax.media.EndOfMediaEvent;
import javax.media.Format;
import javax.media.Manager;
import javax.media.MediaLocator;
import javax.media.PrefetchCompleteEvent;
import javax.media.Processor;
import javax.media.ProcessorModel;
import javax.media.RealizeCompleteEvent;
import javax.media.ResourceUnavailableEvent;
import javax.media.control.TrackControl;
import javax.media.datasink.DataSinkErrorEvent;
import javax.media.datasink.DataSinkEvent;
import javax.media.datasink.DataSinkListener;
import javax.media.datasink.EndOfStreamEvent;
import javax.media.format.AudioFormat;
import javax.media.format.VideoFormat;
import javax.media.protocol.ContentDescriptor;
import javax.media.protocol.DataSource;
import javax.media.protocol.FileTypeDescriptor;

/**
 * Merged the tracks from different inputs and generate a QuickTime file with
 * the all the merged tracks.
 */
public class Merge implements ControllerListener, DataSinkListener {

	Processor[] processors = null;

	String outputFile = null;

	String videoEncoding = "JPEG"; 

	String audioEncoding = "LINEAR"; 

	String outputType = FileTypeDescriptor.QUICKTIME;

	DataSource[] dataOutputs = null;

	DataSource merger = null;

	DataSource outputDataSource;

	Processor outputProcessor;

	ProcessorModel outputPM;

	DataSink outputDataSink;

	MediaLocator outputLocator;

	boolean done = false;

	VideoFormat videoFormat = null;

	AudioFormat audioFormat = null;

	public Merge(String output) {
		outputFile = output;
	}

	public Merge(String output, String outputTy) {

		outputFile = output;
		outputType = outputTy;
	}

	public void doMerge(Vector sourcesURLs) {
		processors = new Processor[sourcesURLs.size()];
		dataOutputs = new DataSource[sourcesURLs.size()];

		for (int i = 0; i < sourcesURLs.size(); i++) {
			String source = (String) sourcesURLs.elementAt(i);
			MediaLocator ml = new MediaLocator(source);
			ProcessorModel pm = new MyPM(ml);
			try {
				processors[i] = Manager.createRealizedProcessor(pm);
				dataOutputs[i] = processors[i].getDataOutput();
				processors[i].start();
			} catch (Exception e) {
				System.err.println(Messages.getString("Failed_to_create_a_processor__", e)); 
			}
		}

		// Merge the data sources from the individual processors
		try {
			merger = Manager.createMergingDataSource(dataOutputs);
			merger.connect();
			merger.start();
		} catch (Exception ex) {
			System.err.println(Messages.getString("Failed_to_merge_data_sources_", ex)); 

		}
		if (merger == null) {
			System.err.println(Messages.getString("Failed_to_merge_data_sources_", "")); 

		}
		/*
		 * try { Player p = Manager.createPlayer(merger); new
		 * com.sun.media.ui.PlayerWindow(p); } catch (Exception e) {
		 * System.err.println("Failed to create player " + e); }
		 */

		// Create the output processor
		ProcessorModel outputPM = new MyPMOut(merger);

		try {
			outputProcessor = Manager.createRealizedProcessor(outputPM);
			outputDataSource = outputProcessor.getDataOutput();
		} catch (Exception exc) {
			System.err.println(Messages.getString("Failed_to_create_output_processor__", exc)); 

		}

		while(outputDataSink==null){
		try {
			outputLocator = new MediaLocator(outputFile);
			outputDataSink = Manager.createDataSink(outputDataSource,
					outputLocator);
			outputDataSink.open();
		} catch (Exception exce) {
			//System.err.println("Failed to create output DataSink: " + exce);

		}}

		outputProcessor.addControllerListener(this);
		outputDataSink.addDataSinkListener(this);
		//System.err.println("Merging...");
		try {
			outputDataSink.start();
			outputProcessor.start();
		} catch (Exception excep) {
			System.err.println(Messages.getString("Failed_to_start_file_writing__", excep)); 

		}

//		while (!done) {
//			try {
//				Thread.currentThread().sleep(100);
//			} catch (InterruptedException ie) {
//			}
//
//			if (outputProcessor != null
//					&& (int) (outputProcessor.getMediaTime().getSeconds()) > count) {
//				System.err.print(".");
//				count = (int) (outputProcessor.getMediaTime().getSeconds());
//			}
//
//		}

		waitForFileDone();
		
		if (outputDataSink != null) {

			outputDataSink.close();
			outputDataSink.removeDataSinkListener(this);
		}
		
		synchronized (this) {

			if (outputProcessor != null) {

				outputProcessor.close();
				outputProcessor.removeControllerListener(this);
			}

		}
		try {
			merger.stop();
			merger.disconnect();

		} catch (IOException e) {
			e.printStackTrace();
		}
		
		for (int ii = 0; ii < processors.length; ii++) {
			processors[ii].stop();
			processors[ii].close();
			processors[ii].deallocate();
		}

		//System.err.println("Done!");

	}

	class MyPM extends ProcessorModel {

		MediaLocator inputLocator;

		public MyPM(MediaLocator inputLocator) {
			this.inputLocator = inputLocator;
		}

		
		public ContentDescriptor getContentDescriptor() {
			return new ContentDescriptor(ContentDescriptor.RAW);
		}

		
		public DataSource getInputDataSource() {
			return null;
		}

		
		public MediaLocator getInputLocator() {
			return inputLocator;
		}

		
		public Format getOutputTrackFormat(int index) {
			return null;
		}

		
		public int getTrackCount(int n) {
			return n;
		}

		
		public boolean isFormatAcceptable(int index, Format format) {
			if (videoFormat == null) {
				videoFormat = new VideoFormat(videoEncoding);
			}
			if (audioFormat == null) {
				audioFormat = new AudioFormat(audioEncoding);
			}
			if (format.matches(videoFormat) || format.matches(audioFormat))
				return true;
			return false;
		}
	}

	class MyPMOut extends ProcessorModel {

		DataSource inputDataSource;

		public MyPMOut(DataSource inputDataSource) {
			this.inputDataSource = inputDataSource;
		}

		
		public ContentDescriptor getContentDescriptor() {
			return new FileTypeDescriptor(outputType);
		}

		
		public DataSource getInputDataSource() {
			return inputDataSource;
		}

		
		public MediaLocator getInputLocator() {
			return null;
		}

		
		public Format getOutputTrackFormat(int index) {
			return null;
		}

		
		public int getTrackCount(int n) {
			return n;
		}

		
		public boolean isFormatAcceptable(int index, Format format) {
			if (videoFormat == null) {
				videoFormat = new VideoFormat(videoEncoding);
			}
			if (audioFormat == null) {
				audioFormat = new AudioFormat(audioEncoding);
			}
			if (format.matches(videoFormat) || format.matches(audioFormat))
				return true;
			return false;
		}
	}

	private void showUsage() {
		System.err.println(Messages.getString("Usage__Merge__url1___url2____url3__________o__out_URL_____v__video_encoding_____a__audio_encoding_____t__content_type__")); 
	}

	public void doSingle(DataSource ds) {

		Processor p = null;
		MediaLocator outML = new MediaLocator(outputFile);

		try {
			p = Manager.createProcessor(ds);
		} catch (Exception e) {
			System.err.println(Messages.getString("Cannot_create_a_processor_from_the_data_source_")); 
		}

		p.addControllerListener(this);

		// Put the Processor into configured state so we can set
		// some processing options on the processor.
		p.configure();
		if (!waitForState(p, Processor.Configured)) {
			System.err.println(Messages.getString("Failed_to_configure_the_processor_")); 
		}

		// Set the output content descriptor to WAVE.
		p.setContentDescriptor(new ContentDescriptor(FileTypeDescriptor.WAVE));

		// Query for the processor for supported formats.
		// Then set it on the processor.
		TrackControl tcs[] = p.getTrackControls();
		Format f[] = tcs[0].getSupportedFormats();
		if (f == null || f.length <= 0) {
			System.err.println(Messages.getString("The_mux_does_not_support_the_input_format__", tcs[0].getFormat()));
		}

		tcs[0].setFormat(f[0]);

		// System.err.println("Setting the track format to: " + f[0]);

		// We are done with programming the processor. Let's just
		// realize it.
		p.realize();
		if (!waitForState(p, Controller.Realized)) {
			System.err.println(Messages.getString("Failed_to_realize_the_processor_")); 
		}

		// Now, we'll need to create a DataSink.
		DataSink dsink;
		while((dsink = createDataSink(p, outML)) == null) {
			//System.err
				//	.println("Failed to create a DataSink for the given output MediaLocator: "
				//			+ outML);
		}

		dsink.addDataSinkListener(this);
		fileDone = false;

		// System.err.println("start processing...");

		// OK, we can now start the actual transcoding.
		try {
			p.start();
			dsink.start();
		} catch (IOException e) {
			// System.err.println("IO error during processing");

		}

		// Wait for EndOfStream event.
		waitForFileDone();

		// Cleanup.
		try {
			dsink.close();
		} catch (Exception e) {
		}
		p.removeControllerListener(this);
		dsink.removeDataSinkListener(this);
		p.close();
		// System.err.println("...done processing.");

	}

	Object waitSync = new Object();

	boolean stateTransitionOK = true;

	/**
	 * Block until the processor has transitioned to the given state. Return
	 * false if the transition failed.
	 */
	boolean waitForState(Processor p, int state) {
		synchronized (waitSync) {
			try {
				while (p.getState() < state && stateTransitionOK)
					waitSync.wait();
			} catch (Exception e) {
			}
		}
		return stateTransitionOK;
	}

	/**
	 * Controller Listener.
	 */
	public void controllerUpdate(ControllerEvent evt) {

		if (evt instanceof ConfigureCompleteEvent
				|| evt instanceof RealizeCompleteEvent
				|| evt instanceof PrefetchCompleteEvent) {
			synchronized (waitSync) {
				stateTransitionOK = true;
				waitSync.notifyAll();
			}
		} else if (evt instanceof ResourceUnavailableEvent) {
			synchronized (waitSync) {
				stateTransitionOK = false;
				waitSync.notifyAll();
			}
		} else if (evt instanceof EndOfMediaEvent) {
			evt.getSourceController().stop();
			evt.getSourceController().close();
		}
	}

	Object waitFileSync = new Object();

	boolean fileDone = false;

	boolean fileSuccess = true;

	/**
	 * Block until file writing is done.
	 */
	boolean waitForFileDone() {
		synchronized (waitFileSync) {
			try {
				while (!fileDone)
					waitFileSync.wait();
			} catch (Exception e) {
			}
		}
		return fileSuccess;
	}

	/**
	 * Event handler for the file writer.
	 */
	public void dataSinkUpdate(DataSinkEvent evt) {

		if (evt instanceof EndOfStreamEvent) {
			synchronized (waitFileSync) {
				fileDone = true;
				waitFileSync.notifyAll();
			}
		} else if (evt instanceof DataSinkErrorEvent) {
			synchronized (waitFileSync) {
				fileDone = true;
				fileSuccess = false;
				waitFileSync.notifyAll();
			}
		}
	}

	DataSink createDataSink(Processor p, MediaLocator outML) {

		DataSource ds;

		if ((ds = p.getDataOutput()) == null) {
			System.err.println(Messages.getString("Something_is_really_wrong__the_processor_does_not_have_an_output_DataSource")); 
			return null;
		}

		DataSink dsink;

		try {
			// System.err.println("- create DataSink for: " + outML);
			dsink = Manager.createDataSink(ds, outML);
			dsink.open();
		} catch (Exception e) {
			//System.err.println("Cannot create the DataSink: " + e);
			return null;
		}

		return dsink;
	}

}
