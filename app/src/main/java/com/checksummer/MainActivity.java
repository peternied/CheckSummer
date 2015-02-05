
package com.checksummer;

import com.microsoft.onedrivesdk.picker.IPicker;
import com.microsoft.onedrivesdk.picker.IPickerResult;
import com.microsoft.onedrivesdk.picker.LinkType;
import com.microsoft.onedrivesdk.picker.Picker;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {

    // {@see https://account.live.com/developers/applications}
    private static final String CHECKSUM_LIVE_APP_ID = "000000004012FB6F";
    private static final int REQUEST_CODE_PICK_FILE = 1000;
    private IPicker mPicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPicker = Picker.createPicker(CHECKSUM_LIVE_APP_ID);

        final Activity thisActivity = this;
        Button button = (Button)findViewById(R.id.main_open_file_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                PackageManager manager = thisActivity.getPackageManager();

                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                intent.setType("*/*");
                int requestCode = REQUEST_CODE_PICK_FILE;

                if (manager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY).isEmpty()) {
                    Log.d("CheckSumer-MainActivity", "Unable to resolve intent: " + intent.toString());
                }

                thisActivity.startActivityForResult(intent, requestCode);
            }
        });

        final Button buttonOneDrive = (Button)findViewById(R.id.main_open_file_onedrive);
        buttonOneDrive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                mPicker.startPicking((Activity)v.getContext(), LinkType.DownloadLink);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        final Intent intent = new Intent();
        intent.setClass(getApplication(), CheckSumActivity.class);

        final IPickerResult onedrivePickerResult = mPicker.getPickerResult(requestCode, resultCode, data);
        if (onedrivePickerResult != null) {
            intent.setData(onedrivePickerResult.getLink());
        } else if (requestCode == REQUEST_CODE_PICK_FILE && data != null && data.getData() != null) {
            intent.setData(data.getData());
        } else {
            // If the did not get a known result for the activity bail!
            return;
        }
        startActivity(intent);
    }
}
