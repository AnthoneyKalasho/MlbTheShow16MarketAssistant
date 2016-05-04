import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.Date;
import java.text.DateFormat;
import java.text.Format;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import javax.swing.text.AbstractDocument;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.RowFilter.ComparisonType;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;


public class Main {
	private static Hashtable<String, String> teamTable = new Hashtable<String, String>();
	private static int i, blankPg, firstRun, zeroResults,rankMinVal,rankMaxVal,buyMinVal,buyMaxVal,sellMinVal,sellMaxVal,errorExists;
	private static String sourceCode, perDifStr;
	private static ArrayList<String> allMatches = new ArrayList<String>();
	private static ArrayList<PlayerCard> allCards = new ArrayList<PlayerCard>();
	private static List<String> allMatchesOnPage = new ArrayList<String>();
	private static String[] columnNames = {"Ovr", "Name", "Team","Series" ,"Buy Now", "Sell Now", "Buy - Sell", "% Difference"};
	private static String[] comboItems = {"Live Series"};
	private static Object[][] data ={};
    private static DefaultTableModel model = new DefaultTableModel(data,columnNames);
    private static JTable table;
    private static JPanel filterPanel,rankPanel,buyPanel,sellPanel,mainPanel,bottomPanel, seriesPanel;
    private static Float perDif;
    private static float percentDiff;
    private static JFrame frame;
    private static JLabel lastUpdated, updatingLabel,errorLabel;
    private static BufferedReader in;
    private static JButton addButton, filterButton, resetFilterButton;
    private static JTextField rankMin,rankMax,buyMin,buyMax,sellMin,sellMax;
    private static MyDocumentFilter documentFilter;
    private static Border redBorder, defBorder;
    private static TableRowSorter<DefaultTableModel> rowSorter;
    private static JComboBox seriesCombo;
    
