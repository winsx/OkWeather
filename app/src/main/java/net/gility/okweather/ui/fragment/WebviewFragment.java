package net.gility.okweather.ui.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import net.gility.okweather.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author Alimy
 */

public class WebviewFragment extends Fragment {
    public static final String TAG = "WebView";
    View rootView;
    @BindView(R.id.webview) public WebView mWebView;
    @BindView(R.id.progressBar) ProgressBar mProgressBar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_webview, container, false);
            ButterKnife.bind(this, rootView);

            WebSettings settings = mWebView.getSettings();
            settings.setSupportZoom(true);
            settings.setBuiltInZoomControls(true);
            settings.setJavaScriptEnabled(true);

            mWebView.setWebChromeClient(new WebFragmentClient());
            mWebView.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    view.loadUrl(url);
                    return true;
                }
            });
            mProgressBar.setMax(100);
        }
        String url = (String) getArguments().get("url");
        //优先使用缓存:
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        mWebView.loadUrl(url);
        return rootView;
    }

    private class WebFragmentClient extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            mProgressBar.setProgress(newProgress);
            if (newProgress == 100) {
                mProgressBar.setVisibility(View.GONE);
            }
            super.onProgressChanged(view, newProgress);
        }
    }
}
