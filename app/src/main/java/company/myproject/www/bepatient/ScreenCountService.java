package company.myproject.www.bepatient;

import android.app.Activity;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static android.app.PendingIntent.FLAG_CANCEL_CURRENT;

public class ScreenCountService extends Service {

    public static final String TAG = "ScreenCountService";

    private BroadcastReceiver mReceiver;
    private IntentFilter mIntentFilter;

    // 카운트 변수값 통계자료로 넘기기 위한 준비
    // 현재시각 구하기 위한 변수들
    long mNow;
    Date mDate;
    SimpleDateFormat mSdf;
    String mGetDate;
    // 통계자료 저장용
    SharedPreferences sPref;
    SharedPreferences.Editor sPrefEditor;

    /**
     * 서비스바인딩을 위한 Binder 구현
     */
    private final IBinder mBinder = new ScreenCountBinder(); // 클라이언트로 넘겨줄 Binder
    public class ScreenCountBinder extends Binder {
        ScreenCountService getService() {
            // Return this instance of ScreenCountService so clients can call public methods
            return ScreenCountService.this;
        }
    }

    /**
     * BindService() 로 호출됐을 경우 onBind 실행
     */
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind");
        return mBinder;
    }

    /**
     * startService() 로 호출됐을 경우 onStartCommand 실행
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");

        startForeground(1, setNotification()); // TaskKiller에 서비스가 죽지 않도록 하기 위하여
        // + 노티피케이션 실행

        // 화면켜짐액션 받을 리시버 객체 생성과 정의
        mReceiver = new BroadcastReceiver() {

            // 화면켜짐 카운트 횟수 누적과 저장을 위한 SharedPreferences 사전작업
            SharedPreferences pref = getSharedPreferences("pref_saveData", Activity.MODE_PRIVATE); // 카운트 횟수를 누적시켜둔 변수에 접근하기 위한 sharedPreferences 연결 통로 생성
            SharedPreferences.Editor editor = pref.edit(); // SharedPreferences 수정을 위한 에디터 호출.
            int screenOnCount = pref.getInt("dailyCount", 0); // 저장된 당일 카운트 횟수 변수를 불러온다. 초기값은 0.

            // 리시버 필터를 통한 행동감지 및 행동구현
            @Override
            public void onReceive(Context context, Intent intent) { // 리시버 반응
                if(intent.getAction().equals(Intent.ACTION_USER_PRESENT)) { // 화면이 켜졌을 때의 상황
                    screenOnCount++; // 저장된 횟수에 1을 더하여 누적시킴
                    editor.putInt("dailyCount", screenOnCount); // 'pref_saveData' 파일의 'dailyCount' 변수에 누적된 'screenOnCount' 변수를 저장.
                    editor.apply();
                    //Log.d(TAG, "testCount is # " + screenOnCount);
                }
            }
        };
        mIntentFilter = new IntentFilter(Intent.ACTION_USER_PRESENT); // 화면 켜짐(잠금화면 풀린 상태) 액션 필터 등록
        registerReceiver(mReceiver, mIntentFilter); // 브로드캐스트 리시버 등록

        /**
         * statData 파일로 dailyData(일별 카운트) 변수값과 날짜 넘겨서 통계자료 만들기
         * 알고리즘:
         *  1) 스위치를 켠다. (지금 이 서비스의 onStartCommand 실행 됨)
         *  2) 당일 날짜를 받아서 변수에 넣는다. 이전 날짜는 또 다른 날짜에 저장되어 있는 상태. 변수 두개는 배열로 관리.
         *  3) 분기
         *   3-1) 이전 날짜와 당일 날짜를 비교하여 같으면 날짜가 지나지 않은 것이므로 동작 없음. 계속 카운트.
         *   3-2) 이전 날짜와 당일 날짜를 비교하여 다르면 날짜가 지난 것이므로 이전 날짜와 남아있는 dailyData 변수값을 함께 통계자료로 넘김.
         *        이전 날짜가 담겨져 있던 배열 공간은 비우고, 당일 날짜가 있던 공간만이 아 만들면서 생각해보자.
         */
        // 현재시각 구하기
        mNow = System.currentTimeMillis(); // 현재시각을 구한다.
        mDate = new Date(mNow); // Date를 하나 생성하고 거기에 현재시각을 넣는다.
        mSdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()); // 표기방식을 설정한다.
        mGetDate = mSdf.format(mDate); // 날짜를 String 형태로 받아와서 저장한다.
        //Log.d(TAG, "mGetDate is # " + mGetDate);

        // 통계자료 넘기기
        sPref = getSharedPreferences("pref_statData", Activity.MODE_PRIVATE); // 통계자료 저장을 위한 pref 파일 받아옴.
        if(sPref.getString(mGetDate, "no data").equals("no data")) { // 오늘 날짜로 된 데이터(변수)가 생성되어 있지 않다면
            sPrefEditor = sPref.edit(); // 데이터 수정을 위한 에디터 연결.
            sPrefEditor.putString(mGetDate, "checking now..."); // 오늘 날짜로 변수 생성함
            sPrefEditor.apply();
        }

        return super.onStartCommand(intent, flags, startId);
    }

    // 서비스 소멸시(스위치 Off) 호출
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");

        stopForeground(true); // Foreground 죽이고, 노티피케이션도 죽임.

        if(mReceiver != null) { // mReceiver가 null일때 unregister하면 에러뜸
            unregisterReceiver(mReceiver); // 리시버 등록 해제
        }
    }

    // 노티피케이션 설정 함수
    public Notification setNotification() {

        // 노티피케이션 터치시 액티비티 실행을 위한 인텐트 설정
        Intent mIntent = new Intent(this, MainActivity.class);
        mIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        mIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        // 액티비티 중복 실행을 막기 위한 플래그 설정

        // 설정된 인텐트를 펜딩인텐트에 set
        PendingIntent mPendingIntent =
                PendingIntent.getActivity(this, 0, mIntent, FLAG_CANCEL_CURRENT);
        // FLAG_CANCEL_CURRENT 없애면 액티비티 중복 생성됨. 어디서 펜딩인텐트가 여러번 호출되는듯.

        // 이것저것 노티피케이션 설정
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this, "M_CH_ID")
                        .setSmallIcon(R.drawable.icon_noti)
                        .setContentTitle("Be patient 실행 중")
                        .setContentText("화면 켜짐 횟수 체크 중")
                        .setContentIntent(mPendingIntent);

        Notification mNotification = mBuilder.build(); // 지금까지 설정한 Builder를 Build해서 Notification에 부착.
        mNotification.flags = Notification.FLAG_NO_CLEAR; // 노티피케이션 삭제 안 되도록 플래그 설정

        return mNotification; // 완성된 노티피케이션 덩어리를 리턴
    }

    /**
     * method for clients
     * 화면켜짐횟수를 누적한 변수를 반환하는 함수(SharedPreferences로 부터)
     * Tab01_CountFragment.java 에서 사용
     **/
    public int getScreenOnCount() {
        SharedPreferences pref = getSharedPreferences("pref_saveData", Activity.MODE_PRIVATE);
        return pref.getInt("dailyCount", 0);
    }
}
