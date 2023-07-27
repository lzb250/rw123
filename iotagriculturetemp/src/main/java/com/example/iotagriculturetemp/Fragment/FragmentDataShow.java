package com.example.iotagriculturetemp.Fragment;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.iotagriculturetemp.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


public class FragmentDataShow extends Fragment {

    private TextView tv_message;
    public FragmentDataShow() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onHomeCtrEvent(String recvData) {
        handleSocketReciveData(recvData);
    }

    private void handleSocketReciveData(String recvData) {
        tv_message.setText(recvData);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.layout_fragment_datashow, container, false);

        tv_message = view.findViewById(R.id.tv_message);

        return view;
    }
    public void initView(){

    }
}