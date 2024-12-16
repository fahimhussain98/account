package wts.com.accountpe.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;

import wts.com.accountpe.R;
import wts.com.accountpe.activities.AddMoneyActivity;
import wts.com.accountpe.activities.AddUserActivity;
import wts.com.accountpe.activities.AdminQrCodeActivity;
import wts.com.accountpe.activities.BankDetailActivity;
import wts.com.accountpe.activities.ChangeMpinActivity;
import wts.com.accountpe.activities.ChangePasswordActivity;
import wts.com.accountpe.activities.ChangeTpin;
import wts.com.accountpe.activities.CreditDebitBalanceActivity;
import wts.com.accountpe.activities.DocumentActivity;
import wts.com.accountpe.activities.FundTransferActivity;
import wts.com.accountpe.activities.MyCommissionActivity;
import wts.com.accountpe.activities.ViewCustomerActivity;

public class OthersFragment extends Fragment {

    LinearLayout creditLayout, debitLayout, fundRequestLayout, myCommissionLayout,addMoneyLayout, addUserLayout, viewUsersLayout, changePasswordLayout,
            changeMpinLayout, bankDetailLayout, documentLayout, qrcodeLayout, privacyPolicyLayout, termConditionLayout;
    LinearLayout mantraLayout, morphoLayout,startekLayout,changeTPIN;
    String userTypeId;
    SharedPreferences sharedPreferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_others, container, false);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        userTypeId = sharedPreferences.getString("userTypeId", null);

        initViews(view);

//        if (!userTypeId.equalsIgnoreCase("6"))
        if(userTypeId != null && !userTypeId.equalsIgnoreCase("6"))
        {

            addUserLayout.setVisibility(View.VISIBLE);
            viewUsersLayout.setVisibility(View.VISIBLE);
            creditLayout.setVisibility(View.VISIBLE);
            debitLayout.setVisibility(View.VISIBLE);
        }

        creditLayout.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), CreditDebitBalanceActivity.class);
            intent.putExtra("title", "Credit Balance");
            startActivity(intent);
        });

        debitLayout.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), CreditDebitBalanceActivity.class);
            intent.putExtra("title", "Debit Balance");
            startActivity(intent);
        });

        addUserLayout.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), AddUserActivity.class));
        });

        viewUsersLayout.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), ViewCustomerActivity.class));
        });

        fundRequestLayout.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), FundTransferActivity.class));
        });


        changePasswordLayout.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), ChangePasswordActivity.class));
        });

        addMoneyLayout.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), AddMoneyActivity.class));
        });

        myCommissionLayout.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), MyCommissionActivity.class));

        });

        changeMpinLayout.setOnClickListener(view1 -> {
            Intent intent=new Intent(getActivity(), ChangeMpinActivity.class);
            intent.putExtra("pinType","mpin");
            startActivity(intent);
        });

        bankDetailLayout.setOnClickListener(view1 -> {
               startActivity(new Intent(getActivity(), BankDetailActivity.class));

        });

        documentLayout.setOnClickListener(view1 -> {
            startActivity(new Intent(getActivity(), DocumentActivity.class));
        });

        qrcodeLayout.setOnClickListener(view1 -> {
            startActivity(new Intent(getActivity(), AdminQrCodeActivity.class));
        });

        privacyPolicyLayout.setOnClickListener(view1 -> {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://accountpe.in/policy.aspx")));
        });

        termConditionLayout.setOnClickListener(view1 -> {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://accountpe.in/TermAndCondition.aspx")));
        });

        startekLayout.setOnClickListener(view1 -> {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.mantra.rdservice&hl=en_IN&gl=US")));

        });

        morphoLayout.setOnClickListener(view1 -> {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.scl.rdservice&hl=en_IN&gl=US")));

        });

        mantraLayout.setOnClickListener(view1 -> {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.acpl.registersdk&hl=en_IN&gl=US")));

        });
        changeTPIN.setOnClickListener(view1->{
            startActivity(new Intent(getActivity(), ChangeTpin.class));

        });

        return view;
    }


    private void initViews(View view) {
        creditLayout = view.findViewById(R.id.credit_layout);
        debitLayout = view.findViewById(R.id.debit_layout);
        fundRequestLayout = view.findViewById(R.id.fund_request_layout);
        addUserLayout = view.findViewById(R.id.add_user_layout);
        viewUsersLayout = view.findViewById(R.id.view_users_layout);
        changePasswordLayout = view.findViewById(R.id.change_password_layout);
        changeMpinLayout = view.findViewById(R.id.changeMpinLayout);
        bankDetailLayout = view.findViewById(R.id.bankDetail_layout);
        documentLayout = view.findViewById(R.id.document_layout);
        qrcodeLayout = view.findViewById(R.id.qrcode_layout);
        privacyPolicyLayout = view.findViewById(R.id.privacyPolicy_layout);
        termConditionLayout= view.findViewById(R.id.termCondition_layout);
        addMoneyLayout = view.findViewById(R.id.add_money_layout);
        myCommissionLayout = view.findViewById(R.id.my_commission_layout);
        morphoLayout = view.findViewById(R.id.morpho_layout);
        mantraLayout = view.findViewById(R.id.startekLayout);
        startekLayout = view.findViewById(R.id.mantraLayout);
        changeTPIN = view.findViewById(R.id.changeTPIN);
    }
}