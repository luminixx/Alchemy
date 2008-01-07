/**
 * MicrophoneShapes.java
 *
 * Created on December 6, 2007, 5:39 PM
 *
 * @author  Karl D.D. Willis
 * @version 1.0
 */
package alchemy.create;

import alchemy.*;
import alchemy.ui.AlcSubSlider;
import alchemy.ui.AlcSubToolBarSection;
import java.awt.Point;
import java.awt.event.MouseEvent;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class MicrophoneShapes extends AlcModule implements AlcConstants {

    private AlcMicInput micIn;
    private Point oldP;
    private float volume = 10F;
    private AlcSubToolBarSection subToolBarSection;

    /** Creates a new instance of MicrophoneShapes */
    public MicrophoneShapes() {
    }

    @Override
    public void setup() {
        // Create a new MicInput Object with a buffer of 10
        micIn = new AlcMicInput(2);
        micIn.startMicInput();
        createSubToolBarSection();
        toolBar.addSubToolBarSection(subToolBarSection);
    }

    @Override
    public void deselect() {
        micIn.stopMicInput();
        micIn = null;
    }

    @Override
    public void reselect() {
        //micIn = new AlcMicInput(2);
        micIn.startMicInput();
        toolBar.addSubToolBarSection(subToolBarSection);
    }

    public void createSubToolBarSection() {
        subToolBarSection = new AlcSubToolBarSection(this);

        // Volume Slider
        AlcSubSlider volumeSlider = new AlcSubSlider("Volume", 0, 100, 10);
        volumeSlider.setToolTipText("Adjust the microphone input volume");
        volumeSlider.slider.addChangeListener(
                new ChangeListener() {

                    public void stateChanged(ChangeEvent e) {
                        JSlider source = (JSlider) e.getSource();
                        if (!source.getValueIsAdjusting()) {
                            int value = source.getValue();
                            volume = value;
                        //System.out.println(volume);
                        }
                    }
                });
        subToolBarSection.add(volumeSlider);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        Point p = e.getPoint();
        canvas.createShapes.add(makeShape(p));
        oldP = p;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        Point p = e.getPoint();

        byte[] buffer = micIn.getBuffer();


        //Point pt = rightAngle(p, oldP, micIn.getMicLevel());
        Point pt = rightAngle(p, oldP, buffer[0]);

        // Need to test if it is null incase the shape has been auto-cleared
        if (canvas.getCurrentCreateShape() != null) {
            canvas.getCurrentCreateShape().addCurvePoint(pt);
            canvas.redraw();
            oldP = p;
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        Point p = e.getPoint();
        // Need to test if it is null incase the shape has been auto-cleared
        if (canvas.getCurrentCreateShape() != null) {
            canvas.getCurrentCreateShape().addLastPoint(p);
            canvas.redraw();
            canvas.commitShapes();
        }
    }

    private Point rightAngle(Point p1, Point p2, double distance) {
        double adjustedDistance = distance * volume;
        // Calculate the angle between the last point and the new point
        double angle = Math.atan2(p1.y - p2.y, p1.x - p2.x);
        // Conver the polar coordinates to cartesian
        double x = p1.x + (volume * Math.cos(angle));
        double y = p1.y + (volume * Math.sin(angle));

        return new Point((int) x, (int) y);
    }

    private AlcShape makeShape(Point p) {
        // Make a new shape with the globally defined style etc...
        return new AlcShape(p, canvas.getColour(), canvas.getAlpha(), canvas.getStyle(), canvas.getLineWidth());
    }

    // KEY EVENTS
//    @Override
//    public void keyReleased(KeyEvent e) {
//        char keyChar = e.getKeyChar();
//
//        //System.out.println(keyChar);
//        switch (keyChar) {
//            case '[':
//                System.out.println("[");
//
//
//                break;
//
//            case ']':
//
//                System.out.println("]");
//
//                break;
//            case 'p':
//                System.out.println(micIn.getMicLevel());
//                break;
//        }
//    }
}
