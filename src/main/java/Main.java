import static org.junit.Assert.assertEquals;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.junit.Test;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;


class Main extends JPanel implements ActionListener  {
	@Argument
	private List<String> folders = new ArrayList<String>();
	String [] file =  new String[1];
	EclipseAstParser parser = new EclipseAstParser();

	JButton go;
	   JButton openButton, runButton;
	   JTextArea project;
	   JTextArea log;
	   
	   static private final String newline = "\n";
	   
	   JFileChooser chooser;
	   String choosertitle;
	   
	  public Main() 
	  {
		  
		  project = new JTextArea(2, 30);
		  project.setMargin(new Insets(5, 5, 5, 5));
		  project.setEditable(false);
		  JScrollPane projectScrollPane = new JScrollPane(project);
		  
		  log = new JTextArea(10, 50);
		  log.setMargin(new Insets(5, 5, 5, 5));
		  log.setEditable(true);	
		  JScrollPane logScrollPane = new JScrollPane(log);
		  
		  openButton = new JButton("Open a File...");
		  openButton.addActionListener(this);

		  runButton = new JButton("Run...");
		  runButton.addActionListener(this);
		  
		  JPanel buttonPanel = new JPanel();
		  //buttonPanel.setSize(20, 15);
		  buttonPanel.add(openButton);
		  buttonPanel.add(runButton);

		  add(buttonPanel, BorderLayout.PAGE_START);
		  add(projectScrollPane, BorderLayout.CENTER);
		  add(logScrollPane, BorderLayout.CENTER);
//		  MessageConsole mc = new MessageConsole(log);
//		  mc.redirectOut();
//		  mc.redirectErr(Color.RED, null);
//		  mc.setMessageLines(100);
		  
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
	        	project.append( chooser.getSelectedFile() + newline);
	        	//System.out.println("getSelectedFile() : " + chooser.getSelectedFile() + newline);
	        	file[0]=chooser.getSelectedFile().toString();
	    	}
	    	
	    } 
	    else if (e.getSource() == runButton) 
	    {
	  		try {
				doMain(file);
			} catch (CmdLineException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
	  		        
	    }
	    else 
	    {
	    	project.append("Nothing Selected");
	    	System.out.println("No Selection ");
	    }
	  }
	   
	  public Dimension getPreferredSize()
	  {
	    return new Dimension(590, 310);
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
			{
				//System.out.format("%s: %s\n", visitor.getKlass(), method.getName());
				String junitTest = visitor.getKlass()+ " tester = new " + visitor.getKlass()+ "();\n\n"
					+ "@Test\n"
					+ "public void test" + method.getName() + "(";
					if(method.parameters().toString().length()!=2)
						junitTest+= setParameters(method.parameters().toString().substring(method.parameters().
							toString().indexOf("[")+1,method.parameters().
							toString().indexOf(" ")),method.parameters().size());
					else
						junitTest+= setParameters(method.parameters().toString().substring(method.parameters().
								toString().indexOf("[")+1,method.parameters().
								toString().indexOf("]")),method.parameters().size());
				
					junitTest+= ")\n"
					+ "{\n"
					+ "\t" + "assertEquals( \"error\", [Value To Test], tester." + method.getName() + "(" + setSendParameters(method.parameters().size()) + ");\n"
					+ "}\n";
				log.append(junitTest);
				}
		}
	}
	
	public String setParameters(String parameterType, int count)
	{
		String parameters = "";
		for(int i = 0; i<count; i++)
		 {
		 		parameters += parameterType + " a" + i; 
		 		if(i!=count-1)
		 			parameters += ",";
		 }
		return parameters;
	}
	
	public String setSendParameters(int count)
	{
		String parameters = "";
		for(int i = 0; i<count; i++)
		 {
		 		parameters += " a" + i; 
		 		if(i!=count-1)
		 			parameters += ",";
		 }
		return parameters;
	}

	public void doMain(String[] args) throws CmdLineException, IOException {
		CmdLineParser argParser = new CmdLineParser(this);
		argParser.parseArgument(args);

		log.append( "import static org.junit.Assert.*;\n"
        		+ "import org.junit.Test;\n\n"
        		+ "public class test {\n\n");
		for (String folder : folders) {
			visitFile(new File(folder));
		}
		
		log.append("}");
		
		
	}
	public static void main(String[] args) throws IOException, CmdLineException {
		JFrame frame = new JFrame("");
		frame.setSize(20, 15);
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
