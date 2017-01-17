package assistant;

import java.awt.EventQueue;

import assistant.ui.MainFrame;


public class Program {
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    new SkinManager().initialize();
                    new MainFrame().setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
