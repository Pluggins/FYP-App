package com.example.myapplication.ui.home;

import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.myapplication.R;
import com.example.myapplication.adapter.MemberOrderAdapter;
import com.example.myapplication.model.MemberOrder;
import com.example.myapplication.service.MemberService;

import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.List;

public class MemberFragment extends Fragment {

    private MemberViewModel memberViewModel;
    private TextView fullName;
    private TextView email;
    private TextView joinedDate;
    private ListView listView;
    private ConstraintLayout memberConstraint;
    private ArrayList<MemberOrder> memberOrders = (ArrayList<MemberOrder>) MemberService.getOrders();
    private MemberOrderAdapter aAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        memberViewModel =
                ViewModelProviders.of(this).get(MemberViewModel.class);
        View root = inflater.inflate(R.layout.fragment_member, container, false);
        final TextView textView = root.findViewById(R.id.memberSeparator1);
        memberViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {

            }
        });

        fullName = (TextView) root.findViewById(R.id.memberFullname);
        email = (TextView) root.findViewById(R.id.memberEmail);
        joinedDate = (TextView) root.findViewById(R.id.memberJoinedSince);
        memberConstraint = (ConstraintLayout) root.findViewById(R.id.memberConstraint);
        if (!MemberService.isMember()) {
            memberConstraint.setVisibility(View.GONE);
        } else {
            fullName.setText(MemberService.getName());
            email.setText(MemberService.getEmail());
            joinedDate.setText("Member joined on "+MemberService.getDateJoined());
        }
        aAdapter = new MemberOrderAdapter(memberOrders, getActivity());
        listView = (ListView) root.findViewById(R.id.memberOrderListView);
        listView.setAdapter(aAdapter);
        return root;
    }
}