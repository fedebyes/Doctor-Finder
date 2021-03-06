package com.doctorfinderapp.doctorfinder.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.doctorfinderapp.doctorfinder.R;
import com.doctorfinderapp.doctorfinder.fragment.DoctorFragment;
import com.doctorfinderapp.doctorfinder.fragment.FeedbackDialogFragment;
import com.doctorfinderapp.doctorfinder.fragment.FeedbackFragment;
import com.doctorfinderapp.doctorfinder.functions.GlobalVariable;
import com.doctorfinderapp.doctorfinder.functions.RoundedImageView;
import com.github.clans.fab.FloatingActionMenu;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.pnikosis.materialishprogress.ProgressWheel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimerTask;

public class DoctorActivity extends AppCompatActivity implements View.OnClickListener, FeedbackFragment.OnFragmentInteractionListener, FragmentManager.OnBackStackChangedListener {


    public static ParseObject DOCTORTHIS;
    //Doctor information
    private static int index;
    public static com.melnykov.fab.FloatingActionButton fabfeedback;
    private static FloatingActionMenu fabmenu;
    public final String EMAIL = "Email";
    private com.github.clans.fab.FloatingActionButton fab_email, fab_message, fab_phone;
    private String DOCTOR_EMAIL = "";
    private static RoundedImageView photoProfile;
    private boolean DOCTOR_SEX;
    private String DOCTOR_FIRST_NAME;
    private String DOCTOR_LAST_NAME;
    private ArrayList<String> DOCTOR_CITY_ARRAY;
    private ArrayList<String> DOCTOR_SPECIALIZATION_ARRAY;
    private Bitmap DOCTOR_PHOTO;
    private List<ParseObject> doctors;
    private static String Title;
    private String email;
    public static FragmentManager fragmentActivity;
    CollapsingToolbarLayout collapsingToolbarLayout;
    public static ProgressWheel progressWheel;
    public static CoordinatorLayout coordinator_layout;
    static Context context;
    static TimerTask task;


    //switch fab
    public static void switchFAB(int position) {
        switch (position) {
            case 0:
                if (fabfeedback.isVisible())
                    fabfeedback.hide();

                fabmenu.showMenu(true);

                break;

            case 1:
                fabmenu.hideMenu(true);
                fabfeedback.setVisibility(View.VISIBLE);
                fabfeedback.show();

                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        fragmentActivity=getSupportFragmentManager();
        setContentView(R.layout.activity_doctor);
        coordinator_layout=(CoordinatorLayout) findViewById(R.id.coordinator_doctor_activity);
        //take index
        Bundle extras = getIntent().getExtras();

        doctors = GlobalVariable.DOCTORS;

        progressWheel = (ProgressWheel) findViewById(R.id.progress_doctor);
        progressWheel.setBarColor(getResources().getColor(R.color.colorPrimaryDark));
        progressWheel.spin();

        context = getApplicationContext();
        //set ParseDoctor this
        if (extras != null) {

            email = extras.getString("email");
            index = extras.getInt("index");
            //Log.d("EXTRAS ===> ", "email: " + email + " " + "index: " + index);
        }

        if (email == null) {
            DOCTORTHIS = doctors.get(index);

        } else {

            ParseQuery<ParseObject> doctorQuery = ParseQuery.getQuery("Doctor");
            doctorQuery.whereEqualTo(EMAIL, email);

            doctorQuery.getFirstInBackground(new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject object, ParseException e) {
                    if (e == null) {
                        DOCTORTHIS = object;

                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

                        DoctorFragment doctorFragment = DoctorFragment.newInstance(index);
                        ft.replace(R.id.frame_doctor, doctorFragment);

                        ft.commit();
                        new GetSet().execute();
                        Log.d("IN BACKGROUND", "OK");
                    }
                }
            });
        }

        // Begin the transaction
        if (email == null && DOCTORTHIS != null) {
            Log.d("NON IN BACKROUND", "OK");
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

            DoctorFragment doctorFragment = DoctorFragment.newInstance(index);
            ft.replace(R.id.frame_doctor, doctorFragment);

            ft.commit();
        }

        doctors = GlobalVariable.DOCTORS;

        //get and set
        photoProfile = (RoundedImageView) findViewById(R.id.doctor_propic);
        if (email == null && DOCTORTHIS != null)
            new GetSet().execute();

