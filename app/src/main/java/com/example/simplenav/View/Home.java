package com.example.simplenav.View;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.simplenav.Controller.BottomListener;
import com.example.simplenav.Controller.ClickListenerForTwok;
import com.example.simplenav.Controller.HomeErrorInitializeListener;
import com.example.simplenav.Controller.SidInitializeError;
import com.example.simplenav.Model.HomeModel;
import com.example.simplenav.Model.CallApi.Sid;
import com.example.simplenav.Controller.SidInitializeListener;
import com.example.simplenav.Model.SidRepository;
import com.example.simplenav.R;
import com.example.simplenav.Controller.TwokListAdapter;


public class Home extends Fragment{
    public final static String TAG = "Home";
    public HomeModel homeModel = HomeModel.getInstance();
    private TextView errorView;
    private ProgressBar progressBar;
    TwokListAdapter adapter;

    public Home() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        progressBar = view.findViewById(R.id.progressBar2);
        progressBar.setVisibility(view.VISIBLE);
        errorView = view.findViewById(R.id.errorMessage);

        SidRepository.initSid(getActivity(), new SidInitializeListener() {

            @Override
            public void onSidInizialized(Sid sid) {
                progressBar.setVisibility(view.GONE);
                Log.d(TAG, "sid ottenuto, possiamo iniziare...");
                ViewPager2 viewPager = view.findViewById(R.id.homeViewPager);
                // recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
                adapter = new TwokListAdapter(homeModel.getTwokListRepository(), view.getContext(), new ClickListenerForTwok() {
                    @Override
                    public void onListClickEvent(int index) {
                        Log.d(TAG, "Click on element with position: " + index);
                        Bundle bundle = new Bundle();
                        bundle.putInt("uid", homeModel.getTwokListRepository().getTwok(index).getUid());
                        Home.this.getFragmentManager().beginTransaction()
                                .setReorderingAllowed(true)
                                .replace(R.id.fragmentContainerView, UserProfile.class, bundle)
                                .addToBackStack(null)
                                .commit();
                    }

                    @Override
                    public void onListClickEventMap(int index) {
                        Log.d(TAG, "Click on map with position: " + index);
                        if (homeModel.getTwokListRepository().getTwok(index).getLat() != null && homeModel.getTwokListRepository().getTwok(index).getLon() != null) {
                            Bundle bundle = new Bundle();
                            bundle.putDouble("lat", homeModel.getTwokListRepository().getTwok(index).getLat());
                            bundle.putDouble("lon", homeModel.getTwokListRepository().getTwok(index).getLon());
                            bundle.putString("autore", homeModel.getTwokListRepository().getTwok(index).getAuthor());
                            bundle.putString("twokText", homeModel.getTwokListRepository().getTwok(index).getTwokText());
                            Log.d(TAG, "passo " + homeModel.getTwokListRepository().getTwok(index).getLat() +" "+ homeModel.getTwokListRepository().getTwok(index).getLon());
                            Home.this.getFragmentManager().beginTransaction()
                                .setReorderingAllowed(true)
                                .replace(R.id.fragmentContainerView, MapsFragment.class, bundle)
                                .addToBackStack(null)
                                .commit();
                        } else {
                            AlertDialog newDialog = new AlertDialog.Builder(Home.this.getContext()).create();
                            newDialog.setTitle("Nessuna posizione per il twok selezionato");
                            newDialog.show();
                        }
                    }
                }, new BottomListener() {
                    @Override
                    public void onBottomReached(TwokListAdapter adapter) {
                        homeModel.fiveMoreTwoks(adapter, getActivity(), new HomeErrorInitializeListener() {
                            @Override
                            public void onHomeNotInitialized() {
                                AlertDialog newDialog = new AlertDialog.Builder(Home.this.getContext()).create();
                                newDialog.setTitle("Impossibile caricare altri twok\nverifica connessione di rete");
                                newDialog.show();
                            }
                        });
                    }
                });

                homeModel.init(adapter, getActivity(), new HomeErrorInitializeListener() {
                    @Override
                    public void onHomeNotInitialized() {
                        errorView.setText("Impossibile caricare twok\nverifica connessione di rete");
                    }
                });
                viewPager.setAdapter(adapter);
            }
        }, new SidInitializeError() {
            @Override
            public void onSidNotInizalized(String s) {
                errorView.setText(s);
                progressBar.setVisibility(view.GONE);
            }
        });

        SwipeRefreshLayout swipeRefreshLayout = view.findViewById(R.id.swipe_list_container_home);
        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        Log.d(TAG, "onRefresh called from SwipeRefreshLayout");
                        // This method performs the actual data-refresh operation.
                        // The method calls setRefreshing(false) when it's finished.
                        homeModel.refresh(adapter, getContext(), new HomeErrorInitializeListener() {
                            @Override
                            public void onHomeNotInitialized() {
                                errorView.setText("Impossibile caricare twok\nverifica connessione di rete");
                            }
                        });
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}