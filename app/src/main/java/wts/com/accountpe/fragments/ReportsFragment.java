package wts.com.accountpe.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;

import wts.com.accountpe.R;
import wts.com.accountpe.activities.AddMoneyReportActivity;
import wts.com.accountpe.activities.AepsReportActivity;
import wts.com.accountpe.activities.BbpsReportActivity;
import wts.com.accountpe.activities.ComplaintReportActivity;
import wts.com.accountpe.activities.CreditDebitReportActivity;
import wts.com.accountpe.activities.DaybookReportActivity;
import wts.com.accountpe.activities.DmtReportActivity;
import wts.com.accountpe.activities.ExpressDmtReportActivity;
import wts.com.accountpe.activities.LedgerReportActivity;
import wts.com.accountpe.activities.MyCommissionActivity;
import wts.com.accountpe.activities.OfflineFundReportActivity;
import wts.com.accountpe.activities.PaymentGatewayReportActivity;
import wts.com.accountpe.activities.RechargeReportActivity;
import wts.com.accountpe.activities.SettlementReportActivity;
import wts.com.accountpe.activities.WalletToWalletReportActivity;

public class ReportsFragment extends Fragment {

    LinearLayout rechargeReportLayout,fastagReportLayout, electricityReport, insuranceReport,loanRepayLayout,gasBookingReport,postpaidReport,fundReportLayout, addMoneyReport, complainReport, walletReportLayout,aepsWalletReportLayout, dayBookLayout, creditReportLayout, debitReportLayout,
           addMoney2ReportLayout, myCommissionLayout, settlementReportLayout,aepsReportLayout,dmtReportLayout,expressDmtReportLayout, walletToWalletReportLayout;

