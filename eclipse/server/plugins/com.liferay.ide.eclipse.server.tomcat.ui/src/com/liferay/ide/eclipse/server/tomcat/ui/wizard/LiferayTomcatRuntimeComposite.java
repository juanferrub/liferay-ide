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
 *******************************************************************************/

package com.liferay.ide.eclipse.server.tomcat.ui.wizard;

import com.liferay.ide.eclipse.server.tomcat.core.LiferayTomcatRuntime;
import com.liferay.ide.eclipse.server.tomcat.core.util.LiferayTomcatUtil;
import com.liferay.ide.eclipse.server.ui.LiferayServerUIPlugin;
import com.liferay.ide.eclipse.ui.util.SWTUtil;

import java.util.ArrayList;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.IVMInstallType;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jst.server.core.IJavaRuntime;
import org.eclipse.jst.server.tomcat.core.internal.TomcatRuntime;
import org.eclipse.jst.server.tomcat.ui.internal.Messages;
import org.eclipse.jst.server.tomcat.ui.internal.TomcatRuntimeComposite;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wst.server.core.IRuntimeWorkingCopy;
import org.eclipse.wst.server.ui.wizard.IWizardHandle;

/**
 * @author Greg Amerson
 */
@SuppressWarnings({ "restriction", "unchecked" })
public class LiferayTomcatRuntimeComposite extends TomcatRuntimeComposite implements ModifyListener {

	public static void setFieldValue(Text field, String value) {
		if (field != null && !field.isDisposed()) {
			field.setText(value != null ? value : "");
		}
	}

	protected Text dirField;

	protected boolean ignoreModifyEvent;

	protected Button jreButton;

	protected Combo jreCombo;

	protected Label jreLabel;

	protected Text nameField;

	public LiferayTomcatRuntimeComposite(Composite parent, IWizardHandle wizard) {
		super(parent, wizard);

		wizard.setTitle("Liferay Tomcat Runtime");
		wizard.setDescription("Specify the installation directory of the Tomcat configured with Liferay.");
		wizard.setImageDescriptor(LiferayServerUIPlugin.getImageDescriptor(LiferayServerUIPlugin.IMG_WIZ_RUNTIME));
	}

	public void modifyText(ModifyEvent e) {
		if (ignoreModifyEvent) {
			ignoreModifyEvent = false;

			return;
		}

		if (e.getSource().equals(dirField)) {
			getRuntime().setLocation(new Path(dirField.getText()));
		}
		else if (e.getSource().equals(nameField)) {
			getRuntime().setName(nameField.getText());
		}

		validate();

		IStatus status = getRuntime().validate(null);

		if (!status.isOK() && e.getSource().equals(dirField)) {
			// check to see if we need to modify from a liferay folder down to
			// embedded tomcat
			IPath currentLocation = getRuntime().getLocation();

			IPath modifiedLocation = LiferayTomcatUtil.modifyLocationForBundle(currentLocation);

			if (modifiedLocation != null) {
				getRuntime().setLocation(modifiedLocation);

				status = getRuntime().validate(null);

				if (status.isOK()) {
					ignoreModifyEvent = true;

					dirField.setText(modifiedLocation.toOSString());

					validate();
				}
			}
		}

		enableJREControls(true);

		if (getTomcatRuntime().getVMInstall() != null) {
			// check to see if selected VM is in same path as new server
			// location
			IPath vmLoc = new Path(getTomcatRuntime().getVMInstall().getInstallLocation().getPath());

			IPath runtimeLoc = getRuntime().getLocation();

			if (!runtimeLoc.isPrefixOf(vmLoc)) {
				// we have a jre that is outside the runtime location, need to
				// look for new bundled JRE
				LiferayTomcatRuntime runtime = (LiferayTomcatRuntime) getTomcatRuntime();

				IVMInstall newVM = runtime.findPortalBundledJRE(true);

				if (newVM != null) {
					runtime.setVMInstall(newVM);
				}
			}

			updateJREs();
		}
	}

	protected Button createButton(String text, int style) {
		Button button = new Button(this, style);

		button.setText(text);

		GridDataFactory.generate(button, 2, 1);

		return button;
	}

	@Override
	protected void createControl() {
		setLayout(createLayout());

		setLayoutData(new GridData(GridData.FILL_BOTH));

		createFields();

		// initially disabled until user selects an installation directory
		enableJREControls(false);

		init();

		validate();

		Dialog.applyDialogFont(this);
	}

