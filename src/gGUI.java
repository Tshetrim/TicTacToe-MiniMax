import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.awt.event.ActionEvent;

public class gGUI{
    public JFrame frame;
    public JPanel gridPane;
    public JTextArea textArea;
    public TypeWriter TW;

    public JButton buttons [][] = new JButton[3][3];
    
    
    public gGUI(){
        initialize();
    }
    
    //set GUI with all components and panes 
    public void initialize(){
        setLookAndFeel();
        frame = new JFrame();
        frame.setLayout(new BorderLayout());

        gridPane= getGridPanel();
        frame.add(gridPane, BorderLayout.CENTER);
        textArea = getTextArea();
        frame.add(new JScrollPane(textArea), BorderLayout.WEST);
        
        //JFRAME
        frame.setTitle("Tic-Tac-Toe");
        frame.setLocation(500,500); //to-do: set to center
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    //try to set look and feel of components to OS look and feel
    public void setLookAndFeel(){
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {}
    }

    //initializing and returning the grid panel 
    public JPanel getGridPanel(){
        JPanel gridPane = new JPanel(new GridBagLayout());
        gridPane.setPreferredSize(new Dimension(800, 600));
        gridPane.setBorder(BorderFactory.createLineBorder(Color.GREEN));
        return gridPane;
    }


    /*fill and update grid panel to the state of the game board
        GBC -> GridBagConstraints allows for the layout of the JButtons in a grid */
    public void updateGrid(Object object, String[][] board, Method method, HashMap<String, Object> gameVariables) throws Exception{
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = java.awt.GridBagConstraints.BOTH;
        gbc.weightx = 0.25;
        gbc.weighty = 0.25;

        int identifierValue = 0;
        for(int r = 0; r < buttons.length; r++){
            for(int c = 0; c < buttons.length; c++){
                if(buttons[r][c]==null){
                    buttons[r][c] = new JButton(board[r][c]);
                    buttons[r][c].setName(String.valueOf((identifierValue)));
                    identifierValue++;
                }
                else
                    buttons[r][c].setText(board[r][c]);
                addActionListenerToButton(buttons[r][c], object, method, gameVariables);
                
                gbc.gridx = c;
                gbc.gridy = r;
                gridPane.add(buttons[r][c], gbc);
            }
        }
    }

    //updates the text of the textArea with a new log and TypeWriter handles creating TypeWriter animation 
    public void updateText(String log){
        TW = new TypeWriter(textArea);
        TW.setInput(log);
        TW.start();
    }

    //initializing and returning the text panel
    public JTextArea getTextArea(){
        JTextArea textArea = new JTextArea(20, 50);
        textArea.setBorder(BorderFactory.createLineBorder(Color.BLUE,1));
        textArea.setLineWrap(true);

        textArea.setPreferredSize(new Dimension(400, 500));
        return textArea;
    }
    
    //add action listener to all buttons 
    public void addActionListenerToButton(JButton button, Object object, Method method, HashMap<String, Object> gameVariables) throws Exception{
        button.addActionListener(new ActionListener() {

            //performs an action if a button is hit
            //that action is dependent on the actionPerformedManager from the Game class and its relevant states 
            @Override
            public void actionPerformed(ActionEvent e){
                JButton button = (JButton) e.getSource();
                Object[] parameters = new Object[gameVariables.size()];
                parameters[0] = button;
                parameters[1] = TW;
                try {
                    method.invoke(object, parameters[0], parameters[1]);
                } catch (IllegalAccessException e1) {
                    e1.printStackTrace();
                } catch (InvocationTargetException e1) {
                    e1.printStackTrace();
                }
                
            }
        });
    }

}
