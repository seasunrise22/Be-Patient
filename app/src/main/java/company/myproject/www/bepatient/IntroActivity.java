package company.myproject.www.bepatient;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class IntroActivity extends AppCompatActivity {

    TextView mIntroText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        mIntroText = findViewById(R.id.introText); // 인트로 텍스트뷰 연결

        // 인트로 화면 텍스트에 커스텀 폰트 적용
        mIntroText.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/nanumpen.ttf"));

        // TotalActivity로의 전환
        Handler mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {
            //Do Something
            @Override
            public void run() {
                // TODO Auto-generated method stub
                Intent intent = new Intent(IntroActivity.this, MainActivity.class);
                startActivity(intent);
            }
        }, 2000); // 밀리세컨드(1000분의 1초)
    }

    // 화면 전환되면 IntroActivity는 Destroy한다.
    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
