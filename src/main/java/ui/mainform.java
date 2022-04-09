package ui;

import com.sun.org.apache.xml.internal.security.Init;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import service.Points;
import service.TestMean;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.*;
import java.util.Set;

public class mainform extends JFrame {
    private JPanel mainpanel;
    private JLabel Title;
    private JTextField dataset_text;
    private JTextField cluster_text;
    private JPanel infopanel;
    private JPanel datasetpanel;
    private JPanel toppanel;
    private JButton generateDataButton;
    private JButton clusterButton;
    private ChartPanel chartPanel1;

     Set<Points> datalist;
    TestMean testMean;
    XYSeriesCollection dataset;
    XYSeries xySeries;
    JFreeChart scatterPlot;
    XYPlot plot;

    public mainform(String title) throws HeadlessException {
        super(title);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setSize(1000, 1000);
        this.setContentPane(mainpanel);
        this.pack();

        testMean = new TestMean(3, 100);

        InputVerifier InputVerifier = new InputVerifier() {
            @Override
            public boolean verify(JComponent input) {
                final JTextComponent source = (JTextComponent) input;
                String text = source.getText();
                int num;
                try {
                    num = Integer.parseInt(text);
                    return true;
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(source, "Field cannot be empty or enter input in numbers", "Error Dialog",
                            JOptionPane.ERROR_MESSAGE);
                }

                return false;
            }
        };



        InputVerifier clusterInputVerifier = new InputVerifier() {
            @Override
            public boolean verify(JComponent input) {
                final JTextComponent source = (JTextComponent) input;
                String text = source.getText();
                int num;
                try {
                    num = Integer.parseInt(text);
                    return true;
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(source, "Field cannot be empty or enter input in numbers", "Error Dialog",
                            JOptionPane.ERROR_MESSAGE);
                }

                return false;
            }
        };

        dataset_text.setInputVerifier(InputVerifier);
        cluster_text.setInputVerifier(clusterInputVerifier);

        generateDataButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String datasettext = dataset_text.getText();
                testMean.setDatasize(Integer.parseInt(datasettext));
                testMean.GenerateData();
                datalist = testMean.getDatalist();
                DrawInitialPlot();

            }
        });
        clusterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String clustertext = cluster_text.getText();
                testMean.setClusters(Integer.parseInt(clustertext));
                testMean.getListMap().clear();
                testMean.getCenteroid().clear();
                if(testMean.getDatalist().size()!=0){
                    testMean.Init();
                    testMean.CalculateMean();
                    DrawPlot(testMean.getClusters());
                }else{
                    JOptionPane.showMessageDialog(cluster_text, "No Data Available", "Error Dialog",
                            JOptionPane.ERROR_MESSAGE);
                }

            }
        });

        dataset_text.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                EnableDisableDataset();

            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                EnableDisableDataset();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                EnableDisableDataset();
            }
        });



        cluster_text.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                EnableDisableDataset();
                EnableDisableClusterbtn();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                EnableDisableDataset();
                EnableDisableClusterbtn();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                EnableDisableDataset();
                EnableDisableClusterbtn();
            }
        });


    }


    public void DrawInitialPlot() {
        xySeries = new XYSeries("Random Data");
        datalist.stream().forEach(points -> {
            xySeries.add(points.getX(), points.getY());
        });
        dataset.removeAllSeries();
        dataset.addSeries(xySeries);

        //Changes background color
        plot = (XYPlot) scatterPlot.getPlot();
//        plot.setBackgroundPaint(new Color(31, 30, 30));


        // Create Panel
        chartPanel1 = new ChartPanel(scatterPlot);
//        setContentPane(chartPanel1);
    }


    public void DrawPlot(int clusters) {

        dataset.removeAllSeries();
        XYSeries[] xySeries = new XYSeries[clusters];

        for (int i = 0; i < clusters; i++) {
            xySeries[i] = new XYSeries("Cluster " + i);
            int finalI = i;
            testMean.getListMap().get(testMean.getcetroidvalue(i)).stream().forEach(points -> {
                xySeries[finalI].add(points.getX(), points.getY());
            });


            dataset.addSeries(xySeries[i]);
        }


        //Changes background color
        plot = (XYPlot) scatterPlot.getPlot();
//        plot.setBackgroundPaint(new Color(31, 30, 30));


        // Create Panel
        chartPanel1 = new ChartPanel(scatterPlot);


    }


    private void createUIComponents() {
        // TODO: place custom component creation code here
        dataset = new XYSeriesCollection();
        XYSeries xySeries = new XYSeries("No data");
        dataset.addSeries(xySeries);
        scatterPlot = ChartFactory.createScatterPlot(
                "K Mean Clustering", // Chart title
                "DataSet", // X-Axis Label
                "DataSet", // Y-Axis Label
                dataset // Dataset for the Chart
        );

        //Changes background color
        plot = (XYPlot) scatterPlot.getPlot();
        plot.setBackgroundPaint(new Color(31, 30, 30));


        // Create Panel
        chartPanel1 = new ChartPanel(scatterPlot);
//        ChartPanel panel = new ChartPanel(scatterPlot);
        setContentPane(chartPanel1);
    }


    public void EnableDisableDataset(){
        String text = dataset_text.getText();

//        System.out.println("Text length:" + text.length() + "Text " +text);
        if (text.length() > 0) {
//            testMean.setDatasize(Integer.parseInt(text));

            generateDataButton.setEnabled(true);
        } else {
            generateDataButton.setEnabled(false);
        }
    }


    public void EnableDisableClusterbtn(){
        String text = cluster_text.getText();
        String text1 = dataset_text.getText();
        if (text.length() > 0 && text1.length()>0) {
            clusterButton.setEnabled(true);
        } else {
            clusterButton.setEnabled(false);
        }
    }
}
