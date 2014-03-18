package ironrabbit.gmapstest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class BlankForTest extends Activity {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent i=getIntent();
        TextView title = new TextView(this);
        title.setText(i.getStringExtra("lol"));
        setContentView(title);
    }
}