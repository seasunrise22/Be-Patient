package company.myproject.www.bepatient;

import android.app.Activity;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.Map;
import java.util.TreeMap;

/**
 * 통계 데이터 보여줄 프래그먼트. 원래 statistics 인데 stat 으로 줄임.
 */
public class Tab02_StatFragment extends android.support.v4.app.Fragment {
    private String TAG = "Tab02_StatFragment";

    ListView mListView;
    ListTextAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab02, container, false);

        mListView = (ListView) view.findViewById(R.id.listView01); // 리스트뷰 객체 생성

        adapter = new ListTextAdapter(getActivity()); // 어댑터 객체 생성
        // 아이템 생성한 후 어댑터에 추가

        // pref_statsData 에서 데이터 뽑아서 리스트뷰로 보여줄 아이템에 집어넣기
        SharedPreferences myPrefs = getActivity().getSharedPreferences("pref_statsData", Activity.MODE_PRIVATE);
//        // 테스트용 데이터 넣기 시작
//        SharedPreferences.Editor prefEdit = myPrefs.edit();
//        prefEdit.putInt("2019-01-01", 1);
//        prefEdit.putInt("2019-01-02", 2);
//        prefEdit.putInt("2019-01-03", 3);
//        prefEdit.putInt("2019-01-04", 4);
//        prefEdit.apply();
//        // 테스트용 끝

        TreeMap<String, ?> keys = new TreeMap<String, Object>(myPrefs.getAll()); // TreeMap 이란게 값이 생성된 순서대로 쫘라락 정렬되는 느낌 같은거 같은데 ㅎ
        // for( A : B )
        // B에서 차례대로 객체를 꺼내서 A에다가 넣겠다 는 의미
        for (Map.Entry<String, ?> entry : keys.entrySet()) {
//            Log.i(TAG, "map keys " + entry.getKey());
//            Log.i(TAG, "map values " + entry.getValue());
            if(!entry.getKey().equals("beforeDate") && !entry.getKey().equals("currentDate")) {
                adapter.addItem(new ListTextItem(entry.getKey(), ""+entry.getValue()));
            }
        } // TreeMap으로 pref_statsData에서 데이터를 뽑아내고
        // 뽑아낸 데이터를 다시 Map.Entry로 넣고,
        // Map.Entry에서 getKey로 Key만 뽑아내는 구존가보네

        mListView.setAdapter(adapter); // 리스트뷰에 어댑터 설정

        // 새로 정의한 리스너로 객체를 만들어 설정
        // 각 뷰 터치하면 반응하는 부분인데 지금당장 딱히 필요는 없음.
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListTextItem curItem = (ListTextItem) adapter.getItem(position);
                String[] curData = curItem.getData();

                Toast.makeText(getActivity(), "Selected : " + curData[0], Toast.LENGTH_LONG).show();
            }
        });

        return view;
    }
}
