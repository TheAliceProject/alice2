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

package edu.cmu.cs.stage3.alice.scenegraph.renderer.java3drenderer;

class AppearanceProxy extends ElementProxy {
    private TextureMapProxy m_diffuseColorMap = null;
    private javax.media.j3d.Appearance m_j3dAppearance = new javax.media.j3d.Appearance();
    private javax.media.j3d.Material m_j3dMaterial = new javax.media.j3d.Material();
    private javax.media.j3d.ColoringAttributes m_j3dColoringAttributes = new javax.media.j3d.ColoringAttributes();
    private javax.media.j3d.PolygonAttributes m_j3dPolygonAttributes = new javax.media.j3d.PolygonAttributes();
    private javax.media.j3d.LineAttributes m_j3dLineAttributes = new javax.media.j3d.LineAttributes();
    private javax.media.j3d.PointAttributes m_j3dPointAttributes = new javax.media.j3d.PointAttributes();
    private javax.media.j3d.RenderingAttributes m_j3dRenderingAttributes = new javax.media.j3d.RenderingAttributes();
    private javax.media.j3d.TextureAttributes m_j3dTextureAttributes = new javax.media.j3d.TextureAttributes();
    protected javax.media.j3d.Appearance getJ3DAppearance() {
        return m_j3dAppearance;
    }
    protected void initJ3D() {
        super.initJ3D();
        m_j3dAppearance.setCapability( javax.media.j3d.Appearance.ALLOW_TEXTURE_WRITE );
        m_j3dMaterial.setCapability( javax.media.j3d.Material.ALLOW_COMPONENT_WRITE );

        m_j3dPolygonAttributes.setCapability( javax.media.j3d.PolygonAttributes.ALLOW_MODE_WRITE );
        m_j3dPolygonAttributes.setCapability( javax.media.j3d.PolygonAttributes.ALLOW_CULL_FACE_WRITE );
        m_j3dPolygonAttributes.setCullFace( javax.media.j3d.PolygonAttributes.CULL_BACK );
        m_j3dPolygonAttributes.setBackFaceNormalFlip( true );

        m_j3dColoringAttributes.setCapability( javax.media.j3d.ColoringAttributes.ALLOW_COLOR_WRITE );
        m_j3dColoringAttributes.setCapability( javax.media.j3d.ColoringAttributes.ALLOW_SHADE_MODEL_WRITE );

        m_j3dTextureAttributes.setTextureMode( javax.media.j3d.TextureAttributes.MODULATE );

        /*
        javax.media.j3d.RenderingAttributes ra = new javax.media.j3d.RenderingAttributes();
        ra.setDepthBufferEnable( true );
        ra.setDepthBufferWriteEnable( true );
        ra.setAlphaTestValue( 0.0f );
        ra.setAlphaTestFunction( ra.ALWAYS );
*/
        javax.media.j3d.TransparencyAttributes ta = new javax.media.j3d.TransparencyAttributes();
        //ta.setCapability( javax.media.j3d.TransparencyAttributes.ALLOW_VALUE_READ );
        //ta.setCapability( javax.media.j3d.TransparencyAttributes.ALLOW_VALUE_WRITE );
        ta.setTransparencyMode( javax.media.j3d.TransparencyAttributes.NICEST );
        ta.setTransparency( (float)(0.0) );
        

        m_j3dAppearance.setMaterial( m_j3dMaterial );
        m_j3dAppearance.setColoringAttributes( m_j3dColoringAttributes );
        m_j3dAppearance.setPointAttributes( m_j3dPointAttributes );
        m_j3dAppearance.setLineAttributes( m_j3dLineAttributes );
        m_j3dAppearance.setPolygonAttributes( m_j3dPolygonAttributes );
        m_j3dAppearance.setTextureAttributes( m_j3dTextureAttributes );
        //m_j3dAppearance.setRenderingAttributes( ra );
        m_j3dAppearance.setTransparencyAttributes( ta );
    }

    public void updateJ3DTexture( javax.media.j3d.Texture j3dTexture ) {
        m_j3dAppearance.setTexture( j3dTexture );
    }

