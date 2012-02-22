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
import java.io.IOException;
//import java.net.MalformedURLException;

import javax.media.*;
import javax.media.format.*;
import javax.media.protocol.*;
import javax.media.control.TrackControl;
import javax.media.datasink.*;

import java.util.Vector;

/**
 * Merged the tracks from different inputs and generate a QuickTime file with
 * the all the merged tracks.
 */
public class Merge implements ControllerListener, DataSinkListener {

	Processor[] processors = null;

	String outputFile = null;

	String videoEncoding = "JPEG"; //$NON-NLS-1$

	String audioEncoding = "LINEAR"; //$NON-NLS-1$

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
				System.err.println(Messages.getString("Merge.2") + e); //$NON-NLS-1$
			}
		}

		// Merge the data sources from the individual processors
		try {
			merger = Manager.createMergingDataSource(dataOutputs);
			merger.connect();
			merger.start();
		} catch (Exception ex) {
			System.err.println(Messages.getString("Merge.3") + ex); //$NON-NLS-1$

		}
		if (merger == null) {
			System.err.println(Messages.getString("Merge.4")); //$NON-NLS-1$

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
			System.err.println(Messages.getString("Merge.5") + exc); //$NON-NLS-1$

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
			System.err.println(Messages.getString("Merge.6") + excep); //$NON-NLS-1$

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
			else
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
			else
				return false;
		}
	}

	private void showUsage() {
		System.err
				.println(Messages.getString("Merge.7")); //$NON-NLS-1$
	}

	public void doSingle(DataSource ds) {

		Processor p = null;
		MediaLocator outML = new MediaLocator(outputFile);

		try {
			// System.err.println("- create processor for the image datasource
			// ...");
			p = Manager.createProcessor(ds);
		} catch (Exception e) {
			System.err.println(Messages.getString("Merge.8")); //$NON-NLS-1$
		}

		p.addControllerListener(this);

		// Put the Processor into configured state so we can set
		// some processing options on the processor.
		p.configure();
		if (!waitForState(p, Processor.Configured)) {
			System.err.println(Messages.getString("Merge.9")); //$NON-NLS-1$
		}

		// Set the output content descriptor to WAVE.
		p.setContentDescriptor(new ContentDescriptor(FileTypeDescriptor.WAVE));

		// Query for the processor for supported formats.
		// Then set it on the processor.
		TrackControl tcs[] = p.getTrackControls();
		Format f[] = tcs[0].getSupportedFormats();
		if (f == null || f.length <= 0) {
			System.err.println(Messages.getString("Merge.10") //$NON-NLS-1$
					+ tcs[0].getFormat());
		}

		tcs[0].setFormat(f[0]);

		// System.err.println("Setting the track format to: " + f[0]);

		// We are done with programming the processor. Let's just
		// realize it.
		p.realize();
		if (!waitForState(p, Controller.Realized)) {
			System.err.println(Messages.getString("Merge.11")); //$NON-NLS-1$
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
			System.err
					.println(Messages.getString("Merge.12")); //$NON-NLS-1$
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
