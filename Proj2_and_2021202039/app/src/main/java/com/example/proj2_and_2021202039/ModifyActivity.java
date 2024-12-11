package com.example.proj2_and_2021202039;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ModifyActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_PICK = 1; // 이미지 선택 요청 코드

    Board board;

    EditText titleButton;
    EditText contentButton;
    Bitmap selectedImageBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify);

        board = getIntent().getParcelableExtra("board");

        EditText titleEditText = findViewById(R.id.title_editText);
        EditText contentEditText = findViewById(R.id.content_editText);
        Button uploadImageButton = findViewById(R.id.uploadImageButon);
        Button uploadArticleButton = findViewById(R.id.uploadArticleButton);
        Button backButton = findViewById(R.id.backButton);

        //갤러리로 이동하기
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ModifyActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        // 이미지 업로드 버튼
        uploadImageButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                openGallery();
            }
        });

        // 서버로 전달
        uploadArticleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = titleEditText.getText().toString();
                String content = contentEditText.getText().toString();
                if (selectedImageBitmap != null && !title.isEmpty() && !content.isEmpty()) {
                    Bitmap resizedBitmap = Bitmap.createScaledBitmap(selectedImageBitmap, 300, 300, true);
                    new ModifyBoardTask(board.getId(), title, content, resizedBitmap).execute();
                } else {
                    Toast.makeText(ModifyActivity.this, "모든 필드를 채워주세요.", Toast.LENGTH_SHORT).show();
                }
                Intent intent=new Intent(ModifyActivity.this, MainActivity.class);
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

    //서버로 수정 요청(/board/id)
    private class ModifyBoardTask extends AsyncTask<Void, Void, Boolean> {
        private long boardId;
        private String title;
        private String content;
        private Bitmap image;

        ModifyBoardTask(long boardId, String title, String content, Bitmap image) {
            this.boardId = boardId;
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
                        .addFormDataPart("id", String.valueOf(boardId))
                        .addFormDataPart("title", title)
                        .addFormDataPart("content", content)
                        .addFormDataPart("image", tempFile.getName(),
                                RequestBody.create(MediaType.parse("image/jpeg"), tempFile))
                        .build();

                // Request 객체 생성
                Request request = new Request.Builder()
                        .url("http://10.0.2.2:8080/board/"+boardId)
                        .put(requestBody)
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
        protected void onPostExecute(Boolean aVoid) {
            Intent intent = new Intent(ModifyActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }

    }
    private String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }
}