    public static void main(String[] args) throws IOException{
			teamTable.put("ari","Diamondbacks"); teamTable.put("atl","Braves"); teamTable.put("bal","Orioles"); teamTable.put("bos","Red Sox"); teamTable.put("chc","Cubs"); teamTable.put("cin","Reds"); teamTable.put("cle","Indians"); teamTable.put("col","Rockies"); teamTable.put("cws","White Sox"); teamTable.put("det","Tigers"); teamTable.put("fa","Free Agent"); teamTable.put("hou","Astros"); teamTable.put("kc","Royals"); teamTable.put("laa","Angels"); teamTable.put("lad","Dodgers"); teamTable.put("mia","Marlins"); teamTable.put("mil","Brewers"); teamTable.put("min","Twins"); teamTable.put("nym","Mets"); teamTable.put("nyy","Yankees"); teamTable.put("oak","Athletics"); teamTable.put("phi","Phillies"); teamTable.put("pit","Pirates"); teamTable.put("sd","Padres"); teamTable.put("sea","Mariners"); teamTable.put("sf","Giants"); teamTable.put("stl","Cardinals"); teamTable.put("tb","Rays"); teamTable.put("tex","Rangers"); teamTable.put("wsh","Nationals"); teamTable.put("tor","Blue Jays");
			blankPg=2000;
			
			SwingUtilities.invokeLater(new Runnable(){
				public void run(){
			        
					try
			        {
			            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			        }
			        catch(Exception e)
			        {
			            e.printStackTrace();
			        }
					
					JPanel topPanel = new JPanel();
					JPanel tablePanel = new JPanel();
					addButton = new JButton("Refresh");
					filterButton = new JButton("Filter");
					resetFilterButton = new JButton("Reset Filter");
					//JButton removeButton = new JButton("Remove");
			        addButton.setBounds(50,50,90, 50);
			        filterButton.setBounds(50,50,90, 50);  
					
			        

					frame = new JFrame("MLB The Show 16 Market Assistant");
					frame.setSize(1200,560);
					//930 520
				    table = new JTable(model){
			            /**
						 * 
						 */
						private static final long serialVersionUID = 1L;

					    @Override
					    public Class<?> getColumnClass(int columnIndex) {
					        /*if (columnIndex == 0 || columnIndex == 3 || columnIndex == 4 || columnIndex == 5 ){
					        	
					        	return Integer.class;
					        }
					        else{
					        	return String.class;
					        }
					    	*/
					    	
					    	switch (columnIndex) {
			                    case 0:
			                        return Integer.class;
			                    case 3:
			                        return Integer.class;
			                    case 4:
			                        return Integer.class;
			                    case 5:
			                        return Integer.class;
			                    case 6:
			                        return String.class;
			                    default:
			                        return String.class;
					    		}
		            
					    }
					    public boolean isCellEditable(int row, int col) {
					         return false;
					    }



				    };
				    
				      addButton.addActionListener(new ActionListener()
				      {
				         public void actionPerformed(ActionEvent e)
				         {
				        	
				        	 try {
								model.setRowCount(0);
								allMatches = new ArrayList<String>();
								allCards = new ArrayList<PlayerCard>();
								allMatchesOnPage = new ArrayList<String>();
								refreshMethod();
							    DateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy HH:mm:ss");
							    //get current date time with Date()
							   	long date = System.currentTimeMillis();
							    String dateString = dateFormat.format(date);
								lastUpdated.setText("Last Updated: " + dateString);
								
							} catch (IOException e1) {
								e1.printStackTrace();
							} 
				        	 

				         }
				      });
				      
				      filterButton.addActionListener(new ActionListener()
				      {
				         public void actionPerformed(ActionEvent e)
				         {
				        	 int errorExists = 0;

				        	 
				        	 if(sellMin.getText().equals("")){
				        		 sellMin.setText("0");
				        	 }
				        	 
				        	 if(sellMax.getText().equals("")){
				        		 sellMax.setText("9999999");

				        	 }
				        	 if(buyMin.getText().equals("")){
				        		 buyMin.setText("0");

				        	 }
				        	 if(buyMax.getText().equals("")){
				        		 buyMax.setText("9999999");

				        	 }
				        	 if(rankMin.getText().equals("")){
				        		 rankMin.setText("0");

				        	 }
				        	 if(rankMax.getText().equals("")){
				        		 rankMax.setText("99");
				        	 }

				        	 sellMinVal=Integer.parseInt(sellMin.getText());
				        	 sellMaxVal=Integer.parseInt(sellMax.getText());
				        	 rankMinVal=Integer.parseInt(rankMin.getText());
				        	 rankMaxVal=Integer.parseInt(rankMax.getText());
				        	 buyMinVal=Integer.parseInt(buyMin.getText());
				        	 buyMaxVal=Integer.parseInt(buyMax.getText());
				        	 errorLabel.setVisible(false);
				        	 
			        		 buyMin.setBorder(defBorder);
			        		 buyMax.setBorder(defBorder);
			        		 sellMin.setBorder(defBorder);
			        		 sellMax.setBorder(defBorder);
			        		 rankMin.setBorder(defBorder);
			        		 rankMax.setBorder(defBorder);
			        		 
				        	 if(sellMinVal>sellMaxVal){
				        		 sellMin.setBorder(redBorder);
				        		 sellMax.setBorder(redBorder);
				        		 errorExists = 1;
				        	 }
				        	 if(buyMinVal>buyMaxVal){
				        		 buyMin.setBorder(redBorder);
				        		 buyMax.setBorder(redBorder);
				        		 errorExists = 1;

				        	 }
				        	 if(rankMinVal>rankMaxVal){
				        		 rankMin.setBorder(redBorder);
				        		 rankMax.setBorder(redBorder);
				        		 errorExists = 1;
				        	 }
				        	 
				        	 if(errorExists==0){
				        	      List<RowFilter<Object,Object>> rfs = new ArrayList<RowFilter<Object,Object>>(6);
				        	      
				        	      rfs.add(RowFilter.numberFilter(ComparisonType.AFTER, rankMinVal-1 ,0));
				        	      rfs.add(RowFilter.numberFilter(ComparisonType.BEFORE, rankMaxVal+1 ,0));
				        	      rfs.add(RowFilter.numberFilter(ComparisonType.AFTER, sellMinVal-1 ,5));
				        	      rfs.add(RowFilter.numberFilter(ComparisonType.BEFORE, sellMaxVal+1 ,5));
				        	      rfs.add(RowFilter.numberFilter(ComparisonType.AFTER, buyMinVal-1 ,4));
				        	      rfs.add(RowFilter.numberFilter(ComparisonType.BEFORE, buyMaxVal+1 ,4));
				        	      //sorter.setRowFilter(RowFilter.numberFilter(ComparisonType.AFTER, 80 ,0));
				        	      //sorter.setRowFilter(RowFilter.numberFilter(ComparisonType.BEFORE, 90 ,0));
				        	      rowSorter.setRowFilter(RowFilter.andFilter(rfs));
				        	      
				        	 }
				        	 if(errorExists==1){
				        		 errorLabel.setVisible(true);
				        	 }
				         }
				      });
				      
				      resetFilterButton.addActionListener(new ActionListener()
				      {
				         public void actionPerformed(ActionEvent e)
				         {
				        	  rowSorter.setRowFilter(null);
				         }
				      });
				      
				    topPanel.add(addButton);
				    topPanel.add(filterButton);
				    topPanel.add(resetFilterButton);
				    
				    mainPanel = new JPanel();
				    
				    filterPanel = new JPanel();
				    
				    bottomPanel = new JPanel();
				    bottomPanel.setPreferredSize(new Dimension(1200,20));
				    rankPanel = new JPanel();
				    rankPanel.setBorder(BorderFactory.createTitledBorder("Ovr"));
				    
				    buyPanel = new JPanel();
				    buyPanel.setBorder(BorderFactory.createTitledBorder("Buy Now"));
				    
				    sellPanel = new JPanel();
				    sellPanel.setBorder(BorderFactory.createTitledBorder("Sell Now"));
				    
				    seriesPanel = new JPanel();
				    seriesPanel.setBorder(BorderFactory.createTitledBorder("Card Series"));
				    seriesCombo = new JComboBox<String>(comboItems);
				    seriesPanel.add(seriesCombo);
				    
				    
				    
				    filterPanel.add(rankPanel);
				    
				    
				    JPanel donationPanel = new JPanel();
				    donationPanel.setLayout(new BorderLayout());
				    
				    
				    rankMin = new JTextField("0");
				    rankMax = new JTextField("99");
				    
				    redBorder=BorderFactory.createLineBorder(Color.red);
				    defBorder=rankMin.getBorder();
				    
				    rankMin.setPreferredSize(new Dimension(30,25));
				    rankMax.setPreferredSize(new Dimension(30,25));
				    
				    JLabel rankMinLabel = new JLabel("Min:");
				    JLabel rankMaxLabel = new JLabel("Max:");
				    JLabel rankDashLabel = new JLabel(" - ");

				    rankPanel.add(rankMinLabel);
				    rankPanel.add(rankMin);
				    rankPanel.add(rankDashLabel);
				    rankPanel.add(rankMaxLabel);
				    rankPanel.add(rankMax);
				   
				    
				    filterPanel.add(buyPanel);
				    buyMin = new JTextField("0");
				    buyMax = new JTextField("9999999");
				    
				    
				    
				    
				    buyMin.setPreferredSize(new Dimension(80,25));
				    buyMax.setPreferredSize(new Dimension(80,25));

				    JLabel buyMinLabel = new JLabel("Min:");
				    JLabel buyMaxLabel = new JLabel("Max:");
				    JLabel buyDashLabel = new JLabel(" - ");
				    
				    buyPanel.add(buyMinLabel);
				    buyPanel.add(buyMin);
				    buyPanel.add(buyDashLabel);
				    buyPanel.add(buyMaxLabel);
				    buyPanel.add(buyMax);
				    
				    filterPanel.add(sellPanel);
				    filterPanel.add(seriesPanel);
				    sellMin = new JTextField("0");
				    sellMax = new JTextField("9999999");

				    
				    sellMin.setPreferredSize(new Dimension(80,25));
				    sellMax.setPreferredSize(new Dimension(80,25));
				    
				    JLabel sellMinLabel = new JLabel("Min:");
				    JLabel sellMaxLabel = new JLabel("Max:");
				    JLabel sellDashLabel = new JLabel(" - ");
				    
				    sellPanel.add(sellMinLabel);
				    sellPanel.add(sellMin);
				    sellPanel.add(sellDashLabel);
				    sellPanel.add(sellMaxLabel);
				    sellPanel.add(sellMax);
				    
			        ((AbstractDocument)buyMin.getDocument()).setDocumentFilter(new MyDocumentFilter());  
			        ((AbstractDocument)buyMax.getDocument()).setDocumentFilter(new MyDocumentFilter());  
			        ((AbstractDocument)rankMin.getDocument()).setDocumentFilter(new MyDocumentFilter());  
			        ((AbstractDocument)rankMax.getDocument()).setDocumentFilter(new MyDocumentFilter());  
			        ((AbstractDocument)sellMin.getDocument()).setDocumentFilter(new MyDocumentFilter());  
			        ((AbstractDocument)sellMax.getDocument()).setDocumentFilter(new MyDocumentFilter());  

				    
				    
				    
				    
				    
				    JScrollPane scrollPane = new JScrollPane(table);
				    scrollPane.setPreferredSize(new Dimension(1095, 400));
				    table.getColumnModel().getColumn(0).setPreferredWidth(100);
				    table.getColumnModel().getColumn(1).setPreferredWidth(400);
				    table.getColumnModel().getColumn(2).setPreferredWidth(350);
				    table.getColumnModel().getColumn(3).setPreferredWidth(200);
				    table.getColumnModel().getColumn(4).setPreferredWidth(200);
				    table.getColumnModel().getColumn(5).setPreferredWidth(200);
				    table.getColumnModel().getColumn(6).setPreferredWidth(270);
				    table.getColumnModel().getColumn(7).setPreferredWidth(270);
				    

				    
				    lastUpdated = new JLabel("Last Updated: Never");
				    updatingLabel = new JLabel("Updating: Page 0 of 0");
				    errorLabel = new JLabel("Error: Min is higher than Max");
				    JLabel donationLabel = new JLabel("This is a free program. If you like it, donate to PayPal: AnthoneyKalasho@Gmail.com");
				    donationPanel.add(donationLabel,BorderLayout.CENTER);
				    errorLabel.setForeground(Color.red);
				    errorLabel.setVisible(false);
				    bottomPanel.setLayout(new BorderLayout());
				    updatingLabel.setBorder(new EmptyBorder( 3,50, 3, 3 ));
				    lastUpdated.setBorder(new EmptyBorder( 3, 3, 3, 50 ));
				    errorLabel.setBorder(new EmptyBorder( 3, 260, 3, 0 ));
				    donationLabel.setBorder(new EmptyBorder( 3, 3, 3, 3 ));
				    bottomPanel.add(updatingLabel, BorderLayout.WEST);
				    bottomPanel.add(lastUpdated,BorderLayout.EAST);
				    bottomPanel.add(errorLabel,BorderLayout.CENTER);

				    //bottomPanel.setPreferredSize(preferredSize)
				    
				    //updatingLabel.setLocation(0,0);

				    //RowSorter<DefaultTableModel> sorter = new TableRowSorter<DefaultTableModel>(model);
				    //table.setRowSorter(sorter);
				    table.setAutoCreateRowSorter(true);
				    //@SuppressWarnings("unchecked")
				    rowSorter = (TableRowSorter<DefaultTableModel>)table.getRowSorter();
				    rowSorter.setComparator(4, new Comparator<Integer>() {

				            @Override
				            public int compare(Integer o1, Integer o2)
				            {
				                return o2 - o1;
				            }

				        });
				    rowSorter.setComparator(0, new Comparator<Integer>() {

			            @Override
			            public int compare(Integer o1, Integer o2)
			            {
			                return o2 - o1;
			            }

			        });
				    rowSorter.setComparator(5, new Comparator<Integer>() {

			            @Override
			            public int compare(Integer o1, Integer o2)
			            {
			                return o2 - o1;
			            }

			        });
				    rowSorter.setComparator(6, new Comparator<Integer>() {

			            @Override
			            public int compare(Integer o1, Integer o2)
			            {
			                return o2 - o1;
			            }

			        });
				    rowSorter.setComparator(7, new Comparator<String>() {

			            @Override
			            public int compare(String o1,String o2)
			            {
				           /*
			            	if(o1 == null && o2 == null){
				        	   System.out.println("bothNull");
				        	   return 0;
				           }
				           if(o1 == null && !(o2 == null)){
				        	   System.out.println("oneNull");
				        	   return -1;
				           }
				           if(!(o1 == null) && o2 == null){
				        	   System.out.println("oneNull");
				        	   return 1;
				           }
				           
			            	System.out.println("o1 = " + o1);
			            	System.out.println("o2 = " + o2);
			            	System.out.println("-");
			            	if(o1.equals(null)){
			            		System.out.println("NaN encountered");
			            	}
			            	if(o2.equals(null)){
			            		System.out.println("NaN encountered");
			            	}
				           System.out.println("noNull");
				           return (int) (o2 - o1);
				           */
			               if(o1 == "N/A" && o2 == "N/A"){
					           return 0;
					       }
				           if(o1 == "N/A" && !(o2 == "N/A")){
				        	   return 1;
				           }
				           if(!(o1 == "N/A") && o2 == "N/A"){
				        	   return -1;
				           }
					       if((Float.parseFloat(o1))==(Float.parseFloat(o2))){
					    	   return 0;
					       }
					       if((Float.parseFloat(o1))>(Float.parseFloat(o2))){
					    	   return -1;
					       }
			            	   return 1;
			            }

			        });
				    tablePanel.add(scrollPane);
				    //frame.add(new JScrollPane(table));
				    table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
					
				    
					
		
					mainPanel.add(topPanel,BorderLayout.NORTH);
					//topPanel.setAlignmentX(BorderLayout.WEST);
					//filterPanel.setAlignmentX(BorderLayout.WEST);
					
					mainPanel.add(filterPanel, BorderLayout.NORTH);
					mainPanel.add(tablePanel, BorderLayout.CENTER);
					//mainPanel.add(bottomPanel, BorderLayout.PAGE_END);
					//mainPanel.add(lastUpdated);
					//BorderLayout bottomLayout = new BorderLayout();
					mainPanel.add(bottomPanel);
					mainPanel.add(donationPanel);
					//bottomPanel.setLayout(bottomLayout);
					//bottomLayout.setHgap(0);
					//mainPanel.setLayout(new FlowLayout());
					
					mainPanel.revalidate();
					frame.add(mainPanel);
				    frame.revalidate();
				    //frame.setLayout(new FlowLayout());
				    frame.setResizable(false);
				    
					frame.setVisible(true);
					frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				};
			});
	    
	    
	    
	    
		
	}
	