	protected void createFields() {
		nameField = createTextField("Name");
		nameField.addModifyListener(this);

		dirField = createTextField("Liferay Tomcat directory");
		dirField.addModifyListener(this);

		SWTUtil.createButton(this, "Browse...").addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				DirectoryDialog dd = new DirectoryDialog(LiferayTomcatRuntimeComposite.this.getShell());

				dd.setMessage("Select Liferay Tomcat directory");
				dd.setFilterPath(dirField.getText());

				String selectedDir = dd.open();

				if (selectedDir != null) {
					dirField.setText(selectedDir);
				}
			}
		});

		installLabel = new Label(this, SWT.RIGHT);

		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalIndent = 10;

		installLabel.setLayoutData(data);

		install = SWTUtil.createButton(this, Messages.install);
		install.setVisible(false);

		jreLabel = createLabel("Select runtime JRE");

		jreCombo = new Combo(this, SWT.DROP_DOWN | SWT.READ_ONLY);
		jreCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		jreCombo.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				int sel = jreCombo.getSelectionIndex();

				IVMInstall vmInstall = null;

				if (sel > 0) {
					vmInstall = (IVMInstall) installedJREs.get(sel - 1);
				}

				getTomcatRuntime().setVMInstall(vmInstall);

				validate();
			}
		});

		jreButton = SWTUtil.createButton(this, "Installed JREs...");
		jreButton.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				if (SWTUtil.showPreferencePage("org.eclipse.jdt.debug.ui.preferences.VMPreferencePage", getShell())) {
					updateJREs();
					validate();
				}
			}

		});

		createLink("For developing Ext plugins with this runtime, <a>additional configuration is available.</a>").addSelectionListener(
			new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					if (wizard instanceof IWizardPage) {
						IWizard parentWizard = ((IWizardPage) wizard).getWizard();
						parentWizard.getContainer().showPage(((IWizardPage) wizard).getNextPage());
					}
				}

			});
	}

	protected Label createLabel(String text) {
		Label label = new Label(this, SWT.NONE);
		label.setText(text);

		GridDataFactory.generate(label, 2, 1);

		return label;
	}

	protected Layout createLayout() {
		GridLayout layout = new GridLayout(2, false);
		return layout;
	}

	protected Link createLink(String linkText) {
		Link link = new Link(this, SWT.NONE);
		link.setText(linkText);

		GridDataFactory.generate(link, 2, 1);

		return link;
	}

	protected void createSpacer() {
		new Label(this, SWT.NONE);
	}

	protected Text createTextField(String labelText) {
		createLabel(labelText);

		Text text = new Text(this, SWT.BORDER);
		text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		return text;
	}

	protected void enableJREControls(boolean enabled) {
		jreLabel.setEnabled(enabled);
		jreCombo.setEnabled(enabled);
		jreButton.setEnabled(enabled);
	}

	protected IJavaRuntime getJavaRuntime() {
		return (IJavaRuntime) this.runtime;
	}

	protected IRuntimeWorkingCopy getRuntime() {
		return this.runtimeWC;
	}

	protected TomcatRuntime getTomcatRuntime() {
		return (TomcatRuntime) this.runtime;
	}

	@Override
	protected void init() {
		if ((nameField == null) || dirField == null || getRuntime() == null) {
			return;
		}

		setFieldValue(nameField, getRuntime().getName());
		setFieldValue(dirField, getRuntime().getLocation() != null ? getRuntime().getLocation().toOSString() : "");
	}

	protected void updateJREs() {
		IVMInstall currentVM = getJavaRuntime().getVMInstall();

		int currentJREIndex = -1;

		// get all installed JVMs
		installedJREs = new ArrayList<IVMInstall>();

		IVMInstallType[] vmInstallTypes = JavaRuntime.getVMInstallTypes();

		int size = vmInstallTypes.length;

		for (int i = 0; i < size; i++) {
			IVMInstall[] vmInstalls = vmInstallTypes[i].getVMInstalls();

			int size2 = vmInstalls.length;

			for (int j = 0; j < size2; j++) {
				installedJREs.add(vmInstalls[j]);
			}
		}

		// get names
		size = installedJREs.size();

		jreNames = new String[size + 1];
		jreNames[0] = "<Default Workbench JRE>";

		for (int i = 0; i < size; i++) {
			IVMInstall vmInstall = (IVMInstall) installedJREs.get(i);

			jreNames[i + 1] = vmInstall.getName();

			if (vmInstall.equals(currentVM)) {
				currentJREIndex = i + 1;
			}
		}

		if (jreCombo != null) {
			jreCombo.setItems(jreNames);
			jreCombo.select(currentJREIndex);
		}
	}

}
