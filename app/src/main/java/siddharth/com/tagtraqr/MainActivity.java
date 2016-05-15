package siddharth.com.tagtraqr;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class MainActivity extends android.support.v4.app.Fragment {
    private LinearLayoutManager lLayout;
    DeviceDBDataSource dataSource;
    private static final int REQUEST_ENABLE_BT = 1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_main, container, false);

        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);


        dataSource = new DeviceDBDataSource(getActivity());
        dataSource.open();

        List<Devices> tagList = dataSource.findAll();
        lLayout = new LinearLayoutManager(getActivity());

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(lLayout);
        if(tagList.size()!= 0) {

            siddharth.com.tagtraqr.RecyclerViewAdapter rcAdapter = new siddharth.com.tagtraqr.RecyclerViewAdapter(getActivity(), tagList);
            recyclerView.setAdapter(rcAdapter);
        }

        FloatingActionButton myFab = (FloatingActionButton)  view.findViewById(R.id.fab);
        myFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent i = new Intent(getActivity(), BarcodeScanner.class);
                startActivity(i);
                getActivity().finish();

            }
        });

        return view;
    }
}
