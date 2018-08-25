package com.shifu.user.mynewsfeed;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;

public class FragmentNews extends Fragment implements OnBackPressed {

    private WebView mWebView;
    private View mProgressView;

    private boolean isLoaded = true;

    private class MyWebViewClient extends WebViewClient
    {
        // for old API
        @SuppressWarnings("deprecation")
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url)
        {
            view.loadUrl(url);
            return true;
        }

        // for latest APIs
        @RequiresApi(Build.VERSION_CODES.N)
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request)
        {
            view.loadUrl(request.getUrl().toString());
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            if (isLoaded) {
                super.onPageFinished(view, url);
                showProgress(false);
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.news_layout, container, false);

        ImageButton button = rootView.findViewById(R.id.backButton);
        button.setOnClickListener(view -> getFragmentManager().popBackStackImmediate());

        mProgressView = rootView.findViewById(R.id.progress);

        mWebView = rootView.findViewById(R.id.web_view);
        //mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setWebViewClient(new MyWebViewClient());
        Bundle args = getArguments();
        if (args != null || args.getString("url") != null) {

            String url = args.getString("url");
            mWebView.loadUrl(url);
            showProgress(true);
        }

        return rootView;
    }

    @Override
    public void onBackPressed() {

        if(mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            getActivity().getSupportFragmentManager().popBackStack();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mProgressView.setVisibility(View.GONE);
        isLoaded = false;
    }

    @Override
    public void onResume(){
        super.onResume();
        isLoaded = true;
    }

    private void showProgress(final boolean show) {
        mWebView.setVisibility(show ? View.GONE : View.VISIBLE);
        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
    }
}