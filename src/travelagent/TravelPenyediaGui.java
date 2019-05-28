package travelagent;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

class TravelPenyediaGui extends JFrame {

    private static final long serialVersionUID = 1L;

    private TravelPenyedia myAgent;

    private JTextField titleField, jbField, priceField;

    TravelPenyediaGui(TravelPenyedia a) {
        super(a.getLocalName());

        myAgent = a;

        JPanel p = new JPanel();
        p.setLayout(new GridLayout(3, 2));
        p.add(new JLabel("Kota Tujuan:"));
        titleField = new JTextField(15);
        p.add(titleField);

        p.add(new JLabel("Jadwal Berangkat:"));
        jbField = new JTextField(15);
        p.add(jbField);

        p.add(new JLabel("Biaya:"));
        priceField = new JTextField(15);
        p.add(priceField);
        getContentPane().add(p, BorderLayout.CENTER);

        JButton addButton = new JButton("Tambah");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ev) {
                try {
                    String title = titleField.getText().trim();
                    String jadwalBerangkat = jbField.getText().trim();
                    String price = priceField.getText().trim();
                    String[] hari = {"Senin", "Selasa", "Rabu", "Kamis", "Jumat", "Sabtu", "Minggu"};
                    int jb=0;
                    for (int i = 0; i < hari.length; i++) {
                        if (jadwalBerangkat.equals(hari[i])) {
                            jb = i;
                        }
                    }
                    myAgent.updateCatalogue(title, price, jb);
                    titleField.setText("");
                    priceField.setText("");
                    jbField.setText("");
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(TravelPenyediaGui.this, "Invalid values. " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        p = new JPanel();
        p.add(addButton);
        getContentPane().add(p, BorderLayout.SOUTH);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                myAgent.doDelete();
            }
        });

        setResizable(false);
    }

    public void showGui() {
        pack();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int centerX = (int) screenSize.getWidth() / 2;
        int centerY = (int) screenSize.getHeight() / 2;
        setLocation(centerX - getWidth() / 2, centerY - getHeight() / 2);
        super.setVisible(true);
    }
}
