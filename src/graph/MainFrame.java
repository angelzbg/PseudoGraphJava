package graph;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.Popup;
import javax.swing.PopupFactory;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileSystemView;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.NumberFormatter;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import ui.HintJTextField;

public class MainFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static void main(String[] args) {
		MainFrame mainFrame = new MainFrame();
	}
	
	/* --------------- Components [ START ] */
	// Wrapper
	JPanel panelWrapper = new JPanel();
	
	// Background Panel
	JPanel panelContent = new JPanel();
	
	// JLabels to open the menus
	JLabel labelOpenLeftMenu, labelOpenTopMenu, labelOpenConsole, labelOpenSettings, labelOpenTable;
	
	// components for the file menu
	JPanel panelFileMenu = new JPanel(), panelFileMenu_Nav = new JPanel(), panelFileMenu_Body = new JPanel();
	JLabel labelFileMenu_Back, labelFileMenu_Open;
	JPanel panelFileMenu_Container = new JPanel();
	JScrollPane JSP_ScrollerContainer = new JScrollPane(panelFileMenu_Container);
	
	// components for the top menu
	JPanel panelTopMenuWrap = new JPanel();
	JLabel labelTopMenu_Back;
	// ---> components for panelTopMenuChoices
	JPanel panelTopMenuChoices = new JPanel();
	JLabel labelChoice_AddRoom, labelChoice_AddLink, labelChoice_Search;
	// ---> components for panelAddRoom
	JPanel panelAddRoom = new JPanel();
	JTextField JTF_addRoom_Name = new JTextField(), JTF_addRoom_X, JTF_addRoom_Y, JTF_addRoom_Floor;
	JComboBox JCB_addRoom_Type = new JComboBox(new String[] {"room", "transit"});
	JLabel labelAddRoom;
	// ---> components for panelAddLink
	JPanel panelAddLink = new JPanel();
	JTextField JTF_addLink_From = new JTextField(), JTF_addLink_To = new JTextField(),  JTF_addLink_Cost;
	JComboBox JCB_addLink_Type = new JComboBox(new String[]{ "walk", "climb", "lift" });
	JComboBox JCB_addLink_Way = new JComboBox(new String[]{ "two-way", "one-way" });
	JLabel labelAddLink;
	// ---> components for panelSearchPath
	JPanel panelSearch = new JPanel();
	JTextField JTF_searchFrom = new JTextField(), JTF_searchTo = new JTextField();
	JComboBox JCB_searchType = new JComboBox(new String[]{ "no stairs", "by coords", "lifts prior" });
	JLabel labelSearch;
	
	// components for panelConsoleWrap
	JPanel panelConsoleWrap = new JPanel();
	JLabel labelConsoleBack;
	JTextPane JTA_Console = new JTextPane();
	JScrollPane JSP_ScrollerConsole = new JScrollPane(JTA_Console);
	
	// components for panelTableWrap
	JPanel panelTableWrap = new JPanel();
	JTable JT_Table = new JTable();
	JScrollPane JSP_ScrollerTable = new JScrollPane(JT_Table);
	JLabel labelTableBack, labelShowRoomsInTable, labelShowLinksInTable;
	
	//components for panelSettingsWrap
	JPanel panelSettingsWrap = new JPanel();
	JComboBox JCB_floorChooser = new JComboBox(new String[] {"0", "1", "2"});
	JLabel labelGoToFloor, labelSettingsBack;
	
	/* --------------- Components [  END  ] */
	
	/* --------------- Display things */
	private int width, height, side;
	private final int side_div_15; // after the first assignment it will stay the same value till the end
	/* ----- Colors ----- */
	private final Color lightGreen = Color.decode("#c3f7dd"), 
			lightGreenDarker = Color.decode("#c7edd9"), 
			buttonGreenLight = Color.decode("#baf2bc"), 
			buttonGreenDark = Color.decode("#387f3b"), 
			textGreen1 = Color.decode("#61a564"),
			textWhite = Color.WHITE,
			textBlueLight1 = Color.decode("#4cd4ff"),
			textBlueLightDarker1 = Color.decode("#4cbaff"),
			blueDarker2 = Color.decode("#4c9cff");
	
	// Graph
	Graph graph = new Graph();
	Graph[] grap_by_floor;
	
	/* ----- For the Logic ----- */
	private int max_floor = Integer.MIN_VALUE, min_floor = Integer.MAX_VALUE;
	private String file_path; // the path of the current(last loaded) file
	HashMap<String, Boolean> flagged = new HashMap<String, Boolean>(); // instead of flagging and deflagging the whole map
	HashMap<String, LinkSearch> comingFrom = new HashMap<String, LinkSearch>(); // saving the link between the current room in the searching and where it came from (this way we can put together the path in reverse order if we find one)
	boolean isPathFound = false;
	
	public MainFrame() { // Constructor [ START ]
		// Display things
		GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice(); // get display metrics
		int width = gd.getDisplayMode().getWidth();
		int height = gd.getDisplayMode().getHeight();
		if(height > width) side = (int) (width*0.8);
		else side = (int) (height*0.8);
		side_div_15 = side/15;
		
		NumberFormat numberFormat = NumberFormat.getInstance();
		NumberFormatter formatterInteger = new NumberFormatter(numberFormat);
		formatterInteger.setValueClass(Integer.class);
		formatterInteger.setMinimum(0);
		formatterInteger.setMaximum(Integer.MAX_VALUE);
		formatterInteger.setAllowsInvalid(false);
		formatterInteger.setCommitsOnValidEdit(true);
		
		
		this.setLayout(new BorderLayout());
		
		panelWrapper.setPreferredSize(new Dimension(side,side));
		this.add(panelWrapper, BorderLayout.CENTER);
		panelWrapper.setLayout(null);
		
		this.pack();
		
		labelOpenLeftMenu = new JLabel(new ImageIcon(new ImageIcon("src/icon_open_left_menu.png").getImage().getScaledInstance(side_div_15, side_div_15, Image.SCALE_DEFAULT)));
		labelOpenLeftMenu.setSize(side_div_15, side_div_15);
		panelWrapper.add(labelOpenLeftMenu);
		labelOpenLeftMenu.setLocation(0,0);
		labelOpenLeftMenu.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		labelOpenLeftMenu.setVisible(false);
		
		// components for the file menu (left)
		panelFileMenu.setSize(side_div_15, side);
		panelWrapper.add(panelFileMenu);
		panelFileMenu.setLocation(0, 0);
		panelFileMenu.setLayout(null);
		panelFileMenu.setVisible(true);
		
		panelFileMenu_Nav.setSize(side_div_15, side_div_15*2);
		panelFileMenu.add(panelFileMenu_Nav);
		panelFileMenu_Nav.setLocation(0, 0);
		panelFileMenu_Nav.setLayout(new GridLayout(2,1)); // for labelFileMenu_Back and labelFileMenu_Open positioned vertically on the layout
		panelFileMenu_Nav.setBackground(lightGreenDarker);
		
		labelFileMenu_Back = new JLabel(new ImageIcon(new ImageIcon("src/icon_arrow_left.png").getImage().getScaledInstance(side_div_15, side_div_15, Image.SCALE_DEFAULT)));
		panelFileMenu_Nav.add(labelFileMenu_Back);
		labelFileMenu_Back.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 1, Color.BLACK));
		labelFileMenu_Back.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		labelFileMenu_Open = new JLabel(new ImageIcon(new ImageIcon("src/icon_choose_file.png").getImage().getScaledInstance(side_div_15, side_div_15, Image.SCALE_DEFAULT)));
		panelFileMenu_Nav.add(labelFileMenu_Open);
		labelFileMenu_Open.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, Color.BLACK));
		labelFileMenu_Open.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		
		
		panelFileMenu_Body.setSize(side_div_15, side-side_div_15*2);
		panelFileMenu.add(panelFileMenu_Body);
		panelFileMenu_Body.setLocation(0, side_div_15*2);
		panelFileMenu_Body.setBackground(lightGreen);
		panelFileMenu_Body.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, Color.BLACK));
		panelFileMenu_Body.setLayout(null);
		
		panelFileMenu_Container.setSize(panelFileMenu_Body.getWidth(), 0); // later must be resized according to how many components it has
		panelFileMenu_Container.setBackground(lightGreen);
		panelFileMenu_Container.setLayout(null);
		
		JSP_ScrollerContainer.setSize(panelFileMenu_Body.getWidth(), panelFileMenu_Body.getHeight());
		panelFileMenu_Body.add(JSP_ScrollerContainer);
		JSP_ScrollerContainer.setBackground(new Color(0,0,0,0));
		
		//--
		
		labelOpenTopMenu = new JLabel(new ImageIcon(new ImageIcon("src/icon_open_top_menu.png").getImage().getScaledInstance(side_div_15, side_div_15, Image.SCALE_DEFAULT)));
		labelOpenTopMenu.setSize(side_div_15, side_div_15);
		panelWrapper.add(labelOpenTopMenu);
		labelOpenTopMenu.setLocation(side-side_div_15,0);
		labelOpenTopMenu.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		labelOpenTopMenu.setVisible(false);
		
		// components for the top menu
		panelTopMenuWrap.setSize(side-side_div_15*2, side_div_15);
		panelWrapper.add(panelTopMenuWrap);
		panelTopMenuWrap.setLocation(side_div_15*2, 0);
		panelTopMenuWrap.setLayout(null);
		panelTopMenuWrap.setBorder(BorderFactory.createMatteBorder(0, 1, 1, 1, Color.BLACK));
		panelTopMenuWrap.setBackground(lightGreenDarker);
		panelTopMenuWrap.setVisible(true);
		
		labelTopMenu_Back = new JLabel(new ImageIcon(new ImageIcon("src/icon_arrow_up.png").getImage().getScaledInstance(side_div_15, side_div_15, Image.SCALE_DEFAULT)));
		labelTopMenu_Back.setSize(side_div_15, side_div_15);
		panelTopMenuWrap.add(labelTopMenu_Back);
		labelTopMenu_Back.setLocation(panelTopMenuWrap.getWidth()-side_div_15, 0);
		labelTopMenu_Back.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		
		
		// ---> components for panelTopMenuChoices
		panelTopMenuChoices.setSize(panelTopMenuWrap.getWidth()-side_div_15, side_div_15/2);
		panelTopMenuWrap.add(panelTopMenuChoices);
		panelTopMenuChoices.setLocation(0, 0);
		panelTopMenuChoices.setLayout(new GridLayout(1,3)); // for the 3 labels grouped equally horizontally
		panelTopMenuChoices.setBackground(new Color(0, 0, 0, 0));
		labelChoice_AddRoom = new JLabel("ADD ROOM", SwingConstants.CENTER);
		labelChoice_AddRoom.setBorder(BorderFactory.createMatteBorder(0, 1, 1, 1, Color.BLACK));
		labelChoice_AddRoom.setBackground(buttonGreenDark);
		labelChoice_AddRoom.setOpaque(true);
		labelChoice_AddRoom.setForeground(textWhite);
		labelChoice_AddRoom.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		labelChoice_AddLink = new JLabel("ADD LINK", SwingConstants.CENTER);
		labelChoice_AddLink.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, Color.BLACK));
		labelChoice_AddLink.setBackground(buttonGreenLight);
		labelChoice_AddLink.setOpaque(true);
		labelChoice_AddLink.setForeground(textGreen1);
		labelChoice_AddLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		labelChoice_Search = new JLabel("SEARCH PATH", SwingConstants.CENTER);
		labelChoice_Search.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, Color.BLACK));
		labelChoice_Search.setBackground(buttonGreenLight);
		labelChoice_Search.setOpaque(true);
		labelChoice_Search.setForeground(textGreen1);
		labelChoice_Search.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		panelTopMenuChoices.add(labelChoice_AddRoom);
		panelTopMenuChoices.add(labelChoice_AddLink);
		panelTopMenuChoices.add(labelChoice_Search);
		
		// ---> components for panelAddRoom
		panelAddRoom.setSize(panelTopMenuChoices.getWidth(), panelTopMenuChoices.getHeight());
		panelTopMenuWrap.add(panelAddRoom);
		panelAddRoom.setLocation(0, side_div_15/2);
		panelAddRoom.setBackground(new Color(0, 0, 0, 0));
		panelAddRoom.setLayout(new GridLayout(1,6));
		panelAddRoom.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.BLACK));

		panelAddRoom.add(JTF_addRoom_Name);
		JTF_addRoom_Name.setUI(new HintJTextField("Room name", true));
		JTF_addRoom_X = new JFormattedTextField(formatterInteger);
		panelAddRoom.add(JTF_addRoom_X);
		JTF_addRoom_X.setUI(new HintJTextField("X", true));
		JTF_addRoom_Y = new JFormattedTextField(formatterInteger);
		panelAddRoom.add(JTF_addRoom_Y);
		JTF_addRoom_Y.setUI(new HintJTextField("Y", true));
		JTF_addRoom_Floor = new JFormattedTextField(formatterInteger);
		panelAddRoom.add(JTF_addRoom_Floor);
		JTF_addRoom_Floor.setUI(new HintJTextField("# Floor", true));
		panelAddRoom.add(JCB_addRoom_Type);
		labelAddRoom = new JLabel("CONFIRM", SwingConstants.CENTER);
		panelAddRoom.add(labelAddRoom);
		labelAddRoom.setForeground(textBlueLight1);
		labelAddRoom.setBackground(Color.WHITE);
		labelAddRoom.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, textBlueLightDarker1));
		labelAddRoom.setOpaque(true);
		labelAddRoom.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		
		// ---> components for panelAddLink
		panelAddLink.setSize(panelTopMenuChoices.getWidth(), panelTopMenuChoices.getHeight());
		panelTopMenuWrap.add(panelAddLink);
		panelAddLink.setLocation(0, side_div_15/2);
		panelAddLink.setBackground(new Color(0, 0, 0, 0));
		panelAddLink.setLayout(new GridLayout(1,6));
		panelAddLink.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.BLACK));
		panelAddLink.setVisible(false);
		
		panelAddLink.add(JTF_addLink_From);
		JTF_addLink_From.setUI(new HintJTextField("From room", true));
		panelAddLink.add(JTF_addLink_To);
		JTF_addLink_To.setUI(new HintJTextField("To room", true));
		JTF_addLink_Cost = new JFormattedTextField(formatterInteger);
		JTF_addLink_Cost.setUI(new HintJTextField("Cost", true));
		panelAddLink.add(JTF_addLink_Cost);
		panelAddLink.add(JCB_addLink_Type);
		panelAddLink.add(JCB_addLink_Way);
		labelAddLink = new JLabel("CONFIRM", SwingConstants.CENTER);
		panelAddLink.add(labelAddLink);
		labelAddLink.setForeground(textBlueLight1);
		labelAddLink.setBackground(Color.WHITE);
		labelAddLink.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, textBlueLightDarker1));
		labelAddLink.setOpaque(true);
		labelAddLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		
		// ---> components for panelSearch
		panelSearch.setSize(panelTopMenuChoices.getWidth(), panelTopMenuChoices.getHeight());
		panelTopMenuWrap.add(panelSearch);
		panelSearch.setLocation(0, side_div_15/2);
		panelSearch.setBackground(new Color(0, 0, 0, 0));
		panelSearch.setLayout(new GridLayout(1,4));
		panelSearch.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 1, Color.BLACK));
		panelSearch.setVisible(false);
		
		JTF_searchFrom.setUI(new HintJTextField("From room", true));
		panelSearch.add(JTF_searchFrom);
		JTF_searchTo.setUI(new HintJTextField("To room", true));
		panelSearch.add(JTF_searchTo);
		panelSearch.add(JCB_searchType);
		labelSearch = new JLabel("SEARCH", SwingConstants.CENTER);
		panelSearch.add(labelSearch);
		labelSearch.setForeground(textBlueLight1);
		labelSearch.setBackground(Color.WHITE);
		labelSearch.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, textBlueLightDarker1));
		labelSearch.setOpaque(true);
		labelSearch.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			
		//--
		
		labelOpenConsole = new JLabel(new ImageIcon(new ImageIcon("src/icon_open_console.png").getImage().getScaledInstance(side_div_15, side_div_15, Image.SCALE_DEFAULT)));
		labelOpenConsole.setSize(side_div_15, side_div_15);
		panelWrapper.add(labelOpenConsole);
		labelOpenConsole.setLocation(side-side_div_15,side_div_15);
		labelOpenConsole.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		labelOpenConsole.setVisible(false);
		
		// components for panelConsoleWrap
		panelConsoleWrap.setSize(side/2, side_div_15*4);
		panelWrapper.add(panelConsoleWrap);
		panelConsoleWrap.setLocation(side-panelConsoleWrap.getWidth(), side_div_15);
		panelConsoleWrap.setBackground(new Color(0,0,0,0)); // transparent background
		panelConsoleWrap.setLayout(null);
		panelConsoleWrap.setVisible(true);
		
		JSP_ScrollerConsole.setSize(side/2, panelConsoleWrap.getHeight()-side_div_15);
		panelConsoleWrap.add(JSP_ScrollerConsole);
		JSP_ScrollerConsole.setLocation(0, side_div_15);
		
		labelConsoleBack = new JLabel(new ImageIcon(new ImageIcon("src/icon_arrow_right.png").getImage().getScaledInstance(side_div_15, side_div_15, Image.SCALE_DEFAULT)));
		labelConsoleBack.setSize(side_div_15, side_div_15);
		panelConsoleWrap.add(labelConsoleBack);
		labelConsoleBack.setLocation(panelConsoleWrap.getWidth()-side_div_15, 0);
		labelConsoleBack.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		
		JTA_Console.setActionMap(null); // so the user is not able to write anything in the text area
		JTA_Console.setContentType("text/html");
		JTA_Console.setBackground(Color.decode("#375275"));
		
		//--
		
		labelOpenSettings = new JLabel(new ImageIcon(new ImageIcon("src/icon_open_settings.png").getImage().getScaledInstance(side_div_15, side_div_15, Image.SCALE_DEFAULT)));
		labelOpenSettings.setSize(side_div_15, side_div_15);
		panelWrapper.add(labelOpenSettings);
		labelOpenSettings.setLocation(side-side_div_15,side-side_div_15);
		labelOpenSettings.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		labelOpenSettings.setVisible(true);
		
		//components for panelSettingsWrap
		panelSettingsWrap.setSize(side_div_15*3, side_div_15);
		panelWrapper.add(panelSettingsWrap);
		panelSettingsWrap.setLocation(side-panelSettingsWrap.getWidth(), side-panelSettingsWrap.getHeight());
		panelSettingsWrap.setBackground(lightGreenDarker);
		panelSettingsWrap.setBorder(BorderFactory.createMatteBorder(1, 1, 0, 1, Color.BLACK));
		panelSettingsWrap.setLayout(new GridLayout(1,3));
		panelSettingsWrap.setVisible(false);
		
		panelSettingsWrap.add(JCB_floorChooser);
		
		labelGoToFloor = new JLabel(new ImageIcon(new ImageIcon("src/icon_go.png").getImage().getScaledInstance(side_div_15, side_div_15, Image.SCALE_DEFAULT)));
		panelSettingsWrap.add(labelGoToFloor);
		panelSettingsWrap.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		
		labelSettingsBack = new JLabel(new ImageIcon(new ImageIcon("src/icon_arrow_down.png").getImage().getScaledInstance(side_div_15, side_div_15, Image.SCALE_DEFAULT)));
		panelSettingsWrap.add(labelSettingsBack);
		labelSettingsBack.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		
		//--
		
		labelOpenTable = new JLabel(new ImageIcon(new ImageIcon("src/icon_open_table.png").getImage().getScaledInstance(side_div_15, side_div_15, Image.SCALE_DEFAULT)));
		labelOpenTable.setSize(side_div_15, side_div_15);
		panelWrapper.add(labelOpenTable);
		labelOpenTable.setLocation(side-side_div_15,side-side_div_15*2);
		labelOpenTable.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		
		// components for panelTableWrap
		panelTableWrap.setSize(side/2+side_div_15*2, side_div_15*5);
		panelWrapper.add(panelTableWrap);
		panelTableWrap.setLocation(side-panelTableWrap.getWidth(), side-(side_div_15+panelTableWrap.getHeight()));
		panelTableWrap.setLayout(null);
		panelTableWrap.setBackground(new Color(0,0,0,0)); //transparent
		panelTableWrap.setVisible(false);
		
		JSP_ScrollerTable.setSize(panelTableWrap.getWidth(), panelTableWrap.getHeight()-side_div_15);
		panelTableWrap.add(JSP_ScrollerTable);
		JSP_ScrollerTable.setLocation(0, 0);
		labelTableBack = new JLabel(new ImageIcon(new ImageIcon("src/icon_arrow_right.png").getImage().getScaledInstance(side_div_15, side_div_15, Image.SCALE_DEFAULT)));
		labelTableBack.setSize(side_div_15, side_div_15);
		panelTableWrap.add(labelTableBack);
		labelTableBack.setLocation(JSP_ScrollerTable.getWidth()-side_div_15, JSP_ScrollerTable.getHeight());
		labelTableBack.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		labelShowRoomsInTable = new JLabel(new ImageIcon(new ImageIcon("src/icon_rooms.png").getImage().getScaledInstance(side_div_15, side_div_15, Image.SCALE_DEFAULT)));
		labelShowRoomsInTable.setSize(side_div_15, side_div_15);
		panelTableWrap.add(labelShowRoomsInTable);
		labelShowRoomsInTable.setLocation(JSP_ScrollerTable.getWidth()-side_div_15*2, JSP_ScrollerTable.getHeight());
		labelShowRoomsInTable.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		labelShowLinksInTable = new JLabel(new ImageIcon(new ImageIcon("src/icon_links.png").getImage().getScaledInstance(side_div_15, side_div_15, Image.SCALE_DEFAULT)));
		labelShowLinksInTable.setSize(side_div_15, side_div_15);
		panelTableWrap.add(labelShowLinksInTable);
		labelShowLinksInTable.setLocation(JSP_ScrollerTable.getWidth()-side_div_15*3, JSP_ScrollerTable.getHeight());
		labelShowLinksInTable.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		
		//--
		
		// panelContent at the end so it stays behind all other UI components
		panelContent.setBackground(Color.decode("#99d4ff"));
		panelContent.setSize(side, side);
		panelWrapper.add(panelContent);
		panelContent.setLocation(0, 0);
		
		//Open the window for the user
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); // the user is going to close it through JOptionPane on confirmation
		this.pack();
		//this.setVisible(true);
		this.setResizable(false);
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent we) { // listening for the X button
				String ObjButtons[] = {"Exit","Cancel"};
				int PromptResult = JOptionPane.showOptionDialog(null, "Are you sure you want to exit?", "Confirm", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, ObjButtons, ObjButtons[1]);
			    if(PromptResult==0) System.exit(0); // if the user chose Confirm the program will close    
			}
		});
		fixThisTrashSwing();
		
		// ----- ActionListeners
		labelOpenLeftMenu.addMouseListener(mouseAdapter_Open_Close_Menus);
		labelFileMenu_Back.addMouseListener(mouseAdapter_Open_Close_Menus);
		labelOpenTopMenu.addMouseListener(mouseAdapter_Open_Close_Menus);
		labelTopMenu_Back.addMouseListener(mouseAdapter_Open_Close_Menus);
		labelChoice_AddRoom.addMouseListener(mouseAdapter_Open_Close_Menus);
		labelChoice_AddLink.addMouseListener(mouseAdapter_Open_Close_Menus);
		labelChoice_Search.addMouseListener(mouseAdapter_Open_Close_Menus);
		labelOpenConsole.addMouseListener(mouseAdapter_Open_Close_Menus);
		labelConsoleBack.addMouseListener(mouseAdapter_Open_Close_Menus);
		labelOpenTable.addMouseListener(mouseAdapter_Open_Close_Menus);
		labelTableBack.addMouseListener(mouseAdapter_Open_Close_Menus);
		labelOpenSettings.addMouseListener(mouseAdapter_Open_Close_Menus);
		labelSettingsBack.addMouseListener(mouseAdapter_Open_Close_Menus);
		labelShowRoomsInTable.addMouseListener(mouseAdapter_Open_Close_Menus);
		labelShowLinksInTable.addMouseListener(mouseAdapter_Open_Close_Menus);
		labelFileMenu_Open.addMouseListener(mouseAdapter_Open_Close_Menus);
		
		labelAddRoom.addMouseListener(mouseAdapter_Confirm);
		labelAddLink.addMouseListener(mouseAdapter_Confirm);
		labelSearch.addMouseListener(mouseAdapter_Confirm);
		
		openData("src/example.blegh");
	} // Constructor [  END  ]
	
	private void fixThisTrashSwing() {
		while (this.getWidth() > side+this.getInsets().left+this.getInsets().right) { // pack() fix
			this.pack();
			//System.out.println("packing, width = " + this.getWidth() + " | height = " + this.getHeight() + " | side = " + side);
	    }
		this.setLocationRelativeTo(null);
		this.setVisible(true);
	} // fixThisTrashSwing() [  END  ]
	
	private MouseAdapter mouseAdapter_Open_Close_Menus = new MouseAdapter() {
		@Override
        public void mouseClicked(MouseEvent e) {
    		Object source = e.getSource();
    		if(labelOpenLeftMenu == source) { // open panelFileMenu
    			labelOpenLeftMenu.setVisible(false);
    			panelFileMenu.setVisible(true);
    		}
    		else if(labelFileMenu_Back == source) { // close panelFileMenu
    			panelFileMenu.setVisible(false);
    			labelOpenLeftMenu.setVisible(true);
    		}
    		else if(labelOpenTopMenu == source) { // open panelTopMenuWrap
    			labelOpenTopMenu.setVisible(false);
    			panelTopMenuWrap.setVisible(true);
    		}
    		else if(labelTopMenu_Back == source) { // close panelTopMenuWrap
    			panelTopMenuWrap.setVisible(false);
    			labelOpenTopMenu.setVisible(true);
    		}
    		else if(labelChoice_AddRoom == source) { // open panelAddRoom
    			labelChoice_AddRoom.setBackground(buttonGreenDark);
    			labelChoice_AddRoom.setForeground(textWhite);
    			
    			labelChoice_AddLink.setBackground(buttonGreenLight);
    			labelChoice_AddLink.setForeground(textGreen1);
    			labelChoice_Search.setBackground(buttonGreenLight);
    			labelChoice_Search.setForeground(textGreen1);
    			
    			panelAddLink.setVisible(false);
    			panelSearch.setVisible(false);
    			panelAddRoom.setVisible(true);
    		}
    		else if(labelChoice_AddLink == source) { // open panelAddLink
    			labelChoice_AddLink.setBackground(buttonGreenDark);
    			labelChoice_AddLink.setForeground(textWhite);
    			
    			labelChoice_AddRoom.setBackground(buttonGreenLight);
    			labelChoice_AddRoom.setForeground(textGreen1);
    			labelChoice_Search.setBackground(buttonGreenLight);
    			labelChoice_Search.setForeground(textGreen1);
    			
    			panelSearch.setVisible(false);
    			panelAddRoom.setVisible(false);
    			panelAddLink.setVisible(true);
    		}
    		else if(labelChoice_Search == source) { // open panelSearch
    			labelChoice_Search.setBackground(buttonGreenDark);
    			labelChoice_Search.setForeground(textWhite);
    			
    			labelChoice_AddRoom.setBackground(buttonGreenLight);
    			labelChoice_AddRoom.setForeground(textGreen1);
    			labelChoice_AddLink.setBackground(buttonGreenLight);
    			labelChoice_AddLink.setForeground(textGreen1);
    			
    			panelAddRoom.setVisible(false);
    			panelAddLink.setVisible(false);
    			panelSearch.setVisible(true);
    		}
    		else if(labelOpenConsole == source) { // open panelConsoleWrap
    			labelOpenConsole.setVisible(false);
    			panelConsoleWrap.setVisible(true);
    		}
    		else if(labelConsoleBack == source) { // close panelConsoleWrap
    			panelConsoleWrap.setVisible(false);
    			labelOpenConsole.setVisible(true);
    		}
    		else if(labelOpenTable == source) { // open panelTableWrap
    			labelOpenTable.setVisible(false);
    			panelTableWrap.setVisible(true);
    		}
    		else if(labelTableBack == source) { // close panelTableWrap
    			panelTableWrap.setVisible(false);
    			labelOpenTable.setVisible(true);
    		}
    		else if(labelOpenSettings == source) { // open panelSettingsWrap
    			labelOpenSettings.setVisible(false);
    			panelSettingsWrap.setVisible(true);
    		}
    		else if(labelSettingsBack == source) { // close panelSettingsWrap
    			panelSettingsWrap.setVisible(false);
    			labelOpenSettings.setVisible(true);
    		}
    		else if(labelShowRoomsInTable == source) { // update Table with rooms
    			updateTableWithRooms();
    		}
    		else if(labelShowLinksInTable == source) { // update Table with links
    			updateTableWithLinks();
    		}
    		else if(labelFileMenu_Open == source) { // show file chooser
    			JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());

    			int returnValue = jfc.showOpenDialog(null);

    			if (returnValue == JFileChooser.APPROVE_OPTION) {
    				File selectedFile = jfc.getSelectedFile();
    				openData(selectedFile.getAbsolutePath());
    			}
    		}
        }
    	@Override
        public void mouseEntered(MouseEvent e) {
            super.mouseEntered(e);
            Object source = e.getSource();
        }
        @Override
        public void mouseExited(MouseEvent e) {
            super.mouseExited(e); 
            Object source = e.getSource();
        }
	};
	
	private MouseAdapter mouseAdapter_Confirm = new MouseAdapter() {
		@Override
        public void mouseClicked(MouseEvent e) {
    		Object source = e.getSource();
    		if(labelAddRoom == source) {
    			tryToAddRoom();
    		}
    		else if(labelAddLink == source) {
    			tryToAddLink();
    		}
    		else if(labelSearch == source) {
    			
    			String fromRoom = JTF_searchFrom.getText().trim(), toRoom = JTF_searchTo.getText().trim();
    			
    			if(fromRoom.isEmpty() || toRoom.isEmpty()) {
    				log("! Please, fill all the fields.", 2);
    				return;
    			}
    			if(!graph.containsRoom(fromRoom)) {
    				log("! Room [ " + fromRoom + " ] doesn't exist.", 2);
    				return;
    			}
    			if(!graph.containsRoom(toRoom)) {
    				log("! Room [ " + toRoom + " ] doesn't exist.", 2);
    				return;
    			}
    			
    			if(fromRoom.equals(toRoom)) {
    				log("! The names of the rooms entered are the same.", 2);
    				return;
    			}
    			
    			String searchChoice = JCB_searchType.getSelectedItem().toString();
    			if(searchChoice == "no stairs") { // no stairs
    				searchWithoutStairs(fromRoom, toRoom);
    			} else if(searchChoice == "by coords") { // by coords
    				searchByCoords(fromRoom, toRoom);
    			} else { // lifts prior
    				searchPrioriryLift(fromRoom, toRoom);
    			}
    		}
        }
    	@Override
        public void mouseEntered(MouseEvent e) {
            super.mouseEntered(e);
            Object source = e.getSource();
            if(labelAddRoom == source) {
            	labelAddRoom.setForeground(textBlueLightDarker1);
        		labelAddRoom.setBackground(Color.WHITE);
        		labelAddRoom.setBorder(BorderFactory.createMatteBorder(3, 3, 3, 3, blueDarker2));
            }
            else if(labelAddLink == source) {
            	labelAddLink.setForeground(textBlueLightDarker1);
            	labelAddLink.setBackground(Color.WHITE);
            	labelAddLink.setBorder(BorderFactory.createMatteBorder(3, 3, 3, 3, blueDarker2));
    		}
            else if(labelSearch == source) {
            	labelSearch.setForeground(textBlueLightDarker1);
            	labelSearch.setBackground(Color.WHITE);
            	labelSearch.setBorder(BorderFactory.createMatteBorder(3, 3, 3, 3, blueDarker2));
            }
        }
        @Override
        public void mouseExited(MouseEvent e) {
            super.mouseExited(e); 
            Object source = e.getSource();
            if(labelAddRoom == source) {
            	labelAddRoom.setForeground(textBlueLight1);
        		labelAddRoom.setBackground(Color.WHITE);
        		labelAddRoom.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, textBlueLightDarker1));
            }
            else if(labelAddLink == source) {
            	labelAddLink.setForeground(textBlueLight1);
            	labelAddLink.setBackground(Color.WHITE);
            	labelAddLink.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, textBlueLightDarker1));
    		}
            else if(labelSearch == source) {
            	labelSearch.setForeground(textBlueLight1);
            	labelSearch.setBackground(Color.WHITE);
            	labelSearch.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, textBlueLightDarker1));
            }
        }
	};
	
	private Popup popup;
	private HashMap<String, Boolean> listLoadedFiles = new HashMap<String, Boolean>(); // max 13 files because im too lazy to fix the scroller over the panel with null layout
	private int lastFileinMenu_Y = 0;
	private void openData(String pathToFile) { // openData() [ START ]
		
		File file = new File(pathToFile);
		if(!file.exists()) {
			log("! File [ " + pathToFile + " ] doesn't exist.", 2);
			return;
		}
		
		if(!listLoadedFiles.containsKey(pathToFile) && listLoadedFiles.size() < 13) {
			listLoadedFiles.put(pathToFile, true);
			// adding button in the menu
			JLabel fileLabel = new JLabel(new ImageIcon(new ImageIcon("src/icon_file.png").getImage().getScaledInstance(side_div_15, side_div_15, Image.SCALE_DEFAULT)));
			fileLabel.setSize(side_div_15, side_div_15);
			fileLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			panelFileMenu_Container.setSize(side_div_15, lastFileinMenu_Y+side_div_15);
			panelFileMenu_Container.add(fileLabel);
			fileLabel.setLocation(0, lastFileinMenu_Y);
			lastFileinMenu_Y+=side_div_15;

			fileLabel.addMouseListener(new MouseAdapter() {
				@Override
		        public void mouseClicked(MouseEvent e) {
					openData(pathToFile);
		        }
		    	@Override
		        public void mouseEntered(MouseEvent e) {
		            super.mouseEntered(e);
		            JLabel text = new JLabel(pathToFile);
	                popup = PopupFactory.getSharedInstance().getPopup(e.getComponent(), text, e.getXOnScreen(), e.getYOnScreen());
	                popup.show();
		        }
		        @Override
		        public void mouseExited(MouseEvent e) {
		            super.mouseExited(e); 
		            if (popup != null) {
	                    popup.hide();
	                }
		        }
			});
		}
		
		log("> Loading: " + pathToFile, 1);
		file_path = pathToFile;
		
		graph = new Graph();
		
		ArrayList<String[]> allLines = new ArrayList<String[]>();
		
		try (Scanner scanner = new Scanner( file, "UTF-8" )) {
			while(scanner.hasNext()) {
				String text = scanner.useDelimiter(";").next(); // reading to ';'
			    allLines.add(text.replace(System.lineSeparator(), "").split(",")); // removing the line separator if existing and splitting the line on ','
			}
		} catch(Exception e) {
			log(e.getMessage(), 2);
		}
		
		ArrayList<Link> links = new ArrayList<Link>();
		
		for(String[] s : allLines) {
			
			if(s.length != 5) { // the line must have exactly 5 elements - name, x, y, floor and type - anything else is wrong information
				log("Грешка при четене на ред: " + Arrays.toString(s), 2);
				continue;
			}
			
			for(int i=0; i<5; i++) {
				s[i] = s[i].trim(); // we splitted the lines on ',' but we don't know if and where is the white space so we just trim it
			}
			
			if(s[4].equals("room") || s[4].equals("transit")) { // we have a room
				graph.addRoom(new Room(s[0], Integer.parseInt(s[1]), Integer.parseInt(s[2]), Integer.parseInt(s[3]), s[4]));
				int current_floor = Integer.parseInt(s[3]);
				if(current_floor > max_floor) max_floor = current_floor; // finding the max floor
				if(current_floor < min_floor) min_floor = current_floor; // finding the min floor
        	} else if(s[4].equals("yes") || s[4].equals("no")) { // we have a link
        		boolean isTwoWay = false;
        		if(s[4].equals("yes")) isTwoWay = true;
        		links.add(new Link(s[0], s[1], s[2], Integer.parseInt(s[3]), isTwoWay));
        	}
		}
		
		// Links have to be loaded after the rooms, because we don't know if they are writed in the right way in the file.
		for(Link l: links) {
			graph.addLink(l.fromRoomName, l.toRoomName, l.type, l.cost, l.isTwoWay);
		}
		
		log("* Loaded: " + pathToFile, 3);
		
		updateTableWithRooms();
		loadGraphByFloors();
		this.setTitle(pathToFile);
	} // openData() [  END  ]
	
	private void log(String log, int color) { // log() [ START ]
		
		SimpleAttributeSet red = new SimpleAttributeSet();
		StyleConstants.setForeground(red, Color.decode("#e04a3a"));
		SimpleAttributeSet white = new SimpleAttributeSet();
		StyleConstants.setForeground(white, Color.WHITE);
		SimpleAttributeSet green = new SimpleAttributeSet();
		StyleConstants.setForeground(green, Color.decode("#50ce6b"));
		SimpleAttributeSet blue = new SimpleAttributeSet();
		StyleConstants.setForeground(blue, Color.decode("#59a1ff"));
		
		Document doc = JTA_Console.getDocument();
		
		switch(color) {
		case 2:
			try {
				doc.insertString(doc.getLength(), log + System.lineSeparator(), red);
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
			break;
		case 3:
			try {
				doc.insertString(doc.getLength(), log + System.lineSeparator(), green);
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
			break;
		case 4:
			try {
				doc.insertString(doc.getLength(), log + System.lineSeparator(), blue);
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
			break;
		default:
			try {
				doc.insertString(doc.getLength(), log + System.lineSeparator(), white);
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
			break;
		}
		
	} // log() [  END  ]
	
	private void tryToAddRoom() { // tryToAddRoom() [ START ]
		String roomName = JTF_addRoom_Name.getText().trim(), roomX = JTF_addRoom_X.getText().trim(), roomY = JTF_addRoom_Y.getText().trim(), roomFloor = JTF_addRoom_Floor.getText().trim(), roomType = String.valueOf(JCB_addRoom_Type.getSelectedItem());
		// 1st - check if any of the fields is empty
		if(roomName.isEmpty() || roomX.isEmpty() || roomY.isEmpty() || roomFloor.isEmpty()) {
			log("! Please, fill all the fields.", 2);
			return;
		}
		
		//2nd - check if the room is added in the graph
		String error = graph.addRoom(new Room(roomName, Integer.parseInt(roomX), Integer.parseInt(roomY), Integer.parseInt(roomFloor), roomType));
		if(error != null) {
			log(error, 2);
		} else { //3rd - save the room in the file
			try {
		        File file = new File(file_path);
		        if (!file.exists()) {
		        	log("! Looks like the last loaded file is missing from the file system.", 2);
		        } else {
		        	FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
			        BufferedWriter bw = new BufferedWriter(fw);
			        bw.write(System.lineSeparator() + roomName + ", " + roomX + ", " + roomY + ", " + roomFloor + ", " + roomType + ";");
			        bw.close();
			        fw.close();
		        }
		    } catch(Exception e) { 
		    	log(e.getMessage(), 2);
		    }
			int current_floor = Integer.parseInt(roomFloor);
			if(current_floor > max_floor) max_floor = current_floor;
			if(current_floor < min_floor) min_floor = current_floor;
			log("* Room added successfully [ " + roomName + " ].", 3);
			updateTableWithRooms();
			//updateSketch(); <-- loadGraphByFloors();
		}
	} // tryToAddRoom() [  END  ]
	
	private void tryToAddLink() { // tryToAddLink() [ START ]
		String roomNameFrom = JTF_addLink_From.getText().trim(), roomNameTo = JTF_addLink_To.getText().trim(), linkType = String.valueOf(JCB_addLink_Type.getSelectedItem()), linkCost = JTF_addLink_Cost.getText().trim(), linkTwoWay = String.valueOf(JCB_addLink_Way.getSelectedItem());
		if(roomNameFrom.isEmpty() || roomNameTo.isEmpty() || linkCost.isEmpty()) {
			log("! Please, fill all the fields.", 2);
			return;
		}
		
		String error = graph.addLink(roomNameFrom, roomNameTo, linkType, Integer.parseInt(linkCost.replace(",", ".")), linkTwoWay.equals("two-way"));
		if(error != null) {
			log(error, 2);
		} else {
			try {
		        File file = new File(file_path);
		        if (!file.exists()) {
		        	log("! Looks like the last loaded file is missing from the file system.", 2);
		        } else {
		        	FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
			        BufferedWriter bw = new BufferedWriter(fw);
			        String twoWay = "";
			        if(linkTwoWay.equals("two-way")) twoWay = "yes";
			        else twoWay = "no";
			        bw.write(System.lineSeparator() + roomNameFrom + ", " + roomNameTo + ", " + linkType + ", " + linkCost.replace(",", ".") + ", " + twoWay + ";");
			        bw.close();
			        fw.close();
		        }
		    } catch(Exception e) { 
		    	log(e.getMessage(), 2);
		    }
			log("* Link added [ " + roomNameFrom + " to " + roomNameTo + " ].", 3);
			updateTableWithLinks();
		}
		
	} // tryToAddLink() [  END  ]
	
	private void loadGraphByFloors() { // loadGraphByFloors() [ START ]
		grap_by_floor = new Graph[max_floor+1];
		
		Set entries = graph.myGraph.entrySet();
		Iterator entriesIterator = entries.iterator();

		Map.Entry mapping;
		while(entriesIterator.hasNext()){
			mapping = (Map.Entry) entriesIterator.next();
			Room room = (Room) mapping.getValue();
			if(grap_by_floor[room.floor] == null) grap_by_floor[room.floor] = new Graph();
			grap_by_floor[room.floor].addRoom(room);
		}
		String[] floors = new String[grap_by_floor.length-1];
		for(int i=0; i<floors.length; i++) {
			floors[i] = new String( (i+1)+"" );
		}
		JCB_floorChooser.setModel(new DefaultComboBoxModel(floors));
	} // loadGraphByFloors() [  END  ]
	
	private void updateTableWithRooms() {
		Object[][] data = new Object[graph.myGraph.size()][5];
			
		Set entries = graph.myGraph.entrySet();
		Iterator entriesIterator = entries.iterator();

		int i = 0;
		Map.Entry mapping;
		while(entriesIterator.hasNext()){
			mapping = (Map.Entry) entriesIterator.next();
			Room room = (Room) mapping.getValue();
			data[i][0] = mapping.getKey();
			data[i][1] = room.x;
			data[i][2] = room.y;
			data[i][3] = room.floor;
			data[i][4] = room.type;
			i++;
		}
		
		JT_Table.setModel(new DefaultTableModel(data, new String[] {"# Room", "X", "Y", "# Floor", "Type"}));
		MainFrame.this.revalidate();
		MainFrame.this.repaint();
	}
	private void updateTableWithLinks() {
		Set entries = graph.myGraph.entrySet();
		Iterator entriesIterator = entries.iterator();
			
		ArrayList<Link> allLinks = new ArrayList<>();
			
		int i = 0;
		Map.Entry mapping;
		while(entriesIterator.hasNext()){
			mapping = (Map.Entry) entriesIterator.next();
			Room room = (Room) mapping.getValue();
			for(Link l : room.links) {
				allLinks.add(l);
			}
			i++;
		}
		
		Object[][] data = new Object[allLinks.size()][5];
		i=0;
		while(i<allLinks.size()) {
			Link link = allLinks.get(i);
			data[i][0] = link.fromRoomName;
			data[i][1] = link.toRoomName;
			data[i][2] = link.type;
			data[i][3] = link.cost;
			if(link.isTwoWay) data[i][4] = "yes";
			else data[i][4] = "no";
			i++;
		}
			
		JT_Table.setModel(new DefaultTableModel(data, new String[] {"From # Room", "To # Room", "Type", "Cost", "Two-way?"}));
		MainFrame.this.revalidate();
		MainFrame.this.repaint();
	}
	
	private void searchWithoutStairs(String fromRoom, String toRoom) { // searchWithoutStairs() [ START ]
		
		Room startRoom = graph.getRoom(fromRoom), endRoom = graph.getRoom(toRoom);
		
		Room currentRoom = startRoom;
		log("@ Searching started [ Without Stairs ]", 1);
		searchWithoutStairsExtend(currentRoom, null, endRoom.name);
		
		if(isPathFound) {
			log("* There is path without stairs from " + startRoom.name + " to " + endRoom.name + ".", 4);
			
			ArrayList<String> path = new ArrayList<String>();
			ArrayList<Integer> costs = new ArrayList<Integer>();
			int costCounter = 0;
			int walk=0, lift=0;
			
			String name = endRoom.name;
			while(!name.equals(startRoom.name)) {
				path.add(name);
				LinkSearch linkSearch = comingFrom.get(name);
				if(linkSearch.type.equals("walk")) walk++;
				else lift++;
				costs.add(linkSearch.cost);
				costCounter+=linkSearch.cost;
				name = linkSearch.from;
			}
			path.add(startRoom.name);
			String pathString = "| ";
			for(int i= path.size()-1; i>-1; i--) {
				pathString+=path.get(i) + " | ";
			}
			log("* Full path: " + pathString, 4);
			int costIndex = 0;
			log("* Cost:", 1);
			for(int i= path.size()-1; i>0; i--) {
				log("- From [ " + path.get(i) + " ] to [ " + path.get(i-1) + " ] : " + costs.get(costIndex++), 1);
			}
			log("* [ Full cost x" + costCounter + " ] [ Walk x" + walk + " ] [ Lift x" + lift + " ]", 4);
		} else {
			log("* There is no path without stairs from " + startRoom.name + " to " + endRoom.name + ".", 2);
		}
		log("@ Search finished [ Without Stairs ]", 1);
		
		//Cleaning the current search
		flagged = new HashMap<String, Boolean>();
		comingFrom = new HashMap<String, LinkSearch>();
		isPathFound = false;
		
	} // searchWithoutStairs() [  END  ]
	private void searchWithoutStairsExtend(Room currentRoom, Link comingFromLink, String endRoomName) { // searchWithoutStairsExtend() [ START ]
		if(isPathFound) return; // not sure if this prevents anything anymore
		
		log(" + Observing room [ " + currentRoom.name + " ]", 1);
		
		if(comingFromLink != null) comingFrom.put(currentRoom.name, new LinkSearch(comingFromLink.fromRoomName, comingFromLink.cost, comingFromLink.type));
		flagged.put(currentRoom.name, true);
		for(Link l : currentRoom.links) {
			if(isPathFound) return; // little optimization
			if(!l.type.equals("climb")) {
				if(l.toRoomName.equals(endRoomName)) {
					log(" + Observing room [ " + endRoomName + " ]", 1);
					isPathFound = true;
					comingFrom.put(endRoomName, new LinkSearch(currentRoom.name, l.cost, l.type));
					return;
				}
				if(!flagged.containsKey(l.toRoomName)) searchWithoutStairsExtend(graph.getRoom(l.toRoomName), l, endRoomName);
			}
		}
	} // searchWithoutStairsExtend() [  END  ]
	
	private void searchByCoords(String fromRoom, String toRoom) {
		
		Room startRoom = graph.getRoom(fromRoom), endRoom = graph.getRoom(toRoom);
		
		Room currentRoom = startRoom;
		log("@ Searching started [ By Coords ]", 1);
		searchByCoordsExtend(currentRoom, null, endRoom);
		
		if(isPathFound) {
			log("* There is path from " + startRoom.name + " to " + endRoom.name + ".", 4);
			
			ArrayList<String> path = new ArrayList<String>();
			ArrayList<Integer> costs = new ArrayList<Integer>();
			int costCounter = 0, walk=0, climb=0, lift=0;
			
			String name = endRoom.name;
			while(!name.equals(startRoom.name)) {
				path.add(name);
				LinkSearch linkSearch = comingFrom.get(name);
				if(linkSearch.type.equals("walk")) walk++;
				else if(linkSearch.type.equals("climb")) climb++;
				else lift++;
				costs.add(linkSearch.cost);
				costCounter+=linkSearch.cost;
				name = linkSearch.from;
			}
			path.add(startRoom.name);
			String pathString = "| ";
			for(int i= path.size()-1; i>-1; i--) { // reverse it to to show it in the right order
				pathString+=path.get(i) + " | ";
			}
			log("* Full path: " + pathString, 4);
			int costIndex = 0;
			log("* Cost:", 1);
			for(int i= path.size()-1; i>0; i--) {
				log("- From [ " + path.get(i) + " ] to [ " + path.get(i-1) + " ] : " + costs.get(costIndex++), 1);
			}
			log("* [ Full cost = " + costCounter + " ] [ Walk x" + walk + " ] [ Stair x" + climb + " ] [ Lift x" + lift + " ]", 4);
		} else {
			log("* There is no path  from " + startRoom.name + " to " + endRoom.name + ".", 2);
		}
		log("@ Search finished [ By Coords ]", 1);
		
		// Clear the current search
		flagged = new HashMap<String, Boolean>();
		comingFrom = new HashMap<String, LinkSearch>();
		isPathFound = false;
	}
	private void searchByCoordsExtend(Room currentRoom, Link comingFromLink, Room endRoom) { // с надеждата да не направи StackOverflow
		if(isPathFound) return; // not sure if this prevents anything anymore
		
		log(" + Observing room [ " + currentRoom.name + " ]", 1);
		
		if(comingFromLink != null) comingFrom.put(currentRoom.name, new LinkSearch(comingFromLink.fromRoomName, comingFromLink.cost, comingFromLink.type));
		flagged.put(currentRoom.name, true);
		
		Link[] currentLinks = new Link[currentRoom.links.size()];
		currentLinks = currentRoom.links.toArray(currentLinks);
		
		int n = currentLinks.length;  
        Link temp = null;  
        for(int i=0; i < n; i++) { // TURBO LINK SORT (clumsy bubble sort to sort the linked rooms by coords)
        	for(int j=1; j < (n-i); j++) {
        		Room r1 = graph.getRoom(currentLinks[j-1].toRoomName);
        		Room r2 = graph.getRoom(currentLinks[j].toRoomName);
        		
        		// if the linked room is on the same floor but not on the floor where is the endRoom we must check it's lenght to the current room
        		// if on the same floor as endRoom -> check lenght to endRoom
        		// using formula for vector lenght -> same as hypotenuse formula - 1:1
        		
        		// we must know the building architecture to make an optimal solution otherwise we must write really complicated code to make this task possible for a random building.
        		if(r1.floor == endRoom.floor && r2.floor == endRoom.floor) { // the rooms are on the same floor
        			if( Math.sqrt((endRoom.x-r1.x)*(endRoom.x-r1.x)+(endRoom.y-r1.y)*(endRoom.y-r1.y)) > Math.sqrt((endRoom.x-r2.x)*(endRoom.x-r2.x)+(endRoom.y-r2.y)*(endRoom.y-r2.y)) ) {
            			temp = currentLinks[j-1];
            			currentLinks[j-1] = currentLinks[j];
            			currentLinks[j] = temp;
            		}
        		}
        		else if(r2.floor == endRoom.floor) { // room 2 is on the same room as endRoom
        			temp = currentLinks[j-1];
        			currentLinks[j-1] = currentLinks[j];
        			currentLinks[j] = temp;
        		}
        		else if(r1.floor != endRoom.floor) { // both rooms are not on the same floor as endRoom
        			if(currentRoom.floor > endRoom.floor && r1.floor < currentRoom.floor && r1.floor < r2.floor) {
        				// stay on this index
        			}
        			else if(currentRoom.floor > endRoom.floor && r2.floor < currentRoom.floor && r2.floor < r1.floor) {
        				temp = currentLinks[j-1];
            			currentLinks[j-1] = currentLinks[j];
            			currentLinks[j] = temp;
        			}
        			else if(currentRoom.floor < endRoom.floor && r1.floor > currentRoom.floor && r1.floor > r2.floor) {
        				// stay on this index
        			}
        			else if(currentRoom.floor < endRoom.floor && r2.floor > currentRoom.floor && r2.floor > r1.floor) {
        				temp = currentLinks[j-1];
            			currentLinks[j-1] = currentLinks[j];
            			currentLinks[j] = temp;
        			}
        			else if(currentRoom.floor > endRoom.floor && r1.floor == currentRoom.floor && r2.floor > currentRoom.floor) {
        				// stay on this index
        			}
        			else if(currentRoom.floor > endRoom.floor && r2.floor == currentRoom.floor && r1.floor > currentRoom.floor) {
        				temp = currentLinks[j-1];
            			currentLinks[j-1] = currentLinks[j];
            			currentLinks[j] = temp;
        			}
        			else if(currentRoom.floor < endRoom.floor && r1.floor == currentRoom.floor && r2.floor < currentRoom.floor) {
        				// stay on this index
        			}
        			else if(currentRoom.floor < endRoom.floor && r2.floor == currentRoom.floor && r1.floor < currentRoom.floor) {
        				temp = currentLinks[j-1];
            			currentLinks[j-1] = currentLinks[j];
            			currentLinks[j] = temp;
        			}
        			else { // basic sort to the closest room
        				if( Math.sqrt((currentRoom.x-r1.x)*(currentRoom.x-r1.x)+(currentRoom.y-r1.y)*(currentRoom.y-r1.y)) > Math.sqrt((currentRoom.x-r2.x)*(currentRoom.x-r2.x)+(currentRoom.y-r2.y)*(currentRoom.y-r2.y)) ) {
                			temp = currentLinks[j-1];
                			currentLinks[j-1] = currentLinks[j];
                			currentLinks[j] = temp;
                		}
        			}
        		} // else r1.floor == endRoom.floor -> stay on same indexes
        		
        	}  
        }
        
        for(int i=0; i<n; i++) {
        	if(isPathFound) return; // little optimization
        	if(currentLinks[i].toRoomName.equals(endRoom.name)) {
        		log(" + Observing room [ " + currentRoom.name + " ]", 1);
				isPathFound = true;
				comingFrom.put(endRoom.name, new LinkSearch(currentRoom.name, currentLinks[i].cost, currentLinks[i].type));
				return;
			}
			if(!flagged.containsKey(currentLinks[i].toRoomName)) searchByCoordsExtend(graph.getRoom(currentLinks[i].toRoomName), currentLinks[i], endRoom);
        }
	}
	
	private void searchPrioriryLift(String fromRoom, String toRoom) {
		Room startRoom = graph.getRoom(fromRoom), endRoom = graph.getRoom(toRoom);
		
		Room currentRoom = startRoom;
		log("@ Searching started [ Prioriry Lift ]", 1);
		searchPrioriryLiftExtend(currentRoom, null, endRoom.name);
		
		if(isPathFound) {
			log("* There is path from " + startRoom.name + " to " + endRoom.name + ".", 4);
			
			ArrayList<String> path = new ArrayList<String>();
			ArrayList<Integer> costs = new ArrayList<Integer>();
			int costCounter = 0, walk=0, climb=0, lift=0;
			
			String name = endRoom.name;
			while(!name.equals(startRoom.name)) {
				path.add(name);
				LinkSearch linkSearch = comingFrom.get(name);
				if(linkSearch.type.equals("walk")) walk++;
				else if(linkSearch.type.equals("climb")) climb++;
				else lift++;
				costs.add(linkSearch.cost);
				costCounter+=linkSearch.cost;
				name = linkSearch.from;
			}
			path.add(startRoom.name);
			String pathString = "| ";
			for(int i= path.size()-1; i>-1; i--) { // reverse it to to show it in the right order
				pathString+=path.get(i) + " | ";
			}
			log("* Full path: " + pathString, 4);
			int costIndex = 0;
			log("* Cost:", 1);
			for(int i= path.size()-1; i>0; i--) {
				log("- From [ " + path.get(i) + " ] to [ " + path.get(i-1) + " ] : " + costs.get(costIndex++), 1);
			}
			log("* [ Full cost = " + costCounter + " ] [ Walk x" + walk + " ] [ Stair x" + climb + " ] [ Lift x" + lift + " ]", 4);
		} else {
			log("* There is no path  from " + startRoom.name + " to " + endRoom.name + ".", 2);
		}
		log("@ Search finished [ Prioriry Lift ]", 1);;
		
		// Clear the current search
		flagged = new HashMap<String, Boolean>();
		comingFrom = new HashMap<String, LinkSearch>();
		isPathFound = false;
	}
	
	private void searchPrioriryLiftExtend(Room currentRoom, Link comingFromLink, String endRoomName) {
		if(isPathFound) return; // not sure if does anything at this point
		
		log(" + Observing room [ " + currentRoom.name + " ]", 1);
		
		if(comingFromLink != null) comingFrom.put(currentRoom.name, new LinkSearch(comingFromLink.fromRoomName, comingFromLink.cost, comingFromLink.type));
		flagged.put(currentRoom.name, true);
		
		ArrayList<Link> links = new ArrayList<Link>();
		for(Link l : currentRoom.links) { // ordering the links -> lifts
			if(l.type.equals("lift")) {
				links.add(0, l); // going first in the list
			} else links.add(l); // adding it behind
		}
		
		ArrayList<Link> skippedStairs = new ArrayList<Link>();
		for(Link l : links) { // работим с подредените линкове
			if(isPathFound) return;
			int cost = l.cost;
			if(l.type.equals("climb")) {
				cost *= 2; 
			}
			
			if(l.toRoomName.equals(endRoomName)) {
				log(" + Observing room [ " + currentRoom.name + " ]", 1);
				isPathFound = true;
				comingFrom.put(endRoomName, new LinkSearch(currentRoom.name, cost, l.type));
				return;
			}
			
			if(l.type.equals("climb")) skippedStairs.add(l); // we leave the stairs always for last (last resort)
			else if(!flagged.containsKey(l.toRoomName)) searchPrioriryLiftExtend(graph.getRoom(l.toRoomName), l, endRoomName);
		}
		
		for(Link l: skippedStairs) {
			if(isPathFound) return;
			int cost = l.cost*2;
			
			if(l.toRoomName.equals(endRoomName)) {
				log(" + Observing room [ " + currentRoom.name + " ]", 1);
				isPathFound = true;
				comingFrom.put(endRoomName, new LinkSearch(currentRoom.name, cost, l.type));
				return;
			}
			
			if(!flagged.containsKey(l.toRoomName)) searchPrioriryLiftExtend(graph.getRoom(l.toRoomName), l, endRoomName);
		}
		
	}

} // MainFrame{} [  END  ]