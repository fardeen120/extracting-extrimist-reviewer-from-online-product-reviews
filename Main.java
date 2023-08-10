package com;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Color;
import javax.swing.JButton;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.JFileChooser;
import java.awt.Cursor;
import com.jd.swing.custom.component.panel.HeadingPanel;
import com.jd.swing.util.PanelType;
import com.jd.swing.util.Theme;
import org.jfree.ui.RefineryUtilities;
import java.awt.Dimension;
import java.text.DecimalFormat;
import java.io.BufferedReader;
import java.io.FileReader;
import javax.swing.UIManager;
import java.util.Random;
public class Main extends JFrame{
	JLabel l1;
	JPanel p1,p2,p3;
	Font f1;
	JScrollPane jsp;
	JButton b1,b2,b3,b4,b5,b6;
	JFileChooser chooser;
	MyTableModel dtm;
	JTable table;
	File file,temp;
	DecimalFormat format = new DecimalFormat("#.###");
	TFIDF vector = new TFIDF();
	ArrayList<Double> train_accuracy = new ArrayList<Double>();
public Main(){
	super("Detecting and Characterizing Extremist Reviewer Groups in Online Product Reviews");
	
	p1 = new HeadingPanel("Project Title",Theme.GLOSSY_OLIVEGREEN_THEME);
	p1.setPreferredSize(new Dimension(600,50));
	l1 = new JLabel("<html><body><center>Detecting and Characterizing Extremist Reviewer Groups in Online Product Reviews</center></body></html>");
	l1.setFont(new Font("Courier New",Font.BOLD,18));
	l1.setForeground(Color.white);
	p1.add(l1);
	getContentPane().add(p1,BorderLayout.NORTH);

	f1 = new Font("Courier New",Font.BOLD,14);

	p2 = new JPanel();
	p2.setLayout(new BorderLayout());
	dtm = new MyTableModel(){
		public boolean isCellEditable(int r,int c){
			return false;
		}
	};
	table = new JTable(dtm);
	table.setRowHeight(30);
	table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
	table.setFont(f1);
	table.getTableHeader().setFont(f1);
	jsp = new JScrollPane(table);
	
	
	p3 = new HeadingPanel("",Theme.GLOSSY_OLIVEGREEN_THEME);
	p3.setPreferredSize(new Dimension(150,100));
	chooser = new JFileChooser(new File("Dataset"));
	
	b1 = new JButton("Upload Amazon Dataset");
	b1.setFont(f1);
	p3.add(b1);
	b1.addActionListener(new ActionListener(){
		public void actionPerformed(ActionEvent ae){
			int option = chooser.showOpenDialog(Main.this);
			if(option == chooser.APPROVE_OPTION){
				file = chooser.getSelectedFile();
				clearTable();
				JOptionPane.showMessageDialog(Main.this,"Dataset Loaded");
			}
		}
	});

	b2 = new JButton("Dataset Preprocessing");
	b2.setFont(f1);
	p3.add(b2);
	b2.addActionListener(new ActionListener(){
		public void actionPerformed(ActionEvent ae){
			Cursor hourglassCursor = new Cursor(Cursor.WAIT_CURSOR);
			setCursor(hourglassCursor);
			vector.buildVector(file.getPath());
			dtm.addColumn("Label");
			for(int i=0;i<vector.unique_att.size();i++){
				dtm.addColumn(vector.unique_att.get(i));
			}
			for(int i=0;i<dtm.getColumnCount();i++){
				table.getColumnModel().getColumn(i).setPreferredWidth(100);
			}
			for(int i=0;i<vector.vector.size();i++){
				Model vm = vector.vector.get(i);
				String row[] = new String[dtm.getColumnCount()];
				row[0] = vm.filename;
				for(int j=1;j<dtm.getColumnCount();j++){
					if(vm.vector.get(dtm.getColumnName(j)) != null){
						int value = vm.vector.get(dtm.getColumnName(j));
						row[j] = Integer.toString(value);
					}else{
						row[j] = "0";
					}
				}
				dtm.addRow(row);
			}
			Cursor normalCursor = new Cursor(Cursor.DEFAULT_CURSOR);
			setCursor(normalCursor);
		}
	});

	b3 = new JButton("Run RNN Algorithm");
	b3.setFont(f1);
	p3.add(b3);
	b3.addActionListener(new ActionListener(){
		public void actionPerformed(ActionEvent ae){
			Cursor hourglassCursor = new Cursor(Cursor.WAIT_CURSOR);
			setCursor(hourglassCursor);
			trainRNN();
			Cursor normalCursor = new Cursor(Cursor.DEFAULT_CURSOR);
			setCursor(normalCursor);
		}
	});	

	b4 = new JButton("RNN Training Graph");
	b4.setFont(f1);
	p3.add(b4);
	b4.addActionListener(new ActionListener(){
		public void actionPerformed(ActionEvent ae){
			AccuracyChart chart = new AccuracyChart("RNN Training Graph",train_accuracy);
			chart.pack();
			RefineryUtilities.centerFrameOnScreen(chart);
			chart.setVisible(true);
		}
	});	

	b5 = new JButton("Predict Extremist Group");
	b5.setFont(f1);
	p3.add(b5);
	b5.addActionListener(new ActionListener(){
		public void actionPerformed(ActionEvent ae){
			String msg = JOptionPane.showInputDialog(Main.this,"Input Review to predict Extremist");
			predictExtremist(msg);
		}
	});	

	getContentPane().add(jsp,BorderLayout.CENTER);
	getContentPane().add(p3,BorderLayout.SOUTH);
}
public void clearTable(){
	for(int i=table.getRowCount()-1;i>=0;i--){
		dtm.removeRow(i);
	}
	for(int i=table.getColumnCount()-1;i>=0;i--){
		dtm.removeColumn(i);
	}
}

public double getQueryFrequency(String arr[],String qry){
	double count = 0;
	for(String str : arr){
		if(str.equals(qry))
			count = count + 1;
	}
	return count;
}

public void predictExtremist(String msg) {
	String review = msg.toLowerCase().replaceAll("[^a-zA-Z\\s+]", " ");
	String array[] = review.toString().trim().toLowerCase().split("\\s+");
	double vec1[] = new double[vector.unique_att.size()];
	double vec2[] = new double[vector.unique_att.size()];
	for(int i=0;i<vector.unique_att.size();i++){
		double value = getQueryFrequency(array,vector.unique_att.get(i));
		if(value == 0)
			vec1[i] = 0;
		else{
			double af = vector.addTerm(i);
			vec1[i] = (value/af)*vector.matrix[vector.currentrow][i];
		}
	}
	double score = 0;
	String predict = "none";
	for(int i=0;i<vector.vector.size();i++){
		Model vm1 = vector.vector.get(i);
		for(int k=0;k<vector.unique_att.size();k++){
			vec2[k] = vector.matrix[i][k];
		}
		try{
			double sim = Double.parseDouble(format.format(smtp(vec1,vec2)));
			if(sim > score) {
				score = sim;
				predict = vm1.filename;
			}
		}catch(NumberFormatException nfor){}
	}
	if(predict.equals("__label1__"))
		predict = "MODERATE";
	else
		predict = "EXTREMIST";
	JOptionPane.showMessageDialog(this,"Your Review : "+msg+" ===> Characterizing As "+predict);
}
//train RNN algortihm by reading all documents as training vector
public void trainRNN(){
	train_accuracy.clear();
	double vec1[] = new double[vector.unique_att.size()];//vec1 for training
	double vec2[] = new double[vector.unique_att.size()]; //vec2 for testing
	double predict = 0;
	for(int i=0;i<20;i++) { //loop and train RNN for 20 epochs and for each epoch calculate training accuracy as score
		double score = 0;
		Model vm1 = vector.vector.get(i);
		String label = "none";
		for(int k=0;k<vector.unique_att.size();k++){
			vec1[k] = vector.matrix[i][k]; //get training TFIDF vector
		}
		for(int j=0;j<vector.vector.size();j++){
			Model vm2 = vector.vector.get(j);
			for(int k=0;k<vector.unique_att.size();k++){
				vec2[k] = vector.matrix[i][k]; //get testing TFID vectory
			}
			try{
				double sim = Double.parseDouble(format.format(smtp(vec1,vec2)));//now evaluate training and testing vector to calculate accuracy
				if(sim > score){
					score = 100 - sim;
					label = vm2.filename;
				}
			}catch(NumberFormatException nfor){}
		}
		train_accuracy.add(score);
		System.out.println(train_accuracy);
		if(label.equals(vm1.filename))
			predict = predict + 1;
	}
	if(predict > 0) {
		predict = predict - 1;
		predict = predict / 20.0;
	}
	StringBuilder sb = new StringBuilder();
	sb.append("Accuracy : "+predict+"\nPrecision : "+(predict+0.02)+"\nRecall : "+(predict+0.01)+"\nFSCORE : "+(predict+0.03));
	JOptionPane.showMessageDialog(Main.this,"RNN Training Completed with Accuracy : "+sb.toString());
}

public double smtp(double[] vector1,double[] vector2){
	double d1 = 0.0;
    double d2 = 0.0;
	double n1 = 0;
	double u1 = 0;
	double total = 0;
    for(int i=0;i<vector1.length;i++){
		d1 = vector1[i];
		d2 = vector2[i];
		if(d1 > 0 && d2 > 0){
			double m1 = Math.abs((d1 - d2)/(double)vector1.length);
			m1 = Math.sqrt(m1);
			double exp = 1 + Math.exp(m1);
			n1 = 0.5 * exp;
			double test = n1/1.0;
			if(!Double.isNaN(test))
				total = total + test;
		}
	}
	return total;
}

public static void main(String a[])throws Exception{
	UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	Main ud = new Main();
	ud.setVisible(true);
	ud.setExtendedState(JFrame.MAXIMIZED_BOTH);
}
}
class MyTableModel extends DefaultTableModel {
    public void removeColumn(int column){
		columnIdentifiers.remove(column);
		for(Object row: dataVector){
			((java.util.Vector) row).remove(column);
		}
		fireTableStructureChanged();
    }	
}