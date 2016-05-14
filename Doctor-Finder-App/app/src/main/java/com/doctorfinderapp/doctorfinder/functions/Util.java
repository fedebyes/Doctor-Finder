package com.doctorfinderapp.doctorfinder.functions;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.util.Log;
import android.view.View;

import com.doctorfinderapp.doctorfinder.R;
import com.doctorfinderapp.doctorfinder.fragment.DoctorFragment;
import com.doctorfinderapp.doctorfinder.fragment.DoctorListFragment;
import com.doctorfinderapp.doctorfinder.objects.Doctor;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;

/**
 * Created by francesco on 02/03/16.
 */
public class Util {

    public static final String FRIENDS = "friends";
    public static final String FACEBOOK = "Facebook";
    public static final String USER = "_User";
    public static final String ID = "facebookId";
    public static final String FEEDBACK = "Feedback";
    public static final String NAME = "fName";
    public static final String SURNAME = "lName";
    public static final String USER_EMAIl = "email_user";
    public static final String EMAIl = "email";
    public static final String DOCTOR_EMAIL = "email_doctor";
    public static final String ANONYMOUS = "Anonymus";

    public static String setSpecialization(ArrayList<String> specialization) {

        String specializationString = "";
        //divido le spec
        specializationString += specialization.get(0);

        if (specialization.size() > 1) {
            if (specialization.get(0).length() >= 12)
                specializationString += ", " + specialization.get(1).subSequence(0, 6) + "...";
            else if (specialization.get(1).length() < 12)
                specializationString += ", " + specialization.get(1);
            else specializationString += ", " + specialization.get(1).subSequence(0, 6) + "...";
        }
        return specializationString;
        /**finish setting specialization*/
    }

    public static String[][] setPosition(ArrayList<HashMap> position) {
        String[][] positionString = new String[position.size()][2];

        HashMap currentMap;

        if (position.size() == 1) {
            currentMap = position.get(0);
            positionString[0][0] = currentMap.get("Lat").toString();
            positionString[0][1] = currentMap.get("Long").toString();
        } else {
            for (int i = 0; i < position.size(); i++) {
                currentMap = position.get(i);
                positionString[i][0] = currentMap.get("Lat").toString();
                positionString[i][1] = currentMap.get("Long").toString();
            }
        }
        return positionString;
    }

    public static String reduceString(String string) {
        if (string.length() > 12) return string.substring(0, 12) + "...";
        else return string;
    }

    public static String setCity(ArrayList<String> city) {
        String res = "";
        for (int i = 0; i < city.size(); i++) {
            if (i == city.size() - 1) res += city.get(i);
            else res += city.get(i) + ", ";
        }
        return res;
    }


    public static Intent getOpenFacebookIntent(Context context) {

        try {
            context.getPackageManager().getPackageInfo("com.facebook.katana", 0);
            return new Intent(Intent.ACTION_VIEW, Uri.parse("fb://page/1523582397968672"));
        } catch (Exception e) {
            return new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/dcfind"));
        }
    }


