package com;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import java.util.ArrayList; 
import java.awt.event.WindowEvent;
public class AccuracyChart extends ApplicationFrame{
	ArrayList<Double> list;
public AccuracyChart(String title,ArrayList<Double> list){
	super(title);
	this.list = list;
	JFreeChart lineChart = ChartFactory.createLineChart(title,"Technique Name","Accuracy Value",createDataset(),PlotOrientation.VERTICAL,true,true,false);
	ChartPanel chartPanel = new ChartPanel( lineChart );
    chartPanel.setPreferredSize( new java.awt.Dimension( 560 , 367 ) );
    setContentPane( chartPanel );
}
public void windowClosing(WindowEvent we){
	this.setVisible(false);
}
private DefaultCategoryDataset createDataset(){
	DefaultCategoryDataset dataset = new DefaultCategoryDataset( );
    for(int i=0;i<list.size();i++){
		dataset.addValue(list.get(i),"RNN Accuracy","E"+(i+1));
		dataset.addValue(100 - list.get(i),"RNN Loss","E"+(i+1));
	}
    return dataset;
}
}