        //find fab buttons
        fabfeedback = (com.melnykov.fab.FloatingActionButton) findViewById(R.id.fabfeedback);
        fabmenu = (FloatingActionMenu) findViewById(R.id.fab_menu);
        fab_email = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.fab_email);
        fab_message = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.fab_message);
        fab_phone = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.fab_phone);

        //onClick button
        fabfeedback.setOnClickListener(this);
        fab_email.setOnClickListener(this);
        fab_phone.setOnClickListener(this);
        fab_message.setOnClickListener(this);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_doctor);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar_doc);
        collapsingToolbarLayout.setTitle(Title);
        collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(R.color.transparent));

        collapsingToolbarLayout.setCollapsedTitleTextColor(Color.rgb(255, 255, 255));

    }

    public void getAndSetDoctorInformation() {

        DOCTOR_FIRST_NAME = DOCTORTHIS.getString("FirstName");
        DOCTOR_LAST_NAME = DOCTORTHIS.getString("LastName");
        DOCTOR_SEX = DOCTORTHIS.getString("Sesso").equals("M");
        DOCTOR_CITY_ARRAY = (ArrayList<String>) DOCTORTHIS.get("Province");
        DOCTOR_SPECIALIZATION_ARRAY = (ArrayList<String>) DOCTORTHIS.get("Specialization");
        DOCTOR_EMAIL = DOCTORTHIS.getString(EMAIL);

        if (DOCTOR_SEX)
            Title = "Dott. " + DOCTOR_FIRST_NAME + " " + DOCTOR_LAST_NAME;
        else
            Title = "Dott.ssa " + DOCTOR_FIRST_NAME + " " + DOCTOR_LAST_NAME;

        if (email != null)
            collapsingToolbarLayout.setTitle(Title);

        //update recent search
        final ParseObject doctor = new ParseObject("recentDoctor");
        doctor.put("FN", DOCTOR_FIRST_NAME);
        doctor.put("LN", DOCTOR_LAST_NAME);
        doctor.put("E@", DOCTOR_EMAIL);
        doctor.put("SPEC", DOCTOR_SPECIALIZATION_ARRAY);
        doctor.put("CITY", DOCTOR_CITY_ARRAY);
        doctor.put("SEX", DOCTOR_SEX);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        doctor.put("DATE", simpleDateFormat.format(Calendar.getInstance().getTime()));

        ParseQuery<ParseObject> recentSearch = ParseQuery.getQuery("recentDoctor");
        recentSearch.orderByDescending("DATE");
        recentSearch.fromLocalDatastore();
        recentSearch.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    if (objects.size() > 9) {

                        try {
                            objects.get(9).unpin();
                        } catch (ParseException e1) {
                            e1.printStackTrace();
                        }
                    }

                    boolean flag = true;
                    for (int i = 0; i < objects.size(); i++) {
                        if (objects.get(i).getString("FN").equals(doctor.getString("FN"))
                                && objects.get(i).getString("LN").equals(doctor.getString("LN")))
                            flag = false;
                    }

                    if (flag)
                        try {
                            doctor.pin();
                        } catch (ParseException e1) {
                            e1.printStackTrace();
                        }
                }
            }
        });

        getDoctorPhoto();
    }


    class GetSet extends AsyncTask<Void,Void, Void>{

        @Override
        protected Void doInBackground(Void... params) {
            getAndSetDoctorInformation();
            return null;
        }
    }


    public void getDoctorPhoto(){

        final ParseQuery<ParseObject> doctorph = ParseQuery.getQuery("DoctorPhoto");
        doctorph.whereEqualTo(EMAIL, DOCTOR_EMAIL);

        doctorph.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject doctorPhoto, ParseException e) {

                if (doctorPhoto == null) {
                    Log.d("doctorphoto", DOCTOR_EMAIL + " isNull");
                } else {

                    ParseFile file = (ParseFile) doctorPhoto.get("profilePhoto");
                    if (e == null) {

                        file.getDataInBackground(new GetDataCallback() {
                            @Override
                            public void done(byte[] data, ParseException e) {
                                DOCTOR_PHOTO = BitmapFactory.decodeByteArray(data, 0, data.length);
                                Log.d("DOCTOR PHOTO --> ", DOCTOR_PHOTO == null ? "is null" : "ok");
                                photoProfile.setImageBitmap(DOCTOR_PHOTO);
                            }
                        });
                    }
                }
            }
        });
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                FragmentManager fm = getSupportFragmentManager();
                if (fm.getBackStackEntryCount() > 0) {
                    fm.popBackStack();
                } else {
                    super.onBackPressed();
                    finish();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {

            case R.id.fabfeedback:

                if (ParseUser.getCurrentUser() != null) {
                    openFeedbackDialog();

                } else {

                }
                break;


            case R.id.fab_email:
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto",DOCTORTHIS.get("Email").toString(), null));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Richiesta informazioni");
                //emailIntent.putExtra(Intent.EXTRA_BCC, "info@doctorfinderapp.com");
                emailIntent.putExtra(Intent.EXTRA_TEXT, "" +
                        " \n " +
                        " \n " +
                        " \n " +
                        " \n " +
                        " \n " +

                        " \n " +
                        " \n " +
                        "Messaggio inviato tramite Doctor Finder ");
                startActivity(Intent.createChooser(emailIntent, "Invia mail"));

                // Verify that the intent will resolve to an activity
                if (emailIntent.resolveActivity(getPackageManager()) != null) {
                    //startActivity(emailIntent);
                }
                break;

            case R.id.fab_phone:

                    final String no = DOCTORTHIS.getString("Cellphone");
                TelephonyManager tm = (TelephonyManager)getSystemService(TELEPHONY_SERVICE);
                if( tm != null && tm.getSimState()==TelephonyManager.SIM_STATE_READY){
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:"+no));
                    startActivity(intent);}


                break;

            case R.id.fab_message:
                sendSMS();
                break;
        }
    }

    private void sendSMS(){

        final String no = DOCTORTHIS.getString("Cellphone");

        startActivity(new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", no, null)));

    }


    private void openFeedbackDialog() {

        DialogFragment newFragment = new FeedbackDialogFragment().newInstance(DOCTOR_EMAIL);

        newFragment.show(getSupportFragmentManager(), "feedback");

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onBackPressed() {
        Log.d("Doctor Activity", " On back pressed");

        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            Log.d("Doctor Activity", "" + getSupportFragmentManager().getBackStackEntryCount());
            getSupportFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
            finish();
        }
    }

    @Override
    public void onBackStackChanged() {
        Log.d("Doctor Activity", " On back stqck changed");
        shouldDisplayHomeUp();
    }

    public void shouldDisplayHomeUp() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            Log.d("Doctor Activity", "" + getSupportFragmentManager().getBackStackEntryCount());
            getSupportFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
            finish();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {

        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            Log.d("Doctor Activity", "" + getSupportFragmentManager().getBackStackEntryCount());
            getSupportFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
            finish();
        }
        return true;
    }

    public static Drawable getDocPhoto(){
        return photoProfile.getDrawable();
    }

    public static String getDocName(){
        return Title;
    }

}
