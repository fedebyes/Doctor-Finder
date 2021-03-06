package com.doctorfinderapp.doctorfinder.adapter;

import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RatingBar;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.creativityapps.gmailbackgroundlibrary.BackgroundMail;
import com.doctorfinderapp.doctorfinder.R;
import com.doctorfinderapp.doctorfinder.activity.DoctorActivity;
import com.doctorfinderapp.doctorfinder.fragment.DoctorFragment;
import com.doctorfinderapp.doctorfinder.fragment.FeedbackFragment;
import com.doctorfinderapp.doctorfinder.functions.RoundedImageView;
import com.doctorfinderapp.doctorfinder.functions.Util;
import com.mikepenz.iconics.view.IconicsImageView;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.pnikosis.materialishprogress.ProgressWheel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by fedebyes on 05/03/16.
 */
public class FeedbackAdapter extends RecyclerView.Adapter<FeedbackAdapter.FeedbackViewHolder> {

    List<ParseObject> feedbacklist;
    String EMAIL_DOCTOR_THIS;
    String EMAIL_USER_THIS;
    String FEEDBACK = "Feedback";
    String USER_EMAIL = "email_user";
    String DOCTOR_EMAIL = "email_doctor";
    String NUM_THUMB = "num_thumb";
    String THUMB_LIST = "thumb_list";
    String DATE = "date";
    String NAME = "Name";
    String ANONYMUS = "Anonymus";
    String FEEDBACK_DESCRIPTION = "feedback_description";
    String RATING = "Rating";
    String DOCTOR = "Doctor";
    String EMAIL = "Email";
    String TYPE = "type";
    String PLACE = "place";
    String CORDIALITA = "cordialita_rating";
    String DISPONIBILITA = "disponibilita_rating";
    String SODDISFAZIONE = "soddisfazione_rating";
    String DATE_VISIT = "visit_date";
    ParseObject annulla;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");


    public FeedbackAdapter(List<ParseObject> feedbacks, String doctor_email, String user_email) {
        this.feedbacklist = feedbacks;
        EMAIL_DOCTOR_THIS = doctor_email;
        EMAIL_USER_THIS = user_email;
    }

