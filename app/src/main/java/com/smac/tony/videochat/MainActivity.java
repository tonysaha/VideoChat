package com.smac.tony.videochat;

import android.Manifest;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;

import com.opentok.android.OpentokError;
import com.opentok.android.Publisher;
import com.opentok.android.PublisherKit;
import com.opentok.android.Session;
import com.opentok.android.Stream;
import com.opentok.android.Subscriber;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static android.Manifest.permission.INTERNET;

public class MainActivity extends AppCompatActivity implements Session.SessionListener,PublisherKit.PublisherListener {
    private  static  String API_KEY="46125722";
    private static String SESSION_ID="2_MX40NjEyNTcyMn5-MTUyNzI3Mjk4MDY5OX5BK3U2NjlPODgvVk5qTlNVcVZ2SmFtNmJ-fg";
    private static String TOKEN="T1==cGFydG5lcl9pZD00NjEyNTcyMiZzZGtfdmVyc2lvbj1kZWJ1Z2dlciZzaWc9YzJkMjE0NWNjZDNmNjZhYzBhOWRlNjY0NGJhODBlOWYzNDRiNjc0ZjpzZXNzaW9uX2lkPTJfTVg0ME5qRXlOVGN5TW41LU1UVXlOekkzTWprNE1EWTVPWDVCSzNVMk5qbFBPRGd2Vms1cVRsTlZjVloyU21GdE5tSi1mZyZjcmVhdGVfdGltZT0xNTI3MzE0MDU1JnJvbGU9bW9kZXJhdG9yJm5vbmNlPTE1MjczMTQwNTUuNzQ5ODE5MDI2NjY3MTAmZXhwaXJlX3RpbWU9MTUyOTkwNjA1NQ==" ;
    private static String LOG_TAG=MainActivity.class.getSimpleName();
    private static final int PC_SETTINGS=123;
    private static final int RC_SETTINGS_SCREEN_PERM = 123;
    private static final int RC_VIDEO_APP_PERM = 124;


    private Session mSession;
    private FrameLayout pub_container,sub_container;

    private FrameLayout mPublisherViewContainer;
    private FrameLayout mSubscriberViewContainer;
    private Publisher mPublisher;
    private Subscriber mSubscriber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestPermissions();

        mPublisherViewContainer = (FrameLayout)findViewById(R.id.publisher_container);
        mSubscriberViewContainer = (FrameLayout)findViewById(R.id.subscriber_container);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode,permissions,grantResults);
    }

    @AfterPermissionGranted(RC_VIDEO_APP_PERM)
    private void requestPermissions() {
        String[] perms = { Manifest.permission.INTERNET, Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO };
        if (EasyPermissions.hasPermissions(this, perms)) {
            // initialize view objects from your layout
            mPublisherViewContainer = (FrameLayout) findViewById(R.id.publisher_container);
            mSubscriberViewContainer = (FrameLayout) findViewById(R.id.subscriber_container);

            mSession = new Session.Builder(this, API_KEY, SESSION_ID).build();
            mSession.setSessionListener(this);
            mSession.connect(TOKEN);


        } else {
            EasyPermissions.requestPermissions(this, "This app needs access to your camera and mic to make video calls", RC_VIDEO_APP_PERM, perms);
        }
    }

    @Override
    public void onConnected(Session session) {
        Log.i(LOG_TAG, "Session Connected");
        mPublisher = new Publisher.Builder(this).build();
        mPublisher.setPublisherListener(this);

        mPublisherViewContainer.addView(mPublisher.getView());
        mSession.publish(mPublisher);
    }

    @Override
    public void onDisconnected(Session session) {
        Log.i(LOG_TAG, "Session Disconnected");
    }

    @Override
    public void onStreamReceived(Session session, Stream stream) {
        Log.i(LOG_TAG, "Stream Received");
        if (mSubscriber == null) {
            mSubscriber = new Subscriber.Builder(this, stream).build();
            mSession.subscribe(mSubscriber);
            mSubscriberViewContainer.addView(mSubscriber.getView());
        }
    }

    @Override
    public void onStreamDropped(Session session, Stream stream) {
        Log.i(LOG_TAG, "Stream Dropped");
        if (mSubscriber != null) {
            mSubscriber = null;
            mSubscriberViewContainer.removeAllViews();
        }
    }

    @Override
    public void onError(Session session, OpentokError opentokError) {


    }

    @Override
    public void onStreamCreated(PublisherKit publisherKit, Stream stream) {

    }

    @Override
    public void onStreamDestroyed(PublisherKit publisherKit, Stream stream) {

    }

    @Override
    public void onError(PublisherKit publisherKit, OpentokError opentokError) {

    }
}
