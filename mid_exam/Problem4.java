
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Problem4 {

    public static class ProblemFrame extends JFrame {

        private final ProblemPanel problemPanel;

        private class ProblemPanel extends JPanel {

            private int second = 0;
            private int microsecond= 0;
            private final Timer timer;
            private final JButton startButton;
            private final JButton stopButton;
            private final JButton clearButton;

            public ProblemPanel() {
                this.timer = new Timer(10, new TimerHandler());

                this.startButton = new JButton("開始");
                this.startButton.addActionListener(new startButtonListener());
                this.add(this.startButton);

                this.stopButton = new JButton("停止");
                this.stopButton.addActionListener(new stopButtonListener());
                this.add(this.stopButton);

                this.clearButton = new JButton("清除");
                this.clearButton.addActionListener(new clearButtonListener());
                this.add(this.clearButton);
                
                this.repaint();
            }

            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawString(String.format("%02d:%02d", second, microsecond), 130, 50);
            }

            private class startButtonListener implements ActionListener {

                @Override
                public void actionPerformed(ActionEvent event) {
                    timer.start();
                }
            }

            private class stopButtonListener implements ActionListener {

                @Override
                public void actionPerformed(ActionEvent event) {
                    timer.stop();
                }
            }

            private class clearButtonListener implements ActionListener {

                @Override
                public void actionPerformed(ActionEvent event) {
                    second = 0;
                    microsecond = 0;
                    repaint();
                }
            }

            private class TimerHandler implements ActionListener {

                @Override
                public void actionPerformed(ActionEvent event) {
                    if (++microsecond == 100) {
                        microsecond = 0;
                        second++;
                    }
                    repaint();
                }
            }

        }

        public ProblemFrame() {
            super("Problem 4");

            this.problemPanel = new ProblemPanel();
            this.add(this.problemPanel);
        }
    }

    public static void main(String[] args) {
        ProblemFrame problemFrame = new ProblemFrame();
        problemFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        problemFrame.setSize(300, 100);
        problemFrame.setVisible(true);
    }
}
