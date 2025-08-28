package com.zain.authorize_net_sdk_plugin

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import net.authorize.acceptsdk.AcceptSDKApiClient
import net.authorize.acceptsdk.datamodel.common.Message
import net.authorize.acceptsdk.datamodel.merchant.ClientKeyBasedMerchantAuthentication
import net.authorize.acceptsdk.datamodel.transaction.CardData
import net.authorize.acceptsdk.datamodel.transaction.EncryptTransactionObject
import net.authorize.acceptsdk.datamodel.transaction.TransactionObject
import net.authorize.acceptsdk.datamodel.transaction.TransactionType
import net.authorize.acceptsdk.datamodel.transaction.callbacks.EncryptTransactionCallback
import net.authorize.acceptsdk.datamodel.transaction.response.EncryptTransactionResponse
import net.authorize.acceptsdk.datamodel.transaction.response.ErrorTransactionResponse

class AuthorizeNetSdkPlugin  : FlutterPlugin, ActivityAware, MethodChannel.MethodCallHandler,
    EncryptTransactionCallback {

    private var mContext: Context? = null
    private var mApplication: Application? = null
    private var mIntent: Intent? = null
    private var mMethodChannel: MethodChannel? = null
    private var activity: Activity? = null
    private var channelResult: MethodChannel.Result? = null

    private fun onAttachedToEngine(applicationContext: Context, messenger: BinaryMessenger) {
        mContext = applicationContext
        mMethodChannel = MethodChannel(messenger, "authorize_net_plugin")
        mMethodChannel?.setMethodCallHandler(this)
    }

    override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
        if (call.method == "authorizeNetToken") {
            val env = call.argument<String>("env") ?: ""
            val cardNumber = call.argument<String>("card_number") ?: ""
            val expirationMonth = call.argument<String>("expiration_month") ?: ""
            val expirationYear = call.argument<String>("expiration_year") ?: ""
            val cardCvv = call.argument<String>("card_cvv") ?: ""
            val zipCode = call.argument<String>("zip_code") ?: ""
            val cardHolderName = call.argument<String>("card_holder_name") ?: ""
            val apiLoginId = call.argument<String>("api_login_id") ?: ""
            val clientId = call.argument<String>("client_id") ?: ""

            setupAuthorizeNet(
                env, cardNumber, expirationMonth, expirationYear,
                cardCvv, zipCode, cardHolderName,
                apiLoginId, clientId
            )
            channelResult = result
        } else {
            result.notImplemented()
        }
    }

    fun setupAuthorizeNet(
        env: String,
        cardNumber: String,
        expirationMonth: String,
        expirationYear: String,
        cardCvv: String,
        zipCode: String,
        cardHolderName: String,
        apiLoginId: String,
        clientId: String
    ) {
        val apiClient = if (env == "production") {
            AcceptSDKApiClient.Builder(activity, AcceptSDKApiClient.Environment.PRODUCTION)
                .connectionTimeout(5000)
                .build()
        } else {
            AcceptSDKApiClient.Builder(activity, AcceptSDKApiClient.Environment.SANDBOX)
                .connectionTimeout(5000)
                .build()
        }

        val cardData = CardData.Builder(cardNumber, expirationMonth, expirationYear)
            .cvvCode(cardCvv)
            .zipCode(zipCode)
            .cardHolderName(cardHolderName)
            .build()

        val merchantAuthentication =
            ClientKeyBasedMerchantAuthentication.createMerchantAuthentication(apiLoginId, clientId)

        val transactionObject = TransactionObject.createTransactionObject(
            TransactionType.SDK_TRANSACTION_ENCRYPTION
        )
            .cardData(cardData)
            .merchantAuthentication(merchantAuthentication)
            .build() as EncryptTransactionObject

        apiClient.getTokenWithRequest(transactionObject, this)
    }

    override fun onErrorReceived(errorResponse: ErrorTransactionResponse) {
        val error: Message = errorResponse.getFirstErrorMessage()
        channelResult?.error("-1", error.getMessageText(), null)
    }

    override fun onEncryptionFinished(response: EncryptTransactionResponse) {
        channelResult?.success(response.getDataValue())
    }

    override fun onAttachedToEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        onAttachedToEngine(binding.applicationContext, binding.binaryMessenger)
    }

    override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        mMethodChannel?.setMethodCallHandler(null)
        mMethodChannel = null
    }

    override fun onAttachedToActivity(binding: ActivityPluginBinding) {
        activity = binding.activity
        mIntent = binding.activity.intent
        mApplication = binding.activity.application
    }

    override fun onDetachedFromActivity() {
        activity = null
    }

    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
        activity = binding.activity
    }

    override fun onDetachedFromActivityForConfigChanges() {
        activity = null
    }
}
