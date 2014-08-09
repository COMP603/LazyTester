  

import java.awt.BorderLayout;
import java.awt.event.*;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;

public class TestWindow extends JFrame {

        /**
     * 
     */
    private static final long serialVersionUID = 1L;
        int count = 0;
        Container content = getContentPane();

        private JTextComponent[] textComp;
        private JLabel[] textLabel;
        // Create an editor.
        public TestWindow() {
                super("Swing Editor");
                dinamicTA();
                content.setLayout(new FlowLayout());

                for(int i=0;i<count;i++) {
                		content.add(textLabel[i]);
                        content.add(textComp[i]);
                }

                pack();
                content.setSize(content.getPreferredSize());
                pack();
        }

        //create DINAMIC TEXT AREA
        public void dinamicTA () {
                if(count==0) {
                        textComp = new JTextComponent[1];
                        textLabel = new JLabel[1];
                        textComp[0] = createTextComponent();
                        textLabel[0] = createLabel();
                        count+=1;
                }
                else {
                        JTextComponent[] texttemp;
                        JLabel[] labelTemp;
                        texttemp = textComp;
                        labelTemp = textLabel;
                        count+=1;
                        textComp = new JTextComponent[count];
                        textLabel = new JLabel[count];
                        for(int i=0;i<count-1;i++) {
                        		textLabel[i] = labelTemp[i];
                        		textLabel[i].setText("Generation Test #" + i);
                                textComp[i] = texttemp[i];
                                textComp[i].setText(textComp[i].getText()+"wow"); //<-- not working
                        }
                        
                        textComp[count-1] = createTextComponent();
                        textLabel[count-1] = createLabel();
                        content.add(textLabel[count-1]);
                        content.add(textComp[count-1]);
                        textLabel[count-1].requestFocus();
                        textComp[count-1].requestFocus(); //get focus
                }
        }

        // Create the JTextComponent subclass.
        protected JTextComponent createTextComponent() {
                final JTextArea ta = new JTextArea();
                if (count%2==0)
                        ta.setForeground(Color.red);
                else
                        ta.setForeground(Color.GREEN);
                ta.setFont(new Font("Courier New",Font.PLAIN,12));
                ta.setLineWrap(true);                                                                                                                           
                ta.setWrapStyleWord(true);  
                ta.addKeyListener(new java.awt.event.KeyAdapter() {
                        public void keyReleased(java.awt.event.KeyEvent ev) {
                                taKeyReleased(ev);
                        }
                });

                ta.setColumns(15);
                pack();
                ta.setSize(ta.getPreferredSize());
                pack();

                return ta;
        }
        
        protected JLabel createLabel() {
            final JLabel ta = new JLabel();
            ta.setFont(new Font("Courier New",Font.PLAIN,12));
            //ta.setText("Generation Test #1");
            ta.addKeyListener(new java.awt.event.KeyAdapter() {
                    public void keyReleased(java.awt.event.KeyEvent ev) {
                            taKeyReleased(ev);
                    }
            });

            pack();
            ta.setSize(ta.getPreferredSize());
            pack();

            return ta;
    }

        private void taKeyReleased(java.awt.event.KeyEvent ev) { 
                int key = ev.getKeyCode();
                if (key == KeyEvent.VK_ENTER) {
                        dinamicTA();
                        pack();
                        content.setSize(content.getPreferredSize());
                        pack();
                }
        }

		public void open() {
			// TODO Auto-generated method stub
			TestWindow editor = new TestWindow();
            editor.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            editor.setVisible(true);
		}    
}