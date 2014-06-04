
package com.microsoft.checksumer;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.app.Activity;
import android.app.Fragment;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class CheckSumActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_sum);

        if(getIntent().getData() == null)
        {
            Log.e("CheckSumer-CheckSumActivity", "No file source provided!");
        }

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction().add(R.id.container, new PlaceholderFragment()).commit();
        }

        final Activity thisActivity = this;
        new AsyncTask<Uri, Long, String>() {
            @SuppressWarnings("unchecked")
            @Override
            protected String doInBackground(Uri... params) {
                Uri fileUrl = params[0];
                try {
                    ContentResolver contentResolver = getContentResolver();
                    ContentProviderClient contentProvider = contentResolver.acquireContentProviderClient(fileUrl);
                    ParcelFileDescriptor descriptor = contentProvider.openFile(fileUrl, "r");
                    int fileSize = (int)descriptor.getStatSize();

                    ByteArrayOutputStream memorySteam = new ByteArrayOutputStream(fileSize);
                    FileInputStream fis = new FileInputStream(descriptor.getFileDescriptor());
                    copyStreamContents(fileSize, fis, memorySteam);
                    byte[] fileInMemory = memorySteam.toByteArray();

                    AsyncTask<Object, Long, String>[] backgroundTasks = new AsyncTask[] {
                            generateTaskForCheckSumCalcuation(thisActivity, CheckSumType.SHA1, R.id.check_sum_sha1),
                            generateTaskForCheckSumCalcuation(thisActivity, CheckSumType.SHA256, R.id.check_sum_sha256),
                            generateTaskForCheckSumCalcuation(thisActivity, CheckSumType.MD5, R.id.check_sum_md5)
                    };
                    for (AsyncTask<Object, Long, String> task : backgroundTasks) {
                        task.execute(fileInMemory);
                    }
                } catch (Exception e) {
                    Log.e("CheckSumer-CheckSumActivity", "Unexpected error: " + e.toString());
                }
                return params[0].toString();
            }

            protected void onPostExecute(String result) {
                ((TextView)thisActivity.findViewById(R.id.check_sum_file_path)).setText(result);
            };
        }.execute(getIntent().getData());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.check_sum, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_check_sum, container, false);
            return rootView;
        }
    }

    private static int copyStreamContents(int size, InputStream input, OutputStream output) throws IOException {
        byte[] buffer = new byte[size];
        int count = 0;
        int n = 0;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }

    private static AsyncTask<Object, Long, String> generateTaskForCheckSumCalcuation(final Activity activity,
            final CheckSumType checkSumType, final int updatedViewResourceId) {
        return new AsyncTask<Object, Long, String>() {
            protected String doInBackground(Object... params) {
                try {
                    return generateCheckSum(checkSumType, (byte[])params[0]);
                } catch (NoSuchAlgorithmException e) {
                    Log.d("CheckSumer-CheckSumActivity", "Couldn't generate checksum for: " + e.toString());
                    return null;
                }
            }

            protected void onPostExecute(String result) {
                ((TextView)activity.findViewById(updatedViewResourceId)).setText(result);
            };
        };
    }

    private static String generateCheckSum(CheckSumType checkSumType, byte[] sourceFile)
            throws NoSuchAlgorithmException {
        MessageDigest mDigest = MessageDigest.getInstance(checkSumType.digestCode);
        byte[] result = mDigest.digest(sourceFile);
        StringBuffer hexResult = new StringBuffer();
        for (int i = 0; i < result.length; i++) {
            hexResult.append(Integer.toHexString(0xFF & result[i]));
        }
        return hexResult.toString();
    }

    protected enum CheckSumType {
        SHA1("SHA-1"), SHA256("SHA-256"), MD5("MD5");
        private CheckSumType(String digestCode)
        {
            this.digestCode = digestCode;
        }
        protected String digestCode;
    }
}
