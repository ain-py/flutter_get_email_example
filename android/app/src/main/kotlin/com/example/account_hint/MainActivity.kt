// package com.example.account_hint

// import android.app.PendingIntent
// import android.content.Intent
// import android.content.IntentSender
// import android.os.Bundle
// import android.util.Log
// import androidx.annotation.NonNull
// import com.google.android.gms.auth.api.credentials.Credential
// import com.google.android.gms.auth.api.credentials.Credentials
// import com.google.android.gms.auth.api.credentials.CredentialsClient
// import com.google.android.gms.auth.api.credentials.HintRequest
// import com.google.android.gms.auth.api.credentials.IdentityProviders
// import io.flutter.embedding.android.FlutterActivity
// import io.flutter.embedding.engine.FlutterEngine
// import io.flutter.plugin.common.MethodChannel
// import java.util.concurrent.CompletableFuture

// class MainActivity : FlutterActivity() {
//     private val CHANNEL = "com.example.hints/credentials"
//     private lateinit var credentialsClient: CredentialsClient
//     private var hintFuture: CompletableFuture<String>? = null

//     override fun onCreate(savedInstanceState: Bundle?) {
//         super.onCreate(savedInstanceState)
//         credentialsClient = Credentials.getClient(this)
//     }

//     override fun configureFlutterEngine(@NonNull flutterEngine: FlutterEngine) {
//         super.configureFlutterEngine(flutterEngine)
//         MethodChannel(flutterEngine.dartExecutor.binaryMessenger, CHANNEL).setMethodCallHandler { call, result ->
//             if (call.method == "getHint") {
//                 getHint(result)
//             } else {
//                 result.notImplemented()
//             }
//         }
//     }

//     private fun getHint(result: MethodChannel.Result) {
//         val hintRequest = HintRequest.Builder()
//             .setHintPickerConfig(com.google.android.gms.auth.api.credentials.CredentialPickerConfig.Builder()
//                 .setShowCancelButton(true)
//                 .build())
//             .setEmailAddressIdentifierSupported(true)
//             .setAccountTypes(IdentityProviders.GOOGLE)
//             .build()
//         val intent: PendingIntent = credentialsClient.getHintPickerIntent(hintRequest)

//         hintFuture = CompletableFuture()  // Create a new CompletableFuture

//         try {
//             startIntentSenderForResult(intent.intentSender, RC_HINT, null, 0, 0, 0, null)
//             hintFuture!!.thenAccept { hint ->
//                 result.success(hint)  // Return the result to Flutter
//             }.exceptionally { throwable ->
//                 result.error("UNAVAILABLE", "Could not start hint picker intent", throwable)
//                 null
//             }
//         } catch (e: IntentSender.SendIntentException) {
//             result.error("UNAVAILABLE", "Could not start hint picker intent", null)
//         }
//     }

//     override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//         super.onActivityResult(requestCode, resultCode, data)
//         if (requestCode == RC_HINT) {
//             if (resultCode == RESULT_OK) {
//                 val credential: Credential? = data?.getParcelableExtra(Credential.EXTRA_KEY)
//                 credential?.let {
//                     hintFuture?.complete(it.id)  // Complete the CompletableFuture with the result
//                 }
//             } else {
//                 Log.e("MainActivity", "Hint Read: NOT OK")
//                 hintFuture?.completeExceptionally(RuntimeException("Hint read failed"))
//             }
//         }
//     }

//     companion object {
//         private const val RC_HINT = 1000
//     }
// }
