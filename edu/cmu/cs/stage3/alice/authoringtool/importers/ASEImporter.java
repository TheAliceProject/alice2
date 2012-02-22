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

package edu.cmu.cs.stage3.alice.authoringtool.importers;

/**
 * @author Jason Pratt
 */
public class ASEImporter extends edu.cmu.cs.stage3.alice.authoringtool.AbstractImporter {
	protected java.io.StreamTokenizer tokenizer;
	protected java.util.HashMap modelsToParentStrings;
	protected java.util.HashMap namesToModels;
	protected java.util.HashMap namesToMaterials;
	protected java.util.HashMap modelsToMaterialIndices;
	protected java.util.HashMap modelsToKeyframeAnims;
	protected java.util.ArrayList models;
	protected Material[] materials = null;

	protected int firstFrame;
	protected int lastFrame;
	protected int frameSpeed;
	protected int ticksPerFrame;
	protected double timeScaleFactor; // = (1.0/ticksPerFrame) * (1.0/frameSpeed)

	protected String currentObject = Messages.getString("ASEImporter.0"); //$NON-NLS-1$
	protected String currentlyLoading = Messages.getString("ASEImporter.1"); //$NON-NLS-1$
	protected int currentProgress = 0;
	//protected ASEOptionsDialog optionsDialog = new ASEOptionsDialog();
	protected ProgressDialog progressDialog;

	protected static edu.cmu.cs.stage3.alice.authoringtool.util.Configuration importersConfig = edu.cmu.cs.stage3.alice.authoringtool.util.Configuration.getLocalConfiguration( ASEImporter.class.getPackage() );

