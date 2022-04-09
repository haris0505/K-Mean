package service;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.stream.Collectors;

public class TestMean extends JFrame {


    int clusters = 2;
    Set<Points> datalist;
    int datasize;
    boolean nochange = false;
    //todo make array of centroid of size k
    //todo select random means points

    Set<Points> centeroid;
    Set<Points> oldcenteroid;
    Map<Points, Set<Points>> listMap;
    static int iteration = 0;
    static int count = 0;


    public TestMean()  {
        this.clusters = clusters;
        this.datasize = datasize;
        centeroid = new HashSet<>();
        oldcenteroid = new HashSet<>();
        listMap = new HashMap<>();
        datalist = new HashSet<>();
    }

    public TestMean(int clusters, int datasize) {
        this.clusters = clusters;
        this.datasize = datasize;
        centeroid = new HashSet<>();
        oldcenteroid = new HashSet<>();
        listMap = new HashMap<>();
        datalist = new HashSet<>();
    }

    public void GenerateData() {
        datalist.clear();
        Random random = new Random();
        while (datalist.size() != datasize) {
            int x = random.nextInt(100) + 1;
            int y = random.nextInt(100) + 1;
            datalist.add(new Points(x, y));
        }


    }

    public void Init() {
        //todo calculate the mean
        if (datasize >= clusters) {
            Random random = new Random();
            while (centeroid.size() != clusters) {
                int temppos = random.nextInt(datasize-1) + 1;
                centeroid.add(getvalue(temppos));
            }


            datalist.forEach(t -> {
//                System.out.println(t);
                AddvaluetoMap(calculateDistance(t), t);
            });
            System.out.println("Iteration " + iteration);
            PrintMap();
            iteration++;
        } else {
            nochange = true;
        }

    }

    //datalist
    public Points getvalue(int pos) {
        Points[] points = datalist.toArray(new Points[datalist.size()]);
        return points[pos];
    }


    public Points getcetroidvalue(int pos) {
        Points[] points = centeroid.toArray(new Points[centeroid.size()]);
        return points[pos];
    }

    public  void Verifysets(){

        boolean temp=centeroid.equals(oldcenteroid);
        if(temp){
            nochange=true;

        }else{
            centeroid.clear();
            listMap.clear();

        }

        oldcenteroid.stream().forEach(points -> {
            centeroid.add(points);
        });
        oldcenteroid.clear();
    }

    public void CalculateMean() {

        while (!nochange) {
            System.out.println("Iteration " + iteration);
            iteration++;
            listMap.forEach((points, points2) -> {
                Points temp = calculateCenter(points2);
                oldcenteroid.add(temp);
            });

            Verifysets();

            datalist.stream().forEach(points -> {
                AddvaluetoMap(calculateDistance(points), points);
            });
            PrintMap();
        }


    }

    private void AddvaluetoMap(Points key, Points value) {
        listMap.putIfAbsent(key, new HashSet<>());
        listMap.get(key).add(value);
    }


    public Points calculateDistance(Points value) {
        double min = 100;
        int pos = 0;
        for (int i = 0; i < centeroid.size(); i++) {
            double tempdis = Math.sqrt(Math.pow((value.x - getcetroidvalue(i).x), 2) + Math.pow((value.y - getcetroidvalue(i).y), 2));

            if (tempdis < min) {
                min = tempdis;
                pos = i;
            }
        }


        return getcetroidvalue(pos);
    }

    public Points calculateCenter(Set<Points> datalist) {
        //todo calculate new mean from datasets and store it in array

        //todo need to changes
        double x = Math.round(datalist.stream().mapToDouble(Points::getX).average().orElse(50.0));
        double y = Math.round(datalist.stream().mapToDouble(Points::getY).average().orElse(50));
        Points p = new Points(x, y);
        return p;

    }




    public void PrintMap() {
        listMap.forEach((points, points2) -> {
            System.out.println("Key " + points + " Count " + points2.size());
            System.out.println("values " + points2);
            System.out.println("---------------------------");
        });


    }
    public void DrawPlot() {
        XYSeriesCollection dataset = new XYSeriesCollection();

        XYSeries[] xySeries = new XYSeries[clusters];

        for (int i = 0; i < clusters; i++) {
            xySeries[i] = new XYSeries("Cluster " + i);
            int finalI = i;
            listMap.get(getcetroidvalue(i)).stream().forEach(points -> {
                xySeries[finalI].add(points.x, points.y);
            });


            dataset.addSeries(xySeries[i]);
        }


        JFreeChart scatterPlot = ChartFactory.createScatterPlot(
                "JFreeChart Scatter Plot", // Chart title
                "Numbers", // X-Axis Label
                "Numbers", // Y-Axis Label
                dataset // Dataset for the Chart
        );

        //Changes background color
        XYPlot plot = (XYPlot) scatterPlot.getPlot();
        plot.setBackgroundPaint(new Color(31, 30, 30));


        // Create Panel
        ChartPanel panel = new ChartPanel(scatterPlot);
        setContentPane(panel);

    }



    public void DrawInitialPlot() {
        XYSeriesCollection dataset = new XYSeriesCollection();

        XYSeries xySeries = new XYSeries(datalist.size());

        datalist.stream().forEach(points -> {
            xySeries.add(points.x,points.y);
        });

        dataset.addSeries(xySeries);
        JFreeChart scatterPlot = ChartFactory.createScatterPlot(
                "Initial", // Chart title
                "DataSet", // X-Axis Label
                "Numbers", // Y-Axis Label
                dataset // Dataset for the Chart
        );

        //Changes background color
        XYPlot plot = (XYPlot) scatterPlot.getPlot();
        plot.setBackgroundPaint(new Color(31, 30, 30));


        // Create Panel
        ChartPanel panel = new ChartPanel(scatterPlot);
        setContentPane(panel);

    }

    public Set<Points> getDatalist() {
        return datalist;
    }

    public void setDatalist(Set<Points> datalist) {
        this.datalist = datalist;
    }

    public int getClusters() {
        return clusters;
    }

    public void setClusters(int clusters) {
        this.clusters = clusters;
    }

    public Map<Points, Set<Points>> getListMap() {
        return listMap;
    }

    public void setListMap(Map<Points, Set<Points>> listMap) {
        this.listMap = listMap;
    }

    public int getDatasize() {
        return datasize;
    }

    public void setDatasize(int datasize) {
        this.datasize = datasize;
    }

    public Set<Points> getCenteroid() {
        return centeroid;
    }

    public void setCenteroid(Set<Points> centeroid) {
        this.centeroid = centeroid;
    }
}
