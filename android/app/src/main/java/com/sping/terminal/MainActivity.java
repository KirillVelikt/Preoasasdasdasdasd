package com.sping.terminal;
import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebSettings;
public class MainActivity extends Activity {
 @Override public void onCreate(Bundle b) {
  super.onCreate(b);
  WebView w = new WebView(this);
  WebSettings s = w.getSettings();
  s.setJavaScriptEnabled(true);
  s.setDomStorageEnabled(true);
  s.setAllowFileAccess(true);
  w.setBackgroundColor(0xFF070B09);
  setContentView(w);
  w.loadUrl("file:///android_asset/index.html");
 }
 @Override public void onBackPressed() { super.onBackPressed(); }
}
