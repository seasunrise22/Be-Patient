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
//    int currentCount;
//    int afterCount;
    TextView mTextView;
    Handler mHanlder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        //Log.d(TAG, "Tab01_CountFragment : onCreateView");
        View view = inflater.inflate(R.layout.fragment_tab01, container, false);

        mTextView = view.findViewById(R.id.screenOnCount); // 텍스트뷰 접근
//        if(((MainActivity)getActivity()) != null) {
//            if (((MainActivity) getActivity()).isBinding != null && ((MainActivity) getActivity()).isBinding) {
//                currentCount = ((MainActivity) getActivity()).countService.getScreenOnCount();
//                mTextView.setText("" + currentCount);
//            }
//        }

        // 그냥 TimerTask로 기능을 돌리면 스레드관련 에러떠서 Handler 이용해서 구현
        mHanlder = new Handler();
        TimerTask mTimerTask = new TimerTask() {
            @Override
            public void run() {
                mHanlder.post(new Runnable() {
                    @Override
                    public void run() {
                        if(((MainActivity)getActivity()) != null) {
                            if (((MainActivity) getActivity()).isBinding != null && ((MainActivity) getActivity()).isBinding) {
                                mTextView.setText("" + ((MainActivity) getActivity()).countService.getScreenOnCount());
                            }
                        }
                    }
                });
            }
        };

        Timer mTimer = new Timer();
        mTimer.schedule(mTimerTask, 0, 1000);



        // 텍스트 변경
        //Log.d(TAG, "isBinding is : " + ((MainActivity)getActivity()).isBinding);
//        if (((MainActivity) getActivity()) != null) { // 프래그먼트 뗏다 붙였다 할 때 간혹 null 되기도 하는 듯
//            if (((MainActivity) getActivity()).isBinding != null && ((MainActivity) getActivity()).isBinding) { // 서비스바인딩 상태가 null이 아니고 true면(바인딩중)
//                mTextView.setText("" + ((MainActivity) getActivity()).countService.getScreenOnCount());
//            } else { // 서비스바인딩 상태가 null이거나 false면(바인딩아님)
//                mTextView.setTextSize(32);
//                mTextView.setText("No signal");
//            }
//        }

//        FragmentTransaction ft = ((MainActivity) getActivity()).getSupportFragmentManager().beginTransaction();
//        ft.detach(Tab01_CountFragment.this);
//        ft.attach(Tab01_CountFragment.this);
//        ft.commitAllowingStateLoss();

//        // 텍스트 갱신을 위한 프래그먼트 새로고침(뗏다 붙였다)
//        Handler mHandler = new Handler();
//        mHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                if(((MainActivity)getActivity()).getSupportFragmentManager() != null) {
//                    FragmentTransaction ft = ((MainActivity)getActivity()).getSupportFragmentManager().beginTransaction();
//                    ft.detach(Tab01_CountFragment.this);
//                    ft.attach(Tab01_CountFragment.this);
//                    ft.commitAllowingStateLoss();
//                }
//            }
//        }, 1000); // 1초에 한 번씩 계속 화면을 뗏다 붙였다... 실시간 갱신 처럼 보이게

        return view;
    }
}
