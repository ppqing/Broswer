package com.shenhaiqing.broswer;

import android.content.Context;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private WebView webView;
    private EditText editText;
    private ProgressBar loadProgressBar;
    final String index ="https://www.baidu.com/";
    private boolean noPicFlag =false;
    private boolean homeFlag =false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        Button go =(Button) findViewById(R.id.go);
        editText=(EditText) findViewById(R.id.editText2);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView arg0, int arg1, KeyEvent arg2) {
                if (arg1 == EditorInfo.IME_ACTION_GO) {
                    init();
                    hintKb();
                }
                return true;
            }
        });
        loadProgressBar=(ProgressBar)findViewById(R.id.loadding_progress);
        Button back =(Button) findViewById(R.id.back);
        Button ahead =(Button) findViewById(R.id.ahead);
        Button nopic =(Button) findViewById(R.id.nopic);
        Button power =(Button) findViewById(R.id.power);
        Button home =(Button) findViewById(R.id.home);
        go.setOnClickListener(this);
        back.setOnClickListener(this);
        ahead.setOnClickListener(this);
        nopic.setOnClickListener(this);
        power.setOnClickListener(this);
        home.setOnClickListener(this);
        open(index);
        homeFlag=true;
        editText.setText("");
    }
    public void onClick(View v){
        switch (v.getId()){
            case R.id.go:
                init();
                hintKb();//打开网页后关闭软键盘
                break;
            case R.id.back:
                if(webView.canGoBack()) {
                    webView.goBack();//返回上一页面
                }
                else {
                    Toast.makeText(getApplicationContext(), "已无上一个页面", Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.ahead:
                if (webView.canGoForward()) {
                    webView.goForward();
                }
                else {
                    Toast.makeText(getApplicationContext(), "已到达当前页面", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.nopic:
                if(!noPicFlag){
                    webView.getSettings().setBlockNetworkImage(true);
                    noPicFlag=true;
                    Toast.makeText(getApplicationContext(), "已打开无图模式", Toast.LENGTH_SHORT).show();
                }
                else if(noPicFlag) {
                    webView.getSettings().setBlockNetworkImage(false);
                    noPicFlag=false;
                    Toast.makeText(getApplicationContext(), "已关闭无图模式", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.home:
                open("https://baidu.com");
                Toast.makeText(getApplicationContext(), "返回主页", Toast.LENGTH_SHORT).show();
                homeFlag = true;
                editText.setText("");
                break;
            case R.id.power:
                Toast.makeText(getApplicationContext(), "再见~", Toast.LENGTH_SHORT).show();
                finish();
                break;
            default:
                break;
        }
    }
    public void init(){
        String input =editText.getText().toString();
        if(input.matches("[a-zA-z]+://[^\\s]*"))
            open(input);
                    //匹配网址是否包含协议，若不包含，则添加
        else open("http://"+input);
        homeFlag=false;
        }


    private void open(String u){
        webView = (WebView) findViewById(R.id.webView);
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        //WebView加载web资源
        webView.loadUrl(u);
        //覆盖WebView默认使用第三方或系统默认浏览器打开网页的行为，使网页用WebView打开
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
                view.loadUrl(url);
                return true;
            }
            public void onPageFinished(WebView view,String url)
            {
                if(!homeFlag){
                    editText.setText(webView.getUrl());
                }
            }

        });
        webView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int newProgress) {
                if(newProgress==100){
                    loadProgressBar.setVisibility(View.GONE);//加载完网页进度条消失
                }
                else{
                    loadProgressBar.setVisibility(View.VISIBLE);//开始加载网页时显示进度条
                    loadProgressBar.setProgress(newProgress);//设置进度值
                }
            }
        });

    }
    //关闭软键盘方法
    private void hintKb() {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        if(imm.isActive()&&getCurrentFocus()!=null){
            if (getCurrentFocus().getWindowToken()!=null) {
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK)
        {
            if(webView.canGoBack()) {
                webView.goBack();//返回上一页面
                return true;
            }
            else {
                finish();//退出程序
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
