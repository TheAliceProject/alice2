package edu.cmu.cs.stage3.io;

import edu.cmu.cs.stage3.lang.Messages;

public class FileUtilities {
	private static boolean s_successfullyLoadedLibrary;
	
	public static final int DIRECTORY_DOES_NOT_EXIST = -1;
	public static final int DIRECTORY_IS_NOT_WRITABLE = -2;
	public static final int DIRECTORY_IS_WRITABLE = 1;
	public static final int BAD_DIRECTORY_INPUT = -3;
	
	static {
		try {
			System.loadLibrary( "jni_fileutilities" ); 
			s_successfullyLoadedLibrary = true;
			//} catch( UnsatisfiedLinkError ule ) {
		} catch( Throwable t ) {
			s_successfullyLoadedLibrary = false;
		}
	}
	public static boolean isFileCopySupported() {
		return s_successfullyLoadedLibrary;
	}
	
	private static native boolean copy( String srcPath, String dstPath, boolean overwriteIfNecessary, edu.cmu.cs.stage3.progress.ProgressObserver progressObserver ) throws edu.cmu.cs.stage3.progress.ProgressCancelException;
	public static boolean copy( java.io.File src, java.io.File dst, boolean overwriteIfNecessary, edu.cmu.cs.stage3.progress.ProgressObserver progressObserver ) throws edu.cmu.cs.stage3.progress.ProgressCancelException {
		if( isFileCopySupported() ) {
			dst.getParentFile().mkdirs();
			if( progressObserver != null ) {
				progressObserver.progressBegin( edu.cmu.cs.stage3.progress.ProgressObserver.UNKNOWN_TOTAL );
			}
			try {
				return copy( src.getAbsolutePath(), dst.getAbsolutePath(), overwriteIfNecessary, progressObserver );
			} finally {
				if( progressObserver != null ) {
					progressObserver.progressEnd();
				}
			}
		} else {
			throw new RuntimeException( Messages.getString("file_copy_not_supported") ); 
		}
	}
	public static void copy( java.io.File src, java.io.File dst, boolean overwriteIfNecessary ) {
		try {
			copy( src, dst, overwriteIfNecessary, null );
		} catch( edu.cmu.cs.stage3.progress.ProgressCancelException pce ) {
			throw new Error( Messages.getString("caught_ProgressCancelException_without_ProgressObserver") ); 
		}
	}

	public static String getExtension( String filename ) {
		String extension = null;
		if( filename != null ) {
			int index = filename.lastIndexOf( '.' );
			if( index != -1 ) {
				extension = filename.substring( index+1 );
			}
		}
		return extension;
	}
	public static String getExtension( java.io.File file ) {
		if( file != null ) {
			return getExtension( file.getName() );
		} else {
			return null;
		}
	}

	public static String getBaseName( String filename ) {
		String basename = null;
		if( filename != null ) {
			int index = filename.lastIndexOf( '.' );
			if( index != -1 ) {
				basename = filename.substring( 0, index );
			} else {
				basename = filename;
			}
		}
		return basename;
	}
	public static String getBaseName( java.io.File file ) {
		if( file != null ) {
			return getBaseName( file.getName() );
		} else {
			return null;
		}
	}
	
	public static int isWritableDirectory( java.io.File directory ){
		if (directory == null || !directory.isDirectory()){
			return BAD_DIRECTORY_INPUT;
		}
		java.io.File testFile = new java.io.File(directory, "test.test"); 
		boolean writable;
		if ( testFile.exists() ) {
			writable = testFile.canWrite();
		} else {
			try{
				boolean success = testFile.createNewFile();
				writable = success;
			} catch (Throwable t){
				writable = false;
			} finally{
				testFile.delete();
			}
		}		
		if (!writable){
			return DIRECTORY_IS_NOT_WRITABLE;
		}
		if( (!directory.exists()) || (!directory.canRead())){
			return DIRECTORY_DOES_NOT_EXIST;
		}
		return DIRECTORY_IS_WRITABLE;
	}

/*	public static void main( String[] args ) {
		edu.cmu.cs.stage3.progress.ProgressObserver progressObserver = new edu.cmu.cs.stage3.progress.ProgressObserver() {
			public void progressBegin( int total ) {
				System.err.println( "progressBegin: " + total );
			}
			public void progressUpdateTotal( int total ) {
				System.err.println( "progressUpdateTotal: " + total );
			}
			public void progressUpdate( int current, String description ) throws edu.cmu.cs.stage3.progress.ProgressCancelException {
				if( current > 1000000 ) {
					throw new edu.cmu.cs.stage3.progress.P