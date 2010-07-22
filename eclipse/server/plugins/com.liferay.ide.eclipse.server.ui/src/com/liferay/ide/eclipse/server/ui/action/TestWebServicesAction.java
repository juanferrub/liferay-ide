/*******************************************************************************
 * Copyright (c) 2000-2010 Liferay, Inc. All rights reserved.
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
 *******************************************************************************/

package com.liferay.ide.eclipse.server.ui.action;

import com.liferay.ide.eclipse.server.core.IPortalServer;
import com.liferay.ide.eclipse.server.util.PortalServicesHelper;
import com.liferay.ide.eclipse.ui.dialog.StringsFilteredDialog;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.window.Window;
import org.eclipse.wst.ws.internal.explorer.LaunchOption;
import org.eclipse.wst.ws.internal.explorer.LaunchOptions;
import org.eclipse.wst.ws.internal.explorer.WSExplorerLauncherCommand;
import org.eclipse.wst.ws.internal.explorer.plugin.ExplorerPlugin;
import org.eclipse.wst.ws.internal.monitor.GetMonitorCommand;
import org.eclipse.wst.ws.internal.parser.wsil.WebServicesParser;

/**
 * @author Greg Amerson
 */
public class TestWebServicesAction extends AbstractServerRunningAction {

	public TestWebServicesAction() {
		super();
	}

	public void run(IAction action) {
		if (selectedServer == null && selectedModule == null) {
			return; // can't do anything if server has not been selected
		}

		URL webServicesListURL = null;
		if (selectedServer != null) {
			IPortalServer portalServer = (IPortalServer) selectedServer.getAdapter(IPortalServer.class);
	
			webServicesListURL = portalServer.getWebServicesListURL();		
		}
		else if (selectedModule != null) {
			selectedModule.getModule()[0].getProject();
			IPortalServer portalServer = (IPortalServer) selectedModule.getServer().getAdapter(IPortalServer.class);
			try {
				webServicesListURL = new URL(portalServer.getPortalHomeUrl(), selectedModule.getModule()[0].getName() + "/axis");
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}
		
		PortalServicesHelper helper = new PortalServicesHelper(webServicesListURL);
		String[] names = helper.getWebServiceNames();
		StringsFilteredDialog dialog = new StringsFilteredDialog(getActiveShell());
		dialog.setTitle("Web Service selection");
		dialog.setMessage("Please select a web service to test:");
		dialog.setInput(names);
		int retval = dialog.open();
		
		if (retval == Window.OK) {
			String serviceName = dialog.getFirstResult().toString();
			
			String url = helper.getWebServiceWSDLURLByName(serviceName);
			
			String stateLocation = ExplorerPlugin.getInstance().getPluginStateLocation();
			String defaultFavoritesLocation = ExplorerPlugin.getInstance().getDefaultFavoritesLocation();
		  	WSExplorerLauncherCommand command = new WSExplorerLauncherCommand();
		    command.setForceLaunchOutsideIDE(false);
		    Vector launchOptions = new Vector();
		    addLaunchOptions(launchOptions, url, stateLocation, defaultFavoritesLocation);        
		    command.setLaunchOptions((LaunchOption[])launchOptions.toArray(new LaunchOption[0]));
		    command.execute();
		}
	}
	
	protected void addLaunchOptions(Vector launchOptions, String wsdlURL, String stateLocation, String defaultFavoritesLocation)
	  {
		  GetMonitorCommand getMonitorCmd = new GetMonitorCommand();
	      getMonitorCmd.setMonitorService(true);
	      getMonitorCmd.setCreate(false);
	      getMonitorCmd.setWebServicesParser(new WebServicesParser());
	      getMonitorCmd.setWsdlURI(wsdlURL);
	      getMonitorCmd.execute(null, null);
	      List endpoints = getMonitorCmd.getEndpoints();
	      for (Iterator endpointsIt = endpoints.iterator(); endpointsIt.hasNext();)
	      {
	    	  launchOptions.add(new LaunchOption(LaunchOptions.WEB_SERVICE_ENDPOINT, (String)endpointsIt.next()));
	      }
	      launchOptions.add(new LaunchOption(LaunchOptions.WSDL_URL, wsdlURL));
		  launchOptions.add(new LaunchOption(LaunchOptions.STATE_LOCATION,stateLocation));
		  launchOptions.add(new LaunchOption(LaunchOptions.DEFAULT_FAVORITES_LOCATION,defaultFavoritesLocation));
	  }

}