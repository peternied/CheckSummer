
package com.microsoft.checksumer;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {

    private static final int REQUEST_CODE_OPEN_DOCUMENT = 42;

    private static final int REQUEST_CODE_PICK_FILE = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Activity thisActivity = this;
        Button button = (Button)findViewById(R.id.main_open_file_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PackageManager manager = thisActivity.getPackageManager();

                Intent intent = new Intent("android.intent.action.GET_CONTENT");
                intent.addCategory("android.intent.category.DEFAULT");
                intent.setType("*/*");
                int requestCode = REQUEST_CODE_PICK_FILE;

                if (manager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY).isEmpty()) {
                    Log.d("CheckSumer-MainActivity", "Unable to resolve intent: " + intent.toString());
                }
                /*
                 * if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                 * intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                 * intent.addCategory(Intent.CATEGORY_OPENABLE); requestCode =
                 * REQUEST_CODE_OPEN_DOCUMENT; }
                 * if(manager.queryIntentActivities(intent,
                 * PackageManager.MATCH_DEFAULT_ONLY).isEmpty()) {
                 * Log.d("CheckSumer-MainActivity", "Unable to resolve intent: "
                 * + intent.toString()); }
                 */

                thisActivity.startActivityForResult(intent, requestCode);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_PICK_FILE && data != null && data.getData() != null) {
            Intent intent = new Intent();
            intent.setData(data.getData());
            intent.setClass(getApplication(), CheckSumActivity.class);
            startActivity(intent);
            return;
        }

        if (requestCode == REQUEST_CODE_OPEN_DOCUMENT && resultCode == Activity.RESULT_OK && data != null) {
            Uri uri = data.getData();

            Log.i("MainActivity", "URI: " + uri.toString());
         }
    }
}
