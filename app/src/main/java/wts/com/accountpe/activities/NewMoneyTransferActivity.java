package wts.com.accountpe.activities;

import static wts.com.accountpe.retrofit.RetrofitClient.AUTH_KEY;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import wts.com.accountpe.R;
import wts.com.accountpe.adapters.ViewPagerAdapter;
import wts.com.accountpe.fragments.AddBeneFragment;
import wts.com.accountpe.fragments.AddUpiFragment;
import wts.com.accountpe.fragments.BeneficariesFragment;
import wts.com.accountpe.models.RecipientModel;
import wts.com.accountpe.retrofit.RetrofitClient;

public class NewMoneyTransferActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    public static ViewPager viewPager;
    TextView tvName, tvAvailableLimit, tvConsumedLimit, tvTotalLimit;
    public static String senderMobileNumber;
   public static String senderName, availableLimit, totalLimit, consumedLimit,remitterId;
    public String selectedTextMode;
    String deviceId, deviceInfo;
    String userId;
    SharedPreferences sharedPreferences;

    public static ArrayList<RecipientModel> dmtArrayList, upiDmtArrayList;
    public static boolean isBeneCountZero = true;

    public static boolean shouldRefreshActivity=true;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_money_transfer);
        initViews();

        shouldRefreshActivity=true;

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(NewMoneyTransferActivity.this);
        deviceId = sharedPreferences.getString("deviceId", null);
        deviceInfo = sharedPreferences.getString("deviceInfo", null);
        userId = sharedPreferences.getString("userid", null);

        senderName = getIntent().getStringExtra("senderName");
        senderMobileNumber = getIntent().getStringExtra("senderMobileNumber");
        selectedTextMode = getIntent().getStringExtra("mode");
        availableLimit = getIntent().getStringExtra("availableLimit");
        consumedLimit = getIntent().getStringExtra("consumedLimit");
        totalLimit = getIntent().getStringExtra("totalLimit");
        remitterId=getIntent().getStringExtra("remitterId");

        tvName.setText(senderName + " (" + senderMobileNumber + ")");
        tvAvailableLimit.setText("Available Limit\n" + "₹" + availableLimit);
        tvConsumedLimit.setText("Consumed Limit\n" + "₹" + consumedLimit);
        tvTotalLimit.setText("Total Limit\n" + "₹" + totalLimit);

        //setUpViewPager();


    }

    @SuppressLint("SuspiciousIndentation")
    @Override
    protected void onResume() {
        super.onResume();
        if (shouldRefreshActivity)
        isSenderValidate();
    }

    private void isSenderValidate() {
        ProgressDialog pDialog = new ProgressDialog(NewMoneyTransferActivity.this);
        pDialog.setCancelable(false);
        pDialog.setMessage("Loading...");
        pDialog.show();


        Call<JsonObject> call = RetrofitClient.getInstance().getApi().isUserValidate(AUTH_KEY, deviceId, deviceInfo, userId, SenderValidationActivity.senderMobileNumber);

        call.enqueue(new Callback<JsonObject>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JSONObject jsonObject = null;


                    try {
                        jsonObject = new JSONObject(String.valueOf(response.body()));

                        dmtArrayList = new ArrayList<>();
                        upiDmtArrayList = new ArrayList<>();

                        String beneListStr = jsonObject.getString("benelist");
                        if (beneListStr.equalsIgnoreCase("false")) {
                            isBeneCountZero = true;
                        } else {
                            JSONArray beneListArray = new JSONArray(beneListStr);
                            for (int i = 0; i < beneListArray.length(); i++) {
                                RecipientModel recipientModel = new RecipientModel();

                                JSONObject beneListObject = beneListArray.getJSONObject(i);
                                String bankAccountNumber = beneListObject.getString("AccountNo");
                                String bankName = beneListObject.getString("BankName");
                                String ifsc = beneListObject.getString("IfscCode");
                                String recipientId = beneListObject.getString("BeneId");
                                String recipientName = beneListObject.getString("Name");
                                String beneMobileNo = beneListObject.getString("Mobileno");

                                recipientModel.setBankAccountNumber(bankAccountNumber);
                                recipientModel.setBankName(bankName);
                                recipientModel.setIfsc(ifsc);
                                recipientModel.setRecipientId(recipientId);
                                recipientModel.setRecipientName(recipientName);
                                recipientModel.setMobileNumber(beneMobileNo);

                                if (bankAccountNumber.contains("@"))
                                {
                                    upiDmtArrayList.add(recipientModel);
                                }
                                else
                                {
                                    dmtArrayList.add(recipientModel);
                                }

                            }
                            isBeneCountZero = false;

                        }
                       try {
                           setUpViewPager();
                       }catch (Exception e)
                       {
                           e.printStackTrace();
                       }

                        pDialog.dismiss();

                    } catch (Exception e) {
                        e.printStackTrace();
                        pDialog.dismiss();
                        new AlertDialog.Builder(NewMoneyTransferActivity.this)
                                .setTitle("Alert!!!")
                                .setMessage("Something went wrong.")
                                .setCancelable(false)
                                .setPositiveButton("Ok", (dialogInterface, i) -> finish())
                                .show();
                    }
                } else {
                    pDialog.dismiss();
                    new AlertDialog.Builder(NewMoneyTransferActivity.this)
                            .setTitle("Alert!!!")
                            .setMessage("Something went wrong.")
                            .setCancelable(false)
                            .setPositiveButton("Ok", (dialogInterface, i) -> finish())
                            .show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                pDialog.dismiss();
                new AlertDialog.Builder(NewMoneyTransferActivity.this)
                        .setTitle("Alert!!!")
                        .setMessage("Something went wrong.")
                        .setCancelable(false)
                        .setPositiveButton("Ok", (dialogInterface, i) -> finish())
                        .show();
            }
        });

    }

    private void setUpViewPager() {
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        if (!isBeneCountZero) {
            //viewPagerAdapter.addFragments(new BeneficariesFragment(), "Beneficiary");
            //viewPagerAdapter.addFragments(new TransactionsFragment(), "Transactions");
            viewPagerAdapter.addFragments(new BeneficariesFragment(), "Beneficiary");
        }

      //  viewPagerAdapter.addFragments(new AddBeneFragment(), "Add");
      //  viewPagerAdapter.addFragments(new AddUpiFragment(), "Add UPI");

        if (SenderValidationActivity.dmtType.equalsIgnoreCase("dmt"))
        {
            viewPagerAdapter.addFragments(new AddBeneFragment(), "Add");
        }
        else
        {
            viewPagerAdapter.addFragments(new AddUpiFragment(), "Add UPI");
        }

        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        if (isBeneCountZero) {
            Objects.requireNonNull(tabLayout.getTabAt(0)).setIcon(R.drawable.add_bene);
            //Objects.requireNonNull(tabLayout.getTabAt(1)).setIcon(R.drawable.beneficiary);
            //Objects.requireNonNull(tabLayout.getTabAt(1)).setIcon(R.drawable.list);
            Objects.requireNonNull(tabLayout.getTabAt(1)).setIcon(R.drawable.add_bene);
        } else {
            Objects.requireNonNull(tabLayout.getTabAt(0)).setIcon(R.drawable.beneficiary);
            //Objects.requireNonNull(tabLayout.getTabAt(1)).setIcon(R.drawable.list);
            Objects.requireNonNull(tabLayout.getTabAt(1)).setIcon(R.drawable.add_bene);
            Objects.requireNonNull(tabLayout.getTabAt(2)).setIcon(R.drawable.add_bene);
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