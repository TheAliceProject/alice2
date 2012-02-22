/*
 * Copyright (c) 1999-2003, Carnegie Mellon University. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 * 
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 
 * 3. Products derived from the software may not be called "Alice",
 *    nor may "Alice" appear in their name, without prior written
 *    permission of Carnegie Mellon University.
 * 
 * 4. All advertising materials mentioning features or use of this software
 *    must display the following acknowledgement:
 *    "This product includes software developed by Carnegie Mellon University"
 */

package edu.cmu.cs.stage3.io;

/**
 * @author Jason Pratt
 */
public class FileSystemTreeStorer implements DirectoryTreeStorer {
	protected java.io.File root = null;
	protected java.io.File currentDirectory = null;
	protected java.io.OutputStream currentlyOpenStream = null;

	/**
	 * pathname can be a String or java.io.File
	 */
	public void open( Object pathname ) throws IllegalArgumentException, java.io.IOException {
		if( root != null ) {
			close();
		}

		if( pathname instanceof String ) {
			root = new java.io.File( (String)pathname );
		}
		else if( pathname instanceof java.io.File ) {
			root = (java.io.File)pathname;
		}
		else {
			throw new IllegalArgumentException( Messages.getString("FileSystemTreeStorer.0") ); //$NON-NLS-1$
		}

		if( root.exists() ) {
			if( ! root.canWrite() ) {
				throw new java.io.IOException( Messages.getString("FileSystemTreeStorer.1") + root ); //$NON-NLS-1$
			}
		}
		else {
			if( ! root.mkdir() ) {
				throw new java.io.IOException( Messages.getString("FileSystemTreeStorer.2") + root ); //$NON-NLS-1$
			}
			if( ! root.canWrite() ) {
				throw new java.io.IOException( Messages.getString("FileSystemTreeStorer.3") + root ); //$NON-NLS-1$
			}
		}

		currentDirectory = root;
	}

	public void close() throws java.io.IOException {
		closeCurrentFile();
		root = null;
		currentDirectory = null;

		//TODO: handle keepFile cleanup
	}

	public void createDirectory( String pathname ) throws IllegalArgumentException, java.io.IOException {
		if( pathname.indexOf( '/' ) != -1 ) {
			throw new IllegalArgumentException( Messages.getString("FileSystemTreeStorer.4") ); //$NON-NLS-1$
		}
		if( pathname.length() <= 0 ) {
			throw new IllegalArgumentException( Messages.getString("FileSystemTreeStorer.5") ); //$NON-NLS-1$
		}
		java.io.File newDir = new java.io.File( currentDirectory, pathname );
		if( ! newDir.exists() ) {
			if( ! newDir.mkdir() ) {
				throw new java.io.IOException( Messages.getString("FileSystemTreeStorer.6") + newDir ); //$NON-NLS-1$
			}
		}
	}

	public void setCurrentDirectory( String pathname ) throws IllegalArgumentException {
		java.io.File newCurrentDirectory;
		if( (pathname.length() == 0) || (pathname.charAt( 0 ) == '/') || (pathname.charAt( 0 ) == '\\') ) {
			newCurrentDirectory = new java.io.File( root.getAbsolutePath() + pathname );
		}
		else {
			newCurrentDirectory = new java.io.File( currentDirectory.getAbsolutePath() + "/" + pathname ); //$NON-NLS-1$
		}

		if( ! newCurrentDirectory.exists() ) {
			throw new IllegalArgumentException( newCurrentDirectory + Messages.getString("FileSystemTreeStorer.8") ); //$NON-NLS-1$
		}
		if( ! newCurrentDirectory.isDirectory() ) {
			throw new IllegalArgumentException( newCurrentDirectory + Messages.getString("FileSystemTreeStorer.9") ); //$NON-NLS-1$
		}

		currentDirectory = newCurrentDirectory;
	}

	public String getCurrentDirectory() {
		StringBuffer dir = new StringBuffer( currentDirectory.getAbsolutePath() );
		dir.delete( 0, root.getAbsolutePath().length() );
		return dir.toString();
	}

	public java.io.OutputStream createFile( String filename, boolean compressItIfYouGotIt ) throws IllegalArgumentException, java.io.IOException {
		java.io.File newFile = new java.io.File( currentDirectory, filename );
		if( ! newFile.exists() ) {
			if( ! newFile.createNewFile() ) {
				throw new java.io.IOException( Messages.getString("FileSystemTreeStorer.10") + newFile ); //$NON-NLS-1$
			}
		}
		if( ! newFile.canWrite() ) {
			throw new java.io.IOException( Messages.getString("FileSystemTreeStorer.11") + newFile ); //$NON-NLS-1$
		}

		currentlyOpenStream = new java.io.FileOutputStream( newFile );
		return currentlyOpenStream;
	}

	public void closeCurrentFile() throws java.io.IOException {
		if( currentlyOpenStream != null ) {
			currentlyOpenStream.flush();
			currentlyOpenStream.close();
			currentlyOpenStream = null;
		}
	}

	public Object getKeepKey( String filename ) throws KeepFileNotSupportedException {
		return FileSystemTreeLoader.getKeepKey( currentDirectory, filename );
	}

	public boolean isKeepFileSupported() {
		return true;
	}

	public void keepFile( String filename ) throws KeepFileNotSupportedException, KeepFileDoesNotExistException {
		java.io.File file = new java.io.File( currentDirectory, filename );
		if( ! file.exists() ) {
			throw new KeepFileDoesNotExistException( currentDirectory.getAbsolutePath(), filename );
		}
	}
}