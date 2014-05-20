
package com.microsoft.checksumer;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {

    private static final int REQUEST_CODE_OPEN_DOCUMENT = 42;

    private static final int REQUEST_CODE_PICK_FILE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Activity thisActivity = this;
        Button button = (Button)findViewById(R.id.main_open_file_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("org.openintents.action.PICK_FILE");
                intent.putExtra("org.openintents.extra.TITLE", "Open File");
                intent.putExtra("org.openintents.extra.BUTTON_TEXT", "Open");
                int requestCode = REQUEST_CODE_PICK_FILE;

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    requestCode = REQUEST_CODE_OPEN_DOCUMENT;
                }

                thisActivity.startActivityForResult(intent, requestCode);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_PICK_FILE && resultCode == Activity.RESULT_OK) {

        }

        if (requestCode == REQUEST_CODE_OPEN_DOCUMENT && resultCode == Activity.RESULT_OK && data != null) {
            Uri uri = data.getData();
            Log.i("MainActivity", "URI: " + uri.toString());
        }
    }
}
