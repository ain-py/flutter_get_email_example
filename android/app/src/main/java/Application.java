package com.example.account_hint;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.util.Log;
import androidx.annotation.NonNull;
import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.credentials.Credentials;
import com.google.android.gms.auth.api.credentials.CredentialsClient;
import com.google.android.gms.auth.api.credentials.HintRequest;
import com.google.android.gms.auth.api.credentials.IdentityProviders;
import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.plugin.common.MethodChannel;

import java.util.concurrent.CompletableFuture;

public class Application extends FlutterActivity {
    private static final String CHANNEL = "com.example.hints/credentials";
    private CredentialsClient credentialsClient;
    private CompletableFuture<String> hintFuture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        credentialsClient = Credentials.getClient(this);
    }

    @Override
    public void configureFlutterEngine(@NonNull FlutterEngine flutterEngine) {
        super.configureFlutterEngine(flutterEngine);
        new MethodChannel(flutterEngine.getDartExecutor().getBinaryMessenger(), CHANNEL).setMethodCallHandler(
            (call, result) -> {
                if (call.method.equals("getHint")) {
                    getHint(result);
                } else {
                    result.notImplemented();
                }
            }
        );
    }

    private void getHint(MethodChannel.Result result) {
        HintRequest hintRequest = new HintRequest.Builder()
            .setHintPickerConfig(new com.google.android.gms.auth.api.credentials.CredentialPickerConfig.Builder()
                .setShowCancelButton(true)
                .build())
            .setEmailAddressIdentifierSupported(true)
            .setAccountTypes(IdentityProviders.GOOGLE)
            .build();

        PendingIntent intent = credentialsClient.getHintPickerIntent(hintRequest);
        hintFuture = new CompletableFuture<>();  // Create a new CompletableFuture

        try {
            startIntentSenderForResult(intent.getIntentSender(), RC_HINT, null, 0, 0, 0, null);
            hintFuture.thenAccept(hint -> result.success(hint))  // Return the result to Flutter
                       .exceptionally(throwable -> {
                           result.error("UNAVAILABLE", "Could not start hint picker intent", throwable);
                           return null;
                       });
        } catch (IntentSender.SendIntentException e) {
            result.error("UNAVAILABLE", "Could not start hint picker intent", null);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_HINT) {
            if (resultCode == RESULT_OK) {
                Credential credential = data.getParcelableExtra(Credential.EXTRA_KEY);
                if (credential != null) {
                    hintFuture.complete(credential.getId());  // Complete the CompletableFuture with the result
                }
            } else {
                Log.e("MainActivity", "Hint Read: NOT OK");
                hintFuture.completeExceptionally(new RuntimeException("Hint read failed"));
            }
        }
    }

    private static final int RC_HINT = 1000;
}
