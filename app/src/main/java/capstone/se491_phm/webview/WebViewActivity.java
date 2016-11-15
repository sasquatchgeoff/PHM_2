package capstone.se491_phm.webview;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;

import capstone.se491_phm.MainActivity;
import capstone.se491_phm.R;

/**
 * Created by Advait on 14-11-2016.
 */

public class WebViewActivity extends Activity
{
    private WebView webView;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        webView = (WebView) findViewById(R.id.webView1);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("http://phm.oregonresearchmethod.org/home.html");


    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            //webView.goBack();
            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(i);
        } else {
            super.onBackPressed();
        }
    }
}
