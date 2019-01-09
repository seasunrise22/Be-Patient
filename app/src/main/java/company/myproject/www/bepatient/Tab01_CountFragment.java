package company.myproject.www.bepatient;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 1번 탭
 * 화면켜짐횟수 디스플레이 구현
 */
public class Tab01_CountFragment extends android.support.v4.app.Fragment {

    public static final String TAG = "Tab01_CountFragment";
    TextView mCountText;
    TextView mDateText;
    Handler mHanlder;
    int mCount;

    // 현재시각 받아오기용 멤버변수들
    long mNow;
    Date mDate;
    SimpleDateFormat mSdf;
    String mGetDate;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        //Log.d(TAG, "Tab01_CountFragment : onCreateView");
        View view = inflater.inflate(R.layout.fragment_tab01, container, false);

        mCountText = view.findViewById(R.id.screenOnCount); // 카운트 보여줄 텍스트뷰 접근
        mDateText = view.findViewById(R.id.dateView); // 오늘 날짜 보여줄 텍스트뷰 접근

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        // 현재시각 표시용 현재시각 받아오기 작업
        mNow = System.currentTimeMillis(); // 현재시각을 구한다.
        mDate = new Date(mNow); // Date를 하나 생성하고 거기에 현재시각을 넣는다.
        mSdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()); // 표기방식을 설정한다.
        mGetDate = mSdf.format(mDate); // 날짜를 String 형태로 받아와서 저장한다.

        Log.d(TAG, "mNow is # " + mNow);
        Log.d(TAG, "mDate is # " + mDate);
        Log.d(TAG, "mSdf is # " + mSdf);
        Log.d(TAG, "mGetDate is # " + mGetDate);

        // 실시간으로 텍스트가 변화하는 것처럼 보이기 위한 TimerTask와 Handler 사용부분
        mDateText.setText(((MainActivity) getActivity()).mGetDate);

        // 그냥 TimerTask로 기능을 돌리면 스레드관련 에러떠서 Handler 이용해서 구현
        mHanlder = new Handler();
        TimerTask mTimerTask = new TimerTask() {
            @Override
            public void run() {
                mHanlder.post(new Runnable() { // UI변경은 메인쓰레드에서만 가능하므로 Handler의 post로 메인스레드의 메시지큐에 던짐
                    @Override
                    public void run() {
                        if(((MainActivity)getActivity()) != null) { // 메인액티비티와 순간 떨어졌을 때 getActivity 쓰면 가끔 에러남
                            if (((MainActivity) getActivity()).isBinding != null && ((MainActivity) getActivity()).isBinding) { // 서비스바인딩상태라면
                                mCount = ((MainActivity) getActivity()).countService.getScreenOnCount();
                                mCountText.setText("" + mCount);
                            }
                        }
                    }
                });
            }
        };

        Timer mTimer = new Timer();
        mTimer.schedule(mTimerTask, 0, 1000);
    }
}
