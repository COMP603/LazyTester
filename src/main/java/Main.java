import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;


class Main extends JPanel implements ActionListener  {
	@Argument
	private List<String> folders = new ArrayList<String>();
	String [] file =  new String[1];
	EclipseAstParser parser = new EclipseAstParser();

	JButton go;
	   JButton openButton, saveButton;
	   JTextArea log;
	   
	   static private final String newline = "\n";
	   
	   JFileChooser chooser;
	   String choosertitle;
	   
	  public Main() 
	  {
		  
		  log = new JTextArea(5, 20);
		  log.setMargin(new Insets(5, 5, 5, 5));
		  log.setEditable(false);	
		  JScrollPane logScrollPane = new JScrollPane(log);
		  
		  openButton = new JButton("Open a File...");
		  openButton.addActionListener(this);

		  saveButton = new JButton("Run...");
		  saveButton.addActionListener(this);
		  
		  JPanel buttonPanel = new JPanel();
		  buttonPanel.add(openButton);
		  buttonPanel.add(saveButton);

		  add(buttonPanel, BorderLayout.PAGE_START);
		  add(logScrollPane, BorderLayout.CENTER);
		  
	   }

	  public void actionPerformed(ActionEvent e) 
	  {
	        
	    chooser = new JFileChooser(); 
	    chooser.setCurrentDirectory(new java.io.File("."));
	    chooser.setDialogTitle(choosertitle);
	    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	    chooser.setAcceptAllFileFilterUsed(false);
	    
	    if (e.getSource() == openButton) 
	    {
	    	if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) 
	    	{ 
	        	log.append("getCurrentDirectory(): " + chooser.getSelectedFile() + newline);
	        	System.out.println("getSelectedFile() : " + chooser.getSelectedFile() + newline);
	        	file[0]=chooser.getSelectedFile().toString();
	    	}
	    	
	    } 
	    else if (e.getSource() == saveButton) 
	    {
	  		try {
				doMain(file);
			} catch (CmdLineException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
	  		TestWindow test = new TestWindow();
	  		test.open();
	    }
	    else 
	    {
	    	log.append("Nothing Selected");
	    	System.out.println("No Selection ");
	    }
	  }
	   
	  public Dimension getPreferredSize()
	  {
	    return new Dimension(200, 200);
	  }
	
	public void visitFile(File f) throws IOException {
		if (f.isDirectory()) {
			for (File child : f.listFiles()) {
				visitFile(child);
			}
		}
		if (f.getName().endsWith(".java")) {
			CompilationUnit ast = parser.getAST(f);
	        // AstVisitor extends org.eclipse.jdt.core.dom.ASTVisitor
	        AstVisitor visitor = new AstVisitor();
	        ast.accept( visitor );

			for (MethodDeclaration method : visitor.methods)
				System.out.format("%s.%s: %s\n", visitor.pakage, visitor.getKlass(), method.getName());
			
		}
	}

	public void doMain(String[] args) throws CmdLineException, IOException {
		CmdLineParser argParser = new CmdLineParser(this);
		argParser.parseArgument(args);

		for (String folder : folders) {
			visitFile(new File(folder));
		}
	}
	public static void main(String[] args) throws IOException, CmdLineException {
		JFrame frame = new JFrame("");
	    Main panel = new Main();
	    frame.addWindowListener
	    (
	      new WindowAdapter() 
	      {
	        public void windowClosing(WindowEvent e) 
	        {
	          System.exit(0);
	        }
	      }
	    );
	    frame.getContentPane().add(panel,"Center");
	    frame.setSize(panel.getPreferredSize());
	    frame.setVisible(true);
	}
}