    String userTypeId;
    SharedPreferences sharedPreferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reports, container, false);
        initViews(view);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        userTypeId = sharedPreferences.getString("userTypeId", null);

        if (userTypeId != null && !userTypeId.equalsIgnoreCase("6"))
        {
            creditReportLayout.setVisibility(View.VISIBLE);
            debitReportLayout.setVisibility(View.VISIBLE);
        }

        handleClickEvents();

        return view;
    }

    private void handleClickEvents() {
        rechargeReportLayout.setOnClickListener(v ->
        {
            Intent intent = new Intent(getContext(), RechargeReportActivity.class);
            intent.putExtra("isSpecificReport", false);
            startActivity(intent);
        });

        walletReportLayout.setOnClickListener(v ->
        {
            Intent intent = new Intent(getContext(), LedgerReportActivity.class);
            intent.putExtra("title", "Ledger Report");
            startActivity(intent);
        });

        aepsWalletReportLayout.setOnClickListener(v ->
        {
            Intent intent = new Intent(getContext(), LedgerReportActivity.class);
            intent.putExtra("title", "Aeps Ledger Report");
            startActivity(intent);
        });

        dayBookLayout.setOnClickListener(v ->
        {
            Intent intent = new Intent(getContext(), DaybookReportActivity.class);
            startActivity(intent);
        });

        creditReportLayout.setOnClickListener(v ->
        {
            Intent intent = new Intent(getContext(), CreditDebitReportActivity.class);
            intent.putExtra("title", "Credit Report");
            startActivity(intent);
        });

        debitReportLayout.setOnClickListener(v ->
        {
            Intent intent = new Intent(getContext(), CreditDebitReportActivity.class);
            intent.putExtra("title", "Debit Report");
            startActivity(intent);
        });

        myCommissionLayout.setOnClickListener(v ->
        {
            startActivity(new Intent(getContext(), MyCommissionActivity.class));

        });

        settlementReportLayout.setOnClickListener(v ->
        {
            startActivity(new Intent(getContext(), SettlementReportActivity.class));

        });

        aepsReportLayout.setOnClickListener(v->
        {
            startActivity(new Intent(getContext(), AepsReportActivity.class));
        });

        dmtReportLayout.setOnClickListener(v->
        {
            startActivity(new Intent(getContext(), DmtReportActivity.class));

        });

        expressDmtReportLayout.setOnClickListener(view ->
        {
            startActivity(new Intent(requireContext(), ExpressDmtReportActivity.class));
        });

        walletToWalletReportLayout.setOnClickListener(view ->
        {
            startActivity(new Intent(requireContext(), WalletToWalletReportActivity.class));
        });

        electricityReport.setOnClickListener(view ->
        {
            Intent in =new Intent(getActivity(), BbpsReportActivity.class);
            in.putExtra("serviceName", "Electricity");
            startActivity(in);
        });

        fastagReportLayout.setOnClickListener(view ->
        {
            Intent in =new Intent(getActivity(), BbpsReportActivity.class);
            in.putExtra("serviceName", "Fastag");
            startActivity(in);
        });

        insuranceReport.setOnClickListener(view ->
        {
            Intent in =new Intent(getActivity(), BbpsReportActivity.class);
            in.putExtra("serviceName", "Insurance");
            startActivity(in);
        });
        loanRepayLayout.setOnClickListener(view ->
        {
            Intent in =new Intent(getActivity(), BbpsReportActivity.class);
            in.putExtra("serviceName", "Loan Repayment");
            startActivity(in);
        });

        gasBookingReport.setOnClickListener(view ->
        {
            Intent in =new Intent(getActivity(), BbpsReportActivity.class);
            in.putExtra("serviceName", "Gas");
            startActivity(in);
        });

        postpaidReport.setOnClickListener(view ->
        {
            Intent in =new Intent(getActivity(), BbpsReportActivity.class);
            in.putExtra("serviceName", "Postpaid");
            startActivity(in);
        });

        fundReportLayout.setOnClickListener(view -> {
            Intent intent = new Intent(getContext(), OfflineFundReportActivity.class);
            startActivity(intent);
        });

        addMoneyReport.setOnClickListener(view ->
        {
            startActivity(new Intent(getActivity(), AddMoneyReportActivity.class));
          //  startActivity(new Intent(getActivity(), PaymentGatewayReportActivity.class));
        });
        addMoney2ReportLayout.setOnClickListener(view ->
        {
              startActivity(new Intent(getActivity(), AddMoneyReportActivity.class));

        });
        complainReport.setOnClickListener(view ->
        {
            Intent in = new Intent(getContext(), ComplaintReportActivity.class);
            in.putExtra("title", "My Complain Report");
            startActivity(in);
        });

    }

    private void initViews(View view) {
        rechargeReportLayout = view.findViewById(R.id.recharge_report_layout);
        fastagReportLayout = view.findViewById(R.id.fastagReport_layout);
        electricityReport = view.findViewById(R.id.electricityReport_layout);
        insuranceReport = view.findViewById(R.id.insurance_report_layout);
        loanRepayLayout = view.findViewById(R.id.loanRepay_report_layout);
        gasBookingReport = view.findViewById(R.id.gasBooking_report_layout);
        postpaidReport = view.findViewById(R.id.postpaid_report_layout);
        fundReportLayout = view.findViewById(R.id.fundRequest_report_layout);
        addMoneyReport = view.findViewById(R.id.addMoney_layout);
        addMoney2ReportLayout = view.findViewById(R.id.addMoney2_layout);
        complainReport = view.findViewById(R.id.complainReport_layout);
        walletReportLayout = view.findViewById(R.id.wallet_report_layout);
        aepsWalletReportLayout = view.findViewById(R.id.AepsWallet_report_layout);
        dayBookLayout = view.findViewById(R.id.day_book_layout);
        creditReportLayout = view.findViewById(R.id.credit_report_layout);
        debitReportLayout = view.findViewById(R.id.debit_report_layout);
        myCommissionLayout = view.findViewById(R.id.my_commission_layout);
        settlementReportLayout = view.findViewById(R.id.settlement_report_layout);
        aepsReportLayout = view.findViewById(R.id.aeps_reports_layout);
        dmtReportLayout = view.findViewById(R.id.money_report_layout);
        expressDmtReportLayout = view.findViewById(R.id.express_money_report_layout);
        walletToWalletReportLayout = view.findViewById(R.id.wallettoWallet_layout);
    }
}