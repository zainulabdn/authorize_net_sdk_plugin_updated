import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'authorize_net_sdk_plugin_method_channel.dart';

abstract class AuthorizeNetSdkPluginPlatform extends PlatformInterface {
  /// Constructs a AuthorizeNetSdkPluginPlatform.
  AuthorizeNetSdkPluginPlatform() : super(token: _token);

  static final Object _token = Object();

  static AuthorizeNetSdkPluginPlatform _instance = MethodChannelAuthorizeNetSdkPlugin();

  /// The default instance of [AuthorizeNetSdkPluginPlatform] to use.
  ///
  /// Defaults to [MethodChannelAuthorizeNetSdkPlugin].
  static AuthorizeNetSdkPluginPlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [AuthorizeNetSdkPluginPlatform] when
  /// they register themselves.
  static set instance(AuthorizeNetSdkPluginPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<String?> getPlatformVersion() {
    throw UnimplementedError('platformVersion() has not been implemented.');
  }
}
