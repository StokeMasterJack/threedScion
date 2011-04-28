package com.tms.threed.previewPaneAdapter.client;

public enum PanelType {

	Configurator (467, 602),
	Summary (467, 378);

	double height = 0;
	double width = 0;
	
	private PanelType( double height, double width )
	{
		this.height = height;
		this.width = width;
	}

	public double getHeight() {
		return height;
	}

	public double getWidth() {
		return width;
	}
	
}
