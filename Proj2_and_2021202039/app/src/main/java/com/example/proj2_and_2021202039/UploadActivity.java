package com.example.proj2_and_2021202039;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

// 업로드 페이지
public class UploadActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_PICK = 1; // 이미지 선택 요청 코드


    EditText titleButton;
    EditText contentButton;
    Bitmap selectedImageBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        //갤러리로 이동하기
        Button backButton=(Button) findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(UploadActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        // 이미지 선택 버튼
        Button uploadImageButton=(Button)findViewById(R.id.uploadImageButon);
        uploadImageButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                openGallery();
            }
        });

        //제목과 내용
        titleButton= (EditText) findViewById(R.id.title_editText);
        contentButton=(EditText)findViewById(R.id.content_editText);

        // 업로드 버튼
        Button uploadButton=(Button) findViewById(R.id.uploadArticleButton);
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // AsyncTask를 사용하여 서버로 데이터 전송
                String title = titleButton.getText().toString();
                String content = contentButton.getText().toString();
                if (selectedImageBitmap != null && !title.isEmpty() && !content.isEmpty()) {
                    Bitmap resizedBitmap = Bitmap.createScaledBitmap(selectedImageBitmap, 300, 300, true);
                    new UploadTask(title, content, resizedBitmap).execute();
                } else {
                    Toast.makeText(UploadActivity.this, "모든 필드를 채워주세요.", Toast.LENGTH_SHORT).show();
                }

                Intent intent=new Intent(UploadActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    
    
    // 로컬 갤러리 열기
    public void openGallery(){
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, REQUEST_IMAGE_PICK); // 이미지 선택 요청
    }

    // 이미지 선택 후 결과 처리: selectedImageBitmap 할당
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            try {
                selectedImageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                // 이미지를 ImageView에 표시하거나 처리할 수 있음
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //서버로 업로드 요청(/board)
    private class UploadTask extends AsyncTask<Void, Void, Boolean> {

        private String title;
        private String content;
        private Bitmap image;

        public UploadTask(String title, String content, Bitmap image) {
            this.title = title;
            this.content = content;
            this.image = image;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                // 이미지 파일을 임시로 생성하여 저장
                File tempFile = File.createTempFile("image", ".jpg");
                FileOutputStream fos = new FileOutputStream(tempFile);
                image.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.close();

                // OkHttpClient 객체 생성
                OkHttpClient client = new OkHttpClient();

                // MultipartBody를 사용하여 multipart/form-data 요청 구성
                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("title", title)
                        .addFormDataPart("content", content)
                        .addFormDataPart("image", tempFile.getName(),
                                RequestBody.create(MediaType.parse("image/jpeg"), tempFile))
                        .build();

                // Request 객체 생성
                Request request = new Request.Builder()
                        .url("http://10.0.2.2:8080/board")
                        .post(requestBody)
                        .build();

                // 요청 실행 및 응답 처리
                Response response = client.newCall(request).execute();
                return response.isSuccessful();

            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                Toast.makeText(getApplicationContext(), "업로드 성공", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "업로드 실패", Toast.LENGTH_SHORT).show();
            }
        }
    }

    }