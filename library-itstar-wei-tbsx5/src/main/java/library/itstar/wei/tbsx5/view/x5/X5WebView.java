package library.itstar.wei.tbsx5.view.x5;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;


public class X5WebView extends WebView
{
    private WebViewClient client = new WebViewClient() {
        /**
         * 防止加载网页时调起系统浏览器
         */
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    };

    @SuppressLint( "SetJavaScriptEnabled" )
    public X5WebView ( Context arg0, AttributeSet arg1 )
    {
        super( arg0, arg1 );
        initWebViewSettings();
        this.getView().setClickable( true );
        setWebContentsDebuggingEnabled( true );
    }

    private void initWebViewSettings ()
    {
        WebSettings webSetting = this.getSettings();
        webSetting.setJavaScriptEnabled( true );
        webSetting.setJavaScriptCanOpenWindowsAutomatically( true );
        webSetting.setAllowFileAccess( true );
        webSetting.setAllowFileAccessFromFileURLs( true );
        webSetting.setAllowUniversalAccessFromFileURLs( true );
        webSetting.setLoadWithOverviewMode( true );
        webSetting.setLayoutAlgorithm( WebSettings.LayoutAlgorithm.NARROW_COLUMNS );
        webSetting.setSupportZoom( true );
        webSetting.setBuiltInZoomControls( true );
        webSetting.setUseWideViewPort( true );
        webSetting.setSupportMultipleWindows( true );
        // webSetting.setLoadWithOverviewMode(true);
        webSetting.setAppCacheEnabled( true );
        // webSetting.setDatabaseEnabled(true);
        webSetting.setDomStorageEnabled( true );
        webSetting.setGeolocationEnabled( true );
        webSetting.setAppCacheMaxSize( Long.MAX_VALUE );
        // webSetting.setPageCacheCapacity(IX5WebSettings.DEFAULT_CACHE_CAPACITY);
        webSetting.setPluginState( WebSettings.PluginState.ON_DEMAND );
        // webSetting.setRenderPriority(WebSettings.RenderPriority.HIGH);

        // this.getSettingsExtension().setPageCacheCapacity(IX5WebSettings.DEFAULT_CACHE_CAPACITY);//extension
        // settings 的设计

        setWebContentsDebuggingEnabled( true );
        setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
    }

    @Override
    protected boolean drawChild ( Canvas canvas, View child, long drawingTime )
    {
        return super.drawChild( canvas, child, drawingTime );
    }

    public X5WebView ( Context arg0 )
    {
        super( arg0 );
        setBackgroundColor( 85621 );
    }

}