    public static List<ParseObject> getUserFacebookFriends(ParseUser user) {
        List<String> id = new ArrayList<>();
        List<ParseObject> friends = new ArrayList<>();
        if (user == null) return friends;
        if (user.getString(FACEBOOK) != null)
            if (!user.getString(FACEBOOK).equals("true")) return friends;

        if (user.getString(FRIENDS) == null) return friends;
        if (user.getString(FACEBOOK).equals("true"))
            id = Arrays.asList(user.get(FRIENDS).toString().split(","));

        for (int i = 0; i < id.size(); i++) {
            // Log.d("AMICO --> ", id.get(i));
        }
        //Log.d("getUserFacebookfriend", ""+id.size());


        ParseQuery<ParseObject> friendQuery = ParseQuery.getQuery(USER);

        friendQuery.whereContainedIn(ID, id);

        try {
            friends = friendQuery.find();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return friends;
    }

    public static List<ParseObject> getUserFacebookFriendsAndFeedback(String doctor_email, List<ParseObject> friends_objects) {

        //Log.d("EMAIL DOCTOR --> ", doctor_email);

        List<ParseObject> friends = friends_objects;

        ArrayList<String> friends_email = new ArrayList<>();

        for (int i = 0; i < friends.size(); i++)
            friends_email.add(friends.get(i).getString(EMAIl));

        ParseQuery<ParseObject> feedback = ParseQuery.getQuery(FEEDBACK);
        feedback.whereEqualTo(DOCTOR_EMAIL, doctor_email);
        feedback.whereContainedIn(USER_EMAIl, friends_email);
        feedback.whereEqualTo(ANONYMOUS, false);


        try {
            friends = new ArrayList<>(feedback.find());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < friends.size(); i++) {
            //Log.d("ALL FEEDBACK --> ", friends.get(i).getString(USER_EMAIl));
        }

        friends_email.clear();
        for (int i = 0; i < friends.size(); i++) {
            friends_email.add(friends.get(i).getString(USER_EMAIl));
        }

        ParseQuery<ParseObject> ret = ParseQuery.getQuery(USER);
        ret.whereContainedIn(EMAIl, friends_email);

        try {
            friends = new ArrayList<>(ret.find());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < friends.size(); i++) {
            //Log.d("SUGGEST FEEDBACK --> ", friends.get(i).getString(EMAIl));
        }

        return friends;
    }

    public static void calculateFeedback(final String doctor_email) {


        final ParseQuery<ParseObject> query = ParseQuery.getQuery("Doctor");
        query.whereEqualTo("Email", doctor_email);
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            public void done(ParseObject doctor, ParseException e) {

                if (doctor == null) {
                    //Log.d("calculate feedback", "Error doctor not exists ");
                } else {

                    ParseQuery query2 = ParseQuery.getQuery("Feedback");
                    query2.whereEqualTo("email_doctor", doctor_email);
                    //Log.d("Feedback", doctor.toString());
                    try {
                        List<ParseObject> objects = (List<ParseObject>) query2.find();
                        Float somma = 0f;
                        for (int i = 0; i < objects.size(); i++) {
                            String f = objects.get(i).get("Rating").toString();
                            //Log.d("Feedback", f);
                            somma = somma + Float.parseFloat(f);
                        }

                        float media = 0;
                        if (objects.size() != 0) {
                            media = somma / objects.size();
                            doctor.put("Feedback", media);

                        } else
                            doctor.put("Feedback", 0.0f);

                        Log.d("PUTTO --> ", media + "");
                        DoctorFragment.changeRating(media);
                        DoctorListFragment.refreshList();
                        doctor.save();


                    } catch (ParseException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }


    public static void copyAll() {
        final ParseQuery<ParseObject> query = ParseQuery.getQuery("DoctorJSON");

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> doctorjson, ParseException e) {
                if (e == null) {

                    Log.d("Util CopyAll", doctorjson.size() + "");
                    ParseQuery<ParseObject> query2 = ParseQuery.getQuery("Doctor");
                    ArrayList<String> assenti = new ArrayList<String>();
                    ArrayList<ParseObject> assentiObject = new ArrayList<ParseObject>();
                    try {
                        ArrayList<ParseObject> doctor =
                                (ArrayList<ParseObject>) query2.find();
                        Log.d("Util CopyAll", doctor.size() + "");

                        for (int q = 0; q < doctorjson.size(); q++) {
                            boolean trovato=false;
                            String emailJSON=doctorjson.get(q).getString("Email");
                            Log.d("Util CopyAll", "finding "+emailJSON.toString());
                            for(int p=0;p<doctor.size();p++){

                                String email=doctor.get(p).getString("Email");
                                if(email.equals(emailJSON)){
                                    trovato=true;
                                    Log.d("Util CopyAll", "found "+emailJSON.toString()+" = " + email.toString());
                                }
                            }
                            if(trovato==false){
                                assenti.add(emailJSON);
                                assentiObject.add(doctorjson.get(q));

                            }
                        }
                        Log.d("Util CopyAll", "found "+assenti.size()+" dottori assenti");
                        Log.d("Util CopyAll", "found "+assentiObject.size()+" dottori assenti");
                        for(String assente:assenti){
                            //Log.d("Util CopyAll", assente.toString());
                        }
                        for (int i = 0; i < assentiObject.size(); i++) {


                            ParseObject d = new ParseObject("Doctor");
                            ParseObject DOCTORTHIS = assentiObject.get(i);
                            String DOCTOR_FIRST_NAME = DOCTORTHIS.getString("FirstName");
                            String DOCTOR_LAST_NAME = DOCTORTHIS.getString("LastName");
                            String DOCTOR_EXPERIENCE = DOCTORTHIS.getString("Exp");
                            String DOCTOR_FEEDBACK = (String) DOCTORTHIS.get("Feedback");
                            String DOCTOR_EMAIL = DOCTORTHIS.getString("Email");
                            String DOCTOR_SEX = DOCTORTHIS.getString("Sesso");
                            String DOCTOR_DESCRIPTION = DOCTORTHIS.getString("Description");
                            ArrayList<String> DOCTOR_WORK_ARRAY = (ArrayList<String>) DOCTORTHIS.get("Work");
                            ArrayList<String> DOCTOR_CITY_ARRAY = (ArrayList<String>) DOCTORTHIS.get("Province");
                            ArrayList<String> DOCTOR_SPECIALIZATION_ARRAY = (ArrayList<String>) DOCTORTHIS.get("Specialization");
                            String DOCTOR_CELLPHONE = DOCTORTHIS.getString("Cellphone");
                            String DOCTOR_VISIT = DOCTORTHIS.getString("Visit");
                            String DOCTOR_BORN = DOCTORTHIS.getString("Born");
                            String DOCTOR_PRICE = DOCTORTHIS.getString("Price");
                            ArrayList<String> DOCTOR_Marker = (ArrayList<String>) DOCTORTHIS.get("Marker");

                            Log.d("Util CopyAll", DOCTOR_EMAIL.toString()+"EMAIL DOC ADD");

                            d.put("FirstName", DOCTOR_FIRST_NAME);
                            d.put("LastName", DOCTOR_LAST_NAME);
                            d.put("Exp", DOCTOR_EXPERIENCE);
                            d.put("Feedback", DOCTOR_FEEDBACK);
                            d.put("Email", DOCTOR_EMAIL);
                            d.put("Sesso", DOCTOR_SEX);
                            d.put("Description", DOCTOR_DESCRIPTION);
                            d.put("Work", DOCTOR_WORK_ARRAY);
                            d.put("Province", DOCTOR_CITY_ARRAY);
                            d.put("Specialization", DOCTOR_SPECIALIZATION_ARRAY);
                            d.put("Cellphone", DOCTOR_CELLPHONE);
                            d.put("Visit", DOCTOR_VISIT);
                            d.put("Born", DOCTOR_BORN);
                            d.put("Price", DOCTOR_PRICE);
                            d.put("Marker", DOCTOR_Marker);
                            try {
                                d.save();
                            } catch (ParseException e1) {
                                e1.printStackTrace();
                            }


                        }





                    } catch (ParseException e1) {
                        e1.printStackTrace();
                    }


                }else{
                    Log.d("Util CopyAll", e.toString());
                }


            }
        });


    }


    public static void dowloadDoctorPhoto(List<ParseObject> doctors) {
        GlobalVariable.DOCTORPHOTO = new ArrayList<>(doctors.size());

        //Log.d("Download Doctor Photo", "doctors.size()" + doctors.size());

        for (int i = 0; i < doctors.size(); i++) {
            final ParseObject CURRENTDOCTOR = doctors.get(i);
            GlobalVariable.DOCTORPHOTO.add(i, new byte[]{});
            if (CURRENTDOCTOR != null) {
                final ParseQuery<ParseObject> doctorph = ParseQuery.getQuery("DoctorPhoto");
                doctorph.whereEqualTo("Email", CURRENTDOCTOR.get("Email").toString());

                final int finalI = i;
                doctorph.getFirstInBackground(new GetCallback<ParseObject>() {
                    @Override
                    public void done(ParseObject doctorPhoto, ParseException e) {

                        if (doctorPhoto == null) {
                            //Log.d("doctorphoto", CURRENTDOCTOR.get("Email").toString() + " isNull");


                        } else {
                            //Log.d("doctorphoto", CURRENTDOCTOR.get("Email").toString() + " exists");
                            final ParseFile file = (ParseFile) doctorPhoto.get("profilePhoto");
                            if (e == null) {

                                file.getDataInBackground(new GetDataCallback() {
                                    @Override
                                    public void done(byte[] data, ParseException e) {
                                        if (e == null) {
                                            //Log.d("doctorphoto", CURRENTDOCTOR.get("Email").toString() + " downloaded");
                                            GlobalVariable.DOCTORPHOTO.add(finalI, data);
                                        } else {
                                            //Log.d("doctorphoto", CURRENTDOCTOR.get("Email").toString() + " exception" + e.toString());
                                        }


                                    }
                                });
                            }
                        }
                    }
                });


            }
        }


    }


    public static void sendFeedbackMail(Activity a) {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", "info@doctorfinderapp.com", null));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Feedback app");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "" +
                " \n " +
                " \n " +
                " \n " +
                " \n " +
                " \n " +
                " \n " +
                " \n " +
                "Messaggio inviato tramite Doctor Finder ");
        a.startActivity(Intent.createChooser(emailIntent, "Invia mail"));

