package com.example.sammy.mymp3player_thread_test;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Environment;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    ListView listviewMP3;
    Button btnPlay, btnStop, btnLike, btnLikeList;
    TextView tvMP3, tvTime;
    SeekBar pbMP3;

    MyDBHelper myDBHelper;
    SQLiteDatabase sqLiteDatabase;

    MediaPlayer mediaPlayer;
    ArrayList<String> list = new ArrayList<String>();
    String selectedMP3;
    static final String MP3_PATH = Environment.getExternalStorageDirectory().getPath() + "/";

    //seekBar 내가 움직일 때마다 그 부분에서 음악 시작하기위한 변수 선언
    static int mSeek;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("MP3 Player");

        listviewMP3 = findViewById(R.id.listviewMP3);
        btnPlay = findViewById(R.id.btnPlay);
        btnStop = findViewById(R.id.btnStop);
        tvMP3 = findViewById(R.id.tvMP3);
        tvTime = findViewById(R.id.tvTime);
        pbMP3 = findViewById(R.id.pbMP3);
        btnLike = findViewById(R.id.btnLike);
        btnLikeList = findViewById(R.id.btnLikeList);

        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, MODE_PRIVATE);

        File[] files = new File(MP3_PATH).listFiles();

        //확장자명 걸러내기
        for (File file : files) {
            String fileName = file.getName();
            if (fileName.length() >= 5) {
                String extendName = fileName.substring(fileName.length() - 3);
                if (extendName.equals("mp3")) {
                    list.add(fileName);
                }
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice, list);

        listviewMP3.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listviewMP3.setAdapter(adapter);
        listviewMP3.setItemChecked(0, true);

        listviewMP3.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                selectedMP3 = list.get(position);
            }
        });

        btnLike.setOnClickListener(this);
        btnLikeList.setOnClickListener(this);
        btnPlay.setOnClickListener(this);
        btnStop.setOnClickListener(this);
        btnPlay.setEnabled(true);
        btnStop.setEnabled(false);

        //progressBar 보이게 하면서 진행률은 0 으로 세팅
        pbMP3.setProgress(0);
        selectedMP3 = list.get(0);
        //진행시간
        tvTime.setText("진행시간: 0");

        //seekBar 내가 움직일 때마다 그 부분에서 음악 시작하기
        pbMP3.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mSeek = seekBar.getProgress();
                mediaPlayer.seekTo(mSeek);
                mediaPlayer.start();
            }
        });//end of serOnSeekBarChangedListener
    }//end of onCreate()

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnPlay:
                mediaPlayer = new MediaPlayer();
                try {
                    mediaPlayer.setDataSource(MP3_PATH + selectedMP3);
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                    btnPlay.setEnabled(false);
                    btnStop.setEnabled(true);
                    tvMP3.setText("실행중인 음악:" + selectedMP3);

                    //스레드 시작
                    //스레드 안에서는 위젯값을 바꾸면 안된다. 멈춰버린다. 그렇기 때문에 runOnUiThread 써야함
                    Thread thread = new Thread() {
                        //시간 표현 방식
                        SimpleDateFormat timeFormat = new SimpleDateFormat("mm:ss");
                        @Override
                        public void run() {
                            if (mediaPlayer == null) {
                                return;
                            }
                            //1. 노래 총 재생시간 == mediaPlayer.getDuration()
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    tvTime.setText(selectedMP3 + " 재생 시간" + mediaPlayer.getDuration());
                                    //progressBar 최대 시간 주기
                                    pbMP3.setMax(mediaPlayer.getDuration());
                                }
                            });//end of renOnUniThread(1)

                            //2. seekBar 움직이는것
                            while (mediaPlayer.isPlaying()) {
                                //스레드 안에서 위젯값을 변경하기 위해서  runOnUiThread 쓴다.
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        pbMP3.setProgress(mediaPlayer.getCurrentPosition());
                                        tvTime.setText("진행 중 시간: " + timeFormat.format(mediaPlayer.getCurrentPosition()) + "/" + timeFormat.format(mediaPlayer.getDuration()));
                                    }
                                });//end of runOnUiThread(2)는 스레드 안에서 화면위젯을 변경할 수 있는 스레드이다.
                                //seekBar가 0.1초마다 움직이게 하기
                                SystemClock.sleep(100);
                            }//end of while
                        }
                    };// end of 큰 Thread
                    thread.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btnStop:
                mediaPlayer.stop();
                mediaPlayer.reset();
                btnPlay.setEnabled(true);
                btnStop.setEnabled(false);
                tvMP3.setText("음악: ");
                pbMP3.setProgress(0);//현재 위치를 지정
                tvTime.setText("진행 시간: ");
                break;

            case R.id.btnLike:
                View view = View.inflate(this, R.layout.like_dialog, null);
                final EditText edtTitle = view.findViewById(R.id.edtTitle);
                final EditText edtSinger = view.findViewById(R.id.edtSinger);
                final ImageView albumImg = view.findViewById(R.id.albumImg);
                final String imageName = MP3_PATH + selectedMP3;

                //노래의 앨범 이미지 가져오기
                MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                mmr.setDataSource(imageName);

                byte[] data = mmr.getEmbeddedPicture();
                if (data != null) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                    albumImg.setImageBitmap(bitmap);
                } else {
                    albumImg.setImageResource(R.mipmap.empty_album);
                }
                albumImg.setAdjustViewBounds(true);
                //

                AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                dialog.setTitle("Like this song");
                dialog.setView(view);

                dialog.setPositiveButton("추가", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                        String title = edtTitle.getText().toString().trim();
                        String singer = edtSinger.getText().toString().trim();
                        try {
                            myDBHelper = new MyDBHelper(MainActivity.this);
                            sqLiteDatabase = myDBHelper.getWritableDatabase();

                            sqLiteDatabase.execSQL("INSERT INTO musicTBL VALUES (null, '" + title + "'," + "'" + singer + "'," + "'" + imageName + "'" + ");");
                            sqLiteDatabase.close();
                            Toast.makeText(MainActivity.this, "성공", Toast.LENGTH_LONG).show();
                        } catch (SQLException sqle) {
                            Toast.makeText(MainActivity.this, sqle.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }

                });
                dialog.setNegativeButton("돌아가기", null);
                dialog.show();
                break;
            case R.id.btnLikeList:
                Intent intent = new Intent(MainActivity.this, SubActivity.class);
                startActivity(intent);
                break;
        }
    }
}
