package library.itstar.wei.tbsx5.view.SpinKit;


import library.itstar.wei.tbsx5.view.SpinKit.sprite.Sprite;
import library.itstar.wei.tbsx5.view.SpinKit.style.ChasingDots;
import library.itstar.wei.tbsx5.view.SpinKit.style.Circle;
import library.itstar.wei.tbsx5.view.SpinKit.style.CubeGrid;
import library.itstar.wei.tbsx5.view.SpinKit.style.DoubleBounce;
import library.itstar.wei.tbsx5.view.SpinKit.style.FadingCircle;
import library.itstar.wei.tbsx5.view.SpinKit.style.FoldingCube;
import library.itstar.wei.tbsx5.view.SpinKit.style.MultiplePulse;
import library.itstar.wei.tbsx5.view.SpinKit.style.MultiplePulseRing;
import library.itstar.wei.tbsx5.view.SpinKit.style.Pulse;
import library.itstar.wei.tbsx5.view.SpinKit.style.PulseRing;
import library.itstar.wei.tbsx5.view.SpinKit.style.RotatingCircle;
import library.itstar.wei.tbsx5.view.SpinKit.style.RotatingPlane;
import library.itstar.wei.tbsx5.view.SpinKit.style.ThreeBounce;
import library.itstar.wei.tbsx5.view.SpinKit.style.WanderingCubes;
import library.itstar.wei.tbsx5.view.SpinKit.style.Wave;

/**
 * Created by ybq.
 */
public class SpriteFactory {

    public static Sprite create( Style style) {
        Sprite sprite = null;
        switch (style) {
            case ROTATING_PLANE:
                sprite = new RotatingPlane();
                break;
            case DOUBLE_BOUNCE:
                sprite = new DoubleBounce();
                break;
            case WAVE:
                sprite = new Wave();
                break;
            case WANDERING_CUBES:
                sprite = new WanderingCubes();
                break;
            case PULSE:
                sprite = new Pulse();
                break;
            case CHASING_DOTS:
                sprite = new ChasingDots();
                break;
            case THREE_BOUNCE:
                sprite = new ThreeBounce();
                break;
            case CIRCLE:
                sprite = new Circle();
                break;
            case CUBE_GRID:
                sprite = new CubeGrid();
                break;
            case FADING_CIRCLE:
                sprite = new FadingCircle();
                break;
            case FOLDING_CUBE:
                sprite = new FoldingCube();
                break;
            case ROTATING_CIRCLE:
                sprite = new RotatingCircle();
                break;
            case MULTIPLE_PULSE:
                sprite = new MultiplePulse();
                break;
            case PULSE_RING:
                sprite = new PulseRing();
                break;
            case MULTIPLE_PULSE_RING:
                sprite = new MultiplePulseRing();
                break;
            default:
                break;
        }
        return sprite;
    }
}
