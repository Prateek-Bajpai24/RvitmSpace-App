package com.example.demoforproj;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.checkerframework.checker.units.qual.A;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class AttendanceMonth extends AppCompatActivity implements EditAdapter.OnItemClick {
    String foruser,subjcode,subjname,batch;
    int rowcount=250;
    ArrayList<String> monthlist = new ArrayList<>();
    ArrayList<String> monthnumlist = new ArrayList<>();
    ArrayList<String> usnlist = new ArrayList<>();
    ArrayList<String> studlisttoput = new ArrayList<>();
    RecyclerView recyclerView;
    EditAdapter editAdapter;
    LinearLayout progressdialog;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    ArrayList<String> ref;
    ArrayList<ArrayList<String>> twodarray;
    ImageView bb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_month);
        bb=findViewById(R.id.bb);
        progressdialog=findViewById(R.id.linv);
        recyclerView=findViewById(R.id.monthdata);
        editAdapter=new EditAdapter(this,monthlist,monthnumlist,"attmonth");
        recyclerView.setLayoutManager(new LinearLayoutManager(AttendanceMonth.this));
        recyclerView.setAdapter(editAdapter);
        Intent intent = getIntent();
        subjcode=intent.getStringExtra("subjcode");
        subjname=intent.getStringExtra("subjname");
        batch=intent.getStringExtra("batch");
        database.getReference().child("teachers").child(auth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                database.getReference().child("teachers").child(auth.getCurrentUser().getUid()).child("check")
                        .setValue(snapshot.child("Username").getValue(String.class)).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    foruser = snapshot.child("check").getValue(String.class);
                                    database.getReference().child("Classroom").child(foruser).child(subjcode).child("Attendance").addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            monthlist.clear();
                                            List<Dateobjinatt> customlist = new ArrayList<>();
                                            if(snapshot.exists()){
                                                for(DataSnapshot csnap:snapshot.getChildren()){
                                                    String[] pathref = csnap.getRef().toString().split("/");
                                                    String[] datecomp = pathref[7].split("-");
                                                                String monname = monthname(Integer.valueOf(datecomp[1]),datecomp[0]);
                                                                customlist.add(new Dateobjinatt(Integer.valueOf(datecomp[0]),Integer.valueOf(datecomp[1]),Integer.valueOf(datecomp[2]),monname));
                                                            }
                                                        }
                                                        CompDates compDates=new CompDates();
                                                        customlist.sort(compDates);
                                                        for(Dateobjinatt dateobjinatt:customlist){
                                                            if(!monthlist.contains(dateobjinatt.datetosend))
                                                                monthlist.add(dateobjinatt.datetosend);
                                                            if(!monthnumlist.contains(String.valueOf(dateobjinatt.d2)))
                                                                monthnumlist.add(String.valueOf(dateobjinatt.d2));
                                                        }
                                                        Log.d("monthlist",monthnumlist.toString());
                                                        progressdialog.setVisibility(View.GONE);
                                                        editAdapter.notifyDataSetChanged();
                                                    // Notify the adapter that the data has changed
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });
                                    database.getReference().child("Classroom").child(foruser).child(subjcode)
                                            .child("Course_Students").addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot ssnapshot) {
                                                    usnlist.clear();
                                                    studlisttoput.clear();// Clear the list to avoid duplicates
                                                    for (DataSnapshot ch : ssnapshot.getChildren()) {
                                                        usnlist.add(ch.getKey());
                                                        String[] name = ch.getValue(String.class).split(" ");
                                                        studlisttoput.add(name[0]);
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });

                                }
                            }
                        });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        bb.setOnClickListener(vb->{
            finish();
        });

    }
    public String monthname(int month,String year){
        String finalreturn = "None";
        switch (month){
            case 1: finalreturn="Jan-"+ year;break;
            case 2: finalreturn="Feb-"+ year;break;
            case 3: finalreturn="Mar-"+ year;break;
            case 4: finalreturn="Apr-"+ year;break;
            case 5: finalreturn="May-"+ year;break;
            case 6: finalreturn="Jun-"+ year;break;
            case 7: finalreturn="Jul-"+ year;break;
            case 8: finalreturn="Aug-"+ year;break;
            case 9: finalreturn="Sept-"+ year;break;
            case 10: finalreturn="Oct-"+ year;break;
            case 11: finalreturn="Nov-"+ year;break;
            case 12: finalreturn="Dec-"+ year;break;
        }
        return finalreturn;
    }

    @Override
    public void onitemclick(int position) {
        ref = new ArrayList<>();
        ref=editAdapter.getDatefromrecycler();
        twodarray=new ArrayList<>();
        String[] dateref = ref.get(0).split("-");
        for (int i = 0; i < 31; i++) {
            // Initialize a row with empty elements
            ArrayList<String> row = new ArrayList<>();
            for (int j = 0; j < usnlist.size(); j++) {
                row.add(" ");
            }
            twodarray.add(row);
        }
        database.getReference().child("teachers").child(auth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                database.getReference().child("teachers").child(auth.getCurrentUser().getUid()).child("check").setValue(snapshot.child("Username").getValue(String.class)).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        foruser=snapshot.child("check").getValue(String.class);
                        database.getReference().child("Classroom").child(foruser).child(subjcode).child("Attendance").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for(int i=0;i<31;i++){
                                        if(snapshot.child(dateref[1]+"-"+ref.get(1)+"-"+(i+1)).exists()){
                                            int j=0;
                                            for(DataSnapshot csnapfordate:snapshot.child(dateref[1]+"-"+ref.get(1)+"-"+(i+1)).getChildren()){
                                                twodarray.get(i).set(j,csnapfordate.getValue(String.class));
                                                j+=1;
                                            }
                                        }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        launcher.launch(ref.get(0)+"-Attendance.pdf");

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
                            int tableY = 180;
                            int tableyT=187;
                            int cellWidth = 120;
                            int cellHeight = 25;
                            paint.setTextSize(20);
                            paint.setTextAlign(Paint.Align.CENTER);
                            Typeface customTypeface =Typeface.create("Arial",Typeface.BOLD);
                            paint.setTypeface(customTypeface);
                            Canvas canvas = page.getCanvas();
                            Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.logotoput);
                            Bitmap scaleimage = Bitmap.createScaledBitmap(imageBitmap,53,53,true);
                            canvas.drawBitmap(scaleimage,105,50,paint);
                            canvas.drawText("RV INSTITUTE OF TECHNOLOGY AND MANAGEMENT",pageInfo.getPageWidth()/2,50,paint);
                            paint.setTextSize(15);
                            canvas.drawText("(Affliated to Visvesvaraya Technological University)",pageInfo.getPageWidth()/2,68,paint);
                            canvas.drawText("JP Nagar, Bengaluru - 560076",pageInfo.getPageWidth()/2,86,paint);
                            paint.setTextSize(17);
                            Typeface typeface = Typeface.create("Arial",Typeface.ITALIC);
                            paint.setTypeface(typeface);
                            canvas.drawText("Attendance Sheet - "+ref.get(0),pageInfo.getPageWidth()/2,115,paint);
                            paint.setTypeface(null);
                            paint.setTextSize(15);
                            paint.setTextAlign(Paint.Align.LEFT);
                            canvas.drawText("Course Code - " + subjcode,40, 143, paint);
                            paint.setTextAlign(Paint.Align.CENTER);
                            canvas.drawText("Course - " + subjname, pageInfo.getPageWidth()/2, 143, paint);
                            canvas.drawText("Batch - " + batch, 759, 143, paint);
                            paint.setStyle(Paint.Style.STROKE);
                            canvas.drawRect(tableX, tableY, 809, 555, paint);
                            for (int row = 1; row < 15; row++) {
                                float y = tableY + cellHeight * row;
                                canvas.drawLine(tableX, y, 809, y, paint);
                            }

// Draw vertical lines (column borders)
                            canvas.drawLine(133, tableY, 133, tableY + cellHeight * 15, paint);
                            canvas.drawLine(250, tableY, 250, tableY + cellHeight * 15, paint);
                            paint.setTextSize(11);
                            rowcount=250;
                            for (int col=0;col<30;col++) {
                                paint.setStyle(Paint.Style.FILL);
                                canvas.drawText(String.valueOf(col + 1), rowcount + 9, 195, paint);
                                rowcount += 18;
                                paint.setStyle(Paint.Style.STROKE);
                                canvas.drawLine(rowcount, tableY, rowcount, tableY + cellHeight * 15, paint);
                            }
                            paint.setStyle(Paint.Style.FILL);
                            canvas.drawText("31",rowcount+9,195,paint);
                            int startatY=210;
                            int startatX;
                            for(int row=0;row<usnlist.size();row++){
                                if(row>13)
                                    break;
                                startatX=250;
                                for(int col=0;col<31;col++){
                                    canvas.drawText(twodarray.get(col).get(row),startatX+9,startatY+10,paint);
                                    startatX+=18;
                                }
                                startatY+=25;
                            }

// Reset the paint style for text (fill)
                            paint.setStyle(Paint.Style.FILL);

// Table rows
                            for (int row = 0; row < usnlist.size(); row++) {
                                if(row>13)
                                    break;
                                tableyT += cellHeight;
                                for (int col = 0; col < 1; col++) {
                                    float x = tableX + cellWidth * col;
                                    float y = tableyT;

                                    // Draw the text
                                    paint.setColor(Color.BLACK);
                                    paint.setTextSize(15);// Set the text color
                                    canvas.drawText(usnlist.get(row), x + 45, y + cellHeight/2, paint);
                                    paint.setColor(Color.TRANSPARENT); // Set the text color to fully transparent for a transparent background
                                }
                            }
                            tableyT=187;
                            paint.setTextAlign(Paint.Align.LEFT);
                            for (int row = 0; row < usnlist.size(); row++) {
                                if(row>13)
                                    break;
                                tableyT += cellHeight;
                                for (int col = 0; col < 1; col++) {
                                    float x = tableX + cellWidth * col;
                                    float y = tableyT;

                                    // Draw the text
                                    paint.setColor(Color.BLACK);
                                    paint.setTextSize(15);// Set the text color
                                    canvas.drawText(studlisttoput.get(row), x + 100, y + cellHeight/2, paint);
                                    paint.setColor(Color.TRANSPARENT); // Set the text color to fully transparent for a transparent background
                                }
                            }
                            pdfDocument.finishPage(page);
                            if(usnlist.size()>13){
                                PdfDocument.PageInfo pageInfo2 = new PdfDocument.PageInfo.Builder(842, 612, 2).create();
                                PdfDocument.Page page2 = pdfDocument.startPage(pageInfo2);
                                Canvas canvas2 = page2.getCanvas();
                                Paint paint2 = new Paint();
                                paint2.setStyle(Paint.Style.STROKE);
                                canvas2.drawRect(tableX, 40, 809, 565, paint2);
                                canvas2.drawLine(133, 40, 133, 40 + cellHeight * 21, paint2);
                                canvas2.drawLine(250, 40, 250, 40 + cellHeight * 21, paint2);
                                paint2.setTextSize(11);
                                for (int row = 1; row < 22; row++) {
                                    float y = 40 + cellHeight * row;
                                    canvas2.drawLine(tableX, y, 809, y, paint2);
                                }
                                rowcount=250;
                                for (int col=0;col<30;col++) {
                                    paint2.setStyle(Paint.Style.FILL);
                                    canvas2.drawText(String.valueOf(col + 1), rowcount + 3, 55, paint2);
                                    rowcount += 18;
                                    paint2.setStyle(Paint.Style.STROKE);
                                    canvas2.drawLine(rowcount, 40, rowcount, 40 + cellHeight * 21, paint2);
                                }
                                paint2.setStyle(Paint.Style.FILL);
                                canvas2.drawText("31",rowcount+3,55,paint2);
                                startatY=70;
                                for(int row=14;row<usnlist.size();row++){
                                    if(row>33)
                                        break;
                                    startatX=250;
                                    for(int col=0;col<31;col++){
                                        canvas2.drawText(twodarray.get(col).get(row),startatX+5,startatY+10,paint2);
                                        startatX+=18;
                                    }
                                    startatY+=25;
                                }
                                tableyT=47;
                                paint2.setTextAlign(Paint.Align.CENTER);
                                for (int row = 14; row < usnlist.size(); row++) {
                                    if(row>33)
                                        break;
                                    tableyT += cellHeight;
                                    for (int col = 0; col < 1; col++) {
                                        float x = tableX + cellWidth * col;
                                        float y = tableyT;

                                        // Draw the text
                                        paint2.setColor(Color.BLACK);
                                        paint2.setTextSize(15);// Set the text color
                                        canvas2.drawText(usnlist.get(row), x + 45, y + cellHeight/2, paint2);
                                        paint2.setColor(Color.TRANSPARENT); // Set the text color to fully transparent for a transparent background
                                    }
                                }
                                tableyT=47;
                                paint2.setTextAlign(Paint.Align.LEFT);
                                for (int row = 14; row < usnlist.size(); row++) {
                                    if(row>33)
                                        break;
                                    tableyT += cellHeight;
                                    for (int col = 0; col < 1; col++) {
                                        float x = tableX + cellWidth * col;
                                        float y = tableyT;

                                        // Draw the text
                                        paint2.setColor(Color.BLACK);
                                        paint2.setTextSize(15);// Set the text color
                                        canvas2.drawText(studlisttoput.get(row), x + 100, y + cellHeight/2, paint2);
                                        paint2.setColor(Color.TRANSPARENT); // Set the text color to fully transparent for a transparent background
                                    }
                                }
                                pdfDocument.finishPage(page2);
                            }
                            if(usnlist.size()>33){
                                PdfDocument.PageInfo pageInfo3 = new PdfDocument.PageInfo.Builder(842, 612, 3).create();
                                PdfDocument.Page page3 = pdfDocument.startPage(pageInfo3);
                                Canvas canvas3 = page3.getCanvas();
                                Paint paint3 = new Paint();
                                paint3.setStyle(Paint.Style.STROKE);
                                canvas3.drawRect(tableX, 40, 809, 565, paint3);
                                canvas3.drawLine(133, 40, 133, 40 + cellHeight * 21, paint3);
                                canvas3.drawLine(250, 40, 250, 40 + cellHeight * 21, paint3);
                                paint3.setTextSize(11);
                                for (int row = 1; row < 22; row++) {
                                    float y = 40 + cellHeight * row;
                                    canvas3.drawLine(tableX, y, 809, y, paint3);
                                }
                                rowcount=250;
                                for (int col=0;col<30;col++) {
                                    paint3.setStyle(Paint.Style.FILL);
                                    canvas3.drawText(String.valueOf(col + 1), rowcount + 3, 55, paint3);
                                    rowcount += 18;
                                    paint3.setStyle(Paint.Style.STROKE);
                                    canvas3.drawLine(rowcount, 40, rowcount, 40 + cellHeight * 21, paint3);
                                }
                                paint3.setStyle(Paint.Style.FILL);
                                canvas3.drawText("31",rowcount+3,55,paint3);
                                startatY=70;
                                for(int row=34;row<usnlist.size();row++){
                                    if(row>53)
                                        break;
                                    startatX=250;
                                    for(int col=0;col<31;col++){
                                        canvas3.drawText(twodarray.get(col).get(row),startatX+5,startatY+10,paint3);
                                        startatX+=18;
                                    }
                                    startatY+=25;
                                }
                                tableyT=47;
                                paint3.setTextAlign(Paint.Align.CENTER);
                                for (int row = 34; row < usnlist.size(); row++) {
                                    if(row>53)
                                        break;
                                    tableyT += cellHeight;
                                    for (int col = 0; col < 1; col++) {
                                        float x = tableX + cellWidth * col;
                                        float y = tableyT;

                                        // Draw the text
                                        paint3.setColor(Color.BLACK);
                                        paint3.setTextSize(15);// Set the text color
                                        canvas3.drawText(usnlist.get(row), x + 45, y + cellHeight/2, paint3);
                                        paint3.setColor(Color.TRANSPARENT); // Set the text color to fully transparent for a transparent background
                                    }
                                }
                                tableyT=47;
                                paint3.setTextAlign(Paint.Align.LEFT);
                                for (int row = 34; row < usnlist.size(); row++) {
                                    if(row>53)
                                        break;
                                    tableyT += cellHeight;
                                    for (int col = 0; col < 1; col++) {
                                        float x = tableX + cellWidth * col;
                                        float y = tableyT;
                                        // Draw the text
                                        paint3.setColor(Color.BLACK);
                                        paint3.setTextSize(15);// Set the text color
                                        canvas3.drawText(studlisttoput.get(row), x + 100, y + cellHeight/2, paint3);
                                        paint3.setColor(Color.TRANSPARENT); // Set the text color to fully transparent for a transparent background
                                    }
                                }
                                pdfDocument.finishPage(page3);
                            }
                            if(usnlist.size()>53){
                                PdfDocument.PageInfo pageInfo4 = new PdfDocument.PageInfo.Builder(842, 612, 4).create();
                                PdfDocument.Page page4 = pdfDocument.startPage(pageInfo4);
                                Canvas canvas4 = page4.getCanvas();
                                Paint paint4 = new Paint();
                                paint4.setStyle(Paint.Style.STROKE);
                                canvas4.drawRect(tableX, 40, 809, 565, paint4);
                                canvas4.drawLine(133, 40, 133, 40 + cellHeight * 21, paint4);
                                canvas4.drawLine(250, 40, 250, 40 + cellHeight * 21, paint4);
                                paint4.setTextSize(11);
                                for (int row = 1; row < 22; row++) {
                                    float y = 40 + cellHeight * row;
                                    canvas4.drawLine(tableX, y, 809, y, paint4);
                                }
                                rowcount=250;
                                for (int col=0;col<30;col++) {
                                    paint4.setStyle(Paint.Style.FILL);
                                    canvas4.drawText(String.valueOf(col + 1), rowcount + 3, 55, paint4);
                                    rowcount += 18;
                                    paint4.setStyle(Paint.Style.STROKE);
                                    canvas4.drawLine(rowcount, 40, rowcount, 40 + cellHeight * 21, paint4);
                                }
                                paint4.setStyle(Paint.Style.FILL);
                                canvas4.drawText("31",rowcount+3,55,paint4);
                                startatY=70;
                                for(int row=54;row<usnlist.size();row++){
                                    if(row>73)
                                        break;
                                    startatX=250;
                                    for(int col=0;col<31;col++){
                                        canvas4.drawText(twodarray.get(col).get(row),startatX+5,startatY+10,paint4);
                                        startatX+=18;
                                    }
                                    startatY+=25;
                                }
                                tableyT=47;
                                paint4.setTextAlign(Paint.Align.CENTER);
                                for (int row = 54; row < usnlist.size(); row++) {
                                    if(row>73)
                                        break;
                                    tableyT += cellHeight;
                                    for (int col = 0; col < 1; col++) {
                                        float x = tableX + cellWidth * col;
                                        float y = tableyT;

                                        // Draw the text
                                        paint4.setColor(Color.BLACK);
                                        paint4.setTextSize(15);// Set the text color
                                        canvas4.drawText(usnlist.get(row), x + 45, y + cellHeight/2, paint4);
                                        paint4.setColor(Color.TRANSPARENT); // Set the text color to fully transparent for a transparent background
                                    }
                                }
                                tableyT=47;
                                paint4.setTextAlign(Paint.Align.LEFT);
                                for (int row = 54; row < usnlist.size(); row++) {
                                    if(row>73)
                                        break;
                                    tableyT += cellHeight;
                                    for (int col = 0; col < 1; col++) {
                                        float x = tableX + cellWidth * col;
                                        float y = tableyT;

                                        // Draw the text
                                        paint4.setColor(Color.BLACK);
                                        paint4.setTextSize(15);// Set the text color
                                        canvas4.drawText(studlisttoput.get(row), x + 100, y + cellHeight/2, paint4);
                                        paint4.setColor(Color.TRANSPARENT); // Set the text color to fully transparent for a transparent background
                                    }
                                }
                                pdfDocument.finishPage(page4);
                            }
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}