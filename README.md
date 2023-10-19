# Be-Patient
- 개발인원 : 1명
- 역할
  - 전체
## Introduction
스마트폰 화면이 켜진 횟수를 카운팅 해주는 안드로이드 애플리케이션 입니다. 

백그라운드에서 서비스로 동작하며 자정을 기준으로 날짜별 카운트 기록이 집계됩니다. 

집계되는 카운트 기록은 단순 숫자에 불과하므로 데이터베이스가 아닌 SharedPreferences를 활용하여 디바이스 내부에 자체 저장되도록 구현했습니다. 

## Development Environment
- IDE : Android Studio
- Language : Java

## Screenshots
![resize_bepatient_01](https://user-images.githubusercontent.com/45503931/56092683-ef874c80-5ef9-11e9-910c-a7aeb36c0141.png)
![resize_bepatient_02](https://user-images.githubusercontent.com/45503931/56092684-ef874c80-5ef9-11e9-96f8-cd37bacd5b47.png)

![resize_bepatient_03](https://user-images.githubusercontent.com/45503931/56092685-ef874c80-5ef9-11e9-8d25-cccf879cb570.png)
![resize_bepatient_04](https://user-images.githubusercontent.com/45503931/56092682-eeeeb600-5ef9-11e9-916a-2a2f7f1a89eb.png)

## Code Preview
***서비스 동작 중 화면이 켜지면 반응할 Receiver 구현***
```java
public int onStartCommand(Intent intent, int flags, int startId) {

    startForeground(1, setNotification()); // TaskKiller에 서비스가 죽지 않도록 하기 위하여
                                           // + 노티피케이션 실행

    // 화면켜짐 카운트 횟수 누적과 저장을 위한 SharedPreferences 사전작업
    pref = getSharedPreferences("pref_saveData", Activity.MODE_PRIVATE); // 카운트 횟수를 누적시켜둔 변수에 접근하기 위한 
                                                                         // sharedPreferences 연결 통로 생성
    editor = pref.edit(); // SharedPreferences 수정을 위한 에디터 호출.

    // 화면켜짐액션 받을 리시버 객체 생성과 정의
    mReceiver = new BroadcastReceiver() {
    // 리시버 필터를 통한 행동감지 및 행동구현

        @Override
        public void onReceive(Context context, Intent intent) { // 리시버 반응
            // 화면이 켜질 때 마다 이제 dailyCount를 받아온다.
            screenOnCount = pref.getInt("dailyCount", 0); // 저장된 당일 카운트 횟수 변수를 불러온다. 초기값은 0.

            if(intent.getAction().equals(Intent.ACTION_USER_PRESENT)) { // 화면이 켜졌을 때의 상황
                screenOnCount++; // 저장된 횟수에 1을 더하여 누적시킴
                editor.putInt("dailyCount", screenOnCount); // 'pref_saveData' 파일의 'dailyCount' 변수에 
                                                        // 누적된 'screenOnCount' 변수를 저장.
                editor.apply();
            }
        }
    };
    mIntentFilter = new IntentFilter(Intent.ACTION_USER_PRESENT); // 화면 켜짐(잠금화면 풀린 상태) 액션 필터 등록
    registerReceiver(mReceiver, mIntentFilter); // 브로드캐스트 리시버 등록
```
