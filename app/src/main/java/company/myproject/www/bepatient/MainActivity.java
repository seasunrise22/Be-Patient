package company.myproject.www.bepatient;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private ViewPager mViewPager;
    private SectionsPageAdapter adapter; // 프래그먼트를 전환시킬 어댑터
    private Intent serviceIntent;

    // 현재시각 받아오기용 멤버변수들
    long mNow;
    Date mDate;
    SimpleDateFormat mSdf;
    String mGetDate;

    // 카운트 변수값 통계자료로 넘기기 위한 준비
    // 통계자료 저장용 SharedPreferences
    SharedPreferences statPref;
    SharedPreferences.Editor statPrefEditor;
    // 날짜가 바뀌었음을 감지하고 바뀐 날짜에 데이터를 넘기기 위한 이전날짜, 현재날짜 세팅
    Date beforeDate; // 이전날짜
    Date currentDate; // 현재날짜

    /**
     * ServiceBinding 관련 시작
     */
    ScreenCountService countService; // 화면켜짐카운트 서비스 클래스
    Boolean isBinding; // 서비스바인딩 여부 확인용 변수
    ServiceConnection conn = new ServiceConnection() {
        // 서비스바인딩 할 때 던져줄 커넥션 객체
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            // 서비스바인딩 성공
            ScreenCountService.ScreenCountBinder serviceBinder = (ScreenCountService.ScreenCountBinder) iBinder;
            countService = serviceBinder.getService(); // ScreenCountService에서 구현한 Binder에서 서비스의 함수에 접근할 수 있도록 마련해 둔 getService()에 접근.
            //saveBindServiceState(true); // 서비스바인딩 상태 저장
            isBinding = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            // 예기치 않게 서비스와 연결이 끊기거나 종료되었을 때 호출되는 메서드. unbindService했다고 호출되지는 x.
            //saveBindServiceState(false); // 서비스바인딩 상태 저장
            isBinding = false;
        }
    };
    /**
     * ServiceBinding 관련 종료
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate");

        // 툴바 관련
        Toolbar mToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(mToolbar); // 툴바 set
        getSupportActionBar().setDisplayShowTitleEnabled(false); // 타이틀 없애고 커스텀 타이틀 적용

        // 탭 : 뷰페이저 with Sections adapter
        adapter = new SectionsPageAdapter(getSupportFragmentManager()); // 만들어둔 Section... 객체 생성
        mViewPager = findViewById(R.id.container);
        setupViewPager(mViewPager); // 아직 어댑터가 담기지 않은 뷰페이저 전달
        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager); // 어댑터가 달린 뷰페이저를 탭 레이아웃에 장착

        // 서비스 시작용 인텐트
        serviceIntent = new Intent(getApplicationContext(), ScreenCountService.class);

        /**
         * set 된 시간(자정)에 그 날 하루동안의 화면켜짐횟수(dailyData)를 통계저장용 파일로 넘겨서 저장시킴
         * 이거 안 쓰게 될수도 있음.
         */
        // Calendar 객체를 생성해 시간을 set
        Calendar mCalendar = Calendar.getInstance();
        mCalendar.set(Calendar.HOUR_OF_DAY, 24); // 자정
        mCalendar.set(Calendar.MINUTE, 0);
        mCalendar.set(Calendar.SECOND, 0);

        // AlarmManager에게 실행을 부탁할 PendingIntent 구현
        Intent mAlarmIntent = new Intent("company.myproject.www.bepatient.ALARM_START"); // manifest.xml에 지정해 둔 intent-filter 활용
        PendingIntent mPendingIntent = PendingIntent.getBroadcast(this, 0, mAlarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // AlarmManager를 얻어와서 세팅
        AlarmManager mAlarmManager = (AlarmManager)getSystemService(ALARM_SERVICE); // 알람매니저 하나 가져옴
        mAlarmManager.set(
                AlarmManager.RTC_WAKEUP,
                mCalendar.getTimeInMillis(),
                mPendingIntent
        );
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // 현재시각 표시용 현재시각 받아오기 작업
        mNow = System.currentTimeMillis(); // 현재시각을 구한다.
        mDate = new Date(mNow); // Date를 하나 생성하고 거기에 현재시각을 넣는다.
        mSdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()); // 표기방식을 설정한다.
        mGetDate = mSdf.format(mDate); // 날짜를 String 형태로 받아와서 저장한다.

        if(beforeDate == null) { // 최초실행일시
            beforeDate = mDate;
            currentDate = mDate;
        } else { // 최초실행이 아닐시
            currentDate = mDate;
            if(currentDate.after(beforeDate)) { // 현재날짜가 저장되어있는 이전날짜보다 이후라면(날짜가 바뀜)
                String searchDate = mSdf.format(beforeDate);
//                statPrefEditor.putString(searchDate, adapter.getItem(0).mCount)
                // 요런식으로 비교해서 데이터 집어넣으면 될 듯.
                // 근데 Main에 말고 걍 Tab01 프래그먼트 Resume 안에다가 구현하자.
                // 서비스로부터 받아 온 count 변수를 0으로 초기화...
                // 아, 액티비티에서 서비스 함수로 어떻게 접근하지? 서비스 함수에 있는 count 변수를 0으로 초기화 해야 하는데.
                // 카운트 변수를 지금처럼 서비스에 둬서 서비스에 접근한 후 데이터를 조작하거나.
                // 아니면... 그냥 Main에 count 변수를 static으로 둬서 전역으로 접근해버릴까?
            }
        }

        // 앱화면 활성화 될 때 마다 통계자료 데이터 확인하고 넘기기
        // 서비스에 구현하려니 스위치안끄면 어떻게 새 날짜를 받아서 데이터를 넘겨야할지 애매함. 서비스에 타이머 걸어두는것도 좀 그렇고.
        statPref = getSharedPreferences("pref_statData", Activity.MODE_PRIVATE); // 통계자료 저장을 위한 pref 파일 받아옴.
        if(statPref.getString(mGetDate, "no data").equals("no data")) { // 오늘 날짜로 된 데이터(변수)가 생성되어 있지 않다면
            statPrefEditor = statPref.edit(); // 데이터 수정을 위한 에디터 연결.
            statPrefEditor.putString(mGetDate, "checking now..."); // 오늘 날짜로 변수 생성하고 변수값으로 checking now... 입력.
            statPrefEditor.apply();
        }

