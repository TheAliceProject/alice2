package edu.cmu.cs.stage3.media.jmfmedia;

public class FixedJavaSoundRenderer extends com.sun.media.renderer.audio.JavaSoundRenderer {
    public static void usurpControlFromJavaSoundRenderer() {
        final String OFFENDING_RENDERER_PLUGIN_NAME = com.sun.media.renderer.audio.JavaSoundRenderer.class.getName();
        javax.media.Format[] rendererInputFormats = javax.media.PlugInManager.getSupportedInputFormats( OFFENDING_RENDERER_PLUGIN_NAME, javax.media.PlugInManager.RENDERER );
        javax.media.Format[] rendererOutputFormats = javax.media.PlugInManager.getSupportedOutputFormats( OFFENDING_RENDERER_PLUGIN_NAME, javax.media.PlugInManager.RENDERER );
        //should be only rendererInputFormats
        if( rendererInputFormats != null || rendererOutputFormats != null ) {
            final String REPLACEMENT_RENDERER_PLUGIN_NAME = FixedJavaSoundRenderer.class.getName();
            javax.media.PlugInManager.removePlugIn( OFFENDING_RENDERER_PLUGIN_NAME, javax.media.PlugInManager.RENDERER );
            javax.media.PlugInManager.addPlugIn( REPLACEMENT_RENDERER_PLUGIN_NAME, rendererInputFormats, rendererOutputFormats, javax.media.PlugInManager.RENDERER );
        }
    }
 
    @Override
    protected com.sun.media.renderer.audio.device.AudioOutput createDevice( javax.media.format.AudioFormat format ) {
        return new com.sun.media.renderer.audio.device.JavaSoundOutput() {
            @Override
            public void setGain( double g ) {
            	if (this.gc != null) {
            		g = Math.max( g, this.gc.getMinimum() );
            		g = Math.min( g, this.gc.getMaximum() );
            	}
        		super.setGain( g );
            }
        };
    }
}
