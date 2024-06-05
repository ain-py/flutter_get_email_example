import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

void main() => runApp(MyApp());

class MyApp extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: SignInHintScreen(),
    );
  }
}

class SignInHintScreen extends StatefulWidget {
  @override
  _SignInHintScreenState createState() => _SignInHintScreenState();
}

class _SignInHintScreenState extends State<SignInHintScreen> {
  static const platform = MethodChannel(
      'com.example.hints/credentials'); // Ensure this matches the native code

  String _hint = 'No hint retrieved';

  Future<void> _getHint() async {
    String hint;
    try {
      final String result = await platform.invokeMethod('getHint');
      print('abcdd');
      hint = 'Hint: $result';
      print(hint);
    } on PlatformException catch (e) {
      hint = "Failed to get hint: '${e.message}'.";
    }

    setState(() {
      _hint = hint;
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('Sign-In Hint'),
      ),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: <Widget>[
            Text(_hint),
            ElevatedButton(
              onPressed: _getHint,
              child: Text('Get Sign-In Hint'),
            ),
          ],
        ),
      ),
    );
  }
}
