package library.itstar.wei.tbsx5.model;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Color;

import library.itstar.wei.tbsx5.R;


public class ShowDialog
{
    public static ShowDialog instance()
    {
        if( _instance == null )
        {
            return new ShowDialog();
        }
        return _instance;
    }

    public static void showMessageDialog( Context aContext, String aMessage, String aTitle )
    {
        AlertDialog.Builder builder = new AlertDialog.Builder( aContext );
        builder.setTitle( aMessage );
        builder.setIcon( android.R.drawable.ic_dialog_alert );
        builder.setPositiveButton( aContext.getString( R.string.dialog_confirm ), new OnClickListener()
        {

            public void onClick( DialogInterface dialog, int whichButton )
            {
                dialog.dismiss();
            }
        } );
        AlertDialog alert = builder.create();
//        alert.getWindow().setType( WindowManager.LayoutParams.TYPE_SYSTEM_ALERT );//
        alert.show();
    }

    public static void showButtonMessageDialog( Context aContext, String aMessage, String aTitle, OnClickListener listener )
    {
        AlertDialog.Builder builder = new AlertDialog.Builder( aContext );
        builder.setTitle( aMessage );
        builder.setIcon( android.R.drawable.ic_dialog_alert );
        builder.setPositiveButton( aContext.getString( R.string.dialog_confirm ), listener );
        AlertDialog alert = builder.create();
//        alert.getWindow().setType( WindowManager.LayoutParams.TYPE_SYSTEM_ALERT );//
        alert.show();
    }

    public void showButtonRestartMessageDialog( Context aContext, String aMessage, String aTitle, OnClickListener listener )
    {
        AlertDialog.Builder builder = new AlertDialog.Builder( aContext );
        builder.setCancelable( false );
        builder.setTitle( aTitle );
        builder.setMessage( aMessage );
        builder.setIcon( android.R.drawable.ic_dialog_alert );
        builder.setPositiveButton( aContext.getString( R.string.dialog_restart ), listener );
        builder.setNegativeButton( aContext.getString( R.string.dialog_cancle ), new OnClickListener()
        {
            @Override
            public void onClick ( DialogInterface dialogInterface, int i )
            {
                if( alert != null )
                {
                    alert.dismiss();
                }
            }
        } );
        if( alert == null )
        {
            alert = builder.create();
        }
        alert.setOnShowListener( new DialogInterface.OnShowListener()
        {
            @Override
            public void onShow ( DialogInterface arg0 )
            {
                alert.getButton( AlertDialog.BUTTON_POSITIVE ).setTextColor( Color.RED );
            }
        } );

        if( !alert.isShowing() )
        {
            alert.show();
        }
    }

    public void showBuglyRestartMessageDialog( Context aContext, OnClickListener listener )
    {
        AlertDialog.Builder builder = new AlertDialog.Builder( aContext, AlertDialog.THEME_HOLO_LIGHT );
        builder.setCancelable( false );
        builder.setTitle( aContext.getString( R.string.dialog_title_system ) );
        builder.setMessage( aContext.getString( R.string.dialog_system_updated_plz_restart ) );
//        builder.setIcon( android.R.drawable.ic_dialog_alert );
        builder.setPositiveButton( aContext.getString( R.string.no_factory_ok ), listener );
        if( alertOnTop == null )
        {
            alertOnTop = builder.create();
        }
        alertOnTop.setOnShowListener( new DialogInterface.OnShowListener()
        {
            @Override
            public void onShow ( DialogInterface arg0 )
            {
                if( alertOnTop != null )
                {
                    alertOnTop.getButton( AlertDialog.BUTTON_POSITIVE ).setTextColor( Color.RED );
                }
            }
        } );
//        alert.getWindow().setType( WindowManager.LayoutParams.TYPE_SYSTEM_ALERT );
        if( !alertOnTop.isShowing() )
        {
            alertOnTop.show();
        }
    }

    public void showMessageErrorDialog( Context aContext, String aMessage, String aTitle )
    {
        AlertDialog.Builder builder = new AlertDialog.Builder( aContext );
        builder.setTitle( aTitle );
        builder.setMessage( aMessage );
        builder.setIcon( android.R.drawable.ic_delete );
        builder.setPositiveButton( "确认", new OnClickListener()
        {

            public void onClick( DialogInterface dialog, int whichButton )
            {
                if( dialog != null )
                {
                    dialog.dismiss();
                }
            }
        } );
        if( alert == null )
        {
            alert = builder.create();
        }
        if( !alert.isShowing() )
        {
            alert.show();
        }

    }

    public static void showMessageToHomeDialog( final Context aContext, final Activity aNextClass, String aMessage, String aTitle )
    {
        AlertDialog.Builder builder = new AlertDialog.Builder( aContext );
        builder.setTitle( aMessage );
        builder.setIcon( null );
        builder.setPositiveButton( "確認", new OnClickListener()
        {

            public void onClick( DialogInterface dialog, int whichButton )
            {
                Intent intent = new Intent( aContext, aNextClass.getClass() );
                intent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );
                aContext.startActivity( intent );
                dialog.dismiss();
            }
        } );
        AlertDialog alert = builder.create();
//        alert.getWindow().setType( WindowManager.LayoutParams.TYPE_SYSTEM_ALERT );//
        alert.show();
    }
    public boolean isShowing()
    {
        if( alert != null )
        {
            return alert != null? alert.isShowing() : false;
        }
        else if( alertOnTop != null )
        {
            return alertOnTop != null? alertOnTop.isShowing() : false;
        }
        else
        {
            return false;
        }
    }
    public static void realse()
    {
        alert = null;
        _instance = null;
    }
    private Context _context = null;
    private static AlertDialog alert;
    private static AlertDialog alertOnTop;
    private static ShowDialog _instance;
}
