package wts.com.accountpe.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.source.DocumentSource;

import wts.com.accountpe.R;

public class DocumentActivity extends AppCompatActivity {

    ImageView imgPancard, imgIncomeTax, imgGstForm, imgInCorpoCerti, msmeCerti, accountPeCerti;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document);

        imgPancard = findViewById(R.id.img_pancard);
        imgIncomeTax = findViewById(R.id.imgIncomeTax);
        imgGstForm = findViewById(R.id.img_formGst);
        imgInCorpoCerti = findViewById(R.id.img_Corpo_Certi);
        msmeCerti = findViewById(R.id.img_msmeCerti);
        accountPeCerti = findViewById(R.id.img_accountPe_Certi);

        imgPancard.setOnClickListener(view ->
                {
                    Dialog dialog = new Dialog(DocumentActivity.this);
                    dialog.setContentView(R.layout.see_document_dialog);

                    dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    dialog.getWindow().setBackgroundDrawableResource(R.drawable.home_dash_back_white);

                    ImageView imgDocument = dialog.findViewById(R.id.img_seeDocument);
                    imgDocument.setImageResource(R.drawable.cleint_pancard);

                    dialog.show();

                });

        imgIncomeTax.setOnClickListener(view ->
        {
            Dialog dialog = new Dialog(DocumentActivity.this);
            dialog.setContentView(R.layout.see_document_dialog);

            ImageView imgDocument = dialog.findViewById(R.id.img_seeDocument);
            imgDocument.setImageResource(R.drawable.income_tax);

            dialog.show();

        });

        imgGstForm.setOnClickListener(view ->
        {
            Intent in = new Intent(DocumentActivity.this, PdfViewActivity.class);
            in.putExtra("pdfType", "gst");
            startActivity(in);
        });
        imgInCorpoCerti.setOnClickListener(view ->
        {
            Intent in = new Intent(DocumentActivity.this, PdfViewActivity.class);
            in.putExtra("pdfType", "incorpo");
            startActivity(in);
        });

        msmeCerti.setOnClickListener(view ->
        {
            Intent in = new Intent(DocumentActivity.this, PdfViewActivity.class);
            in.putExtra("pdfType", "msme");
            startActivity(in);
        });

        accountPeCerti.setOnClickListener(view ->
        {
            Intent in = new Intent(DocumentActivity.this, PdfViewActivity.class);
            in.putExtra("pdfType", "accountPe");
            startActivity(in);
        });

     //   pdfView.fromAsset("gstcertificate.pdf").load();

    }
}