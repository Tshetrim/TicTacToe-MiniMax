import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

//https://stackoverflow.com/questions/43333999/java-add-typewriter-effect-to-jtextarea
public class TypeWriter {
    private Timer timer;
    public int characterIndex = 0;
    private String input;
    private JTextArea textArea;
    private int delay = 1;

    public TypeWriter(JTextArea textArea) {
        this.textArea = textArea;

        timer = new Timer(delay, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Input inside event - Input length: "+ input.length());
                if (characterIndex < input.length()) {
                    System.out.println("Appending character " + characterIndex);
                    textArea.append(Character.toString(input.charAt(characterIndex)));
                    textArea.setCaretPosition(textArea.getDocument().getLength());

                    characterIndex++;
                } else {
                    stop();
                    System.out.println("Timer stopped");
                    System.out.println(isRunning());

                }
            }
        });
    }

    public TypeWriter(String input) {
        this.input = input;

        timer = new Timer(delay, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Input inside event - Input length: "+ input.length());
                if (characterIndex < input.length()) {
                    System.out.println("Appending ");
                    textArea.append(Character.toString(input.charAt(characterIndex)));
                    characterIndex++;
                } else {
                    stop();
                    System.out.println("Timer stopped");
                    System.out.println(isRunning());
                }
            }
        });
    }


    public void start() {
        if(textArea.getText()==null||textArea.getText().length()==0)
            characterIndex = 0;
        else
            characterIndex = textArea.getText().length();
        timer.start();

        System.out.println("Timer started");
        System.out.println("character Index: "+ characterIndex + " Text area: " + textArea.getText());
        System.out.println("Input length: "+ input.length() +" Input : " + input);
    }

    public void stop() {
        timer.stop();
    }

    public void setInput(String input){
        this.input = input;
    }

    public void setDelay(int s){
        this.delay = s;
    }

    public boolean isRunning(){
        return timer.isRunning();
    }

    public Timer getTimer(){
        return timer;
    }

    public JTextArea getTextArea(){
        return this.textArea;
    } 


}