    @Override
    public FeedbackAdapter.FeedbackViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.feedback_item, parent, false);
        return new FeedbackViewHolder(v);
    }

    public static class FeedbackViewHolder extends RecyclerView.ViewHolder {

        TextView feedback_text;
        RatingBar ratingBar;
        TextView name;
        RoundedImageView propic;
        IconicsImageView spam;
        IconicsImageView clear;
        IconicsImageView thumb;
        ProgressWheel delete_progress;
        TextView num_thumb;
        boolean THUMB_PRESSED;
        TextView date;
        PopupMenu popup;
        View divider;
        CardView feedback_details;
        MaterialDialog dialog;
        TextView custom_feedback;
        RatingBar rating_custom;
        RatingBar rating_disp_custom;
        RatingBar rating_cord_custom;
        RatingBar rating_sodd_custom;
        TextView num_feed_custom;
        TextView num_thumb_custom;
        RoundedImageView user_custom;
        RoundedImageView doctor_custom;
        TextView doctor_name_custom;
        TextView user_name_custom;
        TextView type_custom;
        TextView place_custom;
        TextView date_custom;
        TextView feedback_text_custom;
        String text;

        FeedbackViewHolder(View itemView) {
            super(itemView);
            feedback_text = (TextView) itemView.findViewById(R.id.feedback_text);
            ratingBar = (RatingBar) itemView.findViewById(R.id.ratingBar);
            name = (TextView) itemView.findViewById(R.id.username);
            propic = (RoundedImageView) itemView.findViewById(R.id.user_image_feed);
            spam = (IconicsImageView) itemView.findViewById(R.id.feed_spam);
            clear = (IconicsImageView) itemView.findViewById(R.id.feed_clear);
            thumb = (IconicsImageView) itemView.findViewById(R.id.feed_like);
            num_thumb = (TextView) itemView.findViewById(R.id.num_thumb);
            date = (TextView) itemView.findViewById(R.id.feed_date);
            divider = itemView.findViewById(R.id.divider_feedback);
            delete_progress = (ProgressWheel) itemView.findViewById(R.id.delete_progress);
            feedback_details = (CardView) itemView.findViewById(R.id.card_view_feedback);

            /**dialog view*/
            dialog = new MaterialDialog.Builder(itemView.getContext())
                    .customView(R.layout.feedback_details, false).build();

            custom_feedback = (TextView) dialog.findViewById(R.id.float_feedback_custom);
            rating_custom = (RatingBar) dialog.findViewById(R.id.rating_bar_feedback_custom);
            num_feed_custom = (TextView) dialog.findViewById(R.id.num_feedback_custom);
            num_thumb_custom = (TextView) dialog.findViewById(R.id.num_thumb_custom);
            user_name_custom = (TextView) dialog.findViewById(R.id.user_name_custom);
            doctor_name_custom = (TextView) dialog.findViewById(R.id.doctor_name_custom);
            user_custom = (RoundedImageView) dialog.findViewById(R.id.user_image_feed);
            doctor_custom = (RoundedImageView) dialog.findViewById(R.id.doctor_image_feed);
            rating_disp_custom = (RatingBar) dialog.findViewById(R.id.rating_bar_disp_custom);
            rating_cord_custom = (RatingBar) dialog.findViewById(R.id.rating_bar_cordi_custom);
            rating_sodd_custom = (RatingBar) dialog.findViewById(R.id.rating_bar_sodd_custom);
            date_custom = (TextView) dialog.findViewById(R.id.date_custom);
            type_custom = (TextView) dialog.findViewById(R.id.type_custom);
            place_custom = (TextView) dialog.findViewById(R.id.place_custom);
            feedback_text_custom = (TextView) dialog.findViewById(R.id.feedback_text_custom);
        }
    }

    @Override
    public void onBindViewHolder(final FeedbackAdapter.FeedbackViewHolder holder, final int position) {

            holder.THUMB_PRESSED = feedbacklist.get(position).getList(THUMB_LIST).contains(EMAIL_USER_THIS);

            holder.text = feedbacklist.get(position).get("feedback_description").toString();
            String text_smorx = holder.text;

            if(holder.text.length()>151){
                text_smorx = text_smorx.substring(0,150)+"...";
            }
            String rating = feedbacklist.get(position).get("Rating").toString();
            boolean anonymus = (boolean) feedbacklist.get(position).get("Anonymus");
            holder.propic.setImageResource(R.drawable.mario);
            holder.feedback_text.setText(text_smorx);
            holder.ratingBar.setRating(Float.parseFloat(rating));
            if (!anonymus) {
                String name ;


                String email = feedbacklist.get(position).get("email_user").toString();
                //Log.d("email", email.toString());
                final ParseQuery<ParseObject> userPhoto = ParseQuery.getQuery("UserPhoto");
                userPhoto.whereEqualTo("username", email);
                if (EMAIL_USER_THIS.equals(feedbacklist.get(position).getString(USER_EMAIL))) {
                    name = feedbacklist.get(position).get("Name").toString()+" (Tu) ";
                }else{
                    name = feedbacklist.get(position).get("Name").toString();
                }
                holder.name.setText(name);
                userPhoto.getFirstInBackground(new GetCallback<ParseObject>() {
                    @Override
                    public void done(ParseObject userPhoto, ParseException e) {

                        if (userPhoto == null) {
                            //Log.d("user feedback photo", "isNull");

                        } else {

                            ParseFile file = (ParseFile) userPhoto.get("profilePhoto");
                            //Log.d("user feedback photo", "not null");
                            if (e == null) {

                                file.getDataInBackground(new GetDataCallback() {
                                    @Override
                                    public void done(byte[] data, ParseException e) {
                                        holder.propic.setImageBitmap(BitmapFactory.decodeByteArray(data, 0, data.length));
                                    }
                                });
                            } else {
                                e.printStackTrace();
                            }
                        }
                    }
                });

            } else {
                holder.name.setText(R.string.anonymus_name);
            }

            if (EMAIL_USER_THIS.equals(feedbacklist.get(position).getString(USER_EMAIL))) {
                holder.divider.setVisibility(View.VISIBLE);
                holder.spam.setVisibility(View.INVISIBLE);
                holder.spam.setClickable(false);
                holder.clear.setVisibility(View.VISIBLE);
                holder.clear.setClickable(true);
                holder.propic.getLayoutParams().height = (int) holder.itemView.getResources().getDimension(R.dimen.feed_image);
                holder.propic.getLayoutParams().width = (int) holder.itemView.getResources().getDimension(R.dimen.feed_image);
                FeedbackFragment.fabfeedback.setImageResource(R.drawable.ic_create_white_24dp);
            }

            holder.date.setText(feedbacklist.get(position).getString(DATE));

            if (ParseUser.getCurrentUser().getEmail() != null)
                if (feedbacklist.get(position).getList(THUMB_LIST).contains(EMAIL_USER_THIS))
                    holder.thumb.setColor(holder.itemView.getResources().getColor(R.color.colorPrimaryDark));
                else holder.thumb.setColor(holder.itemView.getResources().getColor(R.color.grey));

            holder.num_thumb.setText(String.valueOf(feedbacklist.get(position).getInt(NUM_THUMB)));

            holder.thumb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.thumb.setClickable(false);
                    holder.THUMB_PRESSED = !holder.THUMB_PRESSED;

                    onClickButton(v, holder, position);
                }
            });

            holder.spam.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickButton(v, holder, position);
                }
            });

            holder.clear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickButton(v, holder, position);
                }
            });

            holder.feedback_details.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickButton(v, holder, position);
                }
            });
    }

    @Override
    public int getItemCount() {
        //Log.d("Feedback", "" + feedbacklist.size());
        return feedbacklist.size();
    }

    public void onClickButton(final View v, final FeedbackViewHolder holder, final int position) {
        final int id = v.getId();

        switch (id) {
            case R.id.feed_spam:

                //get instance of menu
                holder.popup = new PopupMenu(holder.itemView.getContext(), holder.spam);
                holder.popup.getMenuInflater().inflate(R.menu.menu_card_feedback, holder.popup.getMenu());
                holder.popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        if (item.getItemId() == R.id.action_spam) {

                            new MaterialDialog.Builder(v.getContext())
                                    .title("Report Spam")
                                    .content("Descrivi perchè secondo te questo feedback deve essere eliminato")
                                    .inputType(InputType.TYPE_MASK_CLASS | InputType.TYPE_TEXT_FLAG_IME_MULTI_LINE)
                                    .widgetColor(holder.itemView.getResources().getColor(R.color.docfinder))
                                    .negativeColor(holder.itemView.getResources().getColor(R.color.docfinder))
                                    .positiveColor(holder.itemView.getResources().getColor(R.color.docfinder))
                                    .input("Testo", null, new MaterialDialog.InputCallback() {
                                        @Override
                                        public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {

                                            Log.d("INPUT", input.toString());

                                            final String body = "Doctor: " + EMAIL_DOCTOR_THIS + "\n" + "\n" +
                                                    "User Feedback: " + EMAIL_USER_THIS + "\n" + "\n" +
                                                    "User Spammer: " + feedbacklist.get(holder.getAdapterPosition()).getString(USER_EMAIL) + "\n" + "\n" +
                                                    "Feedback text: " + holder.feedback_text.getText() + "\n" +
                                                    "User Text: " + input.toString();

                                            BackgroundMail.newBuilder(v.getContext())
                                                    .withUsername("doctor.finder.dcf@gmail.com")
                                                    .withPassword(Util.PASSWORD)
                                                    .withMailto("doctor.finder.dcf@gmail.com")
                                                    .withSubject("REPORT SPAM")
                                                    .withBody(body)
                                                    .send();

                                            Util.SnackBarFiga(FeedbackFragment.fabfeedback, v, "Grazie per la segnalazione!");

                                        }
                                    }).positiveText("Invia")
                                    .negativeText("Annulla")
                                    .show();
                        }
                        return true;
                    }
                });

                holder.popup.show();
                break;

            case R.id.feed_clear:

                //get instance of menu
                holder.popup = new PopupMenu(holder.itemView.getContext(), holder.clear);
                holder.popup.getMenuInflater().inflate(R.menu.menu_card_clear, holder.popup.getMenu());
                holder.popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        if (item.getItemId() == R.id.action_clear) {
                            holder.delete_progress.setVisibility(View.VISIBLE);
                            holder.delete_progress.spin();
                            deleteFeedback(position, holder);
                        }
                        return true;
                    }
                });

                holder.popup.show();
                break;

            case R.id.feed_like:
                holder.thumb.setClickable(false);
                switchColorAndThumb(holder, position);
                break;

            case R.id.card_view_feedback:
                holder.user_custom.setImageDrawable(holder.propic.getDrawable());
                holder.user_name_custom.setText(holder.name.getText().toString());
                holder.doctor_custom.setImageDrawable(DoctorActivity.getDocPhoto());
                holder.doctor_name_custom.setText(DoctorActivity.getDocName());
                holder.num_thumb_custom.setText(holder.num_thumb.getText().toString());
                holder.num_feed_custom.setText(DoctorFragment.getNumFeed());
                holder.custom_feedback.setText(String.format("%.1f", holder.ratingBar.getRating()));
                holder.rating_custom.setRating(holder.ratingBar.getRating());
                holder.rating_disp_custom.setRating(Float.parseFloat(feedbacklist.get(position).get(DISPONIBILITA).toString()));
                holder.rating_cord_custom.setRating(Float.parseFloat(feedbacklist.get(position).get(CORDIALITA).toString()));
                holder.rating_sodd_custom.setRating(Float.parseFloat(feedbacklist.get(position).get(SODDISFAZIONE).toString()));
                holder.place_custom.setText(feedbacklist.get(position).getString(PLACE));
                holder.type_custom.setText(feedbacklist.get(position).getString(TYPE));
                holder.date_custom.setText(simpleDateFormat.format(feedbacklist.get(position).getDate(DATE_VISIT)));
                holder.feedback_text_custom.setText(holder.text);
                holder.dialog
                        .getBuilder()
                        .positiveText("chiudi")
                        .show();

        }
    }

    public void switchColorAndThumb(FeedbackViewHolder holder, int position) {
        Animation resize_big = AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.resize_big);
        holder.thumb.startAnimation(resize_big);

        if (holder.THUMB_PRESSED) {
            holder.thumb.setColor(holder.itemView.getResources().getColor(R.color.colorPrimaryDark));
            holder.num_thumb.setText(String.valueOf(Integer.parseInt(holder.num_thumb.getText().toString()) + 1));
            ThumbUtil(holder.THUMB_PRESSED, position, holder);

        } else {
            holder.thumb.setColor(holder.itemView.getResources().getColor(R.color.grey));
            holder.num_thumb.setText(String.valueOf(Integer.parseInt(holder.num_thumb.getText().toString()) - 1));
            ThumbUtil(holder.THUMB_PRESSED, position, holder);
        }
    }

    public void ThumbUtil(final boolean THUMB_PRESSED, int position, final FeedbackViewHolder holder) {

        final Animation resize_small = AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.resize_small);
        final String user_email = feedbacklist.get(position).getString(USER_EMAIL);
        ParseQuery<ParseObject> feedback = ParseQuery.getQuery(FEEDBACK);
        feedback.whereEqualTo(DOCTOR_EMAIL, EMAIL_DOCTOR_THIS);
        feedback.whereEqualTo(USER_EMAIL, user_email);
        feedback.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if (e != null) {
                    //error

                } else {

                    //case of thumb was pressed and we need to put object and increment 1 like
                    if (THUMB_PRESSED) {

                        if (EMAIL_USER_THIS != null) {
                            object.add(THUMB_LIST, EMAIL_USER_THIS);

                            object.increment(NUM_THUMB, 1);
                            try {
                                object.save();
                            } catch (ParseException e1) {
                                e1.printStackTrace();
                            }
                        }

                        //case of thumb wasn't pressed and we need to remove object from thumb list and decrement 1 like
                    } else if (!THUMB_PRESSED) {

                        List<String> thumbList = object.getList(THUMB_LIST);

                        if (ParseUser.getCurrentUser().getEmail() != null)
                            thumbList.remove(EMAIL_USER_THIS);

                        object.put(THUMB_LIST, thumbList);
                        object.increment(NUM_THUMB, -1);
                        try {
                            object.save();
                        } catch (ParseException e1) {
                            e1.printStackTrace();
                        }
                    }
                    holder.thumb.setClickable(true);
                    holder.thumb.startAnimation(resize_small);
                }
            }
        });
    }

    public void deleteFeedback(final int position, final FeedbackViewHolder holder){

        annulla = feedbacklist.get(position);
        ParseQuery<ParseObject> delete = ParseQuery.getQuery(FEEDBACK);
        delete.whereEqualTo(DOCTOR_EMAIL, EMAIL_DOCTOR_THIS);
        delete.whereEqualTo(USER_EMAIL, EMAIL_USER_THIS);
        delete.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                    if (e == null) {
                        object.deleteEventually();
                        feedbacklist.remove(position);
                        notifyItemRemoved(position);
                        FeedbackFragment.fabfeedback.setImageResource(R.drawable.ic_add_white_24dp);
                        DoctorFragment.minus1();
                        holder.delete_progress.stopSpinning();
                        holder.delete_progress.setVisibility(View.GONE);
                        Util.calculateFeedback(EMAIL_DOCTOR_THIS);

                        //recover feedback
                        View v = holder.itemView;
                        final float fab_up = v.getResources().getDimension(R.dimen.fab_up);
                        final float fab_down = v.getResources().getDimension(R.dimen.fab_down);
                        Snackbar.make(v, "Eliminato!", Snackbar.LENGTH_LONG)
                                .setActionTextColor(v.getResources().getColor(R.color.docfinder))
                                .setAction("Annulla", new View.OnClickListener() {

                                    @Override
                                    public void onClick(View v) {
                                        holder.clear.setClickable(false);
                                        safeSave(annulla);
                                        annulla = null;
                                        holder.clear.setClickable(true);
                                        holder.delete_progress.setVisibility(View.GONE);
                                        FeedbackFragment.fabfeedback.setImageResource(R.drawable.ic_create_white_24dp);
                                        Util.calculateFeedback(EMAIL_DOCTOR_THIS);
                                        DoctorFragment.plus1();
                                    }})
                                .setCallback(new Snackbar.Callback() {
                                    @Override
                                    public void onDismissed(Snackbar snackbar, int event) {
                                        super.onDismissed(snackbar, event);
                                        ViewCompat.animate(FeedbackFragment.fabfeedback).translationYBy(fab_up).setInterpolator(new FastOutLinearInInterpolator()).withLayer();
                                    }
                                })
                                .show();
                        ViewCompat.animate(FeedbackFragment.fabfeedback).translationYBy(fab_down).setInterpolator(new FastOutLinearInInterpolator()).withLayer();
                    }
            }
        });
    }

    public void insertItem(ParseObject item){
        feedbacklist.add(0,item);
        notifyItemInserted(0);
        FeedbackFragment.scroolTo(0);
    }

    public void changeMyFeedback(){
        notifyItemChanged(0);
    }


    public void safeSave(final ParseObject object){
        ParseObject feedback = new ParseObject(FEEDBACK);
        feedback.put(USER_EMAIL, object.getString(USER_EMAIL));
        feedback.put(DOCTOR_EMAIL, object.getString(DOCTOR_EMAIL));
        feedback.put(NAME, object.getString(NAME));
        feedback.put(ANONYMUS, object.getBoolean(ANONYMUS));
        feedback.put(FEEDBACK_DESCRIPTION, object.getString(FEEDBACK_DESCRIPTION));
        feedback.put(RATING, Float.parseFloat(object.get(RATING).toString()));
        feedback.put(DATE, object.getString(DATE));
        feedback.put(THUMB_LIST, object.getList(THUMB_LIST));
        feedback.put(NUM_THUMB, object.getInt(NUM_THUMB));
        feedback.put(TYPE, object.getString(TYPE));
        feedback.put(PLACE, object.getString(PLACE));
        feedback.put(CORDIALITA, Float.parseFloat(object.get(CORDIALITA).toString()));
        feedback.put(DISPONIBILITA, Float.parseFloat(object.get(DISPONIBILITA).toString()));
        feedback.put(SODDISFAZIONE, Float.parseFloat(object.get(SODDISFAZIONE).toString()));
        feedback.put(DATE_VISIT, object.getDate(DATE_VISIT));
        feedback.saveEventually(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                insertItem(object);
            }
        });
    }
}