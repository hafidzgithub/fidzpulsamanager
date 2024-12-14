import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class Player extends AppCompatActivity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.layout_cara_menggunakan);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle((CharSequence) "Cara Menggunakan");
    }
}