        // Verify that the intent will resolve to an activity
        if (emailIntent.resolveActivity(a.getPackageManager()) != null) {
            //startActivity(emailIntent);
        }
    }

    public static List<Doctor> transformList(List<ParseObject> objects) {
        ArrayList<Doctor> ret = new ArrayList<>();
        int num = objects.size();
        for (int i = 0; i < num; i++) {

            String FN = objects.get(i).getString("FN");
            String LN = objects.get(i).getString("LN");
            String EM = objects.get(i).getString("E@");
            ArrayList<String> CITY = (ArrayList<String>) objects.get(i).get("CITY");
            ArrayList<String> SPEC = (ArrayList<String>) objects.get(i).get("SPEC");
            boolean SEX = objects.get(i).getBoolean("SEX");
            ret.add(0, new Doctor(FN, LN, SPEC, CITY, SEX, EM));
        }

        return ret;
    }

    public static List<String[]> transformSearch(List<ParseObject> objects) {
        ArrayList<String[]> ret = new ArrayList<>();
        int num = objects.size();
        for (int i = 0; i < num; i++) {

            String SPEC = objects.get(i).getString("SPEC");
            String CITY = objects.get(i).getString("CITY");
            String[] linear = new String[]{SPEC, CITY};
            ret.add(0, linear);
        }
        return ret;
    }

    public static void SnackBarFiga(final com.melnykov.fab.FloatingActionButton fab, View v, String text) {

        if (fab == null) {
            Snackbar.make(v, text, Snackbar.LENGTH_LONG)
                    .setAction("Action", null)
                    .show();
        } else {
            final float fab_up = v.getResources().getDimension(R.dimen.fab_up);
            final float fab_down = v.getResources().getDimension(R.dimen.fab_down);
            Snackbar.make(v, text, Snackbar.LENGTH_LONG)
                    .setAction("Action", null)
                    .setCallback(new Snackbar.Callback() {
                        @Override
                        public void onDismissed(Snackbar snackbar, int event) {
                            ViewCompat.animate(fab).translationYBy(fab_up).setInterpolator(new FastOutLinearInInterpolator()).withLayer();
                            super.onDismissed(snackbar, event);
                        }
                    })
                    .show();
            ViewCompat.animate(fab).translationYBy(fab_down).setInterpolator(new FastOutLinearInInterpolator()).withLayer();
        }
    }

    public static String FIRSTNAME = "FirstName";
    public static String LASTNAME = "LastName";
    public static String EXPERIENCE = "Exp";
    public static String EMAIL_DOCTOR = "Email";
    public static String SESSO = "Sesso";
    public static String PHONE = "Cellphone";
    public static String DESCRIPTION = "Description";
    public static String WORK = "Work";
    public static String PROVINCE = "Province";
    public static String SPECIALIZATION = "Specialization";
    public static String VISIT = "Visit";
    public static String BORN = "Born";
    public static String PRICE = "Price";
    public static String MARKER = "Marker";
    public static String DOCTOR = "Doctor";
    public static String PROFILE_PHOTO = "profilePhoto";
    public static String DOCTOR_PHOTO = "DoctorPhoto";



    public static void addDoctor(final String email, final String firstName, final String lastName, final String visit, final String description, final String price,
                                 final String[] work, final String[] province, final String experience, final String born, final String[] specialization, final String sesso, final String phone,
                                 final ArrayList<HashMap> marker, final Resources res, final int doctorDrawable){

        ParseQuery<ParseObject> doctorExists = ParseQuery.getQuery("provaDoctor");
        doctorExists.whereEqualTo(EMAIL_DOCTOR, email);
        doctorExists.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if (e == null && object != null){
                    Log.d("UTIL.ADD DOCTOR ==> ", object.getString(EMAIL_DOCTOR) + " EXISTS!");
                }

                else if (e == null && object == null){
                    ParseObject doctor = new ParseObject("provaDoctor");
                    doctor.put(FIRSTNAME, firstName);
                    doctor.put(LASTNAME, lastName);
                    doctor.put(EXPERIENCE, experience);
                    doctor.put(EMAIL_DOCTOR, email);
                    doctor.put(FEEDBACK, 0f);
                    doctor.put(SESSO, sesso);
                    doctor.put(DESCRIPTION, description);
                    doctor.put(BORN, born);
                    doctor.put(PHONE, phone);
                    doctor.put(VISIT, visit);
                    doctor.put(PRICE, price);
                    doctor.put(WORK, Arrays.asList(work));
                    doctor.put(SPECIALIZATION, Arrays.asList(specialization));
                    doctor.put(PROVINCE, Arrays.asList(province));
                    doctor.put(MARKER, marker);
                    addPhoto(res, doctorDrawable, email);
                }

                else{
                    Log.d("UTIL.EXCEPTION ==> ", ""); e.printStackTrace();
                }
            }
        });
    }


    public static void addPhoto(final Resources res, int drawable, String doctorEmail){

        Bitmap bm = BitmapFactory.decodeResource(res, drawable);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 80, baos);
        byte[] byteArrayImage = baos.toByteArray();
        ParseFile file = new ParseFile(doctorEmail+"_doctor_profile.jpg", byteArrayImage);

        try {
            file.save();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        // Creazione di un ParseObject da inviare
        ParseObject doctorPhoto = new ParseObject("provaDoctorPhoto");

        doctorPhoto.put(PROFILE_PHOTO, file);
        doctorPhoto.put(EMAIL_DOCTOR, doctorEmail);
        try {
            doctorPhoto.save();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Log.d("addfile","file saved");
        ParseFile get = (ParseFile)doctorPhoto.get(PROFILE_PHOTO);
        try {
            get.getData();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
