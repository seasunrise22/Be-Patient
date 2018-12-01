package company.myproject.www.bepatient;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import static android.app.PendingIntent.FLAG_CANCEL_CURRENT;

public class ScreenCountService extends Service {

    public static final String TAG = "ScreenCountService";

    private BroadcastReceiver mReceiver;
    private IntentFilter mIntentFilter;
    private int screenOnCount = 0; // 화면켜짐횟수 누적시킬 변수

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

        // 화면켜짐액션 받을 리시버 등록
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent.getAction().equals(Intent.ACTION_USER_PRESENT)) {
                    screenOnCount++; // 화면켜짐 카운트
                    Log.d(TAG, "testCount is # " + screenOnCount);
                }
            }
        };
        mIntentFilter = new IntentFilter(Intent.ACTION_USER_PRESENT); // 화면 켜짐(잠금화면 풀린 상태) 액션 필터 등록
        registerReceiver(mReceiver, mIntentFilter); // 브로드캐스트 리시버 등록

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
     * 화면켜짐횟수를 누적한 변수를 반환하는 함수
     **/
    public int getScreenOnCount() {
        return screenOnCount;
    }
}
