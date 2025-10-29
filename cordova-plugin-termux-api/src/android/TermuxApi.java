package com.exemplo.smsapp;

import android.telephony.SmsManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.content.Context;
import android.Manifest;
import android.os.Build;

import org.apache.cordova.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

public class TermuxApi extends CordovaPlugin {

  @Override
  public boolean execute(String action, JSONArray args, CallbackContext callbackContext) {
    try {
      if (action.equals("smsSend")) {
        JSONObject opts = args.getJSONObject(0);
        String number = opts.getString("number");
        String text = opts.getString("text");
        int sim = opts.optInt("sim", 0);
        sendSMS(number, text, sim, callbackContext);
        return true;
      } else if (action.equals("telephonyInfo")) {
        getTelephonyInfo(callbackContext);
        return true;
      }
    } catch (Exception e) {
      callbackContext.error(e.getMessage());
    }
    return false;
  }

  private void sendSMS(String number, String text, int simIndex, CallbackContext callbackContext) {
    Context ctx = this.cordova.getActivity().getApplicationContext();

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
      SubscriptionManager sm = SubscriptionManager.from(ctx);
      List<SubscriptionInfo> list = sm.getActiveSubscriptionInfoList();
      if (list != null && list.size() > simIndex) {
        int subId = list.get(simIndex).getSubscriptionId();
        SmsManager sms = SmsManager.getSmsManagerForSubscriptionId(subId);
        sms.sendTextMessage(number, null, text, null, null);
        callbackContext.success("SMS enviado via SIM " + (simIndex + 1));
        return;
      }
    }

    // fallback
    SmsManager sms = SmsManager.getDefault();
    sms.sendTextMessage(number, null, text, null, null);
    callbackContext.success("SMS enviado via SIM padrão");
  }

  private void getTelephonyInfo(CallbackContext callbackContext) {
    Context ctx = this.cordova.getActivity().getApplicationContext();
    try {
      JSONObject result = new JSONObject();
      JSONArray sims = new JSONArray();

      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
        SubscriptionManager sm = SubscriptionManager.from(ctx);
        List<SubscriptionInfo> list = sm.getActiveSubscriptionInfoList();
        if (list != null) {
          for (SubscriptionInfo info : list) {
            JSONObject s = new JSONObject();
            s.put("carrier_name", info.getCarrierName());
            s.put("number", info.getNumber());
            s.put("operator_name", info.getDisplayName());
            sims.put(s);
          }
        }
      }
      result.put("sim_info", sims);
      callbackContext.success(result);
    } catch (Exception e) {
      callbackContext.error(e.getMessage());
    }
  }
}