	public static PlayerCard toPlayerCard(String match){
		String cardName = "blankName",cardTeam="blank";
		int cardRank = 0, cardBuy = 0, cardSell = 0, cardID = 0;
		String objZero = "0",objOne = "0",objTwo = "0",objThree = "0",objFour = "0", objFive = "0";
		final Matcher m0 = Pattern.compile("k -->[\\s-\\S]<td>[\\s-\\S](.*?)[\\s-\\S]<\\/td>").matcher(match);
		while (m0.find()) {
			//rank
			objZero=m0.group(1);
			//0System.out.println(objZero);
		}
		
		final Matcher m1 = Pattern.compile("(e -->[\\s-\\S]<td>[\\s-\\S](.*?)[\\s-\\S]<\\/td>)").matcher(match);
		while (m1.find()) {
			//name
			objOne=m1.group(2);
		}
		
		final Matcher m2 = Pattern.compile("<\\/span>[\\s-\\S](.*?)[\\s-\\S]<\\/td>").matcher(match);
		while (m2.find()) {
			//sell
			objTwo=m2.group(1);
		}
		
		final Matcher m3 = Pattern.compile(">[\\s-\\S](.*?)[\\s-\\S]<\\/span").matcher(match);
		while (m3.find()) {
			//buy
			objThree=m3.group();
		}
		
		final Matcher m4 = Pattern.compile("\\?id=(.*?)\"").matcher(match);
		while (m4.find()) {
			//id
			objFour=m4.group();
		}
		
		final Matcher m5 = Pattern.compile("<img class='img-responsive' src='https:\\/\\/s3.amazonaws.com\\/playcore-community-assets\\/mlb16-production\\/dist\\/img\\/teams\\/cap\\/logo2_(.*?).png'>").matcher(match);
		while (m5.find()) {
			//team
			objFive=m5.group(1);
		}
		
		objTwo=objTwo.replaceAll("[^0-9]","");
		objThree=objThree.replaceAll("[^0-9]","");
		objFour=objFour.replaceAll("[^0-9]","");
		
		cardName = objOne;
		//cardTeam=objFive;
		cardRank=Integer.parseInt(objZero);
		cardBuy=Integer.parseInt(objTwo);
		cardSell=Integer.parseInt(objThree);
		cardID=Integer.parseInt(objFour);
		
		cardTeam =  teamTable.get(objFive).toString();
		PlayerCard outputCard = new PlayerCard(cardRank, cardName,"Live Series", cardBuy, cardSell, cardID, cardTeam);
		return outputCard;
	}
	public static void refreshMethod() throws IOException{
		/*
		outerLoop: for(i=1;i<5;i++){
			sourceCode="";
			URL yahoo = new URL("http://theshownation.com/market?page=" + i);
			BufferedReader in = new BufferedReader(
			            new InputStreamReader(
			            yahoo.openStream()));
	
			String inputLine;
			StringBuilder a = new StringBuilder();
			while ((inputLine = in.readLine()) != null){
			    a.append(inputLine + "\n");
			    if(inputLine.contains("Your search returned 0 results")){
					in.close();
					System.out.println("Page "+i+" is blank.");
					blankPg=i-1;
					break outerLoop;
			    	
			    }
			}
			sourceCode=a.toString();
			
			

			Matcher m = Pattern.compile("(<tr>[\\s\\S]*?<\\/tr>)").matcher(sourceCode);
			while (m.find()) {
				allMatchesOnPage.add(m.group());
			}
			Iterator<String> matchIterator = allMatchesOnPage.iterator();
			while (matchIterator.hasNext()){
				String currentItem =matchIterator.next();
				if(currentItem.contains("text-center")){
					matchIterator.remove();
				}
			}
			in.close();
			
			updatingLabel.setText("Updating: Page "+i+" of"+ blankPg);
			updatingLabel.revalidate();
			System.out.println(i);
			allMatches.addAll(allMatchesOnPage);
		}
		*/
		
		
		i=1;
		zeroResults=0;
		
        final Timer t = new Timer(0,null);
        t.addActionListener(new ActionListener(){
          public void actionPerformed(ActionEvent e){
	        	sourceCode="";
	        	addButton.setEnabled(false);
	        	filterButton.setEnabled(false);
	        	resetFilterButton.setEnabled(false);
	        	
	  			try{
	        	//URL yahoo = new URL("http://theshownation.com/market?page=" + i);
		        URL yahoo = new URL("http://theshownation.com/market?page="+i+"&series_id=1337");

	  			in = new BufferedReader(
	  			            new InputStreamReader(
	  			            yahoo.openStream()));
	  	
	  			String inputLine;
	  			StringBuilder a = new StringBuilder();
	  			outerLoop: while ((inputLine = in.readLine()) != null){
	  			    a.append(inputLine + "\n");
	  			    
	  			    if(inputLine.contains("Your search returned 0 results")){
	  					//in.close();
	  					//System.out.println("Page "+i+" is blank.");
	  					blankPg=i-1;
	          		    zeroResults=1;
	  					for(String currentMatch : allMatchesOnPage){
	  						PlayerCard tempCard=toPlayerCard(currentMatch);
	  						percentDiff = (float)((((double)tempCard.getBuyNow()-(double)tempCard.getSellNow())/(double)tempCard.getSellNow())*100);
	  						percentDiff = round(percentDiff,2);
	  						perDif = (Float)percentDiff;
	  						perDifStr = Float.toString(perDif);
	  						if (tempCard.getBuyNow()==0){
	  							perDifStr = "N/A";
	  						}
	  						if (tempCard.getSellNow()==0) {
	  							perDifStr = "N/A";
	  						}
	  						model.addRow(new Object[]{(Integer)tempCard.getCardRank(),tempCard.getName(),tempCard.getTeam(),"Live Series",(Integer)tempCard.getBuyNow(),(Integer)tempCard.getSellNow(),(Integer)tempCard.getBuyNow()-(Integer)tempCard.getSellNow(),perDifStr});
	  						allCards.add(tempCard);
	  							
	  					}
	  					addButton.setEnabled(true);
	  		        	filterButton.setEnabled(true);
	  		        	resetFilterButton.setEnabled(true);
	  					break outerLoop;
	          		    
	  			    	
	  			    }
	  			}
	  			if(zeroResults==1){
	  				in.close();
	  				addButton.setEnabled(true);
  		        	filterButton.setEnabled(true);
  		        	resetFilterButton.setEnabled(true);
  					firstRun=1;
  					try{
  						in.close();
  					}
  					catch(IOException q){}
  						t.stop();
	  			}
	  			sourceCode=a.toString();
           


  			Matcher m = Pattern.compile("(<tr>[\\s\\S]*?<\\/tr>)").matcher(sourceCode);
  			while (m.find()) {
  				allMatchesOnPage.add(m.group());
  			}
  			Iterator<String> matchIterator = allMatchesOnPage.iterator();
  			while (matchIterator.hasNext()){
  				String currentItem =matchIterator.next();
  				if(currentItem.contains("text-center")){
  					matchIterator.remove();
  				}
  			}
  			in.close();
  			if(firstRun==0){
  				updatingLabel.setText("Updating: Page "+(i-1)+" of ?");
  			}
  			else {
  				updatingLabel.setText("Updating: Page "+(i-1)+" of " + (blankPg));
  			}
  			updatingLabel.revalidate();
  			//System.out.println(i);
  			allMatches.addAll(allMatchesOnPage);
	  		}
	  		catch(IOException g){
	  			g.printStackTrace();
	  		}
        	  
        	  
        	  
        	  
        	  
        	  
        	  if(i==blankPg + 1){
        		  addButton.setEnabled(true);
		        	filterButton.setEnabled(true);
		        	resetFilterButton.setEnabled(true);
        		  t.stop();
        	  }
        	  i++;
          }
        });
        
        t.start();
        

		
		
		
	}
	
	public static float round(float number, int scale) {
	    int pow = 10;
	    for (int i = 1; i < scale; i++)
	        pow *= 10;
	    float tmp = number * pow;
	    return (float) (int) ((tmp - (int) tmp) >= 0.5f ? tmp + 1 : tmp) / pow;
	}
	

}
