/*******************************************************************************
 * Copyright (c) 2000-2011 Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *   
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *    
 * Contributors:
 *               Kamesh Sampath - initial implementation
 *******************************************************************************/

package com.liferay.ide.eclipse.portlet.core.model.internal;

import com.liferay.ide.eclipse.portlet.core.model.ICustomWindowState;
import com.liferay.ide.eclipse.portlet.core.model.IWindowState;

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ImageData;
import org.eclipse.sapphire.modeling.ModelPropertyChangeEvent;
import org.eclipse.sapphire.modeling.ModelPropertyListener;
import org.eclipse.sapphire.services.ImageService;

/**
 * @author <a href="mailto:kamesh.sampath@accenture.com">Kamesh Sampath</a>
 */
public class WindowStateImageService extends ImageService {

	private static final ImageData IMG_DEFAULT = ImageData.readFromClassLoader(
		WindowStateImageService.class, "images/window_states.png" );

	private static final ImageData IMG_MAXIMIZED = ImageData.readFromClassLoader(
		WindowStateImageService.class, "images/maximize.png" );

	private static final ImageData IMG_MINIMIZED = ImageData.readFromClassLoader(
		WindowStateImageService.class, "images/minimize.png" );

	private ModelPropertyListener listener;

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.sapphire.modeling.ModelElementService#init(org.eclipse.sapphire.modeling.IModelElement,
	 * java.lang.String[])
	 */
	@Override
	protected void init() {
		this.listener = new ModelPropertyListener() {

			@Override
			public void handlePropertyChangedEvent( final ModelPropertyChangeEvent event ) {
				broadcast();
			}
		};

		context( IModelElement.class ).addListener( this.listener, IWindowState.PROP_WINDOW_STATE.getName() );
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.sapphire.modeling.ImageService#provide()
	 */
	@Override
	public ImageData provide() {
		String strWindowState = null;
		IModelElement element = context( IModelElement.class );

		if ( element instanceof ICustomWindowState ) {
			ICustomWindowState customWindowState = (ICustomWindowState) element;
			strWindowState = String.valueOf( customWindowState.getWindowState().getContent() );
		}
		else if ( element instanceof IWindowState ) {
			IWindowState windowState = (IWindowState) element;
			strWindowState = windowState.getWindowState().getContent();
		}

		if ( "MAXIMIZED".equalsIgnoreCase( strWindowState ) ) {
			return IMG_MAXIMIZED;
		}
		else if ( "MINIMIZED".equalsIgnoreCase( strWindowState ) ) {
			return IMG_MINIMIZED;
		}

		return IMG_DEFAULT;
	}

	/**
	 * 
	 */
	@Override
	public void dispose() {
		super.dispose();

		context( IModelElement.class ).removeListener( this.listener, IWindowState.PROP_WINDOW_STATE.getName() );
	}

}
