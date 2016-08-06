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
                "<h2>What is this app</h2>\n" +
                "<p>LyftOxi app is a platform to share your owned or rented car with other co-passengers travelling to same destination.\n" +
                "You need to Sign Up and start enjoying unlimited number of share/find rides without any charge from LyftOxi.</p>\n" +
                "\n" +
                "Such concept is very popular in other part of world. Time to fly with same concept here in India.<br/><br/>\n" +
                "\n" +
                "<h4>It's your decision</h4>Travel cost is mutually decided by Car Owner and Co-traveller(s).\n" +
                "<h4>Safe &amp; Enjoyable</h4>You can view your co-traveller's profile, call him/her before travel.\n" +
                "<h4>Economical</h4>Cheaper than any online shared cab facilities. Your cost is simply divided by number of travllers.\n" +
                "<h4>No surcharge</h4>No need to check whether 2X or 2.5X.\n" +
                "<h4>Free Free Free</h4>LyftOxi doesn't charge anything from anyone. No hidden cost.\n" +
                "<h4>Eco Friendly</h4>Produce less CO2. Gift greener worl to your bloodline.<br/><br/>\n" +
                "\n" +
                "<h4>Still concerned?</h4>\n" +
                "Let's have some more useful information.<br/>\n" +
                "It is legal to offer lift in private vehicle. The Motor Vehicles Act 1988 does not specifically disallow ride sharing or lift giving or carpooling.<br/>\n" +
                "Visit http://www.tn.gov.in/sta/Mvact1988.pdf<br/><br/>\n" +
                "\n" +
                "For rented cab, The cab owner/cab driver has no technical or legal ground to restrict you from offering lift to your friends/family/acquaintance.<br/>\n" +
                "\n" +
                "We are always ready to provide you more help please visit<br/><br/>\n" +
                "\n" +
                "\n" +
                "<div>https://www.lyftoxi.com</div>\n" +
                "</div>"));
    }
}