//        Log.d(TAG, "mNow is # " + mNow);
//        Log.d(TAG, "mDate is # " + mDate);
//        Log.d(TAG, "mSdf is # " + mSdf);
//        Log.d(TAG, "mGetDate is # " + mGetDate);
//        Log.d(TAG, "beforeDate is # " + beforeDate);
//        Log.d(TAG, "currentDate is # " + currentDate);
    }

    // onStop은 스마트폰 화면만 꺼도 호출 됨.(액티비티가 전면에 없으면 무조건 호출)
    @Override
    protected void onStop() {
        super.onStop();
        saveSwitchState(loadSwitchState()); // 화면 안 보일 때 스위치 상태 저장
        //saveBindServiceState(loadBindServiceState()); // 화면 안 보일 때 서비스바인딩 상태 저장.
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(conn); // 어플 종료되면 서비스바인딩 해제(serviceConnectionLeaked 문제 때문)
    }

    /**
     * 툴바에 menu.xml 인플레이트
     * 스위치 버튼을 툴바에 넣는다.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);

        /**
         * OnOff 스위치 기능 구현 관련 시작
         */
        View swLayout = menu.findItem(R.id.mySwitch).getActionView(); // menu에서 findItem으로 item을 가져오고 거기서 Action되는 View를 가져옴.
        Switch mSwitch = swLayout.findViewById(R.id.switchForToolBar); // Action되는 View로부터 스위치 View로 접근. 바로 findView.. 하면 Null Ref...뜸

        if(loadSwitchState()) { // 만약 저장된 스위치 상태(변수값)가 true 라면(초기값은 false)
            mSwitch.setChecked(true); // 스위치 초기 상태를 On으로 표시
            //if(!loadBindServiceState()) { // 스위치 초기 상태가 On인데 서비스는 바인딩되어 있지 않다면
            if(isBinding == null || isBinding == false) { // 스위치 초기 상태가 On인데 서비스는 바인딩되어 있지 않다면
                bindService(serviceIntent, conn, Context.BIND_AUTO_CREATE); // 서비스바인드 시작
                //saveBindServiceState(true);
            }
        }

        // 스위치 OnOff 리스너
        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked) { // 스위치 on
                    bindService(serviceIntent, conn, Context.BIND_AUTO_CREATE); // 서비스바인드 시작
                    startService(serviceIntent); // 화면켜짐카운트 누적시키는 서비스 시작
                    saveSwitchState(isChecked); // 스위치 상태를 'pref_saveData' 파일에 저장
                } else { // 스위치 off
                    stopService(serviceIntent);
                    if(isBinding) { // 현재 바인드서비스가 돌고 있다면
                        unbindService(conn); // 서비스 종료
                        //saveBindServiceState(false); // 서비스바인딩 상태 저장
                        isBinding = false;
                    }
                    saveSwitchState(isChecked); // 스위치 상태와 서비스바인딩여부 'pref_saveData' 파일에 저장
                }
            }
        });
        /**
         * OnOff 스위치 기능 구현 관련 종료
         */

        return true;
    }

    // 탭 뷰페이저 : 받은 뷰페이저 객체에 프레그먼트와 타이틀 정보가 담긴 어댑터 객체를 세트
    private void setupViewPager(ViewPager viewPager) {
        adapter.addFragment(new Tab01_CountFragment(), "카운트");
        adapter.addFragment(new Tab02_StatFragment(), "통계");
        adapter.addFragment(new Tab03_EmptyFragment(), "빈탭03");
        adapter.addFragment(new Tab04_EmptyFragment(), "빈탭04");
        viewPager.setAdapter(adapter);
    }

    // 현재 스위치 상태를 'pref_MainActivity' 파일에 저장시키는 함수
    private void saveSwitchState(Boolean sw) {
        SharedPreferences pref = getSharedPreferences("pref_saveData", Activity.MODE_PRIVATE); // 스위치 상태를 저장해둔 pref 파일 가져오기
        SharedPreferences.Editor editor = pref.edit(); // SharedPreferences 상태를 수정하기 위한 Editor 생성
        editor.putBoolean("switchState", sw); // switchState 변수에 스위치 상태값 저장
        editor.apply();
    }

    // 스위치의 현재 상태를 불러오기 위한 함수
    public boolean loadSwitchState() {
        SharedPreferences pref = getSharedPreferences("pref_saveData", Activity.MODE_PRIVATE); // 스위치 상태를 저장해둔 pref 파일 가져오기
        return pref.getBoolean("switchState", false); // switchState 변수의 값을 return. 초기값은 false.
    }
}
