package com.doctorfinderapp.doctorfinder;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.doctorfinderapp.doctorfinder.NewSocialShare.MainActivityNewSocialShare;
import com.doctorfinderapp.doctorfinder.SocialShare.MainActivitySocialShare;
import com.doctorfinderapp.doctorfinder.adapter.FacebookAdapter;
import com.doctorfinderapp.doctorfinder.Class.Person;
import com.doctorfinderapp.doctorfinder.SocialShare.ShareTextActivity;
import com.doctorfinderapp.doctorfinder.adapter.PersonAdapter;
import com.doctorfinderapp.doctorfinder.functions.GlobalVariable;
import com.doctorfinderapp.doctorfinder.functions.RoundedImageView;
import com.doctorfinderapp.doctorfinder.functions.Util;
import com.parse.ParseUser;
import cn.pedant.SweetAlert.SweetAlertDialog;


public class UserProfileActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private FacebookAdapter mAdapter;
    private Button segnala;
    public final String USER_EMAIL = "email";
    public static final String NAME = "fName";
    public static final String SURNAME = "lName";
    private String Title ="";
    private RoundedImageView profile;
    private String firstName = "";
    private String lastName = "";
    private TextView utente;
    private TextView email;
    private String email_users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //scrolling
        setContentView(R.layout.activity_scrolling_user);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_user);
        setSupportActionBar(toolbar);

        //se non sei loggato vai via
        ParseUser user = ParseUser.getCurrentUser();

        if(user != null) {

            if (user.getString(NAME) != null)
                firstName = user.get(NAME).toString();

            if (user.getString(SURNAME) != null)
                lastName = user.get(SURNAME).toString();

            if (user.getString(USER_EMAIL) != null)
                email_users = user.get(USER_EMAIL).toString();

            utente = (TextView) findViewById(R.id.txt_users);
            email = (TextView) findViewById(R.id.emaillino);

            utente.setText(firstName + " " + lastName);
            email.setText(email_users);

            Title = firstName + " " + lastName;
            profile = (RoundedImageView) findViewById(R.id.user_photo);
            profile.setImageBitmap(GlobalVariable.UserPropic);

            Log.d("UTENTE --> ", firstName + ", " + lastName + ", " + email_users);
        }

        //recycler view
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_friends);

        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);

        mRecyclerView.setLayoutManager(mLayoutManager);

        //Util.getUserFacebookFriends(ParseUser.getCurrentUser());
        //set adapter to recycler

        mAdapter = new FacebookAdapter(Util.getUserFacebookFriends(user));

        mRecyclerView.setAdapter(mAdapter);

        //nameProfile.setText(Title);
        if(getSupportActionBar()!=null){
            //getSupportActionBar().setTitle(Title);

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        final CollapsingToolbarLayout collapsingToolbarLayout =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar_user);
        collapsingToolbarLayout.setTitle(Title);
        collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(R.color.transparent));
        // transperent color = #00000000
        collapsingToolbarLayout.setCollapsedTitleTextColor(Color.rgb(255, 255, 255));



        //floating button for report problems
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new SweetAlertDialog(UserProfileActivity.this)
                        .setTitleText("Hai qualcosa da segnalarci?")
                        .setContentText("Clicca sul pulsante posto in basso.")
                        .setConfirmText("Riceverai presto nostre notizie!")
                        .show();
            }
        });


        RelativeLayout rateus = (RelativeLayout) findViewById(R.id.rateus);
        rateus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new SweetAlertDialog(UserProfileActivity.this, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                        .setTitleText("Ciao!")
                        .setContentText("Condividi Doctor Finder con i tuoi amici")
                        .setCustomImage(R.drawable.logoverde)
                        .show();
            }
        });

        RelativeLayout condividi = (RelativeLayout) findViewById(R.id.cambiamelo);
        rateus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent_about = new Intent(UserProfileActivity.this, MainActivityNewSocialShare.class);
                startActivity(intent_about);
                 /*
                final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.doctorfinderapp.doctorfinder" + appPackageName)));
                }

                Intent intent_about = new Intent(UserProfileActivity.this, WebViewActivity.class);

                Bundle about = new Bundle();
                about.putString("URL",
                        "https://play.google.com/store/apps/details?id=com.doctorfinderapp.doctorfinder" );
                intent_about.putExtras(about);
                startActivity(intent_about);
                */
            }
        });

        RelativeLayout cambia = (RelativeLayout) findViewById(R.id.inviaci_mail);
        cambia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_SEND);
                               i.setType("message/rfc822");
                              i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"info@doctorfinderapp.com"});
                               i.putExtra(Intent.EXTRA_SUBJECT, "UTENTE DI DOCTOR FINDER");
                                i.putExtra(Intent.EXTRA_TEXT, "Ciao, sto inviando questa mail perchè");
                               try {
                                       startActivity(Intent.createChooser(i, "Invia mail usando..."));
                                   } catch (android.content.ActivityNotFoundException ex) {
                                       Toast.makeText(UserProfileActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                                   }
            }
        });

    }

    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }
}

