package library.itstar.wei.tbsx5.view.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import library.itstar.wei.tbsx5.R;

/**
 * Created by Ching Wei on 2018/7/16.
 */

public class DialogPermission extends DialogFragment
{
    private static final int READ_CONTACTS_REQUEST_CODE = 12;
    private final        int PERMISSION_REQUEST_CODE    = 11;

    private Context                          context;
    private CameraPermissionsGrantedCallback listener;

    private boolean shouldResolve;
    private boolean shouldRetry;
    private boolean externalGrantNeeded;

    public static DialogPermission newInstance ()
    {
        return new DialogPermission();
    }

    public DialogPermission ()
    {
    }

    @Override
    public void onAttach ( Context context )
    {
        super.onAttach( context );
        this.context = context;
        if ( context instanceof CameraPermissionsGrantedCallback )
        {
            listener = ( CameraPermissionsGrantedCallback ) context;
        }
    }

    @Override
    public void onCreate ( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        setStyle( STYLE_NO_TITLE, R.style.PermissionsDialogFragmentStyle );
        setCancelable( false );
        requestNecessaryPermissions();
    }

    @Override
    public void onResume ()
    {
        super.onResume();
        if ( shouldResolve )
        {
            if ( externalGrantNeeded )
            {
                showAppSettingsDialog();
            }
            else if ( shouldRetry )
            {
                showRetryDialog();
            }
            else
            {
                //permissions have been accepted
                if ( listener != null )
                {
                    listener.navigateToCaptureFragment();
                    dismiss();
                }
            }
        }
    }

    @Override
    public void onDetach ()
    {
        super.onDetach();
        context = null;
        listener = null;
    }

    @Override
    public void onRequestPermissionsResult (
            int requestCode,
            String permissions[], int[] grantResults
    )
    {
        shouldResolve = true;
        shouldRetry = false;

        for ( int i = 0; i < permissions.length; i++ )
        {
            String permission  = permissions[ i ];
            int    grantResult = grantResults[ i ];

            if ( !shouldShowRequestPermissionRationale( permission ) && grantResult != PackageManager.PERMISSION_GRANTED )
            {
                externalGrantNeeded = true;
                return;
            }
            else if ( grantResult != PackageManager.PERMISSION_GRANTED )
            {
                shouldRetry = true;
                return;
            }
        }
    }

    private void requestNecessaryPermissions ()
    {
        requestPermissions( new String[]{
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        }, PERMISSION_REQUEST_CODE );
    }

    private void showAppSettingsDialog ()
    {
        new AlertDialog.Builder( context )
                .setTitle( "Permissions Required" )
                .setMessage( context.getString( R.string.dialog_retry_permission ) )
                .setCancelable( false )
                .setPositiveButton( "App Settings", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick ( DialogInterface dialogInterface, int i )
                    {
                        Intent intent = new Intent();
                        intent.setAction( Settings.ACTION_APPLICATION_DETAILS_SETTINGS );
                        Uri uri = Uri.fromParts( "package", context.getApplicationContext().getPackageName(), null );
                        intent.setData( uri );
                        ((Activity)context).startActivityForResult( intent, 995 );
                        dismiss();
                    }
                } )
                .create().show();
    }

    private void showRetryDialog ()
    {

        new AlertDialog.Builder( context )
                .setTitle( "Permissions Declined" )
                .setMessage( context.getString( R.string.dialog_retry_permission ) )
                .setCancelable( false )
                .setPositiveButton( context.getString( R.string.dialog_restart ), new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick ( DialogInterface dialogInterface, int i )
                    {
                        requestNecessaryPermissions();
                    }
                } )
                .create().show();
    }

    public interface CameraPermissionsGrantedCallback
    {
        void navigateToCaptureFragment ();
    }
}