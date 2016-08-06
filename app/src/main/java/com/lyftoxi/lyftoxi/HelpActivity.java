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

        TextView helpContent =  (TextView) findViewById(R.id.help_content);
        helpContent.setText(Html.fromHtml("<div>\n" +
                "<h2><font color=\"#2b89ab\"><u>Smart Features</u></font></h2>\n" +
                "<h4><font color=\"#2b89ab\">It's your decision</font></h4>\n" +
                "<p>Travel cost is mutually decided by Car Owner and Co-traveller(s) only.</p>\n" +
                "<h4><font color=\"#2b89ab\">Safe &amp; Enjoyable</font></h4>\n" +
                "<p>You can view your co-traveller's profile, call him/her before travel.</p>\n" +
                "<h4><font color=\"#2b89ab\">Economical</font></h4>\n" +
                "<p>Cheaper than any online shared cab facilities. Your cost is simply divided by number of travllers.</p>\n" +
                "<h4><font color=\"#2b89ab\">No surcharge</font></h4>\n" +
                "<p>If online cab is available with 2X…Just share with atleast one person. You will travel with 1X. More you share less you need to pay.</p>\n" +
                "<h4><font color=\"#2b89ab\">Free Free Free</font></h4>\n" +
                "<p>LyftOxi doesn't charge anything from anyone. No hidden cost.</p>\n" +
                "<h4><font color=\"#2b89ab\">Eco Friendly</font></h4>\n" +
                "<p>Produce less CO2. Gift greener world to your bloodline.</p>\n" +
                "<h4><font color=\"#2b89ab\">Still concerned?</font></h4>\n" +
                "<p>\n" +
                "Let's have some more useful information.<br/>\n" +
                "It is legal to offer lift in private vehicle. The Motor Vehicles Act 1988 does not specifically disallow ride sharing or lift giving or carpooling. \n" +
                "<br/>Check here : http://www.tn.gov.in/sta/Mvact1988.pdf\n" +
                "</p>\n" +
                "<p>For rented cab, The cab owner/cab driver has no technical or legal ground to restrict you from offering lift to your friends/family/acquaintance.</p>\n" +
                "<p>We are always ready to provide you more help. \n" +
                "please visit https://www.lyftoxi.com</p>\n" +
                "</div>"));
    }
}