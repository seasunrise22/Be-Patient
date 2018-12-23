package company.myproject.www.bepatient;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * 통계 데이터 보여줄 프래그먼트. 원래 statistics 인데 stat 으로 줄임.
 */
public class Tab02_StatFragment extends android.support.v4.app.Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab02, container, false);

        return view;
    }
}
