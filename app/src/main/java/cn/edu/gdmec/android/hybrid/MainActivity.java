package cn.edu.gdmec.android.hybrid;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {
    private ProgressDialog mProgressDialog;
    private WebView mWebView1,mWebView2;
    private Button mButton1,mButton2,mButton3;
    private EditText et1,username,pass;
    private TextView tv1;
    public class AndroidtoJs extends Object{
        @JavascriptInterface
        public void sayHi(String msg){
            Toast.makeText(MainActivity.this,"Javascript传来的数据是:"+msg,Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mProgressDialog = new ProgressDialog(this);

        mWebView1 = findViewById(R.id.mWedview1);
        mWebView2 = findViewById(R.id.mWedview2);
        mButton1 = findViewById(R.id.button1);
        mButton2 = findViewById(R.id.button2);
        mButton3 = findViewById(R.id.login);
        et1 = findViewById(R.id.editText1);
        tv1 = findViewById(R.id.textView1);
        username = findViewById(R.id.username);
        pass = findViewById(R.id.password);

        mWebView1.getSettings().setJavaScriptEnabled(true);
        mWebView2.getSettings().setJavaScriptEnabled(true);
        mWebView1.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        mWebView2.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);

        mWebView1.addJavascriptInterface(new AndroidtoJs(), "hybrid");
        mWebView1.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
                Toast.makeText(MainActivity.this, "javascript传来的数据是:" + message, Toast.LENGTH_LONG).show();
                return super.onJsAlert(view, url, message, result);
            }
        });
        mWebView1.loadUrl("file:///android_asset/lists.html");

        mWebView2.setWebViewClient(new WebViewClient() {

            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view,url,favicon);
                mProgressDialog.show();
            }

            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                String js = "var script = document.createElement('script');\n" +
                        "script.type = 'text/javascript';\n" +
                        "var username = document.getElementById('username')\n" +
                        "username.value='" + username.getText().toString() + "';\n" +
                        "var password = document.getElementById('password')\n" +
                        "password.value='" + pass.getText().toString() + "';\n" +
                        "var loginbtn = document.getElementById('btn-submit');\n" +
                        "loginbtn.clivk();";
                mWebView1.loadUrl("javascript:" + js);
                mWebView2.loadUrl("javascript:" + js);
                mProgressDialog.hide();
            }
        });
        mWebView1.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Uri uri = Uri.parse(url);
                if (uri.getScheme().equals("js")) {
                    String num = uri.getQueryParameter("arg1");
                    Toast.makeText(MainActivity.this, "javascript传来的数据是：" + num, Toast.LENGTH_LONG).show();
                    return true;
                }
                return super.shouldOverrideUrlLoading(view, url);
            }

            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                mProgressDialog.show();
            }

            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                String js = getFromAsstes("injection.js");
                view.loadUrl("javascript:" + js);
                mProgressDialog.hide();
            }
        });
        mButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mWebView1.loadUrl("javascript:register('" + et1.getText().toString() + "')");
            }
        });
        mButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mWebView1.evaluateJavascript("javascript:register('" + et1.getText().toString() + "')",
                        new ValueCallback<String>() {
                            @Override
                            public void onReceiveValue(String value) {
                                tv1.setText("共" + value + "人注册");
                            }
                        });
            }
        });
        mButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mWebView2.loadUrl("https://login.m.taobao.com/login.htm");
            }
        });
    }
        public String getFromAsstes(String fileName){
            try{
                InputStreamReader inputReader = new InputStreamReader(getResources().getAssets().open(fileName));
                BufferedReader bufReader = new BufferedReader(inputReader);
                String line="";
                String Result="";
                while ((line = bufReader.readLine())!=null)
                    Result += line;
                return Result;
            }catch (Exception e){
                e.printStackTrace();
                return "";
            }
    }

}
