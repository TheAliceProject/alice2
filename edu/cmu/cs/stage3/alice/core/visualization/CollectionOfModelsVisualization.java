package edu.cmu.cs.stage3.alice.core.visualization;

import edu.cmu.cs.stage3.alice.core.Collection;
import edu.cmu.cs.stage3.alice.core.Model;
import edu.cmu.cs.stage3.alice.core.TextureMap;
import edu.cmu.cs.stage3.alice.core.Variable;

public abstract class CollectionOfModelsVisualization extends edu.cmu.cs.stage3.alice.core.Visualization {
	private java.util.Vector m_bins = new java.util.Vector();
	
    
	public void unhook( Model model ) {
        int i = indexOf( model, 0 );
        if( i != -1 ) {
            set( i, null );
        }
    }

	protected String getItemsName() {
		return "items";
	}
    private Variable m_itemsVariable = null;
    private Variable getItemsVariable() {
        if( m_itemsVariable == null ) {
            m_itemsVariable = (Variable)getChildNamed( getItemsName() );
        }
        return m_itemsVariable;
    }
    public Collection getItemsCollection() {
        return (Collection)getItemsVariable().value.getValue();
    }
    public Model[] getItems() {
        return (Model[])getItemsCollection().values.getArrayValue();
    }
    public void setItems( Model[] items ) {
        getItemsCollection().values.set( items );
    }

    private Model getPrototype() {
        return (Model)getChildNamed( "BinPrototype" );
    }
    private int getBinCount() {
        return m_bins.size();
    }
    private Model getBinAt( int i ) {
        return (Model)m_bins.get( i );
    }
    private void setBinAt( int i, Model bin ) {
    	if( m_bins.size() == i ) {
			m_bins.addElement( bin );
    	} else {
    		if( m_bins.size() < i ) {
    			m_bins.ensureCapacity( i+1 );
    		}
			m_bins.set( i, bin );
    	}
    }

    private static final java.awt.Font s_font = new java.awt.Font( "Serif", java.awt.Font.PLAIN, 32 );
    private static TextureMap getEmptyTextureMap( Model bin ) {
        return (TextureMap)bin.getChildNamed( "EmptyTexture" );
    }
    private static TextureMap getFilledTextureMap( Model bin ) {
        return (TextureMap)bin.getChildNamed( "FilledTexture" );
    }

    private static void decorateTextureMap( TextureMap skin, int i ) {
        if( skin != null ) {
            java.awt.Image originalImage = skin.image.getImageValue();
            if( originalImage instanceof java.awt.image.BufferedImage ) {
                java.awt.image.BufferedImage originalBufferedImage = (java.awt.image.BufferedImage)originalImage;
                java.awt.Image image = new java.awt.image.BufferedImage( originalBufferedImage.getWidth(), originalBufferedImage.getHeight(), java.awt.image.BufferedImage.TYPE_INT_ARGB );
                java.awt.Graphics g = image.getGraphics();
                g.drawImage( originalImage, 0, 0, null );
                g.setFont( s_font );
                String s = Integer.toString( i );
                java.awt.FontMetrics fm = g.getFontMetrics();
                java.awt.geom.Rectangle2D r = fm.getStringBounds( s, g );
                g.setColor( java.awt.Color.black );
                g.drawString( s, 80, (int)((20-r.getX())+r.getHeight()) );
                g.dispose();
                skin.image.set( image );
                skin.touchImage();
            }
        }
    }

    private void synchronize( Model[] curr ) {
        int binCount = getBinCount();
        for( int i=binCount-1; i>=curr.length; i-- ) {
            Model binI = getBinAt( i );
            binI.vehicle.set( null );
			//binI.removeFromParent();
            m_bins.remove( binI );
        }
        Model prototype = getPrototype();
        if( prototype != null ) {
            for( int i=binCount; i<curr.length; i++ ) {
                Class[] share = { edu.cmu.cs.stage3.alice.core.Geometry.class };
				String name = "Sub"+i;
                Model binI = (Model) getChildNamed( name );
                if( binI == null ) {
                	binI = (Model)prototype.HACK_createCopy( name, this, -1, share, null );
					decorateTextureMap( getEmptyTextureMap( binI ), i );
					decorateTextureMap( getFilledTextureMap( binI ), i );
                }
                setBinAt( i, binI );
            }