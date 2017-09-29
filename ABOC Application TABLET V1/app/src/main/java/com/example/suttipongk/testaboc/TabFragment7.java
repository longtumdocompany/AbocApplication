package com.example.suttipongk.testaboc;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;



/**
 * Created by TOPPEE on 7/23/2017.
 */
public class TabFragment7 extends Fragment implements View.OnClickListener {
    private IFragmentToActivity mCallback;
    private ImageButton btnFtoA;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_fragment_7, container, false);

        btnFtoA = (ImageButton) view.findViewById(R.id.elderlybutton);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.elderlybutton:
                mCallback.showToast("Chat Room");
//                Intent mainActivityChat = new Intent("applicationB.intent.action.Launch");
                Intent mainActivityChat = new Intent(getContext(), ChatMainActivity.class);
                startActivity(mainActivityChat);
                break;
        }
    }
}
