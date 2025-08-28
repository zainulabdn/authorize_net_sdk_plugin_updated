import 'package:flutter_test/flutter_test.dart';
import 'package:authorize_net_sdk_plugin/authorize_net_sdk_plugin.dart';
import 'package:authorize_net_sdk_plugin/authorize_net_sdk_plugin_platform_interface.dart';
import 'package:authorize_net_sdk_plugin/authorize_net_sdk_plugin_method_channel.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

class MockAuthorizeNetSdkPluginPlatform
    with MockPlatformInterfaceMixin
    implements AuthorizeNetSdkPluginPlatform {

  @override
  Future<String?> getPlatformVersion() => Future.value('42');
}

void main() {
  final AuthorizeNetSdkPluginPlatform initialPlatform = AuthorizeNetSdkPluginPlatform.instance;

  test('$MethodChannelAuthorizeNetSdkPlugin is the default instance', () {
    expect(initialPlatform, isInstanceOf<MethodChannelAuthorizeNetSdkPlugin>());
  });

  test('getPlatformVersion', () async {
    AuthorizeNetSdkPlugin authorizeNetSdkPlugin = AuthorizeNetSdkPlugin();
    MockAuthorizeNetSdkPluginPlatform fakePlatform = MockAuthorizeNetSdkPluginPlatform();
    AuthorizeNetSdkPluginPlatform.instance = fakePlatform;

    expect(await authorizeNetSdkPlugin.getPlatformVersion(), '42');
  });
}
