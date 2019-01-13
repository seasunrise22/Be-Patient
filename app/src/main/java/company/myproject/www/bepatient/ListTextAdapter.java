package company.myproject.www.bepatient;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * '기록' 탭에 보여줄 리스트뷰를 선택해서 보여줄 어댑터 클래스
 */
public class ListTextAdapter extends BaseAdapter {
    private Context mContext;

    // 각 아이템의 데이터를 담고 있는 ListTextItem 객체를 저장할 ArrayList 객체
    private List<ListTextItem> mItems = new ArrayList<ListTextItem>();

    public ListTextAdapter(Context context) {
        mContext = context;
    }

    // 리스트에 아이템 추가
    public void addItem(ListTextItem it) {
        mItems.add(it);
    }

    // 전체 아이템의 개수를 리턴하는 메소드
    public int getCount() {
        return mItems.size();
    }

    // 리스트에서 특정 아이템을 가져오는 메소드
    public Object getItem(int position) {
        return mItems.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    // 아이템에 표시할 뷰를 리턴하는 메소드
    public View getView(int position, View convertView, ViewGroup parent) {
        ListTextView itemView;
        // convertView는 각 아이템을 매번 새로 생성하지 않고 재활용 하기 위함
        if(convertView == null) {
            itemView = new ListTextView(mContext, mItems.get(position));
        } else {
            itemView = (ListTextView) convertView;
        }

        itemView.setText(0, mItems.get(position).getData(0)); // 뷰의 0번 인덱스에는 item에 담긴 날짜를
        itemView.setText(1, mItems.get(position).getData(1)); // 뷰의 1번 인덱스에는 item에 담긴 카운트를
        return itemView;
    }
}
