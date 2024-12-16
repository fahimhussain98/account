package wts.com.accountpe.activities;

import static wts.com.accountpe.activities.ExpressDmtGetSenderActivity.isBeneCountZero;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.util.Objects;

import wts.com.accountpe.R;
import wts.com.accountpe.adapters.ViewPagerAdapter;
import wts.com.accountpe.fragments.ExpressDmtAddBeneFragment;
import wts.com.accountpe.fragments.ExpressDmtBeneficiaryFragment;

public class ExpressDmtMoneyTransferActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    public static ViewPager viewPager;
    TextView tvName, tvAvailableLimit, tvConsumedLimit, tvTotalLimit;
    public static String senderMobileNumber;
    static String senderName, availableLimit, totalLimit, consumedLimit;
    String deviceId, deviceInfo;
    String userId;
    SharedPreferences sharedPreferences;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_express_dmt_money_transfer);

        initViews();

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ExpressDmtMoneyTransferActivity.this);
        deviceId = sharedPreferences.getString("deviceId", null);
        deviceInfo = sharedPreferences.getString("deviceInfo", null);
        userId = sharedPreferences.getString("userid", null);

        senderName = getIntent().getStringExtra("senderName");
        senderMobileNumber = getIntent().getStringExtra("senderMobileNumber");
        availableLimit = getIntent().getStringExtra("availableLimit");
        consumedLimit = getIntent().getStringExtra("consumedLimit");
        totalLimit = getIntent().getStringExtra("totalLimit");

        tvName.setText(senderName + " (" + senderMobileNumber + ")");
        tvAvailableLimit.setText("Available Limit\n" + "₹" + availableLimit);
        tvConsumedLimit.setText("Consumed Limit\n" + "₹" + consumedLimit);
        tvTotalLimit.setText("Total Limit\n" + "₹" + totalLimit);

        setUpViewPager();

    }

    private void setUpViewPager() {
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        if (!isBeneCountZero) {

            viewPagerAdapter.addFragments(new ExpressDmtBeneficiaryFragment(), "Beneficiary");
            viewPagerAdapter.addFragments(new ExpressDmtAddBeneFragment(), "Add");

        }
        else
        {
            viewPagerAdapter.addFragments(new ExpressDmtAddBeneFragment(), "Add");

        }

        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        if (isBeneCountZero) {
            Objects.requireNonNull(tabLayout.getTabAt(0)).setIcon(R.drawable.add_bene);

        } else {
            Objects.requireNonNull(tabLayout.getTabAt(0)).setIcon(R.drawable.beneficiary);
            Objects.requireNonNull(tabLayout.getTabAt(1)).setIcon(R.drawable.add_bene);

        }

    }
    private void initViews() {
        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tablayout);
        tvName = findViewById(R.id.tv_name);
        tvAvailableLimit = findViewById(R.id.tv_available_limit);
        tvConsumedLimit = findViewById(R.id.tv_consumed_limit);
        tvTotalLimit = findViewById(R.id.tv_total_limit);
    }
}