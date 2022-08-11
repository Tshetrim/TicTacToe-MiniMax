import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;


public class GUI extends JFrame{  
    public static GUI frame;
    public static String difficulty = "";
    public static String log = "";
    public static String stage = "";
    public static volatile TypeWriter TW;
    
    public static void initialize() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {}
        
        frame= new GUI();
        frame.setLayout(new BorderLayout());
        
        //JPANEL 
        JPanel gridPane = new JPanel(new GridBagLayout());
        gridPane.setPreferredSize(new Dimension(800, 600));
        gridPane.setBorder(BorderFactory.createLineBorder(Color.GREEN));
        
        frame.add(gridPane, BorderLayout.CENTER);
        printBoard(gridPane, new int[]{});
        
        //JTEXT
        JTextArea textPane = new JTextArea(20, 50);
        textPane.setBorder(BorderFactory.createLineBorder(Color.BLUE,1));
        textPane.setLineWrap(true);
        
        frame.add(new JScrollPane(textPane), BorderLayout.WEST);
        textPane.setPreferredSize(new Dimension(400, 500));
        printText(textPane);

        //JFRAME
        frame.setTitle("Tic-Tac-Toe");
        frame.setLocation(500,500); //to-do: set to center
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }


    public static void printBoard(JPanel gridPane, int[] board){
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.fill = java.awt.GridBagConstraints.BOTH;
        gbc.weightx = 0.25;
        gbc.weighty = 0.25;

        JButton buttons [][]= new JButton[3][3];
        
        for(int r = 0; r < buttons.length; r++){
            for(int c = 0; c < buttons.length; c++){
                //handle picking difficulty
                if(r==0){
                    if(c==0)
                        buttons[r][c] = new JButton("Player VS Player");
                    if(c==1)
                        buttons[r][c] = new JButton("Easy");
                    if(c==2)
                        buttons[r][c] = new JButton("Hard");
                }
                else 
                    buttons[r][c] = new JButton();
                
                //add action listerner
                buttons[r][c].addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        JButton button = (JButton) e.getSource();
                        if(button.getText().equals("Player VS Player"))
                            difficulty = "PVP";
                        else if(button.getText().equals("Easy"))
                            difficulty = "Easy";
                        else if(button.getText().equals("Hard"))
                            difficulty = "Hard";
                            
                        button.setText("Clicked");
                        System.out.println(difficulty);

                        //If intro text is still running and event was detected, text autocompletes 
                        if(TW.isRunning()){
                            TW.stop();
                            TW.getTextArea().setText(log);
                        } 
                        //TW is restarted with new log which is influenced by difficulty
                        updateText(TW);
                    }
                });

                gbc.gridx = c;
                gbc.gridy = r;
                gridPane.add(buttons[r][c], gbc);
            }
        }
    }


    public static void printText(JTextArea textArea){
        stage = "New";
        log = manageLog();

        TW = new TypeWriter(textArea);
        TW.setInput(log);

        TW.start();
        stage = "Pick Difficulty";
    }

    public static void updateText(TypeWriter t){
        manageLog();
        System.out.println(log);
        TW.setInput(log);
        TW.start();
    }

    public static String manageLog(){
        System.out.println("Stage: "+stage + " Manage Log Triggered");
        //stage 1 -> New
        if(stage.equals("New")){
            log = (">> Hello. Welcome to the game.\n>> To the grid to your right, there is a grid of boxes.\n>> Beat your opponent by getting a row, diagonal, or column of your own marks\n>> First, pick your game mode:");
        } 

        //stage 2 -> Pick Difficulty
        if(stage.equals("Pick Difficulty") && difficulty.length()>0){
            if(difficulty.equals("PVP"))
                log+= "\n>> Game starting as Player vs Player ";
            else 
                log+= "\n>> Game starting on difficulty " + difficulty;
            stage = "Figure out order";
        }


        return log;
    }



    public static void main(String[] args) {  
        GUI.initialize();
    }   
}  