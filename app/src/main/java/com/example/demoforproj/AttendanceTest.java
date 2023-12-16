package com.example.demoforproj;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Button;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

public class AttendanceTest extends AppCompatActivity {
    private static final int CREATE_FILE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_test);
        Button save = findViewById(R.id.save);
        save.setOnClickListener(v -> {
            if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED) {
                // Request the permission
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
//            File file = getExternalFilesDir(null);
//            File directory = new File(file,"hello.pdf");
//            try {
//                pdfDocument.writeTo(new FileOutputStream(directory));
//            }catch (IOException e){
//                e.printStackTrace();
//            }
//            pdfDocument.close();
//                Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
//                intent.addCategory(Intent.CATEGORY_OPENABLE);
//                intent.setType("application/pdf");
//                intent.putExtra(Intent.EXTRA_TITLE, "invoice.pdf");
                launcher.launch("invoice.pdf");
        });
    }
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {



            } else {
                // Permission denied, handle this case
            }
        }
    }
    public ActivityResultLauncher<String> launcher = registerForActivityResult(
            new ActivityResultContracts.CreateDocument( "application/pdf"),
            result -> {
                if (result!= null) {
                    Uri uri = result;
                    try {
                        OutputStream outputStream = getContentResolver().openOutputStream(uri);
                        if (outputStream != null) {
                            PdfDocument pdfDocument = new PdfDocument();
                            Paint paint = new Paint();
                            PdfDocument.PageInfo pageInfo =new PdfDocument.PageInfo.Builder(842,612,1).create();
                            PdfDocument.Page page =pdfDocument.startPage(pageInfo);
                            int tableX = 40;
                            int tableY = 100;
                            int cellWidth = 120;
                            int cellHeight = 50;
                            int rows = 5; // Number of rows
                            int cols = 3; // Number of columns
                            paint.setTextSize(20);
                            paint.setTextAlign(Paint.Align.CENTER);
                            Typeface customTypeface =Typeface.create("Arial",Typeface.BOLD);
                            paint.setTypeface(customTypeface);
                            Canvas canvas = page.getCanvas();
                            String rvitm = "RV INSTITUTE OF TECHNOLOGY AND MANAGEMENT";
                            Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.logotoput);
                            Bitmap scaleimage = Bitmap.createScaledBitmap(imageBitmap,73,73,true);
                            canvas.drawBitmap(scaleimage,105,30,paint);
                            canvas.drawText(rvitm,pageInfo.getPageWidth()/2,50,paint);
                            paint.setTextSize(15);
                            canvas.drawText("(Affliated to Visvesvaraya Technological University)",pageInfo.getPageWidth()/2,68,paint);
                            canvas.drawText("JP Nagar, Bengaluru - 560076",pageInfo.getPageWidth()/2,86,paint);

//                            paint.setStyle(Paint.Style.STROKE);
//                            canvas.drawRect(tableX, tableY, tableX + cellWidth * cols, tableY + cellHeight * rows, paint);
//                            for (int row = 1; row < rows; row++) {
//                                float y = tableY + cellHeight * row;
//                                canvas.drawLine(tableX, y, tableX + cellWidth * cols, y, paint);
//                            }
//
//// Draw vertical lines (column borders)
//                            for (int col = 1; col < cols; col++) {
//                                float x = tableX + cellWidth * col;
//                                canvas.drawLine(x, tableY, x, tableY + cellHeight * rows, paint);
//                            }
//
//// Reset the paint style for text (fill)
//                            paint.setStyle(Paint.Style.FILL);
//
//// Table rows
//                            for (int row = 0; row < rows; row++) {
//                                tableY += cellHeight;
//                                for (int col = 0; col < cols; col++) {
//                                    float x = tableX + cellWidth * col;
//                                    float y = tableY;
//
//                                    // Draw the cell border (rectangle)
//                                    paint.setStyle(Paint.Style.STROKE);
//                                    canvas.drawRect(x, y, x + cellWidth, y + cellHeight, paint);
//
//                                    // Reset the paint style for text (fill)
//                                    paint.setStyle(Paint.Style.FILL);
//
//                                    // Draw the text
//                                    paint.setColor(Color.BLACK); // Set the text color
//                                    canvas.drawText("Row " + (row + 1) + ", Col " + (col + 1), x + 10, y + cellHeight / 2, paint);
//                                    paint.setColor(Color.TRANSPARENT); // Set the text color to fully transparent for a transparent background
//                                }
//                            }
                            pdfDocument.finishPage(page);
                            pdfDocument.writeTo(outputStream);
                            pdfDocument.close();
                            outputStream.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
    );
}