package library.itstar.wei.tbsx5.view.SpinKit.style;

import library.itstar.wei.tbsx5.view.SpinKit.sprite.Sprite;
import library.itstar.wei.tbsx5.view.SpinKit.sprite.SpriteContainer;

/**
 * Created by ybq.
 */
public class MultiplePulseRing extends SpriteContainer
{

    @Override
    public Sprite[] onCreateChild() {
        return new Sprite[]{
                new PulseRing(),
                new PulseRing(),
                new PulseRing(),
        };
    }

    @Override
    public void onChildCreated(Sprite... sprites) {
        for (int i = 0; i < sprites.length; i++) {
            sprites[i].setAnimationDelay(200 * (i + 1));
        }
    }
}