	protected void changed( edu.cmu.cs.stage3.alice.scenegraph.Property property, Object value ) {
		if( property == edu.cmu.cs.stage3.alice.scenegraph.Appearance.AMBIENT_COLOR_PROPERTY ) {
			edu.cmu.cs.stage3.alice.scenegraph.Color color = (edu.cmu.cs.stage3.alice.scenegraph.Color)value;
			if( color==null ) {
				color = ((edu.cmu.cs.stage3.alice.scenegraph.Appearance)getSceneGraphElement()).getDiffuseColor();
			}
			m_j3dMaterial.setAmbientColor( color.createVecmathColor3f() );
		} else if( property == edu.cmu.cs.stage3.alice.scenegraph.Appearance.DIFFUSE_COLOR_PROPERTY ) {
			edu.cmu.cs.stage3.alice.scenegraph.Color color = (edu.cmu.cs.stage3.alice.scenegraph.Color)value;
			m_j3dMaterial.setDiffuseColor( color.createVecmathColor3f() );
			if( ((edu.cmu.cs.stage3.alice.scenegraph.Appearance)getSceneGraphElement()).getAmbientColor() == null ) {
				changed( edu.cmu.cs.stage3.alice.scenegraph.Appearance.AMBIENT_COLOR_PROPERTY, color );
			}
		} else if( property == edu.cmu.cs.stage3.alice.scenegraph.Appearance.FILLING_STYLE_PROPERTY ) {
			if( value.equals( edu.cmu.cs.stage3.alice.scenegraph.FillingStyle.SOLID ) ) {
                m_j3dPolygonAttributes.setPolygonMode( javax.media.j3d.PolygonAttributes.POLYGON_FILL );
			} else if( value.equals( edu.cmu.cs.stage3.alice.scenegraph.FillingStyle.WIREFRAME ) ) {
                m_j3dPolygonAttributes.setPolygonMode( javax.media.j3d.PolygonAttributes.POLYGON_LINE );
			} else if( value.equals( edu.cmu.cs.stage3.alice.scenegraph.FillingStyle.POINTS ) ) {
                m_j3dPolygonAttributes.setPolygonMode( javax.media.j3d.PolygonAttributes.POLYGON_POINT );
			} else {
				throw new RuntimeException();
			}
		} else if( property == edu.cmu.cs.stage3.alice.scenegraph.Appearance.SHADING_STYLE_PROPERTY ) {
			if( value==null || value.equals( edu.cmu.cs.stage3.alice.scenegraph.ShadingStyle.NONE ) ) {
                m_j3dMaterial.setLightingEnable( false );
			} else if( value.equals( edu.cmu.cs.stage3.alice.scenegraph.ShadingStyle.FLAT ) ) {
                m_j3dMaterial.setLightingEnable( true );
                m_j3dColoringAttributes.setShadeModel( javax.media.j3d.ColoringAttributes.SHADE_FLAT );
			} else if( value.equals( edu.cmu.cs.stage3.alice.scenegraph.ShadingStyle.SMOOTH ) ) {
                m_j3dMaterial.setLightingEnable( true );
                m_j3dColoringAttributes.setShadeModel( javax.media.j3d.ColoringAttributes.SHADE_GOURAUD );
			} else {
				throw new RuntimeException();
			}
		} else if( property == edu.cmu.cs.stage3.alice.scenegraph.Appearance.OPACITY_PROPERTY ) {
            // Aik Min - code for opacity.
	        javax.media.j3d.TransparencyAttributes ta = new javax.media.j3d.TransparencyAttributes();
	        ta.setTransparencyMode( javax.media.j3d.TransparencyAttributes.BLEND_ONE_MINUS_SRC_ALPHA );
	        ta.setTransparency( (float)1.0 - ((Double)value).floatValue());
	        m_j3dAppearance.setTransparencyAttributes( ta );

		} else if( property == edu.cmu.cs.stage3.alice.scenegraph.Appearance.SPECULAR_HIGHLIGHT_COLOR_PROPERTY ) {
			edu.cmu.cs.stage3.alice.scenegraph.Color color = (edu.cmu.cs.stage3.alice.scenegraph.Color)value;
			m_j3dMaterial.setSpecularColor( color.createVecmathColor3f() );
		} else if( property == edu.cmu.cs.stage3.alice.scenegraph.Appearance.SPECULAR_HIGHLIGHT_EXPONENT_PROPERTY ) {
			m_j3dMaterial.setShininess( ((Number)value).floatValue() );
		} else if( property == edu.cmu.cs.stage3.alice.scenegraph.Appearance.EMISSIVE_COLOR_PROPERTY ) {
			edu.cmu.cs.stage3.alice.scenegraph.Color color = (edu.cmu.cs.stage3.alice.scenegraph.Color)value;
			m_j3dMaterial.setEmissiveColor( color.createVecmathColor3f() );
		} else if( property == edu.cmu.cs.stage3.alice.scenegraph.Appearance.DIFFUSE_COLOR_MAP_PROPERTY ) {
            if( m_diffuseColorMap != null ) {
                m_diffuseColorMap.removeAppearance( this );
            }
            m_diffuseColorMap = (TextureMapProxy)getProxyFor( (edu.cmu.cs.stage3.alice.scenegraph.TextureMap)value );
            if( m_diffuseColorMap != null ) {
                m_diffuseColorMap.addAppearance( this );
                updateJ3DTexture( m_diffuseColorMap.getJ3DTexture() );
            } else {
                updateJ3DTexture( null );
            }
		} else if( property == edu.cmu.cs.stage3.alice.scenegraph.Appearance.OPACITY_MAP_PROPERTY ) {
            //todo
		} else if( property == edu.cmu.cs.stage3.alice.scenegraph.Appearance.EMISSIVE_COLOR_MAP_PROPERTY ) {
            //todo
		} else if( property == edu.cmu.cs.stage3.alice.scenegraph.Appearance.SPECULAR_HIGHLIGHT_COLOR_MAP_PROPERTY ) {
            //todo
		} else if( property == edu.cmu.cs.stage3.alice.scenegraph.Appearance.BUMP_MAP_PROPERTY ) {
            //todo
		} else if( property == edu.cmu.cs.stage3.alice.scenegraph.Appearance.DETAIL_MAP_PROPERTY ) {
            //todo
		} else {
			super.changed( property, value );
		}
	}
}
