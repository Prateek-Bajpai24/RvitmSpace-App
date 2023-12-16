package com.example.demoforproj;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.shockwave.pdfium.PdfDocument;
import com.shockwave.pdfium.PdfiumCore;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class AllClassroomActivity extends AppCompatActivity implements RecyclerAS.OnItemClick,SectionAdapter.OnItemClick{
    String subjcode,foruser,subjname,selectedDate,batch,stringr,reftosend;;
    RecyclerView recyclerView;
    RecyclerAS adapter;
    SectionAdapter sectionAdapter;
    Button cal,monthsheet,goleft,goright;
    TextView subj,atttext,mattext,cietext,asstext,load;
    ImageView attlogo,matlogo,cielogo,asslogo,bb;
    LinearLayout progressdialog,genbuts,pardispnot;
    ArrayList<String> datelist = new ArrayList<>();
    ArrayList<String> statuslist = new ArrayList<>();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    CardView menucard;
    Handler handler;
    Runnable hideButtonRunnable;
    ViewGroup rootrel;
    RelativeLayout attendlay,materiallay,cielay,asslay;
    int atitn=0,matint=0,cieint=0,assint=0,checki=0,posn;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    ArrayList<String> sectionname = new ArrayList<>();
    ArrayList<ArrayList<String>> typeelement=new ArrayList<>();
    ArrayList<ArrayList<Uri>> elementlink=new ArrayList<>();
    ArrayList<Uri> selectedfileuri = new ArrayList<>();
    SpinKitView spinKitView;
    ArrayList<ArrayList<Uri>> bitmaptosnd = new ArrayList<>();
    byte[] data;
    int recyclerpos;
    int uplsuc=0;
//    ArrayList<ArrayList<byte[]>> tempbit = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_classroom);
        Window window=getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.newpurp));
        spinKitView=findViewById(R.id.spin_kit);
        load=findViewById(R.id.load);
        pardispnot=findViewById(R.id.allclasspardispnot);
        matlogo=findViewById(R.id.matlogo);
        mattext=findViewById(R.id.mattext);
        cielogo=findViewById(R.id.cielogo);
        cietext=findViewById(R.id.cietext);
        asslogo=findViewById(R.id.asslogo);
        asstext=findViewById(R.id.asstext);
        cielay=findViewById(R.id.rl3);
        asslay=findViewById(R.id.rl4);
        attendlay=findViewById(R.id.rl1);
        materiallay=findViewById(R.id.rl2);
        genbuts=findViewById(R.id.genbuts);
        rootrel=findViewById(R.id.rv1);
        menucard=findViewById(R.id.atcard);
        goleft=findViewById(R.id.goleft);
        goright=findViewById(R.id.goright);
        monthsheet=findViewById(R.id.monthsheet);
        progressdialog=findViewById(R.id.linv);
        bb=findViewById(R.id.bb);
        recyclerView=findViewById(R.id.r1r);
        subj=findViewById(R.id.subjname);
        cal=findViewById(R.id.cal);
        attlogo=findViewById(R.id.attlogo);
        atttext=findViewById(R.id.atttext);
        attlogo.setColorFilter(R.color.confocolor);
        atttext.setTextColor(getResources().getColor(R.color.confocolor));
        Intent rcvint= getIntent();
        subjcode=rcvint.getStringExtra("subjcode");
        subjname=rcvint.getStringExtra("subjname");
        batch=rcvint.getStringExtra("batch");
        subj.setText(subjname+" - Attendance");
        sectionAdapter=new SectionAdapter(sectionname,this,typeelement,elementlink,bitmaptosnd);
        adapter=new RecyclerAS(datelist,"AllClass_"+subjcode,statuslist,this);
        recyclerView.setLayoutManager(new LinearLayoutManager(AllClassroomActivity.this));
        atitn=1;
        database.getReference().child("teachers").child(auth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                database.getReference().child("teachers").child(auth.getCurrentUser().getUid()).child("check").setValue(snapshot.child("Username").getValue(String.class)).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            foruser=snapshot.child("check").getValue(String.class);
                            attendancemethod();
                        }
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        cal.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    AllClassroomActivity.this,
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
                            // Handle the selected date
                            // You can display or use the selected date as needed
                            selectedDate = selectedYear + "-" + (selectedMonth + 1) + "-" + selectedDay;
                            // You can replace this with your desired action
                            Intent intent = new Intent(AllClassroomActivity.this,AttendanceSheet.class);
                            intent.putExtra("date",selectedDate);
                            intent.putExtra("subjcode",subjcode);
                            intent.putExtra("mode","write");
                            startActivity(intent);
                        }
                    },
                    year, month, day
            );
            datePickerDialog.show();
        });
        bb.setOnClickListener(v1 -> {
            finish();
        });
        monthsheet.setOnClickListener(vm -> {
            Intent intent = new Intent(AllClassroomActivity.this,AttendanceMonth.class);
            intent.putExtra("subjname",subjname);
            intent.putExtra("subjcode",subjcode);
            intent.putExtra("batch",batch);
            startActivity(intent);
        });
        rootrel.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                timerforbutton();
                return false;
            }
        });
        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                timerforbutton();
                return false;
            }
        });
        goright.setOnClickListener(vgr->{
            timerforbutton();
        });
        goleft.setOnClickListener(vgl->{
            timerforbutton();
        });
        attendlay.setOnClickListener(vatt->{
            timerforbutton();
            checkfocus();
            attlogo.setColorFilter(R.color.confocolor);
            atttext.setTextColor(ContextCompat.getColor(this,R.color.confocolor));
            atitn=1;
            attendancemethod();
        });
        materiallay.setOnClickListener(vmat->{
            cal.setVisibility(View.GONE);
            monthsheet.setVisibility(View.GONE);
            progressdialog.setVisibility(View.VISIBLE);
            goleft.setVisibility(View.GONE);
            goright.setVisibility(View.GONE);
            timerforbutton();
            checkfocus();
            matlogo.setColorFilter(R.color.confocolor);
            mattext.setTextColor(ContextCompat.getColor(this,R.color.confocolor));
            matint=1;
            sectionname.clear();
            typeelement.clear();
            elementlink.clear();
            bitmaptosnd.clear();
//            tempbit.clear();
//            typeuser="teacher";
            recyclerView.setAdapter(sectionAdapter);
//            performfetch();
            try {
                database.getReference().child("Classroom").child(foruser).child(subjcode).child("Materials").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        sectionname.clear();
                        typeelement.clear();
                        elementlink.clear();
                        bitmaptosnd.clear();
                        for(DataSnapshot sectionshot:snapshot.getChildren()){
                            ArrayList<String> typeelel = new ArrayList<>();
                            ArrayList<Uri> elemel = new ArrayList<>();
                            ArrayList<Uri> bitt = new ArrayList<>();
//                            ArrayList<byte[]> tempb = new ArrayList<>();
                            checki=0;
                            for(DataSnapshot childsectionshot:sectionshot.getChildren()){
                                Log.d("ch",childsectionshot.getRef().getParent().toString().split("/")[7]);
                                if(!sectionname.contains(childsectionshot.getRef().getParent().toString().split("/")[7]))
                                    sectionname.add(childsectionshot.getRef().getParent().toString().split("/")[7]);
                                typeelel.add(childsectionshot.getKey());
                                for(DataSnapshot media: childsectionshot.getChildren()){
                                    String checkel="none";
                                    String bit="none";
                                    if(media.getKey().equals("link"))
                                        checkel=media.getValue(String.class);
                                    else
                                        bit=media.getValue(String.class);
                                    if(!bit.equals("none"))
                                        bitt.add(Uri.parse(bit));
                                    if(!checkel.equals("none"))
                                        elemel.add(Uri.parse(checkel));

                                }
//                            String checkel=childsectionshot.getValue(String.class);
                                if(typeelel.get(checki).equals("1")){
                                    typeelel.remove(checki);
                                    checki-=1;
                                }
                                checki+=1;
                            }
                            bitmaptosnd.add(bitt);
                            typeelement.add(typeelel);
                            elementlink.add(elemel);
                            checki+=1;
                            if(uplsuc==1){
                                uplsuc=0;
                                ArrayList<String> temptype = new ArrayList<>();
                                temptype = typeelement.get(posn);
                                temptype.add("uploaded");
                                temptype.add(foruser);
                                temptype.add(stringr);
                                temptype.add(reftosend);
                                temptype.add(subjcode);
                                typeelement.set(posn,temptype);
                            }
                        }
                        Log.d("typel",typeelement.toString());
                        Log.d("ghh",elementlink.toString());
                        Log.d("sss",bitmaptosnd.toString());
//                        Log.d("ttt",tempbit.toString());
                        progressdialog.setVisibility(View.GONE);
                        Log.d("bs",bitmaptosnd.toString());
                        sectionAdapter.notifyDataSetChanged();
//                    typeelement=new ArrayList<>();
//                    elementlink=new ArrayList<>();
//                    bitmaptosnd=new ArrayList<>();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }catch (Exception e){
                sectionAdapter.notifyDataSetChanged();
            }

        });
        cielay.setOnClickListener(vcie->{
            timerforbutton();
            checkfocus();
            cielogo.setColorFilter(R.color.confocolor);
            cietext.setTextColor(ContextCompat.getColor(this,R.color.confocolor));
            cieint=1;
        });
        asslay.setOnClickListener(vcie->{
            timerforbutton();
            checkfocus();
            asslogo.setColorFilter(R.color.confocolor);
            asstext.setTextColor(ContextCompat.getColor(this,R.color.confocolor));
            assint=1;
        });

    }

    @Override
    public void oniteMclick(int position,String daterec) {
        if(daterec.equals("buttonactive")){
            timerforbutton();
        }else{
            database.getReference().child("Classroom").child(foruser).child(subjcode).child("Attendance").child(daterec).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(AllClassroomActivity.this, "Attendance removed successfully", Toast.LENGTH_SHORT).show();
                }
            });
        }

    }
    public void timerforbutton(){
        goleft.setVisibility(View.VISIBLE);
        goright.setVisibility(View.VISIBLE);
        handler.removeCallbacks(hideButtonRunnable);
        handler.postDelayed(hideButtonRunnable, 1500);
        handler.removeCallbacks(hideButtonRunnable);
        handler.postDelayed(hideButtonRunnable, 1500);
    }
    public void attendancemethod(){
        recyclerView.setAdapter(adapter);
        database.getReference().child("Classroom").child(foruser).child(subjcode).child("Attendance").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                datelist.clear();
                List<Dateobjinatt> customlist = new ArrayList<>();
                if(snapshot.exists()){
                    for(DataSnapshot csnap:snapshot.getChildren()){
                        String[] pathref = csnap.getRef().toString().split("/");
                        String[] datecomp = pathref[7].split("-");
                        customlist.add(new Dateobjinatt(Integer.valueOf(datecomp[0]),Integer.valueOf(datecomp[1]),Integer.valueOf(datecomp[2]),pathref[7]));
                    }
                }
                CompDates compDates=new CompDates();
                customlist.sort(compDates);
                for(Dateobjinatt dateobjinatt:customlist)
                    datelist.add(dateobjinatt.datetosend);
                progressdialog.setVisibility(View.GONE);
                rootrel.setVisibility(View.VISIBLE);
                genbuts.setVisibility(View.VISIBLE);
                monthsheet.setVisibility(View.VISIBLE);
                cal.setVisibility(View.VISIBLE);
                menucard.setVisibility(View.VISIBLE);
                goleft.setVisibility(View.VISIBLE);
                goright.setVisibility(View.VISIBLE);
                handler = new Handler();
                hideButtonRunnable = new Runnable() {
                    @Override
                    public void run() {
                        Animation fadeOut = new AlphaAnimation(1, 0);
                        fadeOut.setDuration(500); // You can adjust the duration as needed
                        goleft.startAnimation(fadeOut);
                        goleft.setVisibility(View.INVISIBLE);
                        goright.startAnimation(fadeOut);
                        goright.setVisibility(View.INVISIBLE);
                    }
                };
                timerforbutton();
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void checkfocus(){
        if(atitn==1){
            attlogo.setColorFilter(android.R.color.white);
            atttext.setTextColor(ContextCompat.getColor(this,android.R.color.white));
            atitn=0;
        }
        else if(matint==1){
            matlogo.setColorFilter(android.R.color.white);
            mattext.setTextColor(ContextCompat.getColor(this, android.R.color.white));
            matint=0;
        }
        else if(cieint==1){
            cielogo.setColorFilter(android.R.color.white);
            cietext.setTextColor(ContextCompat.getColor(this, android.R.color.white));
            cieint=0;
        }
        else if(assint==1){
            asslogo.setColorFilter(android.R.color.white);
            asstext.setTextColor(ContextCompat.getColor(this, android.R.color.white));
            assint=0;
        }
    }

    @Override
    public void oniteclicksection(int position, String datesend,String sectionrec) {
        recyclerpos=position;
        if(datesend.equals("addsection")){
            posn=position;
            stringr=sectionrec;
            selectedfileuri.clear();
            Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            intent.putExtra(Intent.EXTRA_MIME_TYPES,new String[]{"application/pdf"});
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            launcher.launch(Intent.createChooser(intent, "Select File"));
        }
    }

    public void performfetch(){
        //
    }

    ActivityResultLauncher<Intent> launcher =  registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if(result.getResultCode()==RESULT_OK && result.getData()!=null){
                        Uri fileuri = result.getData().getData();
                        selectedfileuri.add(fileuri);
                        int pageNumber = 0;
                        PdfiumCore pdfiumCore = new PdfiumCore(this);
                    try {
                        //http://www.programcreek.com/java-api-examples/index.php?api=android.os.ParcelFileDescriptor
                        ParcelFileDescriptor fd = getContentResolver().openFileDescriptor(fileuri, "r");
                        PdfDocument pdfDocument = pdfiumCore.newDocument(fd);
                        pdfiumCore.openPage(pdfDocument, pageNumber);
                        int width = pdfiumCore.getPageWidthPoint(pdfDocument, pageNumber);
                        int height = pdfiumCore.getPageHeightPoint(pdfDocument, pageNumber);
                        Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                        pdfiumCore.renderPageBitmap(pdfDocument, bmp, pageNumber, 0, 0, width, height);
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        data=new byte[0];
                        data = baos.toByteArray();
                        pdfiumCore.closeDocument(pdfDocument); // important!
                    } catch(Exception e) {
                        //todo with exception
                    }
                        String filen = getFileName(fileuri);
                        Dialog dialog = new Dialog(AllClassroomActivity.this);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setContentView(R.layout.confirmdel);
                        LinearLayout selview = dialog.findViewById(R.id.selview);
                        TextView conftext = dialog.findViewById(R.id.conformtext);
                        EditText filename = dialog.findViewById(R.id.filename);
                        selview.setVisibility(View.VISIBLE);
                        filename.setVisibility(View.VISIBLE);
                        conftext.setText("Confirm file addition?");
                        LinearLayout pardialog = dialog.findViewById(R.id.selected);
                        LayoutInflater layoutInflater = LayoutInflater.from(this);
                        View customdi = layoutInflater.inflate(R.layout.custom_for_pdf,pardialog,false);
                        ImageView delpdf = customdi.findViewById(R.id.delete1);
                        delpdf.setVisibility(View.GONE);
                        TextView title = customdi.findViewById(R.id.title);
                        title.setText(filen);
                        pardialog.addView(customdi);
                        Button yes = dialog.findViewById(R.id.yes);
                        Button no = dialog.findViewById(R.id.no);
                        yes.setOnClickListener(vymat->{
                            if(filename.getText().toString().isEmpty())
                                Toast.makeText(this, "Enter filename to add the material", Toast.LENGTH_SHORT).show();
                            else if(filename.getText().toString().contains(".")||filename.getText().toString().contains("$")||filename.getText().toString().contains("#")||filename.getText().toString().contains("[")||filename.getText().toString().contains("]"))
                                Toast.makeText(this, "File name should not contain '#','$','.','[' or ']'", Toast.LENGTH_SHORT).show();
                            else{
                                progressdialog.setVisibility(View.VISIBLE);
                                load.setText("Uploading");
                                spinKitView.setColor(R.color.sun);
//                                final CountDownLatch latch = new CountDownLatch(2); // Number of tasks
//                                final Uri[] u1 = new Uri[1];
//                                final Uri[] u2 = new Uri[1];
                                StorageReference storageReference= FirebaseStorage.getInstance().getReference().child("Textboooks").child(filename.getText().toString()).child("link");
                                UploadTask uploadTask = storageReference.putFile(fileuri);
                                uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> taska) {
                                        storageReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Uri> taskb) {
                                                database.getReference().child("Classroom").child(foruser).child(subjcode).child("Materials").child(stringr).child(filename.getText().toString()).child("link").setValue(taskb.getResult().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> taske) {
//                                                        ArrayList<String> check = typeelement.get(posn);
//                                                        ArrayList<Uri> checkel = elementlink.get(posn);
////                                                        ArrayList<byte[]> bitch = tempbit.get(posn);
//                                                        check.add(filename.getText().toString());
//                                                        checkel.add(taskb.getResult());
//                                                        check.add("uploaded");
////                                                        bitch.add(data);
//                                                        typeelement.set(posn, check);
//                                                        elementlink.set(posn, checkel);
////                                                        tempbit.set(posn,bitch);
//                                                        Log.d("typref", typeelement.toString());
                                                    }
                                                });
                                                    }
                                                });
                                            }
                                        });
                                UploadTask uploadTask1;
                                StorageReference storageReference1=FirebaseStorage.getInstance().getReference().child("Textboooks").child(filename.getText().toString()).child("image");
                                uploadTask1=storageReference1.putBytes(data);
                                uploadTask1.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                        storageReference1.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Uri> taskb) {
                                                Log.d("mess",taskb.getResult().toString());
                                                database.getReference().child("Classroom").child(foruser).child(subjcode).child("Materials").child(stringr).child(filename.getText().toString()).child("image").setValue(taskb.getResult().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        reftosend=filename.getText().toString();
                                                        uplsuc=1;
                                                        dialog.dismiss();
                                                    }
                                                });
