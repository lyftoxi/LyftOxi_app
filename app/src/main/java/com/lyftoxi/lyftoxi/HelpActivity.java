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

 /*       TextView helpLyftoxiWebLink =  (TextView) findViewById(R.id.helpLyftoxiWebLink);
        helpLyftoxiWebLink.setText(Html.fromHtml("For more help visit&nbsp;<a href=\"http://www.lyftoxi.com\">www.lyftoxi.com</a>"));
        helpLyftoxiWebLink.setMovementMethod(LinkMovementMethod.getInstance());

        TextView helpMvact1988WebLink =  (TextView) findViewById(R.id.helpMvact1988WebLink);
        helpLyftoxiWebLink.setText(Html.fromHtml("The Motor Vehicles Act 1988&nbsp;<a href=\"http://www.tn.gov.in/sta/Mvact1988.pdf</a>"));
        helpLyftoxiWebLink.setMovementMethod(LinkMovementMethod.getInstance());*/
    }
}