	// config init
	static {
		if( importersConfig.getValue( "aseImporter.useSpecular" ) == null ) { //$NON-NLS-1$
			importersConfig.setValue( "aseImporter.useSpecular", "false" ); //$NON-NLS-1$ //$NON-NLS-2$
		}
		if( importersConfig.getValue( "aseImporter.colorToWhiteWhenTextured" ) == null ) { //$NON-NLS-1$
			importersConfig.setValue( "aseImporter.colorToWhiteWhenTextured", "true" ); //$NON-NLS-1$ //$NON-NLS-2$
		}
		if( importersConfig.getValue( "aseImporter.groupMultipleRootObjects" ) == null ) { //$NON-NLS-1$
			importersConfig.setValue( "aseImporter.groupMultipleRootObjects", "true" ); //$NON-NLS-1$ //$NON-NLS-2$
		}
		if( importersConfig.getValue( "aseImporter.createNormalsIfNoneExist" ) == null ) { //$NON-NLS-1$
			importersConfig.setValue( "aseImporter.createNormalsIfNoneExist", "true" ); //$NON-NLS-1$ //$NON-NLS-2$
		}
		if( importersConfig.getValue( "aseImporter.createUVsIfNoneExist" ) == null ) { //$NON-NLS-1$
			importersConfig.setValue( "aseImporter.createUVsIfNoneExist", "true" ); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	public java.util.Map getExtensionMap() {
		java.util.HashMap map = new java.util.HashMap();
		map.put( "ASE", Messages.getString("ASEImporter.18") ); //$NON-NLS-1$ //$NON-NLS-2$
		return map;
	}

	protected edu.cmu.cs.stage3.alice.core.Element load( java.io.InputStream is, String ext ) throws java.io.IOException {
		edu.cmu.cs.stage3.alice.authoringtool.util.BackslashConverterFilterInputStream bcfis = new edu.cmu.cs.stage3.alice.authoringtool.util.BackslashConverterFilterInputStream( is );
		java.io.BufferedReader br = new java.io.BufferedReader( new java.io.InputStreamReader( bcfis ) );
		tokenizer = new java.io.StreamTokenizer( br );

		tokenizer.eolIsSignificant( false );
		tokenizer.lowerCaseMode( false );
		tokenizer.parseNumbers();
		tokenizer.wordChars( '*', '*' );
		tokenizer.wordChars( '_', '_' );
		tokenizer.wordChars( ':', ':' );

		modelsToParentStrings = new java.util.HashMap();
		namesToModels = new java.util.HashMap();
		namesToMaterials = new java.util.HashMap();
		modelsToMaterialIndices = new java.util.HashMap();
		modelsToKeyframeAnims = new java.util.HashMap();
		models = new java.util.ArrayList();

		//optionsDialog.show();

		progressDialog = new ProgressDialog();
		progressDialog.start();

		try {
			while( true ) {
				tokenizer.nextToken();

				if( (tokenizer.sval != null) && tokenizer.sval.equalsIgnoreCase( "*SCENE" ) ) { //$NON-NLS-1$
					currentObject = Messages.getString("ASEImporter.20"); //$NON-NLS-1$
					parseSceneInfo();
					currentObject = Messages.getString("ASEImporter.21"); //$NON-NLS-1$
				} else if( (tokenizer.sval != null) && tokenizer.sval.equalsIgnoreCase( "*GEOMOBJECT" ) ) { //$NON-NLS-1$
					currentObject = Messages.getString("ASEImporter.23"); //$NON-NLS-1$
					parseGeomObject();
					currentObject = Messages.getString("ASEImporter.24"); //$NON-NLS-1$
				} else if( (tokenizer.sval != null) && tokenizer.sval.equalsIgnoreCase( "*HELPEROBJECT" ) ) { //$NON-NLS-1$
					currentObject = Messages.getString("ASEImporter.26"); //$NON-NLS-1$
					parseHelperObject();
					currentObject = Messages.getString("ASEImporter.27"); //$NON-NLS-1$
				} else if( (tokenizer.sval != null) && tokenizer.sval.equalsIgnoreCase( "*MATERIAL_LIST" ) ) { //$NON-NLS-1$
					currentObject = Messages.getString("ASEImporter.29"); //$NON-NLS-1$
					parseMaterialList();
					currentObject = Messages.getString("ASEImporter.30"); //$NON-NLS-1$
				} else if( tokenizer.ttype == java.io.StreamTokenizer.TT_EOF ) {
					break;
				}
			}
		} catch( java.io.IOException e ) {
			edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog( Messages.getString("ASEImporter.31") + tokenizer.lineno(), e ); //$NON-NLS-1$
			return null;
		} catch( InvalidFormatError e ) {
			edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog( Messages.getString("ASEImporter.32") + e.getMessage() + Messages.getString("ASEImporter.33") + tokenizer.lineno(), e, false ); //$NON-NLS-1$ //$NON-NLS-2$
			return null;
		}

		edu.cmu.cs.stage3.alice.core.Element element = null;
		try {
			java.util.ArrayList rootModels = new java.util.ArrayList();
			for( java.util.Iterator iter = models.iterator(); iter.hasNext(); ) {
				edu.cmu.cs.stage3.alice.core.Transformable model = (edu.cmu.cs.stage3.alice.core.Transformable)iter.next();
				String parentString = (String)modelsToParentStrings.get( model );
				if( parentString == null ) {
					rootModels.add( model );
					model.isFirstClass.set( Boolean.TRUE );
				} else {
					edu.cmu.cs.stage3.alice.core.Transformable parent = (edu.cmu.cs.stage3.alice.core.Transformable)namesToModels.get( parentString );
					if( parent == null ) {
						edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog( model.name.getValue() + Messages.getString("ASEImporter.34") + parentString + Messages.getString("ASEImporter.35"), null ); //$NON-NLS-1$ //$NON-NLS-2$
						rootModels.add( model );
						model.isFirstClass.set( Boolean.TRUE );
					} else {
						parent.addChild( model );
						parent.parts.add( model );
						model.vehicle.set( parent );
						model.isFirstClass.set( Boolean.FALSE );
					}
				}
			}

			if( rootModels.size() == 1 ) {
				element = (edu.cmu.cs.stage3.alice.core.Transformable)rootModels.get( 0 );
			} else if( rootModels.size() > 1 ) {
				if( importersConfig.getValue( "aseImporter.groupMultipleRootObjects" ).equalsIgnoreCase( "true" ) ) { //$NON-NLS-1$ //$NON-NLS-2$
					element = new edu.cmu.cs.stage3.alice.core.Model();
					element.name.set( null );
					element.isFirstClass.set( Boolean.TRUE );
					for( java.util.Iterator iter = rootModels.iterator(); iter.hasNext(); ) {
						edu.cmu.cs.stage3.alice.core.Transformable model = (edu.cmu.cs.stage3.alice.core.Transformable)iter.next();
						element.addChild( model );
						((edu.cmu.cs.stage3.alice.core.Model)element).parts.add( model );
						model.vehicle.set( (edu.cmu.cs.stage3.alice.core.Model)element );
						model.isFirstClass.set( Boolean.FALSE );
					}
				} else {
					element = new edu.cmu.cs.stage3.alice.core.Module();
					element.name.set( null );
					for( java.util.Iterator iter = rootModels.iterator(); iter.hasNext(); ) {
						edu.cmu.cs.stage3.alice.core.Transformable model = (edu.cmu.cs.stage3.alice.core.Transformable)iter.next();
						element.addChild( model );
						model.isFirstClass.set( Boolean.TRUE );
					}
				}
			} else if( rootModels.size() < 1 ) {
				return null;
			}

			String currentName = (String)element.name.getValue();
			if( currentName == null ) {
				element.name.set( plainName );
			} else if( ! currentName.equalsIgnoreCase( plainName ) ) {
				element.name.set( plainName + "_" + currentName ); //$NON-NLS-1$
			}

			edu.cmu.cs.stage3.alice.core.Transformable dummyScene = new edu.cmu.cs.stage3.alice.core.Transformable();
			if( element instanceof edu.cmu.cs.stage3.alice.core.Model ) {
				edu.cmu.cs.stage3.alice.core.Transformable trans = (edu.cmu.cs.stage3.alice.core.Transformable)element;
				trans.vehicle.set( dummyScene );
				currentObject = (String)trans.name.getValue();
				currentlyLoading = Messages.getString("ASEImporter.39"); //$NON-NLS-1$
				fixTransformations( trans, dummyScene );
				currentObject = (String)trans.name.getValue();
				currentlyLoading = Messages.getString("ASEImporter.40"); //$NON-NLS-1$
				fixVertices( trans );
				currentlyLoading = Messages.getString("ASEImporter.41"); //$NON-NLS-1$
				trans.vehicle.set( null );
				trans.localTransformation.set( edu.cmu.cs.stage3.math.MathUtilities.createIdentityMatrix4d() );
			} else {
				edu.cmu.cs.stage3.alice.core.Element[] children = element.getChildren();
				for( int i = 0; i < children.length; i++ ) {
					edu.cmu.cs.stage3.alice.core.Transformable trans = (edu.cmu.cs.stage3.alice.core.Transformable)children[i];
					trans.vehicle.set( dummyScene );
					currentObject = (String)trans.name.getValue();
					currentlyLoading = Messages.getString("ASEImporter.42"); //$NON-NLS-1$
					fixTransformations( trans, dummyScene );
					currentObject = (String)trans.name.getValue();
					currentlyLoading = Messages.getString("ASEImporter.43"); //$NON-NLS-1$
					fixVertices( trans );
					currentlyLoading = Messages.getString("ASEImporter.44"); //$NON-NLS-1$
					trans.vehicle.set( null );
				}
			}

			for( java.util.Iterator iter = models.iterator(); iter.hasNext(); ) {
				edu.cmu.cs.stage3.alice.core.Transformable trans = (edu.cmu.cs.stage3.alice.core.Transformable)iter.next();
				if( trans instanceof edu.cmu.cs.stage3.alice.core.Model ) {
					edu.cmu.cs.stage3.alice.core.Model model = (edu.cmu.cs.stage3.alice.core.Model)trans;
					//TODO make better
					int materialIndex;
					try {
						materialIndex = ((Integer)modelsToMaterialIndices.get( model )).intValue();
					} catch( NullPointerException e ) {
						materialIndex = -1;
					}
					if( (materialIndex >= 0) && (materialIndex < materials.length) ) {
						Material material = materials[materialIndex];
						if( material != null ) {
//							edu.cmu.cs.stage3.alice.scenegraph.Visual sgVisual = model.getSceneGraphVisual();
							if( (material.diffuseTexture != null) && importersConfig.getValue( "aseImporter.colorToWhiteWhenTextured" ).equalsIgnoreCase( "true" ) ) { //$NON-NLS-1$ //$NON-NLS-2$
								model.ambientColor.set( edu.cmu.cs.stage3.alice.scenegraph.Color.WHITE );
								model.color.set( edu.cmu.cs.stage3.alice.scenegraph.Color.WHITE );
//								sgVisual.getFrontFacingAppearance().setAmbientColor( edu.cmu.cs.stage3.alice.scenegraph.Color.WHITE );
//								sgVisual.getFrontFacingAppearance().setDiffuseColor( edu.cmu.cs.stage3.alice.scenegraph.Color.WHITE );
							} else {
								model.ambientColor.set( material.ambient );
								model.color.set( material.diffuse );
//								sgVisual.getFrontFacingAppearance().setAmbientColor( material.ambient );
//								sgVisual.getFrontFacingAppearance().setDiffuseColor( material.diffuse );
							}
							if( importersConfig.getValue( "aseImporter.useSpecular" ).equalsIgnoreCase( "true" ) ) { //$NON-NLS-1$ //$NON-NLS-2$
								model.specularHighlightColor.set( material.specular );
								model.specularHighlightExponent.set( new Double( material.shine + 1 ) ); // this is a kludge to get roughly the right shininess.  I don't have the MAX lighting equations...
							}
							model.opacity.set( new Double( 1.0 - material.transparency ) );
							model.diffuseColorMap.set( material.diffuseTexture );
							model.opacityMap.set( material.opacityTexture );
							model.specularHighlightColorMap.set( material.shineTexture );
							model.bumpMap.set( material.bumpTexture );
						}
					} else if( materialIndex != -1 ) {
						edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog( model.name.getValue() + Messages.getString("ASEImporter.49"), null ); //$NON-NLS-1$
					}
				}
			}

			for( int i = 0; i < materials.length; i++ ) {
				if( materials[i] != null ) {
					edu.cmu.cs.stage3.alice.core.Transformable materialOwner = null;
					if( element instanceof edu.cmu.cs.stage3.alice.core.Transformable ) {
						materialOwner = (edu.cmu.cs.stage3.alice.core.Transformable)element;
					} else {
						//TODO: if texture is applied to multiple root models, it should be attached to the world,
						//      or something better.  In the implementation below, it just uses the root model of the first
						//      model that uses the material.
						for( java.util.Iterator iter = models.iterator(); iter.hasNext(); ) {
							edu.cmu.cs.stage3.alice.core.Transformable trans = (edu.cmu.cs.stage3.alice.core.Transformable)iter.next();
							try {
								int materialIndex = ((Integer)modelsToMaterialIndices.get( trans )).intValue();
								if( materialIndex == i ) {
									materialOwner = getRootModel( trans );
									break;
								}
							} catch( NullPointerException e ) {
								continue;
							}
						}
					}

					if( materialOwner != null ) {
						if( materials[i].diffuseTexture != null ) {
							materialOwner.addChild( materials[i].diffuseTexture );
							materialOwner.textureMaps.add( materials[i].diffuseTexture );
						}
						if( materials[i].opacityTexture != null ) {
							materialOwner.addChild( materials[i].opacityTexture );
							materialOwner.textureMaps.add( materials[i].diffuseTexture );
						}
						if( materials[i].shineTexture != null ) {
							materialOwner.addChild( materials[i].shineTexture );
							materialOwner.textureMaps.add( materials[i].shineTexture );
						}
						if( materials[i].bumpTexture != null ) {
							materialOwner.addChild( materials[i].bumpTexture );
							materialOwner.textureMaps.add( materials[i].bumpTexture );
						}
					} else {
						edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog( Messages.getString("ASEImporter.50"), null ); //$NON-NLS-1$
					}
				}
			}

			// Keyframe Animation

			for( java.util.Iterator iter = rootModels.iterator(); iter.hasNext(); ) {
				edu.cmu.cs.stage3.alice.core.Transformable root = (edu.cmu.cs.stage3.alice.core.Transformable)iter.next();
				//System.out.println( "root: " + root );
				edu.cmu.cs.stage3.alice.core.response.DoTogether rootAnim = new edu.cmu.cs.stage3.alice.core.response.DoTogether();
				rootAnim.name.set( "keyframeAnimation" ); //$NON-NLS-1$
				for( java.util.Iterator jter = models.iterator(); jter.hasNext(); ) {
					edu.cmu.cs.stage3.alice.core.Transformable trans = (edu.cmu.cs.stage3.alice.core.Transformable)jter.next();
					//System.out.println( "trans: " + trans );
					if( trans.isDescendantOf( root ) || trans.equals( root ) ) {
						java.util.ArrayList anims = (java.util.ArrayList)modelsToKeyframeAnims.get( trans );
						if( anims != null ) {
							//System.out.println( trans + " has anims" );
							String prefix = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getReprForValue( trans );
							prefix = prefix.replace( '.', '_' );
							for( java.util.Iterator kter = anims.iterator(); kter.hasNext(); ) {
								edu.cmu.cs.stage3.pratt.maxkeyframing.KeyframeResponse anim = (edu.cmu.cs.stage3.pratt.maxkeyframing.KeyframeResponse)kter.next();
								anim.duration.set( null );
								String baseName = anim.name.getStringValue();
								anim.name.set( prefix + "_" + baseName ); //$NON-NLS-1$
								anim.subject.set( trans );
								rootAnim.addChild( anim );
								rootAnim.componentResponses.add( anim );
								//System.out.println( "adding " + anim );
							}
						}
					}
				}
				if( ! rootAnim.componentResponses.isEmpty() ) {
					root.addChild( rootAnim );
					root.responses.add( rootAnim );
				}
			}
		} catch( Throwable t ) {
			edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog( Messages.getString("ASEImporter.53"), t ); //$NON-NLS-1$
		}

		progressDialog.stop();
		progressDialog.setVisible( false );
		progressDialog.dispose();

		tokenizer = null;
		modelsToParentStrings = null;
		namesToModels = null;
		namesToMaterials = null;
		modelsToMaterialIndices = null;
		modelsToKeyframeAnims = null;
		models = null;
		materials = null;

		return element;
	}

	protected edu.cmu.cs.stage3.alice.core.Transformable getRootModel( edu.cmu.cs.stage3.alice.core.Transformable trans ) {
		edu.cmu.cs.stage3.alice.core.Element parent = trans.getParent();
		if( ! (parent instanceof edu.cmu.cs.stage3.alice.core.Transformable) ) {
			return trans;
		} else {
			return getRootModel( (edu.cmu.cs.stage3.alice.core.Transformable)parent );
		}
	}

	// all coordinates in ASEs are in world space, so everything has to be fixed once we have the hierarchy...
	protected void fixTransformations( edu.cmu.cs.stage3.alice.core.Transformable root, edu.cmu.cs.stage3.alice.core.Transformable scene ) {
		root.setTransformationRightNow( root.getLocalTransformation(), scene );
		/*
		root.setLocalTransformation(
			edu.cmu.cs.stage3.math.Matrix44.multiply(
				((edu.cmu.cs.stage3.alice.core.ReferenceFrame)root.vehicle.getValue()).getSceneGraphReferenceFrame().getInverseAbsoluteTransformation(),
				(edu.cmu.cs.stage3.math.Matrix44)root.localTransformation.getValue()
			)
		);
		*/

		edu.cmu.cs.stage3.alice.core.Element[] children = root.getChildren();
		for( int i = 0; i < children.length; i++ ) {
			if( children[i] instanceof edu.cmu.cs.stage3.alice.core.Transformable ) {
				fixTransformations( (edu.cmu.cs.stage3.alice.core.Transformable)children[i], scene );
			}
		}
	}

	protected void fixVertices( edu.cmu.cs.stage3.alice.core.Transformable root ) {
		currentObject = (String)root.name.getValue();
		if( root instanceof edu.cmu.cs.stage3.alice.core.Model ) {
			if( ((edu.cmu.cs.stage3.alice.core.Model)root).geometry.getValue() instanceof edu.cmu.cs.stage3.alice.core.geometry.IndexedTriangleArray ) {
				edu.cmu.cs.stage3.alice.core.geometry.IndexedTriangleArray geom = (edu.cmu.cs.stage3.alice.core.geometry.IndexedTriangleArray)((edu.cmu.cs.stage3.alice.core.Model)root).geometry.getValue();
				edu.cmu.cs.stage3.alice.scenegraph.Vertex3d[] vertices = (edu.cmu.cs.stage3.alice.scenegraph.Vertex3d[])geom.vertices.getValue();
				if( vertices != null ) {
					progressDialog.setMax( vertices.length - 1 );
					currentProgress = 0;
					for( int i = 0; i < vertices.length; i++ ) {
						currentProgress = i;
						edu.cmu.cs.stage3.math.Vector4 v = new edu.cmu.cs.stage3.math.Vector4( vertices[i].position.x, vertices[i].position.y, vertices[i].position.z, 1.0 );
						edu.cmu.cs.stage3.math.Vector4 vprime = edu.cmu.cs.stage3.math.Vector4.multiply( v, root.getSceneGraphReferenceFrame().getInverseAbsoluteTransformation() );
						vertices[i].position.set( vprime.x, vprime.y, vprime.z );
					}
					geom.vertices.set( vertices );
				}
			}
		}

		edu.cmu.cs.stage3.alice.core.Element[] children = root.getChildren();
		for( int i = 0; i < children.length; i++ ) {
			if( children[i] instanceof edu.cmu.cs.stage3.alice.core.Transformable ) {
				fixVertices( (edu.cmu.cs.stage3.alice.core.Transformable)children[i] );
			}
		}
	}

	protected String parseString() throws InvalidFormatError, java.io.IOException {
		tokenizer.nextToken();

		if( tokenizer.ttype == '"' ) {
			return tokenizer.sval;
		} else {
			throw new InvalidFormatError( Messages.getString("ASEImporter.54") ); //$NON-NLS-1$
		}
	}

	protected int parseInt() throws InvalidFormatError, java.io.IOException {
		tokenizer.nextToken();

		if( tokenizer.ttype == java.io.StreamTokenizer.TT_NUMBER ) {
			return (int)tokenizer.nval;
		} else {
			throw new InvalidFormatError( Messages.getString("ASEImporter.55") ); //$NON-NLS-1$
		}
	}

	protected double parseDouble() throws InvalidFormatError, java.io.IOException {
		tokenizer.nextToken();

		if( tokenizer.ttype == java.io.StreamTokenizer.TT_NUMBER ) {
			return tokenizer.nval;
		} else {
			throw new InvalidFormatError( Messages.getString("ASEImporter.56") ); //$NON-NLS-1$
		}
	}

	protected float parseFloat() throws InvalidFormatError, java.io.IOException {
		tokenizer.nextToken();

		if( tokenizer.ttype == java.io.StreamTokenizer.TT_NUMBER ) {
			return (float)tokenizer.nval;
		} else {
			throw new InvalidFormatError( Messages.getString("ASEImporter.57") ); //$NON-NLS-1$
		}
	}

	protected void parseUnknownBlock() throws InvalidFormatError, java.io.IOException {
		while( true ) {
			tokenizer.nextToken();

			if( tokenizer.ttype == '{' ) {
				parseUnknownBlock();
			} else if( tokenizer.ttype == '}' ) {
				return;
			} else if( tokenizer.ttype == java.io.StreamTokenizer.TT_EOF ) {
				throw new InvalidFormatError( Messages.getString("ASEImporter.58") ); //$NON-NLS-1$
			}
		}
	}

	protected void parseSceneInfo() throws java.io.IOException {
		firstFrame = 0;
		lastFrame = 0;
		frameSpeed = 0;
		ticksPerFrame = 0;
		timeScaleFactor = 1.0;

		tokenizer.nextToken();
		if( tokenizer.ttype != '{' ) {
			throw new InvalidFormatError( Messages.getString("ASEImporter.59") ); //$NON-NLS-1$
		}

		while( true ) {
			tokenizer.nextToken();

			if( (tokenizer.sval != null) && tokenizer.sval.equalsIgnoreCase( "*SCENE_FIRSTFRAME" ) ) { //$NON-NLS-1$
				firstFrame = parseInt();
			} else if( (tokenizer.sval != null) && tokenizer.sval.equalsIgnoreCase( "*SCENE_LASTFRAME" ) ) { //$NON-NLS-1$
				lastFrame = parseInt();
			} else if( (tokenizer.sval != null) && tokenizer.sval.equalsIgnoreCase( "*SCENE_FRAMESPEED" ) ) { //$NON-NLS-1$
				frameSpeed = parseInt();
			} else if( (tokenizer.sval != null) && tokenizer.sval.equalsIgnoreCase( "*SCENE_TICKSPERFRAME" ) ) { //$NON-NLS-1$
				ticksPerFrame = parseInt();
			} else if( tokenizer.ttype == '}' ) {
				try {
					timeScaleFactor = (1.0/(double)ticksPerFrame) * (1.0/(double)frameSpeed);
				} catch( Exception e ) {
					timeScaleFactor = 1.0;
				}
				return;
			} else if( tokenizer.ttype == java.io.StreamTokenizer.TT_EOF ) {
				throw new InvalidFormatError( Messages.getString("ASEImporter.64") ); //$NON-NLS-1$
			}
		}
	}

	protected void parseMaterialList() throws InvalidFormatError, java.io.IOException {
		int count = 0;

		tokenizer.nextToken();
		if( tokenizer.ttype != '{' ) {
			throw new InvalidFormatError( Messages.getString("ASEImporter.65") ); //$NON-NLS-1$
		}

		currentProgress = 0;
		while( true ) {
			tokenizer.nextToken();

			if( (tokenizer.sval != null) && tokenizer.sval.equalsIgnoreCase( "*MATERIAL_COUNT" ) ) { //$NON-NLS-1$
				count = parseInt();
				materials = new Material[count];
			} else if( (tokenizer.sval != null) && tokenizer.sval.equalsIgnoreCase( "*MATERIAL" ) ) { //$NON-NLS-1$
				if( count < 1 ) {
					throw new InvalidFormatError( Messages.getString("ASEImporter.68") ); //$NON-NLS-1$
				}

				parseMaterial();
				currentObject = Messages.getString("ASEImporter.69"); //$NON-NLS-1$
			} else if( tokenizer.ttype == '}' ) {
				return;
			} else if( tokenizer.ttype == java.io.StreamTokenizer.TT_EOF ) {
				throw new InvalidFormatError( Messages.getString("ASEImporter.70") ); //$NON-NLS-1$
			}
		}
	}

	protected void parseMaterial() throws InvalidFormatError, java.io.IOException {
		Material material = new Material();

		int index = parseInt();

		tokenizer.nextToken();
		if( tokenizer.ttype != '{' ) {
			throw new InvalidFormatError( Messages.getString("ASEImporter.71") ); //$NON-NLS-1$
		}

		while( true ) {
			tokenizer.nextToken();

			if( (tokenizer.sval != null) && tokenizer.sval.equalsIgnoreCase( "*MATERIAL_NAME" ) ) { //$NON-NLS-1$
				material.name = tokenizer.sval;
				namesToMaterials.put( material.name, material );
				currentObject = material.name;
			} else if( (tokenizer.sval != null) && tokenizer.sval.equalsIgnoreCase( "*MATERIAL_AMBIENT" ) ) { //$NON-NLS-1$
				material.ambient = new edu.cmu.cs.stage3.alice.scenegraph.Color( parseDouble(), parseDouble(), parseDouble() );
			} else if( (tokenizer.sval != null) && tokenizer.sval.equalsIgnoreCase( "*MATERIAL_DIFFUSE" ) ) { //$NON-NLS-1$
				material.diffuse = new edu.cmu.cs.stage3.alice.scenegraph.Color( parseDouble(), parseDouble(), parseDouble() );
			} else if( (tokenizer.sval != null) && tokenizer.sval.equalsIgnoreCase( "*MATERIAL_SPECULAR" ) ) { //$NON-NLS-1$
				material.specular = new edu.cmu.cs.stage3.alice.scenegraph.Color( parseDouble(), parseDouble(), parseDouble() );
			} else if( (tokenizer.sval != null) && tokenizer.sval.equalsIgnoreCase( "*MATERIAL_SHINE" ) ) { //$NON-NLS-1$
				material.shine = parseDouble();
			} else if( (tokenizer.sval != null) && tokenizer.sval.equalsIgnoreCase( "*MATERIAL_SHINESTRENGTH" ) ) { //$NON-NLS-1$
				material.shinestrength = parseDouble();
			} else if( (tokenizer.sval != null) && tokenizer.sval.equalsIgnoreCase( "*MATERIAL_TRANSPARENCY" ) ) { //$NON-NLS-1$
				material.transparency = parseDouble();
			} else if( (tokenizer.sval != null) && tokenizer.sval.equalsIgnoreCase( "*MAP_AMBIENT" ) ) { //$NON-NLS-1$
				material.ambientTexture = parseMap();
				currentObject = material.name;
			} else if( (tokenizer.sval != null) && tokenizer.sval.equalsIgnoreCase( "*MAP_DIFFUSE" ) ) { //$NON-NLS-1$
				material.diffuseTexture = parseMap();
				currentObject = material.name;
			} else if( (tokenizer.sval != null) && tokenizer.sval.equalsIgnoreCase( "*MAP_SHINE" ) ) { //$NON-NLS-1$
				material.shineTexture = parseMap();
				currentObject = material.name;
			} else if( (tokenizer.sval != null) && tokenizer.sval.equalsIgnoreCase( "*MAP_SHINESTRENGTH" ) ) { //$NON-NLS-1$
				material.shineStrengthTexture = parseMap();
				currentObject = material.name;
			} else if( (tokenizer.sval != null) && tokenizer.sval.equalsIgnoreCase( "*MAP_SELFILLUM" ) ) { //$NON-NLS-1$
				material.selfIllumTexture = parseMap();
				currentObject = material.name;
			} else if( (tokenizer.sval != null) && tokenizer.sval.equalsIgnoreCase( "*MAP_OPACITY" ) ) { //$NON-NLS-1$
				material.opacityTexture = parseMap();
				currentObject = material.name;
			} else if( (tokenizer.sval != null) && tokenizer.sval.equalsIgnoreCase( "*MAP_BUMP" ) ) { //$NON-NLS-1$
				material.bumpTexture = parseMap();
				currentObject = material.name;
			} else if( tokenizer.ttype == '{' ) {
				parseUnknownBlock();
			} else if( tokenizer.ttype == '}' ) {
				materials[index] = material;
				return;
			} else if( tokenizer.ttype == java.io.StreamTokenizer.TT_EOF ) {
				throw new InvalidFormatError( Messages.getString("ASEImporter.86") ); //$NON-NLS-1$
			}
		}
	}

	protected edu.cmu.cs.stage3.alice.core.TextureMap parseMap() throws InvalidFormatError, java.io.IOException {
		edu.cmu.cs.stage3.alice.core.TextureMap texture = null;

		tokenizer.nextToken();
		if( tokenizer.ttype != '{' ) {
			throw new InvalidFormatError( Messages.getString("ASEImporter.87") ); //$NON-NLS-1$
		}

		while( true ) {
			tokenizer.nextToken();

			if( (tokenizer.sval != null) && tokenizer.sval.equalsIgnoreCase( "*BITMAP" ) ) { //$NON-NLS-1$
				String filename = parseString();
				currentObject = filename;

				java.io.File imageFile = new java.io.File( filename );
				String justName = imageFile.getName();
				String extension = justName.substring( justName.lastIndexOf( '.' ) + 1 );
				java.io.BufferedInputStream bis = null;

				if( imageFile.exists() ) {
					if( imageFile.canRead() ) {
						bis = new java.io.BufferedInputStream( new java.io.FileInputStream( imageFile ) );
					} else {
						edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog( Messages.getString("ASEImporter.89") + filename + Messages.getString("ASEImporter.90") + tokenizer.lineno(), null, false ); //$NON-NLS-1$ //$NON-NLS-2$
						continue;
					}
				} else {
					Object location = getLocation();
					if( location instanceof java.io.File ) {
						imageFile = new java.io.File( (java.io.File)location, filename );
						if( ! imageFile.exists() ) {
							imageFile = new java.io.File( (java.io.File)location, justName );
						}
						if( imageFile.exists() ) {
							if( imageFile.canRead() ) {
								bis = new java.io.BufferedInputStream( new java.io.FileInputStream( imageFile ) );
							} else {
								edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog( Messages.getString("ASEImporter.91") + filename + Messages.getString("ASEImporter.92") + tokenizer.lineno(), null, false ); //$NON-NLS-1$ //$NON-NLS-2$
								continue;
							}
						} else {
							edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog( Messages.getString("ASEImporter.93") + filename + Messages.getString("ASEImporter.94") + tokenizer.lineno(), null, false ); //$NON-NLS-1$ //$NON-NLS-2$
							continue;
						}
					} else if( location instanceof java.net.URL ) {
						// escape necessary characters
						StringBuffer name = new StringBuffer();
						char[] chars = new char[justName.length()];
						justName.getChars( 0, justName.length(), chars, 0 );
						for( int i = 0; i < chars.length; i++ ) {
							char c = chars[i];
							if( c == ' ' ) {
								name.append( "%20" ); //$NON-NLS-1$
							} else if( c == '#' ) {
								name.append( "%23" ); //$NON-NLS-1$
							} else if( c == ';' ) {
								name.append( "%3B" ); //$NON-NLS-1$
							} else if( c == '@' ) {
								name.append( "%40" ); //$NON-NLS-1$
							} else if( c == '&' ) {
								name.append( "%26" ); //$NON-NLS-1$
							} else if( c == '=' ) {
								name.append( "%3D" ); //$NON-NLS-1$
							} else if( c == '+' ) {
								name.append( "%2B" ); //$NON-NLS-1$
							} else if( c == '$' ) {
								name.append( "%24" ); //$NON-NLS-1$
							} else if( c == ',' ) {
								name.append( "%2C" ); //$NON-NLS-1$
							} else if( c == '%' ) {
								name.append( "%25" ); //$NON-NLS-1$
							} else if( c == '"' ) {
								name.append( "%22" ); //$NON-NLS-1$
							} else if( c == '{' ) {
								name.append( "%7B" ); //$NON-NLS-1$
							} else if( c == '}' ) {
								name.append( "%7D" ); //$NON-NLS-1$
							} else if( c == '^' ) {
								name.append( "%5E" ); //$NON-NLS-1$
							} else if( c == '[' ) {
								name.append( "%5B" ); //$NON-NLS-1$
							} else if( c == ']' ) {
								name.append( "%5D" ); //$NON-NLS-1$
							} else if( c == '`' ) {
								name.append( "%60" ); //$NON-NLS-1$
							} else {
								name.append( c );
							}
						}
						java.net.URL url = (java.net.URL)location;
						url = new java.net.URL( url.toExternalForm() + name.toString() );
						bis = new java.io.BufferedInputStream( url.openStream() );
					} else {
						edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog( Messages.getString("ASEImporter.112") + location, null, false ); //$NON-NLS-1$
					}
				}
				if( bis == null ) {
					edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog( Messages.getString("ASEImporter.113") + filename, null ); //$NON-NLS-1$
					continue;
				}

				String codec = edu.cmu.cs.stage3.image.ImageIO.mapExtensionToCodecName( extension );
				if( codec == null ) {
					edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog( Messages.getString("ASEImporter.114") + filename, null ); //$NON-NLS-1$
					continue;
				}

				java.awt.Image image = edu.cmu.cs.stage3.image.ImageIO.load( codec, bis );
				if( image == null ) {
					edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog( Messages.getString("ASEImporter.115") + filename, null ); //$NON-NLS-1$
					continue;
				}

				String textureName = justName.substring( 0, justName.indexOf( '.' ) );
				texture = new edu.cmu.cs.stage3.alice.core.TextureMap();

				if( image instanceof java.awt.image.BufferedImage ) {
					java.awt.image.BufferedImage bi = (java.awt.image.BufferedImage)image;
					if( bi.getColorModel().hasAlpha() ) {
						texture.format.set( new Integer( edu.cmu.cs.stage3.alice.scenegraph.TextureMap.RGBA ) );
					}
				}

				texture.name.set( textureName );
				texture.image.set( image );
			} else if( tokenizer.ttype == '}' ) {
				return texture;
			} else if( tokenizer.ttype == java.io.StreamTokenizer.TT_EOF ) {
				throw new InvalidFormatError( Messages.getString("ASEImporter.116") ); //$NON-NLS-1$
			}
		}
	}

	protected void parseHelperObject() throws java.io.IOException {
		edu.cmu.cs.stage3.alice.core.Model helper = new edu.cmu.cs.stage3.alice.core.Model();
		helper.isFirstClass.set( Boolean.FALSE );
		models.add( helper );

		tokenizer.nextToken();
		if( tokenizer.ttype != '{' ) {
			throw new InvalidFormatError( Messages.getString("ASEImporter.117") ); //$NON-NLS-1$
		}

		while( true ) {
			tokenizer.nextToken();

			if( (tokenizer.sval != null) && tokenizer.sval.equalsIgnoreCase( "*NODE_NAME" ) ) { //$NON-NLS-1$
				helper.name.set( parseString() );
				namesToModels.put( helper.name.getValue(), helper );
				currentObject = (String)helper.name.getValue();
			} else if( (tokenizer.sval != null) && tokenizer.sval.equalsIgnoreCase( "*NODE_PARENT" ) ) { //$NON-NLS-1$
				modelsToParentStrings.put( helper, parseString() );
			} else if( (tokenizer.sval != null) && tokenizer.sval.equalsIgnoreCase( "*NODE_TM" ) ) { //$NON-NLS-1$
				currentlyLoading = Messages.getString("ASEImporter.121"); //$NON-NLS-1$
				currentProgress = 0;
				helper.localTransformation.set( parseTransformation() );
				currentProgress = 0;
				currentlyLoading = Messages.getString("ASEImporter.122"); //$NON-NLS-1$
			} else if( (tokenizer.sval != null) && tokenizer.sval.equalsIgnoreCase( "*TM_ANIMATION" ) ) { //$NON-NLS-1$
				java.util.ArrayList anims = parseAnimationNode();
				modelsToKeyframeAnims.put( helper, anims );
			} else if( tokenizer.ttype == '{' ) {
				parseUnknownBlock();
			} else if( tokenizer.ttype == '}' ) {
				return;
			} else if( tokenizer.ttype == java.io.StreamTokenizer.TT_EOF ) {
				throw new InvalidFormatError( Messages.getString("ASEImporter.124") ); //$NON-NLS-1$
			}
		}
	}

	protected void parseGeomObject() throws java.io.IOException {
		edu.cmu.cs.stage3.alice.core.Model model = new edu.cmu.cs.stage3.alice.core.Model();
		model.isFirstClass.set( Boolean.FALSE );
		models.add( model );

		tokenizer.nextToken();
		if( tokenizer.ttype != '{' ) {
			throw new InvalidFormatError( Messages.getString("ASEImporter.125") ); //$NON-NLS-1$
		}

		while( true ) {
			tokenizer.nextToken();

			if( (tokenizer.sval != null) && tokenizer.sval.equalsIgnoreCase( "*NODE_NAME" ) ) { //$NON-NLS-1$
				model.name.set( parseString() );
				namesToModels.put( model.name.getValue(), model );
				currentObject = (String)model.name.getValue();
			} else if( (tokenizer.sval != null) && tokenizer.sval.equalsIgnoreCase( "*NODE_PARENT" ) ) { //$NON-NLS-1$
				modelsToParentStrings.put( model, parseString() );
			} else if( (tokenizer.sval != null) && tokenizer.sval.equalsIgnoreCase( "*NODE_TM" ) ) { //$NON-NLS-1$
				currentlyLoading = Messages.getString("ASEImporter.129"); //$NON-NLS-1$
				currentProgress = 0;
				model.localTransformation.set( parseTransformation() );
				currentProgress = 0;
				currentlyLoading = Messages.getString("ASEImporter.130"); //$NON-NLS-1$
			} else if( (tokenizer.sval != null) && tokenizer.sval.equalsIgnoreCase( "*MESH" ) ) { //$NON-NLS-1$
				currentlyLoading = Messages.getString("ASEImporter.132"); //$NON-NLS-1$
				currentProgress = 0;
				model.geometry.set( parseMesh() );
				model.geometry.getElementValue().setParent( model );
				model.geometry.getElementValue().name.set( edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getNameForNewChild( "__ita__", model ) ); //$NON-NLS-1$
				currentProgress = 0;
				currentlyLoading = Messages.getString("ASEImporter.134"); //$NON-NLS-1$
			} else if( (tokenizer.sval != null) && tokenizer.sval.equalsIgnoreCase( "*PROP_CASTSHADOW" ) ) { //$NON-NLS-1$
				//TODO
			} else if( (tokenizer.sval != null) && tokenizer.sval.equalsIgnoreCase( "*PROP_RECVSHADOW" ) ) { //$NON-NLS-1$
				//TODO
			} else if( (tokenizer.sval != null) && tokenizer.sval.equalsIgnoreCase( "*MATERIAL_REF" ) ) { //$NON-NLS-1$
				modelsToMaterialIndices.put( model, new Integer( parseInt() ) );
			} else if( (tokenizer.sval != null) && tokenizer.sval.equalsIgnoreCase( "*TM_ANIMATION" ) ) { //$NON-NLS-1$
				java.util.ArrayList anims = parseAnimationNode();
				modelsToKeyframeAnims.put( model, anims );
			} else if( tokenizer.ttype == '{' ) {
				parseUnknownBlock();
			} else if( tokenizer.ttype == '}' ) {
				return;
			} else if( tokenizer.ttype == java.io.StreamTokenizer.TT_EOF ) {
				throw new InvalidFormatError( Messages.getString("ASEImporter.139") ); //$NON-NLS-1$
			}
		}
	}

	protected edu.cmu.cs.stage3.math.Matrix44 parseTransformation() throws InvalidFormatError, java.io.IOException {
		edu.cmu.cs.stage3.math.Matrix44 m = new edu.cmu.cs.stage3.math.Matrix44();
		edu.cmu.cs.stage3.math.Matrix33 rot = new edu.cmu.cs.stage3.math.Matrix33();

		tokenizer.nextToken();
		if( tokenizer.ttype != '{' ) {
			throw new InvalidFormatError( Messages.getString("ASEImporter.140") ); //$NON-NLS-1$
		}

		while( true ) {
			tokenizer.nextToken();

			if( (tokenizer.sval != null) && tokenizer.sval.equalsIgnoreCase( "*TM_ROW0" ) ) { //$NON-NLS-1$
				rot.m00 = parseDouble();
				rot.m01 = parseDouble();
				rot.m02 = parseDouble();
			} else if( (tokenizer.sval != null) && tokenizer.sval.equalsIgnoreCase( "*TM_ROW1" ) ) { //$NON-NLS-1$
				rot.m10 = parseDouble();
				rot.m11 = parseDouble();
				rot.m12 = parseDouble();
			} else if( (tokenizer.sval != null) && tokenizer.sval.equalsIgnoreCase( "*TM_ROW2" ) ) { //$NON-NLS-1$
				rot.m20 = parseDouble();
				rot.m21 = parseDouble();
				rot.m22 = parseDouble();
			} else if( (tokenizer.sval != null) && tokenizer.sval.equalsIgnoreCase( "*TM_ROW3" ) ) { //$NON-NLS-1$
				// X = -X, Y = Z, Z = -Y
				m.m30 = -parseDouble();
				m.m32 = -parseDouble();
				m.m31 = parseDouble();
			} else if( tokenizer.ttype == '{' ) {
				parseUnknownBlock();
			} else if( tokenizer.ttype == '}' ) {
				/**
				 * Transforming a rotation matrix from MAX space (right-handed, x-left, y-back, z-up)
				 * to Alice space (left-handed, x-right, y-up, z-forward):
				 *
				 * [ a  b  c ]       [-a -b -c ]       [ a -c  b ]
				 * [ d  e  f ]  -->  [ g  h  i ]  -->  [-g  i -h ]
				 * [ g  h  i ]       [-d -e -f ]       [ d -f  e ]
				 */


				m.m00 =  rot.m00;
				m.m01 = -rot.m02;
				m.m02 =  rot.m01;
				m.m10 = -rot.m20;
				m.m11 =  rot.m22;
				m.m12 = -rot.m21;
				m.m20 =  rot.m10;
				m.m21 = -rot.m12;
				m.m22 =  rot.m11;

				//m.makeAffine();  // should NOT be needed
				return m;
			} else if( tokenizer.ttype == java.io.StreamTokenizer.TT_EOF ) {
				throw new InvalidFormatError( Messages.getString("ASEImporter.145") ); //$NON-NLS-1$
			}
		}
	}

	protected edu.cmu.cs.stage3.alice.core.geometry.IndexedTriangleArray parseMesh() throws InvalidFormatError, java.io.IOException {
		edu.cmu.cs.stage3.alice.core.geometry.IndexedTriangleArray geometry = new edu.cmu.cs.stage3.alice.core.geometry.IndexedTriangleArray();
		edu.cmu.cs.stage3.alice.scenegraph.Vertex3d[] verts = null;
		int[] coordIndices = null;
		int[] uvIndices = null;
		double[] coordinates = null;
		double[] normals = null;
		float[] uvs = null;
		double[] colors = null;
		int numVerts = -1;
		int numUVs = -1;
		int numFaces = -1;

		tokenizer.nextToken();
		if( tokenizer.ttype != '{' ) {
			throw new InvalidFormatError( Messages.getString("ASEImporter.146") ); //$NON-NLS-1$
		}

		while( true ) {
			tokenizer.nextToken();

			if( (tokenizer.sval != null) && tokenizer.sval.equalsIgnoreCase( "*MESH_NUMVERTEX" ) ) { //$NON-NLS-1$
				numVerts = parseInt();
			} else if( (tokenizer.sval != null) && tokenizer.sval.equalsIgnoreCase( "*MESH_NUMFACES" ) ) { //$NON-NLS-1$
				numFaces = parseInt();
				coordIndices = new int[numFaces*3];
				uvIndices = new int[numFaces*3];    // assuming same number of texture faces for now
			} else if( (tokenizer.sval != null) && tokenizer.sval.equalsIgnoreCase( "*MESH_NUMTVERTEX" ) ) { //$NON-NLS-1$
				numUVs = parseInt();
			} else if( (tokenizer.sval != null) && tokenizer.sval.equalsIgnoreCase( "*MESH_VERTEX_LIST" ) ) { //$NON-NLS-1$
				if( numVerts < 0 ) {
					throw new InvalidFormatError( Messages.getString("ASEImporter.151") ); //$NON-NLS-1$
				}
				coordinates = new double[numVerts*3];

				currentlyLoading = Messages.getString("ASEImporter.152"); //$NON-NLS-1$
				progressDialog.setMax( numVerts );
				parseVertexList( coordinates );
				currentlyLoading = Messages.getString("ASEImporter.153"); //$NON-NLS-1$
			} else if( (tokenizer.sval != null) && tokenizer.sval.equalsIgnoreCase( "*MESH_TVERTLIST" ) ) { //$NON-NLS-1$
				if( numUVs < 0 ) {
					throw new InvalidFormatError( Messages.getString("ASEImporter.155") ); //$NON-NLS-1$
				}
				uvs = new float[numUVs*2];

				currentlyLoading = "uvs"; //$NON-NLS-1$
				progressDialog.setMax( numUVs );
				parseUVList( uvs );
				currentlyLoading = Messages.getString("ASEImporter.157"); //$NON-NLS-1$
			} else if( (tokenizer.sval != null) && tokenizer.sval.equalsIgnoreCase( "*MESH_NORMALS" ) ) { //$NON-NLS-1$
				if( numVerts < 0 ) {
					throw new InvalidFormatError( Messages.getString("ASEImporter.159") ); //$NON-NLS-1$
				}
				normals = new double[numFaces*3*3];

				currentlyLoading = Messages.getString("ASEImporter.160"); //$NON-NLS-1$
				progressDialog.setMax( numFaces );
				parseNormals( normals );
				currentlyLoading = Messages.getString("ASEImporter.161"); //$NON-NLS-1$
			//TODO vertex colors
			} else if( (tokenizer.sval != null) && tokenizer.sval.equalsIgnoreCase( "*MESH_FACE_LIST" ) ) { //$NON-NLS-1$
				if( coordIndices == null ) {
					throw new InvalidFormatError( Messages.getString("ASEImporter.163") ); //$NON-NLS-1$
				}

				currentlyLoading = Messages.getString("ASEImporter.164"); //$NON-NLS-1$
				progressDialog.setMax( coordIndices.length/3 );
				parseFaceList( coordIndices );
				currentlyLoading = Messages.getString("ASEImporter.165"); //$NON-NLS-1$
			} else if( (tokenizer.sval != null) && tokenizer.sval.equalsIgnoreCase( "*MESH_TFACELIST" ) ) { //$NON-NLS-1$
				if( uvIndices == null ) {
					throw new InvalidFormatError( Messages.getString("ASEImporter.167") ); //$NON-NLS-1$
				}

				currentlyLoading = Messages.getString("ASEImporter.168"); //$NON-NLS-1$
				progressDialog.setMax( uvIndices.length/3 );
				parseUVFaceList( uvIndices );
				currentlyLoading = Messages.getString("ASEImporter.169"); //$NON-NLS-1$
			} else if( tokenizer.ttype == '{' ) {
				parseUnknownBlock();
			} else if( tokenizer.ttype == '}' ) {
				if( (numVerts > 0) && (coordIndices != null) ) {
					int vertexFormat = 0;
					if( coordinates != null ) {
						vertexFormat |= edu.cmu.cs.stage3.alice.scenegraph.Vertex3d.FORMAT_POSITION;
					}
					if( (normals != null) || importersConfig.getValue( "aseImporter.createNormalsIfNoneExist" ).equalsIgnoreCase( "true" ) ) { //$NON-NLS-1$ //$NON-NLS-2$
						vertexFormat |= edu.cmu.cs.stage3.alice.scenegraph.Vertex3d.FORMAT_NORMAL;
					}
					if( (uvs != null) || importersConfig.getValue( "aseImporter.createUVsIfNoneExist" ).equalsIgnoreCase( "true" ) ) { //$NON-NLS-1$ //$NON-NLS-2$
						vertexFormat |= edu.cmu.cs.stage3.alice.scenegraph.Vertex3d.FORMAT_TEXTURE_COORDINATE_0;
					}
					if( colors != null ) {
						vertexFormat |= edu.cmu.cs.stage3.alice.scenegraph.Vertex3d.FORMAT_DIFFUSE_COLOR;
					}

					verts = new edu.cmu.cs.stage3.alice.scenegraph.Vertex3d[numFaces*3];
					for( int i = 0; i < numFaces*3; i++ ) {
						verts[i] = new edu.cmu.cs.stage3.alice.scenegraph.Vertex3d( vertexFormat );
					}

					int[] indices = new int[numFaces*3];
					for( int i = 0; i < numFaces*3; i++ ) {
						indices[i] = i;
					}

					for( int i = 0; i < numFaces; i++ ) {
						for( int j = 0; j < 3; j++ ) {
							if( coordinates != null ) {
								verts[i*3 + j].position.x = coordinates[coordIndices[i*3 + j]*3 + 0];
								verts[i*3 + j].position.y = coordinates[coordIndices[i*3 + j]*3 + 1];
								verts[i*3 + j].position.z = coordinates[coordIndices[i*3 + j]*3 + 2];
							}
							if( normals != null ) {
								verts[i*3 + j].normal.x = normals[i*9 + j*3 + 0];
								verts[i*3 + j].normal.y = normals[i*9 + j*3 + 1];
								verts[i*3 + j].normal.z = normals[i*9 + j*3 + 2];
							}
							if( uvs != null ) {
								verts[i*3 + j].textureCoordinate0.x = uvs[uvIndices[i*3 + j]*2 + 0];
								verts[i*3 + j].textureCoordinate0.y = uvs[uvIndices[i*3 + j]*2 + 1];
							}
							//TODO colors
						}
					}

					geometry.vertices.set( verts );
					geometry.indices.set( indices );

					if( (normals == null) && importersConfig.getValue( "aseImporter.createNormalsIfNoneExist" ).equalsIgnoreCase( "true" ) ) { //$NON-NLS-1$ //$NON-NLS-2$
						edu.cmu.cs.stage3.alice.gallery.ModelFixer.calculateNormals( geometry );
					}
				}

				return geometry;
			} else if( tokenizer.ttype == java.io.StreamTokenizer.TT_EOF ) {
				throw new InvalidFormatError( Messages.getString("ASEImporter.176") ); //$NON-NLS-1$
			}
		}
	}

	protected void parseVertexList( double[] coordinates ) throws InvalidFormatError, java.io.IOException {
		tokenizer.nextToken();
		if( tokenizer.ttype != '{' ) {
			throw new InvalidFormatError( Messages.getString("ASEImporter.177") ); //$NON-NLS-1$
		}

		currentProgress = 0;
		while( true ) {
			tokenizer.nextToken();

			if( (tokenizer.sval != null) && tokenizer.sval.equalsIgnoreCase( "*MESH_VERTEX" ) ) { //$NON-NLS-1$
				int index = parseInt();
				// X = -X, Y = Z, Z = -Y
				coordinates[index*3 + 0] = -parseDouble();
				coordinates[index*3 + 2] = -parseDouble();
				coordinates[index*3 + 1] = parseDouble();
				currentProgress++;
			} else if( tokenizer.ttype == '}' ) {
				return;
			} else if( tokenizer.ttype == java.io.StreamTokenizer.TT_EOF ) {
				throw new InvalidFormatError( Messages.getString("ASEImporter.179") ); //$NON-NLS-1$
			}
		}
	}

	protected void parseUVList( float[] uvs ) throws InvalidFormatError, java.io.IOException {
		tokenizer.nextToken();
		if( tokenizer.ttype != '{' ) {
			throw new InvalidFormatError( Messages.getString("ASEImporter.180") ); //$NON-NLS-1$
		}

		currentProgress = 0;
		while( true ) {
			tokenizer.nextToken();

			if( (tokenizer.sval != null) && tokenizer.sval.equalsIgnoreCase( "*MESH_TVERT" ) ) { //$NON-NLS-1$
				int index = parseInt();
				uvs[index*2 + 0] = parseFloat();
				uvs[index*2 + 1] = parseFloat();
				currentProgress++;
			} else if( tokenizer.ttype == '}' ) {
				return;
			} else if( tokenizer.ttype == java.io.StreamTokenizer.TT_EOF ) {
				throw new InvalidFormatError( Messages.getString("ASEImporter.182") ); //$NON-NLS-1$
			}
		}
	}

	protected void parseNormals( double[] normals ) throws InvalidFormatError, java.io.IOException {
		tokenizer.nextToken();
		if( tokenizer.ttype != '{' ) {
			throw new InvalidFormatError( Messages.getString("ASEImporter.183") ); //$NON-NLS-1$
		}

		// this may not be exactly correct...
		int face = 0;
		int v = 0;
		currentProgress = 0;
		while( true ) {
			tokenizer.nextToken();

			if( (tokenizer.sval != null) && tokenizer.sval.equalsIgnoreCase( "*MESH_VERTEXNORMAL" ) ) { //$NON-NLS-1$
				int index = parseInt();
				int realv = v;
				// reverse face order
				if( v == 1 ) realv = 2;
				if( v == 2 ) realv = 1;
				// X = -X, Y = Z, Z = -Y
				normals[face*9 + realv*3 + 0] = -parseDouble();
				normals[face*9 + realv*3 + 2] = -parseDouble();
				normals[face*9 + realv*3 + 1] = parseDouble();
				v++;
				if( v == 3 ) {
					v = 0;
					face++;
				}
				currentProgress++;
			} else if( tokenizer.ttype == '}' ) {
				return;
			} else if( tokenizer.ttype == java.io.StreamTokenizer.TT_EOF ) {
				throw new InvalidFormatError( Messages.getString("ASEImporter.185") ); //$NON-NLS-1$
			}
		}
	}

	protected void parseVertexColors( double[] colors ) {
		//TODO
	}

	protected void parseFaceList( int[] indices ) throws InvalidFormatError, java.io.IOException {
		tokenizer.nextToken();
		if( tokenizer.ttype != '{' ) {
			throw new InvalidFormatError( Messages.getString("ASEImporter.186") ); //$NON-NLS-1$
		}

		currentProgress = 0;
		while( true ) {
			tokenizer.nextToken();

			if( (tokenizer.sval != null) && tokenizer.sval.equalsIgnoreCase( "*MESH_FACE" ) ) { //$NON-NLS-1$
				parseMeshFace( indices );
				currentProgress++;
			} else if( tokenizer.ttype == '}' ) {
				return;
			} else if( tokenizer.ttype == java.io.StreamTokenizer.TT_EOF ) {
				throw new InvalidFormatError( Messages.getString("ASEImporter.188") ); //$NON-NLS-1$
			}
		}
	}

	protected void parseMeshFace( int[] indices ) throws InvalidFormatError, java.io.IOException {
		int index = parseInt();

		while( true ) {
			tokenizer.nextToken();

			// reverse face order
			if( (tokenizer.sval != null) && tokenizer.sval.equalsIgnoreCase( "A:" ) ) { //$NON-NLS-1$
				indices[index*3 + 0] = parseInt();
			} else if( (tokenizer.sval != null) && tokenizer.sval.equalsIgnoreCase( "B:" ) ) { //$NON-NLS-1$
				indices[index*3 + 2] = parseInt();
			} else if( (tokenizer.sval != null) && tokenizer.sval.equalsIgnoreCase( "C:" ) ) { //$NON-NLS-1$
				indices[index*3 + 1] = parseInt();
			} else if( (tokenizer.sval != null) && tokenizer.sval.equalsIgnoreCase( "*MESH_FACE" ) ) { //$NON-NLS-1$
				tokenizer.pushBack();
				return;
			} else if( tokenizer.ttype == '}' ) {
				tokenizer.pushBack();
				return;
			} else if( tokenizer.ttype == java.io.StreamTokenizer.TT_EOF ) {
				throw new InvalidFormatError( Messages.getString("ASEImporter.193") ); //$NON-NLS-1$
			}
		}
	}

	protected void parseUVFaceList( int[] indices ) throws InvalidFormatError, java.io.IOException {
		tokenizer.nextToken();
		if( tokenizer.ttype != '{' ) {
			throw new InvalidFormatError( Messages.getString("ASEImporter.194") ); //$NON-NLS-1$
		}

		currentProgress = 0;
		while( true ) {
			tokenizer.nextToken();

			// reverse face order
			if( (tokenizer.sval != null) && tokenizer.sval.equalsIgnoreCase( "*MESH_TFACE" ) ) { //$NON-NLS-1$
				int index = parseInt();
				indices[index*3 + 0] = parseInt();
				indices[index*3 + 2] = parseInt();
				indices[index*3 + 1] = parseInt();
				currentProgress++;
			} else if( tokenizer.ttype == '}' ) {
				return;
			} else if( tokenizer.ttype == java.io.StreamTokenizer.TT_EOF ) {
				throw new InvalidFormatError( Messages.getString("ASEImporter.196") ); //$NON-NLS-1$
			}
		}
	}

	protected java.util.ArrayList parseAnimationNode() throws InvalidFormatError, java.io.IOException {
		java.util.ArrayList anims = new java.util.ArrayList();

		tokenizer.nextToken();
		if( tokenizer.ttype != '{' ) {
			throw new InvalidFormatError( Messages.getString("ASEImporter.197") ); //$NON-NLS-1$
		}

		while( true ) {
			tokenizer.nextToken();

			if( (tokenizer.sval != null) && tokenizer.sval.equalsIgnoreCase( "*CONTROL_POS_LINEAR" ) ) { //$NON-NLS-1$
				anims.add( parseLinearPositionAnimation() );
			} else if( (tokenizer.sval != null) && tokenizer.sval.equalsIgnoreCase( "*CONTROL_POS_BEZIER" ) ) { //$NON-NLS-1$
				anims.add( parseBezierPositionAnimation() );
			} else if( (tokenizer.sval != null) && tokenizer.sval.equalsIgnoreCase( "*CONTROL_POS_TCB" ) ) { //$NON-NLS-1$
				anims.add( parseTCBPositionAnimation() );
			} else if( (tokenizer.sval != null) && tokenizer.sval.equalsIgnoreCase( "*CONTROL_ROT_LINEAR" ) ) { // to the best of my knowledge, rotation animations are all handled by quaternion slerping //$NON-NLS-1$
				anims.add( parseLinearQuaternionAnimation() );
			} else if( (tokenizer.sval != null) && tokenizer.sval.equalsIgnoreCase( "*CONTROL_ROT_BEZIER" ) ) { //$NON-NLS-1$
				anims.add( parseBezierQuaternionAnimation() );
			} else if( (tokenizer.sval != null) && tokenizer.sval.equalsIgnoreCase( "*CONTROL_ROT_TCB" ) ) { //$NON-NLS-1$
				anims.add( parseTCBQuaternionAnimation() );
			} else if( (tokenizer.sval != null) && tokenizer.sval.equalsIgnoreCase( "*CONTROL_SCALE_LINEAR" ) ) { //$NON-NLS-1$
				//TODO
			} else if( (tokenizer.sval != null) && tokenizer.sval.equalsIgnoreCase( "*CONTROL_SCALE_BEZIER" ) ) { //$NON-NLS-1$
				//TODO
			} else if( (tokenizer.sval != null) && tokenizer.sval.equalsIgnoreCase( "*CONTROL_SCALE_TCB" ) ) { //$NON-NLS-1$
				//TODO
			} else if( (tokenizer.sval != null) && tokenizer.sval.equalsIgnoreCase( "*CONTROL_POS_TRACK" ) ) { // sampled animations create CatmullRom based keyframe animations //$NON-NLS-1$
				anims.add( parseSampledPositionAnimation() );
			} else if( (tokenizer.sval != null) && tokenizer.sval.equalsIgnoreCase( "*CONTROL_ROT_TRACK" ) ) { //$NON-NLS-1$
				anims.add( parseSampledQuaternionAnimation() );
			} else if( (tokenizer.sval != null) && tokenizer.sval.equalsIgnoreCase( "*CONTROL_SCALE_TRACK" ) ) { //$NON-NLS-1$
				//TODO
			} else if( tokenizer.ttype == '{' ) {
				parseUnknownBlock();
			} else if( tokenizer.ttype == '}' ) {
				return anims;
			} else if( tokenizer.ttype == java.io.StreamTokenizer.TT_EOF ) {
				throw new InvalidFormatError( Messages.getString("ASEImporter.210") ); //$NON-NLS-1$
			}
		}
	}

	protected edu.cmu.cs.stage3.pratt.maxkeyframing.KeyframeResponse parseLinearPositionAnimation() throws InvalidFormatError, java.io.IOException {
		edu.cmu.cs.stage3.pratt.maxkeyframing.PositionKeyframeResponse keyframeResponse = new edu.cmu.cs.stage3.pratt.maxkeyframing.PositionKeyframeResponse();
		keyframeResponse.name.set( "linearPositionKeyframeAnim" ); //$NON-NLS-1$
		edu.cmu.cs.stage3.pratt.maxkeyframing.LinearSpline spline = new edu.cmu.cs.stage3.pratt.maxkeyframing.LinearSpline();
		keyframeResponse.spline.set( spline );

		tokenizer.nextToken();
		if( tokenizer.ttype != '{' ) {
			throw new InvalidFormatError( Messages.getString("ASEImporter.212") ); //$NON-NLS-1$
		}

		while( true ) {
			tokenizer.nextToken();

			if( (tokenizer.sval != null) && tokenizer.sval.equalsIgnoreCase( "*CONTROL_POS_KEY" ) ) { //$NON-NLS-1$
				double time = parseDouble()*timeScaleFactor;
				// don't forget to convert MAX's coordinate system...
				// X = -X, Y = Z, Z = -Y
				double x = -parseDouble();
				double z = -parseDouble();
				double y = parseDouble();
				spline.addKey( new edu.cmu.cs.stage3.pratt.maxkeyframing.Vector3SimpleKey( time, new javax.vecmath.Vector3d( x, y, z ) ) );
			} else if( tokenizer.ttype == '}' ) {
				return keyframeResponse;
			} else if( tokenizer.ttype == java.io.StreamTokenizer.TT_EOF ) {
				throw new InvalidFormatError( Messages.getString("ASEImporter.214") ); //$NON-NLS-1$
			}
		}
	}

	protected edu.cmu.cs.stage3.pratt.maxkeyframing.KeyframeResponse parseBezierPositionAnimation() throws InvalidFormatError, java.io.IOException {
		edu.cmu.cs.stage3.pratt.maxkeyframing.PositionKeyframeResponse keyframeResponse = new edu.cmu.cs.stage3.pratt.maxkeyframing.PositionKeyframeResponse();
		keyframeResponse.name.set( "bezierPositionKeyframeAnim" ); //$NON-NLS-1$
		edu.cmu.cs.stage3.pratt.maxkeyframing.BezierSpline spline = new edu.cmu.cs.stage3.pratt.maxkeyframing.BezierSpline();
		keyframeResponse.spline.set( spline );

		tokenizer.nextToken();
		if( tokenizer.ttype != '{' ) {
			throw new InvalidFormatError( Messages.getString("ASEImporter.216") ); //$NON-NLS-1$
		}

		while( true ) {
			tokenizer.nextToken();

			if( (tokenizer.sval != null) && tokenizer.sval.equalsIgnoreCase( "*CONTROL_BEZIER_POS_KEY" ) ) { //$NON-NLS-1$
				double time = parseDouble()*timeScaleFactor;
				// don't forget to convert MAX's coordinate system...
				// X = -X, Y = Z, Z = -Y
				double x = -parseDouble();
				double z = -parseDouble();
				double y = parseDouble();
				double intan_x = -parseDouble();
				double intan_z = -parseDouble();
				double intan_y = parseDouble();
				double outtan_x = -parseDouble();
				double outtan_z = -parseDouble();
				double outtan_y = parseDouble();
				spline.addKey( new edu.cmu.cs.stage3.pratt.maxkeyframing.Vector3BezierKey( time, new javax.vecmath.Vector3d( x, y, z ), new javax.vecmath.Vector3d( intan_x, intan_y, intan_z ), new javax.vecmath.Vector3d( outtan_x, outtan_y, outtan_z ) ) );
			} else if( tokenizer.ttype == '}' ) {
				spline.convertMAXTangentsToBezierTangents( timeScaleFactor );
				return keyframeResponse;
			} else if( tokenizer.ttype == java.io.StreamTokenizer.TT_EOF ) {
				throw new InvalidFormatError( Messages.getString("ASEImporter.218") ); //$NON-NLS-1$
			}
		}
	}

	protected edu.cmu.cs.stage3.pratt.maxkeyframing.KeyframeResponse parseTCBPositionAnimation() throws InvalidFormatError, java.io.IOException {
		edu.cmu.cs.stage3.pratt.maxkeyframing.PositionKeyframeResponse keyframeResponse = new edu.cmu.cs.stage3.pratt.maxkeyframing.PositionKeyframeResponse();
		keyframeResponse.name.set( "tcbPositionKeyframeAnim" ); //$NON-NLS-1$
		edu.cmu.cs.stage3.pratt.maxkeyframing.TCBSpline spline = new edu.cmu.cs.stage3.pratt.maxkeyframing.TCBSpline();
		keyframeResponse.spline.set( spline );

		tokenizer.nextToken();
		if( tokenizer.ttype != '{' ) {
			throw new InvalidFormatError( Messages.getString("ASEImporter.220") ); //$NON-NLS-1$
		}

		while( true ) {
			tokenizer.nextToken();

			if( (tokenizer.sval != null) && tokenizer.sval.equalsIgnoreCase( "*CONTROL_TCB_POS_KEY" ) ) { //$NON-NLS-1$
				double time = parseDouble()*timeScaleFactor;
				// don't forget to convert MAX's coordinate system...
				// X = -X, Y = Z, Z = -Y
				double x = -parseDouble();
				double z = -parseDouble();
				double y = parseDouble();
				double tension = parseDouble();
				double continuity = parseDouble();
				double bias = parseDouble();
				double easeIn = parseDouble();  // NOT USED AT THE MOMENT
				double easeOut = parseDouble(); // NOT USED AT THE MOMENT
				spline.addKey( new edu.cmu.cs.stage3.pratt.maxkeyframing.Vector3TCBKey( time, new javax.vecmath.Vector3d( x, y, z ), tension, continuity, bias ) );
			} else if( tokenizer.ttype == '}' ) {
				return keyframeResponse;
			} else if( tokenizer.ttype == java.io.StreamTokenizer.TT_EOF ) {
				throw new InvalidFormatError( Messages.getString("ASEImporter.222") ); //$NON-NLS-1$
			}
		}
	}

	protected edu.cmu.cs.stage3.pratt.maxkeyframing.KeyframeResponse parseLinearQuaternionAnimation() throws InvalidFormatError, java.io.IOException {
		edu.cmu.cs.stage3.pratt.maxkeyframing.QuaternionKeyframeResponse keyframeResponse = new edu.cmu.cs.stage3.pratt.maxkeyframing.QuaternionKeyframeResponse();
		keyframeResponse.name.set( "quaternionKeyframeAnim" ); //$NON-NLS-1$
		edu.cmu.cs.stage3.pratt.maxkeyframing.QuaternionSlerpSpline spline = new edu.cmu.cs.stage3.pratt.maxkeyframing.QuaternionSlerpSpline();
		keyframeResponse.spline.set( spline );

		tokenizer.nextToken();
		if( tokenizer.ttype != '{' ) {
			throw new InvalidFormatError( Messages.getString("ASEImporter.224") ); //$NON-NLS-1$
		}

		while( true ) {
			tokenizer.nextToken();

			if( (tokenizer.sval != null) && tokenizer.sval.equalsIgnoreCase( "*CONTROL_ROT_KEY" ) ) { //$NON-NLS-1$
				double time = parseDouble()*timeScaleFactor;
				// don't forget to convert MAX's coordinate system...
				// X = -X, Y = Z, Z = -Y
				double axis_x = -parseDouble();
				double axis_z = -parseDouble();
				double axis_y = parseDouble();
				double angle = -parseDouble();
				spline.addKey( new edu.cmu.cs.stage3.pratt.maxkeyframing.QuaternionKey( time, new edu.cmu.cs.stage3.math.Quaternion( new edu.cmu.cs.stage3.math.AxisAngle( axis_x, axis_y, axis_z, angle ) ) ) );
			} else if( tokenizer.ttype == '}' ) {
				return keyframeResponse;
			} else if( tokenizer.ttype == java.io.StreamTokenizer.TT_EOF ) {
				throw new InvalidFormatError( Messages.getString("ASEImporter.226") ); //$NON-NLS-1$
			}
		}
	}

	protected edu.cmu.cs.stage3.pratt.maxkeyframing.KeyframeResponse parseBezierQuaternionAnimation() throws InvalidFormatError, java.io.IOException {
		edu.cmu.cs.stage3.pratt.maxkeyframing.QuaternionKeyframeResponse keyframeResponse = new edu.cmu.cs.stage3.pratt.maxkeyframing.QuaternionKeyframeResponse();
		keyframeResponse.name.set( "quaternionKeyframeAnim" ); //$NON-NLS-1$
		edu.cmu.cs.stage3.pratt.maxkeyframing.QuaternionSlerpSpline spline = new edu.cmu.cs.stage3.pratt.maxkeyframing.QuaternionSlerpSpline();
		keyframeResponse.spline.set( spline );

		tokenizer.nextToken();
		if( tokenizer.ttype != '{' ) {
			throw new InvalidFormatError( Messages.getString("ASEImporter.228") ); //$NON-NLS-1$
		}

		while( true ) {
			tokenizer.nextToken();

			if( (tokenizer.sval != null) && tokenizer.sval.equalsIgnoreCase( "*CONTROL_ROT_KEY" ) ) { //$NON-NLS-1$
				double time = parseDouble()*timeScaleFactor;
				// don't forget to convert MAX's coordinate system...
				// X = -X, Y = Z, Z = -Y
				double axis_x = -parseDouble();
				double axis_z = -parseDouble();
				double axis_y = parseDouble();
				double angle = -parseDouble();
				spline.addKey( new edu.cmu.cs.stage3.pratt.maxkeyframing.QuaternionKey( time, new edu.cmu.cs.stage3.math.Quaternion( new edu.cmu.cs.stage3.math.AxisAngle( axis_x, axis_y, axis_z, angle ) ) ) );
			} else if( tokenizer.ttype == '}' ) {
				return keyframeResponse;
			} else if( tokenizer.ttype == java.io.StreamTokenizer.TT_EOF ) {
				throw new InvalidFormatError( Messages.getString("ASEImporter.230") ); //$NON-NLS-1$
			}
		}
	}

	protected edu.cmu.cs.stage3.pratt.maxkeyframing.KeyframeResponse parseTCBQuaternionAnimation() throws InvalidFormatError, java.io.IOException {
		edu.cmu.cs.stage3.pratt.maxkeyframing.QuaternionKeyframeResponse keyframeResponse = new edu.cmu.cs.stage3.pratt.maxkeyframing.QuaternionKeyframeResponse();
		keyframeResponse.name.set( "tcbQuaternionKeyframeAnim" ); //$NON-NLS-1$
		edu.cmu.cs.stage3.pratt.maxkeyframing.TCBSpline spline = new edu.cmu.cs.stage3.pratt.maxkeyframing.TCBSpline();
		keyframeResponse.spline.set( spline );

		tokenizer.nextToken();
		if( tokenizer.ttype != '{' ) {
			throw new InvalidFormatError( Messages.getString("ASEImporter.232") ); //$NON-NLS-1$
		}

		while( true ) {
			tokenizer.nextToken();

			if( (tokenizer.sval != null) && tokenizer.sval.equalsIgnoreCase( "*CONTROL_TCB_ROT_KEY" ) ) { //$NON-NLS-1$
				double time = parseDouble()*timeScaleFactor;
				// don't forget to convert MAX's coordinate system...
				// X = -X, Y = Z, Z = -Y
				double axis_x = -parseDouble();
				double axis_z = -parseDouble();
				double axis_y = parseDouble();
				double angle = -parseDouble();
				double tension = parseDouble();     // NOT USED IN QUATERNION ANIMATION
				double continuity = parseDouble();  // NOT USED IN QUATERNION ANIMATION
				double bias = parseDouble();        // NOT USED IN QUATERNION ANIMATION
				double easeIn = parseDouble();  // NOT USED AT THE MOMENT
				double easeOut = parseDouble(); // NOT USED AT THE MOMENT
				spline.addKey( new edu.cmu.cs.stage3.pratt.maxkeyframing.QuaternionTCBKey( time, new edu.cmu.cs.stage3.math.Quaternion( new edu.cmu.cs.stage3.math.AxisAngle( axis_x, axis_y, axis_z, angle ) ), tension, continuity, bias ) );
			} else if( tokenizer.ttype == '}' ) {
				spline.correctForMAXRelativeKeys();
				return keyframeResponse;
			} else if( tokenizer.ttype == java.io.StreamTokenizer.TT_EOF ) {
				throw new InvalidFormatError( Messages.getString("ASEImporter.234") ); //$NON-NLS-1$
			}
		}
	}

	protected edu.cmu.cs.stage3.pratt.maxkeyframing.KeyframeResponse parseLinearScaleAnimation() throws InvalidFormatError, java.io.IOException {
		edu.cmu.cs.stage3.pratt.maxkeyframing.ScaleKeyframeResponse keyframeResponse = new edu.cmu.cs.stage3.pratt.maxkeyframing.ScaleKeyframeResponse();
		keyframeResponse.name.set( "linearScaleKeyframeAnim" ); //$NON-NLS-1$
		edu.cmu.cs.stage3.pratt.maxkeyframing.LinearSpline spline = new edu.cmu.cs.stage3.pratt.maxkeyframing.LinearSpline();
		keyframeResponse.spline.set( spline );

		tokenizer.nextToken();
		if( tokenizer.ttype != '{' ) {
			throw new InvalidFormatError( Messages.getString("ASEImporter.236") ); //$NON-NLS-1$
		}

		while( true ) {
			tokenizer.nextToken();

			if( (tokenizer.sval != null) && tokenizer.sval.equalsIgnoreCase( "*CONTROL_SCALE_KEY" ) ) { //$NON-NLS-1$
				//TODO
			} else if( tokenizer.ttype == '}' ) {
				return keyframeResponse;
			} else if( tokenizer.ttype == java.io.StreamTokenizer.TT_EOF ) {
				throw new InvalidFormatError( Messages.getString("ASEImporter.238") ); //$NON-NLS-1$
			}
		}
	}

	protected edu.cmu.cs.stage3.pratt.maxkeyframing.KeyframeResponse parseBezierScaleAnimation() throws InvalidFormatError, java.io.IOException {
		edu.cmu.cs.stage3.pratt.maxkeyframing.ScaleKeyframeResponse keyframeResponse = new edu.cmu.cs.stage3.pratt.maxkeyframing.ScaleKeyframeResponse();
		keyframeResponse.name.set( "bezierScaleKeyframeAnim" ); //$NON-NLS-1$
		edu.cmu.cs.stage3.pratt.maxkeyframing.BezierSpline spline = new edu.cmu.cs.stage3.pratt.maxkeyframing.BezierSpline();
		keyframeResponse.spline.set( spline );

		tokenizer.nextToken();
		if( tokenizer.ttype != '{' ) {
			throw new InvalidFormatError( Messages.getString("ASEImporter.240") ); //$NON-NLS-1$
		}

		while( true ) {
			tokenizer.nextToken();

			if( (tokenizer.sval != null) && tokenizer.sval.equalsIgnoreCase( "*CONTROL_BEZIER_SCALE_KEY" ) ) { //$NON-NLS-1$
				//TODO
			} else if( tokenizer.ttype == '}' ) {
				spline.convertMAXTangentsToBezierTangents( timeScaleFactor );
				return keyframeResponse;
			} else if( tokenizer.ttype == java.io.StreamTokenizer.TT_EOF ) {
				throw new InvalidFormatError( Messages.getString("ASEImporter.242") ); //$NON-NLS-1$
			}
		}
	}

	protected edu.cmu.cs.stage3.pratt.maxkeyframing.KeyframeResponse parseTCBScaleAnimation() throws InvalidFormatError, java.io.IOException {
		edu.cmu.cs.stage3.pratt.maxkeyframing.ScaleKeyframeResponse keyframeResponse = new edu.cmu.cs.stage3.pratt.maxkeyframing.ScaleKeyframeResponse();
		keyframeResponse.name.set( "tcbScaleKeyframeAnim" ); //$NON-NLS-1$
		edu.cmu.cs.stage3.pratt.maxkeyframing.TCBSpline spline = new edu.cmu.cs.stage3.pratt.maxkeyframing.TCBSpline();
		keyframeResponse.spline.set( spline );

		tokenizer.nextToken();
		if( tokenizer.ttype != '{' ) {
			throw new InvalidFormatError( Messages.getString("ASEImporter.244") ); //$NON-NLS-1$
		}

		while( true ) {
			tokenizer.nextToken();

			if( (tokenizer.sval != null) && tokenizer.sval.equalsIgnoreCase( "*CONTROL_TCB_SCALE_KEY" ) ) { //$NON-NLS-1$
				//TODO
			} else if( tokenizer.ttype == '}' ) {
				return keyframeResponse;
			} else if( tokenizer.ttype == java.io.StreamTokenizer.TT_EOF ) {
				throw new InvalidFormatError( Messages.getString("ASEImporter.246") ); //$NON-NLS-1$
			}
		}
	}

	protected edu.cmu.cs.stage3.pratt.maxkeyframing.KeyframeResponse parseSampledPositionAnimation() throws InvalidFormatError, java.io.IOException {
		edu.cmu.cs.stage3.pratt.maxkeyframing.PositionKeyframeResponse keyframeResponse = new edu.cmu.cs.stage3.pratt.maxkeyframing.PositionKeyframeResponse();
		keyframeResponse.name.set( "sampledPositionAnim" ); //$NON-NLS-1$
		edu.cmu.cs.stage3.pratt.maxkeyframing.CatmullRomSpline spline = new edu.cmu.cs.stage3.pratt.maxkeyframing.CatmullRomSpline();
		keyframeResponse.spline.set( spline );

		tokenizer.nextToken();
		if( tokenizer.ttype != '{' ) {
			throw new InvalidFormatError( Messages.getString("ASEImporter.248") ); //$NON-NLS-1$
		}

		while( true ) {
			tokenizer.nextToken();

			if( (tokenizer.sval != null) && tokenizer.sval.equalsIgnoreCase( "*CONTROL_POS_SAMPLE" ) ) { //$NON-NLS-1$
				double time = parseDouble()*timeScaleFactor;
				// don't forget to convert MAX's coordinate system...
				// X = -X, Y = Z, Z = -Y
				double x = -parseDouble();
				double z = -parseDouble();
				double y = parseDouble();
				spline.addKey( new edu.cmu.cs.stage3.pratt.maxkeyframing.Vector3SimpleKey( time, new javax.vecmath.Vector3d( x, y, z ) ) );
			} else if( tokenizer.ttype == '}' ) {
				return keyframeResponse;
			} else if( tokenizer.ttype == java.io.StreamTokenizer.TT_EOF ) {
				throw new InvalidFormatError( Messages.getString("ASEImporter.250") ); //$NON-NLS-1$
			}
		}
	}

	protected edu.cmu.cs.stage3.pratt.maxkeyframing.KeyframeResponse parseSampledQuaternionAnimation() throws InvalidFormatError, java.io.IOException {
		edu.cmu.cs.stage3.pratt.maxkeyframing.QuaternionKeyframeResponse keyframeResponse = new edu.cmu.cs.stage3.pratt.maxkeyframing.QuaternionKeyframeResponse();
		keyframeResponse.name.set( "sampledQuaternionAnim" ); //$NON-NLS-1$
		edu.cmu.cs.stage3.pratt.maxkeyframing.QuaternionSlerpSpline spline = new edu.cmu.cs.stage3.pratt.maxkeyframing.QuaternionSlerpSpline();
		keyframeResponse.spline.set( spline );

		tokenizer.nextToken();
		if( tokenizer.ttype != '{' ) {
			throw new InvalidFormatError( Messages.getString("ASEImporter.252") ); //$NON-NLS-1$
		}

		while( true ) {
			tokenizer.nextToken();

			if( (tokenizer.sval != null) && tokenizer.sval.equalsIgnoreCase( "*CONTROL_ROT_SAMPLE" ) ) { //$NON-NLS-1$
				double time = parseDouble()*timeScaleFactor;
				// don't forget to convert MAX's coordinate system...
				// X = -X, Y = Z, Z = -Y
				double axis_x = -parseDouble();
				double axis_z = -parseDouble();
				double axis_y = parseDouble();
				double angle = -parseDouble();
				spline.addKey( new edu.cmu.cs.stage3.pratt.maxkeyframing.QuaternionKey( time, new edu.cmu.cs.stage3.math.Quaternion( new edu.cmu.cs.stage3.math.AxisAngle( axis_x, axis_y, axis_z, angle ) ) ) );
			} else if( tokenizer.ttype == '}' ) {
				spline.correctForMAXRelativeKeys();
				return keyframeResponse;
			} else if( tokenizer.ttype == java.io.StreamTokenizer.TT_EOF ) {
				throw new InvalidFormatError( Messages.getString("ASEImporter.254") ); //$NON-NLS-1$
			}
		}
	}

	class InvalidFormatError extends Error {
		public InvalidFormatError( String s ) {
			super( s );
		}
	}

	class ProgressDialog extends javax.swing.JDialog {
		protected javax.swing.JLabel linesLabel = new javax.swing.JLabel( Messages.getString("ASEImporter.255") ); //$NON-NLS-1$
		protected javax.swing.JLabel objectLabel = new javax.swing.JLabel( Messages.getString("ASEImporter.256") ); //$NON-NLS-1$
		protected javax.swing.JLabel progressLabel = new javax.swing.JLabel( Messages.getString("ASEImporter.257") ); //$NON-NLS-1$
		protected javax.swing.JProgressBar progressBar = new javax.swing.JProgressBar();
		protected javax.swing.JPanel progressPanel = new javax.swing.JPanel();
		protected javax.swing.Timer timer = null;

		public ProgressDialog() {
			super( (java.awt.Frame)null, Messages.getString("ASEImporter.258"), false ); //$NON-NLS-1$

			progressBar.setMinimum( 0 );
			linesLabel.setAlignmentX( 0.0f );
			objectLabel.setAlignmentX( 0.0f );
			progressPanel.setAlignmentX( 0.0f );

			progressPanel.setLayout( new java.awt.BorderLayout() );
			progressPanel.add( java.awt.BorderLayout.WEST, progressLabel );
			progressPanel.add( java.awt.BorderLayout.CENTER, progressBar );

			getContentPane().setLayout( new javax.swing.BoxLayout( getContentPane(), javax.swing.BoxLayout.Y_AXIS ) );
			getContentPane().add( linesLabel );
			getContentPane().add( objectLabel );
			getContentPane().add( progressPanel );

			setDefaultCloseOperation( javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE );

			timer = new javax.swing.Timer( 100,
				new java.awt.event.ActionListener() {
					public void actionPerformed( java.awt.event.ActionEvent ev ) {
						linesLabel.setText( Messages.getString("ASEImporter.259") + ASEImporter.this.tokenizer.lineno() ); //$NON-NLS-1$
						objectLabel.setText( Messages.getString("ASEImporter.260") + ASEImporter.this.currentObject ); //$NON-NLS-1$
						progressLabel.setText( Messages.getString("ASEImporter.261") + ASEImporter.this.currentlyLoading + ": " ); //$NON-NLS-1$ //$NON-NLS-2$
						progressBar.setValue( ASEImporter.this.currentProgress );
					}
				}
			);

			pack();
			setVisible( true );
		}

		public void start() {
			timer.start();
		}

		public void stop() {
			timer.stop();
		}

		public void setMax( final int max ) {
			progressBar.setMaximum( max );
		}
	}

	private class Material {
		public String name;
		public edu.cmu.cs.stage3.alice.scenegraph.Color ambient;
		public edu.cmu.cs.stage3.alice.scenegraph.Color diffuse;
		public edu.cmu.cs.stage3.alice.scenegraph.Color specular;
		public double shine;
		public double shinestrength;
		public double transparency;
		public edu.cmu.cs.stage3.alice.core.TextureMap ambientTexture = null;
		public edu.cmu.cs.stage3.alice.core.TextureMap diffuseTexture = null;
		public edu.cmu.cs.stage3.alice.core.TextureMap shineTexture = null;
		public edu.cmu.cs.stage3.alice.core.TextureMap shineStrengthTexture = null;
		public edu.cmu.cs.stage3.alice.core.TextureMap selfIllumTexture = null;
		public edu.cmu.cs.stage3.alice.core.TextureMap opacityTexture = null;
		public edu.cmu.cs.stage3.alice.core.TextureMap bumpTexture = null;
	}

	/*
	private class ASEOptionsDialog extends javax.swing.JDialog {
		javax.swing.JCheckBox useSpecularCheckBox = new javax.swing.JCheckBox( "Use specular highlighting information", useSpecular );
		javax.swing.JCheckBox diffuseToWhiteCheckBox = new javax.swing.JCheckBox( "Set diffuse color to white for textured objects", diffuseToWhite );
		javax.swing.JButton okayButton = new javax.swing.JButton( "Okay" );

		public ASEOptionsDialog() {
			super( (javax.swing.JFrame)null );

			setTitle( "ASE importing options" );
			setModal( true );
			setDefaultCloseOperation( javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE );

			java.awt.event.ItemListener checkBoxListener = new java.awt.event.ItemListener() {
				public void itemStateChanged( java.awt.event.ItemEvent ev ) {
					javax.swing.JCheckBox source = (javax.swing.JCheckBox)ev.getSource();
					if( source == useSpecularCheckBox ) {
						ASEImporter.this.useSpecular = useSpecularCheckBox.isSelected();
					} else if( source == diffuseToWhiteCheckBox ) {
						ASEImporter.this.diffuseToWhite = diffuseToWhiteCheckBox.isSelected();
					}
				}
			};
			useSpecularCheckBox.addItemListener( checkBoxListener );
			diffuseToWhiteCheckBox.addItemListener( checkBoxListener );
			useSpecularCheckBox.setAlignmentX( 0.0f );
			diffuseToWhiteCheckBox.setAlignmentX( 0.0f );

			java.awt.event.ActionListener okayListener = new java.awt.event.ActionListener() {
				public void actionPerformed( java.awt.event.ActionEvent ev ) {
					hide();
				}
			};
			okayButton.addActionListener( okayListener );
			okayButton.setAlignmentX( 0.5f );

			javax.swing.border.Border padding = javax.swing.BorderFactory.createEmptyBorder( 10, 10, 10, 10 );
			javax.swing.JPanel optionsPanel = new javax.swing.JPanel();
			optionsPanel.setAlignmentX( .5f );
			optionsPanel.setBorder( padding );
			optionsPanel.setLayout( new javax.swing.BoxLayout( optionsPanel, javax.swing.BoxLayout.Y_AXIS ) );
			optionsPanel.add( useSpecularCheckBox );
			optionsPanel.add( diffuseToWhiteCheckBox );
			javax.swing.JPanel mainPanel = new javax.swing.JPanel();
			mainPanel.setBorder( padding );
			mainPanel.setLayout( new javax.swing.BoxLayout( mainPanel, javax.swing.BoxLayout.Y_AXIS ) );
			mainPanel.add( optionsPanel );
			mainPanel.add( javax.swing.Box.createVerticalStrut( 5 ) );
			mainPanel.add( javax.swing.Box.createVerticalGlue() );
			mainPanel.add( okayButton );
			getContentPane().setLayout( new java.awt.BorderLayout() );
			getContentPane().add( mainPanel, java.awt.BorderLayout.CENTER );

			pack();
		}
	}
	*/
}

