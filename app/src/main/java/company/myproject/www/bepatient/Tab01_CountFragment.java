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

import java.util.Timer;
import java.util.TimerTask;

/**
 * 1번 탭
 * 화면켜짐횟수 디스플레이 구현
 */
public class Tab01_CountFragment extends android.support.v4.app.Fragment {

    public static final String TAG = "Tab01_CountFragment";
    TextView mTextView;
    Handler mHanlder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        //Log.d(TAG, "Tab01_CountFragment : onCreateView");
        View view = inflater.inflate(R.layout.fragment_tab01, container, false);

        mTextView = view.findViewById(R.id.screenOnCount); // 텍스트뷰 접근

        // 그냥 TimerTask로 기능을 돌리면 스레드관련 에러떠서 Handler 이용해서 구현
        mHanlder = new Handler();
        TimerTask mTimerTask = new TimerTask() {
            @Override
            public void run() {
                mHanlder.post(new Runnable() {
                    @Override
                    public void run() {
                        if(((MainActivity)getActivity()) != null) { // 메인액티비티와 순간 떨어졌을 때 getActivity 쓰면 가끔 에러남
                            if (((MainActivity) getActivity()).isBinding != null && ((MainActivity) getActivity()).isBinding) { // 서비스바인딩상태라면
                                mTextView.setText("" + ((MainActivity) getActivity()).countService.getScreenOnCount());
                            }
                        }
                    }
                });
            }
        };

        Timer mTimer = new Timer();
        mTimer.schedule(mTimerTask, 0, 1000);

        return view;
    }
}
