package wts.com.accountpe.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import wts.com.accountpe.R;

public class RechargeStatusActivity extends AppCompatActivity {

    String responseNumber, responseAmount, responseStatus, responseTransactionId,liveId, responseOperator;

    String outputDate;
    String outputTime;

    ImageView imgStatus;

    TextView tvStatus,tvAmount,tvTransactionId,tvLiveId,tvNumber,tvDate,tvTime,tvOperator;
    Button btnDone;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recharge_status);

        initViews();

        responseNumber=getIntent().getStringExtra("responseNumber");
        responseAmount=getIntent().getStringExtra("responseAmount");
        responseStatus=getIntent().getStringExtra("responseStatus");
        responseTransactionId=getIntent().getStringExtra("responseTransactionId");
        responseTransactionId=getIntent().getStringExtra("responseTransactionId");
        liveId=getIntent().getStringExtra("liveId");
        outputDate=getIntent().getStringExtra("outputDate");
        outputTime=getIntent().getStringExtra("outputTime");
        responseOperator=getIntent().getStringExtra("responseOperator");

        btnDone.setOnClickListener(v -> finish());

        tvStatus.setText(responseStatus);
        tvAmount.setText("₹ "+responseAmount);
        tvTransactionId.setText("Transaction Id :  "+responseTransactionId);
        tvLiveId.setText("Live Id : " + liveId);
        tvNumber.setText("Number :  "+responseNumber);
        tvDate.setText("Date :  "+outputDate);
        tvTime.setText("Time :  "+outputTime);
        tvOperator.setText("Operator :  "+responseOperator);

        if (responseStatus.equalsIgnoreCase("FAILURE") || responseStatus.equalsIgnoreCase("FAILED"))
        {
            imgStatus.setImageResource(R.drawable.failureicon);
        }
        else if (responseStatus.equalsIgnoreCase("PENDING"))
        {
            imgStatus.setImageResource(R.drawable.pending);
        }

    }

    private void initViews() {
        tvStatus=findViewById(R.id.tv_recharge_status);
        tvAmount=findViewById(R.id.tv_amount);
        tvTransactionId=findViewById(R.id.tv_transaction_id);
        tvLiveId=findViewById(R.id.tv_live_id);
        tvNumber=findViewById(R.id.tv_mobile_number);
        tvDate=findViewById(R.id.tv_date);
        tvTime=findViewById(R.id.tv_time);
        tvOperator=findViewById(R.id.tv_operator_name);
        btnDone=findViewById(R.id.btn_done);
        imgStatus=findViewById(R.id.img_status);
    }
}