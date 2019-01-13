package company.myproject.www.bepatient;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * '기록' 탭에 보여줄 리스트뷰에 보여줄 데이터를 가지고 있을 수 있는 클래스
 */
public class ListTextItem {

    private String[] mData;

    public ListTextItem(String obj01, String obj02) {
        mData = new String[2];
        mData[0] = obj01; // 날짜 넣을 공간
        mData[1] = obj02; // 카운트 넣을 공간
    }

    // 전체 문자열 배열 반환
    public String[] getData() {
        return mData;
    }

//    // 받은 인덱스에 해당하는 텍스트 데이터를 반환시키는 메소드
    public String getData(int index) {
        if(mData == null || index >= mData.length) {
            return null;
        }

        return mData[index];
    }
}
