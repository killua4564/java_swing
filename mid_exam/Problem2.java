
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.Timer;

public class Problem2 {

    public static class ProblemFrame extends JFrame {

        private final Timer timer;
        private final JButton button;
        private final JList list;
        private final JTextArea textArea;
        private final JTextField textField;
        private final List<Crawler> connections;
        private ArrayList<String> stringList = new ArrayList<>();

        public ProblemFrame() {
            super("Problem2");
            this.setLayout(new FlowLayout());

            this.timer = new Timer(1000 * 60 * 10, new TimerHandler());

            this.textField = new JTextField("Please enter your url link here.");
            this.add(this.textField);

            this.button = new JButton("Add");
            this.button.addActionListener(new ButtonListener());
            this.add(this.button);
            
            this.list = new JList();
            this.list.setVisibleRowCount(5);
            this.add(new JScrollPane(this.list));
            
            this.textArea = new JTextArea("", 10, 15);
            this.textArea.setLineWrap(true);
            this.add(new JScrollPane(this.textArea));
        }

        private class ButtonListener implements ActionListener {

            @Override
            public void actionPerformed(ActionEvent event) {
                String string = textField.getText();
                textField.setText("");
                stringList.add(string);
                list.setListData(stringList.toArray());
            }
        }

        private class Crawler extends Thread {
            
        }

        
        private class TimerHandler implements ActionListener {

            @Override
            public void actionPerformed(ActionEvent event) {

            }
        }
    }

    public static void main(String[] args) {
        ProblemFrame problemFrame = new ProblemFrame();
        problemFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        problemFrame.setSize(800, 600);
        problemFrame.setVisible(true);
    }
}
