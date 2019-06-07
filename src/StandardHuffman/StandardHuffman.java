package StandardHuffman;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;

public class StandardHuffman
{
	JFrame frame1 = new JFrame();
	JFrame frame2 = new JFrame();
	JFrame frame3 = new JFrame();
	
	static class Huff
	{
		float Prob;
		String Symbol;
		Huff Right;
		Huff Left;
	}
	
	private static ArrayList<String> CompSymbol = new ArrayList<>();    // Store Compression Symbol
	private static ArrayList<String> HuffCode = new ArrayList<>();      // Store Compression HuffmanCode
	private static int LineLength = 0;            // get Length of original data
	private static int CompSize = 0;              // get Compressed file size
	private static String CompCode = "";          // get Compressed Code
	
	public static void main(String[] args)
	{
		ArrayList<Float> Probability = new ArrayList<>();
		ArrayList<String> Symbol = new ArrayList<>();
		try
		{
			File file = new File("Symbols.txt");
			File file1 = new File("Code.txt");
			File file2 = new File("Table.txt");
			File file3 = new File("DeComp.txt");
			if(!file.exists())
				file.createNewFile();
			if(!file1.exists())
				file1.createNewFile();
			if(!file2.exists())
				file2.createNewFile();
			if(!file3.exists())
				file3.createNewFile();
			
			BufferedReader br = new BufferedReader(new FileReader("Symbols.txt"));
			BufferedWriter cbw = new BufferedWriter(new FileWriter("Code.txt"));
			BufferedWriter tbw = new BufferedWriter(new FileWriter("Table.txt"));
			BufferedWriter debw = new BufferedWriter(new FileWriter("DeComp.txt"));
			BufferedReader cbr = new BufferedReader(new FileReader("Code.txt"));
			BufferedReader tbr = new BufferedReader(new FileReader("Table.txt"));
			
			///Compression
			
			String line;
			while((line = br.readLine()) != null)
			{
				System.out.println("Symbols : " + line);
				LineLength += line.length();
				
				Symbol.add(line.charAt(0) + "");
				for(int i = 1; i < line.length(); i++)
				{
					if(!(Symbol.contains((line.charAt(i)) + "")))
					{
						Symbol.add(line.charAt(i) + "");
					}
				}
				
				int n = 0;
				int counter = 0;
				int it = 0;
				while(n != Symbol.size())
				{
					if((line.charAt(it) + "").equals(Symbol.get(n)))
					{
						counter++;
					}
					if(it == line.length() - 1)
					{
						Probability.add((float) counter / line.length());
						n++;
						counter = 0;
						it = 0;
					}
					it++;
				}
				
				ArrayList<Huff> Standard = new ArrayList<>();
				for(int i = 0; i < Probability.size(); i++)
				{
					Huff node = new Huff();
					node.Symbol = Symbol.get(i);
					node.Prob = Probability.get(i);
					node.Right = null;
					node.Left = null;
					Standard.add(node);
				}
				
				Huff root = null;
				for(int i = 1; i < Standard.size(); i++)
				{
					Sort(Standard);
					Huff P1 = Standard.get(i - 1);
					Huff P2 = Standard.get(i);
					
					Huff Obj = new Huff();
					Obj.Prob = P1.Prob + P2.Prob;
					Obj.Symbol = P1.Symbol + P2.Symbol;
					Obj.Right = P2;
					Obj.Left = P1;
					
					root = Obj;
					Standard.set(i, Obj);
				}
				
				Get_CompData(root, "");
				for(int i = 0; i < LineLength; i++)
				{
					if(CompSymbol.contains(line.charAt(i) + ""))
					{
						int index = CompSymbol.indexOf(line.charAt(i) + "");
						CompCode += HuffCode.get(index);
					}
				}
			}
			
			System.out.println("\nUnCompressed File Size : " + LineLength * 3);
			System.out.println("Compressed File Size : " + CompSize);
			System.out.println("Entropy Value : " + Entropy(Probability) + " Bits/Symbol");
			System.out.println("Optimal File Size : " + Entropy(Probability) * LineLength);
			
			cbw.write(CompCode);
			tbw.write("Symbol" + "\t\t" + "HuffCode");
			
			for(int i = 0; i < CompSymbol.size(); i++)
			{
				tbw.newLine();
				tbw.write(CompSymbol.get(i) + "\t\t" + HuffCode.get(i));
			}
			
			
			cbw.close();
			tbw.close();
			
			
			///DeCompression
			
			String DeComp = "";
			String SymbolCode = CompCode.charAt(0) + "";
			
			String RCode = cbr.readLine();
			ArrayList<String> RHuff = new ArrayList<>();
			ArrayList<String> RSymbol = new ArrayList<>();
			
			String Read = tbr.readLine();
			String [] S;
			while((Read = tbr.readLine()) != null)
			{
				
				S = Read.split("\t\t");
				RSymbol.add(S[0]);
				RHuff.add(S[1]);
			}
			
			for(int i = 1; i < RCode.length(); i++)
			{
				if(RHuff.contains(SymbolCode))
				{
					int index = RHuff.indexOf(SymbolCode);
					DeComp += RSymbol.get(index);
					SymbolCode = RCode.charAt(i) + "";
				}
				else
				{
					SymbolCode += RCode.charAt(i) + "";
				}
				if(i == RCode.length() - 1)
				{
					if(RHuff.contains(SymbolCode))
					{
						int index = RHuff.indexOf(SymbolCode);
						DeComp += RSymbol.get(index);
						SymbolCode = RCode.charAt(i) + "";
					}
					else
					{
						SymbolCode += RCode.charAt(i) + "";
					}
					break;
				}
			}
			debw.write(DeComp);
			System.out.println("\nDecompression Data : ");
			System.out.println(DeComp);
			
			br.close();
			tbr.close();
			cbr.close();
			debw.close();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		
		EventQueue.invokeLater(new Runnable()
		{
			public void run()
			{
				try
				{
					StandardHuffman window = new StandardHuffman();
					window.frame1.setVisible(true);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		});
	}
	
	public static void Sort(ArrayList<Huff> Standard)
	{
		Huff Temp = new Huff();
		for(int i = 0; i < Standard.size(); i++)
		{
			for(int j = i + 1; j < Standard.size(); j++)
			{
				if(Standard.get(j).Prob < Standard.get(i).Prob)
				{
					Temp = Standard.get(j);
					Standard.set(j, Standard.get(i));
					Standard.set(i, Temp);
				}
			}
		}
	}
	
	public static void Get_CompData(Huff root, String Code)
	{
		if(root.Left == null && root.Right == null)
		{
			CompSize += Code.length() * (root.Prob * LineLength);
			CompSymbol.add(root.Symbol);
			HuffCode.add(Code);
			return;
		}
		Get_CompData(root.Right, Code + "1");
		Get_CompData(root.Left, Code + "0");
	}
	
	public static double Entropy(ArrayList<Float> Prob)
	{
		// prob * log(1/prob)
		// log2 x = log10 x / log10 2
		double Result = 0;
		for(int i = 0; i < Prob.size(); i++)
		{
			Result += (double) Prob.get(i) * (Math.log((double) 1 / Prob.get(i)) / Math.log((double) 2));
		}
		return Result;
	}
	
	
	
	public StandardHuffman()
	{
		initialize_Form1();
		initialize_Form2();
		initialize_Form3();
	}
	
	private void initialize_Form1()
	{
		frame1 = new JFrame();
		frame1.setBounds(100, 100, 600, 600);
		frame1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame1.getContentPane().setLayout(null);
		
		JLabel lblWel = new JLabel("Welcome ^^");
		lblWel.setBounds(10, 10, 100, 50);
		frame1.getContentPane().add(lblWel);
		
		JLabel lblName = new JLabel("Click on Your Choice.");
		lblName.setBounds(65, 100, 200, 50);
		frame1.getContentPane().add(lblName);
		
		JButton btnSubmit1 = new JButton("Compression");
		btnSubmit1.setBackground(Color.WHITE);
		btnSubmit1.setForeground(Color.RED);
		btnSubmit1.setBounds(65, 170, 150, 40);
		frame1.getContentPane().add(btnSubmit1);
		
		JButton btnSubmit2 = new JButton("De-compression");
		btnSubmit2.setBackground(Color.WHITE);
		btnSubmit2.setForeground(Color.RED);
		btnSubmit2.setBounds(350, 170, 150, 40);
		frame1.getContentPane().add(btnSubmit2);
		
		btnSubmit1.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				frame1.setVisible(false);
				frame2.setVisible(true);
			}
		});
		btnSubmit2.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				frame1.setVisible(false);
				frame3.setVisible(true);
			}
		});
	}
	
	private void initialize_Form2()
	{
		frame2 = new JFrame();
		frame2.setBounds(100, 100, 1000, 600);
		frame2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame2.getContentPane().setLayout(null);
		
		
		JLabel lblWel = new JLabel("Compression Selected");
		lblWel.setBounds(90, 1, 180, 20);
		frame2.getContentPane().add(lblWel);
		
		JLabel lblDic = new JLabel("Get Your Data : ");
		lblDic.setBounds(5, 50, 100, 20);
		frame2.getContentPane().add(lblDic);
		
		JButton btnSubmit = new JButton("Get Data");
		btnSubmit.setBackground(Color.WHITE);
		btnSubmit.setForeground(Color.BLUE);
		btnSubmit.setBounds(180, 50, 100, 30);
		frame2.getContentPane().add(btnSubmit);
		
		JTextArea textArea_1 = new JTextArea();
		textArea_1.setBounds(180, 100, 700, 80);
		frame2.getContentPane().add(textArea_1);
		textArea_1.setColumns(20);
		
		JButton btnSubmit1 = new JButton("Compress");
		btnSubmit1.setBackground(Color.WHITE);
		btnSubmit1.setForeground(Color.BLUE);
		btnSubmit1.setBounds(180, 220, 100, 30);
		frame2.getContentPane().add(btnSubmit1);
		
		JLabel lblTags = new JLabel("Dictionary:");
		lblTags.setBounds(5, 250, 100, 60);
		frame2.getContentPane().add(lblTags);
		
		JTextArea textArea_2 = new JTextArea();
		textArea_2.setBounds(180, 270, 700, 250);
		frame2.getContentPane().add(textArea_2);
		textArea_2.setColumns(20);
		
		btnSubmit.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				JFileChooser chooser = new JFileChooser();
				chooser.showOpenDialog(null);
				File f = chooser.getSelectedFile();
				String FileName = f.getAbsolutePath();
				try
				{
					BufferedReader br = new BufferedReader(new FileReader("Symbols.txt"));
					textArea_1.read(br , null);
					br.close();
					textArea_1.requestFocus();
				}
				catch (Exception e1)
				{
					JOptionPane.showMessageDialog(null , e);
				}
			}
		});
		btnSubmit1.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					BufferedReader br = new BufferedReader(new FileReader("Code.txt"));
					String Line = br.readLine();
					textArea_2.append(Line);
					br.close();
					textArea_2.requestFocus();
				}
				catch (Exception e1)
				{
					JOptionPane.showMessageDialog(null , e1);
					e1.printStackTrace();
				}
			}
		});
		
		JButton btnSubmit3 = new JButton("UP");
		btnSubmit3.setBackground(Color.WHITE);
		btnSubmit3.setForeground(Color.BLACK);
		btnSubmit3.setBounds(1, 1, 70, 20);
		frame2.getContentPane().add(btnSubmit3);
		
		
		btnSubmit3.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				frame2.setVisible(false);
				frame1.setVisible(true);
			}
		});
		
	}
	
	private void initialize_Form3()
	{
		frame3 = new JFrame();
		frame3.setBounds(100, 100, 1000, 600);
		frame3.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame3.getContentPane().setLayout(null);
		
		JLabel lblWel = new JLabel("De-compression Selected");
		lblWel.setBounds(90, 1, 150, 20);
		frame3.getContentPane().add(lblWel);
		
		JLabel lblget = new JLabel("Get your Dic : ");
		lblget.setBounds(1, 50, 150, 20);
		frame3.getContentPane().add(lblget);
		
		JButton btnSubmit = new JButton("Get Dic");
		btnSubmit.setBackground(Color.WHITE);
		btnSubmit.setForeground(Color.BLUE);
		btnSubmit.setBounds(200, 50, 150, 30);
		frame3.getContentPane().add(btnSubmit);
		
		JTextArea textArea_1 = new JTextArea();
		textArea_1.setBounds(200, 100, 700, 150);
		frame3.getContentPane().add(textArea_1);
		textArea_1.setColumns(20);
		
		JLabel lblDecomp = new JLabel("De-compress For previous Dic:");
		lblDecomp.setBounds(1, 290, 250, 50);
		frame3.getContentPane().add(lblDecomp);
		
		JButton btnSubmit1 = new JButton("Decompress");
		btnSubmit1.setBackground(Color.WHITE);
		btnSubmit1.setForeground(Color.BLUE);
		btnSubmit1.setBounds(200, 300, 150, 30);
		frame3.getContentPane().add(btnSubmit1);
		
		JTextArea textArea_2 = new JTextArea();
		textArea_2.setBounds(200, 350, 700, 120);
		frame3.getContentPane().add(textArea_2);
		textArea_2.setColumns(20);
		
		btnSubmit.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				JFileChooser chooser = new JFileChooser();
				chooser.showOpenDialog(null);
				File f = chooser.getSelectedFile();
				String FileName = f.getAbsolutePath();
				try
				{
					BufferedReader br = new BufferedReader(new FileReader("Code.txt"));
					textArea_1.read(br , null);
					br.close();
					textArea_1.requestFocus();
				}
				catch (Exception e1)
				{
					JOptionPane.showMessageDialog(null , e);
				}
			}
		});
		
		btnSubmit1.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				String DeComp = "";
				try
				{
					BufferedReader br = new BufferedReader(new FileReader("DeComp.txt"));
					DeComp = br.readLine();
				}
				catch(IOException e1)
				{
					e1.printStackTrace();
				}
				textArea_2.append(DeComp);
			}
		});
		
		
		JButton btnSubmit3 = new JButton("UP");
		btnSubmit3.setBackground(Color.WHITE);
		btnSubmit3.setForeground(Color.BLACK);
		btnSubmit3.setBounds(1, 1, 70, 20);
		frame3.getContentPane().add(btnSubmit3);
		
		btnSubmit3.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				frame3.setVisible(false);
				frame1.setVisible(true);
			}
		});
	}
}
