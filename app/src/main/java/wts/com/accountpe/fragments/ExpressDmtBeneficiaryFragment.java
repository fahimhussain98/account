package wts.com.accountpe.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import wts.com.accountpe.R;
import wts.com.accountpe.activities.ExpressDmtGetSenderActivity;
import wts.com.accountpe.activities.NewMoneyTransferActivity;
import wts.com.accountpe.adapters.ExpressDmtRecipientAdapter;
import wts.com.accountpe.adapters.RecipientAdapter;
import wts.com.accountpe.models.RecipientModel;

public class ExpressDmtBeneficiaryFragment extends Fragment {

    ArrayList<RecipientModel> recipientModelArrayList;
    RecyclerView recipientRecycler;
    String  mobileNumber;
    String userId, userName;
    SharedPreferences sharedPreferences;
    String deviceId,deviceInfo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_express_dmt_beneficiary, container, false);

        initViews(view);

        mobileNumber = NewMoneyTransferActivity.senderMobileNumber;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        userId = sharedPreferences.getString("userid", null);
        userName = sharedPreferences.getString("userNameResponse", null);
        deviceId=sharedPreferences.getString("deviceId",null);
        deviceInfo=sharedPreferences.getString("deviceInfo",null);

        recipientModelArrayList = ExpressDmtGetSenderActivity.dmtArrayList;

        ExpressDmtRecipientAdapter recipientAdapter = new ExpressDmtRecipientAdapter(getContext(), getActivity(),
                recipientModelArrayList, mobileNumber,  userId, userName,deviceId,deviceInfo);
        recipientRecycler.setLayoutManager(new LinearLayoutManager(getContext(),
                RecyclerView.VERTICAL, false));
        recipientRecycler.setAdapter(recipientAdapter);


        return view;
    }

    private void initViews(View view) {
        recipientRecycler = view.findViewById(R.id.recipient_recycler_view);
    }
}