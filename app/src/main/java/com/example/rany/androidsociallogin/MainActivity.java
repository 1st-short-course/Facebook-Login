package com.example.rany.androidsociallogin;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.share.model.ShareHashtag;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "ooooo";
    private CallbackManager callbackManager;
    private static final String EMAIL = "email";
    private LoginButton loginButton;
    private AccessToken accessToken;
    private Button shareImage, shareLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        loginButton.setReadPermissions(Arrays.asList(EMAIL));

        accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();
        Log.e(TAG, "Status: "+ isLoggedIn );

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.e(TAG, "onSuccess: "+ loginResult.getAccessToken());
                getUserInfo(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.e(TAG, "onCancel: ");
            }

            @Override
            public void onError(FacebookException error) {
                Log.e(TAG, "onError: "+ error.getMessage());
            }
        });

        shareImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareDialog shareDialog = new ShareDialog(MainActivity.this);
                if(shareDialog.canShow(SharePhotoContent.class)){

                    Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
                            R.drawable.sunset);
                    SharePhoto photo = new SharePhoto.Builder()
                            .setBitmap(bitmap)
                            .build();
                    SharePhotoContent sharePhotoContent = new SharePhotoContent.Builder()
                            .addPhoto(photo)
                            .build();
                    shareDialog.show(sharePhotoContent);
                }
            }
        });

        shareLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareDialog shareDialog = new ShareDialog(MainActivity.this);
                if(shareDialog.canShow(ShareLinkContent.class)){
                    ShareLinkContent shareLinkContent = new ShareLinkContent.Builder()
                            .setQuote("Hello, it's me...")
                            .setShareHashtag(new ShareHashtag.Builder()
                            .setHashtag("#"+ "Welcome to Facebook sharing").build())
                            .setContentUrl(Uri.parse("www.khmeracademy.org"))
                            .build();
                    shareDialog.show(shareLinkContent);
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void initView() {
        callbackManager = CallbackManager.Factory.create();
        loginButton = findViewById(R.id.login_button);
        shareImage = findViewById(R.id.btnShareImage);
        shareLink = findViewById(R.id.btnShareLink);
    }

    public void getUserInfo(AccessToken accessToken){
        GraphRequest request = GraphRequest.newMeRequest(
                accessToken,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject object,
                            GraphResponse response) {
                        try {
                            String name = object.getString("name");
                            String email = object.getString("email");
                            String profileUrl = "https://graph.facebook.com/"+
                                    object.getString("id")+"/picture?type=large";
                            Log.e(TAG, "onCompleted: "
                                        +  name +" "
                                        +  email + " "+
                                            profileUrl);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email,picture.type(large)");
        request.setParameters(parameters);
        request.executeAsync();
    }
}
