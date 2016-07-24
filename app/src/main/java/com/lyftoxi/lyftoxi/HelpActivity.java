package com.lyftoxi.lyftoxi;

import android.os.Bundle;;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

public class HelpActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        TextView helpLyftoxiWebLink =  (TextView) findViewById(R.id.helpLyftoxiWebLink);
        helpLyftoxiWebLink.setText(Html.fromHtml("Visit&nbsp;<a href=\"http://www.lyftoxi.com\">www.lyftoxi.com</a>"));
        helpLyftoxiWebLink.setMovementMethod(LinkMovementMethod.getInstance());
    }
}
