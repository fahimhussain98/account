package wts.com.accountpe.commFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;

import wts.com.accountpe.R;
import wts.com.accountpe.activities.MyCommissionActivity;
import wts.com.accountpe.adapters.CommissionAdapter;
import wts.com.accountpe.models.MyCommissionModel;

public class DmtCommissionFragment extends Fragment {

    ArrayList<MyCommissionModel> myCommissionModelArrayList;
    ListView listView;
    CommissionAdapter commissionAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dmt_commission, container, false);

        myCommissionModelArrayList = MyCommissionActivity.dmtCommissionList;
        listView = view.findViewById(R.id.gas_list);
        commissionAdapter = new CommissionAdapter(getContext(), getActivity(), myCommissionModelArrayList);

        listView.setAdapter(commissionAdapter);
        return view;
    }
}