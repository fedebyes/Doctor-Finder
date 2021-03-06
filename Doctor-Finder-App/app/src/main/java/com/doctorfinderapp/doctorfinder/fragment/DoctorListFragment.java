package com.doctorfinderapp.doctorfinder.fragment;


import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.doctorfinderapp.doctorfinder.R;
import com.doctorfinderapp.doctorfinder.activity.MainActivity;
import com.doctorfinderapp.doctorfinder.activity.ResultsActivity;
import com.doctorfinderapp.doctorfinder.adapter.ParseAdapter;
import com.doctorfinderapp.doctorfinder.functions.GlobalVariable;
import com.doctorfinderapp.doctorfinder.functions.Util;
import com.doctorfinderapp.doctorfinder.objects.EndlessRecyclerOnScrollListener;
import com.melnykov.fab.FloatingActionButton;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.pnikosis.materialishprogress.ProgressWheel;

import java.util.List;

import jp.wasabeef.recyclerview.adapters.SlideInBottomAnimationAdapter;


/**
 * Created by francesco on 03/02/16.
 */

public class DoctorListFragment extends Fragment
        implements  SwipeRefreshLayout.OnRefreshListener
{

    private static RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private static ParseAdapter parseAdapter;
    private List<ParseObject> DOCTORS;
    private FloatingActionButton fab;
    private static ProgressWheel progressBar;
    private static SwipeRefreshLayout refresh;
    private static CardView cardNothing;
    private static View rootView;
    private String nienteTesto="Nessun dottore corrispondente alla " +
            "ricerca, ne conosci uno? Usa il bottone in basso per suggerircelo";

    private TextView niente;


    public DoctorListFragment() {
    }

    public static DoctorListFragment newInstance(int index) {
        DoctorListFragment doc = new DoctorListFragment();
        Bundle args = new Bundle();
        args.putInt("index", index);

        doc.setArguments(args);
        return doc;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_doctorlist,
                container, false);
        DoctorListFragment.rootView =rootView;
        progressBar = (ProgressWheel) rootView.findViewById(R.id.progressBarList);
        progressBar.setBarColor(getResources().getColor(R.color.colorPrimaryDark));
        progressBar.spin();

        refresh= (SwipeRefreshLayout) rootView.findViewById(R.id.refresh);
        refresh.setOnRefreshListener(this);


        cardNothing= (CardView) rootView.findViewById(R.id.card_nothing);
        niente= (TextView) rootView.findViewById(R.id.niente);
        niente.setText(nienteTesto);
        //cardNothing.setVisibility(View.VISIBLE);



        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.result_recyclerview);
        mRecyclerView.getMaxFlingVelocity();
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);

        mRecyclerView.setLayoutManager(mLayoutManager);

        DOCTORS = GlobalVariable.DOCTORS;

        parseAdapter = new ParseAdapter(DOCTORS);

        //animation
        SlideInBottomAnimationAdapter slide_adapter=new SlideInBottomAnimationAdapter(parseAdapter);
        orderList("feedback", false);
        mRecyclerView.setAdapter(slide_adapter);

        mRecyclerView.setOnScrollListener(new EndlessRecyclerOnScrollListener(mLayoutManager) {
            @Override
            public void onLoadMore(int current_page) {
                loadMoreData(100, 100);
            }
        });


        //fab
        fab = (com.melnykov.fab.FloatingActionButton) getActivity().findViewById(R.id.fab);

        //attach fab to recycler view on scroll
        fab.attachToRecyclerView(mRecyclerView);

        return rootView;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public static void refreshDoctors(List<ParseObject> filters){
        parseAdapter.animateTo(filters);
        mRecyclerView.scrollToPosition(0);
    }

    public static void setProgressBar(int visibility){
        progressBar.setVisibility(visibility);
        progressBar.stopSpinning();
    }

    public static void refreshList(){
        if (parseAdapter != null)
            parseAdapter.notifyDataSetChanged();
    }

    @Override
    public void onRefresh() {
        Log.d("DoctorListFragment","OnRefresh called");

        refresh.setRefreshing(true);

        if (ResultsActivity.research)
            ResultsActivity.showDatafromAdapter(100);

        else ResultsActivity.showDataM(100);


    }

    public static void CardNothingVisible(){

        cardNothing.setVisibility(View.VISIBLE);

    }

    public static void orderList(String mode, boolean grow){

        parseAdapter.orderBy(mode, grow);
        mRecyclerView.scrollToPosition(0);
    }

    public static void stopRefresh(){

        refresh.setRefreshing(false);
    }

    public static boolean ifNullAdapter(){
        return parseAdapter == null;
    }

    public void loadMoreData(int limit, int skip) {

        //get query: All doctor
        ParseQuery<ParseObject> doctorsQuery = ParseQuery.getQuery("Doctor");

        if (ResultsActivity.research) {
            //retrieve object with multiple city
            if (MainActivity.CITY2.size() != 0 && MainActivity.CITY2.size() != MainActivity.citta.length)
                doctorsQuery.whereContainedIn("Province", MainActivity.CITY2);


            //retrieve object with multiple city
            if (MainActivity.SPECIAL2.size() != 0 && MainActivity.SPECIAL2.size() != MainActivity.special.length)
                doctorsQuery.whereContainedIn("Specialization", MainActivity.SPECIAL2);

            //order by LastName
            if (MainActivity.CITY2.size() != 0 || MainActivity.SPECIAL2.size() != 0) {
                doctorsQuery.orderByAscending("LastName");
            }

        } else {

            if (MainActivity.CITY.size() != 0 && MainActivity.CITY.size() != MainActivity.citta.length)
                doctorsQuery.whereContainedIn("Province", MainActivity.CITY);

            //Log.d("RESULTS SEARCH FOR --> ", MainActivity.CITY + "");

            //retrieve object with multiple city
            if (MainActivity.SPECIAL.size() != 0 && MainActivity.SPECIAL.size() != MainActivity.special.length)
                doctorsQuery.whereContainedIn("Specialization", MainActivity.SPECIAL);

            //order by LastName
            if (MainActivity.CITY.size() != 0 || MainActivity.SPECIAL.size() != 0) {
                doctorsQuery.orderByAscending("LastName");
            }
        }

        doctorsQuery.setLimit(limit);
        doctorsQuery.setSkip(skip);
        doctorsQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {

                if (e == null) {

                    for (ParseObject doctor : objects)
                        GlobalVariable.DOCTORS.add(doctor);

                    Util.dowloadDoctorPhoto(GlobalVariable.DOCTORS);
                    DoctorListFragment.refreshDoctors(GlobalVariable.DOCTORS);
                    DoctorListFragment.setProgressBar(View.GONE);

                    if (objects.size()==0){
                        DoctorListFragment.CardNothingVisible();
                    }

                }
            }
        });

    }

}
