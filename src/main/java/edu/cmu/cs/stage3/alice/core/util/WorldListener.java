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

package edu.cmu.cs.stage3.alice.core.util;

public abstract class WorldListener {
	private edu.cmu.cs.stage3.alice.core.World m_world;
	private edu.cmu.cs.stage3.alice.core.event.ChildrenListener m_childrenListener = new edu.cmu.cs.stage3.alice.core.event.ChildrenListener() {
		public void childrenChanging( edu.cmu.cs.stage3.alice.core.event.ChildrenEvent e ) {
			WorldListener.this.handleChildrenChanging( e );
		}
		public void childrenChanged( edu.cmu.cs.stage3.alice.core.event.ChildrenEvent e ) {
			if( e.getChangeType() == edu.cmu.cs.stage3.alice.core.event.ChildrenEvent.CHILD_INSERTED ) {
				WorldListener.this.hookUp( e.getChild() );
			} else if( e.getChangeType() == edu.cmu.cs.stage3.alice.core.event.ChildrenEvent.CHILD_REMOVED ) {
				WorldListener.this.unhookUp( e.getChild() );
			}
			WorldListener.this.handleChildrenChanged( e );
		}
	};
	private edu.cmu.cs.stage3.alice.core.event.PropertyListener m_propertyListener = new edu.cmu.cs.stage3.alice.core.event.PropertyListener() {
		public void propertyChanging( edu.cmu.cs.stage3.alice.core.event.PropertyEvent e ) {
			WorldListener.this.handlePropertyChanging( e );
		}
		public void propertyChanged( edu.cmu.cs.stage3.alice.core.event.PropertyEvent e ) {
			WorldListener.this.handlePropertyChanged( e );
		}
	};
	private edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyListener m_objectArrayPropertyListener = new edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyListener() {
		public void objectArrayPropertyChanging( edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyEvent e ) {
			WorldListener.this.handleObjectArrayPropertyChanging( e );
		}
		public void objectArrayPropertyChanged( edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyEvent e ) {
			WorldListener.this.handleObjectArrayPropertyChanged( e );
		}
	};

	protected abstract void handleChildrenChanging( edu.cmu.cs.stage3.alice.core.event.ChildrenEvent e );
	protected abstract void handleChildrenChanged( edu.cmu.cs.stage3.alice.core.event.ChildrenEvent e );
	protected abstract void handlePropertyChanging( edu.cmu.cs.stage3.alice.core.event.PropertyEvent e );
	protected abstract void handlePropertyChanged( edu.cmu.cs.stage3.alice.core.event.PropertyEvent e );
	protected abstract void handleObjectArrayPropertyChanging( edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyEvent e );
	protected abstract void handleObjectArrayPropertyChanged( edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyEvent e );

	protected abstract boolean isPropertyListeningRequired( edu.cmu.cs.stage3.alice.core.Property property );
	protected abstract boolean isObjectArrayPropertyListeningRequired( edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty oap );

	public edu.cmu.cs.stage3.alice.core.World getWorld() {
		return m_world;
	}
	public void setWorld( edu.cmu.cs.stage3.alice.core.World world ) {
		if( m_world != world ) {
			if( m_world != null ) {
				unhookUp( m_world );
			}
			m_world = world;
			if( m_world != null ) {
				hookUp( m_world );
			}
		}
	}
	
	private boolean isChildrenListenerHookedUp( edu.cmu.cs.stage3