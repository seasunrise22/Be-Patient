package company.myproject.www.bepatient;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * '기록' 탭에 보여줄 리스트뷰를 객체화 시키는 클래스
 */
public class ListTextView extends LinearLayout {

    TextView mTextDate;
    TextView mTextCount;

    public ListTextView(Context context, ListTextItem mItem) {
        super(context);

        // 메모리 객체화
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.listitem, this, true);

        // 뷰 객체 참조
        mTextDate = (TextView) findViewById(R.id.list_date);
        mTextDate.setText(mItem.getData(0));
        mTextCount = (TextView) findViewById(R.id.list_count);
        mTextCount.setText(mItem.getData(1));
    }

    public void setText(int index, String data) {
        if(index == 0) {
            mTextDate.setText(data);
        } else if(index == 1) {
            mTextCount.setText(data);
        } else {
            throw new IllegalArgumentException();
        }
    }
}
