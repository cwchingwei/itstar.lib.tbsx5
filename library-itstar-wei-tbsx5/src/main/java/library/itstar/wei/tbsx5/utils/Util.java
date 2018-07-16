package library.itstar.wei.tbsx5.utils;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.util.TypedValue;
import android.widget.Button;
import android.widget.LinearLayout;

/**
 * Created by jojodavid on 16/8/21.
 */
public class Util
{

    // 功能：字符串半角转换为全角
// 说明：半角空格为32,全角空格为12288.
//       其他字符半角(33-126)与全角(65281-65374)的对应关系是：均相差65248
// 输入参数：input -- 需要转换的字符串
// 输出参数：无：
// 返回值: 转换后的字符串
    public static String halfToFull( String input)
    {
        char[] c = input.toCharArray();
        for (int i = 0; i< c.length; i++)
        {
            if (c[i] == 32) //半角空格
            {
                c[i] = (char) 12288;
                continue;
            }

            //根据实际情况，过滤不需要转换的符号
            //if (c[i] == 46) //半角点号，不转换
            // continue;

            if (c[i]> 32 && c[i]< 127)    //其他符号都转换为全角
                c[i] = (char) (c[i] + 65248);
        }
        return new String(c);
    }


    // 功能：字符串全角转换为半角
// 说明：全角空格为12288，半角空格为32
//       其他字符全角(65281-65374)与半角(33-126)的对应关系是：均相差65248
// 输入参数：input -- 需要转换的字符串
// 输出参数：无：
// 返回值: 转换后的字符串
    public static String fullToHalf( String input)
    {
        char[] c = input.toCharArray();
        for (int i = 0; i< c.length; i++)
        {
            if (c[i] == 12288) //全角空格
            {
                c[i] = (char) 32;
                continue;
            }

            if (c[i]> 65280&& c[i]< 65375)
                c[i] = (char) (c[i] - 65248);
        }
        return new String(c);
    }

    public static void correctButtonWidth( Context context, Button aView, int desiredWidth )
    {
        Paint paint  = new Paint();
        Rect  bounds = new Rect();

        paint.setTypeface(aView.getTypeface());
        float textSize = aView.getTextSize();
        paint.setTextSize(textSize);
        String text = aView.getText().toString();
        paint.getTextBounds(text, 0, text.length(), bounds);
        Log.i( "MainActivity", "view.height(): " + desiredWidth );
        Log.i( "MainActivity", "bounds.height(): " + bounds.height() );
        Log.i( "MainActivity", "textSize: " + textSize );

        Button view = new Button( context );
        view.setLayoutParams( new LinearLayout.LayoutParams( LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT ) );
        view.measure( 0, 0 );
        view.setText( text );
        view.setTypeface( aView.getTypeface() );
        view.setTextSize( textSize );

        while (view.getMeasuredHeight() > desiredWidth)
        {
            textSize--;
            view.measure( 0, 0 );
            view.setTextSize( textSize );

            Log.i( "MainActivity", "=========================================================" );
            Log.i( "MainActivity", "bounds.height(): " + view.getMeasuredHeight() );
            Log.i( "MainActivity", "textSize: " + view.getTextSize() );
        }

        aView.setTextSize( TypedValue.COMPLEX_UNIT_PX, textSize);
    }
}
