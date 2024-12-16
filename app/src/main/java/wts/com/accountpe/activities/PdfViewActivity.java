package wts.com.accountpe.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.github.barteksc.pdfviewer.PDFView;

import wts.com.accountpe.R;

public class PdfViewActivity extends AppCompatActivity {

    PDFView pdfView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_view);

        pdfView = findViewById(R.id.pdfView);

        String pdfType = getIntent().getStringExtra("pdfType");

        if (pdfType.equalsIgnoreCase("gst"))
        {
            pdfView.fromAsset("gstcertificate.pdf").load();
        }
        else if (pdfType.equalsIgnoreCase("incorpo"))
        {
            pdfView.fromAsset("incorporation_certificate.pdf").load();
        }
        else if (pdfType.equalsIgnoreCase("msme"))
        {
            pdfView.fromAsset("msme_certificate.pdf").load();
        }
        else
        {
            pdfView.fromAsset("accountpe_certificate.pdf").load();
        }


    }
}