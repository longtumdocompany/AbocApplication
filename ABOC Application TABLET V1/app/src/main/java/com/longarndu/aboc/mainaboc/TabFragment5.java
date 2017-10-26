package com.longarndu.aboc.mainaboc;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.longarndu.aboc.mainaboc.R;

/**
 * Created by TOPPEE on 7/23/2017.
 */
public class TabFragment5 extends Fragment implements View.OnClickListener {
    private IFragmentToActivity mCallback;
    private ImageButton btnFtoA;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_fragment_5, container, false);
        btnFtoA = (ImageButton) view.findViewById(R.id.iotbutton);
        btnFtoA.setOnClickListener(this);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallback = (IFragmentToActivity) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement IFragmentToActivity");
        }
    }

    @Override
    public void onDetach() {
        mCallback = null;
        super.onDetach();
    }

    public void onRefresh() {
        Toast.makeText(getActivity(), "Fragment 5: Refresh called.", Toast.LENGTH_SHORT).show();
    }

    public void fragmentCommunication() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iotbutton:
                mCallback.showToast("Hello from Fragment 5");
                Intent androidIOTWebviewActivity = new Intent(getContext(),AndroidIOTWebviewActivity.class);			//เปิด Scan Paper
                startActivity(androidIOTWebviewActivity);
                break;
        }
    }
}
