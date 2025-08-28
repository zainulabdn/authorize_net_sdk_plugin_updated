import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'authorize_net_sdk_plugin_platform_interface.dart';

/// An implementation of [AuthorizeNetSdkPluginPlatform] that uses method channels.
class MethodChannelAuthorizeNetSdkPlugin extends AuthorizeNetSdkPluginPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('authorize_net_sdk_plugin');

  @override
  Future<String?> getPlatformVersion() async {
    final version = await methodChannel.invokeMethod<String>('getPlatformVersion');
    return version;
  }
}