//                                                    @Override
//                                                    public void onComplete(@NonNull Task<Void> taskf) {
//                                                        ArrayList<Uri> bitcheck = bitmaptosnd.get(posn);
//                                                        bitcheck.add(taskb.getResult());
//                                                        bitmaptosnd.set(posn, bitcheck);
//                                                        Log.d("dsds", bitmaptosnd.toString());
//                                                        progressdialog.setVisibility(View.GONE);
//                                                        load.setText("Loading");
//                                                        spinKitView.setColor(R.color.newpurp);
//                                                        sectionAdapter.notifyDataSetChanged();
                                                dialog.dismiss();
//                                                    }
//                                                });
                                            }
                                        });
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d("uplerr",e.getMessage());
                                    }
                                });}
                        });
                        no.setOnClickListener(vnadd->{
                            dialog.dismiss();
                        });
                        dialog.show();
                        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        dialog.getWindow().setGravity(Gravity.CENTER);
                }
            }
    );
    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                result = cursor.getString(columnIndex);
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }
}
class Dateobjinatt{
    int d1;
    int d2;
    int d3;
    String datetosend;
    public Dateobjinatt(int d1, int d2, int d3, String datetosend){
        this.d1=d1;
        this.d2=d2;
        this.d3=d3;
        this.datetosend=datetosend;
    }
}
class CompDates implements Comparator<Dateobjinatt>{

    @Override
    public int compare(Dateobjinatt o1, Dateobjinatt o2) {
        if(o1.d1> o2.d1)
            return -1;
        else if(o1.d1==o2.d1 && o1.d2>o2.d2)
            return -1;
        else if (o1.d1==o2.d1 && o1.d2==o2.d2 && o1.d3>o2.d3)
            return -1;
        else
            return 1;
    }
}