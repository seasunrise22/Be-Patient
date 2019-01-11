package company.myproject.www.bepatient;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

/**
 * 통계 데이터 보여줄 프래그먼트. 원래 statistics 인데 stat 으로 줄임.
 */
public class Tab02_StatFragment extends android.support.v4.app.Fragment {

    ListView mListView;
    ListTextAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab02, container, false);

        mListView = (ListView) view.findViewById(R.id.listView01); // 리스트뷰 객체 생성

        adapter = new ListTextAdapter(getActivity()); // 어댑터 객체 생성
        adapter.addItem(new ListTextItem("테스트 날짜", "테스트 카운트")); // 아이템 생성한 후 어댑터에 추가
        mListView.setAdapter(adapter); // 리스트뷰에 어댑터 설정

        // 새로 정의한 리스너로 객체를 만들어 설정
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
