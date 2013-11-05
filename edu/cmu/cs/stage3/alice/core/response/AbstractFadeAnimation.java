/*
 * Created on Jun 3, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package edu.cmu.cs.stage3.alice.core.response;

/**
 * @author caitlin
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class AbstractFadeAnimation extends Animation {
	public final edu.cmu.cs.stage3.alice.core.property.ReferenceFrameProperty subject = new edu.cmu.cs.stage3.alice.core.property.ReferenceFrameProperty( this, "subject", null );
	
	protected static java.awt.Color atmosphereColor = new java.awt.Color(72, 72, 72);
	protected static java.awt.Color ambientLightColor = new java.awt.Color(74, 125, 204);
	protected static boolean currentlyBlack = false;	
	
	// these ones are for objects with an emissive color that's not black
	protected static java.util.Vector properties = new java.util.Vector();
	protected static java.util.Vector origPropertyValues = new java.util.Vector();	
	
	//	these ones are for objects with an emissive color that's not black
	protected static java.util.Vector specularProperties = new java.util.Vector();
	protected static java.util.Vector origSpecularValues = new java.util.Vector();	
	
	// these are to find all the lights in the world 
	protected static java.util.Vector lightProperties = new java.util.Vector();
	protected static java.util.Vector origLightValues = new java.util.Vector();	
	
	public abstract class RuntimeAbstractFadeAnimation extends RuntimeAnimation {
		
		protected edu.cmu.cs.stage3.alice.core.property.ColorProperty m_atmosphereColorProp = null;
		protected edu.cmu.cs.stage3.alice.core.property.ColorProperty m_ambientLightColorProp = null;
		
		// emissive color
		protected java.util.Vector m_beginPropColors = new java.util.Vector();
		protected java.util.Vector m_endPropColors = new java.util.Vector();
		
		// specular highlight color
	    protected java.util.Vector m_beginSpecularColors = new java.util.Vector();
	    protected java.util.Vector m_endSpecularColors = new java.util.Vector();	
		
		// light brightness
		protected java.util.Vector m_beginPropBrightness = new java.util.Vector();
		protected java.util.Vector m_endPropBrightness = new java.util.Vector();		
	
		protected java.awt.Color m_beginAtmosphereColor = null;
		protected java.awt.Color m_endAtmosphereColor = null;
		
		protected java.awt.Color m_beginAmbientLightColor = null;
		protected java.awt.Color m_endAmbientLightColor = null;
		
		protected double m_beginBrightness = -1;
		protected double m_endBrightness = -1;
		
		protected abstract boolean endsBlack();

		
		
		public void prologue( double t ) {
			
			super.prologue(t);
			
			// need to extract the right properties
			edu.cmu.cs.stage3.alice.core.World world = AbstractFadeAnimation.this.getWorld();
			m_atmosphereColorProp = (edu.cmu.cs.stage3.alice.core.property.ColorProperty)world.getPropertyNamed("atmosphereColor");
			m_ambientLightColorProp = (edu.cmu.cs.stage3.alice.core.property.ColorProperty)world.getPropertyNamed("ambientLightColor");
			
			// if screen is not currently black, save the current atmosphere/lighting conditions to restore
			if (!currentlyBlack) {		
				atmosphereColor = m_atmosphereColorProp.getColorValue().createAWTColor(); 
				ambientLightColor = m_ambientLightColorProp.getColorValue().createAWTColor();			
				
				//	find objects with emissive colors that aren't black
				properties.clear();
				origPropertyValues.clear();
				edu.cmu.cs.stage3.alice.core.Element[] els = world.search(new edu.cmu.cs.stage3.alice.core.criterion.ElementWithPropertyNameValueCriterion("emissiveColor", new edu.cmu.cs.stage3.alice.scenegraph.Color(0,0,0), false));
				//this list is pretty long, so pare out objects if we already have their "rootparent" 
				for (int i = 0; i < els.length; i++){
					String key = els[i].getKey();		
					edu.cmu.cs.stage3.alice.core.property.ColorProperty emissColorProp = (edu.cmu.cs.stage3.alice.core.property.ColorProperty)els[i].getPropertyNamed("emissiveColor");
					
					properties.addElement(emissColorProp);
					origPropertyValues.addElement(emissColorProp.getColorValue().createAWTColor());
					
				}
				
				//find objects with specular highlight colors that aren't black
				specularProperties.clear();
				origSpecularValues.clear();
				els = world.search(new edu.cmu.cs.stage3.alice.core.criterion.ElementWithPropertyNameValueCriterion("specularHighlightColor", new edu.cmu.cs.stage3.alice.scenegraph.Color(0,0,0), false));
				//this list is pretty long, so pare out objects if we already have their "rootparent" 
				for (int i = 0; i < els.length; i++){
					String key = els[i].getKey();		
					edu.cmu.cs.stage3.alice.core.property.ColorProperty emissColorProp = (edu.cmu.cs.stage3.alice.core.property.ColorProperty)els[i].getPropertyNamed("specularHighlightColor");
		
					properties.addElement(emissColorProp);
					origPropertyValues.addElement(emissColorProp.getColorValue().createAWTColor());
		
				}
				
				//find lights with non-zero brightness
				lightProperties.clear();
				origLightValues.clear();
				els = world.search(new edu.cmu.cs.stage3.alice.core.criterion.ElementWithPropertyNameValueCriterion("brightness", new Double(0.0), false));
				//this list is pretty long, so pare out objects if we already have their "rootparent" 
				for (int i = 0; i < els.length; i++){	
					edu.cmu.cs.stage3.alice.core.property.NumberProperty brightnessProp = (edu.cmu.cs.stage3.alice.core.property.NumberProperty)els[i].getPropertyNamed("brightness");	
					lightProperties.addElement(brightnessProp);
					origLightValues.addElement(brightnessProp.getNumberValue());
				}
			}
			
			m_beginPropColors.clear();
			m_endPropColors.clear();
			
			m_beginSpecularColors.clear();
			m_endSpecularColors.clear();
			
			m_beginPropBrightness.clear();
			m_endPropBrightness.clear();
	
			
			if (endsBlack()) {
				m_beginAtmosphereColor = m_atmosphereColorProp.getColorValue().createAWTColor();
				m_endAtmosphereColor = new java.awt.Color(0,0,0);
				
				m_beginAmbientLightColor = m_atmosphereColorProp.getColorValue().createAWTColor();
				m_endAmbientLightColor =  new java.awt.Color(0,0,0);
				
				//emissive color
				for (int i = 0; i < properties.size(); i++) {
					edu.cmu.cs.stage3.alice.core.property.ColorProperty colorProp = (edu.cmu.cs.stage3.alice.core.property.ColorProperty) properties.elementAt(i);
					m_beginPropColors.addElement(colorProp.getColorValue().createAWTColor());
					m_endPropColors.addElement(new java.awt.Color(0,0,0));
				}
				
				//	specular highlight color
			    for (int i = 0; i < specularProperties.size(); i++) {
				    edu.cmu.cs.stage3.alice.core.property.ColorProperty colorProp = (edu.cmu.cs.stage3.alice.core.property.ColorProperty) specularProperties.elementAt(i);
				    m_beginSpecularColors.addElement(colorProp.getColorValue().createAWTColor());
				    m_endSpecularColors.addElement(new java.awt.Color(0,0,0));
			    }
			    
				// light brightness
				for (int i = 0; i < lightProperties.size(); i++) {
					edu.cmu.cs.stage3.alice.core.property.NumberProperty numberProp = (edu.cmu.cs.stage3.alice.core.property.NumberProperty) lightProperties.elementAt(i);
					m_beginPropBrightness.addElement(numberProp.getNumberValue());
					m_endPropBrightness.addElement(new Double(0.0));
				}
			} else {
				m_beginAtmosphereColor = m_atmosphereColorProp.getColorValue().createAWTColor();
				m_endAtmosphereColor = atmosphereColor;
				
				m_beginAmbientLightColor = m_ambientLightColorProp.getColorValue().createAWTColor();
				m_endAmbientLightColor =  ambientLightColor;
						
				//emissive color
				for (int i = 0; i < properties.size(); i++) {
					edu.cmu.cs.stage3.alice.core.property.ColorProperty colorProp = (edu.cmu.cs.stage3.alice.core.property.ColorProperty) properties.elementAt(i);
					m_beginPropColors.addElement(colorProp.getColorValue().createAWTColor());
					m_endPropColors.addElement(origPropertyValues.elementAt(i));
				}
				//specular highlight color
				for (int i = 0; i < specularProperties.size(); i++) {
					edu.c