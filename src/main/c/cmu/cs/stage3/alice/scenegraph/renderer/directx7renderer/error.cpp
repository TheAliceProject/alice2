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

#include <ddraw.h>
#include <d3d.h>
#include <stdio.h>

static const char* g_vcFile = NULL;
static int g_nLine = NULL;

void ERROR_SetFileAndLine( const char* vcFile, int nLine ) {
	g_vcFile = vcFile;
	g_nLine = nLine;
	char vcBuffer[ 1024 ];
	sprintf( vcBuffer, "__FILE__ %s; __LINE__ %d", g_vcFile, g_nLine );
}

const char* ERROR_GetText( int nResult ) {
	if( g_vcFile!=NULL ) {
		fprintf( stderr, "__FILE__ %s\n", g_vcFile );
		fprintf( stderr, "__LINE__ %d\n", g_nLine );
		DWORD dwError = GetLastError();
		if( dwError!=ERROR_SUCCESS ) {
			fprintf( stderr, "LastError: %d\n", dwError );
		}
		fflush( stderr );
	}
	switch( nResult ) {
	case DDERR_ALREADYINITIALIZED:
		return "The object has already been initialized.";
	case DDERR_BLTFASTCANTCLIP:
		return "A DirectDrawClipper object is attached to a source surface that has passed into a call to the IDirectDrawSurface7::BltFast method.";
	case DDERR_CANNOTATTACHSURFACE:
		return "A surface cannot be attached to another requested surface.";
	case DDERR_CANNOTDETACHSURFACE:
		return "A surface cannot be detached from another requested surface.";
	case DDERR_CANTCREATEDC:
		return "Windows cannot create any more device contexts (DCs), or a DC has requested a palette-indexed surface when the surface had no palette and the display mode was not palette-indexed (in this case DirectDraw cannot select a proper palette into the DC).";
	case DDERR_CANTDUPLICATE:
		return "Primary and 3-D surfaces, or surfaces that are implicitly created, cannot be duplicated.";
	case DDERR_CANTLOCKSURFACE:
		return "Access to this surface is refused because an attempt was made to lock the primary surface without DCI support.";
	case DDERR_CANTPAGELOCK:
		return "An attempt to page-lock a surface failed. Page lock does not work on a display-memory surface or an emulated primary surface.";
	case DDERR_CANTPAGEUNLOCK:
		return "An attempt to page-unlock a surface failed. Page unlock does not work on a display-memory surface or an emulated primary surface.";
	case DDERR_CLIPPERISUSINGHWND:
		return "An attempt was made to set a clip list for a DirectDrawClipper object that is already monitoring a window handle.";
	case DDERR_COLORKEYNOTSET:
		return "No source color key is specified for this operation.";
	case DDERR_CURRENTLYNOTAVAIL:
		return "No support is currently available.";
	case DDERR_DDSCAPSCOMPLEXREQUIRED:
		return "New for DirectX 7.0. The surface requires the DDSCAPS_COMPLEX flag.";
	case DDERR_DCALREADYCREATED:
		return "A device context (DC) has already been returned for this surface. Only one DC can be retrieved for each surface.";
	case DDERR_DEVICEDOESNTOWNSURFACE:
		return "Surfaces created by one DirectDraw device cannot be used directly by another DirectDraw device.";
	case DDERR_DIRECTDRAWALREADYCREATED:
		return "A DirectDraw object representing this driver has already been created for this process.";
	case DDERR_EXCEPTION:
		return "An exception was encountered while performing the requested operation.";
	case DDERR_EXCLUSIVEMODEALREADYSET:
		return "An attempt was made to set the cooperative level when it was already set to exclusive.";
	case DDERR_EXPIRED:
		return "The data has expired and is therefore no longer valid.";
	case DDERR_GENERIC:
		return "There is an undefined error condition.";
	case DDERR_HEIGHTALIGN:
		return "The height of the provided rectangle is not a multiple of the required alignment.";
	case DDERR_HWNDALREADYSET:
		return "The DirectDraw cooperative-level window handle has already been set. It cannot be reset while the process has surfaces or palettes created.";
	case DDERR_HWNDSUBCLASSED:
		return "DirectDraw is prevented from restoring state because the DirectDraw cooperative-level window handle has been subclassed.";
	case DDERR_IMPLICITLYCREATED:
		return "The surface cannot be restored because it is an implicitly created surface.";
	case DDERR_INCOMPATIBLEPRIMARY:
		return "The primary surface creation request does not match the existing primary surface.";
	case DDERR_INVALIDCAPS:
		return "One or more of the capability bits passed to the callback function are incorrect.";
	case DDERR_INVALIDCLIPLIST:
		return "DirectDraw does not support the provided clip list.";
	case DDERR_INVALIDDIRECTDRAWGUID:
		return "The globally unique identifier (GUID) passed to the DirectDrawCreate function is not a valid DirectDraw driver identifier.";
	case DDERR_INVALIDMODE:
		return "DirectDraw does not support the requested mode.";
	case DDERR_INVALIDOBJECT:
		return "DirectDraw received a pointer that was an invalid DirectDraw object.";
	case DDERR_INVALIDPARAMS:
		return "One or more of the parameters passed to the method are incorrect.";
	case DDERR_INVALIDPIXELFORMAT:
		return "The pixel format was invalid as specified.";
	case DDERR_INVALIDPOSITION:
		return "The position of the overlay on the destination is no longer legal.";
	case DDERR_INVALIDRECT:
		return "The provided rectangle was invalid.";
	case DDERR_INVALIDSTREAM:
		return "The specified stream contains invalid data.";
	case DDERR_INVALIDSURFACETYPE:
		return "The surface was of the wrong type.";
	case DDERR_LOCKEDSURFACES:
		return "One or more surfaces are locked, causing the failure of the requested operation.";
	case DDERR_MOREDATA:
		return "There is more data available than the specified buffer size can hold.";
	case DDERR_NEWMODE:
		return "New for DirectX 7.0. When IDirectDraw7::StartModeTest is called with the DDSMT_ISTESTREQUIRED flag, it may return this value to denote that some or all of the resolutions can and should be tested. IDirectDraw7::EvaluateMode returns this value to indicate that the test has switched to a new display mode.";
	case DDERR_NO3D:
		return "No 3-D hardware or emulation is present.";
	case DDERR_NOALPHAHW:
		return "No alpha-acceleration hardware is present or available, causing the failure of the requested operation.";
	case DDERR_NOBLTHW:
		return "No blitter hardware is present.";
	case DDERR_NOCLIPLIST:
		return "No clip list is available.";
	case DDERR_NOCLIPPERATTACHED:
		return "No DirectDrawClipper object is attached to the surface object.";
	case DDERR_NOCOLORCONVHW:
		return "No color-conversion hardware is present or available.";
	case DDERR_NOCOLORKEY:
		return "The surface does not currently have a color key.";
	case DDERR_NOCOLORKEYHW:
		return "There is no hardware support for the destination color key.";
	case DDERR_NOCOOPERATIVELEVELSET:
		return "A create function was called without the IDirectDraw7::SetCooperativeLevel method.";
	case DDERR_NODC:
		return "No device context (DC) has ever been created for this surface.";
	case DDERR_NODDROPSHW:
		return "No DirectDraw raster-operation (ROP) hardware is available.";
	case DDERR_NODIRECTDRAWHW:
		return "Hardware-only DirectDraw object creation is not possible; the driver does not support any hardware.";
	case DDERR_NODIRECTDRAWSUPPORT:
		return "DirectDraw support is not possible with the current display driver.";
	case DDERR_NODRIVERSUPPORT:
		return "New for DirectX 7.0. Testing cannot proceed because the display adapter driver does not enumerate refresh rates.";
	case DDERR_NOEMULATION:
		return "Software emulation is not available.";
	case DDERR_NOEXCLUSIVEMODE:
		return "The operation requires the application to have exclusive mode, but the application does not have exclusive mode.";
	case DDERR_NOFLIPHW:
		return "Flipping visible surfaces is not supported.";
	case DDERR_NOFOCUSWINDOW:
		return "An attempt was made to create or set a device window without first setting the focus window.";
	case DDERR_NOGDI:
		return "No GDI is present.";
	case DDERR_NOHWND:
		return "Clipper notification requires a window handle, or no window handle has been previously set as the cooperative level window handle.";
	case DDERR_NOMIPMAPHW:
		return "No mipmap-capable texture mapping hardware is present or available.";
	case DDERR_NOMIRRORHW:
		return "No mirroring hardware is present or available.";
	case DDERR_NOMONITORINFORMATION:
		return "New for DirectX 7.0. Testing cannot proceed because the monitor has no associated EDID data.";
	case DDERR_NONONLOCALVIDMEM:
		return "An attempt was made to allocate nonlocal video memory from a device that does not support nonlocal video memory.";
	case DDERR_NOOPTIMIZEHW:
		return "The device does not support optimized surfaces.";
	case DDERR_NOOVERLAYDEST:
		return "The IDirectDrawSurface7::GetOverlayPosition method is called on an overlay that the IDirectDrawSurface7::UpdateOverlay method has not been called on to establish as a destination.";
	case DDERR_NOOVERLAYHW:
		return "No overlay hardware is present or available.";
	case DDERR_NOPALETTEATTACHED:
		return "No palette object is attached to this surface.";
	case DDERR_NOPALETTEHW:
		return "There is no hardware support for 16- or 256-color palettes.";
	case DDERR_NORASTEROPHW:
		return "No appropriate raster-operation hardware is present or available.";
	case DDERR_NOROTATIONHW:
		return "No rotation hardware is present or available.";
	case DDERR_NOSTEREOHARDWARE:
		return "There is no stereo hardware present or available.";
	case DDERR_NOSTRETCHHW:
		return "There is no hardware support for stretching.";
	case DDERR_NOSURFACELEFT:
		return "There is no hardware present that supports stereo surfaces.";
	case DDERR_NOT4BITCOLOR:
		return "The DirectDrawSurface object is not using a 4-bit color palette, and the requested operation requires a 4-bit color palette.";
	case DDERR_NOT4BITCOLORINDEX:
		return "The DirectDrawSurface object is not using a 4-bit color index palette, and the requested operation requires a 4-bit color index palette.";
	case DDERR_NOT8BITCOLOR:
		return "The DirectDrawSurface object is not using an 8-bit color palette, and the requested operation requires an 8-bit color palette.";
	case DDERR_NOTAOVERLAYSURFACE:
		return "An overlay component is called for a nonoverlay surface.";
	case DDERR_NOTEXTUREHW:
		return "The operation cannot be carried out because no texture-mapping hardware is present or available.";
	case DDERR_NOTFLIPPABLE:
		return "An attempt was made to flip a surface that cannot be flipped.";
	case DDERR_NOTFOUND:
		return "The requested item was not found.";
	case DDERR_NOTINITIALIZED:
		return "An attempt was made to call an interface method of a DirectDraw object created by CoCreateInstance before the object was initialized.";
	case DDERR_NOTLOADED:
		return "The surface is an optimized surface, but it has not yet been allocated any memory.";
	case DDERR_NOTLOCKED:
		return "An attempt was made to unlock a surface that was not locked.";
	case DDERR_NOTPAGELOCKED:
		return "An attempt was made to page-unlock a surface with no outstanding page locks.";
	case DDERR_NOTPALETTIZED:
		return "The surface being used is not a palette-based surface.";
	case DDERR_NOVSYNCHW:
		return "There is no hardware support for vertical blank–synchronized operations.";
	case DDERR_NOZBUFFERHW:
		return "The operation to create a z-buffer in display memory or to perform a blit, using a z-buffer cannot be carried out because there is no hardware support for z-buffers.";
	case DDERR_NOZOVERLAYHW:
		return "The overlay surfaces cannot be z-layered, based on the z-order because the hardware does not support z-ordering of overlays.";
	case DDERR_OUTOFCAPS:
		return "The hardware needed for the requested operation has already been allocated.";
	case DDERR_OUTOFMEMORY:
		return "DirectDraw does not have enough memory to perform the operation.";
	case DDERR_OUTOFVIDEOMEMORY:
		return "DirectDraw does not have enough display memory to perform the operation.";
	case DDERR_OVERLAPPINGRECTS:
		return "The source and destination rectangles are on the same surface and overlap each other.";
	case DDERR_OVERLAYCANTCLIP:
		return "The hardware does not support clipped overlays.";
	case DDERR_OVERLAYCOLORKEYONLYONEACTIVE:
		return "An attempt was made to have more than one color key active on an overlay.";
	case DDERR_OVERLAYNOTVISIBLE:
		return "The IDirectDrawSurface7::GetOverlayPosition method was called on a hidden overlay.";
	case DDERR_PALETTEBUSY:
		return "Access to this palette is refused because the palette is locked by another thread.";
	case DDERR_PRIMARYSURFACEALREADYEXISTS:
		return "This process has already created a primary surface.";
	case DDERR_REGIONTOOSMALL:
		return "The region passed to the IDirectDrawClipper:GetClipList method is too small.";
	case DDERR_SURFACEALREADYATTACHED:
		return "An attempt was made to attach a surface to another surface to which it is already attached.";
	case DDERR_SURFACEALREADYDEPENDENT:
		return "An attempt was made to make a surface a dependency of another surface on which it is already dependent.";
	case DDERR_SURFACEBUSY:
		return "Access to the surface is refused because the surface is locked by another thread.";
	case DDERR_SURFACEISOBSCURED:
		return "Access to the surface is refused because the surface is obscured.";
	case DDERR_SURFACELOST:
		return "Access to the surface is refused because the surface memory is gone. Call the IDirectDrawSurface7::Restore method on this surface to restore the memory associated with it.";
	case DDERR_SURFACENOTATTACHED:
		return "The requested surface is not attached.";
	case DDERR_TESTFINISHED:
		return "New for DirectX 7.0. When returned by the IDirectDraw7::StartModeTest method, this value means that no test could be initiated because all the resolutions chosen for testing already have refresh rate information in the registry. When returned by IDirectDraw7::EvaluateMode, the value means that DirectDraw has completed a refresh rate test.";
	case DDERR_TOOBIGHEIGHT:
		return "The height requested by DirectDraw is too large.";
	case DDERR_TOOBIGSIZE:
		return "The size requested by DirectDraw is too large. However, the individual height and width are valid sizes.";
	case DDERR_TOOBIGWIDTH:
		return "The width requested by DirectDraw is too large.";
	case DDERR_UNSUPPORTED:
		return "The operation is not supported.";
	case DDERR_UNSUPPORTEDFORMAT:
		return "The pixel format requested is not supported by DirectDraw.";
	case DDERR_UNSUPPORTEDMASK:
		return "The bitmask in the pixel format requested is not supported by DirectDraw.";
	case DDERR_UNSUPPORTEDMODE:
		return "The display is currently in an unsupported mode.";
	case DDERR_VERTICALBLANKINPROGRESS:
		return "A vertical blank is in progress.";
	case DDERR_VIDEONOTACTIVE:
		return "The video port is not active.";
	case DDERR_WASSTILLDRAWING:
		return "The previous blit operation that is transferring information to or from this surface is incomplete.";
	case DDERR_WRONGMODE:
		return "This surface cannot be restored because it was created in a different mode.";
	case DDERR_XALIGN:
		return "The provided rectangle was not horizontally aligned on a required boundary.";

	case D3DERR_BADMAJORVERSION: 
		return "The service that you requested is unavailable in this major version of DirectX. (A major version denotes a primary release, such as DirectX 6.0.)."; 
	case D3DERR_BADMINORVERSION: 
		return "The service that you requested is available in this major version of DirectX, but not in this minor version. Get the latest version of the component run time from Microsoft. (A minor version denotes a secondary release, such as DirectX 6.1.)."; 
	case D3DERR_COLORKEYATTACHED: 
		return "The application attempted to create a texture with a surface that uses a color key for transparency.";
	case D3DERR_CONFLICTINGTEXTUREFILTER: 
		return "The current texture filters cannot be used together.";
	case D3DERR_CONFLICTINGTEXTUREPALETTE: 
		return "The current textures cannot be used simultaneously. This generally occurs when a multitexture device requires that all palettized textures simultaneously enabled also share the same palette.";
	case D3DERR_CONFLICTINGRENDERSTATE: 
		return "The currently set render states cannot be used together.";
	case D3DERR_DEVICEAGGREGATED: 
		return "The IDirect3DDevice7::SetRenderTarget method was called on a device that was retrieved from the render target surface.";
	case D3DERR_EXECUTE_CLIPPED_FAILED: 
		return "The execute buffer could not be clipped during execution.";
	case D3DERR_EXECUTE_CREATE_FAILED: 
		return "The execute buffer could not be created. This typically occurs when no memory is available to allocate the execute buffer.";
	case D3DERR_EXECUTE_DESTROY_FAILED: 
		return "The memory for the execute buffer could not be deallocated.";
	case D3DERR_EXECUTE_FAILED: 
		return "The contents of the execute buffer are invalid and cannot be executed.";
	case D3DERR_EXECUTE_LOCK_FAILED: 
		return "The execute buffer could not be locked.";
	case D3DERR_EXECUTE_LOCKED: 
		return "The operation requested by the application could not be completed because the execute buffer is locked.";
	case D3DERR_EXECUTE_NOT_LOCKED: 
		return "The execute buffer could not be unlocked because it is not currently locked.";
	case D3DERR_EXECUTE_UNLOCK_FAILED: 
		return "The execute buffer could not be unlocked.";
	case D3DERR_INBEGIN: 
		return "The requested operation cannot be completed while scene rendering is taking place. Try again after the scene is completed and the IDirect3DDevice7::EndScene method is called.";
	case D3DERR_INBEGINSTATEBLOCK:
		return "The operation cannot be completed while recording states for a state block. Complete recording by calling the IDirect3DDevice7::EndStateBlock method, and try again.";
	case D3DERR_INITFAILED: 
		return "A rendering device could not be created because the new device could not be initialized.";
	case D3DERR_INVALID_DEVICE: 
		return "The requested device type is not valid.";
	case D3DERR_INVALIDCURRENTVIEWPORT: 
		return "The currently selected viewport is not valid.";
	case D3DERR_INVALIDMATRIX: 
		return "The requested operation could not be completed because the combination of the currently set world, view, and projection matrices is invalid (the determinant of the combined matrix is 0).";
	case D3DERR_INVALIDPALETTE: 
		return "The palette associated with a surface is invalid.";
	case D3DERR_INVALIDPRIMITIVETYPE:
		return "The primitive type specified by the application is invalid.";
	case D3DERR_INVALIDRAMPTEXTURE: 
		return "Ramp mode is being used, and the texture handle in the current material does not match the current texture handle that is set as a render state.";
	case D3DERR_INVALIDSTATEBLOCK: 
		return "The state block handle is invalid.";
	case D3DERR_INVALIDVERTEXFORMAT: 
		return "The combination of flexible vertex format flags specified by the application is not valid.";
	case D3DERR_INVALIDVERTEXTYPE: 
		return "The vertex type specified by the application is invalid.";
	case D3DERR_LIGHT_SET_FAILED: 
		return "The attempt to set lighting parameters for a light object failed.";
	case D3DERR_LIGHTHASVIEWPORT: 
		return "The requested operation failed because the light object is associated with another viewport.";
	case D3DERR_LIGHTNOTINTHISVIEWPORT: 
		return "The requested operation failed because the light object has not been associated with this viewport.";
	case D3DERR_MATERIAL_CREATE_FAILED: 
		return "The material could not be created. This typically occurs when no memory is available to allocate for the material.";
	case D3DERR_MATERIAL_DESTROY_FAILED: 
		return "The memory for the material could not be deallocated.";
	case D3DERR_MATERIAL_GETDATA_FAILED: 
		return "The material parameters could not be retrieved.";
	case D3DERR_MATERIAL_SETDATA_FAILED: 
		return "The material parameters could not be set.";
	case D3DERR_MATRIX_CREATE_FAILED: 
		return "The matrix could not be created. This can occur when no memory is available to allocate for the matrix.";
	case D3DERR_MATRIX_DESTROY_FAILED: 
		return "The memory for the matrix could not be deallocated.";
	case D3DERR_MATRIX_GETDATA_FAILED: 
		return "The matrix data could not be retrieved. This can occur when the matrix was not created by the current device.";
	case D3DERR_MATRIX_SETDATA_FAILED: 
		return "The matrix data could not be set. This can occur when the matrix was not created by the current device.";
	case D3DERR_NOCURRENTVIEWPORT: 
		return "The viewport parameters could not be retrieved because none have been set.";
	case D3DERR_NOTINBEGIN: 
		return "The requested rendering operation could not be completed because scene rendering has not begun. Call IDirect3DDevice7::BeginScene to begin rendering, and try again.";
	case D3DERR_NOTINBEGINSTATEBLOCK: 
		return "The requested operation could not be completed because it is only valid while recording a state block. Call the IDirect3DDevice7::BeginStateBlock method, and try again.";
	case D3DERR_NOVIEWPORTS: 
		return "The requested operation failed because the device currently has no viewports associated with it.";
	case D3DERR_SCENE_BEGIN_FAILED: 
		return "Scene rendering could not begin.";
	case D3DERR_SCENE_END_FAILED: 
		return "Scene rendering could not be completed.";
	case D3DERR_SCENE_IN_SCENE: 
		return "Scene rendering could not begin because a previous scene was not completed by a call to the IDirect3DDevice7::EndScene method.";
	case D3DERR_SCENE_NOT_IN_SCENE: 
		return "Scene rendering could not be completed because a scene was not started by a previous call to the IDirect3DDevice7::BeginScene method.";
	case D3DERR_SETVIEWPORTDATA_FAILED: 
		return "The viewport parameters could not be set.";
	case D3DERR_STENCILBUFFER_NOTPRESENT: 
		return "The requested stencil buffer operation could not be completed because there is no stencil buffer attached to the render target surface.";
	case D3DERR_SURFACENOTINVIDMEM: 
		return "The device could not be created because the render target surface is not located in video memory. (Hardware-accelerated devices require video-memory render target surfaces.)";
	case D3DERR_TEXTURE_BADSIZE: 
		return "The dimensions of a current texture are invalid. This can occur when an application attempts to use a texture that has dimensions that are not a power of 2 with a device that requires them.";
	case D3DERR_TEXTURE_CREATE_FAILED: 
		return "The texture handle for the texture could not be retrieved from the driver.";
	case D3DERR_TEXTURE_DESTROY_FAILED: 
		return "The device was unable to deallocate the texture memory.";
	case D3DERR_TEXTURE_GETSURF_FAILED: 
		return "The DirectDraw surface used to create the texture could not be retrieved.";
	case D3DERR_TEXTURE_LOAD_FAILED: 
		return "The texture could not be loaded.";
	case D3DERR_TEXTURE_LOCK_FAILED: 
		return "The texture could not be locked.";
	case D3DERR_TEXTURE_LOCKED: 
		return "The requested operation could not be completed because the texture surface is currently locked.";
	case D3DERR_TEXTURE_NO_SUPPORT: 
		return "The device does not support texture mapping.";
	case D3DERR_TEXTURE_NOT_LOCKED: 
		return "The requested operation could not be completed because the texture surface is not locked.";
	case D3DERR_TEXTURE_SWAP_FAILED: 
		return "The texture handles could not be swapped.";
	case D3DERR_TEXTURE_UNLOCK_FAILED: 
		return "The texture surface could not be unlocked.";
	case D3DERR_TOOMANYOPERATIONS: 
		return "The application is requesting more texture-filtering operations than the device supports.";
	case D3DERR_TOOMANYPRIMITIVES: 
		return "The device is unable to render the provided number of primitives in a single pass.";
	case D3DERR_UNSUPPORTEDALPHAARG: 
		return "The device does not support one of the specified texture-blending arguments for the alpha channel.";
	case D3DERR_UNSUPPORTEDALPHAOPERATION: 
		return "The device does not support one of the specified texture-blending operations for the alpha channel.";
	case D3DERR_UNSUPPORTEDCOLORARG: 
		return "The device does not support one of the specified texture-blending arguments for color values.";
	case D3DERR_UNSUPPORTEDCOLOROPERATION: 
		return "The device does not support one of the specified texture-blending operations for color values.";
	case D3DERR_UNSUPPORTEDFACTORVALUE: 
		return "The specified texture factor value is not supported by the device.";
	case D3DERR_UNSUPPORTEDTEXTUREFILTER: 
		return "The specified texture filter is not supported by the device.";
	case D3DERR_VBUF_CREATE_FAILED: 
		return "The vertex buffer could not be created. This can happen when there is insufficient memory to allocate a vertex buffer.";
	case D3DERR_VERTEXBUFFERLOCKED: 
		return "The requested operation could not be completed because the vertex buffer is locked.";
	case D3DERR_VERTEXBUFFEROPTIMIZED: 
		return "The requested operation could not be completed because the vertex buffer is optimized. (The contents of optimized vertex buffers are driver-specific and considered private.)";
	case D3DERR_VERTEXBUFFERUNLOCKFAILED: 
		return "The vertex buffer could not be unlocked because the vertex buffer memory was overrun. Be sure that your application does not write beyond the size of the vertex buffer.";
	case D3DERR_VIEWPORTDATANOTSET: 
		return "The requested operation could not be completed because viewport parameters have not yet been set. Set the viewport parameters by calling the IDirect3DDevice7::SetViewport method, and try again.";
	case D3DERR_VIEWPORTHASNODEVICE: 
		return "This value is used only by the IDirect3DDevice3 interface and its predecessors. For the IDirect3DDevice7 interface, this error value is not used. \nThe requested operation could not be completed because the viewport has not yet been associated with a device. Associate the viewport with a rendering device by calling the IDirect3DDevice3::AddViewport method, and try again."; 
	case D3DERR_WRONGTEXTUREFORMAT:
		return "The pixel format of the texture surface is not valid."; 
	case D3DERR_ZBUFF_NEEDS_SYSTEMMEMORY:
		return "The requested operation could not be completed because the specified device requires system-memory depth-buffer surfaces. (Software rendering devices require system-memory depth buffers.)";
	case D3DERR_ZBUFF_NEEDS_VIDEOMEMORY:
		return "The requested operation could not be completed because the specified device requires video-memory depth-buffer surfaces. (Hardware-accelerated devices require video-memory depth buffers.)";
	case D3DERR_ZBUFFER_NOTPRESENT:
		return "The requested operation could not be completed because the render target surface does not have an attached depth buffer.";
	
	default:
		return "unknown";
	}
}
